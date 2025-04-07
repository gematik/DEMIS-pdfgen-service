package de.gematik.demis.pdfgen.receipt.common.model.subsection;

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

import static de.gematik.demis.pdfgen.utils.StringUtils.LIST_DELIMITER;
import static de.gematik.demis.pdfgen.utils.StringUtils.SPACE;
import static de.gematik.demis.pdfgen.utils.StringUtils.concatenateWithDelimiter;
import static org.apache.commons.lang3.StringUtils.isBlank;

import de.gematik.demis.pdfgen.receipt.common.model.enums.AddressUseEnum;
import de.gematik.demis.pdfgen.utils.PostalCodeUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class AddressDTO {

  @Getter private String department;
  @Getter private String organization;
  @Getter private String line;
  private String postalCode;
  @Getter private String city;
  @Getter private String country;
  @Getter @Setter private String use;
  @Getter @Setter private AddressUseEnum useEnum;

  /** DEMIS address use flagged: <code>primary</code> */
  @Getter private boolean primaryAddress;

  public String getPostalCode() {
    // if postal code is shorter than 5 characters, add placeholders
    return PostalCodeUtils.anonymizeWithPlaceholders(postalCode);
  }

  public String getOrganizationAddressAsSingleLine() {
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
    String postalCodeAndCity = concatenateWithDelimiter(SPACE, getPostalCode(), city);
    return concatenateWithDelimiter(
        LIST_DELIMITER, department, organization, line, postalCodeAndCity, country);
  }

  private String getFullAddress() {
    String postalCodeAndCity = concatenateWithDelimiter(" ", getPostalCode(), city);
    return concatenateWithDelimiter(LIST_DELIMITER, line, postalCodeAndCity, country);
  }
}
