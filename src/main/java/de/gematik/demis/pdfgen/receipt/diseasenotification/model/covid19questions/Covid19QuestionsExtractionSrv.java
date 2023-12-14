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

import de.gematik.demis.pdfgen.fhir.extract.ConditionQueries;
import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Covid19QuestionsExtractionSrv {

  private static final String INFECTION_SOURCE_COMPONENT_NAME = "infectionsource";

  private final ConditionQueries conditionQueries;
  private final QuestionnaireResponseHelper questionnaireResponseHelper;
  private final ImmunizationFactory immunizationFactory;
  private final InfectionEnvironmentSettingFactory infectionEnvironmentSettingFactory;

  public Covid19Questions create(Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    Optional<QuestionnaireResponse> questionnaireResponseOptional =
        conditionQueries.getQuestionnaireResponse(bundle, DemisProfiles.DISEASE_INFORMATION_CVDD);
    if (questionnaireResponseOptional.isEmpty()) {
      return null;
    }
    QuestionnaireResponse questionnaireResponse = questionnaireResponseOptional.get();
    String infectionSource =
        questionnaireResponseHelper.getCodingDisplayAnswerValue(
            questionnaireResponse, INFECTION_SOURCE_COMPONENT_NAME);

    String hasInfectionEnvironmentSetting =
        infectionEnvironmentSettingFactory.hasInfectionEnvironmentSetting(questionnaireResponse);
    List<InfectionEnvironmentSetting> infectionEnvironmentSettings =
        infectionEnvironmentSettingFactory.create(questionnaireResponse);

    String hasImmunization = immunizationFactory.hasImmunization(questionnaireResponse);

    Optional<Condition> condition = conditionQueries.getCondition(bundle);
    String diseaseCode;
    if (condition.isPresent()) {
      diseaseCode = condition.get().getCode().getCodingFirstRep().getCode();
    } else {
      diseaseCode = "";
    }
    List<Immunization> immunizations =
        immunizationFactory.create(questionnaireResponse, diseaseCode);

    return Covid19Questions.builder()
        .infectionSource(infectionSource)
        .hasInfectionEnvironmentSetting(hasInfectionEnvironmentSetting)
        .infectionEnvironmentSettings(infectionEnvironmentSettings)
        .hasImmunization(hasImmunization)
        .immunizations(immunizations)
        .build();
  }
}
