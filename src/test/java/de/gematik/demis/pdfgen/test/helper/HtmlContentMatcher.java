package de.gematik.demis.pdfgen.test.helper;

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

import static de.gematik.demis.pdfgen.utils.StringUtils.concatenateWithDelimiter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper Class used for creating a regex pattern for matching against the content of the html file
 * used for generating the pdf receipt
 */
public class HtmlContentMatcher {
  private final String html;
  private final List<String> patterns = new ArrayList<>();

  public HtmlContentMatcher(String html) {
    this.html = html;
  }

  public HtmlContentMatcher addSectionTitle(String title) {
    String regex =
        "<th[^>]*colspan=\"2\"[^>]*>"
            + title
            + "</th>"; // match a spanning th tag containing exactly the title parameter
    patterns.add(regex);
    return this;
  }

  public HtmlContentMatcher addRow(String left, String right) {
    String regex =
        "<th[^>]*>"
            + left
            + "</th>"
            + // match a th tag containing exactly the left parameter
            "[\r\n\s]*"
            + // match white-space including line-break
            "<td[^>]*>"
            + right
            + "</td>"; // match a td tag containing exactly the right parameter
    patterns.add(regex);
    return this;
  }

  public HtmlContentMatcher addDateTimeRow(String left, DateTimeHolder right) {
    // manually convert date to string with format MM.DD.YYYY HH:MM
    String dateString =
        doubleDigit(right.getDateTime().getDay())
            + "."
            + doubleDigit(right.getDateTime().getMonth() + 1)
            + "."
            + right.getDateTime().getYear()
            + " "
            + doubleDigit(right.getDateTime().getHour())
            + ":"
            + doubleDigit(right.getDateTime().getMinute());
    return addRow(left, dateString);
  }

  public HtmlContentMatcher addDateRow(String left, DateTimeHolder right) {
    // manually convert date to string with format MM.DD.YYYY
    String dateString =
        doubleDigit(right.getDateTime().getDay())
            + "."
            + doubleDigit(right.getDateTime().getMonth() + 1)
            + "."
            + right.getDateTime().getYear();
    return addRow(left, dateString);
  }

  public HtmlContentMatcher addTelecomTitle(String title) {
    String regex = "<th[^>]*>" + title + "</th>";
    patterns.add(regex);
    return this;
  }

  public HtmlContentMatcher addTelecomEntry(String prefix, String value, String use) {
    String regex = prefix.trim() + " " + value.trim();
    if (isNotBlank(use)) {
      regex += " \\(" + use + "\\)";
    }
    patterns.add(regex);
    return this;
  }

  public void assertMatches() {
    String regex =
        "(?s)"
            + // make matches for any characters also allow line-breaks
            ".*"
            + // match any character
            concatenateWithDelimiter(".*", patterns)
            + // join regexes with any char between them
            ".*"; // match any character
    assertThat(html).matches(regex);
  }

  private String doubleDigit(int i) {
    return 0 <= i && i <= 9 ? "0" + i : String.valueOf(i);
  }
}
