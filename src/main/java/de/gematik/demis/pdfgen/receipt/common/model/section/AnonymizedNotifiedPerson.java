package de.gematik.demis.pdfgen.receipt.common.model.section;

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

import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.utils.PostalCodeUtils;

/**
 * @param gender The gender of the person or <code>null</code> if not available
 * @param birthdate The anonymized birthdate only holds information on month and year of birth or
 *     <code>null</code> if not available
 * @param postalCode The anonymized postal code only consists of the first three digits or <code>
 *                   null</code> if not available
 */
public record AnonymizedNotifiedPerson(GenderEnum gender, String birthdate, String postalCode) {

  /**
   * Returns the first three digits of the postal code for the Lifecycle Page in the report. In case
   * the postal code is not available, an empty string is returned. In case the postal code is
   * shorter than 3 characters, the full postal code is returned, which contain masked characters.
   *
   * @return the short postal code or null, if none set
   */
  public String shortPostalCode() {
    return PostalCodeUtils.shortPostalCode(postalCode);
  }
}
