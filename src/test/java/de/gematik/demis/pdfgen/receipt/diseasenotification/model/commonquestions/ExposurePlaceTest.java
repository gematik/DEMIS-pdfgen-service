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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.commonquestions;

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.jupiter.api.Test;

class ExposurePlaceTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    DateTimeHolder emptyDateTimeHolder = new DateTimeHolder((BaseDateTimeType) null);
    DateTimeHolder dateHolder = new DateTimeHolder(new Date());
    DateTimeHolder dateTimeHolder = new DateTimeHolder(new DateTimeType());

    List<String> regions = Arrays.asList(null, "", "  ", "region1");
    List<DateTimeHolder> dates =
        Arrays.asList(null, emptyDateTimeHolder, dateHolder, dateTimeHolder);
    List<String> notes = Arrays.asList(null, "", "  ", "note1");

    regions.forEach(
        region -> {
          notes.forEach(
              note -> {
                dates.forEach(
                    begin -> {
                      dates.forEach(
                          end -> {
                            // when
                            ExposurePlace exposurePlace =
                                ExposurePlace.builder()
                                    .region(region)
                                    .begin(begin)
                                    .end(end)
                                    .note(note)
                                    .build();

                            // then
                            assertThat(exposurePlace.getRegion()).isEqualTo(region);
                            assertThat(exposurePlace.getBegin()).isEqualTo(begin);
                            assertThat(exposurePlace.getEnd()).isEqualTo(end);
                            assertThat(exposurePlace.getNote()).isEqualTo(note);
                          });
                    });
              });
        });
  }
}
