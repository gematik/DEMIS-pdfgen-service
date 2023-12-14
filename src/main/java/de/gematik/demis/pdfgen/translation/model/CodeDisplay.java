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

package de.gematik.demis.pdfgen.translation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CodeDisplay {

  private final String code;
  private final String display;
  private List<Designation> designations;

  public CodeDisplay(String code, String display, List<Designation> designations) {
    this.code = code;
    this.display = display;
    this.designations = designations;
  }

  public CodeDisplay(String code, String display) {
    this.code = code;
    this.display = display;
    this.designations = Collections.emptyList();
  }

  // Default constructor for deserialization
  public CodeDisplay() {
    this.code = null;
    this.display = null;
    this.designations = Collections.emptyList();
  }

  public static String getDeTranslationWithFallback(CodeDisplay translated) {
    return translated.getDesignations().stream()
        .filter(designation -> "de".equals(designation.language()))
        .findFirst()
        .map(Designation::value)
        .orElse(translated.getDisplay());
  }
}
