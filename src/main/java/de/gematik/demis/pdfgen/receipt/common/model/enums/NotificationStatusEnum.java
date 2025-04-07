package de.gematik.demis.pdfgen.receipt.common.model.enums;

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

import static de.gematik.demis.pdfgen.utils.StringUtils.makeEnumNameHumanFriendly;

import de.gematik.demis.pdfgen.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Composition;

@Slf4j
@RequiredArgsConstructor
public enum NotificationStatusEnum {
  PRELIMINARY("enum.notification.status.preliminary"),
  FINAL("enum.notification.status.final"),
  AMENDED("enum.notification.status.amended"),
  ENTERED_IN_ERROR("enum.notification.status.entered-in-error"),
  UNKNOWN("enum.notification.status.unknown");

  private final String messageKey;

  public static NotificationStatusEnum of(Composition.CompositionStatus fhirStatus) {
    if (Composition.CompositionStatus.PRELIMINARY == fhirStatus) {
      return PRELIMINARY;
    } else if (Composition.CompositionStatus.FINAL == fhirStatus) {
      return FINAL;
    } else if (Composition.CompositionStatus.AMENDED == fhirStatus) {
      return AMENDED;
    } else if (Composition.CompositionStatus.ENTEREDINERROR == fhirStatus) {
      return ENTERED_IN_ERROR;
    } else if (Composition.CompositionStatus.NULL == fhirStatus) {
      return UNKNOWN;
    } else {
      log.warn("Received unexpected Composition.CompositionStatus value {}", fhirStatus);
      return UNKNOWN;
    }
  }

  @Override
  public String toString() {
    return MessageUtil.getOrDefault(messageKey, makeEnumNameHumanFriendly(name()));
  }
}
