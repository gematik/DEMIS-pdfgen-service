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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ca.uhn.fhir.context.FhirContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;

class NotificationTypeTest {

  @Test
  void shouldReturnLaboratoryType() throws IOException {

    String notificationString =
        Files.readString(
            Path.of("src/test/resources/bundles/laboratoryReport/LaboratoryReportBundleDv2.json"));
    Bundle bundle =
        FhirContext.forR4Cached().newJsonParser().parseResource(Bundle.class, notificationString);

    assertThat(NotificationType.getNotificationType(bundle))
        .isEqualTo(NotificationType.LABORATORY_NOTIFICATION);
  }

  @Test
  void shouldReturnDiseaseType() throws IOException {

    String notificationString =
        Files.readString(
            Path.of("src/test/resources/bundles/disease/DiseaseNotificationBundle.json"));
    Bundle bundle =
        FhirContext.forR4Cached().newJsonParser().parseResource(Bundle.class, notificationString);

    assertThat(NotificationType.getNotificationType(bundle))
        .isEqualTo(NotificationType.DISEASE_NOTIFICATION);
  }

  @Test
  void shouldReturnBedOccupancyType() throws IOException {

    String notificationString =
        Files.readString(
            Path.of("src/test/resources/bundles/bedOccupancy/BedOccupancyBundle.json"));
    Bundle bundle =
        FhirContext.forR4Cached().newJsonParser().parseResource(Bundle.class, notificationString);

    assertThat(NotificationType.getNotificationType(bundle))
        .isEqualTo(NotificationType.BED_OCCUPANCY_REPORT);
  }

  @Test
  void shouldThrowExceptionForUnknownType() throws IOException {

    String notificationString =
        Files.readString(
            Path.of("src/test/resources/bundles/unknown/UnknownNotificationBundle.json"));
    Bundle bundle =
        FhirContext.forR4Cached().newJsonParser().parseResource(Bundle.class, notificationString);

    assertThrows(
        IllegalArgumentException.class, () -> NotificationType.getNotificationType(bundle));
  }
}
