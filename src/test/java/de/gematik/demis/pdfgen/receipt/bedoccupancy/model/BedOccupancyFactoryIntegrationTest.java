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
 * #L%
 */

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createBedOccupancyBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.section.Metadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BedOccupancyFactoryIntegrationTest {

  @Autowired private BedOccupancyFactory bedOccupancyFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(bedOccupancyFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    BedOccupancy bedOccupancy = bedOccupancyFactory.create(createBedOccupancyBundle());

    // then
    assertThat(bedOccupancy).isNotNull();
    Metadata metadata = bedOccupancy.getMetadata();
    assertThat(metadata.getIdentifier())
        .as("Meldevorgangs-ID")
        .isEqualTo("cfcd2084-95d5-35ef-a6e7-dff9f98764da");
    assertThat(metadata.getDate()).as("Zeitpunkt des Meldevorgangs").isNotNull();
    assertThat(bedOccupancy.getNotificationId())
        .as("Meldungs-ID")
        .isEqualTo("5e1e89ce-7a44-4ec1-801c-0f988992e8fe");
    assertThat(bedOccupancy.getOrganization()).isNotNull();
    assertThat(bedOccupancy.getNumberOccupiedBedsGeneralWardAdults()).isEqualTo("221");
    assertThat(bedOccupancy.getNumberOccupiedBedsGeneralWardChildren()).isEqualTo("37");
    assertThat(bedOccupancy.getNumberOperableBedsGeneralWardAdults()).isEqualTo("250");
    assertThat(bedOccupancy.getNumberOperableBedsGeneralWardChildren()).isEqualTo("50");
  }
}
