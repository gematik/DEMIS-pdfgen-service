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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportTransactionIdBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums.LabTestStatusEnum;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
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
    LabTest labTest = labTests.get(0);
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
}
