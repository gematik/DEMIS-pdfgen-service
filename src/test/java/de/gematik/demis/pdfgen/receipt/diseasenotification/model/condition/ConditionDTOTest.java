package de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition;

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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Arrays;
import java.util.List;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.jupiter.api.Test;

class ConditionDTOTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    DateTimeHolder emptyDateTimeHolder = new DateTimeHolder((BaseDateTimeType) null);
    DateTimeHolder dateTimeHolder = new DateTimeHolder(new DateTimeType());

    List<List<String>> symptomsLists =
        Arrays.asList(
            null,
            emptyList(),
            singletonList("symptom1"),
            Arrays.asList(null, "", "  ", "symptom2"));
    List<DateTimeHolder> dates = Arrays.asList(null, emptyDateTimeHolder, dateTimeHolder);

    symptomsLists.forEach(
        symptoms -> {
          dates.forEach(
              onsetDate -> {
                dates.forEach(
                    recordedDate -> {
                      // when
                      ConditionDTO conditionDTO =
                          ConditionDTO.builder()
                              .symptoms(symptoms)
                              .onsetDate(onsetDate)
                              .recordedDate(recordedDate)
                              .note("Fascinating!")
                              .build();

                      // then
                      assertThat(conditionDTO.symptoms()).isEqualTo(symptoms);
                      assertThat(conditionDTO.onsetDate()).isEqualTo(onsetDate);
                      assertThat(conditionDTO.recordedDate()).isEqualTo(recordedDate);
                      assertThat(conditionDTO.getAllSymptoms()).isNotNull();
                      assertThat(conditionDTO.note())
                          .as("diagnostic note")
                          .isEqualTo("Fascinating!");
                    });
              });
        });
  }
}
