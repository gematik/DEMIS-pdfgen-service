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

package de.gematik.demis.pdfgen.receipt.common.model.subsection;

import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.EMAIL;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.FAX;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.PHONE;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.URL;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.HOME;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.MOBILE;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.TEMP;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.WORK;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class TelecomTest {

  @Test
  void builder_shouldCreateTelecomAndHaveGetters() {
    // given
    Telecom telecom = createTelecom("value", PHONE, WORK);

    // then
    assertThat(telecom.getValue()).isEqualTo("value");
    assertThat(telecom.getSystem()).isEqualTo(PHONE);
    assertThat(telecom.getUse()).isEqualTo(WORK);
  }

  @Test
  void builder_shouldHandleEmptyAndNullGracefully() {
    // given
    List<String> values = Arrays.asList(null, "", "  ", "value");
    List<TelecomTypeEnum> systems = Arrays.asList(null, PHONE);
    List<TelecomUseEnum> uses = Arrays.asList(null, WORK);

    values.forEach(
        value -> {
          systems.forEach(
              system -> {
                uses.forEach(
                    use -> {
                      // when
                      Telecom telecom = createTelecom(value, system, use);

                      // then
                      assertThat(telecom.getValue()).isEqualTo(value);
                      assertThat(telecom.getSystem()).isEqualTo(system);
                      assertThat(telecom.getUse()).isEqualTo(use);
                    });
              });
        });
  }

  @Test
  void asSingleLine_shouldCreateStringFromTelecom() {
    // given
    Telecom email = createTelecom("email1", EMAIL, HOME);
    Telecom url = createTelecom("url1", URL, TEMP);
    Telecom fax = createTelecom("fax1", FAX, WORK);
    Telecom phone = createTelecom("phone1", PHONE, MOBILE);

    String expectedEmail = "E-Mail: email1 (Privat)";
    String expectedUrl = "URL: https://url1 (Temporär)";
    String expectedFax = "Fax: fax1 (Dienstlich)";
    String expectedPhone = "Telefon: phone1 (Mobil)";

    assertThat(email.asSingleLine()).isEqualTo(expectedEmail);
    assertThat(url.asSingleLine()).isEqualTo(expectedUrl);
    assertThat(fax.asSingleLine()).isEqualTo(expectedFax);
    assertThat(phone.asSingleLine()).isEqualTo(expectedPhone);
  }

  @Test
  void asSingleLine_shouldHandleNullAndEmptyGracefully() {
    // given
    Telecom nullUse = createTelecom("phone1", PHONE, null);
    Telecom nullType = createTelecom("phone2", null, WORK);
    Telecom nullTypeAndUse = createTelecom("phone3", null, null);
    Telecom nullValue = createTelecom(null, PHONE, WORK);
    Telecom emptyValue = createTelecom("", PHONE, WORK);
    Telecom blankValue = createTelecom("   ", PHONE, WORK);

    String expectedNullUse = "Telefon: phone1";
    String expectedNullType = "phone2 (Dienstlich)";
    String expectedNullTypeAndUse = "phone3";
    String emptyString = "";

    assertThat(nullUse.asSingleLine()).isEqualTo(expectedNullUse);
    assertThat(nullType.asSingleLine()).isEqualTo(expectedNullType);
    assertThat(nullTypeAndUse.asSingleLine()).isEqualTo(expectedNullTypeAndUse);
    assertThat(nullValue.asSingleLine()).isEqualTo(emptyString);
    assertThat(emptyValue.asSingleLine()).isEqualTo(emptyString);
    assertThat(blankValue.asSingleLine()).isEqualTo(emptyString);
  }

  private Telecom createTelecom(String value, TelecomTypeEnum type, TelecomUseEnum use) {
    return Telecom.builder().value(value).system(type).use(use).build();
  }
}
