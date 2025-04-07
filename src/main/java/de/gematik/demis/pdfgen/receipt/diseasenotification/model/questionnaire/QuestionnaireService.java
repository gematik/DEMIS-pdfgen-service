package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire;

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

import de.gematik.demis.pdfgen.fhir.extract.FhirQueries;
import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.Context;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.QuestionnaireFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.translation.QuestionnaireTranslation;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.translation.QuestionnaireTranslations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class QuestionnaireService {

  private static final String CODE_COMMON_QUESTIONNAIRE = "common";
  private static final String PROFILE_URL_BASE_COMPOSITION =
      "https://demis.rki.de/fhir/StructureDefinition/NotificationDisease";
  private static final String PROFILE_URL_BASE_QUESTIONNAIRE_RESPONSE =
      "https://demis.rki.de/fhir/StructureDefinition/DiseaseInformation";

  private final FhirQueries fhirQueries;
  private final QuestionnaireTranslations questionnaireTranslations;
  private final QuestionnaireFactory questionnaireFactory;

  private static String getCompositionProfileUrl(Resource composition) {
    return composition.getMeta().getProfile().stream()
        .map(CanonicalType::getValueAsString)
        .filter(p -> p.startsWith(PROFILE_URL_BASE_COMPOSITION))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Notification bundle composition without profile URL"));
  }

  private static Resource getComposition(Bundle bundle) {
    return bundle.getEntry().stream()
        .filter(Bundle.BundleEntryComponent::hasResource)
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(Composition.class::isInstance)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Notification bundle without composition"));
  }

  public Questionnaire createCommonQuestionnaire(Bundle bundle) {
    try {
      var questionnaireResponse = getCommonQuestionnaireResponse(bundle);
      var code = CODE_COMMON_QUESTIONNAIRE;
      var translation = this.questionnaireTranslations.apply(code);
      return createQuestionnaire(questionnaireResponse, code, translation);
    } catch (Exception e) {
      log.warn(
          "Failed to create disease notification common information questionnaire response", e);
      return null;
    }
  }

  public Questionnaire createSpecificQuestionnaire(Bundle bundle) {
    try {
      var code = getDiseaseCode(bundle);
      String profileUrl = PROFILE_URL_BASE_QUESTIONNAIRE_RESPONSE + code.toUpperCase();
      var questionnaireResponse = getSpecificQuestionnaireResponse(bundle, profileUrl);
      if (questionnaireResponse == null) {
        return null;
      }
      var translation = this.questionnaireTranslations.apply(code);
      return createQuestionnaire(questionnaireResponse, code, translation);
    } catch (Exception e) {
      log.warn(
          "Failed to create disease notification specific information questionnaire response", e);
      return null;
    }
  }

  private QuestionnaireResponse getCommonQuestionnaireResponse(Bundle bundle) {
    return fhirQueries
        .findResourceWithProfile(
            bundle, QuestionnaireResponse.class, DemisProfiles.DISEASE_INFORMATION_COMMON)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Disease notification bundle without common information questionnaire response"));
  }

  private QuestionnaireResponse getSpecificQuestionnaireResponse(Bundle bundle, String profileUrl) {
    return fhirQueries
        .findResourceWithProfile(
            bundle, QuestionnaireResponse.class, DemisProfiles.profile(profileUrl))
        .orElse(null);
  }

  private Questionnaire createQuestionnaire(
      QuestionnaireResponse questionnaireResponse,
      String diseaseCode,
      QuestionnaireTranslation translation) {
    return this.questionnaireFactory.createQuestionnaire(
        new Context(questionnaireResponse, diseaseCode, translation));
  }

  private String getDiseaseCode(Bundle bundle) {
    Resource composition = getComposition(bundle);
    String profileUrl = getCompositionProfileUrl(composition);
    return profileUrl.substring(profileUrl.length() - 4);
  }
}
