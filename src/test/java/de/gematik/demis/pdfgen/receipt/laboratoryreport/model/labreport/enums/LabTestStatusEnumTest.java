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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.Test;

class LabTestStatusEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    LabTestStatusEnum shouldBeRegistered =
        LabTestStatusEnum.of(Observation.ObservationStatus.REGISTERED);
    LabTestStatusEnum shouldBePreliminary =
        LabTestStatusEnum.of(Observation.ObservationStatus.PRELIMINARY);
    LabTestStatusEnum shouldBeFinal = LabTestStatusEnum.of(Observation.ObservationStatus.FINAL);
    LabTestStatusEnum shouldBeAmended = LabTestStatusEnum.of(Observation.ObservationStatus.AMENDED);
    LabTestStatusEnum shouldBeCorrected =
        LabTestStatusEnum.of(Observation.ObservationStatus.CORRECTED);
    LabTestStatusEnum shouldBeCanceled =
        LabTestStatusEnum.of(Observation.ObservationStatus.CANCELLED);
    LabTestStatusEnum shouldBeEnteredInError =
        LabTestStatusEnum.of(Observation.ObservationStatus.ENTEREDINERROR);
    LabTestStatusEnum shouldBeUnknown1 =
        LabTestStatusEnum.of(Observation.ObservationStatus.UNKNOWN);
    LabTestStatusEnum shouldBeUnknown2 = LabTestStatusEnum.of(Observation.ObservationStatus.NULL);
    LabTestStatusEnum shouldBeUnknown3 = LabTestStatusEnum.of(null);

    // then
    assertThat(shouldBeRegistered).isEqualTo(LabTestStatusEnum.REGISTERED);
    assertThat(shouldBePreliminary).isEqualTo(LabTestStatusEnum.PRELIMINARY);
    assertThat(shouldBeFinal).isEqualTo(LabTestStatusEnum.FINAL);
    assertThat(shouldBeAmended).isEqualTo(LabTestStatusEnum.AMENDED);
    assertThat(shouldBeCorrected).isEqualTo(LabTestStatusEnum.CORRECTED);
    assertThat(shouldBeCanceled).isEqualTo(LabTestStatusEnum.CANCELLED);
    assertThat(shouldBeEnteredInError).isEqualTo(LabTestStatusEnum.ENTERED_IN_ERROR);
    assertThat(shouldBeUnknown1).isEqualTo(LabTestStatusEnum.UNKNOWN);
    assertThat(shouldBeUnknown2).isEqualTo(LabTestStatusEnum.UNKNOWN);
    assertThat(shouldBeUnknown3).isEqualTo(LabTestStatusEnum.UNKNOWN);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(LabTestStatusEnum.REGISTERED).hasToString("Registriert");
    assertThat(LabTestStatusEnum.PRELIMINARY).hasToString("Vorläufig");
    assertThat(LabTestStatusEnum.FINAL).hasToString("Final");
    assertThat(LabTestStatusEnum.AMENDED).hasToString("Überarbeitet");
    assertThat(LabTestStatusEnum.CORRECTED).hasToString("Korrigiert");
    assertThat(LabTestStatusEnum.CANCELLED).hasToString("Abgebrochen");
    assertThat(LabTestStatusEnum.ENTERED_IN_ERROR).hasToString("Irrtümlich eingegeben");
    assertThat(LabTestStatusEnum.UNKNOWN).hasToString("Unbekannt");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(LabTestStatusEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
