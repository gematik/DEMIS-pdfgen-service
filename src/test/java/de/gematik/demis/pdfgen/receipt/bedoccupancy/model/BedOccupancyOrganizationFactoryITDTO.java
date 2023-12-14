/*
 * Copyright [2023], gematik GmbH
 *
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
 */

package de.gematik.demis.pdfgen.receipt.bedoccupancy.model;

import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.PHONE;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createFhirOrganization;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BedOccupancyOrganizationFactoryIT {

  @Autowired private BedOccupancyOrganizationFactory bedOccupancyOrganizationFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(bedOccupancyOrganizationFactory.create(Optional.empty())).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    BedOccupancyOrganization organization =
        bedOccupancyOrganizationFactory.create(Optional.of(createFhirOrganization()));

    // then
    assertThat(organization).isNotNull();
    assertThat(organization.getId()).isEqualTo("hl7");
    assertThat(organization.getName()).isEqualTo("Health Level Seven International");
    assertThat(organization.getAddress()).isNotNull();
    assertThat(organization.getAddress().getLine()).isEqualTo("3300 Washtenaw Avenue, Suite 227");
    assertThat(organization.getAddress().getCity()).isEqualTo("Ann Arbor");
    assertThat(organization.getAddress().getPostalCode()).isEqualTo("48104");
    assertThat(organization.getTelecoms()).isNotNull();
    assertThat(organization.getTelecoms()).hasSize(3);
    assertThat(organization.getTelecoms().get(0)).isNotNull();
    assertThat(organization.getTelecoms().get(0).getSystem()).isEqualTo(PHONE);
    assertThat(organization.getTelecoms().get(0).getValue()).isEqualTo("(+1) 734-677-7777");
  }
}
