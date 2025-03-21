package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport;

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

import static de.gematik.demis.pdfgen.fhir.extract.ExtensionQueries.resolve;

import de.gematik.demis.pdfgen.fhir.extract.LaboratoryFhirQueries;
import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums.LabReportStatusEnum;
import de.gematik.demis.pdfgen.translation.TranslationService;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabReportFactory {
  private final LaboratoryFhirQueries laboratoryFhirQueries;
  private final LabTestFactory labTestFactory;
  private final TranslationService displayTranslationService;

  @Nullable
  public LabReport create(final Bundle bundle) {
    Optional<DiagnosticReport> laboratoryReportOptional =
        laboratoryFhirQueries.getLaboratoryReport(bundle);

    DateTimeHolder issued = null;
    LabReportStatusEnum status = null;
    String code = null;
    String conclusionCode = null;
    String reasonForTesting = null;
    String conclusion = null;
    String labRequestId = null;
    String shortCode = null;

    if (laboratoryReportOptional.isPresent()) {
      DiagnosticReport laboratoryReport = laboratoryReportOptional.get();
      issued =
          laboratoryReport.hasIssuedElement()
              ? new DateTimeHolder(laboratoryReport.getIssuedElement())
              : null;
      status = LabReportStatusEnum.of(laboratoryReport.getStatus());
      code = displayTranslationService.resolveCodeableConceptValues(laboratoryReport.getCode());
      shortCode = laboratoryReport.getCode().getCodingFirstRep().getCode();
      if (laboratoryReport.hasExtension(DemisExtensions.EXTENSION_URL_REASON_FOR_TEST)) {
        reasonForTesting =
            displayTranslationService.resolveCodeableConceptValues(
                getReasonForTesting(laboratoryReport));
      }
      conclusionCode =
          displayTranslationService.resolveCodeableConceptValues(
              laboratoryReport.getConclusionCodeFirstRep());
      conclusion = laboratoryReport.getConclusion();
      labRequestId = laboratoryReport.getBasedOnFirstRep().getIdentifier().getValue();
    }

    List<LabTest> labTests = labTestFactory.createLabTests(bundle);

    LabReport.LabReportBuilder builder =
        LabReport.builder()
            .issued(issued)
            .status(status)
            .conclusion(conclusion)
            .reasonForTesting(reasonForTesting)
            .code(code)
            .notificationCategoryShortCode(shortCode)
            .conclusionCode(conclusionCode)
            .labRequestId(labRequestId)
            .labTests(labTests);

    return builder.build();
  }

  protected Coding getReasonForTesting(DiagnosticReport diagnosticReport) {
    final var reasonCode =
        diagnosticReport.getExtensionsByUrl(DemisExtensions.EXTENSION_URL_REASON_FOR_TEST).stream()
            .map(extension -> resolve(extension, CodeableConcept.class))
            .filter(Objects::nonNull)
            .findFirst();

    if (reasonCode.isEmpty() || reasonCode.get().isEmpty()) {
      return null;
    }
    return reasonCode.get().getCodingFirstRep();
  }
}
