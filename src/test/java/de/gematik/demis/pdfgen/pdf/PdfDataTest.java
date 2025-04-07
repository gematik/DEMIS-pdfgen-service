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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PdfDataTest {

  private final byte[] bytes = "fake pdf".getBytes();
  private final PdfData pdfData = new PdfData(bytes);

  @Test
  void shouldImplementDefaultObjectMethods() {
    PdfData other = new PdfData(this.bytes);
    assertThat(this.pdfData)
        .isEqualTo(other)
        .hasSameHashCodeAs(other)
        .hasToString("PdfData Bytes: 8");
  }

  @Test
  void shouldCreateOkResponse() {
    // when
    ResponseEntity<byte[]> pdfResponse = this.pdfData.createOkResponse();
    // then
    assertThat(pdfResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(pdfResponse.getBody()).isEqualTo(this.bytes);
    assertThat(pdfResponse.getHeaders().getLastModified()).isNotZero();
    assertThat(pdfResponse.getHeaders().getContentDisposition()).hasToString("attachment");
  }
}
