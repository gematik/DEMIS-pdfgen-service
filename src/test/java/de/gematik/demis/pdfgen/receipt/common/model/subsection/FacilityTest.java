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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class FacilityTest {

  @Test
  void getNameAndType_shouldHandleNullAndBlankGracefully() {
    // given
    List<String> names = Arrays.asList(null, "", "  ", "name");
    List<String> types = Arrays.asList(null, "", "  ", "type");
    names.forEach(
        name -> {
          types.forEach(
              type -> {
                // when
                Facility facility = Facility.builder().name(name).type(type).build();

                // then
                String expected;
                if ("name".equals(name)) {
                  if ("type".equals(type)) {
                    expected = "name (type)";
                  } else {
                    expected = "name";
                  }
                } else {
                  expected = "";
                }

                assertThat(facility.getNameAndType()).isEqualTo(expected);
              });
        });
  }

  @Test
  void getNameAndType_shouldTrimNameAndType() {
    // given
    Facility justNameFacility = Facility.builder().name("  name  ").build();
    Facility justTypeFacility = Facility.builder().type("  type  ").build();
    Facility nameAndTypeFacility = Facility.builder().name("  name  ").type("  type  ").build();

    // then
    assertThat(justNameFacility.getNameAndType()).isEqualTo("name");
    assertThat(justTypeFacility.getNameAndType()).isEmpty();
    assertThat(nameAndTypeFacility.getNameAndType()).isEqualTo("name (type)");
  }
}
