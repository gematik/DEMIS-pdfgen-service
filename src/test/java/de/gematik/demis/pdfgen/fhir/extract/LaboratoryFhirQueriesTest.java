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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createBedOccupancyBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.test.helper.FhirFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.Test;

class LaboratoryFhirQueriesTest {

  private final Bundle bedOccupancyBundle = createBedOccupancyBundle();
  private final Bundle laboratoryReportBundle = createLaboratoryReportBundle();
  private final LaboratoryFhirQueries laboratoryFhirQueries =
      new LaboratoryFhirQueries(new NotificationFhirQueries());

  @Test
  void getLaboratoryReport_shouldHandleNullGracefully() {
    assertThat(laboratoryFhirQueries.getLaboratoryReport(null)).isEmpty();
  }

  @Test
  void getLaboratoryReport_shouldNotLoadNonExistingLabReport() {
    // when
    Optional<DiagnosticReport> labReportOptional =
        laboratoryFhirQueries.getLaboratoryReport(bedOccupancyBundle);
    // then
    assertThat(labReportOptional).isEmpty();
  }

  @Test
  void getLaboratoryReport_shouldLoadLabReport() {
    // when
    Optional<DiagnosticReport> labReportOptional =
        laboratoryFhirQueries.getLaboratoryReport(laboratoryReportBundle);
    // then
    assertThat(labReportOptional)
        .isPresent()
        .get()
        .extracting("Id")
        .isEqualTo(
            "https://demis.rki.de/fhir/DiagnosticReport/5d3a54e0-0af5-4e61-a917-c1e0f063af4b");
  }

  @Test
  void getPathogenDetectionSet_shouldHandleNullGracefully() {
    assertThat(laboratoryFhirQueries.getPathogenDetections(null)).isEmpty();
  }

  @Test
  void getPathogenDetectionSet_shouldNotLoadNonExistingPathogenDetections() {
    // when
    List<Observation> observations =
        laboratoryFhirQueries.getPathogenDetections(bedOccupancyBundle);
    // then
    assertThat(observations).isNotNull().isEmpty();
  }

  @Test
  void getPathogenDetectionSet_shouldLoadPathogenDetections() {
    // when
    List<Observation> observations =
        laboratoryFhirQueries.getPathogenDetections(
            FhirFactory.createLaboratoryReportPathogensBundle());
    // then
    assertThat(observations).isNotNull().hasSize(2);
    Iterator<Observation> iterator = observations.iterator();
    assertThat(iterator.next().getId())
        .isEqualTo("https://demis.rki.de/fhir/Observation/4cf6dbcb-07b4-4794-bbd3-c052a1d67135");
    assertThat(iterator.next().getId())
        .isEqualTo("https://demis.rki.de/fhir/Observation/4cf6dbcb-07b4-4794-bbd3-c052a1d67142");
  }

  @Test
  void getPathogenDetectionToSpecimenMap_shouldHandleNullGracefully() {
    assertThat(laboratoryFhirQueries.getPathogenDetectionToSpecimenMap(null))
        .isInstanceOf(Map.class)
        .isEmpty();
  }

  @Test
  void getPathogenDetectionToSpecimenMap_shouldCreateEmptyMapOnMismatchedBundle() {
    // when
    Map<Observation, Specimen> map =
        laboratoryFhirQueries.getPathogenDetectionToSpecimenMap(bedOccupancyBundle);
    // then
    assertThat(map).isNotNull().isEmpty();
  }

  @Test
  void getPathogenDetectionToSpecimenMap_shouldCreateMapOfSpecimenByObservation() {
    // when
    Map<Observation, Specimen> map =
        laboratoryFhirQueries.getPathogenDetectionToSpecimenMap(laboratoryReportBundle);
    // then
    assertThat(map).isNotNull().hasSize(1);
    Map.Entry<Observation, Specimen> mapEntry = map.entrySet().iterator().next();
    assertThat(mapEntry.getKey().getId())
        .isEqualTo("https://demis.rki.de/fhir/Observation/4cf6dbcb-07b4-4794-bbd3-c052a1d67135");
    assertThat(mapEntry.getValue().getId())
        .isEqualTo("https://demis.rki.de/fhir/Specimen/5497b5f4-2994-4c8c-a94e-e37ad111e220");
  }
}
