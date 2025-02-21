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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.NotifiedPersonDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.NotificationBundleLaboratoryDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.OrganizationBuilder;
import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.PractitionerRoleBuilder;
import de.gematik.demis.pdfgen.receipt.common.model.enums.AddressUseEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import java.util.Collections;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NotifiedPersonFactoryAddressHandlingTest {

  @Autowired private NotifiedPersonFactory notifiedPersonFactory;

  @Test
  void thatReferencedAddressesAreAddedDirectly() {
    // GIVEN a bundle

    // AND an organization with an address
    final Address whereabouts =
        new AddressDataBuilder()
            .setCity("Berlin")
            .setStreet("Example Street")
            .setHouseNumber("123")
            .setPostalCode("10115")
            .build();

    // AND the address doesn't contain the organization's name
    final Organization organization =
        new OrganizationBuilder()
            .addNotifiedPersonFacilityProfile()
            .setFacilityName("Company Name")
            .setAddress(whereabouts)
            .build();
    // AND a notified person that references the organization as address
    final Address referencingOrganization =
        new AddressDataBuilder()
            .withOrganizationReferenceExtension(organization)
            .withAddressUseExtension(AddressDataBuilder.CURRENT, "Aktueller Aufenthaltsort")
            .build();
    // AND the notified person has it's own address
    final Address primaryResidence =
        new AddressDataBuilder()
            .setStreet("Test Street")
            .withAddressUseExtension(AddressDataBuilder.PRIMARY, "Hauptwohnsitz")
            .build();

    final Patient notifiedPerson =
        new NotifiedPersonDataBuilder()
            .setDefaults()
            .addAddress(referencingOrganization)
            .addAddress(primaryResidence)
            .build();

    final PractitionerRole submittingRole =
        new PractitionerRoleBuilder()
            .asSubmittingRole()
            .setDefaults()
            .withOrganization(organization)
            .build();
    final Bundle diseaseNotificationBundle =
        new NotificationBundleLaboratoryDataBuilder()
            .setDefaults()
            .setNotificationLaboratory(
                new Composition().setSubject(new Reference((notifiedPerson))))
            .setLaboratoryReport(new DiagnosticReport())
            .setNotifiedPerson(notifiedPerson)
            .setSubmitterRole(submittingRole)
            .setSpecimen(Collections.emptyList())
            .build();
    // WHEN the NotifiedPersonDTO is built
    final NotifiedPersonDTO notifiedPersonDTO =
        notifiedPersonFactory.create(diseaseNotificationBundle);

    // THEN the address referencing the organization is found
    assertThat(notifiedPersonDTO.getOrganizationDTOs()).hasSize(1);
    AddressDTO actual = notifiedPersonDTO.getOrganizationDTOs().getFirst().getAddressDTO();
    assertThat(actual.getUseEnum()).isEqualTo(AddressUseEnum.CURRENT);
    assertThat(actual.getOrganizationAddressAsSingleLine())
        .isEqualTo("Example Street 123, 10115 Berlin");

    // AND the regular address is present as well
    assertThat(notifiedPersonDTO.getAddressDTOs()).hasSize(1);
    actual = notifiedPersonDTO.getAddressDTOs().getFirst();
    assertThat(actual.getUseEnum()).isEqualTo(AddressUseEnum.PRIMARY);
    assertThat(actual.isPrimaryAddress()).isTrue();
    assertThat(actual.getOrganizationAddressAsSingleLine()).isEqualTo("Test Street");
  }
}
