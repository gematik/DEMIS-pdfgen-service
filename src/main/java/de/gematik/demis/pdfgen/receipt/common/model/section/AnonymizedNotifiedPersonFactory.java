package de.gematik.demis.pdfgen.receipt.common.model.section;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import de.gematik.demis.pdfgen.utils.PostalCodeUtils;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class AnonymizedNotifiedPersonFactory implements Supplier<AnonymizedNotifiedPerson> {

  private final GenderEnum gender;
  private final DateTimeHolder birthdate;
  private final Collection<AddressDTO> addresses;
  private final Collection<OrganizationDTO> organizations;

  @Override
  public AnonymizedNotifiedPerson get() {
    return new AnonymizedNotifiedPerson(this.gender, anonymizedBirthdate(), anonymizedPostalCode());
  }

  private String anonymizedBirthdate() {
    if (this.birthdate == null) {
      return null;
    }
    return this.birthdate.getDateWithoutTimeAndDay();
  }

  /**
   * Get anonymized postal code. The trick is to pick the best postal code. First pick is the postal
   * code of the primary address. If there is no primary address, we pick the first available postal
   * code. Note: If there is a primary address, but it contains no postal code, then we pick no
   * postal code.
   *
   * @return postal code <code>null</code>
   */
  private String anonymizedPostalCode() {
    return PostalCodeUtils.anonymizeWithPlaceholders(pickPostalCode());
  }

  private String pickPostalCode() {
    Optional<AddressDTO> primary = primaryAddress();
    if (primary.isPresent()) {
      return primary.get().getPostalCode();
    }
    return fallbackPostalCode();
  }

  private Optional<AddressDTO> primaryAddress() {
    return addresses().filter(AddressDTO::isPrimaryAddress).findFirst();
  }

  private Stream<AddressDTO> addresses() {
    if (this.addresses == null) {
      return Stream.empty();
    }
    return this.addresses.stream();
  }

  private String fallbackPostalCode() {
    String fromAddresses =
        addresses()
            .map(AddressDTO::getPostalCode)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    if (fromAddresses != null) {
      return fromAddresses;
    }
    return fallbackPostalCodeFromOrganizations();
  }

  private String fallbackPostalCodeFromOrganizations() {
    if (this.organizations == null) {
      return null;
    }

    return this.organizations.stream()
        .map(OrganizationDTO::getAddressDTO)
        .filter(Objects::nonNull)
        .map(AddressDTO::getPostalCode)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }
}
