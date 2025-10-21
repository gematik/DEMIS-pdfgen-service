package de.gematik.demis.pdfgen.translation;

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

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.WireMockFuts;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.codes.DefaultTranslationFeature;
import org.hl7.fhir.r4.model.Coding;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

/** Use production application.properties for testing */
@AutoConfigureWireMock(port = 0)
@SpringBootTest
@TestPropertySource(
    locations = "classpath:application.properties",
    properties =
        """
        wiremock.server.baseUrl=http://localhost:${wiremock.server.port}
        demis.network.fhir-ui-data-model-translation.address=http://localhost:${wiremock.server.port}
    """)
public class CodeSystemTranslationCacheIntegrationTest {

  public static final String CACHE_FUTS_CODE_SYSTEMS = "futs-code-systems";

  @Autowired private CacheManager cacheManager;
  @Autowired private TranslationService translationService;

  @Test
  void resolveCodeableConceptValues_shouldCacheCodeSystemResponses() {
    this.cacheManager.getCache(CACHE_FUTS_CODE_SYSTEMS).clear();
    new WireMockFuts().add(new DefaultTranslationFeature());

    // First request
    Coding request1 =
        new Coding()
            .setSystem("https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType")
            .setCode("3600");
    final String response1 = this.translationService.resolveCodeableConceptValues(request1);
    assertThat(response1).as("response on first request").isEqualTo("Intensivmedizin");
    assertThat(WireMock.getAllServeEvents()).as("WireMock serves after first request").hasSize(1);
    assertThat(getCacheSize(CACHE_FUTS_CODE_SYSTEMS))
        .as("cache size after first request")
        .isEqualTo(1);

    // Second request
    final String response2 = this.translationService.resolveCodeableConceptValues(request1);
    assertThat(response2).as("response on second request").isEqualTo(response1);
    assertThat(WireMock.getAllServeEvents()).as("WireMock serves after second request").hasSize(1);
    assertThat(getCacheSize(CACHE_FUTS_CODE_SYSTEMS))
        .as("cache size after second request")
        .isEqualTo(1);

    // Third request
    Coding request2 =
        new Coding()
            .setSystem("https://demis.rki.de/fhir/CodeSystem/militaryAffiliation")
            .setCode("memberOfBundeswehr");
    String response3 = this.translationService.resolveCodeableConceptValues(request2);
    assertThat(response3).isEqualTo("Soldat/BW-Angehöriger");
    assertThat(WireMock.getAllServeEvents()).as("WireMock serves after third request").hasSize(2);
    assertThat(getCacheSize(CACHE_FUTS_CODE_SYSTEMS))
        .as("cache size after third request")
        .isEqualTo(2);
  }

  private long getCacheSize(String cacheName) {
    return getCaffeineCache(cacheName).getNativeCache().estimatedSize();
  }

  private CaffeineCache getCaffeineCache(String cacheName) {
    final Cache cache = this.cacheManager.getCache(cacheName);
    if (cache instanceof CaffeineCache caffeine) {
      return caffeine;
    }
    throw new IllegalStateException("Cache not found: " + cacheName);
  }
}
