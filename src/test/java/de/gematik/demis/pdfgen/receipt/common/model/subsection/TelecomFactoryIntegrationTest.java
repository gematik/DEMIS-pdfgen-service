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
 * #L%
 */

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createFhirOrganization;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import java.util.List;
import org.hl7.fhir.r4.model.ContactPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TelecomFactoryIntegrationTest {

  @Autowired private TelecomFactory telecomFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(telecomFactory.createTelecoms((List<ContactPoint>) null)).isEmpty();

    assertThat(telecomFactory.createTelecoms((TransmittingSite) null)).isEmpty();
    assertThat(telecomFactory.createTelecoms(emptyList())).isEmpty();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    List<Telecom> telecoms = telecomFactory.createTelecoms(createFhirOrganization().getTelecom());

    // then
    assertThat(telecoms).isNotNull().isNotEmpty().hasSize(3);
    assertThat(telecoms.get(0).asSingleLine()).isEqualTo("Telefon: (+1) 734-677-7777");
    assertThat(telecoms.get(1).asSingleLine()).isEqualTo("Fax: (+1) 734-677-6622");
    assertThat(telecoms.get(2).asSingleLine()).isEqualTo("E-Mail: hq@HL7.org");
  }
}
