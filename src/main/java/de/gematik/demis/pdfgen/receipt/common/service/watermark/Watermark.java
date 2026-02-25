package de.gematik.demis.pdfgen.receipt.common.service.watermark;

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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;

/** Renders a watermark image using PDFBox with bold font, 45° rotation and optimized size. */
@RequiredArgsConstructor
final class Watermark {

  private static final float FONT_SIZE = 110f;
  private static final float ROTATION_DEGREES = 45f;
  private static final int IMAGE_DPI = 150;
  // Light gray in RGB: 211, 211, 211
  private static final float GRAY_VALUE = 211f / 255f;

  private final String text;

  BufferedImage createBufferedImage() {
    try (PDDocument document = new PDDocument()) {
      PDPage page = createWatermarkedPage(document);
      document.addPage(page);
      return renderPageToImage(document);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create watermark image", e);
    }
  }

  private PDPage createWatermarkedPage(PDDocument document) throws IOException {
    PDPage page = new PDPage(PDRectangle.A4);

    try (PDPageContentStream contentStream =
            new PDPageContentStream(
                document, page, PDPageContentStream.AppendMode.OVERWRITE, true);
        InputStream fontStream =
            getClass().getResourceAsStream("/static/fonts/GoogleSans-Bold.ttf")) {
      PDType0Font font = PDType0Font.load(document, Objects.requireNonNull(fontStream), false);
      contentStream.setFont(font, FONT_SIZE);
      float textWidth = font.getStringWidth(text) / 1000f * FONT_SIZE;
      contentStream.setNonStrokingColor(GRAY_VALUE, GRAY_VALUE, GRAY_VALUE);
      float pageWidth = page.getMediaBox().getWidth();
      float pageHeight = page.getMediaBox().getHeight();
      float centerX = pageWidth / 2f;
      float centerY = pageHeight / 2f;
      contentStream.beginText();

      // Create transformation matrix for rotation and positioning
      Matrix matrix =
          new Matrix(
              (float) Math.cos(Math.toRadians(ROTATION_DEGREES)),
              (float) Math.sin(Math.toRadians(ROTATION_DEGREES)),
              (float) -Math.sin(Math.toRadians(ROTATION_DEGREES)),
              (float) Math.cos(Math.toRadians(ROTATION_DEGREES)),
              centerX
                  - (textWidth * 0.94f) / 2f * (float) Math.cos(Math.toRadians(ROTATION_DEGREES)),
              centerY - textWidth / 2f * (float) Math.sin(Math.toRadians(ROTATION_DEGREES)));

      contentStream.setTextMatrix(matrix);
      contentStream.showText(text);
      contentStream.endText();
    }

    return page;
  }

  private BufferedImage renderPageToImage(PDDocument document) throws IOException {
    PDFRenderer renderer = new PDFRenderer(document);
    return renderer.renderImageWithDPI(0, IMAGE_DPI);
  }
}
