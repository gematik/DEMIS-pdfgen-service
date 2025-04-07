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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class IdentifierTest {

  @Test
  void getAsMultipleLines_shouldAddPrefixToIdsAndReturnAsMultilineString() {
    // given
    List<String> bsnrList = Arrays.asList(null, "", "  ", "bsnr");
    List<String> demisIdList = Arrays.asList(null, "", "  ", "demisId");
    bsnrList.forEach(
        bsnr -> {
          demisIdList.forEach(
              demisId -> {
                // when
                Identifier identifier = Identifier.builder().bsnr(bsnr).demisId(demisId).build();

                // then
                String expectedBsnrWithPrefix = "bsnr".equals(bsnr) ? "BSNR: bsnr" : "";
                String expectedDemisIdWithPrefix =
                    "demisId".equals(demisId) ? "DEMIS-Id: demisId" : "";
                String expectedMultiline;
                if (expectedBsnrWithPrefix.isBlank() && expectedDemisIdWithPrefix.isBlank()) {
                  expectedMultiline = "";
                } else if (expectedBsnrWithPrefix.isBlank()
                    && !expectedDemisIdWithPrefix.isBlank()) {
                  expectedMultiline = expectedDemisIdWithPrefix;
                } else if (!expectedBsnrWithPrefix.isBlank()
                    && expectedDemisIdWithPrefix.isBlank()) {
                  expectedMultiline = expectedBsnrWithPrefix;
                } else {
                  expectedMultiline = expectedBsnrWithPrefix + "\n" + expectedDemisIdWithPrefix;
                }

                assertThat(identifier.getAsMultipleLines()).isEqualTo(expectedMultiline);
              });
        });
  }
}
