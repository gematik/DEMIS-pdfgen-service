package de.gematik.demis.pdfgen.receipt.common.model.section;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 gematik GmbH
 * %%
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the
 * European Commission – subsequent versions of the EUPL (the "Licence").
 * You may not use this work except in compliance with the Licence.
 *
 * You find a copy of the Licence in the "Licence" file or at
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.
 * In case of changes by gematik find details in the "Readme" file.
 *
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */

import static de.gematik.demis.pdfgen.lib.profile.DemisExtensions.EXTENSION_URL_FACILTY_ADDRESS_NOTIFIED_PERSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.pdfgen.fhir.extract.NotifiedFhirQueries;
import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotifiedPersonFactoryTest {

  private NotifiedPersonFactory notifiedPersonFactory;

  @Mock private NotifiedFhirQueries notifiedFhirQueries;
  @Mock private ContactFactory contactFactory;
  @Mock private TelecomFactory telecomFactory;
  @Mock private AddressFactory addressFactory;
  @Mock private OrganizationFactory organizationFactory;

  @BeforeEach()
  void setUp() {
    notifiedPersonFactory =
        new NotifiedPersonFactory(
            notifiedFhirQueries,
            contactFactory,
            telecomFactory,
            addressFactory,
            organizationFactory);
  }

  @AfterEach
  void cleanUp() {
    reset(notifiedFhirQueries, contactFactory, telecomFactory, addressFactory, organizationFactory);
  }

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(notifiedPersonFactory.create(null)).isNull();
  }

  @Test
  void shouldReturnNullForMissingPatient() {

    Bundle laboratoryReportBundle = createLaboratoryReportBundle();

    when(notifiedFhirQueries.getNotifiedPerson(laboratoryReportBundle))
        .thenReturn(Optional.empty());

    assertThat(notifiedPersonFactory.create(laboratoryReportBundle)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {

    //    notifiedPersonFactory =
    //        new NotifiedPersonFactory(
    //            notifiedFhirQueries,
    //            contactFactory,
    //            telecomFactory,
    //            addressFactory,
    //            organizationFactory);

    // given
    Bundle laboratoryReportBundle = new Bundle();

    Patient notifiedPersonPatient =
        new NotifiedPersonDataBuilder()
            .setBirthdate(
                Date.from(
                    LocalDate.of(1950, 2, 11).atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .setGender(Enumerations.AdministrativeGender.FEMALE)
            .buildExampleNotifiedPerson();

    when(notifiedFhirQueries.getNotifiedPerson(laboratoryReportBundle))
        .thenReturn(Optional.of(notifiedPersonPatient));

    NameDTO name = NameDTO.builder().build();

    when(contactFactory.create(notifiedPersonPatient.getNameFirstRep())).thenReturn(name);

    AddressDTO addressDTO = AddressDTO.builder().build();
    when(addressFactory.create(notifiedPersonPatient.getAddress().iterator().next()))
        .thenReturn(addressDTO);

    Telecom telecomDTO = Telecom.builder().build();
    when(telecomFactory.createTelecoms(notifiedPersonPatient.getTelecom()))
        .thenReturn(Collections.singletonList(telecomDTO));

    // then
    NotifiedPersonDTO actualNotifiedPersonDTO =
        notifiedPersonFactory.create(laboratoryReportBundle);
    assertThat(actualNotifiedPersonDTO).isNotNull();
    assertThat(actualNotifiedPersonDTO.getNameDTO()).isEqualTo(name);
    assertThat(actualNotifiedPersonDTO.getGender()).isEqualTo(GenderEnum.FEMALE);
    assertThat(actualNotifiedPersonDTO.getBirthdate()).hasToString("11.02.1950");
    assertThat(actualNotifiedPersonDTO.getAddressDTOs()).containsExactly(addressDTO);
    assertThat(actualNotifiedPersonDTO.getTelecoms()).containsExactly(telecomDTO);
    assertThat(actualNotifiedPersonDTO.getOrganizationDTOs()).isEmpty();
  }

  @Test
  void create_shouldCreateNotifiedPersonWithOrganizationAddressUsingAddressUseFromExtension() {

    //    notifiedPersonFactory =
    //        new NotifiedPersonFactory(
    //            notifiedFhirQueries,
    //            contactFactory,
    //            telecomFactory,
    //            addressFactory,
    //            organizationFactory);

    Bundle diseaseNotificationBundle = new Bundle();

    // add organization
    Extension organizationExtension = new Extension();
    organizationExtension.setUrl(EXTENSION_URL_FACILTY_ADDRESS_NOTIFIED_PERSON);
    Organization notifiedPersonOrg = new Organization();
    Reference referenceToOrganisation = new Reference(notifiedPersonOrg);
    organizationExtension.setValue(referenceToOrganisation);
    Address addressForNPP = new AddressDataBuilder().buildExampleAddress();
    addressForNPP.addExtension(organizationExtension);

    // add DEMIS address use
    DemisAddressUseServiceTest.addUseExtension(
        addressForNPP, DemisAddressUseServiceTest.PRIMARY_CODE);

    Patient notifiedPersonPatient =
        new NotifiedPersonDataBuilder()
            .setBirthdate(
                Date.from(
                    LocalDate.of(1950, 2, 11).atStartOfDay(ZoneId.systemDefault()).toInstant()))
            .setGender(Enumerations.AdministrativeGender.FEMALE)
            .addAddress(addressForNPP)
            .build();

    when(notifiedFhirQueries.getNotifiedPerson(diseaseNotificationBundle))
        .thenReturn(Optional.of(notifiedPersonPatient));

    AddressDTO organizationAddress = AddressDTO.builder().city("Berlin").build();
    OrganizationDTO organizationDTO =
        OrganizationDTO.builder().addressDTO(organizationAddress).build();
    when(organizationFactory.create(any(Extension.class))).thenReturn(organizationDTO);

    String addressUse = "Hauptwohnsitz";
    AddressDTO notifiedPersonAddress =
        AddressDTO.builder().city("Berlin").use(addressUse).primaryAddress(true).build();
    when(this.addressFactory.create(addressForNPP)).thenReturn(notifiedPersonAddress);

    // then
    assertThat(organizationDTO.getAddressDTO().getUse())
        .as("organization address contains to use information")
        .isNull();
    NotifiedPersonDTO notifiedPersonDTO = notifiedPersonFactory.create(diseaseNotificationBundle);
    assertThat(notifiedPersonDTO).isNotNull();
    assertThat(notifiedPersonDTO.getAddressDTOs()).isEmpty();
    assertThat(notifiedPersonDTO.getOrganizationDTOs()).containsExactly(organizationDTO);
    assertThat(organizationDTO.getAddressDTO()).isSameAs(organizationAddress);
    assertThat(organizationAddress.getUse())
        .as(
            "address factory updated use of organization address with value from notified person address use")
        .isEqualTo(addressUse);
  }
}
