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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createDiseaseNotificationBundle;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommonQuestionsFactoryIT {

  @Autowired private CommonQuestionsFactory commonQuestionsFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(commonQuestionsFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    CommonQuestions commonQuestions =
        commonQuestionsFactory.create(createDiseaseNotificationBundle());

    // then
    assertThat(commonQuestions).isNotNull();
    assertThat(commonQuestions.getIsDead()).isEqualTo("Ja");
    assertThat(commonQuestions.getDeathDate()).hasToString("22.01.2022");
    assertThat(commonQuestions.getHasMilitaryAffiliation()).isEqualTo("Soldat/BW-Angehöriger");
    assertThat(commonQuestions.getWasLabSpecimenTaken()).isEqualTo("Ja");
    assertThat(commonQuestions.getLaboratory()).isNotNull();
    assertThat(commonQuestions.getLaboratory().getName()).isEqualTo("Labor");
    assertThat(commonQuestions.getWasHospitalized()).isEqualTo("Ja");
    assertThat(commonQuestions.getHospitalizations()).isNotNull();
    assertThat(commonQuestions.getHospitalizations()).hasSize(2);
    assertThat(commonQuestions.getHospitalizations()).hasSize(2);
    assertThat(commonQuestions.getWasInInfectProtectFacility()).isEqualTo("Ja");
    assertThat(commonQuestions.getInfectProtectFacility()).isNotNull();
    assertThat(commonQuestions.getInfectProtectFacility().getRole()).isEqualTo("Tätigkeit");
    assertThat(commonQuestions.getHasExposurePlace()).isEqualTo("Ja");
    assertThat(commonQuestions.getExposurePlaces()).isNotNull();
    assertThat(commonQuestions.getExposurePlaces()).hasSize(1);
    assertThat(commonQuestions.getExposurePlaces().get(0).getRegion()).isEqualTo("Libyen");
    assertThat(commonQuestions.getOrganDonation()).isEqualTo("Ja");
    assertThat(commonQuestions.getNote())
        .isEqualTo(
            "Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben");
  }
}
