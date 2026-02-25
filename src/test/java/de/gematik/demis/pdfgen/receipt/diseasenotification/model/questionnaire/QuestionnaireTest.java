package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources.Hospitalization;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class QuestionnaireTest {

  @Test
  void findLaboratoryItem_shouldReturnEmptyWhenItemsIsNull() {
    // given
    Questionnaire questionnaire = new Questionnaire("Test", null, "completed");

    // when
    Optional<Item> result = questionnaire.findLaboratoryItem();

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void findLaboratoryItem_shouldReturnEmptyWhenItemsIsEmpty() {
    // given
    Questionnaire questionnaire = new Questionnaire("Test", Collections.emptyList(), "completed");

    // when
    Optional<Item> result = questionnaire.findLaboratoryItem();

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void findLaboratoryItem_shouldReturnEmptyWhenAnswerHasNoSubitems() {
    // given
    Item simpleItem = new Item("Simple question", new Answer("Yes", Collections.emptyList()));
    Questionnaire questionnaire = new Questionnaire("Test", List.of(simpleItem), "completed");

    // when
    Optional<Item> result = questionnaire.findLaboratoryItem();

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void findLaboratoryItem_shouldReturnLaboratoryItemWhenPresent() {
    // given
    OrganizationDTO labOrganization = OrganizationDTO.builder().name("Test Laboratory").build();
    Resource labResource = Resource.laboratory(labOrganization);
    Item labSubitem = new Item("Beauftragtes Labor", labResource);

    Answer answerWithLabSubitem = new Answer("Ja", List.of(labSubitem));
    Item labSpecimenTakenItem =
        new Item("Wurde ein Labor mit der Diagnostik beauftragt?", List.of(answerWithLabSubitem));

    Questionnaire questionnaire =
        new Questionnaire("Test", List.of(labSpecimenTakenItem), "completed");

    // when
    Optional<Item> result = questionnaire.findLaboratoryItem();

    // then
    assertThat(result).contains(labSpecimenTakenItem);
  }

  @Test
  void findLaboratoryItem_shouldReturnEmptyWhenResourceIsNotLaboratory() {
    // given
    Hospitalization hospitalization = new Hospitalization(null, null, null, null, null, null, null);
    Resource hospitalizationResource = Resource.hospitalization(hospitalization);
    Item subitem = new Item("Hospitalization", hospitalizationResource);

    Answer answer = new Answer("Ja", List.of(subitem));
    Item item = new Item("Question with non-laboratory resource", List.of(answer));

    Questionnaire questionnaire = new Questionnaire("Test", List.of(item), "completed");

    // when
    Optional<Item> result = questionnaire.findLaboratoryItem();

    // then
    assertThat(result).isEmpty();
  }

  @Test
  void findLaboratoryItem_shouldReturnEmptyWhenAnswersIsEmpty() {
    // given
    Item itemWithNoAnswers = new Item("Question", Collections.emptyList());
    Questionnaire questionnaire =
        new Questionnaire("Test", List.of(itemWithNoAnswers), "completed");

    // when
    Optional<Item> result = questionnaire.findLaboratoryItem();

    // then
    assertThat(result).isEmpty();
  }
}
