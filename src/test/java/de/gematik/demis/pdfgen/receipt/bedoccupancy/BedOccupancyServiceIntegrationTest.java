package de.gematik.demis.pdfgen.receipt.bedoccupancy;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.BED_OCCUPANCY_BUNDLE_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.BED_OCCUPANCY_BUNDLE_XML;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.BED_OCCUPANCY_WITH_PROVENANCE_BUNDLE_JSON;
import static de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper.extractPdfText;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.pdf.PdfData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BedOccupancyServiceIntegrationTest {

  @Autowired private BedOccupancyService bedOccupancyService;

  @Test
  void createBedOccupancyPdfFromJson_shouldCreateBinaryPdfFromJsonFhirBundle() throws Exception {
    // when
    PdfData pdfData = bedOccupancyService.createBedOccupancyPdfFromJson(BED_OCCUPANCY_BUNDLE_JSON);

    // then
    assertThat(pdfData.bytes()).isNotNull();
    String pdfText = extractPdfText(pdfData);
    validateBedOccupancyPdfText(pdfText);
  }

  @Test
  void createBedOccupancyPdfFromXml_shouldCreateBinaryPdfFromXmlFhirBundle() throws Exception {
    // when
    PdfData pdfData = bedOccupancyService.createBedOccupancyPdfFromXml(BED_OCCUPANCY_BUNDLE_XML);

    // then
    assertThat(pdfData.bytes()).isNotNull();
    String pdfText = extractPdfText(pdfData);
    validateBedOccupancyPdfText(pdfText);
  }

  @Test
  void createBedOccupancyWithProvenancePdfFromJson_shouldCreateBinaryPdfFromJsonFhirBundle()
      throws Exception {
    // when
    PdfData pdfData =
        bedOccupancyService.createBedOccupancyPdfFromJson(
            BED_OCCUPANCY_WITH_PROVENANCE_BUNDLE_JSON);

    // then
    assertThat(pdfData.bytes()).isNotNull();
    String pdfText = extractPdfText(pdfData);
    validateBedOccupancyPdfText(pdfText);
    validateProvenance(pdfText);
  }

  private void validateBedOccupancyPdfText(String pdfText) {
    assertThat(pdfText)
        .containsAnyOf(
            "Empfangsbestätigung Bettenbelegung",
            "Meldevorgangs-ID",
            "cfcd2084-95d5-35ef-a6e7-dff9f98764da",
            "Meldungs-ID",
            "5e1e89ce-7a44-4ec1-801c-0f988992e8fe",
            "Zeitpunkt des Meldevorgangs",
            "Sankt Gertrauden-Krankenhaus GmbH");
  }

  private void validateProvenance(String pdfText) {
    assertThat(pdfText)
        .containsAnyOf(
            "Meldeweg", "Portal",
            "Authentifizierungsmethode", "Authenticator",
            "Vertrauensniveau", "substanziell");
  }
}
