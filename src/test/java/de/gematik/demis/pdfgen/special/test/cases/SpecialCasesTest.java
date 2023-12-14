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

package de.gematik.demis.pdfgen.special.test.cases;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.LABORATORY_NOTIFICATION_WITH_MISSING_BIRTHDAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
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
public class SpecialCasesTest {

  private static final String LABORATORY_REPORT_PATH = "/laboratoryReport";

  private static final WireMockServer translationServer = new WireMockServer(7070);

  @Autowired private MockMvc mockMvc;

  @BeforeAll
  public static void startServers() {
    translationServer.start();
    configureFor(translationServer.port());
    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam("file", WireMock.equalTo("Country"))
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
            .withQueryParam("file", WireMock.equalTo("notificationCategory"))
            .withQueryParam("code", WireMock.equalTo("invp"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                                                {
                                                                                                                    "code": "invp",
                                                                                                                    "display": "Influenza",
                                                                                                                    "designations": []
                                                                                                                }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam("file", WireMock.equalTo("conclusionCode"))
            .withQueryParam("code", WireMock.equalTo("pathogenDetected"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                                                {
                                                                                                                    "code": "invp",
                                                                                                                    "display": "Influenza",
                                                                                                                    "designations": []
                                                                                                                }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam("system", WireMock.equalTo("system"))
            .withQueryParam("code", WireMock.equalTo("othPrivatLab"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                                                                                            {
                                                                                               "code" : "othPrivatLab",
                                                                                               "display" : "othPrivatLab",
                                                                                               "designations" : [ {
                                                                                                 "language" : "en",
                                                                                                 "value" : "othPrivatLab"
                                                                                               }, {
                                                                                                 "language" : "de",
                                                                                                 "value" : "andere Untersuchungsstelle"
                                                                                               } ]
                                                                                             }""")));

    stubFor(
        com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathMatching(".*\\/fhir-ui-data-model-translation/CodeSystem"))
            .withQueryParam("file", WireMock.equalTo("AddressUse"))
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
            .withQueryParam("file", WireMock.equalTo("AddressUse"))
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
            .withQueryParam("file", WireMock.equalTo("LaboratoryTestSARSCoV2"))
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
  }

  @AfterAll
  public static void stopServers() {
    translationServer.stop();
  }

  @Test
  @DisplayName("valid laboratory notification without birthday should not lead to an exception")
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
                    .content(LABORATORY_NOTIFICATION_WITH_MISSING_BIRTHDAY)
                    .headers(headers))
            .andReturn()
            .getResponse();

    var content = response.getContentAsByteArray();
    assertThat(content).isNotEmpty().isNotNull();
  }
}
