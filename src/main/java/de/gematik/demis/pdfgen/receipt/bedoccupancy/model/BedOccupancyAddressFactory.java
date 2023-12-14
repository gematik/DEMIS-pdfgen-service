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

package de.gematik.demis.pdfgen.receipt.bedoccupancy.model;

import de.gematik.demis.pdfgen.translation.TranslationAddressProvider;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.PrimitiveType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BedOccupancyAddressFactory {

  private final TranslationAddressProvider translationAddressProvider;

  @Nullable
  public BedOccupancyAddress create(org.hl7.fhir.r4.model.Address fhirAddress) {
    if (fhirAddress == null) {
      return null;
    }

    String line = getLine(fhirAddress);
    String city = fhirAddress.getCity();
    String postalCode = fhirAddress.getPostalCode();
    String countryName = translationAddressProvider.translateCountryCode(fhirAddress.getCountry());
    return BedOccupancyAddress.builder()
        .line(line)
        .city(city)
        .postalCode(postalCode)
        .country(countryName)
        .build();
  }

  private String getLine(org.hl7.fhir.r4.model.Address fhirAddress) {
    if (fhirAddress.getLine() == null) {
      return "";
    }
    return fhirAddress.getLine().stream()
        .map(PrimitiveType::getValueAsString)
        .findFirst()
        .orElse("");
  }
}
