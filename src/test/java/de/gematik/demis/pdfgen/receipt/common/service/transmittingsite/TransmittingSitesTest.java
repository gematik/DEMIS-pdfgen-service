package de.gematik.demis.pdfgen.receipt.common.service.transmittingsite;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createTransmittingSite;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class TransmittingSitesTest {

  @Test
  void testConstructorAndGetters() {
    TransmittingSites transmittingSites = new TransmittingSites();
    transmittingSites.setItems(List.of(createTransmittingSite()));

    assertThat(transmittingSites.getItems()).hasSize(1);
    TransmittingSite transmittingSite = transmittingSites.getItems().get(0);
    assertThat(transmittingSite.getName()).isEqualTo("Test Gesundheitsamt");
    assertThat(transmittingSite.getDepartment()).isEqualTo("Test Department");
    assertThat(transmittingSite.getEmail()).isEqualTo("test@test.com");
    assertThat(transmittingSite.getStreet()).isEqualTo("Teststraße");
    assertThat(transmittingSite.getPlace()).isEqualTo("Teststadt");
    assertThat(transmittingSite.getPhone()).isEmpty();
    assertThat(transmittingSite.getFax()).isEmpty();
  }
}
