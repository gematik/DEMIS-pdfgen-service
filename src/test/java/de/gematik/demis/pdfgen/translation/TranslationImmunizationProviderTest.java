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
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslationImmunizationProviderTest {

  @Mock private TranslationService translationService;

  private TranslationImmunizationProvider translationImmunizationProvider;

  @BeforeEach
  void setup() {
    this.translationImmunizationProvider = new TranslationImmunizationProvider(translationService);
  }

  @Test
  @DisplayName("should use translation service for translation")
  void shouldUseTranslationServiceForTranslation() {

    Coding coding = new Coding("system", "code", "display");
    CodeableConcept codeableConcept = new CodeableConcept(coding);

    when(translationService.resolveCodeableConceptValues(
            "https://demis.rki.de/fhir/ValueSet/vaccineCVDD", "code"))
        .thenReturn(Optional.of("translation"));

    String translation1 = translationImmunizationProvider.translateCode(codeableConcept);

    assertThat(translation1).isEqualTo("translation");
    verify(translationService)
        .resolveCodeableConceptValues("https://demis.rki.de/fhir/ValueSet/vaccineCVDD", "code");
  }

  @Test
  @DisplayName("should use translation service for translation with nullflavor")
  void shouldUseTranslationServiceForTranslationWithNullflavor() {

    Coding coding = new Coding("system", "ASKU", "display");
    CodeableConcept codeableConcept = new CodeableConcept(coding);

    when(translationService.resolveCodeableConceptValues(
            "http://terminology.hl7.org/CodeSystem/v3-NullFlavor", "ASKU"))
        .thenReturn(Optional.of("translation"));

    String translation1 = translationImmunizationProvider.translateCode(codeableConcept);

    assertThat(translation1).isEqualTo("translation");
    verify(translationService)
        .resolveCodeableConceptValues(
            "http://terminology.hl7.org/CodeSystem/v3-NullFlavor", "ASKU");
    verify(translationService, times(0))
        .resolveCodeableConceptValues("https://demis.rki.de/fhir/ValueSet/vaccineCVDD", "ASKU");
  }
}
