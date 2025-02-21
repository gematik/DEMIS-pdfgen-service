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

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.lowagie.text.pdf.BaseFont;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
@Slf4j
public class PdfGenerator {

  /**
   * Circa size of the smallest PDF report. Currently, this is: bed occupancy report confirmation
   */
  private static final int SMALLEST_PDF_BYTES = 1024 * 150;

  @Value("${server.port:0}")
  private int port = 0;

  private static void verify(String html) throws PdfGenerationException {
    if (isBlank(html)) {
      String message = "Pdf generation from html string failed because input is blank";
      log.error(message);
      throw new PdfGenerationException(message);
    }
  }

  private static ITextRenderer createRenderer() throws IOException {
    ITextRenderer renderer = new ITextRenderer();
    renderer
        .getFontResolver()
        .addFont("/static/fonts/Arimo-Regular.ttf", BaseFont.IDENTITY_H, true);
    return renderer;
  }

  private static void writePdfBytes(String html, OutputStream out) throws IOException {
    ITextRenderer renderer = createRenderer();
    renderer.setDocumentFromString(html);
    renderer.layout();
    renderer.createPDF(out);
  }

  private static byte[] render(String html) throws PdfGenerationException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(SMALLEST_PDF_BYTES);
    try {
      writePdfBytes(html, out);
      return out.toByteArray();
    } catch (Exception e) {
      String message = "Pdf generation from html string failed";
      log.error(message, e);
      throw new PdfGenerationException(message, e);
    }
  }

  /**
   * Create PDF document from HTML document
   *
   * @param html HTML document
   * @return PDF document as bytes
   * @throws PdfGenerationException failed to create PDF document
   */
  public byte[] generatePdfFromHtml(String html) throws PdfGenerationException {
    verify(html);
    return render(fixResourceLinks(html));
  }

  private String fixResourceLinks(String html) {
    if (this.port > 0) {
      return new HttpResourcesHtml(html, this.port).get();
    }
    log.warn("PDF generator unable to create HTML resource links. Port is set to: 0");
    return html;
  }
}
