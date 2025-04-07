package de.gematik.demis.pdfgen.receipt.common.model.section;

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
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.NotificationStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotificationFactoryIntegrationTest {

  @Autowired private NotificationFactory notificationFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(notificationFactory.create(null)).isNull();
  }

  @Test
  void create_shouldCreateNotificationFromBedOccupancyBundle() {
    // given
    Notification notification = notificationFactory.create(createBedOccupancyBundle());

    // then
    assertThat(notification).isNotNull();
    assertThat(notification.getIdentifier()).isEqualTo("5e1e89ce-7a44-4ec1-801c-0f988992e8fe");
    assertThat(notification.getStatus()).isEqualTo(NotificationStatusEnum.FINAL);
    assertThat(notification.getDateTime()).hasToString("20.11.2021 17:50");
    assertThat(notification.getRelations()).isEmpty();
  }

  @Test
  void create_shouldCreateNotificationFromLabReportBundle() {
    // given
    Notification notification = notificationFactory.create(createLaboratoryReportBundle());

    // then
    assertThat(notification).isNotNull();
    assertThat(notification.getIdentifier()).isEqualTo("e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9");
    assertThat(notification.getStatus()).isEqualTo(NotificationStatusEnum.FINAL);
    assertThat(notification.getDateTime()).hasToString("04.03.2021 20:16");
    assertThat(notification.getRelations()).hasSize(1);
    assertThat(notification.getRelations().getFirst()).isEqualTo("ABC123");
  }
}
