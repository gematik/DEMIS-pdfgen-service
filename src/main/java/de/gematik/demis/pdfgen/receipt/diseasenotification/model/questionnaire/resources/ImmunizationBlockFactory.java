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

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Answer;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Item;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.AnswerValues;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.Context;
import de.gematik.demis.pdfgen.translation.TranslationService;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;

/**
 * Processing FHIR questionnaire response top-level items that contain references of immunization
 * resources. The factory provides a method to check items to fit that scheme and a method to render
 * such items from HAPI FHIR to "Thymeleaf-POJO".
 *
 * <p>"Thymeleaf-POJO" objects enable Thymeleaf HTML templates to process business details easier
 * than having to work with HAPI FHIR objects.
 */
@RequiredArgsConstructor
@Service
@Slf4j
class ImmunizationBlockFactory {

  private static final String IMMUNIZATION_BLOCK_LINK_ID = "immunization";
  private static final String IMMUNIZATION_MOTHER_BLOCK_LINK_ID = "immunizationMother";

  private final TranslationService translationService;
  private final AnswerValues answerValues;

  private static boolean hasAnswerWithSubitems(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return source.hasAnswer() && source.getAnswerFirstRep().hasItem();
  }

  private static String getNote(org.hl7.fhir.r4.model.Immunization fhir) {
    return fhir.hasNote() ? fhir.getNote().get(0).getText() : null;
  }

  private static String getOccurrence(org.hl7.fhir.r4.model.Immunization fhir) {
    Type occurrence = fhir.getOccurrence();
    if (occurrence instanceof StringType text) {
      return text.getValue();
    }
    if (occurrence instanceof DateTimeType dateTime) {
      return new DateTimeHolder(dateTime).toString();
    }
    return null;
  }

  /**
   * Create "Thymeleaf-POJO" item of immunization block that enables Thymeleaf HTML templates to
   * process immunization details.
   *
   * @param immunizationBlock FHIR questionnaire response top-level item that contains an
   *     information block of immunizations or immunizations of mother
   * @return rendered top-level item containing one or more immunizations
   */
  public Item createItem(
      QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationBlock, Context context) {
    List<Item> subitems = createImmunizations(immunizationBlock, context);
    Answer answer = new Answer(getAnswerValue(immunizationBlock), subitems);
    String text = context.translation().item(immunizationBlock.getLinkId());
    return new Item(text, answer);
  }

  /**
   * Check FHIR questionnaire response top-level item to be an immunization resource reference block
   * or not.
   *
   * @param source FHIR questionnaire response item
   * @return <code>true</code> if item is an immunization reference block, <code>false</code> if not
   */
  public boolean isImmunizationBlock(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return (source != null) && linkId(source) && hasAnswerWithSubitems(source);
  }

  private List<Item> createImmunizations(
      QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationBlock, Context context) {
    List<Immunization> immunizations = getSortedImmunizations(immunizationBlock);
    String itemText = getImmunizationItemText(immunizationBlock, context);
    return immunizations.stream()
        .map(Resource::immunization)
        .map(r -> new Item(itemText, r))
        .toList();
  }

  private String getImmunizationItemText(
      QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationBlock, Context context) {
    QuestionnaireResponse.QuestionnaireResponseItemComponent firstImmunization =
        immunizationBlock.getAnswerFirstRep().getItemFirstRep();
    return context.translation().item(firstImmunization.getLinkId());
  }

  private String getAnswerValue(QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return this.answerValues.apply(source.getAnswerFirstRep());
  }

  private List<Immunization> getSortedImmunizations(
      QuestionnaireResponse.QuestionnaireResponseItemComponent block) {
    return block.getAnswerFirstRep().getItem().stream()
        .map(this::getFhirImmunization)
        .sorted(this::compare)
        .map(this::render)
        .toList();
  }

  private Immunization render(org.hl7.fhir.r4.model.Immunization immunization) {
    String vaccine = getVaccine(immunization);
    String occurrence = getOccurrence(immunization);
    String note = getNote(immunization);
    return new Immunization(vaccine, occurrence, note);
  }

  private String getVaccine(org.hl7.fhir.r4.model.Immunization fhir) {
    return this.translationService.resolveCodeableConceptValues(fhir.getVaccineCode());
  }

  private org.hl7.fhir.r4.model.Immunization getFhirImmunization(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return (org.hl7.fhir.r4.model.Immunization)
        source.getAnswerFirstRep().getValueReference().getResource();
  }

  private int compare(
      org.hl7.fhir.r4.model.Immunization i1, org.hl7.fhir.r4.model.Immunization i2) {
    return i1.getOccurrence().toString().compareTo(i2.getOccurrence().toString());
  }

  private boolean linkId(QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    String linkId = source.getLinkId();
    return IMMUNIZATION_BLOCK_LINK_ID.equals(linkId)
        || IMMUNIZATION_MOTHER_BLOCK_LINK_ID.equals(linkId);
  }
}
