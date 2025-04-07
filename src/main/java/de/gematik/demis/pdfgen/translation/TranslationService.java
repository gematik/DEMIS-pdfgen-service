package de.gematik.demis.pdfgen.translation;

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

import static java.util.Arrays.asList;

import de.gematik.demis.pdfgen.translation.client.CodeSystemFeignClient;
import de.gematik.demis.pdfgen.translation.client.ValueSetFeignClient;
import de.gematik.demis.pdfgen.translation.model.CodeDisplay;
import de.gematik.demis.pdfgen.translation.model.Designation;
import feign.FeignException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationService {

  private static final String EMPTY_STRING = "";
  private static final List<String> GERMAN_TRANSLATION = asList("de", "de-DE", "de-CH", "de-AT");
  private final ValueSetFeignClient valueSetFeignClient;
  private final CodeSystemFeignClient codeSystemFeignClient;

  public String resolveCodeableConceptValues(final CodeableConcept codeableConcept) {
    if (codeableConcept == null) {
      return EMPTY_STRING;
    }
    return resolveCodeableConceptValues(codeableConcept.getCodingFirstRep());
  }

  public String resolveCodeableConceptValues(final Coding coding) {
    if (!coding.hasSystem() || !coding.hasCode()) {
      return EMPTY_STRING;
    }

    var s = resolveCodeableConceptValues(coding.getSystem(), coding.getCode());
    if (s.isPresent()) {
      return s.get();
    }

    if (coding.hasDisplay()) {
      return coding.getDisplay();
    }
    // If nothing of the above works, add system and code
    return coding.getSystem() + ": " + coding.getCode();
  }

  Optional<String> resolveCodeableConceptValues(String system, String code) {
    Optional<CodeDisplay> display;
    if (system.contains("ValueSet")) {
      display = getInfoForCodeFromValueSet(system, code);
    } else {
      display = getInfoForCodeFromCodeSystem(system, code);
    }
    return display.map(this::extractGermanIfPossible);
  }

  private String extractGermanIfPossible(CodeDisplay display) {
    return display.getDesignations().stream()
        .filter(this::german)
        .findFirst()
        .map(Designation::value)
        .orElseGet(display::getDisplay);
  }

  private boolean german(Designation designation) {
    return GERMAN_TRANSLATION.contains(designation.language());
  }

  private Optional<CodeDisplay> getInfoForCodeFromValueSet(String system, String code) {
    try {
      return Optional.ofNullable(valueSetFeignClient.getInfoForCodeFromValueSet(system, code));
    } catch (FeignException e) {
      log.error(
          "Error when retrieving getInfoForCodeFromValueSet: {} for wanted data: {}/{}",
          e.getMessage(),
          system,
          code);
      return Optional.empty();
    }
  }

  private Optional<CodeDisplay> getInfoForCodeFromCodeSystem(String system, String code) {
    try {
      return Optional.ofNullable(codeSystemFeignClient.getInfoForCodeFromCodeSystem(system, code));
    } catch (FeignException e) {
      log.error(
          "Error when retrieving getInfoForCodeFromCodeSystem: {} for wanted data: {}/{}",
          e.getMessage(),
          system,
          code);
      return Optional.empty();
    }
  }
}
