package de.gematik.demis.pdfgen.translation;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.gematik.demis.pdfgen.translation.client.CodeSystemFeignClient;
import de.gematik.demis.pdfgen.translation.client.ValueSetFeignClient;
import de.gematik.demis.pdfgen.translation.model.CodeDisplay;
import de.gematik.demis.pdfgen.translation.model.Designation;
import de.gematik.demis.pdfgen.translation.model.Use;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

  @Mock private ValueSetFeignClient valueSetFeignClientMock;
  @Mock private CodeSystemFeignClient codeSystemFeignClientMock;

  @Nested
  @DisplayName("value quantity unit translation tests")
  class ValueQuantityUnitTranslationTest {

    private TranslationService translationService;

    @Test
    void shouldReturnEmptyStringWhenCodeIsNull() {
      translationService =
          new TranslationService(
              "someUcumUrl",
              "someUcumUseCode",
              valueSetFeignClientMock,
              codeSystemFeignClientMock,
              false);

      String result = translationService.getValueQuantityUnit(null);

      assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyStringWhenClientReturnsNoData() {
      translationService =
          new TranslationService(
              "someUcumUrl",
              "someUcumUseCode",
              valueSetFeignClientMock,
              codeSystemFeignClientMock,
              false);

      when(codeSystemFeignClientMock.getInfoForCodeFromCodeSystem("someUcumUrl", "someCode"))
          .thenReturn(null);

      String result = translationService.getValueQuantityUnit("someCode");

      assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyStringWhenClientReturnsDataWithNoUcumUseCode() {
      translationService =
          new TranslationService(
              "someUcumUrl",
              "someUcumUseCode",
              valueSetFeignClientMock,
              codeSystemFeignClientMock,
              false);

      when(codeSystemFeignClientMock.getInfoForCodeFromCodeSystem("someUcumUrl", "someCode"))
          .thenReturn(
              new CodeDisplay(
                  "someCode",
                  "Some Display",
                  List.of(
                      new Designation(
                          "DE-de", "otherDisplay", new Use("useSystemUrl", "notAUcumUseCode")))));

      String result = translationService.getValueQuantityUnit("someCode");

      assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnStringFromReturnValueWhenClientReturnsData() {
      translationService =
          new TranslationService(
              "someUcumUrl",
              "someUcumUseCode",
              valueSetFeignClientMock,
              codeSystemFeignClientMock,
              false);

      when(codeSystemFeignClientMock.getInfoForCodeFromCodeSystem("someUcumUrl", "someCode"))
          .thenReturn(
              new CodeDisplay(
                  "someCode",
                  "Some Display",
                  List.of(
                      new Designation(
                          "DE-de", "otherDisplay", new Use("useSystemUrl", "someUcumUseCode")))));

      String result = translationService.getValueQuantityUnit("someCode");

      assertThat(result).isEqualTo("otherDisplay");
    }
  }

  @Test
  void shouldReturnLongCommonNameWhenPdfOptimizationIsEnabled() {
    TranslationService translationService =
        new TranslationService(
            "someUrl", "someCode", valueSetFeignClientMock, codeSystemFeignClientMock, true);

    when(codeSystemFeignClientMock.getInfoForCodeFromCodeSystem("someSystemUrl", "someCode"))
        .thenReturn(
            new CodeDisplay(
                "someUrl",
                "someCode",
                List.of(
                    new Designation(
                        "de-DE",
                        "the long common name",
                        new Use("http://loinc.org", "LONG_COMMON_NAME")))));

    Optional<String> result =
        translationService.resolveCodeableConceptValues("someSystemUrl", "someCode");
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo("the long common name");
  }

  @Test
  void shouldReturnFirstGermanDesignation() {
    TranslationService translationService =
        new TranslationService(
            "someUrl", "someCode", valueSetFeignClientMock, codeSystemFeignClientMock, true);

    when(codeSystemFeignClientMock.getInfoForCodeFromCodeSystem("someSystemUrl", "someCode"))
        .thenReturn(
            new CodeDisplay(
                "someUrl",
                "someCode",
                List.of(
                    new Designation(
                        "de-DE",
                        "fullySpecifiedName",
                        new Use("http://loinc.org", "not the long common name")))));

    Optional<String> result =
        translationService.resolveCodeableConceptValues("someSystemUrl", "someCode");
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo("fullySpecifiedName");
  }

  @Test
  void shouldReturnLongCommonNameBeforeAnyOtherEntry() {
    TranslationService translationService =
        new TranslationService(
            "someUrl", "someCode", valueSetFeignClientMock, codeSystemFeignClientMock, true);

    when(codeSystemFeignClientMock.getInfoForCodeFromCodeSystem("someSystemUrl", "someCode"))
        .thenReturn(
            new CodeDisplay(
                "someUrl",
                "someCode",
                List.of(
                    new Designation(
                        "de-DE",
                        "fullySpecifiedName",
                        new Use("http://loinc.org", "not the long common name")),
                    new Designation(
                        "de-DE",
                        "the long common name",
                        new Use("http://loinc.org", "LONG_COMMON_NAME")))));

    Optional<String> result =
        translationService.resolveCodeableConceptValues("someSystemUrl", "someCode");
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo("the long common name");
  }
}
