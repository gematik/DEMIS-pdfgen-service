package de.gematik.demis.pdfgen;

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

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.QuestionnaireTranslationsCacheIntegrationTest;
import de.gematik.demis.pdfgen.translation.CodeSystemTranslationCacheIntegrationTest;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

/** Use production application.properties for testing */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class CacheIntegrationTest {

  private static final String QUESTIONNAIRES =
      QuestionnaireTranslationsCacheIntegrationTest.CACHE_FUTS_QUESTIONNAIRES;
  private static final String CODE_SYSTEMS =
      CodeSystemTranslationCacheIntegrationTest.CACHE_FUTS_CODE_SYSTEMS;

  @Autowired private CacheManager cacheManager;
  @Autowired private Environment environment;

  /** Ensures names and enabling of caches */
  @Test
  void shouldEnableCaches() {
    final Collection<String> cacheNames = this.cacheManager.getCacheNames();
    assertThat(cacheNames).containsExactlyInAnyOrder(CODE_SYSTEMS, QUESTIONNAIRES);
    assertThat(getCaffeineCache(CODE_SYSTEMS)).isNotNull();
    assertThat(getCaffeineCache(QUESTIONNAIRES)).isNotNull();
  }

  @Test
  void testCacheParameters() {
    assertThat(this.environment.getProperty("spring.cache.caffeine.spec"))
        .isEqualTo("expireAfterWrite=1h,expireAfterAccess=15m");
  }

  private CaffeineCache getCaffeineCache(String cacheName) {
    final Cache cache = this.cacheManager.getCache(cacheName);
    if (cache instanceof CaffeineCache caffeine) {
      return caffeine;
    }
    throw new IllegalStateException("Cache not found: " + cacheName);
  }
}
