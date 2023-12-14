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

import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.DAY;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.MILLI;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.MINUTE;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.MONTH;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.SECOND;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.YEAR;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import lombok.Getter;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;

public class DateTimeHolder {
  private static final Set<TemporalPrecisionEnum> SHOULD_HAVE_MONTH =
      Set.of(MONTH, DAY, MINUTE, SECOND, MILLI);
  private static final Set<TemporalPrecisionEnum> SHOULD_HAVE_DAY =
      Set.of(DAY, MINUTE, SECOND, MILLI);
  private static final Set<TemporalPrecisionEnum> SHOULD_HAVE_TIME = Set.of(MINUTE, SECOND, MILLI);

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  @Getter private final BaseDateTimeType dateTime;
  @Getter private final Date date;

  @Getter private final String dateWithoutTime;
  @Getter private final String dateWithoutTimeAndDay;

  public DateTimeHolder(final BaseDateTimeType dateTime) {
    this.dateTime = validOrNull(dateTime);
    this.date = null;
    dateWithoutTime = toStringWithoutTime();
    dateWithoutTimeAndDay = toStringWithoutTimeAndDay();
  }

  public DateTimeHolder(final Date date) {
    this.dateTime = null;
    this.date = date;
    dateWithoutTime = toStringWithoutTime();
    dateWithoutTimeAndDay = toStringWithoutTimeAndDay();
  }

  private BaseDateTimeType validOrNull(BaseDateTimeType dateTime) {
    if (dateTime == null || dateTime.getPrecision() == null || dateTime.getYear() == null) {
      return null;
    }
    if (dateTime.getMonth() == null && SHOULD_HAVE_MONTH.contains(dateTime.getPrecision())) {
      return null;
    }
    if (dateTime.getDay() == null && SHOULD_HAVE_DAY.contains(dateTime.getPrecision())) {
      return null;
    }
    if ((dateTime.getHour() == null || dateTime.getMinute() == null)
        && SHOULD_HAVE_TIME.contains(dateTime.getPrecision())) {
      return null;
    }
    return dateTime;
  }

  public static DateTimeHolder now() {
    return new DateTimeHolder(DateTimeType.now());
  }

  public String toStringWithoutTime() {
    return dateString();
  }

  public String toStringWithoutTimeAndDay() {
    return dateOfMounthAndYearString();
  }

  @Override
  public String toString() {
    return dateString() + timeStringWithSpacePrefix();
  }

  private String dateString() {
    if (dateTime == null) {
      return date != null ? dateFormat.format(date) : "";
    }

    if (dateTime.getPrecision() == YEAR) {
      return dateTime.getYear().toString();
    }
    if (dateTime.getPrecision() == MONTH) {
      return doubleDigit(dateTime.getMonth() + 1) + "." + dateTime.getYear();
    }
    return doubleDigit(dateTime.getDay())
        + "."
        + doubleDigit(dateTime.getMonth() + 1)
        + "."
        + dateTime.getYear();
  }

  private String dateOfMounthAndYearString() {
    if (date != null) {
      LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      return doubleDigit(localDate.getMonthValue()) + "/" + localDate.getYear();
    }
    if (dateTime != null && dateTime.getPrecision() == YEAR) {
      return dateTime.getYear().toString();
    }
    if (dateTime != null) {
      return doubleDigit(dateTime.getMonth() + 1) + "/" + dateTime.getYear();
    }

    return "";
  }

  private String timeStringWithSpacePrefix() {
    if (dateTime == null || !SHOULD_HAVE_TIME.contains(dateTime.getPrecision())) {
      return "";
    }
    return " " + doubleDigit(dateTime.getHour()) + ":" + doubleDigit(dateTime.getMinute());
  }

  private String doubleDigit(int i) {
    return 0 <= i && i <= 9 ? "0" + i : String.valueOf(i);
  }
}
