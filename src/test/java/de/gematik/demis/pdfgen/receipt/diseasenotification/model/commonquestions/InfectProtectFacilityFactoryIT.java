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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.commonquestions;

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createDiseaseQuestionsCommonQuestionnaireResponse;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InfectProtectFacilityFactoryIT {

  @Autowired private InfectProtectFacilityFactory facilityFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(facilityFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    InfectProtectFacility infectProtectFacility =
        facilityFactory.create(createDiseaseQuestionsCommonQuestionnaireResponse());

    // then
    assertThat(infectProtectFacility).isNotNull();
    assertThat(infectProtectFacility.getBegin()).hasToString("01.12.2021");
    assertThat(infectProtectFacility.getEnd()).hasToString("05.01.2022");
    assertThat(infectProtectFacility.getRole()).isEqualTo("Tätigkeit");
    assertThat(infectProtectFacility.getOrganizationDTO()).isNotNull();
    assertThat(infectProtectFacility.getOrganizationDTO().getName()).isEqualTo("Einrichtungsname");
  }
}
