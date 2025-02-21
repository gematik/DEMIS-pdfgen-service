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
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

@Slf4j
@ControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {
  private final FhirContext ctx;

  public ExceptionHandlingController(FhirContext ctx) {
    this.ctx = ctx;
  }

  @ExceptionHandler(DataFormatException.class)
  public ResponseEntity<String> fhirResourceParsingExceptionReturns422(
      DataFormatException exception) {
    log.warn(
        "Returning 422 (Unprocessable Entity) response status due to FhirResourceParsingException",
        exception);
    String serializedOperationOutcome = createErrorOperationOutcome("Unsupported resource bundle");
    return ResponseEntity.unprocessableEntity().body(serializedOperationOutcome);
  }

  @ExceptionHandler(PdfGenerationException.class)
  public ResponseEntity<String> pdfGenerationExceptionReturns500(PdfGenerationException exception) {
    log.error(
        "Returning 500 Internal Server error response status due to PdfGenerationException",
        exception);
    String serializedOperationOutcome =
        createErrorOperationOutcome("Internal Error: pdf generation failed");
    return ResponseEntity.internalServerError().body(serializedOperationOutcome);
  }

  @NotNull
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex,
      @Nullable Object body,
      HttpHeaders headers,
      HttpStatusCode statusCode,
      WebRequest request) {

    String operationOutcome = null;
    if (ex instanceof HttpRequestMethodNotSupportedException ex2) {
      operationOutcome =
          createErrorOperationOutcome("Request method " + ex2.getMethod() + " not supported");
    } else if (ex instanceof HttpMediaTypeNotSupportedException ex2) {
      operationOutcome =
          createErrorOperationOutcome("Content type " + ex2.getContentType() + " not supported");
    } else if (ex instanceof NoHandlerFoundException) {
      operationOutcome = createErrorOperationOutcome("Not found");
    }

    if (statusCode.is5xxServerError()) {
      request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
    }
    return new ResponseEntity<>(
        operationOutcome != null ? operationOutcome : body, headers, statusCode);
  }

  private String createErrorOperationOutcome(String message) {
    log.warn(message);

    OperationOutcome.OperationOutcomeIssueComponent issue =
        new OperationOutcome.OperationOutcomeIssueComponent();
    issue.setSeverity(OperationOutcome.IssueSeverity.ERROR);
    issue.setDiagnostics(message);

    OperationOutcome operationOutcome = new OperationOutcome();
    operationOutcome.addIssue(issue);

    return ctx.newJsonParser().encodeResourceToString(operationOutcome);
  }
}
