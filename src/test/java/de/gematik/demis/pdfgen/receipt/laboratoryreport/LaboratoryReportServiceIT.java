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

package de.gematik.demis.pdfgen.receipt.laboratoryreport;

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.LABORATORY_REPORT_BUNDLE_DV2_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.LABORATORY_REPORT_BUNDLE_DV2_XML;
import static de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper.extractPdfText;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.pdf.PdfData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LaboratoryReportServiceIT {

  @Autowired private LaboratoryReportService laboratoryReportService;

  @Test
  void generatePdfFromBundleJsonString_shouldCreateBinaryPdfFromJsonFhirBundle() throws Exception {
    // when
    PdfData pdfData =
        laboratoryReportService.generatePdfFromBundleJsonString(LABORATORY_REPORT_BUNDLE_DV2_JSON);

    // then
    assertThat(pdfData.bytes()).isNotNull();
    String pdfText = extractPdfText(pdfData);
    validateLaboratoryReportPdfText(pdfText);
  }

  @Test
  void generatePdfFromBundleXmlString_shouldCreateBinaryPdfFromXmlFhirBundle() throws Exception {
    // when
    PdfData pdfData =
        laboratoryReportService.generatePdfFromBundleXmlString(LABORATORY_REPORT_BUNDLE_DV2_XML);

    // then
    assertThat(pdfData.bytes()).isNotNull();
    String pdfText = extractPdfText(pdfData);
    validateLaboratoryReportPdfText(pdfText);
  }

  private void validateLaboratoryReportPdfText(String pdfText) {
    assertThat(pdfText)
        .containsAnyOf(
            "Empfangsbestätigung",
            "Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet",
            "Meldevorgangs-ID a5e00874-bb26-45ac-8eea-0bde76456703",
            "Meldungsidentifier e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9");
  }
}
