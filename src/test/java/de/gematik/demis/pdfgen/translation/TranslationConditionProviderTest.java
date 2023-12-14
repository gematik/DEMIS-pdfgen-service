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

package de.gematik.demis.pdfgen.translation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.hl7.fhir.r4.model.Coding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslationConditionProviderTest {

  private static final String EVIDENCE_SYSTEM_URL =
      "https://demis.rki.de/fhir/CodeSystem/translationEvidence";

  @Mock private TranslationService translationService;

  private TranslationConditionProvider conditionProvider;

  @BeforeEach
  void setup() {
    this.conditionProvider = new TranslationConditionProvider(translationService);
  }

  @Test
  @DisplayName("should use translation service for translation")
  void shouldTranslateRequest() {
    Coding coding = new Coding().setCode("exampleCode");

    when(translationService.resolveCodeableConceptValues(
            "https://demis.rki.de/fhir/CodeSystem/translationEvidence", "exampleCode"))
        .thenReturn(Optional.of("exampleTranslation"));

    String translation = conditionProvider.translateCode(coding);
    verify(translationService, times(1))
        .resolveCodeableConceptValues(EVIDENCE_SYSTEM_URL, coding.getCode());
    assertThat(translation).isEqualTo("exampleTranslation");
  }

  @Test
  @DisplayName("should use fallback when first call does not deliver translation")
  void shouldUseFallbackWhenFirstCallDoesNotDeliverTranslation() {
    Coding coding = new Coding().setCode("exampleCode2");
    String remoteTranslation = "exampleTranslation2";
    when(translationService.resolveCodeableConceptValues(
            "https://demis.rki.de/fhir/CodeSystem/translationEvidence", "exampleCode2"))
        .thenReturn(Optional.empty());
    when(translationService.resolveCodeableConceptValues(EVIDENCE_SYSTEM_URL, coding.getCode()))
        .thenReturn(Optional.of(remoteTranslation));

    String translation = conditionProvider.translateCode(coding);

    assertEquals(remoteTranslation, translation);
    verify(translationService).resolveCodeableConceptValues(coding);
    verifyNoMoreInteractions(translationService);
  }
}
