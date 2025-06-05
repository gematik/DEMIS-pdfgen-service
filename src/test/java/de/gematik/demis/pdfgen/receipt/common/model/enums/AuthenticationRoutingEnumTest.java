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

class AuthenticationRoutingEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    assertThat(AuthenticationRoutingEnum.of(AuthenticationRoutingEnum.PORTAL.provenanceValue))
        .isEqualTo(AuthenticationRoutingEnum.PORTAL);
    assertThat(AuthenticationRoutingEnum.of(AuthenticationRoutingEnum.INTERFACE.provenanceValue))
        .isEqualTo(AuthenticationRoutingEnum.INTERFACE);
    assertThat(AuthenticationRoutingEnum.of(AuthenticationRoutingEnum.UNKNOWN.provenanceValue))
        .isEqualTo(AuthenticationRoutingEnum.UNKNOWN);
    assertThat(AuthenticationRoutingEnum.of(AuthenticationRoutingEnum.INTERNET.provenanceValue))
        .isEqualTo(AuthenticationRoutingEnum.INTERNET);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(AuthenticationRoutingEnum.PORTAL).hasToString("Portal");
    assertThat(AuthenticationRoutingEnum.INTERFACE).hasToString("Schnittstelle");
    assertThat(AuthenticationRoutingEnum.UNKNOWN).hasToString("Unbekannt");
    assertThat(AuthenticationRoutingEnum.INTERNET).hasToString("Portal");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(AuthenticationRoutingEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
