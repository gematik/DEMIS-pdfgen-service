package de.gematik.demis.pdfgen.receipt.common.model.section;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AnonymizedNotifiedPersonFactoryTest {

  private static final String ANONYMIZED_POSTAL_CODE_PATIENT_INFO = "101--";
  private static final String ANONYMIZED_POSTAL_CODE_LIFECYCLE_PAGE = "101";

  @ParameterizedTest
  @EnumSource(value = GenderEnum.class)
  void shouldUseGender(GenderEnum gender) {
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(gender, null, null, null).get();
    assertThat(actual.gender()).as("gender of anonymized person").isSameAs(gender);
  }

  @Test
  void shouldAcceptNullGender() {
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, null, null).get();
    assertThat(actual.gender()).as("missing gender of anonymized person").isNull();
  }

  @Test
  void shouldAcceptNullBirthdate() {
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, null, null).get();
    assertThat(actual.birthdate()).as("missing birthdate of anonymized person").isNull();
  }

  @Test
  void shouldAnonymizePostalCodeWith2Chars() {
    String postalCode = "10";
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(
                null,
                null,
                Collections.singleton(AddressDTO.builder().postalCode(postalCode).build()),
                null)
            .get();
    assertThat(actual.postalCode()).as("anonymized postal code").isEqualTo("10---");
    assertThat(actual.shortPostalCode()).as("anonymized short postal code").isEqualTo("10-");
  }

  @Test
  void shouldAnonymizePostalCodeWith3Chars() {
    String postalCode = "101";
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(
                null,
                null,
                Collections.singleton(AddressDTO.builder().postalCode(postalCode).build()),
                null)
            .get();
    assertThat(actual.postalCode())
        .as("anonymized postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_PATIENT_INFO);
    assertThat(actual.shortPostalCode())
        .as("anonymized short postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_LIFECYCLE_PAGE);
  }

  @Test
  void shouldAnonymizePostalCodeWith4Chars() {
    String postalCode = "1011";
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(
                null,
                null,
                Collections.singleton(AddressDTO.builder().postalCode(postalCode).build()),
                null)
            .get();
    assertThat(actual.postalCode()).as("anonymized postal code").isEqualTo("1011-");
    assertThat(actual.shortPostalCode())
        .as("anonymized short postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_LIFECYCLE_PAGE);
  }

  @Test
  void shouldPrint3CharsInLifecyclePageBy5chars() {
    String postalCode = "10117";
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(
                null,
                null,
                Collections.singleton(AddressDTO.builder().postalCode(postalCode).build()),
                null)
            .get();
    assertThat(actual.postalCode()).as("anonymized postal code").isEqualTo(postalCode);
    assertThat(actual.shortPostalCode())
        .as("anonymized short postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_LIFECYCLE_PAGE);
  }

  @Test
  void shouldUsePrimaryAddressPostalCode() {
    AddressDTO primary = AddressDTO.builder().postalCode("101").primaryAddress(true).build();
    AddressDTO spare = AddressDTO.builder().postalCode("26579").build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, Arrays.asList(spare, primary), null).get();
    assertThat(actual.postalCode())
        .as("primary address postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_PATIENT_INFO);
    assertThat(actual.shortPostalCode())
        .as("primary address short postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_LIFECYCLE_PAGE);
  }

  @Test
  void shouldUseEmptyPostalCodeFromPrimaryAddress() {
    AddressDTO primary = AddressDTO.builder().city("Berlin").primaryAddress(true).build();
    AddressDTO spare = AddressDTO.builder().postalCode("26579").build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, Arrays.asList(spare, primary), null).get();
    assertThat(actual.postalCode()).as("primary address set but without postal code").isNull();
    assertThat(actual.shortPostalCode()).as("primary address set but without postal code").isNull();
  }

  @Test
  void shouldPickFirstSparePostalCode() {
    AddressDTO spare1 = AddressDTO.builder().postalCode("101").build();
    AddressDTO spare2 = AddressDTO.builder().postalCode("26579").build();
    AddressDTO spare3 = AddressDTO.builder().postalCode("88499").build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, Arrays.asList(spare1, spare2, spare3), null)
            .get();
    assertThat(actual.postalCode())
        .as("first spare postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_PATIENT_INFO);
    assertThat(actual.shortPostalCode())
        .as("first spare short postal code")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_LIFECYCLE_PAGE);
  }

  @Test
  void shouldAcceptMissingPostalCode() {
    assertThat(new AnonymizedNotifiedPersonFactory(null, null, null, null).get().postalCode())
        .isNull();
    assertThat(
            new AnonymizedNotifiedPersonFactory(null, null, Collections.emptyList(), null)
                .get()
                .postalCode())
        .isNull();
    assertThat(
            new AnonymizedNotifiedPersonFactory(
                    null, null, Collections.singleton(AddressDTO.builder().build()), null)
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
                        AddressDTO.builder().build()),
                    null)
                .get()
                .postalCode())
        .isNull();
  }

  @Test
  void shouldFallbackToOrganizationPostalCodeWhenNoAddresses() {
    OrganizationDTO organization =
        OrganizationDTO.builder()
            .addressDTO(AddressDTO.builder().postalCode("10117").build())
            .build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(null, null, null, List.of(organization)).get();
    assertThat(actual.postalCode())
        .as("postal code from organization when no addresses")
        .isEqualTo("10117");
    assertThat(actual.shortPostalCode())
        .as("short postal code from organization")
        .isEqualTo(ANONYMIZED_POSTAL_CODE_LIFECYCLE_PAGE);
  }

  @Test
  void shouldFallbackToOrganizationPostalCodeWhenAddressesHaveNoPostalCode() {
    AddressDTO addressWithoutPostalCode = AddressDTO.builder().city("Berlin").build();
    OrganizationDTO organization =
        OrganizationDTO.builder()
            .addressDTO(AddressDTO.builder().postalCode("10117").build())
            .build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(
                null, null, List.of(addressWithoutPostalCode), List.of(organization))
            .get();
    assertThat(actual.postalCode())
        .as("postal code from organization when addresses have no postal code")
        .isEqualTo("10117");
  }

  @Test
  void shouldPreferAddressPostalCodeOverOrganization() {
    AddressDTO addressWithPostalCode = AddressDTO.builder().postalCode("20095").build();
    OrganizationDTO organization =
        OrganizationDTO.builder()
            .addressDTO(AddressDTO.builder().postalCode("10117").build())
            .build();
    AnonymizedNotifiedPerson actual =
        new AnonymizedNotifiedPersonFactory(
                null, null, List.of(addressWithPostalCode), List.of(organization))
            .get();
    assertThat(actual.postalCode())
        .as("address postal code should be preferred over organization")
        .isEqualTo("20095");
  }
}
