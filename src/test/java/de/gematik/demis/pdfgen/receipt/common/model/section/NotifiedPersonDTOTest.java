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

package de.gematik.demis.pdfgen.receipt.common.model.section;

import static de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum.MALE;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.NameDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotifiedPersonDTOTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    AddressDTO addressDTO = AddressDTO.builder().build();
    Telecom telecom = Telecom.builder().build();
    OrganizationDTO organizationDTO = OrganizationDTO.builder().build();

    List<NameDTO> NameDTOS = Arrays.asList(null, NameDTO.builder().build());
    List<GenderEnum> genders = Arrays.asList(null, MALE);
    List<DateTimeHolder> birthdates = Arrays.asList(null, DateTimeHolder.now());
    List<List<AddressDTO>> addressLists =
        Arrays.asList(null, emptyList(), List.of(addressDTO), List.of(addressDTO, addressDTO));
    List<List<Telecom>> telecomLists =
        Arrays.asList(null, emptyList(), List.of(telecom), List.of(telecom, telecom));
    List<List<OrganizationDTO>> organizationLists =
        Arrays.asList(
            null, emptyList(), List.of(organizationDTO), List.of(organizationDTO, organizationDTO));

    NameDTOS.forEach(
        contact -> {
          genders.forEach(
              gender -> {
                birthdates.forEach(
                    birthdate -> {
                      addressLists.forEach(
                          addresses -> {
                            telecomLists.forEach(
                                telecoms -> {
                                  organizationLists.forEach(
                                      organizations -> {
                                        // when
                                        NotifiedPersonDTO notifiedPersonDTO =
                                            NotifiedPersonDTO.builder()
                                                .nameDTO(contact)
                                                .gender(gender)
                                                .birthdate(birthdate)
                                                .addressDTOs(addresses)
                                                .telecoms(telecoms)
                                                .organizationDTOs(organizations)
                                                .build();

                                        // then
                                        assertThat(notifiedPersonDTO.getNameDTO())
                                            .isEqualTo(contact);
                                        assertThat(notifiedPersonDTO.getGender()).isEqualTo(gender);
                                        assertThat(notifiedPersonDTO.getBirthdate())
                                            .isEqualTo(birthdate);
                                        assertThat(notifiedPersonDTO.getAddressDTOs())
                                            .isEqualTo(addresses);
                                        assertThat(notifiedPersonDTO.getTelecoms())
                                            .isEqualTo(telecoms);
                                        assertThat(notifiedPersonDTO.getOrganizationDTOs())
                                            .isEqualTo(organizations);
                                        assertThat(notifiedPersonDTO.getNotifiedName()).isNotNull();
                                        assertThat(notifiedPersonDTO.getTelecomsAsMultipleLines())
                                            .isNotNull();
                                      });
                                });
                          });
                    });
              });
        });
  }

  @Test
  void getNotifiedName_shouldHandleNullContactGracefully() {
    // given
    NameDTO nullNameDTO = null;
    NameDTO fullNameDTO =
        NameDTO.builder().familyName("family").givenName("given").prefix("prefix").build();
    NotifiedPersonDTO nullContactPerson = NotifiedPersonDTO.builder().nameDTO(nullNameDTO).build();
    NotifiedPersonDTO fullContactPerson = NotifiedPersonDTO.builder().nameDTO(fullNameDTO).build();

    // then
    assertThat(nullContactPerson.getNotifiedName()).isEmpty();
    assertThat(fullContactPerson.getNotifiedName()).isEqualTo("prefix given family");
  }
}
