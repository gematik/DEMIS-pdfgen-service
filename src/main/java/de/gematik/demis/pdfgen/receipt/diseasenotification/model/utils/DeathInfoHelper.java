package de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils;

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

import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class DeathInfoHelper {

  private static final String DEATH_COMPONENT_NAME = "isdead";

  private final QuestionnaireResponseHelper questionnaireResponseHelper;

  private static Optional<DateType> getDateTypeOfFirstAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemComponent itemComponent) {
    return itemComponent.getAnswer().stream()
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(DateType.class::isInstance)
        .map(DateType.class::cast)
        .findFirst();
  }

  @Nullable
  public String isDead(final QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponseHelper.getCodingDisplayAnswerValue(
        questionnaireResponse, DEATH_COMPONENT_NAME);
  }

  @Nullable
  public DateTimeHolder getDeathDate(final QuestionnaireResponse questionnaireResponse) {
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> component =
        questionnaireResponseHelper.getComponent(questionnaireResponse, DEATH_COMPONENT_NAME);
    if (component.isEmpty()) {
      return null;
    }
    List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answers =
        component.get().getAnswer();
    if (CollectionUtils.isEmpty(answers)) {
      return null;
    }
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> answerItems =
        answers.get(0).getItem();
    if (CollectionUtils.isEmpty(answerItems)) {
      return null;
    }
    Optional<DateType> dateTypeOptional = getDateTypeOfFirstAnswer(answerItems.get(0));
    return dateTypeOptional.map(DateTimeHolder::new).orElse(null);
  }
}
