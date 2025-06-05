package de.gematik.demis.pdfgen.receipt.bedoccupancy.model;

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

import de.gematik.demis.pdfgen.fhir.extract.NotifierFhirQueries;
import de.gematik.demis.pdfgen.fhir.extract.QuestionnaireFhirQueries;
import de.gematik.demis.pdfgen.receipt.common.model.section.AuthenticationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.MetadataFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.Notification;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotificationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.common.service.watermark.WatermarkService;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BedOccupancyFactory {

  private final NotificationFactory notificationFactory;
  private final QuestionnaireFhirQueries questionnaireFhirQueries;
  private final NotifierFhirQueries notifierFhirQueries;
  private final OrganizationFactory organizationFactory;
  private final AuthenticationFactory authenticationFactory;
  private final WatermarkService watermarkService;

  private static void setMetaData(Bundle bundle, BedOccupancy.BedOccupancyBuilder bedOccupancy) {
    bedOccupancy.metadata(MetadataFactory.create(bundle));
  }

  /**
   * Create bed occupancy report data object
   *
   * @param bundle FHIR message
   * @return bed occupancy report data
   */
  public BedOccupancy create(Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    BedOccupancy.BedOccupancyBuilder bedOccupancy = BedOccupancy.builder();
    setMetaData(bundle, bedOccupancy);
    setNotificationId(bundle, bedOccupancy);
    setOrganization(bundle, bedOccupancy);
    setBedStatistics(bundle, bedOccupancy);
    setAuthentication(bundle, bedOccupancy);
    setWatermark(bedOccupancy);
    return bedOccupancy.build();
  }

  private void setBedStatistics(Bundle bundle, BedOccupancy.BedOccupancyBuilder bedOccupancy) {
    final Map<String, String> answers = getQuestionnaireAnswers(bundle);
    bedOccupancy.numberOccupiedBedsGeneralWardAdults(
        answers.get("numberOccupiedBedsGeneralWardAdults"));
    bedOccupancy.numberOccupiedBedsGeneralWardChildren(
        answers.get("numberOccupiedBedsGeneralWardChildren"));
    bedOccupancy.numberOperableBedsGeneralWardAdults(
        answers.get("numberOperableBedsGeneralWardAdults"));
    bedOccupancy.numberOperableBedsGeneralWardChildren(
        answers.get("numberOperableBedsGeneralWardChildren"));
  }

  private void setOrganization(Bundle bundle, BedOccupancy.BedOccupancyBuilder bedOccupancy) {
    bedOccupancy.organization(
        this.organizationFactory.create(this.notifierFhirQueries.getFhirNotifierFacility(bundle)));
  }

  private void setNotificationId(Bundle bundle, BedOccupancy.BedOccupancyBuilder bedOccupancy) {
    final Notification notificationDetails = notificationFactory.create(bundle);
    if (notificationDetails != null) {
      bedOccupancy.notificationId(notificationDetails.getIdentifier());
    }
  }

  private void setAuthentication(Bundle bundle, BedOccupancy.BedOccupancyBuilder bedOccupancy) {
    bedOccupancy.authentication(this.authenticationFactory.create(bundle));
  }

  private Map<String, String> getQuestionnaireAnswers(Bundle bundle) {
    Optional<QuestionnaireResponse> responseOptional =
        this.questionnaireFhirQueries.getBedOccupancyQuestionnaireResponse(bundle);
    return this.questionnaireFhirQueries.getQuestionnaireAnswers(responseOptional);
  }

  private void setWatermark(BedOccupancy.BedOccupancyBuilder bedOccupancy) {
    watermarkService.getWatermarkBase64Image().ifPresent(bedOccupancy::watermarkBase64Image);
  }
}
