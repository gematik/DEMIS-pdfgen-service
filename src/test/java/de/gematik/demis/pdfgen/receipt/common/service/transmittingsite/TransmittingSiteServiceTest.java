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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TransmittingSiteServiceTest {

  TransmittingSiteService transmittingSiteService =
      new TransmittingSiteService("TransmittingSiteSearchText.xml", "test-int,test-prod");

  @Test
  void getTransmittingSite_shouldHandleInvalidInputGracefully() {
    assertThat(transmittingSiteService.getTransmittingSite(null)).isNull();
    assertThat(transmittingSiteService.getTransmittingSite("")).isNull();
    assertThat(transmittingSiteService.getTransmittingSite("   ")).isNull();
    assertThat(transmittingSiteService.getTransmittingSite("Not a real code")).isNull();
  }

  @Test
  void getTransmittingSite_shouldHandleNullGracefully() {
    // when
    TransmittingSite transmittingSite1 =
        transmittingSiteService.getTransmittingSite("1.11.0.11.01.");
    TransmittingSite transmittingSite2 = transmittingSiteService.getTransmittingSite("1.12.0.52.");
    TransmittingSite transmittingSite3 = transmittingSiteService.getTransmittingSite("test-int");
    TransmittingSite transmittingSite4 = transmittingSiteService.getTransmittingSite("test-prod");

    // then
    assertThat(transmittingSite1).isNotNull();
    assertThat(transmittingSite1.getName()).isEqualTo("Bezirksamt Lichtenberg von Berlin");

    assertThat(transmittingSite2).isNotNull();
    assertThat(transmittingSite2.getName()).isEqualTo("Stadt Cottbus/S");

    assertThat(transmittingSite3).isNotNull();
    assertThat(transmittingSite3.getName()).isEqualTo("Test Gesundheitsamt");

    assertThat(transmittingSite4).isNotNull();
    assertThat(transmittingSite4.getName()).isEqualTo("Test Gesundheitsamt");
  }
}
