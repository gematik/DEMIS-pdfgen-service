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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AuthenticationLevelOfAssuranceEnumTest {

  @Test
  void shouldCreateEnumCorrectly() {
    assertThat(
            AuthenticationLevelOfAssuranceEnum.of(
                AuthenticationLevelOfAssuranceEnum.LOW.provenanceValue))
        .isEqualTo(AuthenticationLevelOfAssuranceEnum.LOW);
    assertThat(
            AuthenticationLevelOfAssuranceEnum.of(
                AuthenticationLevelOfAssuranceEnum.SUBSTANTIAL.provenanceValue))
        .isEqualTo(AuthenticationLevelOfAssuranceEnum.SUBSTANTIAL);
    assertThat(
            AuthenticationLevelOfAssuranceEnum.of(
                AuthenticationLevelOfAssuranceEnum.HIGH.provenanceValue))
        .isEqualTo(AuthenticationLevelOfAssuranceEnum.HIGH);
    assertThat(
            AuthenticationLevelOfAssuranceEnum.of(
                AuthenticationLevelOfAssuranceEnum.UNKNOWN.provenanceValue))
        .isEqualTo(AuthenticationLevelOfAssuranceEnum.UNKNOWN);
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(AuthenticationLevelOfAssuranceEnum.LOW).hasToString("niedrig");
    assertThat(AuthenticationLevelOfAssuranceEnum.SUBSTANTIAL).hasToString("substanziell");
    assertThat(AuthenticationLevelOfAssuranceEnum.HIGH).hasToString("hoch");
    assertThat(AuthenticationLevelOfAssuranceEnum.UNKNOWN).hasToString("Unbekannt");
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(AuthenticationLevelOfAssuranceEnum.values())
        .forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
