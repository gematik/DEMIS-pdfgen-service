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

import static java.util.Collections.emptyList;

import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.BaseReference;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Service;

@Service
public class ConditionQueries {

  public Optional<Condition> getCondition(final Bundle bundle) {
    for (Composition.SectionComponent section : getSections(bundle)) {
      IBaseResource resource = section.getEntry().get(0).getResource();
      if (resource instanceof Condition fhirCondition) {
        return Optional.of(fhirCondition);
      }
    }
    return Optional.empty();
  }

  public Optional<QuestionnaireResponse> getQuestionnaireResponse(
      final Bundle bundle, DemisProfiles.Profile<QuestionnaireResponse> profile) {
    for (Composition.SectionComponent section : getSections(bundle)) {

      Optional<QuestionnaireResponse> questionnaireResponseOpt =
          section.getEntry().stream()
              .map(BaseReference::getResource)
              .filter(QuestionnaireResponse.class::isInstance)
              .map(QuestionnaireResponse.class::cast)
              .filter(profile::isApplied)
              .findFirst();
      if (questionnaireResponseOpt.isPresent()) {
        return questionnaireResponseOpt;
      }
    }
    return Optional.empty();
  }

  public List<Composition.SectionComponent> getSections(final Bundle bundle) {
    Resource notificationDiseaseResource = bundle.getEntryFirstRep().getResource();
    if (notificationDiseaseResource instanceof Composition notificationDisease) {
      return notificationDisease.getSection();
    }
    return emptyList();
  }
}
