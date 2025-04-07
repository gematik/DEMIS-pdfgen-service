package de.gematik.demis.pdfgen.utils;

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

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

  @Test
  void shouldHandleNullDelimiterGracefully() {
    // when
    var concatenated1 = StringUtils.concatenateWithDelimiter(null, (String) null);
    var concatenated2 = StringUtils.concatenateWithDelimiter(null, (List<String>) null);
    var concatenated3 = StringUtils.concatenateWithDelimiter(null, (Stream<String>) null);
    var concatenated4 = StringUtils.concatenateWithDelimiter(null, "a", "b", "c");
    var concatenated5 = StringUtils.concatenateWithDelimiter(null, List.of("a", "b", "c"));
    var concatenated6 = StringUtils.concatenateWithDelimiter(null, Stream.of("a", "b", "c"));

    // then
    assertThat(concatenated1).isEmpty();
    assertThat(concatenated2).isEmpty();
    assertThat(concatenated3).isEmpty();
    assertThat(concatenated4).isEqualTo("abc");
    assertThat(concatenated5).isEqualTo("abc");
    assertThat(concatenated6).isEqualTo("abc");
  }

  @Test
  void shouldHandleNullSequenceGracefully() {
    // when
    var concatenated1 = StringUtils.concatenateWithDelimiter("-", (String) null);
    var concatenated2 = StringUtils.concatenateWithDelimiter("-", (List<String>) null);
    var concatenated3 = StringUtils.concatenateWithDelimiter("-", (Stream<String>) null);

    // then
    assertThat(concatenated1).isEmpty();
    assertThat(concatenated2).isEmpty();
    assertThat(concatenated3).isEmpty();
  }

  @Test
  void shouldConcatenateWithEmptyDelimiter() {
    // when
    var concatenated1 = StringUtils.concatenateWithDelimiter("", "a", "b", "c");
    var concatenated2 = StringUtils.concatenateWithDelimiter("", List.of("a", "b", "c"));
    var concatenated3 = StringUtils.concatenateWithDelimiter("", Stream.of("a", "b", "c"));

    // then
    assertThat(concatenated1).isEqualTo("abc");
    assertThat(concatenated2).isEqualTo("abc");
    assertThat(concatenated3).isEqualTo("abc");
  }

  @Test
  void shouldConcatenateWithBlankDelimiter() {
    // when
    var concatenated1 = StringUtils.concatenateWithDelimiter(" ", "a", "b", "c");
    var concatenated2 = StringUtils.concatenateWithDelimiter(" ", List.of("a", "b", "c"));
    var concatenated3 = StringUtils.concatenateWithDelimiter(" ", Stream.of("a", "b", "c"));

    // then
    assertThat(concatenated1).isEqualTo("a b c");
    assertThat(concatenated2).isEqualTo("a b c");
    assertThat(concatenated3).isEqualTo("a b c");
  }

  @Test
  void shouldConcatenateWithNotBlankDelimiter() {
    // when
    var concatenated1 = StringUtils.concatenateWithDelimiter(", ", "a", "b", "c");
    var concatenated2 = StringUtils.concatenateWithDelimiter(", ", List.of("a", "b", "c"));
    var concatenated3 = StringUtils.concatenateWithDelimiter(", ", Stream.of("a", "b", "c"));

    // then
    assertThat(concatenated1).isEqualTo("a, b, c");
    assertThat(concatenated2).isEqualTo("a, b, c");
    assertThat(concatenated3).isEqualTo("a, b, c");
  }

  @Test
  void shouldConcatenateWithLineBreakDelimiter() {
    // when
    var concatenated1 = StringUtils.concatenateWithDelimiter("\n", "a", "b", "c");
    var concatenated2 = StringUtils.concatenateWithDelimiter("\n", List.of("a", "b", "c"));
    var concatenated3 = StringUtils.concatenateWithDelimiter("\n", Stream.of("a", "b", "c"));

    // then
    String expectedValue =
        """
                a
                b
                c""";
    assertThat(concatenated1).isEqualTo(expectedValue);
    assertThat(concatenated2).isEqualTo(expectedValue);
    assertThat(concatenated3).isEqualTo(expectedValue);
  }
}
