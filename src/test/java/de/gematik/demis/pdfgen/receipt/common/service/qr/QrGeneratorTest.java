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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

class QrGeneratorTest {

  @Test
  void passIdAndCheckSizeOfQrCode() {
    String id = "a5e00874-bb26-45ac-8eea-0bde76456703";
    QrGenerator qrGenerator = new QrGenerator();
    String qrCodeAsBase64 = qrGenerator.generateQrCodeAsBase64(id);
    // This was checked manual that the id is the content of this qr code
    assertThat(qrCodeAsBase64)
        .isEqualTo(
            "iVBORw0KGgoAAAANSUhEUgAAAScAAAEnAQAAAADDIbjsAAACaElEQVR4Xu2ZQY7bMAxFZWQxyx5BR8nR7KP5KD7CLLMIzP73KU0yRYF2UaBAGC6imHrKgiA/KafFX9jZfvX8zt7UtDc17V9Te8M6jzd9Wc+LFz1+euejDmWHKDk++74udy2xhZfQUojydmxnaz/kXhS2ThA505aKVLtEHNfAjtbOspQdThmK57qdl6hH4R/bXR/3kUDjqRDVsD5k5NvinTrUMMGXmwK1mVJFaXtYHUoyQmjQValJo9det5TXtRK1t6ya0V2nvKImOXyUoSQcLpcVWNvL3RU1ji6lqMa4kTXkzHGvRVBD3edSicpmovnrQylDApFOOXXQh8tQNBPitZIk2paMKGyUUqZTIaq5aiwjDF6R1IBnDdWgiMmRERIst2AVllWWGJahyBy6a8YrxplHApWhUJObayg7jANlldVR9ZuH5rw6JYesQ1lQgSM2YII4dbUERczwZ68lc4heOF7nzK8CVFAuhCwYw5w5UMirfqEUlcbsiZJiLO68wcuMQtSCf1+Zv4gemZMSc7SneFWgKB4tOA5PHdKWISpP6vv6lAVVQydTuKiMHoc9g3zpagEqZywvfm/hf0Wu45LidKpD0Va7vuSlnYlLgjrk9Wv+KkARoQAmQoduZhk20qnnNbYKZTVpQz9MSVSoKBuNpg5lo1ycOefzGNYe6vv61O64KF5+79t8LekBNRtwGWqNbCb2Z+dVAqWatFrU0FXu7j3njGAhjPxCNco3M67wZ545eadRlKKtqobws0X00lmHipw9BTOTkzAN2IP67DAlKDtFedxgiXlJsX1T35em/mxvatqbmvYfqJ9yOxWF0r5NCQAAAABJRU5ErkJggg==");
  }

  @Test
  void passStringThatIsTooLongAndCheckThereIsEmptyQrCodeData() {
    String id = "12345-".repeat(500);
    QrGenerator qrGenerator = new QrGenerator();
    String qrCodeAsBase64 = qrGenerator.generateQrCodeAsBase64(id);
    assertThat(qrCodeAsBase64).isEmpty();
  }

  @Test
  void passEmptyStringAndCheckThereIsAnIllegalArgumentException() {
    String id = "";
    QrGenerator qrGenerator = new QrGenerator();
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              qrGenerator.generateQrCodeAsBase64(id);
            });
  }

  @Test
  void passNullStringAndCheckThereIsANullPointerException() {
    String id = null;
    QrGenerator qrGenerator = new QrGenerator();
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () -> {
              qrGenerator.generateQrCodeAsBase64(id);
            });
  }
}
