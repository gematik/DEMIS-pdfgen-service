package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory;

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

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.TimeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnswerValuesTest {

  @InjectMocks AnswerValues answerValues;

  @Test
  @DisplayName("valueDate supports precision: day")
  void valueDateShouldSupportPrecisionDay() {
    DateType day = new DateType("2024-07-05");
    var answer = new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    answer.setValue(day);
    String text = this.answerValues.apply(answer);
    Assertions.assertThat(text).as("valueDate with day precision").isEqualTo("05.07.2024");
  }

  @Test
  @DisplayName("valueDate supports precision: month")
  void valueDateShouldSupportPrecisionMonth() {
    DateType month = new DateType("2024-07");
    var answer = new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    answer.setValue(month);
    String text = this.answerValues.apply(answer);
    Assertions.assertThat(text).as("valueDate with month precision").isEqualTo("07.2024");
  }

  @Test
  @DisplayName("valueDate supports precision: year")
  void valueDateShouldSupportPrecisionYear() {
    DateType year = new DateType("2024");
    var answer = new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    answer.setValue(year);
    String text = this.answerValues.apply(answer);
    Assertions.assertThat(text).as("valueDate with year precision").isEqualTo("2024");
  }

  @Test
  @DisplayName("valueTime supports precision: second")
  void valueTimeShouldSupportPrecisionSecond() {
    TimeType second = new TimeType("12:53:11");
    var answer = new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    answer.setValue(second);
    String text = this.answerValues.apply(answer);
    Assertions.assertThat(text).as("valueTime with second precision").isEqualTo("12:53:11");
  }

  @Test
  @DisplayName("valueTime supports precision: minute")
  void valueTimeShouldSupportPrecisionMinute() {
    TimeType second = new TimeType("12:53");
    var answer = new QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent();
    answer.setValue(second);
    String text = this.answerValues.apply(answer);
    Assertions.assertThat(text).as("valueTime with minute precision").isEqualTo("12:53");
  }
}
