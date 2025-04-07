package de.gematik.demis.pdfgen.receipt.common.model.section;

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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.test.helper.FhirFactory;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class MetadataFactoryTest {

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(MetadataFactory.create(null)).isNull();
  }

  @Test
  void create_shouldExtractIdentifier() {
    // given
    Metadata occupancy = MetadataFactory.create(FhirFactory.createBedOccupancyBundle());

    // then
    assertThat(occupancy).isNotNull();
    assertThat(occupancy.getIdentifier())
        .as("bed occupancy bundle identifier")
        .isEqualTo("cfcd2084-95d5-35ef-a6e7-dff9f98764da");

    // given
    Metadata laboratory = MetadataFactory.create(FhirFactory.createLaboratoryReportBundle());

    // then
    assertThat(laboratory).isNotNull();
    assertThat(laboratory.getIdentifier())
        .as("laboratory bundle identifier")
        .isEqualTo("a5e00874-bb26-45ac-8eea-0bde76456703");
  }

  @Test
  void create_shouldExtractReceptionTimeStamp() {
    // given
    Metadata metadata = MetadataFactory.create(FhirFactory.createLaboratoryReportBundle());

    // then
    assertThat(metadata).isNotNull();
    assertThat(metadata.getDate())
        .as("laboratory reception timestamp")
        .hasToString("24.10.2023 09:06");
  }

  /** The used FHIR bundle has an internal timestamp */
  @Test
  void create_shouldUseCurrentTimestampOnEmptyInput() {
    // given
    Metadata metadata =
        MetadataFactory.create(FhirFactory.createLaboratoryParticipantReportBundle());
    LocalDateTime now = LocalDateTime.now();

    // then
    assertThat(metadata).isNotNull();
    LocalDateTime reception = toLocalDateTime(metadata.getDate());
    long minutes = ChronoUnit.MINUTES.between(reception, now);
    assertThat(minutes)
        .as("reception timestamp difference from current time")
        .isLessThanOrEqualTo(5L)
        .isNotNegative();
  }

  private static LocalDateTime toLocalDateTime(DateTimeHolder date) {
    String value = date.toString();
    return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
  }
}
