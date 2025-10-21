package de.gematik.demis.pdfgen.receipt.laboratoryreport;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.*;
import static de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper.extractPdfText;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.pdf.PdfData;
import de.gematik.demis.pdfgen.pdf.PdfGenerationException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LaboratoryReportServiceIntegrationTest {

  /** A pattern to match the headline for the lifecycle headline. */
  private static final Pattern LIFECYCLE_HEADLINE_PATTERN =
      Pattern.compile("DEMIS.+Lifecyclemanagement");

  @Autowired private LaboratoryReportService laboratoryReportService;

  @Test
  void generatePdfFromBundleJsonString_shouldCreateBinaryPdfFromJsonFhirBundle() throws Exception {
    generateAndValidateLaboratoryReportPdf(
        laboratoryReportService.generatePdfFromBundleJsonString(LABORATORY_REPORT_BUNDLE_DV2_JSON));
  }

  @Test
  void generatePdfFromBundleXmlString_shouldCreateBinaryPdfFromXmlFhirBundle() throws Exception {
    generateAndValidateLaboratoryReportPdf(
        laboratoryReportService.generatePdfFromBundleXmlString(LABORATORY_REPORT_BUNDLE_DV2_XML));
  }

  @Test
  void generateSubmitterInformation() throws Exception {
    String pdfText =
        generateAndValidateLaboratoryReportPdf(
            laboratoryReportService.generatePdfFromBundleJsonString(
                LABORATORY_REPORT_BUNDLE_DV2_WITH_SUBMITTER));
    assertThat(pdfText).contains("Contact Person @ Station");
  }

  @Test
  void generatePdfWithProvenanceInformation() throws Exception {
    String pdfText =
        generateAndValidateLaboratoryReportPdf(
            laboratoryReportService.generatePdfFromBundleJsonString(
                LABORATORY_REPORT_BUNDLE_DV2_WITH_PROVENANCE_BUNDID));
    assertThat(pdfText)
        .containsAnyOf(
            "Meldeweg", "Portal",
            "Authentifizierungsmethode", "BundID",
            "Vertrauensniveau", "niedrig");
  }

  private @NotNull String generateAndValidateLaboratoryReportPdf(PdfData laboratoryReportService)
      throws PdfGenerationException, IOException {
    PdfData pdfData = laboratoryReportService;
    assertThat(pdfData.bytes()).isNotNull();
    String pdfText = extractPdfText(pdfData);
    validateLaboratoryReportPdfText(pdfText);
    return pdfText;
  }

  @Test
  void addLaboratorySummaryToLifecyclePage() throws Exception {
    // GIVEN we have a bundle with observation and specimen
    // WHEN we generate a PDF
    PdfData pdfData =
        laboratoryReportService.generatePdfFromBundleJsonString(
            LABORATORY_REPORT_BUNDLE_DV2_PATHOGENS_JSON);

    // THEN we add some key details to the lifecycle page
    String pdfText = extractPdfText(pdfData);
    pdfText = skipToLifecyclePage(pdfText);
    // extractPdfText() occasionally generates trailing whitespace characters. We explicitly use a
    // whitespace(\s) here
    // to match the full block of text that we expect. We assume that testing the full block is more
    // robust.
    assertThat(pdfText)
        .contains(
            """
                Meldetatbestand CVDP
                Probenentnahme 04.03.2021\s
                Probenmaterial Upper respiratory swab sample (specimen)
                Probenentnahme 04.03.2021\s
                Probenmaterial Upper respiratory swab sample (specimen)""");
  }

  @Test
  void canHandleMultipleSpecimen() throws Exception {
    // GIVEN we have a bundle with observation and specimen
    // WHEN we generate a PDF
    PdfData pdfData =
        laboratoryReportService.generatePdfFromBundleJsonString(
            LABORATORY_REPORT_BUNDLE_DV2_WITH_MULTIPLE_SPECIMEN);

    // THEN we add some key details to the lifecycle page
    String pdfText = extractPdfText(pdfData);
    pdfText = skipToLifecyclePage(pdfText);
    // extractPdfText() occasionally generates trailing whitespace characters. We explicitly use a
    // whitespace(\s) here
    // to match the full block of text that we expect. We assume that testing the full block is more
    // robust.
    assertThat(pdfText)
        .contains(
            """
            Meldetatbestand CVDP
            Probenentnahme 04.03.2021\s
            Probenmaterial Upper respiratory swab sample (specimen)
            Probenentnahme 04.03.2023\s
            Probenmaterial Nucleic acid assay (procedure)
            Probenmaterial Specimen from throat (specimen)""");
  }

  @Test
  void collectionDateIsOptionalForLaboratorySummaryOnLifecyclePage() throws Exception {
    // GIVEN we have a bundle without observation and specimen
    // WHEN we generate a PDF
    PdfData pdfData =
        laboratoryReportService.generatePdfFromBundleJsonString(
            LABORATORY_NOTIFICATION_WITHOUT_SPECIMEN_COLLECTION_TIME);

    // THEN we DON'T add some details to the lifecycle page
    String pdfText = extractPdfText(pdfData);
    pdfText = skipToLifecyclePage(pdfText);
    assertThat(pdfText)
        .contains(
            """
        Meldetatbestand CVDP
        Probenmaterial Upper respiratory swab sample (specimen)
        Probenmaterial Upper respiratory swab sample (specimen)""");
  }

  @Test
  void createLabTests_createLabTestsWithReasonForTestingAsExpected() throws Exception {
    // GIVEN we have a bundle without observation and specimen
    // WHEN we generate a PDF
    PdfData pdfData =
        laboratoryReportService.generatePdfFromBundleJsonString(
            LABORATORY_REPORT_BUNDLE_DV2_WITH_REASON_FOR_TESTING);

    // THEN extract text and verify the content
    String pdfText = extractPdfText(pdfData);
    assertThat(pdfText)
        .contains("Grund der Untersuchung Sample - microbiological exam (procedure)");
  }

  @Test
  void skipLaboratorySummaryOnLifecyclePageIfNotPresent() throws Exception {
    // GIVEN a bundle without observation and specimen
    // WHEN we generate a PDF
    PdfData pdfData =
        laboratoryReportService.generatePdfFromBundleJsonString(
            LABORATORY_REPORT_BUNDLE_DV2_WITH_SUBMITTER);

    // THEN we DON'T add any laboratory report details to the lifecycle page
    String pdfText = extractPdfText(pdfData);
    pdfText = skipToLifecyclePage(pdfText);
    assertThat(pdfText)
        .doesNotContain("Meldetatbestand")
        .doesNotContain("Probenentnahme")
        .doesNotContain("Probenmaterial");
  }

  private void validateLaboratoryReportPdfText(String pdfText) {
    assertThat(pdfText)
        .containsAnyOf(
            "Empfangsbestätigung",
            "Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet",
            "Meldevorgangs-ID a5e00874-bb26-45ac-8eea-0bde76456703",
            "Meldungsidentifier e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9");
  }

  /**
   * @return the text past the headline of the lifecycle management page (excluding the headline).
   */
  private String skipToLifecyclePage(final String pdfText) {
    // It seems that occasionally special characters are inserted in the PDF so we can't just do
    // pdfText.indexOf()
    // and instead have to rely on a regex.
    final Matcher matcher = LIFECYCLE_HEADLINE_PATTERN.matcher(pdfText);
    if (!matcher.find()) {
      throw new IllegalStateException(
          "Can't find lifecycle page in pdf text. Check the PDF text or regex pattern");
    }
    final int lifecycleManagementOffset = matcher.end();
    assert lifecycleManagementOffset > 0;
    return pdfText.substring(lifecycleManagementOffset);
  }
}
