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

import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Extension;

@Slf4j
@RequiredArgsConstructor
final class ReceptionTimestampFactory implements Supplier<DateTimeHolder> {

  private final Bundle bundle;

  @Override
  public DateTimeHolder get() {
    try {
      return receptionTimestamp();
    } catch (Exception e) {
      log.warn("Failed to get reception timestamp. Using current timestamp. {}", this, e);
      return DateTimeHolder.now();
    }
  }

  private DateTimeHolder receptionTimestamp() {
    return toDateTimeHolder((DateTimeType) extension().getValue());
  }

  private Composition composition() {
    return (Composition) bundle.getEntryFirstRep().getResource();
  }

  private Extension extension() {
    return composition().getExtensionByUrl(DemisExtensions.EXTENSION_URL_RECEPTION_TIME_STAMP_TYPE);
  }

  private DateTimeHolder toDateTimeHolder(DateTimeType dateTime) {
    if (log.isDebugEnabled()) {
      log.debug(
          "Extracted reception timestamp from bundle. {} Timestamp: {}",
          this,
          dateTime.toHumanDisplayLocalTimezone());
    }
    return new DateTimeHolder(dateTime);
  }

  /**
   * Defensive printing because this is called in a catch block
   *
   * @return textual details of this object
   */
  @Override
  public String toString() {
    if (this.bundle == null) {
      return "Bundle: null";
    }
    String identifier = getNotificationIdentifier();
    if (identifier == null) {
      return "Bundle without notification bundle ID!";
    }
    return "Notification: " + identifier;
  }

  private String getNotificationIdentifier() {
    if (this.bundle.hasIdentifier()) {
      return this.bundle.getIdentifier().getValue();
    }
    return null;
  }
}
