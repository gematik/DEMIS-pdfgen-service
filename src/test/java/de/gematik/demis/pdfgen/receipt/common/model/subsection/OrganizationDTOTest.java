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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrganizationDTOTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    AddressDTO emptyAddressDTO = AddressDTO.builder().build();
    Telecom telecom = Telecom.builder().build();
    NameDTO emptyNameDTO = NameDTO.builder().build();

    List<String> names = Arrays.asList(null, "", "  ", "name");
    List<String> types = Arrays.asList(null, "", "  ", "type");
    List<AddressDTO> addressDTOS = Arrays.asList(null, emptyAddressDTO);
    List<List<Telecom>> telecomLists =
        Arrays.asList(null, emptyList(), List.of(telecom), List.of(telecom, telecom));
    List<NameDTO> NameDTOS = Arrays.asList(null, emptyNameDTO);

    names.forEach(
        name -> {
          types.forEach(
              type -> {
                addressDTOS.forEach(
                    address -> {
                      telecomLists.forEach(
                          telecoms -> {
                            NameDTOS.forEach(
                                contact -> {
                                  // when
                                  OrganizationDTO organizationDTO =
                                      OrganizationDTO.builder()
                                          .name(name)
                                          .type(type)
                                          .addressDTO(address)
                                          .telecoms(telecoms)
                                          .nameDTO(contact)
                                          .build();

                                  // then
                                  assertThat(organizationDTO.getName()).isEqualTo(name);
                                  assertThat(organizationDTO.getType()).isEqualTo(type);
                                  assertThat(organizationDTO.getAddressDTO()).isEqualTo(address);
                                  assertThat(organizationDTO.getTelecoms()).isEqualTo(telecoms);
                                  assertThat(organizationDTO.getNameDTO()).isEqualTo(contact);
                                });
                          });
                    });
              });
        });
  }

  @Test
  void getOrganizationInfoIncludingName_shouldGetOrganizationInfoString() {
    AddressDTO addressDTO =
        AddressDTO.builder().line("street").city("city").postalCode("12345").build();
    Telecom telecom = Telecom.builder().system(TelecomTypeEnum.PHONE).value("55544433").build();
    NameDTO nameDTO = NameDTO.builder().build();
    OrganizationDTO organizationDTO =
        OrganizationDTO.builder()
            .name("name")
            .type("type")
            .addressDTO(addressDTO)
            .telecoms(List.of(telecom))
            .nameDTO(nameDTO)
            .build();

    assertThat(organizationDTO.getOrganizationInfo())
        .isEqualTo("street, 12345 city\nTelefon: 55544433");
    assertThat(organizationDTO.getOrganizationInfoIncludingName())
        .isEqualTo("name (type)\nstreet, 12345 city\nTelefon: 55544433");
  }

  @Test
  void getSubmitterDetailsHandlesAbsentDetails() {
    OrganizationDTO build = OrganizationDTO.builder().nameDTO(null).department("").build();
    String submitterDetails = build.getSubmitterDetails();
    assertThat(submitterDetails).isEmpty();
  }

  @Test
  void getSubmitterDetailsHandlesAbsentDepartment() {
    OrganizationDTO build =
        OrganizationDTO.builder().nameDTO(new NameDTO("", "First", "Last")).build();
    String submitterDetails = build.getSubmitterDetails();
    assertThat(submitterDetails).isEqualTo("First Last");
  }

  @Test
  void getSubmitterDetailsWorksWithRelevantInformation() {
    OrganizationDTO build =
        OrganizationDTO.builder()
            .nameDTO(new NameDTO("", "Contact", "Person"))
            .department("Example Department")
            .telecoms(List.of(new Telecom(TelecomTypeEnum.PHONE, TelecomUseEnum.WORK, "123")))
            .build();
    String submitterDetails = build.getSubmitterDetails();
    assertThat(submitterDetails)
        .isEqualTo(
            """
                Contact Person
                Example Department
                Telefon: 123 (Dienstlich)""");
  }
}
