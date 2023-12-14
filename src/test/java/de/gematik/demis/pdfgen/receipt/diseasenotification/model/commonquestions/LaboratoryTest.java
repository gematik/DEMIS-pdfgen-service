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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.commonquestions;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class LaboratoryTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given

    List<String> names = Arrays.asList(null, "", "  ", "name1");
    List<String> departments = Arrays.asList(null, "", "  ", "department1");
    List<AddressDTO> addressDTOS = Arrays.asList(null, AddressDTO.builder().build());
    List<List<Telecom>> telecomsLists =
        Arrays.asList(null, emptyList(), singletonList(Telecom.builder().build()));

    names.forEach(
        name -> {
          departments.forEach(
              department -> {
                addressDTOS.forEach(
                    address -> {
                      telecomsLists.forEach(
                          telecoms -> {
                            // when
                            Laboratory laboratory =
                                Laboratory.builder()
                                    .name(name)
                                    .department(department)
                                    .addressDTO(address)
                                    .telecoms(telecoms)
                                    .build();

                            // then
                            assertThat(laboratory.getName()).isEqualTo(name);
                            assertThat(laboratory.getDepartment()).isEqualTo(department);
                            assertThat(laboratory.getAddressDTO()).isEqualTo(address);
                            assertThat(laboratory.getTelecoms()).isEqualTo(telecoms);
                          });
                    });
              });
        });
  }
}
