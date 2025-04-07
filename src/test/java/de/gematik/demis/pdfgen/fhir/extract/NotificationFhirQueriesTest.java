package de.gematik.demis.pdfgen.fhir.extract;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createBedOccupancyBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createEmptyBundle;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.junit.jupiter.api.Test;

class NotificationFhirQueriesTest {

  private final Bundle bedOccupancyBundle = createBedOccupancyBundle();
  private final Bundle emptyBundle = createEmptyBundle();
  private final NotificationFhirQueries notificationFhirQueries = new NotificationFhirQueries();

  @Test
  void getNotification_shouldHandleNullGracefully() {
    assertThat(notificationFhirQueries.getNotification(null)).isEmpty();
  }

  @Test
  void getNotification_shouldNotLoadNotificationWhenNoneExist() {
    // when
    Optional<Composition> notificationOptional =
        notificationFhirQueries.getNotification(emptyBundle);
    // then
    assertThat(notificationOptional).isEmpty();
  }

  @Test
  void getNotification_shouldLoadNotification() {
    assertThat(notificationFhirQueries.getNotification(bedOccupancyBundle))
        .isPresent()
        .get()
        .extracting("Id")
        .isEqualTo("https://demis.rki.de/fhir/Composition/1b22c8d8-f1cb-311a-991a-8b488979af8e");
  }
}
