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

import static de.gematik.demis.pdfgen.fhir.extract.ExtensionQueries.resolve;
import static java.util.stream.Collectors.joining;

import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import de.gematik.demis.pdfgen.translation.TranslationAddressProvider;
import de.gematik.demis.pdfgen.utils.StringUtils;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * Tool for processing DEMIS address use information.
 *
 * <h1>DEMIS address use</h1>
 *
 * <p>DEMIS address use (<code>https://demis.rki.de/fhir/StructureDefinition/AddressUse</code>) in
 * difference to FHIR address use (<code>http://hl7.org/fhir/ValueSet/address-use</code>).
 *
 * <p>Example codes:
 *
 * <ul>
 *   <li>primary
 *   <li>current
 *   <li>ordinary
 * </ul>
 *
 * <h2>FHIR address use</h2>
 *
 * <p>Example codes:
 *
 * <ul>
 *   <li>home
 *   <li>work
 *   <li>temp
 *   <li>old
 * </ul>
 */
@Service
@RequiredArgsConstructor
public final class DemisAddressUseService {

  /** Code of primary address */
  static final String CODE_PRIMARY = "primary";

  private final TranslationAddressProvider translationAddressProvider;

  /**
   * Create translated text of all DEMIS address uses attached to the given address.
   *
   * @param address address
   * @return translated DEMIS address uses. Examples of potential return values:
   *     <ul>
   *       <li><code>Hauptwohnsitz, Derzeitiger Aufenthaltsort</code>
   *       <li>Hauptwohnsitz
   *     </ul>
   */
  String toString(Address address) {
    return getUses(address)
        .map(this.translationAddressProvider::translateUse)
        .collect(joining(StringUtils.LIST_DELIMITER));
  }

  @NotNull
  private static Stream<Coding> getUses(Address address) {
    return address.getExtensionsByUrl(DemisExtensions.EXTENSION_URL_ADDRESS_USE).stream()
        .map(extension -> resolve(extension, Coding.class))
        .filter(Objects::nonNull);
  }

  /**
   * Check if the given address is a primary address
   *
   * @param address address
   * @return <code>true</code> if primary address, <code>false</code> if not
   */
  public boolean isPrimaryAddress(Address address) {
    return getUses(address).map(Coding::getCode).anyMatch(CODE_PRIMARY::equalsIgnoreCase);
  }
}
