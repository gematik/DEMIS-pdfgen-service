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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.test.helper.FhirFactory;
import org.hl7.fhir.r4.model.Address;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BedOccupancyAddressFactoryIT {

  @Autowired private BedOccupancyAddressFactory bedOccupancyAddressFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(bedOccupancyAddressFactory.create(null)).isNull();
  }

  @Test
  void create_shouldConvertAddress() {
    // given
    Address fhirAddress = FhirFactory.createFhirAddress();

    // when
    BedOccupancyAddress address = bedOccupancyAddressFactory.create(fhirAddress);

    // then
    assertThat(address).isNotNull();
    assertThat(address.getLine()).isEqualTo("3300 Washtenaw Avenue, Suite 227");
    assertThat(address.getCity()).isEqualTo("Ann Arbor");
    assertThat(address.getPostalCode()).isEqualTo("48104");
    assertThat(address.getCountry()).isEqualTo("https://demis.rki.de/fhir/CodeSystem/country: USA");
  }
}
