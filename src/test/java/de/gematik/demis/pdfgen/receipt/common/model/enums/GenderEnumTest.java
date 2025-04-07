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
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.junit.jupiter.api.Test;

class GenderEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    GenderEnum shouldBeMale = GenderEnum.of(AdministrativeGender.MALE);
    GenderEnum shouldBeFemale = GenderEnum.of(AdministrativeGender.FEMALE);
    GenderEnum shouldBeOther = GenderEnum.of(AdministrativeGender.OTHER);
    GenderEnum shouldBeUnknown1 = GenderEnum.of(AdministrativeGender.UNKNOWN);
    GenderEnum shouldBeUnknown2 = GenderEnum.of(AdministrativeGender.NULL);
    GenderEnum shouldBeUnknown3 = GenderEnum.of(null);

    // then
    assertThat(shouldBeMale).isEqualTo(GenderEnum.MALE);
    assertThat(shouldBeFemale).isEqualTo(GenderEnum.FEMALE);
    assertThat(shouldBeOther).isEqualTo(GenderEnum.OTHER);
    assertThat(shouldBeUnknown1).isEqualTo(GenderEnum.UNKNOWN);
    assertThat(shouldBeUnknown2).isEqualTo(GenderEnum.UNKNOWN);
    assertThat(shouldBeUnknown3).isEqualTo(GenderEnum.UNKNOWN);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(GenderEnum.MALE).hasToString("Männlich");
    assertThat(GenderEnum.FEMALE).hasToString("Weiblich");
    assertThat(GenderEnum.OTHER).hasToString("Divers");
    assertThat(GenderEnum.UNKNOWN).hasToString("Unbekannt");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(GenderEnum.values()).forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
