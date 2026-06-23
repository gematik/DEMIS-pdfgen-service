package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportQuantitiesBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportRangeBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportRatioBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportTransactionIdBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums.LabTestStatusEnum;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LabTestFactoryIntegrationTest {

  @Autowired private LabTestFactory labTestFactory;

  @Test
  void createLabTests_createLabTestsAsExpected() {
    // given
    Bundle bundle = createLaboratoryReportBundle();

    // when
    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    // then
    assertThat(labTests).hasSize(1);
    LabTest labTest = labTests.getFirst();
    assertThat(labTest.getCode())
        .isEqualTo(
            "SARS-CoV-2 (COVID-19) RNA [Presence] in Serum or Plasma by NAA with probe detection");
    assertThat(labTest.getStatus()).isEqualTo(LabTestStatusEnum.FINAL);
    assertThat(labTest.getValue()).isEqualTo("Detected");
    assertThat(labTest.getInterpretation()).isEqualTo("Positive");
    assertThat(labTest.getMethod()).isEqualTo("Nucleic acid assay (procedure)");
    assertThat(labTest.getNotes()).hasSize(1);
    assertThat(labTest.getNotes().getFirst()).isEqualTo("Nette Zusatzinformation …");
    assertThat(labTest.getSpecimen().getTransactionId())
        .isEqualToIgnoringCase("IMS-12345-CVDP-00001");
  }

  @Test
  void createLabTests_keepsGermanTimeForReceivedAndCollectedTime() {
    // given
    Bundle bundle = createLaboratoryReportBundle();
    final Specimen specimen = (Specimen) bundle.getEntry().get(10).getResource();
    specimen.setReceivedTime(new DateTimeType("2025-05-14T22:38:00+02:00").getValue());
    specimen.getCollection().setCollected(new DateTimeType("2025-05-13T22:38:00+02:00"));

    // when
    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    // then
    assertThat(labTests).hasSize(1);
    assertThat(labTests.getFirst().getSpecimen().getReceivedTime().toString())
        .isEqualTo("14.05.2025 22:38");
    assertThat(labTests.getFirst().getSpecimen().getCollectionTime().toString())
        .isEqualTo("13.05.2025 22:38");
  }

  @Test
  void createLabTests_setsGermanTimeForReceivedAndCollectedTime_fromDifferentTimeZone() {
    // given
    Bundle bundle = createLaboratoryReportBundle();
    final Specimen specimen = (Specimen) bundle.getEntry().get(10).getResource();
    specimen.setReceivedTime(new DateTimeType("2025-05-14T22:38:00Z").getValue());
    specimen.getCollection().setCollected(new DateTimeType("2025-05-13T22:38:00Z"));

    // when
    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    // then
    assertThat(labTests).hasSize(1);
    assertThat(labTests.getFirst().getSpecimen().getReceivedTime().toString())
        .isEqualTo("15.05.2025 00:38");
    assertThat(labTests.getFirst().getSpecimen().getCollectionTime().toString())
        .isEqualTo("14.05.2025 00:38");
  }

  @Test
  void createLabTests_createLabTestsWithTransactionIdAsExpected() {
    // given
    Bundle bundle = createLaboratoryReportTransactionIdBundle();

    // when
    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    // then
    assertThat(labTests).hasSize(1);
    LabTest labTest = labTests.getFirst();
    assertThat(labTest.getCode())
        .isEqualTo("Influenza virus A H7 RNA [Presence] in Specimen by NAA with probe detection");
    assertThat(labTest.getStatus()).isEqualTo(LabTestStatusEnum.FINAL);
    assertThat(labTest.getSpecimen()).isNotNull();
    assertThat(labTest.getSpecimen().getTransactionId())
        .isEqualToIgnoringCase("IMS-12345-CVDP-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX");
  }

  @Test
  void createLabTests_createLabTestsWithQuantitiesAsExpected() {
    // given
    Bundle bundle = createLaboratoryReportQuantitiesBundle();

    // when
    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    // then
    assertThat(labTests).hasSize(2);
    assertThat(labTests.get(0).getValue()).isEqualTo("6.0 [IU]/mL");
    assertThat(labTests.get(1).getValue()).isEqualTo("8.0 U/mL");
  }

  @Test
  void createLabTests_createLabTestsWithRatioAsExpected() {
    // given
    Bundle bundle = createLaboratoryReportRatioBundle();

    // when
    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    // then
    assertThat(labTests).hasSize(1);
    assertThat(labTests.getFirst().getValue()).isEqualTo("1.0:100.0");
  }

  @Test
  void createLabTests_createLabTestsWithRangeAsExpected() {
    // given
    Bundle bundle = createLaboratoryReportRangeBundle();

    // when
    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    // then
    assertThat(labTests).hasSize(1);
    assertThat(labTests.getFirst().getValue()).isEqualTo("von 5 bis 15");
  }

  @Nested
  @SpringBootTest(properties = {"feature.flag.pdf-optimization=true"})
  class Ratio_PdfOptimizationEnabled {

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithBothComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();
      final Observation resource = (Observation) bundle.getEntry().get(8).getResource();
      final Ratio ratio = (Ratio) resource.getValue();
      ratio.getNumerator().setComparator(Quantity.QuantityComparator.GREATER_THAN);
      ratio.getDenominator().setComparator(Quantity.QuantityComparator.GREATER_OR_EQUAL);

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo(">1.0:>=100.0");
    }

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithNumeratorComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();
      final Observation resource = (Observation) bundle.getEntry().get(8).getResource();
      final Ratio ratio = (Ratio) resource.getValue();
      ratio.getNumerator().setComparator(Quantity.QuantityComparator.GREATER_THAN);

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo(">1.0:100.0");
    }

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithDenominatorComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();
      final Observation resource = (Observation) bundle.getEntry().get(8).getResource();
      final Ratio ratio = (Ratio) resource.getValue();
      ratio.getDenominator().setComparator(Quantity.QuantityComparator.GREATER_THAN);

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo("1.0:>100.0");
    }

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithNoComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo("1.0:100.0");
    }
  }

  @Nested
  @SpringBootTest(properties = {"feature.flag.pdf-optimization=false"})
  class Ratio_PdfOptimizationDisabled {

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithBothComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();
      final Observation resource = (Observation) bundle.getEntry().get(8).getResource();
      final Ratio ratio = (Ratio) resource.getValue();
      ratio.getNumerator().setComparator(Quantity.QuantityComparator.GREATER_THAN);
      ratio.getDenominator().setComparator(Quantity.QuantityComparator.GREATER_THAN);

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo("1.0:100.0");
    }

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithNumeratorComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();
      final Observation resource = (Observation) bundle.getEntry().get(8).getResource();
      final Ratio ratio = (Ratio) resource.getValue();
      ratio.getNumerator().setComparator(Quantity.QuantityComparator.GREATER_THAN);

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo("1.0:100.0");
    }

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithDenominatorComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();
      final Observation resource = (Observation) bundle.getEntry().get(8).getResource();
      final Ratio ratio = (Ratio) resource.getValue();
      ratio.getDenominator().setComparator(Quantity.QuantityComparator.GREATER_THAN);

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo("1.0:100.0");
    }

    @Test
    void createLabTests_createLabTestsWithRatioAsExpected_WithNoComparator() {
      // given
      Bundle bundle = createLaboratoryReportRatioBundle();

      // when
      List<LabTest> labTests = labTestFactory.createLabTests(bundle);

      // then
      assertThat(labTests).hasSize(1);
      assertThat(labTests.getFirst().getValue()).isEqualTo("1.0:100.0");
    }
  }
}
