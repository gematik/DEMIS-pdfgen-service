package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createDiseaseQuestionsCommonQuestionnaireResponse;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import java.util.List;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HospitalizationFactoryIntegrationTest {

  public static final String REASON_CODE_SYSTEM =
      "https://demis.rki.de/fhir/CodeSystem/hospitalizationReason";
  public static final String REASON_CODE = "becauseOfDisease";
  public static final String REASON_DISPLAY = "Hospitalisiert aufgrund der gemeldeten Krankheit";
  @Autowired private HospitalizationFactory hospitalizationFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(hospitalizationFactory.create(null)).isEmpty();
  }

  @Test
  void givenQuestionnaireResponseWithHospitalizationsWhenCreateThenHospitalizations() {
    // given
    QuestionnaireResponse response = createDiseaseQuestionsCommonQuestionnaireResponse();
    // when
    List<Hospitalization> hospitalizations = hospitalizationFactory.create(response);
    // then
    assertThat(hospitalizations).isNotNull().hasSize(2);
    assertFirst(hospitalizations.get(0));
    assertSecond(hospitalizations.get(1));
  }

  @Test
  void givenQuestionnaireResponseWithReasonedHospitalizationWhenCreateThenHospitalizations() {
    // given encounter with reason
    QuestionnaireResponse response = createDiseaseQuestionsCommonQuestionnaireResponse();
    QuestionnaireResponse.QuestionnaireResponseItemComponent hospitalized =
        response.getItem().get(3);
    QuestionnaireResponse.QuestionnaireResponseItemComponent hospitalizedEncounter =
        hospitalized.getAnswerFirstRep().getItem().getFirst().getItemFirstRep();
    Encounter encounter =
        (Encounter)
            ((Reference) hospitalizedEncounter.getAnswerFirstRep().getValue()).getResource();
    encounter.setReasonCode(
        List.of(
            new CodeableConcept(
                new Coding()
                    .setSystem(REASON_CODE_SYSTEM)
                    .setCode(REASON_CODE)
                    .setDisplay(REASON_DISPLAY))));

    // when
    List<Hospitalization> hospitalizations = hospitalizationFactory.create(response);
    // then
    assertThat(hospitalizations).isNotNull().hasSize(2);
    Hospitalization hospitalization = hospitalizations.get(0);
    assertFirst(hospitalization);
    assertSecond(hospitalizations.get(1));
    assertThat(hospitalization.reason()).as("reason").isEqualTo(REASON_DISPLAY);
  }

  private void assertFirst(Hospitalization hospitalization) {
    assertThat(hospitalization.organizationType()).as("organization type").isEqualTo("Krankenhaus");
    OrganizationDTO firstOrganization = hospitalization.organization();
    assertThat(firstOrganization).as("organization").isNotNull();
    assertThat(firstOrganization.getName())
        .as("organization name")
        .isEqualTo("QuickHealing Hospital");
    assertThat(hospitalization.start()).as("begin").isNotNull().hasToString("05.01.2022 00:00");
    assertThat(hospitalization.end()).as("end").isNotNull().hasToString("09.01.2022 00:00");
    assertThat(hospitalization.note()).as("note").isEqualTo("wichtige Zusatzinformation");
    assertThat(hospitalization.serviceType()).as("service type").isNull();
  }

  private void assertSecond(Hospitalization hospitalization) {
    assertThat(hospitalization.organizationType()).as("organization type").isEqualTo("Krankenhaus");
    OrganizationDTO secondOrganization = hospitalization.organization();
    assertThat(secondOrganization).as("organization").isNotNull();
    assertThat(secondOrganization.getName())
        .as("organization name")
        .isEqualTo("QuickHealing Hospital");
    assertThat(hospitalization.start()).as("begin").isNotNull().hasToString("07.01.2022 00:00");
    assertThat(hospitalization.end()).as("empty end").isNotNull().hasToString("");
    assertThat(hospitalization.note()).as("note").isNull();
    assertThat(hospitalization.serviceType()).as("service type").isEqualTo("Intensivmedizin");
  }
}
