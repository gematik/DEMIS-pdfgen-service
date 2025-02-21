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

package de.gematik.demis.pdfgen.receipt.diseasenotification.util;

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

import org.junit.jupiter.api.Test;

class TemplateUtilsTest {

  @Test
  void isTrue_shouldHandleBlankGracefully() {
    assertThat(TemplateUtils.isTrue(null)).isFalse();
    assertThat(TemplateUtils.isTrue("")).isFalse();
    assertThat(TemplateUtils.isTrue("   ")).isFalse();
  }

  @Test
  void isTrue_shouldAcceptTrueVariations() {
    assertThat(TemplateUtils.isTrue("true")).isTrue();
    assertThat(TemplateUtils.isTrue("True")).isTrue();
    assertThat(TemplateUtils.isTrue("TRUE")).isTrue();
    assertThat(TemplateUtils.isTrue("JA")).isTrue();
    assertThat(TemplateUtils.isTrue("Ja")).isTrue();
    assertThat(TemplateUtils.isTrue("ja")).isTrue();
    assertThat(TemplateUtils.isTrue("YES")).isTrue();
    assertThat(TemplateUtils.isTrue("Yes")).isTrue();
    assertThat(TemplateUtils.isTrue("yes")).isTrue();
  }

  @Test
  void isTrue_shouldRejectNonTrueVariations() {
    assertThat(TemplateUtils.isTrue("false")).isFalse();
    assertThat(TemplateUtils.isTrue("False")).isFalse();
    assertThat(TemplateUtils.isTrue("FALSE")).isFalse();
    assertThat(TemplateUtils.isTrue("NEIN")).isFalse();
    assertThat(TemplateUtils.isTrue("Nein")).isFalse();
    assertThat(TemplateUtils.isTrue("nein")).isFalse();
    assertThat(TemplateUtils.isTrue("NO")).isFalse();
    assertThat(TemplateUtils.isTrue("No")).isFalse();
    assertThat(TemplateUtils.isTrue("no")).isFalse();
    assertThat(TemplateUtils.isTrue("anything that is not true")).isFalse();
  }
}
