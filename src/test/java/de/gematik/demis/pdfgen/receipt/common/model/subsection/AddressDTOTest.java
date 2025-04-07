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

import org.junit.jupiter.api.Test;

class AddressDTOTest {

  @Test
  void getOrganizationAddressAsSingleLine_valueIsAsExpected() {
    // given
    AddressDTO addressDTO = createAddress();
    // then
    assertThat(addressDTO.getOrganizationAddressAsSingleLine())
        .isEqualTo("department, organization, line, postalCode city, country");
  }

  @Test
  void getFullAddressAsSingleLine_valueIsAsExpected() {
    // given
    AddressDTO addressDTO = createAddress("use");
    // then
    assertThat(addressDTO.getFullAddressAsSingleLine()).isEqualTo("line, postalCode city, country");
  }

  @Test
  void getFullNameAndAddress_valueIsAsExpected() {
    // given
    AddressDTO addressDTOWithNullUse = createAddress(null);
    AddressDTO addressDTOWithUse = createAddress("use");
    // then
    assertThat(addressDTOWithNullUse.getAddressUseWithPrefix(null)).isEmpty();
    assertThat(addressDTOWithNullUse.getAddressUseWithPrefix("")).isEmpty();
    assertThat(addressDTOWithNullUse.getAddressUseWithPrefix("prefix")).isEqualTo("prefix");

    assertThat(addressDTOWithUse.getAddressUseWithPrefix(null)).isEqualTo("(use)");
    assertThat(addressDTOWithUse.getAddressUseWithPrefix("")).isEqualTo("(use)");
    assertThat(addressDTOWithUse.getAddressUseWithPrefix("prefix")).isEqualTo("prefix (use)");
  }

  private AddressDTO createAddress() {
    return createAddress("use");
  }

  private AddressDTO createAddress(String use) {
    return AddressDTO.builder()
        .department("department")
        .organization("organization")
        .line("line")
        .postalCode("postalCode")
        .city("city")
        .country("country")
        .use(use)
        .build();
  }
}
