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

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.capitalize;

import java.util.Collection;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {
  public static final String LINE_BREAK = "\n";
  public static final String SPACE = " ";
  public static final String LIST_DELIMITER = ", ";

  public static String makeEnumNameHumanFriendly(String value) {
    return capitalize(value.toLowerCase().replace("_", " "));
  }

  public static String concatenateWithDelimiter(String delimiter, String... entries) {
    if (entries == null || entries.length == 0) {
      return "";
    }
    return concatenateWithDelimiter(delimiter, Stream.of(entries));
  }

  public static String concatenateWithDelimiter(String delimiter, Collection<String> entries) {
    if (entries == null || entries.isEmpty()) {
      return "";
    }
    return concatenateWithDelimiter(delimiter, entries.stream());
  }

  public static String concatenateWithDelimiter(String delimiter, Stream<String> entryStream) {
    if (entryStream == null) {
      return "";
    }
    String delimiterToUse = delimiter == null ? "" : delimiter;
    return entryStream
        .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
        .map(String::trim)
        .collect(joining(delimiterToUse));
  }
}
