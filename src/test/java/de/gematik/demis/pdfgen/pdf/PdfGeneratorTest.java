package de.gematik.demis.pdfgen.pdf;

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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PdfGeneratorTest {
  private static final PdfGenerator generator = new PdfGenerator();
  private static final String VALID_HTML =
      "<!DOCTYPE html><html><head><title>Hello!</title></head><body><p>Hello!</p></body></html>";

  @Test
  void shouldThrowExceptionWhenInputIsNull() {
    assertThatThrownBy(() -> generator.generatePdfFromHtml(null))
        .isInstanceOf(PdfGenerationException.class);
  }

  @Test
  void shouldThrowExceptionWhenInputIsBlank() {
    assertThatThrownBy(() -> generator.generatePdfFromHtml("    "))
        .isInstanceOf(PdfGenerationException.class);
  }

  @Test
  void shouldThrowExceptionWhenInputIsInvalidHtml() {
    assertThatThrownBy(() -> generator.generatePdfFromHtml("Not a real html"))
        .isInstanceOf(PdfGenerationException.class);
  }

  @Test
  void shouldGeneratePdfFromValidHtmlString() throws Exception {
    // when
    byte[] pdfBytes = generator.generatePdfFromHtml(VALID_HTML);

    // then
    assertThat(pdfBytes).isNotNull().isNotEmpty();
  }
}
