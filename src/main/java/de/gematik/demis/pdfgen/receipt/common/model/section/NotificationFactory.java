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
 * #L%
 */

import de.gematik.demis.pdfgen.fhir.extract.NotificationFhirQueries;
import de.gematik.demis.pdfgen.receipt.common.model.enums.NotificationStatusEnum;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationFactory {
  private final NotificationFhirQueries notificationFhirQueries;

  @Nullable
  public Notification create(final Bundle bundle) {
    Optional<Composition> fhirNotificationOptional =
        notificationFhirQueries.getNotification(bundle);
    if (fhirNotificationOptional.isEmpty()) {
      return null;
    }
    Composition fhirNotification = fhirNotificationOptional.get();
    Notification.NotificationBuilder notification = Notification.builder();
    setIdentifier(fhirNotification, notification);
    setStatus(fhirNotification, notification);
    setDateTime(fhirNotification, notification);
    setRelations(fhirNotification, notification);
    return notification.build();
  }

  private static void setIdentifier(
      Composition fhirNotification, Notification.NotificationBuilder notification) {
    final String identifier =
        fhirNotification.hasIdentifier() ? fhirNotification.getIdentifier().getValue() : null;
    notification.identifier(identifier);
  }

  private static void setStatus(
      Composition fhirNotification, Notification.NotificationBuilder notification) {
    final NotificationStatusEnum status =
        fhirNotification.hasStatus()
            ? NotificationStatusEnum.of(fhirNotification.getStatus())
            : null;
    notification.status(status);
  }

  private static void setDateTime(
      Composition fhirNotification, Notification.NotificationBuilder notification) {
    notification.dateTime(new DateTimeHolder(fhirNotification.getDateElement()));
  }

  private static void setRelations(
      Composition fhirNotification, Notification.NotificationBuilder notification) {
    notification.relations(
        fhirNotification.getRelatesTo().stream().map(NotificationFactory::createRelation).toList());
  }

  private static String createRelation(Composition.CompositionRelatesToComponent component) {
    String value = "";
    // exclude "appends" codes from Relations
    if (component.hasCode() && !component.getCode().toCode().contains("appends")) {
      value += component.getCode().toCode() + " ";
    }
    if (component.hasTargetIdentifier()) {
      value += component.getTargetIdentifier().getValue();
    } else if (component.hasTargetReference() && component.getTargetReference().hasIdentifier()) {
      value += component.getTargetReference().getIdentifier().getValue();
    }
    return value;
  }
}
