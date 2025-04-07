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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Answer;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Item;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.AnswerValues;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.Context;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.translation.QuestionnaireTranslation;
import de.gematik.demis.pdfgen.test.helper.FhirFactory;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LabSpecimenTakenFactoryTest {

  private static QuestionnaireResponse common;
  private static QuestionnaireResponse.QuestionnaireResponseItemComponent labSpecimenTakenItem;

  @Mock private OrganizationFactory organizationFactory;
  @Mock private AnswerValues answerValues;
  @Mock private QuestionnaireTranslation questionnaireTranslation;
  @InjectMocks private LabSpecimenTakenFactory labSpecimenTakenFactory;

  @BeforeAll
  static void createLabSpecimenTakenItem() {
    String file = "bundles/disease/disease-bpsd-02.xml";
    String xml = FhirFactory.readResourceFile(file);
    Bundle bpsd02 = FhirFactory.createDiseaseNotificationBundle(xml);
    common = (QuestionnaireResponse) bpsd02.getEntry().get(13).getResource();
    labSpecimenTakenItem = common.getItem().get(2);
  }

  @Test
  void isLabSpecimenTaken_shouldDetectItem() {
    assertTrue(this.labSpecimenTakenFactory.isLabSpecimenTaken(labSpecimenTakenItem));
  }

  @Test
  void isLabSpecimenTaken_shouldDismissItem() {
    assertFalse(this.labSpecimenTakenFactory.isLabSpecimenTaken(common.getItem().getFirst()));
  }

  @Test
  void createLabSpecimenTaken_shouldCreateItem() {

    // when
    when(this.answerValues.apply(any())).thenReturn("Ja");
    OrganizationDTO organization = OrganizationDTO.builder().name("laboratory").build();
    when(this.organizationFactory.create((Organization) any())).thenReturn(organization);
    String text = "Beauftragtes Labor";
    when(this.questionnaireTranslation.item(LabSpecimenTakenFactory.LINK_ID)).thenReturn(text);

    // then
    Item item =
        this.labSpecimenTakenFactory.createLabSpecimenTaken(
            labSpecimenTakenItem, new Context(null, "bpsd", this.questionnaireTranslation));
    assertThat(item).isNotNull();
    assertThat(item.isResource()).as("top-level item is no resource").isFalse();
    assertThat(item.isResourceBlock()).as("top-level item is resource block").isTrue();
    assertThat(item.getText()).isEqualTo(text);

    List<Answer> answers = item.getAnswers();
    assertThat(answers).as("answers").hasSize(1);
    Answer answer = answers.getFirst();
    assertThat(answer).isNotNull();
    List<Item> subitems = answer.subitems();
    assertThat(subitems).hasSize(1);
    Item subitem = subitems.getFirst();
    assertThat(subitem).isNotNull();
    assertThat(subitem.isResource()).as("subitem is resource").isTrue();
    assertThat(subitem.isResourceBlock()).as("subitem is no resource block").isFalse();
    Resource resource = subitem.getResource();
    assertThat(resource).isNotNull();
    OrganizationDTO laboratory = resource.getLaboratory();
    assertThat(laboratory).isNotNull().isSameAs(organization);
  }
}
