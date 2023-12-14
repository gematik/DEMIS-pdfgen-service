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

import static de.gematik.demis.pdfgen.utils.StringUtils.makeEnumNameHumanFriendly;

import de.gematik.demis.pdfgen.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.DiagnosticReport;

@Slf4j
@RequiredArgsConstructor
public enum LabReportStatusEnum {
  REGISTERED("enum.lab-report.status.registered"),
  PARTIAL("enum.lab-report.status.partial"),
  PRELIMINARY("enum.lab-report.status.preliminary"),
  FINAL("enum.lab-report.status.final"),
  AMENDED("enum.lab-report.status.amended"),
  CORRECTED("enum.lab-report.status.corrected"),
  APPENDED("enum.lab-report.status.appended"),
  CANCELLED("enum.lab-report.status.cancelled"),
  ENTERED_IN_ERROR("enum.lab-report.status.entered-in-error"),
  UNKNOWN("enum.lab-report.status.unknown");

  private final String messageKey;

  public static LabReportStatusEnum of(DiagnosticReport.DiagnosticReportStatus fhirStatus) {
    if (DiagnosticReport.DiagnosticReportStatus.REGISTERED == fhirStatus) {
      return REGISTERED;
    } else if (DiagnosticReport.DiagnosticReportStatus.PARTIAL == fhirStatus) {
      return PARTIAL;
    } else if (DiagnosticReport.DiagnosticReportStatus.PRELIMINARY == fhirStatus) {
      return PRELIMINARY;
    } else if (DiagnosticReport.DiagnosticReportStatus.FINAL == fhirStatus) {
      return FINAL;
    } else if (DiagnosticReport.DiagnosticReportStatus.AMENDED == fhirStatus) {
      return AMENDED;
    } else if (DiagnosticReport.DiagnosticReportStatus.CORRECTED == fhirStatus) {
      return CORRECTED;
    } else if (DiagnosticReport.DiagnosticReportStatus.APPENDED == fhirStatus) {
      return APPENDED;
    } else if (DiagnosticReport.DiagnosticReportStatus.CANCELLED == fhirStatus) {
      return CANCELLED;
    } else if (DiagnosticReport.DiagnosticReportStatus.ENTEREDINERROR == fhirStatus) {
      return ENTERED_IN_ERROR;
    } else if (DiagnosticReport.DiagnosticReportStatus.UNKNOWN == fhirStatus
        || DiagnosticReport.DiagnosticReportStatus.NULL == fhirStatus) {
      return UNKNOWN;
    } else {
      log.warn("Received unexpected DiagnosticReport.DiagnosticReportStatus value {}", fhirStatus);
      return UNKNOWN;
    }
  }

  @Override
  public String toString() {
    return MessageUtil.getOrDefault(messageKey, makeEnumNameHumanFriendly(name()));
  }
}
