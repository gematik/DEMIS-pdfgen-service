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

package de.gematik.demis.pdfgen.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.*;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.*;
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
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySources({
  @TestPropertySource(locations = "classpath:application-test.properties"),
})
@Disabled(
    value =
        """
      The PDF Text Parsing works differently in every operating system due to interpretation of Fonts and breaks
      in a document. Tests should be refactored in a way that the checks are one based on Substring comparisons
      (search for particular substrings), a whole full-text comparison won't work unless a change
      in the PDF Layout (including standard Fonts) isn't done.
      """)
public class GlobalIntegrationTest {

  private static final String BED_OCCUPANCY_PATH = "/bedOccupancy";

  private static final WireMockServer translationServer = new WireMockServer(7070);
  private static final String DISEASE_NOTIFICATION_PATH = "/diseaseNotification";
  private static final String LABORATORY_REPORT_PATH = "/laboratoryReport";

  @Autowired private MockMvc mockMvc;

  @BeforeAll
  public static void startServers() {
    translationServer.start();

    configureFor(translationServer.port());
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/country"))
            .withQueryParam("code", WireMock.equalTo("20422"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                        {
                                                                            "code": "20422",
                                                                            "display": "Deutschland",
                                                                            "designations": []
                                                                        }""")));
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/ValueSet"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/organizationType"))
            .withQueryParam("code", WireMock.equalTo("hospital"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                        {
                                                                            "code": "hospital",
                                                                            "display": "Krankenhaus",
                                                                            "designations": []
                                                                        }""")));
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/translationEvidence"))
            .withQueryParam("code", WireMock.equalTo("386661006"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                     {
                                                                         "code": "386661006",
                                                                         "display": "Fever (finding)",
                                                                         "designations": [
                                                                             {
                                                                                 "language": "de",
                                                                                 "value": "Fieber"
                                                                             }
                                                                         ]
                                                                     }""")));
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/translationEvidence"))
            .withQueryParam("code", WireMock.equalTo("267102003"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                                   "code": "267102003",
                                                                                                   "display": "Sore throat symptom (finding)",
                                                                                                   "designations": [
                                                                                                       {
                                                                                                           "language": "de",
                                                                                                           "value": "Halsschmerzen/-entzündung"
                                                                                                       }
                                                                                                   ]
                                                                                            }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/translationEvidence"))
            .withQueryParam("code", WireMock.equalTo("49727002"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                                "code": "49727002",
                                                                                                "display": "Cough (finding)",
                                                                                                "designations": [
                                                                                                    {
                                                                                                        "language": "de",
                                                                                                        "value": "Husten"
                                                                                                    }
                                                                                                ]
                                                                                            }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/translationEvidence"))
            .withQueryParam("code", WireMock.equalTo("233604007"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                                 "code": "233604007",
                                                                                                 "display": "Pneumonia (disorder)",
                                                                                                 "designations": [
                                                                                                     {
                                                                                                         "language": "de",
                                                                                                         "value": "Pneunomie (Lungenentzündung)"
                                                                                                     }
                                                                                                 ]
                                                                                             }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/translationEvidence"))
            .withQueryParam("code", WireMock.equalTo("275280004"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                                 "code": "275280004",
                                                                                                 "display": "Sniffles (finding)",
                                                                                                 "designations": [
                                                                                                     {
                                                                                                         "language": "de",
                                                                                                         "value": "Schnupfen"
                                                                                                     }
                                                                                                 ]
                                                                                             }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/translationEvidence"))
            .withQueryParam("code", WireMock.equalTo("67782005"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                                 "code": "67782005",
                                                                                                 "display": "Acute respiratory distress syndrome (disorder)",
                                                                                                 "designations": [
                                                                                                     {
                                                                                                         "language": "de",
                                                                                                         "value": "akutes schweres Atemnotsyndrom (ARDS)"
                                                                                                     }
                                                                                                 ]
                                                                                             }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer"))
            .withQueryParam("code", WireMock.equalTo("yes"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                           {
                                                                                                 "code": "yes",
                                                                                                 "display": "Ja",
                                                                                                 "designations": []
                                                                                             }""")));
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/militaryAffiliation"))
            .withQueryParam("code", WireMock.equalTo("memberOfBundeswehr"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                           {
                                                                                               "code": "memberOfBundeswehr",
                                                                                               "display": "Soldat/BW-Angehöriger",
                                                                                               "designations": []
                                                                                           }""")));
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/ValueSet"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/ValueSet/vaccineCVDD"))
            .withQueryParam("code", WireMock.equalTo("EU/1/20/1528"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                           {
                                                                                               "code": "EU/1/20/1528",
                                                                                               "display": "Comirnaty"
                                                                                           }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system", WireMock.equalTo("http://terminology.hl7.org/CodeSystem/v3-NullFlavor"))
            .withQueryParam("code", WireMock.equalTo("ASKU"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                                               {
                                                "code" : "ASKU",
                                                "display" : "asked but unknown",
                                                "designations" : [ {
                                                  "language" : "de",
                                                  "value" : "nicht ermittelbar"
                                                } ]
                                              }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/ValueSet"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/ValueSet/vaccineCVDD"))
            .withQueryParam("code", WireMock.equalTo("otherVaccine"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                              {
                                                                                                  "code": "otherVaccine",
                                                                                                  "display": "Anderer Impfstoff"
                                                                                              }""")));

    configureFor(translationServer.port());
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/country"))
            .withQueryParam("code", WireMock.equalTo("20422"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                                "code": "20422",
                                                                                                "display": "Deutschland",
                                                                                                "designations": []
                                                                                            }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam("system", WireMock.equalTo("organizationType"))
            .withQueryParam("code", WireMock.equalTo("laboratory"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                               "code" : "laboratory",
                                                                                               "display" : "Erregerdiagnostische Untersuchungsstelle",
                                                                                               "designations" : [ {
                                                                                                 "language" : "en",
                                                                                                 "value" : "Laboratory"
                                                                                               }, {
                                                                                                 "language" : "de",
                                                                                                 "value" : "Erregerdiagnostische Untersuchungsstelle"
                                                                                               } ]
                                                                                             }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/addressUse"))
            .withQueryParam("code", WireMock.equalTo("primary"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                  {
                                                      "code" : "primary",
                                                      "display" : "Hauptwohnsitz",
                                                      "designations" : [ {
                                                        "language" : "en",
                                                        "value" : "Primary Residence"
                                                      } ]
                                                    }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/addressUse"))
            .withQueryParam("code", WireMock.equalTo("current"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                   {
                                                        "code" : "current",
                                                        "display" : "Derzeitiger Aufenthaltsort",
                                                        "designations" : [ {
                                                          "language" : "en",
                                                          "value" : "Current Residence"
                                                        } ]
                                                      }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/ValueSet"))
            .withQueryParam("system", WireMock.equalTo("LaboratoryTestSARSCoV2"))
            .withQueryParam("code", WireMock.equalTo("94660-8"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                                                                    {
                                                                                                                                         "code" : "94660-8",
                                                                                                                                         "display" : "SARS-CoV-2 (COVID-19) RNA [Presence] in Serum or Plasma by NAA with probe detection"
                                                                                                                                       }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system",
                WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType"))
            .withQueryParam("code", WireMock.equalTo("3600"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                                                                   {
                                                                    "code" : "3600",
                                                                    "display" : "intensive care",
                                                                    "designations" : [ {
                                                                      "language" : "de",
                                                                      "value" : "ITS - Intensivstation"
                                                                    } ]
                                                                  }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam(
                "system", WireMock.equalTo("https://demis.rki.de/fhir/CodeSystem/organizationType"))
            .withQueryParam("code", WireMock.equalTo("hospital"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                                                                                       {
                                                                                        "code" : "hospital",
                                                                                        "display" : "hospital",
                                                                                        "designations" : [ {
                                                                                          "language" : "de",
                                                                                          "value" : "Krankenhaus3"
                                                                                        } ]
                                                                                      }""")));
  }

  @AfterAll
  public static void stopServers() {
    translationServer.stop();
  }

  private static void validatePdfText(byte[] pdfBytes, String expectedText) throws IOException {
    String pdfText = PdfExtractorHelper.extractPdfText(pdfBytes);
    assertThat(pdfText).isEqualToIgnoringCase(expectedText);
  }

  @Test
  void createBedOccupancyPdfFromJson_shouldRespond200WithPdf() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(post(BED_OCCUPANCY_PATH).content(BED_OCCUPANCY_BUNDLE_JSON).headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponse(response);
    validatePDFContent(response);
  }

  @Test
  void createBedOccupancyPdfFromJson_shouldRespond422WhenContentTypeIsWrong() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(post(BED_OCCUPANCY_PATH).content(BED_OCCUPANCY_BUNDLE_XML).headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateUnprocessableResponse(response);
  }

  @Test
  void createBedOccupancyPdfFromXml_shouldRespond200WithPdf() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_XML_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(post(BED_OCCUPANCY_PATH).content(BED_OCCUPANCY_BUNDLE_XML).headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponse(response);
    validatePDFContent(response);
  }

  @Test
  void createBedOccupancyPdfFromXml_shouldRespond422WhenContentTypeIsWrong() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_XML_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(post(BED_OCCUPANCY_PATH).content(BED_OCCUPANCY_BUNDLE_JSON).headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateUnprocessableResponseBedOccupancy(response);
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

  private void validatePDFContent(MockHttpServletResponse response) throws IOException {
    String expectedText =
        """
Empfangsbestätigung Bettenbelegung
Meldevorgang cfcd2084-95d5-35ef-a6e7-dff9f98764da
Meldungserstellung/-änderung 20.11.2021 17:50
Meldende Einrichtung
Name Sankt Gertrauden-Krankenhaus GmbH
Identifier 5e1e89ce-7a44-4ec1-801c-0f988992e8ff
Adresse Paretzer Straße 12, 10713 Berlin, Deutschland
Kontakt Telefon: 309876543210 (Dienstlich)
Belegte Betten auf den Normalstationen des meldenden Standortes
Erwachsene 221
Kinder 37
Betreibbare Betten auf den Normalstationen des meldenden Standortes
Erwachsene 250
Kinder 50""";

    String pdfText = PdfExtractorHelper.extractPdfText(response.getContentAsByteArray());
    assertThat(pdfText).isEqualTo(expectedText);
  }

  private void validateUnprocessableResponseBedOccupancy(MockHttpServletResponse response) {
    assertThat(response)
        .isNotNull()
        .extracting("status")
        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  @Test
  void generatePdfFromBundleJsonString_shouldRespond200WithPdf_DiseaseNotification()
      throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(DISEASE_NOTIFICATION_PATH)
                    .content(DISEASE_NOTIFICATION_BUNDLE_JSON)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponseDiseaseNotification(
        response, "Empfangsbestätigung Arztmeldung - Bertha-Luise Hanna Karin Betroffen.pdf");
    validateBodyDiseaseResponse(response);
  }

  @Test
  void generatePdfFromBundleJsonString_shouldRespond422WhenContentTypeIsWrong_DiseaseNotification()
      throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_XML_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(DISEASE_NOTIFICATION_PATH)
                    .content(DISEASE_NOTIFICATION_BUNDLE_JSON)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateUnprocessableResponseDiseaseNotification(response);
  }

  @Test
  void generatePdfFromBundleXmlString_shouldRespond200WithPdf_DiseaseNotification()
      throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_XML_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(DISEASE_NOTIFICATION_PATH)
                    .content(DISEASE_NOTIFICATION_BUNDLE_XML)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponse(
        response, "Empfangsbestätigung Arztmeldung - Bertha-Luise Hanna Karin Betroffen.pdf");
    validateBodyDiseaseResponse(response);
  }

  @Test
  void generatePdfFromBundleXmlString_shouldRespond422WhenContentTypeIsWrong_DiseaseNotification()
      throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(DISEASE_NOTIFICATION_PATH)
                    .content(DISEASE_NOTIFICATION_BUNDLE_XML)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateUnprocessableResponse(response);
  }

  private void validateOkResponseDiseaseNotification(
      MockHttpServletResponse response, String expectedTitle) throws Exception {
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

  private void validateUnprocessableResponseDiseaseNotification(MockHttpServletResponse response) {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  private void validateBodyDiseaseResponse(MockHttpServletResponse response) throws Exception {
    String expectedText =
        """
Empfangsbestätigung Arztmeldung
Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet. Ggf.\s
wird man von dort Kontakt mit Ihnen aufnehmen, um weitere Daten zu ermitteln. Bitte speichern\s
Sie die Meldungsquittung datenschutzrechtlich sicher ab, da diese personenbezogene Daten enthält.
Meldevorgangs-ID 2d66a331-102a-4047-b666-1b2f18ee955e
Zeitpunkt des Eingangs 10.03.2022 14:57
Meldungsidentifier 7f562b87-f2c2-4e9d-b3fc-37f6b5dca3a5
Meldungsstatus Final
Meldungserstellung/-änderung 10.03.2022 14:57
Meldungsverweis (Primärlabor) appends ABC123
Meldungsempfänger
Name Kreis Herzogtum Lauenburg | Gesundheitsamt
Adresse Barlachstr. 4, 23909 Ratzeburg
Telefon: +49 4541 888-380 Fax: +49 4541 888-259 E-
Kontakt Mail: gesundheitsdienste@kreis-rz.de
Meldende Einrichtung
Name TEST Organisation (Krankenhaus3)
Identifier BSNR: 123456789
Adresse Krankenhausstraße 1, 21481 Buchhorst, Deutschland
Kontakt Telefon: 01234567 E-Mail: anna@ansprechpartner.de
Kontaktperson Dr. Anna Beate Carolin Ansprechpartner
Betroffene Person
Name Bertha-Luise Hanna Karin Betroffen
Geschlecht Weiblich
Geburtsdatum 09.06.1999
Adresse (Hauptwohnsitz) Berthastraße 123, 12345 Betroffenenstadt, Deutschland
TEST Organisation (Krankenhaus3)\s
Einrichtungsadresse (Derzeitiger Krankenhausstraße 1, 21481 Buchhorst, Deutschland\s
Telefon: 01234567 E-Mail: anna@ansprechpartner.de\s
Aufenthaltsort) Kontaktperson: Dr. Anna Beate Carolin\s
Ansprechpartner
Kontakt Telefon: 01234567 E-Mail: bertha@betroffen.de
Erkrankung
Erkrankungsbeginn 01.01.2022
Datum der Diagnosestellung 02.01.2022
Fieber Halsschmerzen/-entzündung Husten\s
Symptome Pneunomie (Lungenentzündung) Schnupfen akutes\s
schweres Atemnotsyndrom (ARDS)
Diagnosehinweise Textueller Hinweis
Meldetatbestandsübergreifende klinische und epidemiologische Angaben
Verstorben Ja
Datum des Todes 22.01.2022
Zugehörigkeit zur Bundeswehr Soldat/BW-Angehöriger
Laborbeauftragung Ja
Beauftragtes Labor
Name Labor
Adresse Laborstraße 345, 21481 Buchhorst, Deutschland
Kontakt Telefon: 666555444 E-Mail: mail@labor.de
Aufnahme in ein Krankenhaus Ja
Krankenhaus 1
Typ der Einrichtung Krankenhaus3
Krankenhausstraße 1, 21481 Buchhorst, Deutschland\s
Telefon: 01234567 E-Mail: anna@ansprechpartner.de\s
Einrichtungsadresse Kontaktperson: Dr. Anna Beate Carolin\s
Ansprechpartner
Beginn 05.01.2022
Ende 09.01.2022
Hinweise zur Hospitalisierung wichtige Zusatzinformation
Krankenhaus 2
Typ der Einrichtung Krankenhaus3
Station ITS - Intensivstation
Krankenhausstraße 1, 21481 Buchhorst, Deutschland\s
Telefon: 01234567 E-Mail: anna@ansprechpartner.de\s
Einrichtungsadresse Kontaktperson: Dr. Anna Beate Carolin\s
Ansprechpartner
Beginn 07.01.2022
Tätigkeit, Betreuung oder\s
Unterbringung in Einrichtungen mit\s
Relevanz für den Infektionsschutz (siehe Ja
§ 23 Abs. 3 IfSG oder § 36 Abs. 1 oder 2\s
IfSG)
Beginn der Tätigkeit/Betreuung 01.12.2021
/Unterbringung
Ende der Tätigkeit/Betreuung 05.01.2022
/Unterbringung
Rolle Tätigkeit
Einrichtungsname Straße 123, 21481 Buchhorst,\s
Einrichtungsadresse Deutschland Telefon: 0123456789 E-Mail:\s
mail@einrichtung.de
Wahrscheinlicher Expositionsort Ja
bekannt
Wahrscheinlicher Expositionsort Libyen
Beginn des Aufenthalts am\s
wahrscheinlichen Expositionsort/Datum 20.12.2021
des Aufenthalts
Ende des Aufenthalts am 28.12.2021
wahrscheinlichen Expositionsort
Anmerkungen zum Expositionsort Anmerkung
Spender für eine Blut-, Organ-, Gewebe- Ja
oder Zellspende in den letzten 6 Monaten
Zusatzinformationen zu den\s
Wichtige Zusatzinformationen meldetatbestandsübergreifenden klinischen und\s
epidemiologischen Angaben
Covid-19-spezifische klinische und epidemiologische Angaben
Kontakt zu bestätigtem Fall Ja
Infektionsumfeld vorhanden Ja
Wahrscheinliches Infektionsumfeld Gesundheitseinrichtung
Beginn Infektionsumfeld 28.12.2021
Ende Infektionsumfeld 30.12.2021
Wurde die betroffene Person jemals in Ja
Bezug auf die Krankheit geimpft?
Impfung 1
Impfstoff Comirnaty
Datum der Impfung 07.2021
Hinweis zur Impfung Zusatzinfo2
Impfung 2
Impfstoff nicht ermittelbar
Datum der Impfung 30.11.2021
Hinweis zur Impfung Zusatzinfo3
Impfung 3
Impfstoff Anderer Impfstoff
Datum der Impfung 25.12.2021
Hinweis zur Impfung Zusatzinfo4
Impfung 4
Impfstoff Comirnaty
Datum der Impfung 2021
Hinweis zur Impfung Zusatzinfo1
""";
    String pdfText = PdfExtractorHelper.extractPdfText(response.getContentAsByteArray());
    assertThat(pdfText).isEqualTo(cleanupString(expectedText));
  }

  @Test
  void givenValidReportWhenPostBedOccupancyThen200() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(LABORATORY_REPORT_PATH)
                    .content(LABORATORY_REPORT_BUNDLE_DV1_JSON)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponse(response, "Empfangsbestätigung Labormeldung - Pumpel Humpel.pdf");
    validateLabReportDv1Body(response);
  }

  @Test
  void generatePdfFromDv2BundleJsonString_shouldRespond200WithPdf() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(LABORATORY_REPORT_PATH)
                    .content(LABORATORY_REPORT_BUNDLE_DV2_JSON)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponse(response, "Empfangsbestätigung Labormeldung - Maxime Mustermann.pdf");
    validateLabReportDv2Body(response);
  }

  @Test
  void generatePdfFromBundleJsonString_shouldRespond422WhenContentTypeIsWrong() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(LABORATORY_REPORT_PATH)
                    .content(LABORATORY_REPORT_BUNDLE_DV2_XML)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateUnprocessableResponse(response);
  }

  @Test
  void generatePdfFromDv2BundleXmlString_shouldRespond200WithPdf() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_XML_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(LABORATORY_REPORT_PATH)
                    .content(LABORATORY_REPORT_BUNDLE_DV2_XML)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateOkResponse(response, "Empfangsbestätigung Labormeldung - Maxime Mustermann.pdf");
    validateLabReportDv2Body(response);
  }

  @Test
  void generatePdfFromBundleXmlString_shouldRespond422WhenContentTypeIsWrong() throws Exception {
    // given
    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", MediaType.APPLICATION_XML_VALUE);
    headers.set("Accept", MediaType.APPLICATION_PDF_VALUE);

    // when
    final MockHttpServletResponse response =
        this.mockMvc
            .perform(
                post(LABORATORY_REPORT_PATH)
                    .content(LABORATORY_REPORT_BUNDLE_DV2_JSON)
                    .headers(headers))
            .andReturn()
            .getResponse();

    // then
    validateUnprocessableResponse(response);
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

  private void validateUnprocessableResponse(MockHttpServletResponse response) {
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  private void validateLabReportDv1Body(MockHttpServletResponse response) throws Exception {
    // note: pdfbox extracts the title as the last line for some reason
    String expectedText =
        """
Empfangsbestätigung Labormeldung
Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet. Ggf.\s
wird man von dort Kontakt mit Ihnen aufnehmen, um weitere Daten zu ermitteln. Bitte speichern\s
Sie die Meldungsquittung datenschutzrechtlich sicher ab, da diese personenbezogene Daten enthält.
Meldevorgangs-ID a5e00874-bb26-45ac-8eea-0bde76456703
Zeitpunkt des 03.06.2020 15:35
Eingangs
Meldungsstatus Final
Meldungserstellung/- 03.06.2020 15:35
änderung
Meldungsempfänger
Bezirksamt Lichtenberg von Berlin | Gesundheitsamt Hygiene,\s
Name Infektionsschutz
Adresse Alfred-Kowalke-Straße 24, 10360 Berlin
Telefon: +49 30 90 296-7552 Fax: +49 30 90 296-7553 E-Mail:\s
Kontakt hygiene@lichtenberg.berlin.de
Meldende Person
Name MelderPersonTesVorname MelderPersonTestNachname
Adresse MelderPerson Strasse nr 123 Drittenhinterhof, 13055 Berlin, Deutschland
Kontakt Telefon: 030 1234 (Dienstlich)
Meldende Einrichtung
Name Testlabor (Erregerdiagnostische Untersuchungsstelle)
Identifier BSNR: 123456789
Adresse Teststraße 123a Dritter Hinterhof, 12347 Teststadt, Deutschland
Telefon: +49 30 09876543 221 (Dienstlich) Fax: +49 30 09876543 99221\s
Kontakt (Dienstlich) E-Mail: ifsg@demis-labortest.de (Dienstlich) URL:\s
https://www.demis-labortest.de (Dienstlich)
Kontaktperson VornameAnsprechpartnerIn NachnameAnsprechpartnerIn
Betroffene Person
Name Pumpel Humpel
Geschlecht Weiblich
Geburtsdatum 11.02.1950
Adresse Teststrasse 123, 13055 Berlin, Deutschland
(Hauptwohnsitz)
Kontakt Telefon: +49 30 888 666 999 (Privat) E-Mail: humpel@pumpel.de (Privat)
Einsendende Einrichtung
Name Einsendereinrichtungsname
Identifier BSNR: 123456780
Adresse Einsenderstr. 123, 13055 Berlin, Deutschland
Kontakt Telefon: +49 30 1234567890 (Dienstlich)
Laborbericht
Test - SARS-CoV-2 RNA NAA+probe Ql
Teststatus Final
Ergebniswert positiv
Befund Positiv
Nachweismethode Coronavirus SARS-CoV-2 (PCR)
positiv - Schwache Reaktivität in der SARS-CoV-2-PCR. Die schwache\s
Reaktivität deutet auf eine nur geringe Virusmenge in der Probe hin. Dieses\s
wäre in Zusammenschau mit dem Vorbefund während der späten Phase\s
einer ausheilenden Infektion zu erwarten. Im Falle einer aufgetretenen\s
Pneumonie sollten ergänzend Materialien der tiefen Atemwege wie\s
Bronchial- oder Trachealsekret bzw. BAL-Flüssigkeit untersucht werden.\s
Erläuterung (Test) Andernfalls sollten entsprechend den RKI-Empfehlungen weitere\s
Testungen durchgeführt werden, um bei komplett negativen Befunden die\s
Quarantänemaßnahmen nach Maßgabe des zuständigen Gesundheitsamtes\s
aufheben zu können. Die durchgeführte RT-PCR (RIDA GENE SARS-
CoV-2) weist die E-Region des Virusgenoms nach und wurde mit den\s
aktuell verfügbaren Möglichkeiten intern validiert.
Probeneingang 16.04.2020
Probenmaterialstatus Verfügbar
Probenmaterial Tupferabstrich
Informationen zur Weitergabe der zugehörigen Probe an andere Labore entsprechend dem DEMIS\s
Lifecyclemanagement
Das DEMIS-Lifecyclemanagement von Meldungen beschreibt in verschiedenen Szenarien den\s
Umgang mit Meldungen, inkl. Korrekturen und Ergänzungen (https://simplifier.net/guide
/implementierungsleitfadenfrdemis/meldungs-lifecyclemanagement). Ein Teil des\s
Lifecyclemanagements sieht auch die weiterführende Untersuchung der Probe in einem\s
Sekundärlabor vor. Dies ist z.B. in den Szenarien 2B und 3B beschrieben. Mit der Weitergabe der\s
Probe und des Probenbegleitscheins muss auch die Meldungs-ID (NotificationID) der\s
Erstmeldung an das Sekundärlabor weitergegeben werden. Diese finden Sie zur einfachen\s
Weitergabe auf dieser Seite.
Betroffene Person\s
Geschlecht Weiblich\s
Geburtsdatum (Monat/Jahr) 02/1950\s
Ersten 3 Ziffern der PLZ der Adresse (Hauptwohnsitz) 130
Meldende Einrichtung\s
Name Testlabor (Erregerdiagnostische Untersuchungsstelle)\s
Adresse Teststraße 123a Dritter Hinterhof, 12347 Teststadt, Deutschland\s
Kontakt Telefon: +49 30 09876543 221 (Dienstlich) Fax: +49 30 09876543 99221 (Dienstlich) E-
Mail: ifsg@demis-labortest.de (Dienstlich) URL: https://www.demis-labortest.de (Dienstlich)
Auftragsnummer (E2E-Referenz)\s
Meldevorgangs-ID\s
\s
a5e00874-bb26-45ac-8eea-0bde76456703
Bitte ergänzen Sie den auf dem Probenbegleitschein genannten Labor-Identifikator für die Probe:
""";
    validateLabReportPdfContent(response, cleanupString(expectedText));
  }

  private void validateLabReportDv2Body(MockHttpServletResponse response) throws Exception {
    // note: pdfbox extracts the title as the last line for some reason
    String expectedText =
        """
Empfangsbestätigung Labormeldung
Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet. Ggf.\s
wird man von dort Kontakt mit Ihnen aufnehmen, um weitere Daten zu ermitteln. Bitte speichern\s
Sie die Meldungsquittung datenschutzrechtlich sicher ab, da diese personenbezogene Daten enthält.
Meldevorgangs-ID a5e00874-bb26-45ac-8eea-0bde76456703
Zeitpunkt des Eingangs 04.03.2021 20:16
Meldungsidentifier e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9
Meldungsstatus Final
Meldungserstellung/- 04.03.2021 20:16
änderung
Meldungsverweis appends ABC123
(Primärlabor)
Meldungsempfänger
Bezirksamt Lichtenberg von Berlin | Gesundheitsamt Hygiene,\s
Name Infektionsschutz
Adresse Alfred-Kowalke-Straße 24, 10360 Berlin
Telefon: +49 30 90 296-7552 Fax: +49 30 90 296-7553 E-Mail:\s
Kontakt hygiene@lichtenberg.berlin.de
Meldende Person
Name Dr Adam Careful
Adresse 534 Erewhon St, 3999 PleasantVille
Kontakt Telefon: 030 1234 (Dienstlich)
Meldende Einrichtung
Name Primärlabor (Erregerdiagnostische Untersuchungsstelle)
Identifier BSNR: 98765430 DEMIS-Id: 13589
Adresse Dingsweg 321, 13055 Berlin, Deutschland
Kontakt Telefon: 0309876543210 (Dienstlich)
Betroffene Person
Name Maxime Mustermann
Geschlecht Weiblich
Geburtsdatum 11.02.1950
Adresse (Hauptwohnsitz) Teststrasse 123, 13055 Berlin, Deutschland
Adresse (Derzeitiger Friedrichstraße 987, 66666 Berlin, Deutschland
Aufenthaltsort)
Kontakt Telefon: 030 555 (Privat)
Einsendende Person
Name Dr. Dr. Otto Muster
Adresse Strasse 123, 33445 Stuttgart
Kontakt Fax: 030 1111 (Dienstlich)
Laborbericht
Erstellungs- 04.03.2021 20:15
/Bearbeitungszeitpunkt
Berichtsstatus Final
Meldetatbestand Severe-Acute-Respiratory-Syndrome-Coronavirus-2 (SARS-CoV-
2)
Ergebnis Meldepflichtiger Erreger nachgewiesen
(Zusammenfassung)
Erläuterung Ich bin die textuelle Conclusion ...
Auftragsnummer (E2E- 2021-000672922
Referenz)
Test - SARS-CoV-2 (COVID-19) RNA [Presence] in Serum or Plasma by NAA with probe\s
detection
Teststatus Final
Ergebniswert Detected
Befund Positiv
Nachweismethode Nucleic acid assay (procedure)
Erläuterung (Test) Nette Zusatzinformation …
Probeneingang 04.03.2021
Probenentnahme 04.03.2021
Probenmaterialstatus Verfügbar
Probenmaterial Upper respiratory swab sample (specimen)
Erläuterung (Probe) Ich bin eine interessante Zusatzinformation ...
Transaktions-Id (DESH) IMS-12345-CVDP-00001
Informationen zur Weitergabe der zugehörigen Probe an andere Labore entsprechend dem DEMIS\s
Lifecyclemanagement
Das DEMIS-Lifecyclemanagement von Meldungen beschreibt in verschiedenen Szenarien den\s
Umgang mit Meldungen, inkl. Korrekturen und Ergänzungen (https://simplifier.net/guide
/implementierungsleitfadenfrdemis/meldungs-lifecyclemanagement). Ein Teil des\s
Lifecyclemanagements sieht auch die weiterführende Untersuchung der Probe in einem\s
Sekundärlabor vor. Dies ist z.B. in den Szenarien 2B und 3B beschrieben. Mit der Weitergabe der\s
Probe und des Probenbegleitscheins muss auch die Meldungs-ID (NotificationID) der\s
Erstmeldung an das Sekundärlabor weitergegeben werden. Diese finden Sie zur einfachen\s
Weitergabe auf dieser Seite.
Betroffene Person\s
Geschlecht Weiblich\s
Geburtsdatum (Monat/Jahr) 02/1950\s
Ersten 3 Ziffern der PLZ der Adresse (Hauptwohnsitz) 130
Meldende Einrichtung\s
Name Primärlabor (Erregerdiagnostische Untersuchungsstelle)\s
Adresse Dingsweg 321, 13055 Berlin, Deutschland\s
Kontakt Telefon: 0309876543210 (Dienstlich)
Auftragsnummer (E2E-Referenz) 2021-000672922
Meldevorgangs-ID\s
\s
a5e00874-bb26-45ac-8eea-0bde76456703
Bitte ergänzen Sie den auf dem Probenbegleitschein genannten Labor-Identifikator für die Probe:
""";
    validateLabReportPdfContent(response, cleanupString(expectedText));
  }

  private void validateLabReportPdfContent(MockHttpServletResponse response, String expectedText)
      throws IOException {
    byte[] pdfBytes = response.getContentAsByteArray();
    validatePdfText(pdfBytes, expectedText);
    validatePdfHasQrCode(pdfBytes);
    PdfExtractorHelper.verifyField(pdfBytes, "lab-id", 2);
  }

  private void validatePdfHasQrCode(byte[] pdfBytes) {
    try {
      List<RenderedImage> imagesFromPdf = PdfExtractorHelper.getAllImagesFromPdfData(pdfBytes);
      assertThat(imagesFromPdf).hasSize(2);
      for (RenderedImage qrCode : imagesFromPdf) {
        assertThat(qrCode.getHeight()).isEqualTo(295);
        assertThat(qrCode.getWidth()).isEqualTo(295);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String cleanupString(final String input) {
    return input.replaceAll("\r\n", "\n").replaceAll("^\\s+", "").replaceAll("\\s+$", "");
  }
}
