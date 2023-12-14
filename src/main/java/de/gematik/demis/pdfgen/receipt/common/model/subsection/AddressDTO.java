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

import static de.gematik.demis.pdfgen.utils.StringUtils.LIST_DELIMITER;
import static de.gematik.demis.pdfgen.utils.StringUtils.concatenateWithDelimiter;
import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class AddressDTO {
  private String department;
  private String organization;
  private String line;
  private String postalCode;
  private String city;
  private String country;
  @Setter private String use;

  /** DEMIS address use flagged: <code>primary</code> */
  private boolean primaryAddress;

  public String getFullNameAndAddressAsSingleLine() {
    return getFullNameAndAddress();
  }

  public String getFullAddressAsSingleLine() {
    return getFullAddress();
  }

  public String getAddressUseWithPrefix(String prefix) {
    if (isBlank(prefix)) {
      return isBlank(use) ? "" : "(" + use + ")";
    } else {
      return isBlank(use) ? prefix : prefix + " (" + use + ")";
    }
  }

  private String getFullNameAndAddress() {
    String postalCodeAndCity = concatenateWithDelimiter(" ", postalCode, city);
    return concatenateWithDelimiter(
        LIST_DELIMITER, department, organization, line, postalCodeAndCity, country);
  }

  private String getFullAddress() {
    String postalCodeAndCity = concatenateWithDelimiter(" ", postalCode, city);
    return concatenateWithDelimiter(LIST_DELIMITER, line, postalCodeAndCity, country);
  }
}
