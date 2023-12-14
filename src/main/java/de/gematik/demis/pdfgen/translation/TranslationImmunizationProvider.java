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

import static java.util.Arrays.*;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TranslationImmunizationProvider {

  private static final List<String> NULL_FLAVOR_CODES = asList("ASKU", "NASK");
  private static final String HTTP_TERMINOLOGY_HL_7_ORG_CODE_SYSTEM_V_3_NULL_FLAVOR =
      "http://terminology.hl7.org/CodeSystem/v3-NullFlavor";

  private static final String HTTPS_DEMIS_RKI_DE_FHIR_VALUE_SET_VACCINE_CVDD =
      "https://demis.rki.de/fhir/ValueSet/vaccineCVDD";
  private final TranslationService displayTranslationService;

  public String translateCode(CodeableConcept vaccineCode) {
    return remoteLookup(vaccineCode);
  }

  private String remoteLookup(CodeableConcept vaccineCode) {
    Coding codingFirstRep = vaccineCode.getCodingFirstRep();
    String code = codingFirstRep.getCode();
    String translation;

    if (NULL_FLAVOR_CODES.contains(code)) {
      translation = translateWithNullFlavor(codingFirstRep, code);
    } else {
      translation = translateWithCvdd(codingFirstRep, code);
    }
    return translation;
  }

  private String translateWithCvdd(Coding codingFirstRep, String code) {
    return displayTranslationService
        .resolveCodeableConceptValues(HTTPS_DEMIS_RKI_DE_FHIR_VALUE_SET_VACCINE_CVDD, code)
        .orElse(codingFirstRep.getDisplay());
  }

  private String translateWithNullFlavor(Coding codingFirstRep, String code) {
    return displayTranslationService
        .resolveCodeableConceptValues(HTTP_TERMINOLOGY_HL_7_ORG_CODE_SYSTEM_V_3_NULL_FLAVOR, code)
        .orElse(codingFirstRep.getDisplay());
  }
}
