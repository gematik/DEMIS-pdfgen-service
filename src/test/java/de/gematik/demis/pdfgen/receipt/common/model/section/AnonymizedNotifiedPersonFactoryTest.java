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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AnonymizedNotifiedPersonFactoryTest {

  @ParameterizedTest
  @EnumSource(value = GenderEnum.class)
  void shouldUseGender(GenderEnum gender) {
    AnonymizedNotifiedPerson actual = new AnonymizedNotifiedPersonFactory(gender, null, null).get();
    assertThat(actual.gender()).as("gender of anonymized person").isSameAs(gender);
  }

  @Test
  void shouldAcceptNullGender() {
    AnonymizedNotifiedPerson actual = new AnonymizedNotifiedPersonFactory(null, null, null).get();
    assertThat(actual.gender()).as("missing gender of anonymized person").isNull();
  }

  @Test
  void shouldAnonymizeBirthdate() {
    LocalDate date = LocalDate.of(2003, Month.JULY, 24);
    DateTimeHolder birthdate =
        new DateTimeHolder(
            Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, birthdate, null).get();
    assertThat(actual.birthdate()).as("anonymized birthdate").isEqualTo("07/2003");
  }

  @Test
  void shouldAcceptNullBirthdate() {
    AnonymizedNotifiedPerson actual = new AnonymizedNotifiedPersonFactory(null, null, null).get();
    assertThat(actual.birthdate()).as("missing birthdate of anonymized person").isNull();
  }

  @Test
  void shouldAnonymizePostalCode() {
    String postalCode = "10117";
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(
                null,
                null,
                Collections.singleton(AddressDTO.builder().postalCode(postalCode).build()))
            .get();
    assertThat(actual.postalCode()).as("anonymized postal code").isEqualTo("101");
  }

  @Test
  void shouldUsePrimaryAddressPostalCode() {
    AddressDTO primary = AddressDTO.builder().postalCode("10117").primaryAddress(true).build();
    AddressDTO spare = AddressDTO.builder().postalCode("26579").build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, Arrays.asList(spare, primary)).get();
    assertThat(actual.postalCode()).as("primary address postal code").isEqualTo("101");
  }

  @Test
  void shouldUseEmptyPostalCodeFromPrimaryAddress() {
    AddressDTO primary = AddressDTO.builder().city("Berlin").primaryAddress(true).build();
    AddressDTO spare = AddressDTO.builder().postalCode("26579").build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, Arrays.asList(spare, primary)).get();
    assertThat(actual.postalCode()).as("primary address set but without postal code").isNull();
  }

  @Test
  void shouldPickFirstSparePostalCode() {
    AddressDTO spare1 = AddressDTO.builder().postalCode("10117").build();
    AddressDTO spare2 = AddressDTO.builder().postalCode("26579").build();
    AddressDTO spare3 = AddressDTO.builder().postalCode("88499").build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, Arrays.asList(spare1, spare2, spare3))
            .get();
    assertThat(actual.postalCode()).as("first spare postal code").isEqualTo("101");
  }

  @Test
  void shouldAcceptMissingPostalCode() {
    assertThat(new AnonymizedNotifiedPersonFactory(null, null, null).get().postalCode()).isNull();
    assertThat(
            new AnonymizedNotifiedPersonFactory(null, null, Collections.emptyList())
                .get()
                .postalCode())
        .isNull();
    assertThat(
            new AnonymizedNotifiedPersonFactory(
                    null, null, Collections.singleton(AddressDTO.builder().build()))
                .get()
                .postalCode())
        .isNull();
    assertThat(
            new AnonymizedNotifiedPersonFactory(
                    null,
                    null,
                    Arrays.asList(
                        AddressDTO.builder().build(),
                        AddressDTO.builder().build(),
                        AddressDTO.builder().build()))
                .get()
                .postalCode())
        .isNull();
  }
}
