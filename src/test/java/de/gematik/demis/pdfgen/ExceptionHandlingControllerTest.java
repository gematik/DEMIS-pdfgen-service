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
 * #L%
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import de.gematik.demis.pdfgen.pdf.PdfGenerationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

class ExceptionHandlingControllerTest {

  private final ExceptionHandlingController controller =
      new ExceptionHandlingController(FhirContext.forR4());

  @Test
  void expectFhirResourceParsingExceptionReturns422() {
    final var response =
        controller.fhirResourceParsingExceptionReturns422(new DataFormatException());
    Assertions.assertEquals(422, response.getStatusCode().value());
  }

  @Test
  void expectPdfGenerationExceptionReturns500() {
    final var response =
        controller.pdfGenerationExceptionReturns500(new PdfGenerationException("unit test"));
    Assertions.assertEquals(500, response.getStatusCode().value());
  }

  @Test
  void expectHandleExceptionInternal() {
    final var response =
        controller.handleExceptionInternal(
            new RuntimeException("Test"),
            null,
            new HttpHeaders(),
            HttpStatusCode.valueOf(400),
            null);
    Assertions.assertEquals(400, response.getStatusCode().value());
    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }
}
