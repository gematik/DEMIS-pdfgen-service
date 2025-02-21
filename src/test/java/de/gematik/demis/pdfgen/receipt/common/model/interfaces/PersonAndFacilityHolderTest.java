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

package de.gematik.demis.pdfgen.receipt.common.model.interfaces;

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

import de.gematik.demis.pdfgen.receipt.common.model.subsection.Facility;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.NameDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Person;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class PersonAndFacilityHolderTest {

  @Test
  void hasPerson_checksIfPersonExistsAndHasContactWithGivenName() {
    // given
    NameDTO emptyNameDTO = NameDTO.builder().build();
    NameDTO nonEmptyNameDTO = NameDTO.builder().givenName("given").build();

    Person emptyPerson = Person.builder().build();
    Person personWithEmptyContact = Person.builder().nameDTO(emptyNameDTO).build();
    Person personWithNonEmptyContact = Person.builder().nameDTO(nonEmptyNameDTO).build();

    Facility facility = Facility.builder().build();

    // when
    TestPersonAndFacilityHolder nullPersonNotifier =
        TestPersonAndFacilityHolder.builder().person(null).facility(facility).build();
    TestPersonAndFacilityHolder emptyPersonNotifier =
        TestPersonAndFacilityHolder.builder().person(emptyPerson).facility(facility).build();
    TestPersonAndFacilityHolder emptyContactPersonNotifier =
        TestPersonAndFacilityHolder.builder()
            .person(personWithEmptyContact)
            .facility(facility)
            .build();
    TestPersonAndFacilityHolder nonEmptyContactPersonNotifier =
        TestPersonAndFacilityHolder.builder()
            .person(personWithNonEmptyContact)
            .facility(facility)
            .build();

    // then
    assertThat(nullPersonNotifier.hasPerson()).isFalse();
    assertThat(emptyPersonNotifier.hasPerson()).isFalse();
    assertThat(emptyContactPersonNotifier.hasPerson()).isFalse();
    assertThat(nonEmptyContactPersonNotifier.hasPerson()).isTrue();
  }

  @Test
  void hasFacility_checkIfFacilityExistsAndHasName() {
    // given
    Person person = Person.builder().build();

    Facility emptyFacility = Facility.builder().build();
    Facility nonEmptyFacility = Facility.builder().name("name").build();

    // when
    TestPersonAndFacilityHolder nullFacilityNotifier =
        TestPersonAndFacilityHolder.builder().person(person).facility(null).build();
    TestPersonAndFacilityHolder emptyFacilityNotifier =
        TestPersonAndFacilityHolder.builder().person(person).facility(emptyFacility).build();
    TestPersonAndFacilityHolder nonEmptyFacilityNotifier =
        TestPersonAndFacilityHolder.builder().person(person).facility(nonEmptyFacility).build();

    // then
    assertThat(nullFacilityNotifier.hasFacility()).isFalse();
    assertThat(emptyFacilityNotifier.hasFacility()).isFalse();
    assertThat(nonEmptyFacilityNotifier.hasFacility()).isTrue();
  }

  @Getter
  @Setter
  @Builder
  private static class TestPersonAndFacilityHolder extends PersonAndFacilityHolder {
    private Person person;
    private Facility facility;
  }
}
