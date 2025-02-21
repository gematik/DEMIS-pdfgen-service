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

package de.gematik.demis.pdfgen.receipt.common.service.qr;

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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QrGenerator {
  public static final int SIZE = 295;

  public String generateQrCodeAsBase64(String qrContent) {
    return Base64.getEncoder().encodeToString(generateQrCode(qrContent));
  }

  private byte[] generateQrCode(String qrContent) {
    QRCodeWriter barcodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = null;
    try {
      bitMatrix =
          barcodeWriter.encode(
              qrContent,
              BarcodeFormat.QR_CODE,
              SIZE,
              SIZE,
              Map.of(
                  EncodeHintType.MARGIN,
                  Integer.valueOf(0),
                  EncodeHintType.ERROR_CORRECTION,
                  ErrorCorrectionLevel.H));
    } catch (WriterException e) {
      log.error("Cannot create QR code: %s".formatted(e.getMessage()), e);
      return new byte[0];
    }
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
    } catch (IOException e) {
      log.error("Cannot create QR code: %s".formatted(e.getMessage()), e);
      return new byte[0];
    }
    return byteArrayOutputStream.toByteArray();
  }
}
