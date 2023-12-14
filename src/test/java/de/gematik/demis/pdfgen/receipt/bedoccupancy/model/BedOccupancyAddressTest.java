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

package de.gematik.demis.pdfgen.receipt.bedoccupancy.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class BedOccupancyAddressTest {
  private static final List<String> EMPTY_STRINGS = Arrays.asList(null, "", "  ");

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenDataComplete() {
    // given
    BedOccupancyAddress address =
        BedOccupancyAddress.builder()
            .line("line")
            .postalCode("postalCode")
            .city("city")
            .country("country")
            .build();
    // then
    assertThat(address.getFullAddressAsSingleLine()).isEqualTo("line, postalCode city, country");
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenLineMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .map(
                s ->
                    BedOccupancyAddress.builder()
                        .line(s)
                        .postalCode("postalCode")
                        .city("city")
                        .country("country")
                        .build())
            .toList();
    // then
    addresses.forEach(
        a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("postalCode city, country"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenPostalCodeMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .map(
                s ->
                    BedOccupancyAddress.builder()
                        .line("line")
                        .postalCode(s)
                        .city("city")
                        .country("country")
                        .build())
            .toList();
    // then
    addresses.forEach(
        a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("line, city, country"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenCityMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .map(
                s ->
                    BedOccupancyAddress.builder()
                        .line("line")
                        .postalCode("postalCode")
                        .city(s)
                        .country("country")
                        .build())
            .toList();
    // then
    addresses.forEach(
        a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("line, postalCode, country"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenCountryMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .map(
                s ->
                    BedOccupancyAddress.builder()
                        .line("line")
                        .postalCode("postalCode")
                        .city("city")
                        .country(s)
                        .build())
            .toList();
    // then
    addresses.forEach(
        a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("line, postalCode city"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenLineAndPostalCodeMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .map(
                            s2 ->
                                BedOccupancyAddress.builder()
                                    .line(s1)
                                    .postalCode(s2)
                                    .city("city")
                                    .country("country")
                                    .build()))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("city, country"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenLineAndCityMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .map(
                            s2 ->
                                BedOccupancyAddress.builder()
                                    .line(s1)
                                    .postalCode("postalCode")
                                    .city(s2)
                                    .country("country")
                                    .build()))
            .toList();
    // then
    addresses.forEach(
        a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("postalCode, country"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenLineAndCountryMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .map(
                            s2 ->
                                BedOccupancyAddress.builder()
                                    .line(s1)
                                    .postalCode("postalCode")
                                    .city("city")
                                    .country(s2)
                                    .build()))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("postalCode city"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenPostalCodeAndCityMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .map(
                            s2 ->
                                BedOccupancyAddress.builder()
                                    .line("line")
                                    .postalCode(s1)
                                    .city(s2)
                                    .country("country")
                                    .build()))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("line, country"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenPostalCodeAndCountryMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .map(
                            s2 ->
                                BedOccupancyAddress.builder()
                                    .line("line")
                                    .postalCode(s1)
                                    .city("city")
                                    .country(s2)
                                    .build()))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("line, city"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenPostalCityAndCountryMissing() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .map(
                            s2 ->
                                BedOccupancyAddress.builder()
                                    .line("line")
                                    .postalCode("postalCode")
                                    .city(s1)
                                    .country(s2)
                                    .build()))
            .toList();
    // then
    addresses.forEach(
        a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("line, postalCode"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenOnlyLinePresent() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .flatMap(
                            s2 ->
                                EMPTY_STRINGS.stream()
                                    .map(
                                        s3 ->
                                            BedOccupancyAddress.builder()
                                                .line("line")
                                                .postalCode(s1)
                                                .city(s2)
                                                .country(s3)
                                                .build())))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("line"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenOnlyPostalCodePresent() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .flatMap(
                            s2 ->
                                EMPTY_STRINGS.stream()
                                    .map(
                                        s3 ->
                                            BedOccupancyAddress.builder()
                                                .line(s1)
                                                .postalCode("postalCode")
                                                .city(s2)
                                                .country(s3)
                                                .build())))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("postalCode"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenOnlyCityPresent() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .flatMap(
                            s2 ->
                                EMPTY_STRINGS.stream()
                                    .map(
                                        s3 ->
                                            BedOccupancyAddress.builder()
                                                .line(s1)
                                                .postalCode(s2)
                                                .city("city")
                                                .country(s3)
                                                .build())))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("city"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenOnlyCountryPresent() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .flatMap(
                            s2 ->
                                EMPTY_STRINGS.stream()
                                    .map(
                                        s3 ->
                                            BedOccupancyAddress.builder()
                                                .line(s1)
                                                .postalCode(s2)
                                                .city(s3)
                                                .country("country")
                                                .build())))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEqualTo("country"));
  }

  @Test
  void getFullAddressAsSingleLine_shouldGenerateAddressStringWhenNothingPresent() {
    // given
    List<BedOccupancyAddress> addresses =
        EMPTY_STRINGS.stream()
            .flatMap(
                s1 ->
                    EMPTY_STRINGS.stream()
                        .flatMap(
                            s2 ->
                                EMPTY_STRINGS.stream()
                                    .flatMap(
                                        s3 ->
                                            EMPTY_STRINGS.stream()
                                                .map(
                                                    s4 ->
                                                        BedOccupancyAddress.builder()
                                                            .line(s1)
                                                            .postalCode(s2)
                                                            .city(s3)
                                                            .country(s4)
                                                            .build()))))
            .toList();
    // then
    addresses.forEach(a -> assertThat(a.getFullAddressAsSingleLine()).isEmpty());
  }
}
