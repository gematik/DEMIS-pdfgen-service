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
 * #L%
 */

import static de.gematik.demis.pdfgen.utils.StringUtils.makeEnumNameHumanFriendly;

import de.gematik.demis.pdfgen.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;

@RequiredArgsConstructor
public enum TelecomTypeEnum {
  PHONE("enum.telecom.type.prefix.phone"),
  FAX("enum.telecom.type.prefix.fax"),
  EMAIL("enum.telecom.type.prefix.email"),
  URL("enum.telecom.type.prefix.url"),
  UNKNOWN("enum.telecom.type.prefix.unknown");

  private final String prefixMessageKey;

  public static TelecomTypeEnum of(ContactPointSystem system) {
    if (ContactPointSystem.PHONE == system) {
      return PHONE;
    } else if (ContactPointSystem.FAX == system) {
      return FAX;
    } else if (ContactPointSystem.EMAIL == system) {
      return EMAIL;
    } else if (ContactPointSystem.URL == system) {
      return URL;
    } else {
      return UNKNOWN;
    }
  }

  public String getPrefix() {
    return MessageUtil.getOrDefault(prefixMessageKey, makeEnumNameHumanFriendly(name())).trim();
  }
}
