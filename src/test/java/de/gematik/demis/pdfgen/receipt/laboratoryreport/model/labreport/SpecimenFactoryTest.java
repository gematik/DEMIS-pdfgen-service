package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport;

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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.fhir.extract.LaboratoryFhirQueries;
import de.gematik.demis.pdfgen.fhir.schema.SchemaVersionChecker;
import de.gematik.demis.pdfgen.test.helper.FhirFactory;
import de.gematik.demis.pdfgen.translation.TranslationService;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SpecimenFactoryTest {

  @Mock private LaboratoryFhirQueries laboratoryFhirQueries;

  @Spy // We need the real thing here and don't want to mess around with creating instances
  private SchemaVersionChecker schemaVersionChecker;

  @Mock private TranslationService displayTranslationService;

  @InjectMocks private SpecimenFactory specimenFactory;

  @Test
  void createCanHandleEmptyBundle() {
    Specimen actual = specimenFactory.create(null, FhirFactory.createEmptyBundle());
    assertThat(actual).isNull();
  }

  @Test
  void canHandleDv1Bundle() {
    // GIVEN an observation with a plain specimen
    org.hl7.fhir.r4.model.Specimen fhirSpecimen = new org.hl7.fhir.r4.model.Specimen();
    Reference specimenRef = new Reference(fhirSpecimen);
    Observation observation = new Observation();
    observation.setSpecimen(specimenRef);

    // WHEN a new gematik Specimen is created using a DV1 bundle
    Specimen specimen =
        specimenFactory.create(
            observation, FhirFactory.createBundle(FhirFactory.LABORATORY_REPORT_BUNDLE_DV1_JSON));

    // THEN the code is not producing exceptions
    assertThat(specimen).isNotNull();
  }
}
