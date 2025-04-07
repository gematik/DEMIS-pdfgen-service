package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources;

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

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Answer;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Item;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.AnswerValues;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.Context;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

/** Extracts a laboratory from the "lab specimen taken" questionnaire response item. */
@RequiredArgsConstructor
@Service
class LabSpecimenTakenFactory {

  static final String LINK_ID = "labSpecimenTaken";

  private final OrganizationFactory organizationFactory;
  private final AnswerValues answerValues;

  private static String getText(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item, Context context) {
    return context.translation().item(item.getLinkId());
  }

  boolean isLabSpecimenTaken(QuestionnaireResponse.QuestionnaireResponseItemComponent item) {
    return LINK_ID.equals(item.getLinkId())
        && item.hasAnswer()
        && item.getAnswerFirstRep().hasItem();
  }

  Item createLabSpecimenTaken(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item, Context context) {
    String text = getText(item, context);
    Answer answer = createAnswer(item, context);
    return new Item(text, answer);
  }

  private Answer createAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item, Context context) {
    String text = this.answerValues.apply(item.getAnswerFirstRep());
    Item laboratory = createLaboratory(item, context);
    return new Answer(text, Collections.singletonList(laboratory));
  }

  private Item createLaboratory(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item, Context context) {
    QuestionnaireResponse.QuestionnaireResponseItemComponent labItem =
        item.getAnswerFirstRep().getItemFirstRep();
    String text = context.translation().item(labItem.getLinkId());
    OrganizationDTO organization =
        this.organizationFactory.create(
            (Organization) labItem.getAnswerFirstRep().getValueReference().getResource());
    return new Item(text, Resource.laboratory(organization));
  }
}
