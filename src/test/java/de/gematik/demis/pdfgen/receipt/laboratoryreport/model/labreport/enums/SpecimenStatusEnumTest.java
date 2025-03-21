package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums;

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
import org.hl7.fhir.r4.model.Specimen;
import org.junit.jupiter.api.Test;

class SpecimenStatusEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    SpecimenStatusEnum shouldBeAvailable = SpecimenStatusEnum.of(Specimen.SpecimenStatus.AVAILABLE);
    SpecimenStatusEnum shouldBeUnavailable =
        SpecimenStatusEnum.of(Specimen.SpecimenStatus.UNAVAILABLE);
    SpecimenStatusEnum shouldBeUnsatisfactory =
        SpecimenStatusEnum.of(Specimen.SpecimenStatus.UNSATISFACTORY);
    SpecimenStatusEnum shouldBeEnteredInError =
        SpecimenStatusEnum.of(Specimen.SpecimenStatus.ENTEREDINERROR);
    SpecimenStatusEnum shouldBeNull1 = SpecimenStatusEnum.of(Specimen.SpecimenStatus.NULL);
    SpecimenStatusEnum shouldBeNull2 = SpecimenStatusEnum.of(null);

    // then
    assertThat(shouldBeAvailable).isEqualTo(SpecimenStatusEnum.AVAILABLE);
    assertThat(shouldBeUnavailable).isEqualTo(SpecimenStatusEnum.UNAVAILABLE);
    assertThat(shouldBeUnsatisfactory).isEqualTo(SpecimenStatusEnum.UNSATISFACTORY);
    assertThat(shouldBeEnteredInError).isEqualTo(SpecimenStatusEnum.ENTERED_IN_ERROR);
    assertThat(shouldBeNull1).isNull();
    assertThat(shouldBeNull2).isNull();
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(SpecimenStatusEnum.AVAILABLE).hasToString("Verfügbar");
    assertThat(SpecimenStatusEnum.UNAVAILABLE).hasToString("Nicht verfügbar");
    assertThat(SpecimenStatusEnum.UNSATISFACTORY).hasToString("Mangelhaft");
    assertThat(SpecimenStatusEnum.ENTERED_IN_ERROR).hasToString("Irrtümlich eingegeben");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(SpecimenStatusEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
