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

package de.gematik.demis.pdfgen.fhir.extract;

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createBedOccupancyBundle;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.Test;

class QuestionnaireFhirQueriesTest {

  private final Bundle bedOccupancyBundle = createBedOccupancyBundle();
  private final QuestionnaireFhirQueries questionnaireFhirQueries =
      new QuestionnaireFhirQueries(new FhirQueries());

  @Test
  void getFirstQuestionnaireResponse_shouldReturnNullResponseIfBundleIsNull() {
    assertThat(questionnaireFhirQueries.getBedOccupancyQuestionnaireResponse(null)).isEmpty();
  }

  @Test
  void getFirstQuestionnaireResponse_shouldExtractFirstResponseFromBedOccupancyBundle() {
    // when
    var response =
        questionnaireFhirQueries.getBedOccupancyQuestionnaireResponse(bedOccupancyBundle);

    // then
    assertResponseIsValid(response);
  }

  @Test
  void getQuestionnaireAnswers_shouldReturnEmptyListOfAnswersIfResponseIsNull() {
    // when
    var answers = questionnaireFhirQueries.getQuestionnaireAnswers(Optional.empty());

    // then
    assertThat(answers).isEmpty();
  }

  @Test
  void getFirstQuestionnaireResponse_shouldExtractAnswersFromResponse() {
    // given
    var response =
        questionnaireFhirQueries.getBedOccupancyQuestionnaireResponse(bedOccupancyBundle);

    // when
    var answers = questionnaireFhirQueries.getQuestionnaireAnswers(response);

    // then
    assertThat(answers).hasSize(4);
    assertThat(answers.get("numberOperableBedsGeneralWardAdults")).contains("250");
    assertThat(answers.get("numberOccupiedBedsGeneralWardAdults")).contains("221");
    assertThat(answers.get("numberOperableBedsGeneralWardChildren")).contains("50");
    assertThat(answers.get("numberOccupiedBedsGeneralWardChildren")).contains("37");
  }

  private void assertResponseIsValid(Optional<QuestionnaireResponse> responseOptional) {
    assertThat(responseOptional).isPresent();
    var response = responseOptional.get();
    assertThat(response.getItem()).hasSize(4);
    assertThat(response.getId())
        .contains(
            "https://demis.rki.de/fhir/QuestionnaireResponse/5e1e89ce-7a44-4ec1-801c-0f988992e8fa");
  }
}
