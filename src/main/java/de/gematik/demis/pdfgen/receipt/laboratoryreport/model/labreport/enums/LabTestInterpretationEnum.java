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

package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums;

import static de.gematik.demis.pdfgen.utils.StringUtils.makeEnumNameHumanFriendly;

import de.gematik.demis.pdfgen.utils.MessageUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public enum LabTestInterpretationEnum {
  POSITIVE("POS", "enum.lab-test.interpretation.positive"),
  NEGATIVE("NEG", "enum.lab-test.interpretation.negative"),
  INDETERMINATE("IND", "enum.lab-test.interpretation.indeterminate");

  private final String code;
  private final String messageKey;

  public static Optional<LabTestInterpretationEnum> ofCode(final String inputCode) {
    if ("POS".equalsIgnoreCase(inputCode)) {
      return Optional.of(POSITIVE);
    } else if ("NEG".equalsIgnoreCase(inputCode)) {
      return Optional.of(NEGATIVE);
    } else if ("IND".equalsIgnoreCase(inputCode)) {
      return Optional.of(INDETERMINATE);
    } else {
      log.warn("Received unexpected Lab Test Interpretation code {}", inputCode);
      return Optional.empty();
    }
  }

  @Override
  public String toString() {
    return MessageUtil.getOrDefault(messageKey, makeEnumNameHumanFriendly(name()));
  }
}
