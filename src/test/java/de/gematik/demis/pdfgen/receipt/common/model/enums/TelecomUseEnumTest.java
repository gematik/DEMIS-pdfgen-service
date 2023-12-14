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

package de.gematik.demis.pdfgen.receipt.common.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.hl7.fhir.r4.model.ContactPoint;
import org.junit.jupiter.api.Test;

class TelecomUseEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    TelecomUseEnum shouldBeHome = TelecomUseEnum.of(ContactPoint.ContactPointUse.HOME);
    TelecomUseEnum shouldBeWork = TelecomUseEnum.of(ContactPoint.ContactPointUse.WORK);
    TelecomUseEnum shouldBeTemp = TelecomUseEnum.of(ContactPoint.ContactPointUse.TEMP);
    TelecomUseEnum shouldBeOld = TelecomUseEnum.of(ContactPoint.ContactPointUse.OLD);
    TelecomUseEnum shouldBeMobile = TelecomUseEnum.of(ContactPoint.ContactPointUse.MOBILE);
    TelecomUseEnum shouldBeOther1 = TelecomUseEnum.of(ContactPoint.ContactPointUse.NULL);
    TelecomUseEnum shouldBeOther2 = TelecomUseEnum.of(null);

    // then
    assertThat(shouldBeHome).isEqualTo(TelecomUseEnum.HOME);
    assertThat(shouldBeWork).isEqualTo(TelecomUseEnum.WORK);
    assertThat(shouldBeTemp).isEqualTo(TelecomUseEnum.TEMP);
    assertThat(shouldBeOld).isEqualTo(TelecomUseEnum.OLD);
    assertThat(shouldBeMobile).isEqualTo(TelecomUseEnum.MOBILE);
    assertThat(shouldBeOther1).isNull();
    assertThat(shouldBeOther2).isNull();
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(TelecomUseEnum.HOME).hasToString("Privat");
    assertThat(TelecomUseEnum.WORK).hasToString("Dienstlich");
    assertThat(TelecomUseEnum.TEMP).hasToString("Temporär");
    assertThat(TelecomUseEnum.OLD).hasToString("Veraltet");
    assertThat(TelecomUseEnum.MOBILE).hasToString("Mobil");
  }

  @Test
  void toString_allEnumsHaveNonBlankMessageKeysExceptOther() {
    Stream.of(TelecomUseEnum.values())
        .forEach(
            // given
            telecomUseEnum -> {
              // then
              assertThat(telecomUseEnum.toString()).isNotNull().isNotEmpty();
            });
  }
}
