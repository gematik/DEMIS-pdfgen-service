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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WatermarkServiceTest {

  @Mock private WatermarkConfiguration watermarkConfiguration;
  @InjectMocks private WatermarkService watermarkService;

  @Test
  void givenEmptyStringWhenGetWatermarkBase64ImageThenReturnEmptyWatermark() {
    // Arrange
    when(watermarkConfiguration.text()).thenReturn("");

    // Act
    Optional<String> actual = createWatermark();

    // Assert
    assertThat(actual).as("no watermark created from empty String").isNotPresent();
  }

  @Test
  void givenNullStringWhenGetWatermarkBase64ImageThenReturnEmptyWatermark() {
    // Arrange
    when(watermarkConfiguration.text()).thenReturn(null);

    // Act
    Optional<String> actual = createWatermark();

    // Assert
    assertThat(actual).as("no watermark created from null String").isNotPresent();
  }

  @Test
  void givenValidStringWhenGetWatermarkBase64ImageThenReturnWatermark() {
    // Arrange
    final String watermarkText = "WATERMARK";
    when(watermarkConfiguration.text()).thenReturn(watermarkText);

    // Act
    Optional<String> actual = createWatermark();

    // Assert
    assertThat(actual).as("watermark created").isPresent();
    assertThat(actual.get())
        .as("watermark PNG encoded as BASE64")
        .isNotEmpty()
        .hasSizeGreaterThan(50)
        .isBase64();
  }

  private Optional<String> createWatermark() {
    watermarkService.createWatermarkBase64Image();
    return watermarkService.getWatermarkBase64Image();
  }
}
