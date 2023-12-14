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

package de.gematik.demis.pdfgen.fhir.extract;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionnaireFhirQueries {
  private final FhirQueries fhirQueries;

  public Optional<QuestionnaireResponse> getBedOccupancyQuestionnaireResponse(Bundle bundle) {
    return fhirQueries.findResourceWithProfile(
        bundle, QuestionnaireResponse.class, DemisProfiles.STATISTIC_INFORMATION_BED_OCCUPANCY);
  }

  public Map<String, String> getQuestionnaireAnswers(
      Optional<QuestionnaireResponse> responseOptional) {
    if (responseOptional.isEmpty()) {
      return emptyMap();
    }
    return responseOptional.get().getItem().stream()
        .filter(i -> i.hasLinkId() && i.hasAnswer())
        .collect(
            toMap(
                QuestionnaireResponse.QuestionnaireResponseItemComponent::getLinkId,
                i -> i.getAnswerFirstRep().getValue().primitiveValue()));
  }
}
