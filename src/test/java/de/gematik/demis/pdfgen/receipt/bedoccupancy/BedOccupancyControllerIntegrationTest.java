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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.BED_OCCUPANCY_BUNDLE_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.BED_OCCUPANCY_WITH_INVALID_PROVENANCE_BUNDLE_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.BED_OCCUPANCY_WITH_PROVENANCE_BUNDLE_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.WireMockFuts;
import de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@SpringBootTest(
    properties = "server.port=10101",
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySources({
  @TestPropertySource(
      locations = "classpath:application-test.properties",
      properties =
          "demis.network.fhir-ui-data-model-translation-address=http://localhost:${wiremock.server.port}"),
})
class BedOccupancyControllerIntegrationTest {

  private static final String BED_OCCUPANCY_PATH = "/bedOccupancy";

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void configureWireMockFuts() {
    new WireMockFuts().setDefaults();
  }

  @Test
  void createBedOccupancyPdfFromJson_shouldRespond200WithPdf() throws Exception {
    createAndValidateBedOccupancyPdf(BED_OCCUPANCY_BUNDLE_JSON, "");
  }

  @Test
  void createBedOccupancyWithProvenancePdfFromJson_shouldRespond200WithPdf() throws Exception {
    String expectedProvenance =
        """

    Authentifizierung
    Meldeweg Portal
    Authentifizierungsmethode Authenticator
    Vertrauensniveau substanziell""";

    createAndValidateBedOccupancyPdf(BED_OCCUPANCY_WITH_PROVENANCE_BUNDLE_JSON, expectedProvenance);
  }

  @Test
  void createBedOccupancyWithInvalidProvenancePdfFromJson_shouldRespond200WithPdf()
      throws Exception {
    String expectedProvenance =
        """

    Authentifizierung
    Meldeweg Unbekannt
    Authentifizierungsmethode Unbekannt
    Vertrauensniveau Unbekannt""";

    createAndValidateBedOccupancyPdf(
        BED_OCCUPANCY_WITH_INVALID_PROVENANCE_BUNDLE_JSON, expectedProvenance);
  }

  private void createAndValidateBedOccupancyPdf(String bundleJson, String provenance)
      throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(post(BED_OCCUPANCY_PATH).content(bundleJson).headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponse(response);
    validatePdfText(response, provenance);
  }

  private void validateOkResponse(MockHttpServletResponse response) throws Exception {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotNull();
    assertThat(response.getHeader("Content-Disposition")).contains("attachment");
    assertThat(response.getHeader("Content-Disposition")).doesNotContain("filename");
    assertThat(response.getHeader("Content-Disposition"))
        .doesNotContain(
            "Empfangsbestätigung Bettenbelegung - Sankt Gertrauden-Krankenhaus GmbH.pdf");
    assertThat(response.getHeader("Last-Modified")).isNotNull();
    assertThat(response.getHeader("Content-Type")).isEqualTo("application/pdf");
    assertThat(response.getHeader("Content-Length")).isNotNull();
  }

  private void validatePdfText(MockHttpServletResponse response, String provenance)
      throws IOException {
    String pdfText = PdfExtractorHelper.extractPdfText(response.getContentAsByteArray());
    String expectedText =
        """
            Empfangsbestätigung Bettenbelegung
            Meldevorgangs-ID cfcd2084-95d5-35ef-a6e7-dff9f98764da
            Zeitpunkt des Eingangs 21.11.2021 09:06
            Meldungs-ID 5e1e89ce-7a44-4ec1-801c-0f988992e8fe
            Meldende Einrichtung
            Name Sankt Gertrauden-Krankenhaus GmbH (Krankenhaus)
            Identifier 5e1e89ce-7a44-4ec1-801c-0f988992e8ff
            Adresse Paretzer Straße 12, 10713 Berlin, Deutschland
            Kontakt Telefon: 309876543210 (Dienstlich)
            Belegte Betten auf den Normalstationen des meldenden Standortes
            Erwachsene 221
            Kinder 37
            Betreibbare Betten auf den Normalstationen des meldenden Standortes
            Erwachsene 250
            Kinder 50"""
            + provenance;
    assertThat(pdfText).as("bed occupancy report PDF text").isEqualTo(expectedText);
  }
}
