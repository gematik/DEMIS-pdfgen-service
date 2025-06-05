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

import jakarta.annotation.PostConstruct;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class WatermarkService {

  private final WatermarkConfiguration watermarkConfiguration;

  private String base64Image;

  private static String toBase64(final BufferedImage image) {
    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", os);
      return Base64.getEncoder().encodeToString(os.toByteArray());
    } catch (Exception e) {
      throw new IllegalStateException("Failed to create watermark image.", e);
    }
  }

  @PostConstruct
  void createWatermarkBase64Image() {
    final String watermarkText = watermarkConfiguration.text();
    if (StringUtils.isBlank(watermarkText)) {
      log.info("Watermark text is not set, skipping watermark image creation");
    } else {
      this.base64Image = toBase64(new Watermark(watermarkText).createBufferedImage());
      if (log.isDebugEnabled()) {
        log.debug(
            "Created watermark image. Text: {} Base64Image: {}", watermarkText, this.base64Image);
      } else {
        log.info("Created watermark image. Text: {}", watermarkText);
      }
    }
  }

  /**
   * Get watermark as base64 image, if enabled
   *
   * @return base64 image of watermark
   */
  public Optional<String> getWatermarkBase64Image() {
    return Optional.ofNullable(this.base64Image);
  }
}
