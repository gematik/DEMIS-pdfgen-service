package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire;

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
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.disease.DiseaseFeature;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.translation.QuestionnaireTranslation;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.translation.QuestionnaireTranslations;
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
        pdfgen.warmup=false
        wiremock.server.baseUrl=http://localhost:${wiremock.server.port}
        demis.network.fhir-ui-data-model-translation-address=http://localhost:${wiremock.server.port}
    """)
public class QuestionnaireTranslationsCacheIntegrationTest {

  public static final String CACHE_FUTS_QUESTIONNAIRES = "futs-disease-questionnaires";
  private static final String QUESTIONNAIRE_TITLE_COMMON =
      "Meldetatbestandsübergreifende klinische und epidemiologische Angaben";

  @Autowired private CacheManager cacheManager;
  @Autowired private QuestionnaireTranslations questionnaireTranslations;

  @Test
  void getQuestionnaireTranslations_shouldCacheResponses() {
    this.cacheManager.getCache(CACHE_FUTS_QUESTIONNAIRES).clear();
    new WireMockFuts().add(new DiseaseFeature());

    // First request
    final String request = "common";
    final QuestionnaireTranslation response1 = this.questionnaireTranslations.apply(request);
    assertThat(response1).isNotNull();
    assertThat(response1.title()).isEqualTo(QUESTIONNAIRE_TITLE_COMMON);
    assertThat(WireMock.getAllServeEvents()).as("WireMock serves after first request").hasSize(1);
    assertThat(getCacheSize(CACHE_FUTS_QUESTIONNAIRES))
        .as("cache size after first request")
        .isEqualTo(1);

    // Second request
    final QuestionnaireTranslation response2 = this.questionnaireTranslations.apply(request);
    assertThat(response2).isNotNull().isNotSameAs(response1);
    assertThat(response2.title()).isEqualTo(QUESTIONNAIRE_TITLE_COMMON);
    assertThat(WireMock.getAllServeEvents()).as("WireMock serves after second request").hasSize(1);
    assertThat(getCacheSize(CACHE_FUTS_QUESTIONNAIRES))
        .as("cache size after second request")
        .isEqualTo(1);
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
