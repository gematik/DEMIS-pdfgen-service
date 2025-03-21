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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class NameDTOTest {

  @Test
  void getFullName_shouldIgnoreNullAndBlank() {
    // given
    List<String> prefixes = Arrays.asList(null, "", "  ", "prefix");
    List<String> givenNames = Arrays.asList(null, "", "  ", "given");
    List<String> familyNames = Arrays.asList(null, "", "  ", "family");

    prefixes.forEach(
        prefix -> {
          givenNames.forEach(
              given -> {
                familyNames.forEach(
                    family -> {
                      // when
                      NameDTO nameDTO =
                          NameDTO.builder()
                              .familyName(family)
                              .givenName(given)
                              .prefix(prefix)
                              .build();

                      // then
                      String expected = buildExpectedFullName(prefix, given, family);
                      assertThat(nameDTO.getPrefix()).isEqualTo(prefix);
                      assertThat(nameDTO.getGivenName()).isEqualTo(given);
                      assertThat(nameDTO.getFamilyName()).isEqualTo(family);
                      assertThat(nameDTO.getFullName()).isEqualTo(expected);
                    });
              });
        });
  }

  @Test
  void getFullName_shouldTrimNames() {
    // given
    NameDTO nameDTO =
        NameDTO.builder()
            .familyName("   Muster   ")
            .givenName("   Max   ")
            .prefix("   Dr.  ")
            .build();
    assertThat(nameDTO.getPrefix()).isEqualTo("   Dr.  ");
    assertThat(nameDTO.getGivenName()).isEqualTo("   Max   ");
    assertThat(nameDTO.getFamilyName()).isEqualTo("   Muster   ");
    assertThat(nameDTO.getFullName()).isEqualTo("Dr. Max Muster");
  }

  private String buildExpectedFullName(String prefix, String given, String family) {
    List<String> names = new ArrayList<>();
    if (prefix != null && !prefix.isBlank()) {
      names.add(prefix.trim());
    }
    if (given != null && !given.isBlank()) {
      names.add(given.trim());
    }
    if (family != null && !family.isBlank()) {
      names.add(family.trim());
    }

    return String.join(" ", names);
  }
}
