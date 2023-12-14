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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.hl7.fhir.r4.model.Coding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslationAddressProviderTest {

  @Mock private TranslationService translationService;

  private TranslationAddressProvider translationAddressProvider;

  @BeforeEach
  void setup() {
    this.translationAddressProvider = new TranslationAddressProvider(translationService);
  }

  @Test
  @DisplayName("should translate use code using translation service")
  void shouldTranslateUseCodeUsingTranslationServiceAndUseCacheForSameTranslation() {

    Coding coding = new Coding("system", "primary", null);
    when(translationService.resolveCodeableConceptValues("system", "primary"))
        .thenReturn(Optional.of("Hauptwohnsitz"));
    String translation = translationAddressProvider.translateUse(coding);

    assertThat(translation).isEqualTo("Hauptwohnsitz");
    verify(translationService).resolveCodeableConceptValues("system", "primary");
  }

  @Test
  @DisplayName("should use display if no translation found")
  void shouldUseDisplayIfNoTranslationFound() {

    Coding coding = new Coding("system", "primary", "Hauptwohnsitz");
    when(translationService.resolveCodeableConceptValues("system", "primary"))
        .thenReturn(Optional.empty());
    String translation1 = translationAddressProvider.translateUse(coding);

    assertThat(translation1).isEqualTo("Hauptwohnsitz");
  }

  @Test
  @DisplayName("should use code if no translation found")
  void shouldUseCodeIfNoTranslationFound() {

    Coding coding = new Coding("system", "primary", null);
    when(translationService.resolveCodeableConceptValues("system", "primary"))
        .thenReturn(Optional.empty());
    String translation1 = translationAddressProvider.translateUse(coding);

    assertThat(translation1).isEqualTo("primary");
  }

  @Test
  @DisplayName("should use code if no translation found")
  void shouldUseCountryCodeIfNoTranslationFound() {

    String countryCode = "24220";
    when(translationService.resolveCodeableConceptValues(
            "https://demis.rki.de/fhir/CodeSystem/country", countryCode))
        .thenReturn(Optional.empty());
    String translation1 = translationAddressProvider.translateCountryCode(countryCode);

    assertThat(translation1)
        .isEqualTo("https://demis.rki.de/fhir/CodeSystem/country" + ": " + "24220");
  }
}
