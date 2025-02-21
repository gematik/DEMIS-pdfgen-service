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
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Answer;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Item;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.AnswerValues;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory.Context;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.translation.QuestionnaireTranslation;
import de.gematik.demis.pdfgen.test.helper.FhirFactory;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.StringType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImmunizationBlockFactoryTest {

  private static final String CVDD_VACCINE_DISPLAY = "Comirnaty";
  private static final String BPSD_VACCINE_DISPLAY =
      "Diptherie-Tetanus-Pertussis(azellulär)-Poliomyelitis (inaktiviert)-Adsorbat-Impfstoff, reduzierte Antigenmenge (Tetravac)";
  private static final String IMMUNIZATION_ROOT_TEXT =
      "Wurde die betroffene Person jemals in Bezug auf die Krankheit geimpft?";
  private static final String IMMUNIZATION_ITEM_TEXT = "Impfinformationen";

  private static final String IMMU_MOTHER_ROOT_TEXT =
      "Wenn es sich bei der betroffenen Person um einen Säugling handelt: Wurde die Mutter in der Schwangerschaft in Bezug auf die Krankheit geimpft?";
  private static final String IMMU_MOTHER_ITEM_TEXT = "Impfinformationen der Mutter";
  private static final QuestionnaireResponse CVDD_QUESTIONNAIRE_RESPONSE =
      FhirFactory.createCovidQuestionnaireResponse();
  private static final QuestionnaireResponse.QuestionnaireResponseItemComponent
      CVDD_IMMUNIZATION_ITEM = CVDD_QUESTIONNAIRE_RESPONSE.getItem().get(2);
  private static QuestionnaireResponse bpsdQuestionnaireResponse;

  @Mock private TranslationService translationService;
  @Mock private AnswerValues answerValues;
  @Mock private QuestionnaireTranslation questionnaireTranslation;
  @InjectMocks private ImmunizationBlockFactory immunizationBlockFactory;

  @BeforeAll
  static void createBpsdQuestionnaireResponse() {
    String file = "bundles/disease/disease-bpsd-02.xml";
    String xml = FhirFactory.readResourceFile(file);
    Bundle bpsd02 = FhirFactory.createDiseaseNotificationBundle(xml);
    bpsdQuestionnaireResponse = (QuestionnaireResponse) bpsd02.getEntry().get(14).getResource();
  }

  private static Predicate<Immunization> hasVaccineComirnaty() {
    return i -> StringUtils.equals(i.vaccine(), CVDD_VACCINE_DISPLAY);
  }

  private static Predicate<Immunization> hasVaccineBpsd() {
    return i -> StringUtils.equals(i.vaccine(), BPSD_VACCINE_DISPLAY);
  }

  private static Predicate<Item> isImmunizationResource() {
    return i -> i.isResource() && i.getResource().getImmunization() != null;
  }

  private static Predicate<Item> hasImmunizationItemText() {
    return i -> i.getText().equals(IMMUNIZATION_ITEM_TEXT);
  }

  private static Predicate<Item> hasImmunizationMotherItemText() {
    return i -> i.getText().equals(IMMU_MOTHER_ITEM_TEXT);
  }

  private static Predicate<Immunization> hasOccurence() {
    return i -> i.occurrence() != null;
  }

  private static Predicate<Immunization> hasNote() {
    return i -> StringUtils.isNotBlank(i.note());
  }

  @DisplayName("test handles null-input gracefully")
  @Test
  void test_shouldHandleNullGracefully() {
    assertThat(this.immunizationBlockFactory.isImmunizationBlock(null)).isFalse();
  }

  @DisplayName("test detects block of immunization")
  @Test
  void test_shouldDetectImmunizationBlock() {
    assertThat(this.immunizationBlockFactory.isImmunizationBlock(CVDD_IMMUNIZATION_ITEM))
        .as("immunization block check")
        .isTrue();
  }

  @DisplayName("test detects block of immunization of mother")
  @Test
  void test_shouldDetectImmunizationMotherBlock() {
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items =
        bpsdQuestionnaireResponse.getItem();
    assertThat(this.immunizationBlockFactory.isImmunizationBlock(items.get(1))).isTrue();
  }

  @DisplayName("test dismisses empty immunization block")
  @Test
  void test_shouldDismissEmptyImmunizationItem() {
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items =
        bpsdQuestionnaireResponse.getItem();
    assertThat(this.immunizationBlockFactory.isImmunizationBlock(items.getFirst())).isFalse();
  }

  @DisplayName("test dismisses other item")
  @Test
  void test_shouldDismissOtherItems() {
    assertThat(
            this.immunizationBlockFactory.isImmunizationBlock(
                CVDD_QUESTIONNAIRE_RESPONSE.getItem().getFirst()))
        .as("arbitrary item that is no immunization block")
        .isFalse();
  }

  @DisplayName("apply should render block of immunizations")
  @Test
  void apply_shouldRenderImmunizationBlock() {
    // when
    when(this.answerValues.apply(any())).thenReturn("Ja");
    when(this.questionnaireTranslation.item("immunization")).thenReturn(IMMUNIZATION_ROOT_TEXT);
    when(this.questionnaireTranslation.item("immunizationRef")).thenReturn(IMMUNIZATION_ITEM_TEXT);
    when(this.translationService.resolveCodeableConceptValues((CodeableConcept) any()))
        .thenReturn(CVDD_VACCINE_DISPLAY);
    Context context = new Context(null, "cvdd", this.questionnaireTranslation);

    // then
    var fhirItem = CVDD_IMMUNIZATION_ITEM;
    assertThat(this.immunizationBlockFactory.isImmunizationBlock(fhirItem)).isTrue();
    Item item = this.immunizationBlockFactory.createItem(fhirItem, context);

    // verify
    assertThat(item).isNotNull();
    assertThat(item.getText()).isEqualTo(IMMUNIZATION_ROOT_TEXT);
    List<Answer> answers = item.getAnswers();
    assertThat(answers).hasSize(1);
    Answer answer = answers.getFirst();
    assertThat(answer.value()).isEqualTo("Ja");
    List<Item> subitems = answer.subitems();
    assertThat(subitems)
        .as("number of immunizations")
        .hasSize(4)
        .as("item texts")
        .allMatch(hasImmunizationItemText())
        .as("immunization resources set")
        .allMatch(isImmunizationResource())
        .map(i -> i.getResource().getImmunization())
        .as("vaccine texts")
        .anyMatch(hasVaccineComirnaty())
        .as("occurrence is set")
        .allMatch(hasOccurence())
        .as("note is set")
        .anyMatch(hasNote());
  }

  @DisplayName("apply should render block of immunizations of mother")
  @Test
  void apply_shouldRenderImmunizationMotherBlock() {
    // when
    when(this.answerValues.apply(any())).thenReturn("Ja");
    when(this.questionnaireTranslation.item("immunizationMother"))
        .thenReturn(IMMU_MOTHER_ROOT_TEXT);
    when(this.questionnaireTranslation.item("immunizationMotherRef"))
        .thenReturn(IMMU_MOTHER_ITEM_TEXT);
    when(this.translationService.resolveCodeableConceptValues((CodeableConcept) any()))
        .thenReturn(BPSD_VACCINE_DISPLAY);
    Context context = new Context(null, "bpsd", this.questionnaireTranslation);

    // then
    var fhirItem = bpsdQuestionnaireResponse.getItem().get(1);
    assertThat(this.immunizationBlockFactory.isImmunizationBlock(fhirItem)).isTrue();
    Item item = this.immunizationBlockFactory.createItem(fhirItem, context);

    // verify
    assertThat(item).isNotNull();
    assertThat(item.getText()).isEqualTo(IMMU_MOTHER_ROOT_TEXT);
    List<Answer> answers = item.getAnswers();
    assertThat(answers).hasSize(1);
    Answer answer = answers.getFirst();
    assertThat(answer.value()).isEqualTo("Ja");
    List<Item> subitems = answer.subitems();
    assertThat(subitems)
        .as("number of immunizations")
        .hasSize(5)
        .as("item texts")
        .allMatch(hasImmunizationMotherItemText())
        .as("mother immunization resources set")
        .allMatch(isImmunizationResource())
        .map(i -> i.getResource().getImmunization())
        .as("vaccine texts")
        .anyMatch(hasVaccineBpsd())
        .as("occurrence is set")
        .allMatch(hasOccurence())
        .as("note is set")
        .anyMatch(hasNote());
  }

  @Test
  void apply_shouldSupportOccurrenceString() {

    // when
    QuestionnaireResponse.QuestionnaireResponseItemComponent block =
        FhirFactory.createCovidQuestionnaireResponse().getItem().get(2);
    assertThat(this.immunizationBlockFactory.isImmunizationBlock(block)).isTrue();
    org.hl7.fhir.r4.model.Immunization immunization =
        (org.hl7.fhir.r4.model.Immunization)
            block
                .getAnswerFirstRep()
                .getItem()
                .getFirst()
                .getAnswerFirstRep()
                .getValueReference()
                .getResource();
    assertThat(immunization).isNotNull();
    immunization.setOccurrence(new StringType("unknown"));

    // then
    Item item =
        this.immunizationBlockFactory.createItem(
            block, new Context(null, "cvdd", this.questionnaireTranslation));

    // verify
    assertThat(item).isNotNull();
    List<Answer> answers = item.getAnswers();
    assertThat(answers).hasSize(1);
    Answer answer = answers.getFirst();
    List<Item> subitems = answer.subitems();
    assertThat(subitems).as("number of immunizations").hasSize(4);
    Item renderedImmunization = subitems.get(3);
    assertThat(renderedImmunization.getResource().getImmunization().occurrence())
        .as("text-based occurrence")
        .isEqualTo("unknown");
  }
}
