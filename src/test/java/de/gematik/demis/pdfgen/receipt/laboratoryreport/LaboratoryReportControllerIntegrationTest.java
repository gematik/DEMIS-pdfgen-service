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
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.WireMockFuts;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.laboratory.LaboratoryFeature;
import de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
    properties = "server.port=10103",
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySources({
  @TestPropertySource(
      locations = "classpath:application-test.properties",
      properties =
          "demis.network.fhir-ui-data-model-translation.address=http://localhost:${wiremock.server.port}"),
})
class LaboratoryReportControllerIntegrationTest {

  private static final String LABORATORY_REPORT_PATH = "/laboratoryReport";
  private static final int QR_CODE_WIDTH = 295;

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void configureWireMockFuts() {
    new WireMockFuts().setDefaults().add(new LaboratoryFeature());
  }

  @Test
  void generatePdfFromDv2BundleJsonString_shouldRespond200WithPdf() throws Exception {
    final MockHttpServletResponse response =
        generateLaboratoryPdf(LABORATORY_REPORT_BUNDLE_DV2_JSON);

    validateOkResponse(response, "Empfangsbestätigung Labormeldung - Maxime Mustermann.pdf");
    validateLabReportDv2Body(response, getReportDv2PdfText(""));
  }

  @Test
  @DisplayName("valid laboratory notification without birthday should not lead to an exception")
  void givenLaboratoryNotificationWithoutBirthdayThen200() throws Exception {
    final MockHttpServletResponse response =
        generateLaboratoryPdf(LABORATORY_NOTIFICATION_WITH_MISSING_BIRTHDAY);

    var content = response.getContentAsByteArray();
    assertThat(content).isNotEmpty().isNotNull();
  }

  @Test
  @DisplayName("valid laboratory notification without specimen status should respond 200 with pdf")
  void givenLaboratoryNotificationWithoutSpecimenStatus_shouldRespond200WithPdf() throws Exception {
    final MockHttpServletResponse response =
        generateLaboratoryPdf(LABORATORY_NOTIFICATION_WITHOUT_SPECIMEN_STATUS);

    validateOkResponse(response, "Empfangsbestätigung Labormeldung - Maxime Mustermann.pdf");
    String expected = getReportDv2PdfText("").replace("Probenmaterialstatus Verfügbar\n", "");
    validateLabReportDv2Body(response, expected);
  }

  private static Stream<Arguments> inputValuesProvenanceOzg() {
    return Stream.of(
        Arguments.of("BundID", LABORATORY_REPORT_BUNDLE_DV2_WITH_PROVENANCE_BUNDID),
        Arguments.of("MeinUnternehmenskonto", LABORATORY_REPORT_BUNDLE_DV2_WITH_PROVENANCE_MUK));
  }

  @ParameterizedTest
  @MethodSource("inputValuesProvenanceOzg")
  @DisplayName("valid laboratory notification with provenance should respond 200 with pdf")
  void generatePdfFromDv2BundleWithProvenanceJsonString_shouldRespond200WithPdf(
      String authenticationMethod, String pathToInputBundle) throws Exception {
    String expectedProvenance =
        """
      Authentifizierung
      Meldeweg Portal
      Authentifizierungsmethode {authenticationMethod}
      Vertrauensniveau substanziell
      """;
    expectedProvenance = expectedProvenance.replace("{authenticationMethod}", authenticationMethod);
    final MockHttpServletResponse response = generateLaboratoryPdf(pathToInputBundle);

    validateOkResponse(response, "Empfangsbestätigung Labormeldung - Maxime Mustermann.pdf");
    validateLabReportDv2Body(response, getReportDv2PdfText(expectedProvenance));
  }

