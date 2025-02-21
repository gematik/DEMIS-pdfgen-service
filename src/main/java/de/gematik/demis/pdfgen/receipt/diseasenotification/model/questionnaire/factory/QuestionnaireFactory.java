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
 * #L%
 */

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Answer;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Item;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Questionnaire;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources.ResourceFactory;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

/** Creation of PDF questionnaire (response) from FHIR questionnaire response */
@RequiredArgsConstructor
@Service
@Slf4j
public class QuestionnaireFactory {

  private final GroupByLinkId groupByLinkId;
  private final AnswerValues answerValues;
  private final ResourceFactory resourceFactory;
  private final TranslationService translationService;

  /**
   * Create PDF questionnaire (response) from FHIR questionnaire response
   *
   * @param context FHIR questionnaire response context
   * @return PDF questionnaire
   */
  public Questionnaire createQuestionnaire(Context context) {
    String title = context.translation().title();
    List<Item> items = createItems(context);
    String status = retrieveAnswerStatus(context);
    return new Questionnaire(title, items, status);
  }

  private String retrieveAnswerStatus(final Context context) {
    final var status = context.questionnaire().getStatus();
    // with null in Display, the whole URL + Definition is printed out
    final var coding = new Coding(status.getSystem(), status.toCode(), null);
    return translationService.resolveCodeableConceptValues(coding);
  }

  private List<Item> createItems(Context context) {
    var fhirItems = this.groupByLinkId.apply(context.questionnaire().getItem());
    return fhirItems.stream().map(i -> createItem(i, context)).toList();
  }

  private Item createItem(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source, Context context) {
    Optional<Item> resource = this.resourceFactory.createResourceItem(source, context);
    if (resource.isPresent()) {
      return resource.get();
    }
    String text = context.translation().item(source.getLinkId());
    List<Answer> answers = createAnswers(source, context);
    List<Item> subitems = createSubitems(source, context);
    return new Item(text, answers, subitems);
  }

  private List<Item> createSubitems(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source, Context context) {
    if (source.hasItem()) {
      return source.getItem().stream().map(i -> createItem(i, context)).toList();
    }
    return Collections.emptyList();
  }

  private List<Answer> createAnswers(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source, Context context) {
    if (source.hasAnswer()) {
      return source.getAnswer().stream().map(a -> createAnswer(a, context)).toList();
    }
    return Collections.emptyList();
  }

  private Answer createAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer, Context context) {
    String value = this.answerValues.apply(answer);
    List<Item> subitems = createSubitems(answer, context);
    return new Answer(value, subitems);
  }

  private List<Item> createSubitems(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer, Context context) {
    if (answer.hasItem()) {
      return answer.getItem().stream().map(i -> createItem(i, context)).toList();
    }
    return Collections.emptyList();
  }
}
