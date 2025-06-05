package de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.disease;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.Feature;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.UrlPathQueryParamsContentResponseFeature;
import java.util.HashMap;
import java.util.Map;

final class QuestionnaireTranslations implements Feature {

  private static final String URL_REGEX_BASE =
      ".*\\/fhir-ui-data-model-translation/disease/questionnaire/CODE/items";

  private final Map<String, String> questionnaires = new HashMap<>();

  private static Feature configureWireMock(Map.Entry<String, String> questionnaire) {
    String urlRegex = URL_REGEX_BASE.replace("CODE", questionnaire.getKey());
    return UrlPathQueryParamsContentResponseFeature.builder()
        .requestUrlRegex(urlRegex)
        .responseBody(questionnaire.getValue())
        .build();
  }

  @Override
  public void run() {
    createQuestionnaireTranslations();
    activateQuestionnaireTranslations();
  }

  private void activateQuestionnaireTranslations() {
    this.questionnaires.entrySet().stream()
        .map(QuestionnaireTranslations::configureWireMock)
        .forEach(Feature::run);
  }

  private void createQuestionnaireTranslations() {
    putCommon();
    putCvdd();
  }

  private void putCommon() {
    this.questionnaires.put(
        "common",
"""
{
    "title": "Meldetatbestandsübergreifende klinische und epidemiologische Angaben",
    "items": {
        "labSpecimenTaken": "Laborbeauftragung",
        "additionalInformation": "Wichtige Zusatzinformationen",
        "infectProtectFacilityGroup": null,
        "placeExposureHint": "Anmerkungen zum Expositonsort",
        "hospitalizedGroup": null,
        "hospitalizedEncounter": null,
        "infectProtectFacilityOrganization": "Einrichtung",
        "placeExposureBegin": "Beginn des Aufenthalts am wahrscheinlichen Expositionsort/Datum des Aufenthalts",
        "placeExposureRegion": "Wahrscheinlicher Expositionsort",
        "placeExposure": "Wahrscheinlicher Expositionsort bekannt",
        "isDead": "Verstorben",
        "placeExposureGroup": null,
        "militaryAffiliation": "Zugehörigkeit zur Bundeswehr",
        "hospitalized": "Aufnahme in ein Krankenhaus",
        "organDonation": "Spender für eine Blut-, Organ-, Gewebe- oder Zellspende in den letzten 6 Monaten",
        "infectProtectFacilityBegin": "Beginn der Tätigkeit/Betreuung/Unterbringung",
        "infectProtectFacilityEnd": "Ende der Tätigkeit/Betreuung/Unterbringung",
        "placeExposureEnd": "Ende des Aufenthalts am wahrscheinlichen Expositionsort",
        "deathDate": "Datum des Todes",
        "infectProtectFacilityRole": "Rolle",
        "labSpecimenLab": "Beauftragtes Labor",
        "infectProtectFacility": "Tätigkeit, Betreuung oder Unterbringung in Einrichtungen mit Relevanz für den Infektionsschutz (siehe § 23 Abs. 3 IfSG, §35 Abs. 1 IfSG oder §36 Abs. 1 IfSG)"
    }
}""");
  }

  private void putCvdd() {
    this.questionnaires.put(
        "cvdd",
"""
{
    "title": "Covid-19-spezifische klinische und epidemiologische Angaben",
    "items": {
        "infectionEnvironmentSettingBegin": "Beginn Infektionsumfeld",
        "reason": "Grund der Hospitalisierung",
        "infectionEnvironmentSettingEnd": "Ende Infektionsumfeld",
        "infectionEnvironmentSetting": "Infektionsumfeld vorhanden",
        "infectionRiskKind": "Welche Risikofaktoren liegen bei der betroffenen Person vor?",
        "infectionEnvironmentSettingGroup": null,
        "infectionSource": "Kontakt zu bestätigtem Fall",
        "trimester": "Trimester",
        "outbreakNote": "Fallbezogene Zusatzinformationen zum Ausbruch",
        "immunization": "Wurde die betroffene Person jemals in Bezug auf die Krankheit geimpft?",
        "outbreak": "Kann der gemeldete Fall einem Ausbruch zugeordnet werden?",
        "immunizationRef": "Impfinformationen",
        "infectionEnvironmentSettingKind": "Wahrscheinliches Infektionsumfeld",
        "outbreakNotificationId": "Notification-Id der zugehörigen Ausbruchsmeldung"
    }
}""");
  }
}
