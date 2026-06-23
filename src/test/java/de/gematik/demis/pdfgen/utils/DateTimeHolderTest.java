package de.gematik.demis.pdfgen.utils;

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

import java.util.TimeZone;
import java.util.stream.Stream;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class DateTimeHolderTest {

  @Test
  void constructorAndToString_shouldHandleBlankGracefully() {
    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder((BaseDateTimeType) null);

    // then
    assertThat(dateTimeHolder).isNotNull();
    assertThat(dateTimeHolder.toString()).isEmpty();
  }

  @Test
  void constructorAndToString_shouldHandleDateTimeTypeAsExpected() {
    // given
    DateTimeType emptyDateTime = new DateTimeType("");
    DateTimeType justYear = new DateTimeType("2023");
    DateTimeType monthAndYear = new DateTimeType("2023-12");
    DateTimeType justDate = new DateTimeType("2023-12-20");
    DateTimeType dateAndTime = new DateTimeType("2023-12-20T17:35:00");

    // when
    DateTimeHolder emptyHolder = new DateTimeHolder(emptyDateTime);
    DateTimeHolder justYearHolder = new DateTimeHolder(justYear);
    DateTimeHolder monthAndYearHolder = new DateTimeHolder(monthAndYear);
    DateTimeHolder justDateHolder = new DateTimeHolder(justDate);
    DateTimeHolder dateAndTimeHolder = new DateTimeHolder(dateAndTime);

    // then
    assertThat(emptyHolder).isNotNull();
    assertThat(emptyHolder.toString()).isEmpty();

    assertThat(justYearHolder).isNotNull().hasToString("2023");
    assertThat(monthAndYearHolder).isNotNull().hasToString("12.2023");
    assertThat(justDateHolder).isNotNull().hasToString("20.12.2023");
    assertThat(dateAndTimeHolder).isNotNull().hasToString("20.12.2023 17:35");
  }

  @Test
  void toStringWithoutTime_shouldReturnExpectedString() {
    // given
    DateTimeType dateAndTime = new DateTimeType("2023-12-20T17:35:00");

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(dateAndTime);

    // then
    assertThat(dateTimeHolder.toStringWithoutTime()).isEqualTo("20.12.2023");
  }

  @Test
  void toStringWithoutTimeAndDay_shouldReturnExpectedString() {
    // given
    DateTimeType dateAndTime = new DateTimeType("2023-12-20T17:35:00");

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(dateAndTime);

    // then
    assertThat(dateTimeHolder.toStringWithoutTimeAndDay()).isEqualTo("12/2023");
  }

  @Test
  void now_shouldReturnCurrentDateTimeHolder() {
    // when
    DateTimeHolder nowHolder = DateTimeHolder.now();

    // then
    assertThat(nowHolder).isNotNull();
    assertThat(nowHolder.getDateTime()).isNotNull();
  }

  @Test
  void check_different_combinations() {
    // when
    DateTimeHolder dateNull = new DateTimeHolder(null);
    DateTimeHolder dateOnlyYear = new DateTimeHolder(new DateTimeType("2023"));
    DateTimeHolder dateYearMonth = new DateTimeHolder(new DateTimeType("2023-12"));
    DateTimeHolder dateNoTime = new DateTimeHolder(new DateTimeType("2023-12-20"));
    // then
    assertThat(dateNull.getDateTime()).isNull();
    assertThat(dateOnlyYear.getDateTime()).isNotNull();
    assertThat(dateYearMonth.getDateTime()).isNotNull();
    assertThat(dateNoTime.getDateTime()).isNotNull();
  }

  @Test
  void validOrNull_shouldReturnValidDateTime() {
    // given
    DateTimeType validDateTime = new DateTimeType("2023-12-20T17:35:00");

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(validDateTime);

    // then
    assertThat(dateTimeHolder.getDateTime()).isEqualTo(validDateTime);
  }

  static Stream<String> otherTimeZones() {
    return Stream.of(
        "14:00:00+00:00", "15:00:00+01:00", "16:00:00+02:00", "10:00:00-04:00", "14:00:00Z");
  }

  @ParameterizedTest
  @MethodSource("otherTimeZones")
  void toBerlinTime_shouldConvertUtcTimeToBerlinTime(final String otherTimeZone) {
    // given - UTC time 15:00 should become 16:00 CET or 17:00 CEST in Berlin
    DateTimeType utcDateTime = new DateTimeType("2023-07-15T" + otherTimeZone);

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(utcDateTime);

    // then - July is CEST (UTC+2), so 14:00 UTC -> 16:00 Berlin
    assertThat(dateTimeHolder.toString()).isEqualTo("15.07.2023 16:00");
    assertThat(dateTimeHolder.getDateTime().getTimeZone())
        .isEqualTo(TimeZone.getTimeZone("Europe/Berlin"));
  }

  @ParameterizedTest
  @MethodSource("otherTimeZones")
  void toBerlinTime_shouldConvertUtcTimeToBerlinTimeInWinter(final String otherTimeZone) {
    // given - UTC time in winter (CET = UTC+1)
    DateTimeType utcDateTime = new DateTimeType("2023-01-15T" + otherTimeZone);

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(utcDateTime);

    // then - January is CET (UTC+1), so 14:00 UTC -> 15:00 Berlin
    assertThat(dateTimeHolder.toString()).isEqualTo("15.01.2023 15:00");
  }

  static Stream<String> otherTimeZonesLateNight() {
    return Stream.of("22:00:00+00:00", "23:00:00+01:00", "18:00:00-04:00", "22:00:00Z");
  }

  @ParameterizedTest
  @MethodSource("otherTimeZonesLateNight")
  void toBerlinTime_shouldRollOverToNextDayWhenUtcLateNight(final String otherTimeZone) {
    // given - 22:00 UTC in summer (CEST = UTC+2) should become 00:00 next day in Berlin
    DateTimeType utcDateTime = new DateTimeType("2023-07-15T" + otherTimeZone);

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(utcDateTime);

    // then - 22:00 UTC + 2h = 00:00 on 16.07.2023
    assertThat(dateTimeHolder.toString()).isEqualTo("16.07.2023 00:00");
  }

  @Test
  void toBerlinTime_shouldHandleDateTimeWithoutTimezoneGracefully() {
    // given - has no timezone to convert
    DateTimeType utcDateTime = new DateTimeType("2023-01-15T14:00:00");

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(utcDateTime);

    // then - stays 14:00
    assertThat(dateTimeHolder.toString()).isEqualTo("15.01.2023 14:00");
  }

  @Test
  void toBerlinTime_shouldHandleDateWithoutTimeGracefully() {
    // given - date-only value has no time to convert
    DateTimeType dateOnly = new DateTimeType("2023-06-15");

    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(dateOnly);

    // then - should still format date correctly
    assertThat(dateTimeHolder.toString()).isEqualTo("15.06.2023");
    assertThat(dateTimeHolder.getDateTime().getTimeZone())
        .isEqualTo(TimeZone.getTimeZone("Europe/Berlin"));
  }

  @Test
  void toBerlinTime_shouldNotFailForNullDateTime() {
    // when
    DateTimeHolder dateTimeHolder = new DateTimeHolder(null);

    // then
    assertThat(dateTimeHolder.getDateTime()).isNull();
    assertThat(dateTimeHolder.toString()).isEmpty();
  }
}
