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

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.lowagie.text.pdf.BaseFont;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Slf4j
@Service
public class PdfGenerator {
  public byte[] generatePdfFromHtml(String html) throws PdfGenerationException {
    if (isBlank(html)) {
      String message = "Pdf generation from html string failed because input is blank";
      log.error(message);
      throw new PdfGenerationException(message);
    }

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream =
            new BufferedOutputStream(byteArrayOutputStream)) {

      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocumentFromString(html);
      renderer
          .getFontResolver()
          .addFont("/static/fonts/Arimo-Regular.ttf", BaseFont.IDENTITY_H, true);
      renderer.layout();
      renderer.createPDF(bufferedOutputStream);

      // Flush the buffered output stream to ensure all data is written to byte array output stream
      bufferedOutputStream.flush();

      return byteArrayOutputStream.toByteArray();

    } catch (Exception e) {
      String message = "Pdf generation from html string failed";
      log.error(message, e);
      throw new PdfGenerationException(message, e);
    }
  }
}
