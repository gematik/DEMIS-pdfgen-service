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

package de.gematik.demis.pdfgen.receipt.diseasenotification;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_WITH_MISSING_POSTALCODE_BUNDLE_XML;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_WITH_PARTIAL_POSTALCODE_BUNDLE_XML;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_WITH_PROVENANCE_BUNDID_BUNDLE_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.WireMockFuts;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.disease.DiseaseFeature;
import de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper;
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
    properties = "server.port=10102",
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySources({
  @TestPropertySource(
      locations = "classpath:application-test.properties",
      properties =
          "demis.network.fhir-ui-data-model-translation-address=http://localhost:${wiremock.server.port}"),
})
class DiseaseNotificationControllerIntegrationTest {

  private static final String DISEASE_NOTIFICATION_PATH = "/diseaseNotification";

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void configureWireMockFuts() {
    new WireMockFuts().setDefaults().add(new DiseaseFeature());
  }

  @Test
  void generatePdfFromBundleJsonString_shouldRespond200WithPdf_DiseaseNotification()
      throws Exception {

    final var response =
        generateAndValidateNotification(
            DISEASE_NOTIFICATION_BUNDLE_JSON, MediaType.APPLICATION_JSON_VALUE);

    validateOkResponseDiseaseNotification(
        response,
        "Empfangsbestätigung Erkrankungsmeldung - Bertha-Luise Hanna Karin Betroffen.pdf");
    validateBodyDiseaseResponse(response, "");
  }

  @Test
  void
      generatePdfFromBundleXmlString_shouldRespond200WithPdf_DiseaseNotificationWithProvenanceBundId()
          throws Exception {
    final String expectedProvenance =
        """
        Authentifizierung
        Meldeweg Schnittstelle
        Authentifizierungsmethode DEMIS-Zertifikat
        Vertrauensniveau substanziell
        """;

    final var response =
        generateAndValidateNotification(
            DISEASE_NOTIFICATION_WITH_PROVENANCE_BUNDID_BUNDLE_JSON,
            MediaType.APPLICATION_JSON_VALUE);

    validateOkResponseDiseaseNotification(
        response,
        "Empfangsbestätigung Erkrankungsmeldung - Bertha-Luise Hanna Karin Betroffen.pdf");
    validateBodyDiseaseResponse(response, expectedProvenance);
  }

  @Test
  void generatePdfFromBundleXmlStringWithMissingPostalCode() throws Exception {
    generateAndValidateNotification(
        DISEASE_NOTIFICATION_WITH_MISSING_POSTALCODE_BUNDLE_XML, MediaType.APPLICATION_XML_VALUE);
  }

  @Test
  void generatePdfFromBundleXmlStringWithPartialPostalCode() throws Exception {

    generateAndValidateNotification(
        DISEASE_NOTIFICATION_WITH_PARTIAL_POSTALCODE_BUNDLE_XML, MediaType.APPLICATION_XML_VALUE);
  }

  private MockHttpServletResponse generateAndValidateNotification(
      final String bundle, final String mediaType) throws Exception {
    // given
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", mediaType);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(DiseaseNotificationControllerIntegrationTest.DISEASE_NOTIFICATION_PATH)
                    .content(bundle)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getContentAsString()).isNotNull();
    assertThat(response.getHeader("Last-Modified")).isNotNull();
    assertThat(response.getHeader("Content-Type")).isEqualTo("application/pdf");
    assertThat(response.getHeader("Content-Length")).isNotNull();

