package de.gematik.demis.pdfgen.utils;

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

import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.DAY;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.MILLI;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.MINUTE;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.MONTH;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.SECOND;
import static ca.uhn.fhir.model.api.TemporalPrecisionEnum.YEAR;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;

/**
 * This class is handle a date and time and provide a string representation of it. It supports also
 * the possibility that no date or time is given.
 */
public class DateTimeHolder {
  private static final Set<TemporalPrecisionEnum> SHOULD_HAVE_MONTH =
      Set.of(MONTH, DAY, MINUTE, SECOND, MILLI);
  private static final Set<TemporalPrecisionEnum> SHOULD_HAVE_DAY =
      Set.of(DAY, MINUTE, SECOND, MILLI);
  private static final Set<TemporalPrecisionEnum> SHOULD_HAVE_TIME = Set.of(MINUTE, SECOND, MILLI);

  @Getter private final BaseDateTimeType dateTime;

  @Getter private final String dateWithoutTime;
  @Getter private final String dateWithoutTimeAndDay;

  public DateTimeHolder(final BaseDateTimeType dateTime) {
    this.dateTime = validOrNull(dateTime);
    this.dateWithoutTime = toStringWithoutTime();
    this.dateWithoutTimeAndDay = toStringWithoutTimeAndDay();
  }

  public static DateTimeHolder now() {
    return new DateTimeHolder(DateTimeType.now());
  }

  private BaseDateTimeType validOrNull(final BaseDateTimeType dateTime) {
    if (dateTime == null || dateTime.getPrecision() == null || dateTime.getYear() == null) {
      return null;
    }
    if (dateTime.getMonth() == null
        && DateTimeHolder.SHOULD_HAVE_MONTH.contains(dateTime.getPrecision())) {
      return null;
    }
    if (dateTime.getDay() == null
        && DateTimeHolder.SHOULD_HAVE_DAY.contains(dateTime.getPrecision())) {
      return null;
    }
    if ((dateTime.getHour() == null || dateTime.getMinute() == null)
        && DateTimeHolder.SHOULD_HAVE_TIME.contains(dateTime.getPrecision())) {
      return null;
    }
    return dateTime;
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
    if (Objects.isNull(this.dateTime)) {
      return "";
    }

    if (this.dateTime.getPrecision() == YEAR) {
      return this.dateTime.getYear().toString();
    }
    if (this.dateTime.getPrecision() == MONTH) {
      return doubleDigit(this.dateTime.getMonth() + 1) + "." + this.dateTime.getYear();
    }
    return doubleDigit(this.dateTime.getDay())
        + "."
        + doubleDigit(this.dateTime.getMonth() + 1)
        + "."
        + this.dateTime.getYear();
  }

  private String dateOfMounthAndYearString() {
    if (this.dateTime != null && this.dateTime.getPrecision() == YEAR) {
      return this.dateTime.getYear().toString();
    }
    if (this.dateTime != null) {
      return doubleDigit(this.dateTime.getMonth() + 1) + "/" + this.dateTime.getYear();
    }

    return "";
  }

  private String timeStringWithSpacePrefix() {
    if (this.dateTime == null
        || !DateTimeHolder.SHOULD_HAVE_TIME.contains(this.dateTime.getPrecision())) {
      return "";
    }
    return " "
        + doubleDigit(this.dateTime.getHour())
        + ":"
        + doubleDigit(this.dateTime.getMinute());
  }

  private String doubleDigit(final int i) {
    return 0 <= i && i <= 9 ? "0" + i : String.valueOf(i);
  }
}
