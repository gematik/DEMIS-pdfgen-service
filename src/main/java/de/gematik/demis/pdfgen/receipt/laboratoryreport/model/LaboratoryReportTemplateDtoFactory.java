package de.gematik.demis.pdfgen.receipt.laboratoryreport.model;

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

import de.gematik.demis.pdfgen.receipt.common.model.section.AuthenticationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.MetadataFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotificationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifierFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.RecipientFactory;
import de.gematik.demis.pdfgen.receipt.common.service.qr.QrGenerator;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.LabReportFactory;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.submitter.SubmitterFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LaboratoryReportTemplateDtoFactory {
  private final NotificationFactory notificationFactory;
  private final RecipientFactory recipientFactory;
  private final NotifierFactory notifierFactory;
  private final NotifiedPersonFactory notifiedPersonFactory;
  private final SubmitterFactory submitterFactory;
  private final LabReportFactory labReportFactory;
  private final AuthenticationFactory authenticationFactory;
  private final QrGenerator qrGenerator;

  public LaboratoryReportTemplateDto create(final Bundle bundle, final boolean qrCodeOnLastPage) {
    Optional<String> notificationIdOptional =
        bundle.getEntry().stream()
            .map(BundleEntryComponent::getResource)
            .filter(Composition.class::isInstance)
            .map(Composition.class::cast)
            .filter(Composition::hasIdentifier)
            .map(Composition::getIdentifier)
            .filter(Identifier::hasValue)
            .map(Identifier::getValue)
            .findFirst();
    if (notificationIdOptional.isEmpty()) {
      log.error("Notifications has no NotificationId");
    }
    String qrCode =
        qrGenerator.generateQrCodeAsBase64(
            notificationIdOptional.orElseGet(() -> "Keine NotificationId vorhanden"));

    return LaboratoryReportTemplateDto.builder()
        .metadata(MetadataFactory.create(bundle))
        .notification(notificationFactory.create(bundle))
        .qrCode(qrCode)
        .recipient(recipientFactory.create(bundle))
        .notifier(notifierFactory.create(bundle))
        .notifiedPersonDTO(notifiedPersonFactory.create(bundle))
        .submitter(submitterFactory.create(bundle))
        .labReport(labReportFactory.create(bundle))
        .authentication(authenticationFactory.create(bundle))
        .qrCodeOnLastPage(qrCodeOnLastPage)
        .build();
  }
}
