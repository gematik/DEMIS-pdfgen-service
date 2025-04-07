package de.gematik.demis.pdfgen.receipt.common.model.enums;

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

import java.util.stream.Stream;
import org.hl7.fhir.r4.model.ContactPoint;
import org.junit.jupiter.api.Test;

class TelecomTypeEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    TelecomTypeEnum shouldBeEmail = TelecomTypeEnum.of(ContactPoint.ContactPointSystem.EMAIL);
    TelecomTypeEnum shouldBePhone = TelecomTypeEnum.of(ContactPoint.ContactPointSystem.PHONE);
    TelecomTypeEnum shouldBeFax = TelecomTypeEnum.of(ContactPoint.ContactPointSystem.FAX);
    TelecomTypeEnum shouldBeUrl = TelecomTypeEnum.of(ContactPoint.ContactPointSystem.URL);
    TelecomTypeEnum shouldBeOther1 = TelecomTypeEnum.of(ContactPoint.ContactPointSystem.OTHER);
    TelecomTypeEnum shouldBeOther2 = TelecomTypeEnum.of(ContactPoint.ContactPointSystem.NULL);
    TelecomTypeEnum shouldBeOther3 = TelecomTypeEnum.of(ContactPoint.ContactPointSystem.PAGER);
    TelecomTypeEnum shouldBeOther4 = TelecomTypeEnum.of(null);

    // then
    assertThat(shouldBeEmail).isEqualTo(TelecomTypeEnum.EMAIL);
    assertThat(shouldBePhone).isEqualTo(TelecomTypeEnum.PHONE);
    assertThat(shouldBeFax).isEqualTo(TelecomTypeEnum.FAX);
    assertThat(shouldBeUrl).isEqualTo(TelecomTypeEnum.URL);
    assertThat(shouldBeOther1).isEqualTo(TelecomTypeEnum.UNKNOWN);
    assertThat(shouldBeOther2).isEqualTo(TelecomTypeEnum.UNKNOWN);
    assertThat(shouldBeOther3).isEqualTo(TelecomTypeEnum.UNKNOWN);
    assertThat(shouldBeOther4).isEqualTo(TelecomTypeEnum.UNKNOWN);
  }

  @Test
  void getPrefix_toString_evaluatesToCapitalizedName() {
    assertThat(TelecomTypeEnum.EMAIL.getPrefix()).isEqualTo("E-Mail:");
    assertThat(TelecomTypeEnum.PHONE.getPrefix()).isEqualTo("Telefon:");
    assertThat(TelecomTypeEnum.FAX.getPrefix()).isEqualTo("Fax:");
    assertThat(TelecomTypeEnum.URL.getPrefix()).isEqualTo("URL:");
    assertThat(TelecomTypeEnum.UNKNOWN.getPrefix()).isEmpty();
  }

  @Test
  void getPrefix_allEnumsHaveNonBlankMessageKeysExceptOther() {
    Stream.of(TelecomTypeEnum.values())
        .forEach(
            // given
            telecomTypeEnum -> {
              if (telecomTypeEnum != TelecomTypeEnum.UNKNOWN) {
                // then
                assertThat(telecomTypeEnum.getPrefix()).isNotNull().isNotEmpty();
              }
            });
  }
}
