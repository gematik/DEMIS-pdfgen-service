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

package de.gematik.demis.pdfgen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.jupiter.api.Test;

class DateTimeHolderTest {

  @Test
  void constructorAndToString_shouldHandleBlankGracefully() {
    // when
    DateTimeHolder dateTimeHolder1 = new DateTimeHolder((BaseDateTimeType) null);
    DateTimeHolder dateTimeHolder2 = new DateTimeHolder((Date) null);

    // then
    assertThat(dateTimeHolder1).isNotNull();
    assertThat(dateTimeHolder1.toString()).isEmpty();

    assertThat(dateTimeHolder2).isNotNull();
    assertThat(dateTimeHolder2.toString()).isEmpty();
  }

  @Test
  void constructorAndToString_shouldHandleDateAsExpected() {
    // given
    LocalDate localDate = LocalDate.of(2023, Month.MARCH, 20);
    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    // when
    DateTimeHolder holder = new DateTimeHolder(date);

    // then
    assertThat(holder).isNotNull().hasToString("20.03.2023");
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
}
