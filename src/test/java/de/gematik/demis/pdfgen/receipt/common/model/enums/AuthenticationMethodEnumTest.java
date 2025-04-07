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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AuthenticationMethodEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    assertThat(AuthenticationMethodEnum.of(AuthenticationMethodEnum.BUNDID.provenanceValue))
        .isEqualTo(AuthenticationMethodEnum.BUNDID);
    assertThat(AuthenticationMethodEnum.of(AuthenticationMethodEnum.AUTHENTICATOR.provenanceValue))
        .isEqualTo(AuthenticationMethodEnum.AUTHENTICATOR);
    assertThat(AuthenticationMethodEnum.of(AuthenticationMethodEnum.CERTIFICATE.provenanceValue))
        .isEqualTo(AuthenticationMethodEnum.CERTIFICATE);
    assertThat(AuthenticationMethodEnum.of(AuthenticationMethodEnum.UNKNOWN.provenanceValue))
        .isEqualTo(AuthenticationMethodEnum.UNKNOWN);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(AuthenticationMethodEnum.BUNDID).hasToString("BundID");
    assertThat(AuthenticationMethodEnum.AUTHENTICATOR).hasToString("Authenticator");
    assertThat(AuthenticationMethodEnum.CERTIFICATE).hasToString("DEMIS-Zertifikat");
    assertThat(AuthenticationMethodEnum.UNKNOWN).hasToString("Unbekannt");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(AuthenticationMethodEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
