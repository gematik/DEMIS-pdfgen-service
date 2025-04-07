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

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Answer;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Item;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.AnswerValues;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.Context;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Service;

/**
 * Factory that checks FHIR questionnaire response items to contain resource references. The main
 * feature is to check top-level items to contain a whole list of resource references. But it also
 * scans individual subitems.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ResourceFactory {

  private static final String HOSPITALIZATION_BLOCK_LINK_ID = "hospitalized";

  private final AnswerValues answerValues;
  private final HospitalizationFactory hospitalizationFactory;
  private final OrganizationFactory organizationFactory;
  private final ImmunizationBlockFactory immunizationBlockFactory;
  private final LabSpecimenTakenFactory labSpecimenTakenFactory;

  private static boolean isHospitalizationBlock(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return isLinkId(HOSPITALIZATION_BLOCK_LINK_ID, source) && hasAnswerWithSubitems(source);
  }

  private static boolean isLinkId(
      String linkId, QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return linkId.equals(source.getLinkId());
  }

  private static boolean hasAnswerWithSubitems(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return source.hasAnswer() && source.getAnswerFirstRep().hasItem();
  }

  /**
   * Create item containing a resource, if FHIR item is a resource reference
   *
   * @param item FHIR questionnaire response item
   * @param context FHIR questionnaire response context
   * @return rendered resource item or empty
   */
  public Optional<Item> createResourceItem(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item, Context context) {
    Optional<Item> block = createResourceBlock(item, context);
    if (block.isPresent()) {
      return block;
    }
    return createSingleResource(item, context);
  }

  private Optional<Item> createResourceBlock(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item, Context context) {
    if (isHospitalizationBlock(item)) {
      return Optional.of(renderHospitalizationsBlock(item, context));
    }
    if (this.immunizationBlockFactory.isImmunizationBlock(item)) {
      log.debug(
          "Disease notification questionnaire response renders immunizations resources block");
      return Optional.of(this.immunizationBlockFactory.createItem(item, context));
    }
    if (this.labSpecimenTakenFactory.isLabSpecimenTaken(item)) {
      log.debug("Disease notification questionnaire response renders lab-specimen-taken");
      return Optional.of(this.labSpecimenTakenFactory.createLabSpecimenTaken(item, context));
    }
    return Optional.empty();
  }

  private Optional<Item> createSingleResource(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source, Context context) {
    if (source.hasAnswer()) {
      var answers = source.getAnswer();
      if (answers.size() == 1) {
        var answer = source.getAnswerFirstRep();
        if (answer.hasValueReference()) {
          return createSingleResource(source, context, answer.getValueReference());
        }
      }
    }
    return Optional.empty();
  }

  private Optional<Item> createSingleResource(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source,
      Context context,
      Reference reference) {
    String text = context.translation().item(source.getLinkId());
    IBaseResource resource = reference.getResource();
    if (resource instanceof Organization organization) {
      log.debug("Disease notification questionnaire response renders organization resource");
      return Optional.of(
          new Item(text, Resource.organization(this.organizationFactory.create(organization))));
    }
    log.warn(
        "Unsupported type of single disease notification questionnaire response item resource reference. Reference: {}",
        reference);
    return Optional.empty();
  }

  private Item renderHospitalizationsBlock(
      QuestionnaireResponse.QuestionnaireResponseItemComponent source, Context context) {
    log.debug(
        "Disease notification questionnaire response renders hospitalization resources block");
    List<Item> subitems =
        this.hospitalizationFactory.create(context.questionnaire()).stream()
            .map(Resource::hospitalization)
            .map(Item::new)
            .toList();
    Answer answer = new Answer(getAnswerValue(source), subitems);
    String text = context.translation().item(source.getLinkId());
    return new Item(text, answer);
  }

  private String getAnswerValue(QuestionnaireResponse.QuestionnaireResponseItemComponent source) {
    return this.answerValues.apply(source.getAnswerFirstRep());
  }
}
