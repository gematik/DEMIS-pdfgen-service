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
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.junit.jupiter.api.Test;

class LabReportStatusEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    // given
    LabReportStatusEnum shouldBeRegistered =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.REGISTERED);
    LabReportStatusEnum shouldBePartial =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.PARTIAL);
    LabReportStatusEnum shouldBePreliminary =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.PRELIMINARY);
    LabReportStatusEnum shouldBeFinal =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.FINAL);
    LabReportStatusEnum shouldBeAmended =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.AMENDED);
    LabReportStatusEnum shouldBeCorrected =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.CORRECTED);
    LabReportStatusEnum shouldBeAppended =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.APPENDED);
    LabReportStatusEnum shouldBeCanceled =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.CANCELLED);
    LabReportStatusEnum shouldBeEnteredInError =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.ENTEREDINERROR);
    LabReportStatusEnum shouldBeUnknown1 =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.UNKNOWN);
    LabReportStatusEnum shouldBeUnknown2 =
        LabReportStatusEnum.of(DiagnosticReport.DiagnosticReportStatus.NULL);
    LabReportStatusEnum shouldBeUnknown3 = LabReportStatusEnum.of(null);

    // then
    assertThat(shouldBeRegistered).isEqualTo(LabReportStatusEnum.REGISTERED);
    assertThat(shouldBePartial).isEqualTo(LabReportStatusEnum.PARTIAL);
    assertThat(shouldBePreliminary).isEqualTo(LabReportStatusEnum.PRELIMINARY);
    assertThat(shouldBeFinal).isEqualTo(LabReportStatusEnum.FINAL);
    assertThat(shouldBeAmended).isEqualTo(LabReportStatusEnum.AMENDED);
    assertThat(shouldBeCorrected).isEqualTo(LabReportStatusEnum.CORRECTED);
    assertThat(shouldBeAppended).isEqualTo(LabReportStatusEnum.APPENDED);
    assertThat(shouldBeCanceled).isEqualTo(LabReportStatusEnum.CANCELLED);
    assertThat(shouldBeEnteredInError).isEqualTo(LabReportStatusEnum.ENTERED_IN_ERROR);
    assertThat(shouldBeUnknown1).isEqualTo(LabReportStatusEnum.UNKNOWN);
    assertThat(shouldBeUnknown2).isEqualTo(LabReportStatusEnum.UNKNOWN);
    assertThat(shouldBeUnknown3).isEqualTo(LabReportStatusEnum.UNKNOWN);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(LabReportStatusEnum.REGISTERED).hasToString("Registriert");
    assertThat(LabReportStatusEnum.PARTIAL).hasToString("Unvollständig");
    assertThat(LabReportStatusEnum.PRELIMINARY).hasToString("Vorläufig");
    assertThat(LabReportStatusEnum.FINAL).hasToString("Final");
    assertThat(LabReportStatusEnum.AMENDED).hasToString("Überarbeitet");
    assertThat(LabReportStatusEnum.CORRECTED).hasToString("Korrigiert");
    assertThat(LabReportStatusEnum.APPENDED).hasToString("Hinzugefügt");
    assertThat(LabReportStatusEnum.CANCELLED).hasToString("Abgebrochen");
    assertThat(LabReportStatusEnum.ENTERED_IN_ERROR).hasToString("Irrtümlich eingegeben");
    assertThat(LabReportStatusEnum.UNKNOWN).hasToString("Unbekannt");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(LabReportStatusEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
