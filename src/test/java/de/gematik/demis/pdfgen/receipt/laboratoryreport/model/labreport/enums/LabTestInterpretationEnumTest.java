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

package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class LabTestInterpretationEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    Optional<LabTestInterpretationEnum> shouldBePositive1 = LabTestInterpretationEnum.ofCode("pos");
    Optional<LabTestInterpretationEnum> shouldBePositive2 = LabTestInterpretationEnum.ofCode("POS");
    Optional<LabTestInterpretationEnum> shouldBeNegative1 = LabTestInterpretationEnum.ofCode("neg");
    Optional<LabTestInterpretationEnum> shouldBeNegative2 = LabTestInterpretationEnum.ofCode("NEG");
    Optional<LabTestInterpretationEnum> shouldBeIndeterminate1 =
        LabTestInterpretationEnum.ofCode("ind");
    Optional<LabTestInterpretationEnum> shouldBeIndeterminate2 =
        LabTestInterpretationEnum.ofCode("IND");
    Optional<LabTestInterpretationEnum> shouldBeEmpty =
        LabTestInterpretationEnum.ofCode("NotACode");

    // then
    assertThat(shouldBePositive1).isPresent();
    assertThat(shouldBePositive2).isPresent();
    assertThat(shouldBeNegative1).isPresent();
    assertThat(shouldBeNegative2).isPresent();
    assertThat(shouldBeIndeterminate1).isPresent();
    assertThat(shouldBeIndeterminate2).isPresent();
    assertThat(shouldBeEmpty).isEmpty();
    assertThat(shouldBePositive1).contains(LabTestInterpretationEnum.POSITIVE);
    assertThat(shouldBePositive2).contains(LabTestInterpretationEnum.POSITIVE);
    assertThat(shouldBeNegative1).contains(LabTestInterpretationEnum.NEGATIVE);
    assertThat(shouldBeNegative2).contains(LabTestInterpretationEnum.NEGATIVE);
    assertThat(shouldBeIndeterminate1).contains(LabTestInterpretationEnum.INDETERMINATE);
    assertThat(shouldBeIndeterminate2).contains(LabTestInterpretationEnum.INDETERMINATE);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(LabTestInterpretationEnum.POSITIVE).hasToString("Positiv");
    assertThat(LabTestInterpretationEnum.NEGATIVE).hasToString("Negativ");
    assertThat(LabTestInterpretationEnum.INDETERMINATE).hasToString("Unbestimmt");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(LabTestInterpretationEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
