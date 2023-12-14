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

package de.gematik.demis.pdfgen.receipt.common.model.subsection;

import static org.apache.commons.lang3.StringUtils.isBlank;

import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Telecom {
  private static final String HTTP_SCHEME = "http://";
  private static final String HTTPS_SCHEME = "https://";
  private TelecomTypeEnum system;
  private TelecomUseEnum use;
  private String value;

  public String asSingleLine() {
    if (value == null || isBlank(value)) {
      return "";
    }
    String valueToUse;
    if (system == TelecomTypeEnum.URL) {
      valueToUse =
          value.startsWith(HTTP_SCHEME) || value.startsWith(HTTPS_SCHEME)
              ? value
              : HTTPS_SCHEME + value;
    } else {
      valueToUse = value;
    }

    String combinedString = "";
    if (system != null) {
      combinedString += system.getPrefix().trim() + " ";
    }
    combinedString += valueToUse.trim();
    if (use != null) {
      combinedString += " (" + use + ")";
    }
    return combinedString;
  }
}
