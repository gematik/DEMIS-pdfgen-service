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

import java.util.Objects;

public final class PostalCodeUtils {
  public static final int POSTAL_CODE_STANDARD_LENGTH = 5;
  public static final int ANONYMIZED_POSTAL_CODE_MAX_LENGTH = 3;
  public static final String POSTAL_CODE_PLACEHOLDER = "-";

  private PostalCodeUtils() {}

  /**
   * Creates an anonymized Postal Code, containing placeholders, if it is shorter than 5 chars.
   *
   * @param postalCode the postal code to be anonymized
   * @return the anonymized postal code, or as it is, if null or empty or shorter than 5 chars.
   */
  public static String anonymizeWithPlaceholders(final String postalCode) {
    // Return as is if postal code is null or empty
    if (Objects.isNull(postalCode) || postalCode.isBlank()) {
      return postalCode;
    }

    // Add placeholders if postal code is shorter than 5 characters
    if (postalCode.length() < POSTAL_CODE_STANDARD_LENGTH) {
      return postalCode.concat(
          POSTAL_CODE_PLACEHOLDER.repeat(POSTAL_CODE_STANDARD_LENGTH - postalCode.length()));
    }

    // Return as is
    return postalCode;
  }

  /**
   * Returns the first three digits of the postal code for the Lifecycle Page in the report. In case
   * the postal code is not available, an empty string is returned. In case the postal code is
   * shorter than 3 characters, additional placeholders are added.
   *
   * @param postalCode the postal code to be shortened
   * @return the short postal code or null, if none set
   */
  public static String shortPostalCode(final String postalCode) {
    // Return as is if postal code is null or empty
    if (Objects.isNull(postalCode) || postalCode.isBlank()) {
      return postalCode;
    }

    if (postalCode.length() < ANONYMIZED_POSTAL_CODE_MAX_LENGTH) {
      return postalCode.concat(
          POSTAL_CODE_PLACEHOLDER.repeat(ANONYMIZED_POSTAL_CODE_MAX_LENGTH - postalCode.length()));
    }

    return postalCode.substring(0, ANONYMIZED_POSTAL_CODE_MAX_LENGTH);
  }
}
