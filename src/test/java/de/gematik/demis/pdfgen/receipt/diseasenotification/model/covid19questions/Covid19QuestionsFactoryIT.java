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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.covid19questions;

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createDiseaseNotificationBundle;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Covid19QuestionsFactoryIT {

  @Autowired private Covid19QuestionsExtractionSrv questionsFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(questionsFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    Covid19Questions questions = questionsFactory.create(createDiseaseNotificationBundle());

    // then
    assertThat(questions).isNotNull();
    assertThat(questions.hasInfectionSource()).isTrue();
    assertThat(questions.getInfectionSource()).isEqualTo("Ja");
    assertThat(questions.getHasInfectionEnvironmentSetting()).isEqualTo("Ja");
    assertThat(questions.getInfectionEnvironmentSettings()).hasSize(1);
    assertThat(questions.getHasImmunization()).isEqualTo("Ja");
    assertThat(questions.getImmunizations()).hasSize(4);
  }
}
