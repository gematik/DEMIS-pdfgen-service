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
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.ContactPoint;

@RequiredArgsConstructor
public enum TelecomUseEnum {
  HOME("enum.telecom.use.home"),
  MOBILE("enum.telecom.use.mobile"),
  OLD("enum.telecom.use.old"),
  TEMP("enum.telecom.use.temp"),
  WORK("enum.telecom.use.work");

  private final String messageKey;

  @Nullable
  public static TelecomUseEnum of(ContactPoint.ContactPointUse contactPointUse) {
    if (ContactPoint.ContactPointUse.HOME == contactPointUse) {
      return HOME;
    } else if (ContactPoint.ContactPointUse.MOBILE == contactPointUse) {
      return MOBILE;
    } else if (ContactPoint.ContactPointUse.OLD == contactPointUse) {
      return OLD;
    } else if (ContactPoint.ContactPointUse.TEMP == contactPointUse) {
      return TEMP;
    } else if (ContactPoint.ContactPointUse.WORK == contactPointUse) {
      return WORK;
    } else {
      return null;
    }
  }

  @Override
  public String toString() {
    return MessageUtil.getOrDefault(messageKey, makeEnumNameHumanFriendly(name()));
  }
}