  private @NotNull MockHttpServletResponse generateLaboratoryPdf(String bundle) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    final MockHttpServletResponse response =
        this.mockMvc
            .perform(post(LABORATORY_REPORT_PATH).content(bundle).headers(headers))
            .andReturn()
            .getResponse();
    Assertions.assertThat(response).isNotNull();
    return response;
  }

  private void validateOkResponse(MockHttpServletResponse response, String expectedTitle)
      throws Exception {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotNull();
    assertThat(response.getHeader("Content-Disposition")).contains("attachment");
    assertThat(response.getHeader("Content-Disposition")).doesNotContain("filename");
    assertThat(response.getHeader("Content-Disposition")).doesNotContain(expectedTitle);
    assertThat(response.getHeader("Last-Modified")).isNotNull();
    assertThat(response.getHeader("Content-Type")).isEqualTo("application/pdf");
    assertThat(response.getHeader("Content-Length")).isNotNull();
  }

  String getReportDv2PdfText(String expectedProvenance) {
    return """
    Empfangsbestätigung Labormeldung
    Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet.\s
    Ggf. wird man von dort Kontakt mit Ihnen aufnehmen, um weitere Daten zu ermitteln. Bitte\s
    speichern Sie die Meldungsquittung datenschutzrechtlich sicher ab, da diese personenbezogene\s
    Daten enthält.
    Meldevorgangs-ID a5e00874-bb26-45ac-8eea-0bde76456703
    Zeitpunkt des Eingangs 24.10.2023 09:06
    Meldungs-ID e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9
    Meldungsstatus Final
    Meldungserstellung/-änderung 04.03.2021 20:16
    Meldungsverweis (Primärlabor) ABC123
    Meldungsempfänger
    Name Bezirksamt Lichtenberg von Berlin | Gesundheitsamt
    Adresse Alfred-Kowalke-Straße 24, 10360 Berlin
    Kontakt Telefon: +49 30 90 296-7552
    Fax: +49 30 90 296-7553
    E-Mail: hygiene@lichtenberg.berlin.de
    Meldende Person
    Name Dr Adam Careful
    Adresse 534 Erewhon St, 3999- PleasantVille
    Kontakt Telefon: 030 1234 (Dienstlich)
    Meldende Einrichtung
    Name Primärlabor (Erregerdiagnostische Untersuchungsstelle)
    Identifier BSNR: 98765430
    DEMIS-Id: 13589
    Adresse Dingsweg 321, 13055 Berlin, Deutschland
    Kontakt Telefon: 0309876543210 (Dienstlich)
    Betroffene Person
    Name Maxime Mustermann
    Geschlecht Weiblich
    Geburtsdatum 11.02.1950
    Adresse (Hauptwohnsitz) Teststrasse 123, 13055 Berlin, Deutschland
    Adresse (Derzeitiger Aufenthaltsort) Friedrichstraße 987, 66666 Berlin, Deutschland
    Kontakt Telefon: 030 555 (Privat)
    Einsendende Person
    Name Dr. Dr. Otto Muster
    Adresse Strasse 123, 33445 Stuttgart
    Kontakt Fax: 030 1111 (Dienstlich)
    Laborbericht
    Erstellungs-/Bearbeitungszeitpunkt 04.03.2021 20:15
    Berichtsstatus Final
    Meldetatbestand SARS-CoV-2
    Ergebnis (Zusammenfassung) Meldepflichtiger Erreger nachgewiesen
    Erläuterung Ich bin die textuelle Conclusion ...
    Auftragsnummer (E2E-Referenz) 2021-000672922
    Test - SARS-CoV-2 (COVID-19) RNA [Presence] in Serum or Plasma by NAA with probe detection
    Teststatus Final
    Ergebniswert Detected
    Befund Positive
    Nachweismethode Nucleic acid assay (procedure)
    Erläuterung (Test) Nette Zusatzinformation …
    Probeneingang 04.03.2021
    Probenentnahme 04.03.2021
    Probenmaterialstatus Verfügbar
    Probenmaterial Upper respiratory swab sample (specimen)
    Erläuterung (Probe) Ich bin eine interessante Zusatzinformation ...
    Transaktions-Id (DESH) IMS-12345-CVDP-00001
    """
        + expectedProvenance
        + """
    Informationen zur Weitergabe der zugehörigen Probe an andere Labore\s
    entsprechend dem DEMIS Lifecyclemanagement
    Das DEMIS-Lifecyclemanagement von Meldungen beschreibt in verschiedenen Szenarien den Umgang mit Meldungen,\s
    inkl. Korrekturen und Ergänzungen (https://go.gematik.de/demis-lifecycle-lab). Ein Teil des Lifecyclemanagements sieht\s
    auch die weiterführende Untersuchung der Probe in einem Sekundärlabor vor. Dies ist z.B. in den Szenarien 2B und 3B\s
    beschrieben. Mit der Weitergabe der Probe und des Probenbegleitscheins muss auch die Meldungs-ID (NotificationID)\s
    der Erstmeldung an das Sekundärlabor weitergegeben werden. Diese finden Sie zur einfachen Weitergabe auf dieser\s
    Seite.
    Betroffene Person Meldungs-ID\s
    Geschlecht Weiblich\s
    Geburtsdatum (Monat/Jahr) 02/1950\s
    Ersten 3 Ziffern der PLZ der Adresse 130
    Meldende Einrichtung\s
    Name Primärlabor (Erregerdiagnostische Untersuchungsstelle)\s
    Adresse Dingsweg 321, 13055 Berlin, Deutschland\s
    Kontakt Telefon: 0309876543210 (Dienstlich)
    e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9
    Auftragsnummer (E2E-Referenz) 2021-000672922
    Meldetatbestand CVDP
    Probenentnahme 04.03.2021\s
    Probenmaterial Upper respiratory swab sample (specimen)
    Bitte ergänzen Sie den auf dem Probenbegleitschein genannten Labor-Identifikator für die Probe:""";
  }

  private void validateLabReportDv2Body(MockHttpServletResponse response, String expectedText)
      throws Exception {
    // note: pdfbox extracts the title as the last line for some reason
    validateLabReportPdfContent(response, cleanupString(expectedText));
  }

  private void validateLabReportPdfContent(MockHttpServletResponse response, String expectedText)
      throws IOException {
    byte[] pdfBytes = response.getContentAsByteArray();
    validatePdfText(pdfBytes, expectedText);
    validatePdfHasQrCode(pdfBytes);
    PdfExtractorHelper.verifyField(pdfBytes, "lab-id", 2);
  }

  private void validatePdfText(byte[] pdfBytes, String expectedText) throws IOException {
    String pdfText = PdfExtractorHelper.extractPdfText(pdfBytes);
    assertThat(pdfText).isEqualToIgnoringCase(expectedText);
  }

  private void validatePdfHasQrCode(byte[] pdfBytes) {
    try {
      List<RenderedImage> imagesFromPdf = PdfExtractorHelper.getAllImagesFromPdfData(pdfBytes);
      assertThat(imagesFromPdf).as("logo and repeated QR code").hasSizeGreaterThanOrEqualTo(3);
      List<RenderedImage> qrCodes =
          imagesFromPdf.stream()
              .filter(LaboratoryReportControllerIntegrationTest::isQsCode)
              .toList();
      assertThat(qrCodes).as("repeated QR code").hasSize(2);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean isQsCode(RenderedImage image) {
    return image.getWidth() == QR_CODE_WIDTH && image.getHeight() == QR_CODE_WIDTH;
  }

  private String cleanupString(final String input) {
    return input.replaceAll("\r\n", "\n").replaceAll("^\\s+", "").replaceAll("\\s+$", "");
  }
}
