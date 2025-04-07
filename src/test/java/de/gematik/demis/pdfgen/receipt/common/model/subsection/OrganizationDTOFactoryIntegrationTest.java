package de.gematik.demis.pdfgen.receipt.common.model.subsection;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createFhirOrganization;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createTransmittingSite;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import java.util.Optional;
import org.hl7.fhir.r4.model.Extension;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrganizationDTOFactoryIntegrationTest {

  @Autowired private OrganizationFactory organizationFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    Optional<org.hl7.fhir.r4.model.Organization> empty = Optional.empty();

    assertThat(organizationFactory.create((Extension) null)).isNull();
    assertThat(organizationFactory.create((TransmittingSite) null)).isNull();
    assertThat(organizationFactory.create(empty)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    OrganizationDTO organizationDTO =
        organizationFactory.create(Optional.of(createFhirOrganization()));

    // then
    assertThat(organizationDTO).isNotNull();
    assertThat(organizationDTO.getName()).isEqualTo("Health Level Seven International");
    assertThat(organizationDTO.getType()).isEmpty();
    assertThat(organizationDTO.getAddressDTO()).isNotNull();
    assertThat(organizationDTO.getAddressDTO().getLine())
        .isEqualTo("3300 Washtenaw Avenue, Suite 227");
    assertThat(organizationDTO.getTelecoms()).hasSize(3);
    assertThat(organizationDTO.getNameDTO()).isNull();
  }

  @Test
  void create_shouldTestFactoryCreationFromTransmittingSite() {
    // given
    OrganizationDTO organizationDTO = organizationFactory.create(createTransmittingSite());

    // then
    assertThat(organizationDTO).isNotNull();
    assertThat(organizationDTO.getName()).isEqualTo("Test Gesundheitsamt | Test Department");
    assertThat(organizationDTO.getType()).isNull();
    assertThat(organizationDTO.getAddressDTO()).isNotNull();
    assertThat(organizationDTO.getAddressDTO().getLine()).isEqualTo("Teststraße");
    assertThat(organizationDTO.getTelecoms()).hasSize(1);
    assertThat(organizationDTO.getNameDTO()).isNull();
  }
}
