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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createDiseaseQuestionsCommonQuestionnaireResponse;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HospitalizationFactoryIT {

  @Autowired private HospitalizationFactory hospitalizationFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(hospitalizationFactory.create(null)).isEmpty();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    List<Hospitalization> hospitalizations =
        hospitalizationFactory.create(createDiseaseQuestionsCommonQuestionnaireResponse());

    // then
    assertThat(hospitalizations).isNotNull().hasSize(2);
    assertFirst(hospitalizations.get(0));
    assertSecond(hospitalizations.get(1));
  }

  private static void assertFirst(Hospitalization hospitalization) {
    assertThat(hospitalization.getServiceType()).isEqualTo("Krankenhaus");
    OrganizationDTO firstOrganization = hospitalization.getOrganizationDTO();
    assertThat(firstOrganization).as("organization").isNotNull();
    assertThat(firstOrganization.getName()).as("organization name").isEqualTo("TEST Organisation");
    assertThat(hospitalization.getBegin()).as("begin").isNotNull().hasToString("05.01.2022 00:00");
    assertThat(hospitalization.getEnd()).as("end").isNotNull().hasToString("09.01.2022 00:00");
    assertThat(hospitalization.getNote()).as("note").isEqualTo("wichtige Zusatzinformation");
    assertThat(hospitalization.getDepartment()).as("department").isNull();
  }

  private static void assertSecond(Hospitalization hospitalization) {
    assertThat(hospitalization.getServiceType()).isEqualTo("Krankenhaus");
    OrganizationDTO secondOrganization = hospitalization.getOrganizationDTO();
    assertThat(secondOrganization).as("organization").isNotNull();
    assertThat(secondOrganization.getName()).as("organization name").isEqualTo("TEST Organisation");
    assertThat(hospitalization.getBegin()).as("begin").isNotNull().hasToString("07.01.2022 00:00");
    assertThat(hospitalization.getEnd()).as("empty end").isNotNull().hasToString("");
    assertThat(hospitalization.getNote()).as("note").isNull();
    assertThat(hospitalization.getDepartment()).as("department").isEqualTo("Intensivmedizin");
  }
}
