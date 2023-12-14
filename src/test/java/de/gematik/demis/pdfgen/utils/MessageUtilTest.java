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

package de.gematik.demis.pdfgen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessageUtilTest {

  @Test
  void getOrDefault_shouldHandleNullGracefully() {
    // when
    String value1 = MessageUtil.getOrDefault(null, "defaultValue");
    String value2 = MessageUtil.getOrDefault("", "defaultValue");
    String value3 = MessageUtil.getOrDefault("key", null);
    String value4 = MessageUtil.getOrDefault(null, null);
    // then
    assertThat(value1).isEqualTo("defaultValue");
    assertThat(value2).isEqualTo("defaultValue");
    assertThat(value3).isNull();
    assertThat(value4).isNull();
  }

  @Test
  void getOrDefault_shouldReturnDefaultValue() {
    // when
    String value = MessageUtil.getOrDefault("notAKey", "defaultValue");
    // then
    assertThat(value).isEqualTo("defaultValue");
  }
}
