package de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.codes;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.Feature;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.UrlPathQueryParamsContentResponseFeature;
import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
public class CodeTranslationFeature implements Feature {

  private final Map<String, String> codes = new HashMap<>();
  private String urlRegex;
  private String system;

  public CodeTranslationFeature(String urlRegex, String system) {
    this.urlRegex = urlRegex;
    this.system = system;
  }

  public CodeTranslationFeature() {
    this(null, null);
  }

  @Override
  public void run() {
    this.codes.entrySet().forEach(c -> activate(c.getKey(), c.getValue()));
  }

  public CodeTranslationFeature add(String code, String response) {
    this.codes.put(code, response);
    return this;
  }

  private void activate(String code, String response) {
    log.debug("WireMockFUTS activating code translation. System: {} Code: {}", this.system, code);
    createFeature(code, response).run();
  }

  private Feature createFeature(String code, String response) {
    return UrlPathQueryParamsContentResponseFeature.builder()
        .requestUrlRegex(this.urlRegex)
        .requestQueryParams(createQueryParams(code))
        .responseBody(response)
        .build();
  }

  private Map<String, String> createQueryParams(String code) {
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put("system", this.system);
    queryParams.put("code", code);
    return queryParams;
  }
}
