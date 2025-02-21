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

import de.gematik.demis.pdfgen.translation.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddressTranslationService {

  static final String COUNTRY_CODE_SYSTEM_URL = "https://demis.rki.de/fhir/CodeSystem/country";

  static final String COUNTRY_CODE_SYSTEM_ISO_3166_URL = "urn:iso:std:iso:3166";

  private final TranslationService translationService;

  private static String getCountryCodeSystem(String text) {
    if (StringUtils.isNumeric(text)) {
      return COUNTRY_CODE_SYSTEM_URL;
    }
    return COUNTRY_CODE_SYSTEM_ISO_3166_URL;
  }

  private static Coding createCountryCoding(String code) {
    String system = getCountryCodeSystem(code);
    return new Coding(system, code, null);
  }

  public String translateCountryCode(String countryCode) {
    String code = StringUtils.trimToNull(countryCode);
    if (code == null) {
      return "";
    }
    Coding coding = createCountryCoding(code);
    return this.translationService.resolveCodeableConceptValues(coding);
  }

  public String translateUse(Coding coding) {
    Coding addressUseCoding = createAddressUseCoding(coding);
    return this.translationService.resolveCodeableConceptValues(addressUseCoding);
  }

  private Coding createAddressUseCoding(Coding coding) {
    String system = coding.getSystem();
    String code = coding.getCode();
    String display = createAddressUseDisplay(coding);
    return new Coding(system, code, display);
  }

  private String createAddressUseDisplay(Coding coding) {
    String display = coding.getDisplay();
    if (display != null) {
      return display;
    }
    return coding.getCode();
  }
}
