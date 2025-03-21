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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.hl7.fhir.r4.model.Composition;
import org.junit.jupiter.api.Test;

class NotificationStatusEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    NotificationStatusEnum shouldBePreliminary =
        NotificationStatusEnum.of(Composition.CompositionStatus.PRELIMINARY);
    NotificationStatusEnum shouldBeFinal =
        NotificationStatusEnum.of(Composition.CompositionStatus.FINAL);
    NotificationStatusEnum shouldBeAmended =
        NotificationStatusEnum.of(Composition.CompositionStatus.AMENDED);
    NotificationStatusEnum shouldBeEnteredInError =
        NotificationStatusEnum.of(Composition.CompositionStatus.ENTEREDINERROR);
    NotificationStatusEnum shouldBeUnknown1 =
        NotificationStatusEnum.of(Composition.CompositionStatus.NULL);
    NotificationStatusEnum shouldBeUnknown2 = NotificationStatusEnum.of(null);

    // then
    assertThat(shouldBePreliminary).isEqualTo(NotificationStatusEnum.PRELIMINARY);
    assertThat(shouldBeFinal).isEqualTo(NotificationStatusEnum.FINAL);
    assertThat(shouldBeAmended).isEqualTo(NotificationStatusEnum.AMENDED);
    assertThat(shouldBeEnteredInError).isEqualTo(NotificationStatusEnum.ENTERED_IN_ERROR);
    assertThat(shouldBeUnknown1).isEqualTo(NotificationStatusEnum.UNKNOWN);
    assertThat(shouldBeUnknown2).isEqualTo(NotificationStatusEnum.UNKNOWN);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(NotificationStatusEnum.PRELIMINARY).hasToString("Vorläufig");
    assertThat(NotificationStatusEnum.FINAL).hasToString("Final");
    assertThat(NotificationStatusEnum.AMENDED).hasToString("Überarbeitet");
    assertThat(NotificationStatusEnum.ENTERED_IN_ERROR).hasToString("Irrtümlich eingegeben");
    assertThat(NotificationStatusEnum.UNKNOWN).hasToString("Unbekannt");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(NotificationStatusEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
