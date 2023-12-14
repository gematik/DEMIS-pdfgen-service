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

import org.junit.jupiter.api.Test;

class PersonTest {

  @Test
  void isNotEmpty_checksIfPersonExistsAndHasContactWithGivenName() {
    // given
    NameDTO emptyNameDTO = NameDTO.builder().build();
    NameDTO nonEmptyNameDTO = NameDTO.builder().givenName("given").build();

    Person emptyPerson = Person.builder().build();
    Person personWithEmptyContact = Person.builder().nameDTO(emptyNameDTO).build();
    Person personWithNonEmptyContact = Person.builder().nameDTO(nonEmptyNameDTO).build();

    // then
    assertThat(emptyPerson.isNotEmpty()).isFalse();
    assertThat(personWithEmptyContact.isNotEmpty()).isFalse();
    assertThat(personWithNonEmptyContact.isNotEmpty()).isTrue();
  }
}
