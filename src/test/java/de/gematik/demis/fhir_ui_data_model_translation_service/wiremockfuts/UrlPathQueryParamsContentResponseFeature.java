package de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts;

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

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.Map;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

/** Define an OK HTTP response for a given URL regex. Optionally you can add query parameters. */
@Builder
public class UrlPathQueryParamsContentResponseFeature implements Feature {

  private final String requestUrlRegex;
  private final Map<String, String> requestQueryParams;
  private final String responseBody;

  private static void addQueryParam(Map.Entry<String, String> param, MappingBuilder builder) {
    builder.withQueryParam(param.getKey(), WireMock.equalTo(param.getValue()));
  }

  @Override
  public void run() {
    checkSanity();
    MappingBuilder mappingBuilder = get(urlPathMatching(this.requestUrlRegex));
    addQueryParams(mappingBuilder);
    addResponse(mappingBuilder);
    stubFor(mappingBuilder);
  }

  private void checkSanity() {
    if (StringUtils.isBlank(this.requestUrlRegex)) {
      throw new IllegalStateException("request URL regex is not set");
    }
    if (StringUtils.isBlank(this.responseBody)) {
      throw new IllegalStateException("response body is not set");
    }
  }

  private void addQueryParams(MappingBuilder builder) {
    if (this.requestQueryParams != null) {
      this.requestQueryParams.entrySet().forEach(p -> addQueryParam(p, builder));
    }
  }

  private void addResponse(MappingBuilder builder) {
    builder.willReturn(okForContentType("application/json", this.responseBody));
  }
}
