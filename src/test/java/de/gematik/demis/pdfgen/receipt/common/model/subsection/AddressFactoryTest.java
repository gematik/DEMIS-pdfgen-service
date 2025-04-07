package de.gematik.demis.pdfgen.receipt.common.model.subsection;

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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.pdfgen.lib.profile.DemisExtensions.EXTENSION_URL_ADDRESS_USE;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createFhirAddress;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createTransmittingSite;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressFactoryTest {

  private AddressFactory addressFactory;

  @Mock private AddressTranslationService addressTranslationService;

  @BeforeEach
  void setup() {
    addressFactory =
        new AddressFactory(
            addressTranslationService, new DemisAddressUseService(addressTranslationService));
  }

  @Test
  void create_shouldHandleNullGracefully() {
    org.hl7.fhir.r4.model.Address fhirAddress = new org.hl7.fhir.r4.model.Address();
    org.hl7.fhir.r4.model.Organization fhirOrganization = new org.hl7.fhir.r4.model.Organization();

    assertThat(addressFactory.create((org.hl7.fhir.r4.model.Address) null)).isNull();
    assertThat(addressFactory.create((TransmittingSite) null)).isNull();
    assertThat(addressFactory.create(null, null)).isNull();
    assertThat(addressFactory.create(fhirAddress, null)).isNotNull();
    assertThat(addressFactory.create(null, fhirOrganization)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    when(addressTranslationService.translateCountryCode("USA"))
        .thenReturn("https://demis.rki.de/fhir/CodeSystem/country: USA");

    AddressDTO addressDTO = addressFactory.create(createFhirAddress());

    // then
    assertThat(addressDTO).isNotNull();
    assertThat(addressDTO.getDepartment()).isNull();
    assertThat(addressDTO.getOrganization()).isNull();
    assertThat(addressDTO.getLine()).isEqualTo("3300 Washtenaw Avenue, Suite 227");
    assertThat(addressDTO.getPostalCode()).isEqualTo("48104");
    assertThat(addressDTO.getCity()).isEqualTo("Ann Arbor");
    assertThat(addressDTO.getCountry())
        .isEqualTo("https://demis.rki.de/fhir/CodeSystem/country: USA");
    assertThat(addressDTO.getUse()).isEmpty();
  }

  @Test
  void create_shouldCreateFromTransmittingSite() {
    // given
    AddressDTO addressDTO = addressFactory.create(createTransmittingSite());

    // then
    assertThat(addressDTO).isNotNull();
    assertThat(addressDTO.getDepartment()).isNull();
    assertThat(addressDTO.getOrganization()).isNull();
    assertThat(addressDTO.getLine()).isEqualTo("Teststraße");
    assertThat(addressDTO.getPostalCode()).isNull();
    assertThat(addressDTO.getCity()).isEqualTo("Teststadt");
    assertThat(addressDTO.getCountry()).isNull();
    assertThat(addressDTO.getUse()).isNull();
  }

  @Test
  void create_shouldResolveDemisAddressUsePrimary() {
    // given
    Extension extension = new Extension();
    Coding value =
        new Coding(
            "https://demis.rki.de/fhir/CodeSystem/addressUse",
            DemisAddressUseServiceTest.PRIMARY_CODE,
            null);
    extension.setValue(value);
    extension.setUrl(EXTENSION_URL_ADDRESS_USE);
    org.hl7.fhir.r4.model.Address address = new Address();
    address.addExtension(extension);
    when(this.addressTranslationService.translateUse(any()))
        .thenReturn(DemisAddressUseServiceTest.PRIMARY_GERMAN);

    // then
    AddressDTO dto = this.addressFactory.create(address);
    assertThat(dto.getUse())
        .as("address uses")
        .isEqualTo(DemisAddressUseServiceTest.PRIMARY_GERMAN);
    assertThat(dto.isPrimaryAddress()).as("is primary address").isTrue();
  }

  @Test
  void create_shouldResolveDemisAddressUseOrdinary() {
    // given
    Extension extension = new Extension();
    Coding value =
        new Coding(
            "https://demis.rki.de/fhir/CodeSystem/addressUse",
            DemisAddressUseServiceTest.CURRENT_CODE,
            null);
    extension.setValue(value);
    extension.setUrl(EXTENSION_URL_ADDRESS_USE);
    org.hl7.fhir.r4.model.Address address = new Address();
    address.addExtension(extension);
    when(this.addressTranslationService.translateUse(any()))
        .thenReturn(DemisAddressUseServiceTest.CURRENT_GERMAN);

    // then
    AddressDTO dto = this.addressFactory.create(address);
    assertThat(dto.getUse())
        .as("address uses")
        .isEqualTo(DemisAddressUseServiceTest.CURRENT_GERMAN);
    assertThat(dto.isPrimaryAddress()).as("current address is not primary address").isFalse();
  }
}
