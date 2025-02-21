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

package de.gematik.demis.pdfgen.receipt.common.model.subsection;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.gematik.demis.pdfgen.test.helper.CodingMatcher;
import de.gematik.demis.pdfgen.translation.TranslationService;
import org.hl7.fhir.r4.model.Coding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressTranslationServiceTest {

  @Mock private TranslationService translationService;
  @InjectMocks private AddressTranslationService addressTranslationService;

  @Test
  @DisplayName("address use translation should use translation when display is given")
  void translateUse_shouldUseTranslation() {

    // when
    String system = "system";
    String code = "primary";
    String german = "Hauptwohnsitz";
    String english = "primary address of user";
    Coding request = new Coding(system, code, english);
    when(this.translationService.resolveCodeableConceptValues(argThat(new CodingMatcher(request))))
        .thenReturn(german);

    // then
    String translation = addressTranslationService.translateUse(request);
    assertThat(translation).isEqualTo(german);
    verify(this.translationService)
        .resolveCodeableConceptValues(argThat(new CodingMatcher(request)));
  }

  @Test
  @DisplayName("address use translation should use translation when no display is given")
  void translateUse_shouldUseTranslationWithoutDisplay() {

    // when
    String system = "system";
    String code = "primary";
    String german = "Hauptwohnsitz";
    Coding input = new Coding(system, code, null);
    Coding request = new Coding(system, code, code);
    when(this.translationService.resolveCodeableConceptValues(argThat(new CodingMatcher(request))))
        .thenReturn(german);

    // then
    String translation = addressTranslationService.translateUse(input);
    assertThat(translation).isEqualTo(german);
    verify(this.translationService)
        .resolveCodeableConceptValues(argThat(new CodingMatcher(request)));
  }

  @Test
  @DisplayName("address use translation should use display if no translation found")
  void translateUse_shouldUseDisplayIfNoTranslationFound() {

    // when
    String system = "system";
    String code = "primary";
    String display = "Hauptwohnsitz";
    Coding request = new Coding(system, code, display);
    when(this.translationService.resolveCodeableConceptValues(argThat(new CodingMatcher(request))))
        .thenReturn(display);

    // then
    String translation = addressTranslationService.translateUse(request);
    assertThat(translation).isEqualTo(display);
    verify(this.translationService)
        .resolveCodeableConceptValues(argThat(new CodingMatcher(request)));
  }

  @Test
  @DisplayName("address use translation should use code if no translation found")
  void translateUse_shouldUseCodeIfNoTranslationFound() {
    // when
    String system = "system";
    String code = "primary";
    Coding input = new Coding(system, code, null);
    Coding request = new Coding(system, code, code);
    when(this.translationService.resolveCodeableConceptValues(argThat(new CodingMatcher(request))))
        .thenReturn(code);

    // then
    String translation = this.addressTranslationService.translateUse(input);
    assertThat(translation).isEqualTo(code);
    verify(this.translationService)
        .resolveCodeableConceptValues(argThat(new CodingMatcher(request)));
  }

  @Test
  @DisplayName("country translation should use translation")
  void translateCountryCode_shouldUseTranslation() {

    // when
    String system = AddressTranslationService.COUNTRY_CODE_SYSTEM_URL;
    String code = "24220";
    String display = "Deutschland";
    Coding request = new Coding(system, code, null);
    when(this.translationService.resolveCodeableConceptValues(argThat(new CodingMatcher(request))))
        .thenReturn(display);

    // then
    String translation = this.addressTranslationService.translateCountryCode(code);
    assertThat(translation).isEqualTo(display);
    verify(this.translationService)
        .resolveCodeableConceptValues(argThat(new CodingMatcher(request)));
  }

  @Test
  @DisplayName("country translation should use ISO 3166")
  void translateCountryCode_shouldQueryIso3166() {

    // when
    String system = AddressTranslationService.COUNTRY_CODE_SYSTEM_ISO_3166_URL;
    String code = "DE";
    String display = "Germany";
    Coding request = new Coding(system, code, null);
    when(this.translationService.resolveCodeableConceptValues(argThat(new CodingMatcher(request))))
        .thenReturn(display);

    // then
    String translation = this.addressTranslationService.translateCountryCode(code);
    assertThat(translation).isEqualTo(display);
    verify(this.translationService)
        .resolveCodeableConceptValues(argThat(new CodingMatcher(request)));
  }
}
