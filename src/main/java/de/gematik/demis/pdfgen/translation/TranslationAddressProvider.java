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

package de.gematik.demis.pdfgen.translation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TranslationAddressProvider {

  private static final String COUNTRY_CODE_SYSTEM_URL =
      "https://demis.rki.de/fhir/CodeSystem/country";
  private static final String EMPTY_STRING = "";

  private final TranslationService displayTranslationService;

  public String translateCountryCode(String countryCode) {

    if (countryCode == null) {
      return EMPTY_STRING;
    }

    return displayTranslationService
        .resolveCodeableConceptValues(COUNTRY_CODE_SYSTEM_URL, countryCode)
        .orElse(COUNTRY_CODE_SYSTEM_URL + ": " + countryCode);
  }

  public String translateUse(Coding coding) {
    return displayTranslationService
        .resolveCodeableConceptValues(coding.getSystem(), coding.getCode())
        .orElse(fallbackUse(coding));
  }

  private String fallbackUse(Coding coding) {
    if (coding.getDisplay() != null) {
      return coding.getDisplay();
    }

    return coding.getCode();
  }
}
