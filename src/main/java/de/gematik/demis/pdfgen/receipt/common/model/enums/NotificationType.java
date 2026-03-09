package de.gematik.demis.pdfgen.receipt.common.model.enums;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import org.hl7.fhir.r4.model.Bundle;

public enum NotificationType {
  LABORATORY_NOTIFICATION,
  DISEASE_NOTIFICATION,
  BED_OCCUPANCY_REPORT;

  private static final String PROFILE_BUNDLE_LABORATORY = "BundleLaboratory";
  private static final String PROFILE_BUNDLE_DISEASE = "BundleDisease";
  private static final String PROFILE_REPORT_BUNDLE = "ReportBundle";

  public static NotificationType getNotificationType(Bundle bundle) {

    String profileString = bundle.getMeta().getProfile().getFirst().getValue();
    if (profileString.contains(PROFILE_BUNDLE_LABORATORY)) {
      return NotificationType.LABORATORY_NOTIFICATION;
    }
    if (profileString.contains(PROFILE_BUNDLE_DISEASE)) {
      return NotificationType.DISEASE_NOTIFICATION;
    }

    if (profileString.contains(PROFILE_REPORT_BUNDLE)) {
      return NotificationType.BED_OCCUPANCY_REPORT;
    }

    throw new IllegalArgumentException("Unknown notification type for profile: " + profileString);
  }
}