    return response;
  }

  private void validateOkResponseDiseaseNotification(
      final MockHttpServletResponse response, final String expectedTitle) throws Exception {
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

  private void validateBodyDiseaseResponse(
      final MockHttpServletResponse response, final String provenance) throws Exception {
    final String expectedText =
        """
        Empfangsbestätigung\s
        Erkrankungsmeldung
        Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet. Ggf. wird man von dort\s
        Kontakt mit Ihnen aufnehmen, um weitere Daten zu ermitteln. Bitte speichern Sie die Meldungsquittung\s
        datenschutzrechtlich sicher ab, da diese personenbezogene Daten enthält.
        Weiterführende Informationen zur Meldung gemäß §6 IfSG finden Sie in der DEMIS-Wissensdatenbank
        Meldevorgangs-ID 2d66a331-102a-4047-b666-1b2f18ee955e
        Zeitpunkt des Eingangs 11.03.2022 09:06
        Meldungs-ID 7f562b87-f2c2-4e9d-b3fc-37f6b5dca3a5
        Meldungsstatus Final
        Meldungserstellung/-änderung 10.03.2022 14:57
        Meldungsverweis (Primärlabor) ABC123
        Meldungsempfänger
        Name Kreis Herzogtum Lauenburg | Gesundheitsamt
        Adresse Barlachstr. 4, 23909 Ratzeburg
        Kontakt Telefon: +49 4541 888-380
        Fax: +49 4541 888-259
        E-Mail: gesundheitsdienste@kreis-rz.de
        Meldende Einrichtung
        Name SlowHealing Klinik (Krankenhaus)
        Identifier BSNR: 123456789
        Adresse Krankenhausstraße 1, 21481 Buchhorst, Deutschland
        Kontakt Telefon: 01234567
        E-Mail: anna@ansprechpartner.de
        Kontaktperson Dr. Anna Beate Carolin Ansprechpartner
        Betroffene Person
        Name Bertha-Luise Hanna Karin Betroffen
        Geschlecht Weiblich
        Geburtsdatum 09.06.1999
        Adresse (Hauptwohnsitz) Berthastraße 123, 12345 Betroffenenstadt, Deutschland
        Einrichtungsadresse (Derzeitiger QuickHealing Hospital (Krankenhaus)
        Aufenthaltsort) Krankenhausstraße 1, 21481 Buchhorst, Deutschland
        Telefon: 01234567
        E-Mail: anna@ansprechpartner.de
        Kontaktperson: Dr. Anna Beate Carolin Ansprechpartner
        Kontakt Telefon: 01234567
        E-Mail: bertha@betroffen.de
        Erkrankung
        Verifikationsstatus der Diagnose Bestätigt
        Klinischer Status Aktiv
        Meldetatbestand Coronavirus-Krankheit-2019 (COVID-19)
        Erkrankungsbeginn 01.01.2022
        Datum der Diagnosestellung 02.01.2022
        Symptome Fieber
        Halsschmerzen/-entzündung
        Husten
        Pneunomie (Lungenentzündung)
        Schnupfen
        akutes schweres Atemnotsyndrom (ARDS)
        Diagnosehinweise Textueller Hinweis
        Meldetatbestandsübergreifende klinische und epidemiologische Angaben
        Status http://hl7.org/fhir/questionnaire-answers-status: completed
        Verstorben Ja
        Datum des Todes 22.01.2022
        Zugehörigkeit zur Bundeswehr Soldat/BW-Angehöriger
        Laborbeauftragung Ja
        Beauftragtes Labor
        Name QuickScan Labor (Erregerdiagnostische Untersuchungsstelle)
        Adresse Laborstraße 345, 21481 Buchhorst, Deutschland
        Kontakt Telefon: 666555444
        E-Mail: mail@labor.de
        Kontaktperson Herr Laslo Labora
        Aufnahme in ein Krankenhaus Ja
        Krankenhaus 1
        Name QuickHealing Hospital (Krankenhaus)
        Einrichtungsadresse Krankenhausstraße 1, 21481 Buchhorst, Deutschland
        Telefon: 01234567
        E-Mail: anna@ansprechpartner.de
        Kontaktperson: Dr. Anna Beate Carolin Ansprechpartner
        Beginn 05.01.2022
        Ende 09.01.2022
        Hinweise zur Hospitalisierung wichtige Zusatzinformation
        Krankenhaus 2
        Name QuickHealing Hospital (Krankenhaus)
        Station Intensivmedizin
        Einrichtungsadresse Krankenhausstraße 1, 21481 Buchhorst, Deutschland
        Telefon: 01234567
        E-Mail: anna@ansprechpartner.de
        Kontaktperson: Dr. Anna Beate Carolin Ansprechpartner
        Beginn 07.01.2022
        Tätigkeit, Betreuung oder Unterbringung in Ja
        Einrichtungen mit Relevanz für den\s
        Infektionsschutz (siehe § 23 Abs. 3 IfSG, §35\s
        Abs. 1 IfSG oder §36 Abs. 1 IfSG)
        Beginn der Tätigkeit/Betreuung/Unterbringung 01.12.2021
        Ende der Tätigkeit/Betreuung/Unterbringung 05.01.2022
        Rolle Tätigkeit
        Einrichtungsadresse Einrichtungsname
        Straße 123, 21481 Buchhorst, Deutschland
        Telefon: 0123456789
        E-Mail: mail@einrichtung.de
        Wahrscheinlicher Expositionsort bekannt Ja
        Beginn des Aufenthalts am wahrscheinlichen 20.12.2021
        Expositionsort/Datum des Aufenthalts
        Ende des Aufenthalts am wahrscheinlichen 28.12.2021
        Expositionsort
        Wahrscheinlicher Expositionsort Libyen
        Anmerkungen zum Expositonsort Anmerkung
        Spender für eine Blut-, Organ-, Gewebe- oder Ja
        Zellspende in den letzten 6 Monaten
        Wichtige Zusatzinformationen Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und\s
        epidemiologischen Angaben
        Covid-19-spezifische klinische und epidemiologische Angaben
        Status http://hl7.org/fhir/questionnaire-answers-status: completed
        Kontakt zu bestätigtem Fall Ja
        Infektionsumfeld vorhanden Ja
        Wahrscheinliches Infektionsumfeld Gesundheitseinrichtung
        Beginn Infektionsumfeld 28.12.2021
        Ende Infektionsumfeld 30.12.2021
        Wurde die betroffene Person jemals in Bezug Ja
        auf die Krankheit geimpft?
        Impfinformationen 1
        Impfstoff Comirnaty
        Datum der Impfung 07.2021
        Hinweis zur Impfung Zusatzinfo2
        Impfinformationen 2
        Impfstoff nicht ermittelbar
        Datum der Impfung 30.11.2021
        Hinweis zur Impfung Zusatzinfo3
        Impfinformationen 3
        Impfstoff Anderer Impfstoff
        Datum der Impfung 25.12.2021
        Hinweis zur Impfung Zusatzinfo4
        Impfinformationen 4
        Impfstoff Comirnaty
        Datum der Impfung 2021
        Hinweis zur Impfung Zusatzinfo1
        Welche Risikofaktoren liegen bei der\s
        betroffenen Person vor? Diabetes Typ 1
        Lebererkrankung
        Schwangerschaft
        Trimester
        2. Trimester
        """
            + provenance;
    final String pdfText = PdfExtractorHelper.extractPdfText(response.getContentAsByteArray());
    assertThat(pdfText).as("disease notification PDF text").isEqualTo(cleanupString(expectedText));
  }

  private String cleanupString(final String input) {
    return input.replaceAll("\r\n", "\n").replaceAll("^\\s+", "").replaceAll("\\s+$", "");
  }
}
