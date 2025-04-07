package de.gematik.demis.pdfgen.fhir.schema;

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

import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Service;

@Service
public class SchemaVersionChecker {

  public SchemaVersion getSchemaVersion(final Bundle bundle) {
    Resource resource = bundle.getEntryFirstRep().getResource();
    if (!(resource instanceof Composition composition)) {
      return SchemaVersion.UNKNOWN;
    } else {
      if (hasDiagnosticReport(composition)) {
        return SchemaVersion.V2;
      }

      if (hasConditionAndObservation(composition)) {
        return SchemaVersion.V1;
      }
    }
    return SchemaVersion.UNKNOWN;
  }

  private static boolean hasDiagnosticReport(Composition composition) {
    return compositionHas(composition, DiagnosticReport.class); // Is version for all pathogens
  }

  private static boolean hasConditionAndObservation(Composition composition) {
    boolean hasCondition = compositionHas(composition, Condition.class);
    boolean hasObservation = compositionHas(composition, Observation.class);

    return hasCondition && hasObservation;
  }

  private static boolean compositionHas(
      Composition composition, Class<? extends IBaseResource> referenceClass) {
    if (composition == null) {
      return false;
    }
    final List<Composition.SectionComponent> sections = composition.getSection();

    return sections.stream()
        .map(Composition.SectionComponent::getEntry)
        .flatMap(Collection::stream)
        .filter(
            reference ->
                StringUtils.contains(reference.getReference(), referenceClass.getSimpleName()))
        .map(Reference::getResource)
        .anyMatch(res -> (referenceClass.equals(res.getClass())));
  }
}
