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
 * #L%
 */

import java.util.*;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LaboratoryFhirQueries {

  private final NotificationFhirQueries notificationFhirQueries;

  public Optional<DiagnosticReport> getLaboratoryReport(final Bundle bundle) {
    return getNotification(bundle).flatMap(LaboratoryFhirQueries::getDiagnosticReport);
  }

  private static Optional<DiagnosticReport> getDiagnosticReport(Composition notification) {
    return notification.getSection().stream()
        .map(Composition.SectionComponent::getEntry)
        .flatMap(Collection::stream)
        .map(Reference::getResource)
        .filter(DiagnosticReport.class::isInstance)
        .map(DiagnosticReport.class::cast)
        .findFirst();
  }

  @Nonnull
  public List<Observation> getPathogenDetections(final Bundle bundle) {
    List<Observation> pathogenDetections =
        new LinkedList<>(getPathogenDetectionsFromLaboratoryReport(bundle));
    getObservation(bundle).ifPresent(pathogenDetections::add);
    return pathogenDetections;
  }

  public Map<Observation, Specimen> getPathogenDetectionToSpecimenMap(final Bundle bundle) {
    return getLaboratoryReport(bundle)
        .filter(DiagnosticReport::hasResult)
        .map(LaboratoryFhirQueries::getPathogenDetectionsSpecimen)
        .orElseGet(Collections::emptyMap);
  }

  private static Map<Observation, Specimen> getPathogenDetectionsSpecimen(DiagnosticReport report) {
    Map<Observation, Specimen> pathogenDetectionsSpecimen = new HashMap<>();
    for (Observation pathogenDetection : getObservations(report)) {
      if (pathogenDetection.getSpecimen().getResource() instanceof Specimen specimen) {
        pathogenDetectionsSpecimen.put(pathogenDetection, specimen);
      }
    }
    return pathogenDetectionsSpecimen;
  }

  private List<Observation> getPathogenDetectionsFromLaboratoryReport(final Bundle bundle) {
    return getLaboratoryReport(bundle)
        .filter(DiagnosticReport::hasResult)
        .map(LaboratoryFhirQueries::getObservations)
        .orElseGet(Collections::emptyList);
  }

  private static List<Observation> getObservations(DiagnosticReport report) {
    return report.getResult().stream()
        .filter(Reference::hasReference)
        .map(Reference::getResource)
        .filter(Observation.class::isInstance)
        .map(Observation.class::cast)
        .toList();
  }

  private Optional<Observation> getObservation(final Bundle bundle) {
    return getNotification(bundle).flatMap(LaboratoryFhirQueries::getObservation);
  }

  private Optional<Composition> getNotification(Bundle bundle) {
    return this.notificationFhirQueries.getNotification(bundle).filter(Composition::hasSection);
  }

  private static Optional<Observation> getObservation(Composition notification) {
    return notification.getSection().stream()
        .map(Composition.SectionComponent::getEntry)
        .flatMap(Collection::stream)
        .map(Reference::getResource)
        .filter(Observation.class::isInstance)
        .map(Observation.class::cast)
        .findFirst();
  }
}
