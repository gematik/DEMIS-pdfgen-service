package de.gematik.demis.pdfgen.receipt.common.service.watermark;

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

import java.awt.*;
import java.awt.image.BufferedImage;
import lombok.RequiredArgsConstructor;

/**
 * Renders a watermark image using AWT with bold font, 45° rotation and best ratio on content to
 * image size.
 */
@RequiredArgsConstructor
final class Watermark {

  private static final int CANVAS_PX = 3000;

  private final String text;
  private BufferedImage image;

  BufferedImage createBufferedImage() {
    createImage();
    cropToContent();
    return image;
  }

  private void createImage() {
    image = new BufferedImage(CANVAS_PX, CANVAS_PX, BufferedImage.TYPE_INT_RGB);
    final Graphics2D g2d = image.createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    g2d.setFont(new Font("SansSerif", Font.BOLD, 120));
    g2d.setColor(Color.lightGray);
    g2d.rotate(Math.toRadians(-45), image.getWidth() / 2.0, image.getHeight() / 2.0);
    final FontMetrics fontMetrics = g2d.getFontMetrics();
    int textWidth = fontMetrics.stringWidth(text);
    int textHeight = fontMetrics.getHeight();
    g2d.drawString(text, (CANVAS_PX - textWidth) / 2, (CANVAS_PX + textHeight) / 2);
    g2d.dispose();
  }

  private void cropToContent() {
    final int top = getTopBoundary();
    final int bottom = getBottomBoundary();
    final int left = getLeftBoundary();
    final int right = getRightBoundary();
    image = image.getSubimage(left, top, right - left + 1, bottom - top + 1);
  }

  private int getTopBoundary() {
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (image.getRGB(x, y) != Color.WHITE.getRGB()) {
          return y;
        }
      }
    }
    return 0;
  }

  private int getBottomBoundary() {
    for (int y = image.getHeight() - 1; y >= 0; y--) {
      for (int x = 0; x < image.getWidth(); x++) {
        if (image.getRGB(x, y) != Color.WHITE.getRGB()) {
          return y;
        }
      }
    }
    return image.getHeight() - 1;
  }

  private int getLeftBoundary() {
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        if (image.getRGB(x, y) != Color.WHITE.getRGB()) {
          return x;
        }
      }
    }
    return 0;
  }

  private int getRightBoundary() {
    for (int x = image.getWidth() - 1; x >= 0; x--) {
      for (int y = 0; y < image.getHeight(); y++) {
        if (image.getRGB(x, y) != Color.WHITE.getRGB()) {
          return x;
        }
      }
    }
    return image.getWidth() - 1;
  }
}
