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

import static java.util.Collections.emptyList;

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfectionEnvironmentSettingFactory {

  private static final String INFECTION_ENVIRONMENT_COMPONENT_NAME = "infectionenvironmentsetting";

  private final QuestionnaireResponseHelper questionnaireResponseHelper;

  public String hasInfectionEnvironmentSetting(final QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponseHelper.getCodingDisplayAnswerValue(
        questionnaireResponse, INFECTION_ENVIRONMENT_COMPONENT_NAME);
  }

  public List<InfectionEnvironmentSetting> create(
      final QuestionnaireResponse questionnaireResponse) {
    if (questionnaireResponse == null) {
      return emptyList();
    }
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> componentOpt =
        questionnaireResponseHelper.getComponent(
            questionnaireResponse, INFECTION_ENVIRONMENT_COMPONENT_NAME);
    if (componentOpt.isEmpty()) {
      return emptyList();
    }
    QuestionnaireResponse.QuestionnaireResponseItemComponent component = componentOpt.get();

    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer =
        component.getAnswer().get(0);
    return answer.getItem().stream().map(item -> createSingleEntry(item.getItem())).toList();
  }

  private InfectionEnvironmentSetting createSingleEntry(
      final List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
          infectionEnvironmentSettings) {
    String kind =
        questionnaireResponseHelper.getDisplayValueByLinkId(
            infectionEnvironmentSettings, "infectionEnvironmentSettingKind");
    DateTimeHolder begin =
        questionnaireResponseHelper.getDateTypeByLinkId(
            infectionEnvironmentSettings, "infectionEnvironmentSettingBegin");
    DateTimeHolder end =
        questionnaireResponseHelper.getDateTypeByLinkId(
            infectionEnvironmentSettings, "infectionEnvironmentSettingEnd");

    return InfectionEnvironmentSetting.builder().kind(kind).begin(begin).end(end).build();
  }
}
