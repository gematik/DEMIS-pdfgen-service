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
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum for Authentication Method. It is used to determine the provenance method value from the
 * authentication.
 */
@Slf4j
@RequiredArgsConstructor
public enum AuthenticationMethodEnum {
  BUNDID("enum.authentication.method.bundid", "https://demis.rki.de/fhir/sid/BundIdBPK2"),
  AUTHENTICATOR(
      "enum.authentication.method.authenticator", "https://gematik.de/fhir/sid/telematik-id"),
  CERTIFICATE(
      "enum.authentication.method.certificate",
      "https://demis.rki.de/fhir/NamingSystem/DemisParticipantId"),
  UNKNOWN("enum.authentication.method.unknown", "");

  public final String messageKey;
  public final String provenanceValue;

  public static AuthenticationMethodEnum of(final String givenProvenanceValue) {
    return Optional.ofNullable(givenProvenanceValue)
        .flatMap(n -> Arrays.stream(values()).filter(e -> e.provenanceValue.equals(n)).findFirst())
        .orElse(UNKNOWN);
  }

  @Override
  public String toString() {
    return MessageUtil.getOrDefault(messageKey, makeEnumNameHumanFriendly(name()));
  }
}
