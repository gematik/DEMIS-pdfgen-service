package de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition;

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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.NotificationBundleLaboratoryDataBuilder;
import de.gematik.demis.pdfgen.fhir.extract.ConditionQueries;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConditionFactoryTest {

  private final Bundle bundle =
      new NotificationBundleLaboratoryDataBuilder()
          .setNotifiedPerson(new Patient())
          .setLaboratoryReport(new DiagnosticReport())
          .setSpecimen(List.of(new Specimen()))
          .build(); // .buildExampleLaboratoryBundle();

  @Mock private ConditionQueries conditionQueries;
  @Mock private TranslationService translationService;
  @InjectMocks private ConditionFactory conditionFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(this.conditionFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {

    CodeableConcept diseaseCode =
        new CodeableConcept(new Coding("diseases", "disease", "dontshowme"));
    String diseaseTranslation = "covid";
    when(this.translationService.resolveCodeableConceptValues(diseaseCode))
        .thenReturn(diseaseTranslation);

    CodeableConcept symptomCode = new CodeableConcept(new Coding("system", "code", "display"));
    Condition.ConditionEvidenceComponent evidence =
        new Condition.ConditionEvidenceComponent().addCode(symptomCode);
    List<Condition.ConditionEvidenceComponent> evidenceList = singletonList(evidence);
    String symptomTranslation = "symptom1";
    when(this.translationService.resolveCodeableConceptValues(symptomCode))
        .thenReturn(symptomTranslation);

    String note = "Fascinating!";
    Condition condition =
        new Condition()
            .setCode(diseaseCode)
            .setOnset(new DateTimeType("2022-01-01"))
            .setRecordedDateElement(new DateTimeType("2022-01-02"))
            .setEvidence(evidenceList)
            .setNote(singletonList(new Annotation().setText(note)));
    when(conditionQueries.getCondition(this.bundle)).thenReturn(Optional.of(condition));

    // given
    ConditionDTO actualConditionDTO = conditionFactory.create(this.bundle);

    // then
    assertThat(actualConditionDTO).isNotNull();
    assertThat(actualConditionDTO.disease()).as("disease name").isEqualTo(diseaseTranslation);
    assertThat(actualConditionDTO.onsetDate()).hasToString("01.01.2022");
    assertThat(actualConditionDTO.recordedDate()).hasToString("02.01.2022");
    assertThat(actualConditionDTO.symptoms()).containsExactly(symptomTranslation);
    assertThat(actualConditionDTO.note()).isEqualTo(note);
  }

  @Test
  void shouldNullifyEmptyAnnotation() {

    CodeableConcept diseaseCode =
        new CodeableConcept(new Coding("diseases", "disease", "dontshowme"));
    String diseaseTranslation = "covid";
    when(this.translationService.resolveCodeableConceptValues(diseaseCode))
        .thenReturn(diseaseTranslation);

    CodeableConcept symptomCode = new CodeableConcept(new Coding("system", "code", "display"));
    Condition.ConditionEvidenceComponent evidence =
        new Condition.ConditionEvidenceComponent().addCode(symptomCode);
    List<Condition.ConditionEvidenceComponent> evidenceList = singletonList(evidence);
    String symptomTranslation = "symptom1";
    when(this.translationService.resolveCodeableConceptValues(symptomCode))
        .thenReturn(symptomTranslation);

    Condition condition =
        new Condition()
            .setCode(diseaseCode)
            .setOnset(new DateTimeType("2022-01-01"))
            .setRecordedDateElement(new DateTimeType("2022-01-02"))
            .setEvidence(evidenceList)
            .setNote(singletonList(new Annotation().setText("   ")));
    when(conditionQueries.getCondition(this.bundle)).thenReturn(Optional.of(condition));

    // given
    ConditionDTO actualConditionDTO = conditionFactory.create(this.bundle);

    // then
    assertThat(actualConditionDTO).isNotNull();
    assertThat(actualConditionDTO.disease()).as("disease name").isEqualTo(diseaseTranslation);
    assertThat(actualConditionDTO.onsetDate()).hasToString("01.01.2022");
    assertThat(actualConditionDTO.recordedDate()).hasToString("02.01.2022");
    assertThat(actualConditionDTO.symptoms()).containsExactly(symptomTranslation);
    assertThat(actualConditionDTO.note()).isNull();
  }

  @Test
  void shouldAcceptMissingAnnotation() {
    CodeableConcept diseaseCode =
        new CodeableConcept(new Coding("diseases", "disease", "dontshowme"));
    String diseaseTranslation = "covid";
    when(this.translationService.resolveCodeableConceptValues(diseaseCode))
        .thenReturn(diseaseTranslation);

    CodeableConcept symptomCode = new CodeableConcept(new Coding("system", "code", "display"));
    Condition.ConditionEvidenceComponent evidence =
        new Condition.ConditionEvidenceComponent().addCode(symptomCode);
    List<Condition.ConditionEvidenceComponent> evidenceList = singletonList(evidence);
    String symptomTranslation = "symptom1";
    when(this.translationService.resolveCodeableConceptValues(symptomCode))
        .thenReturn(symptomTranslation);

    Condition condition =
        new Condition()
            .setCode(diseaseCode)
            .setOnset(new DateTimeType("2022-01-01"))
            .setRecordedDateElement(new DateTimeType("2022-01-02"))
            .setEvidence(evidenceList);
    when(conditionQueries.getCondition(this.bundle)).thenReturn(Optional.of(condition));

    // given
    ConditionDTO actualConditionDTO = conditionFactory.create(this.bundle);

    // then
    assertThat(actualConditionDTO).isNotNull();
    assertThat(actualConditionDTO.disease()).as("disease name").isEqualTo(diseaseTranslation);
    assertThat(actualConditionDTO.onsetDate()).hasToString("01.01.2022");
    assertThat(actualConditionDTO.recordedDate()).hasToString("02.01.2022");
    assertThat(actualConditionDTO.symptoms()).containsExactly(symptomTranslation);
    assertThat(actualConditionDTO.note()).as("missing annotation").isNull();
  }

  @Test
  void shouldReturnNullForNoCondition() {
    Optional<Condition> condition = Optional.empty();
    when(conditionQueries.getCondition(this.bundle)).thenReturn(condition);

    // given
    ConditionDTO actualConditionDTO = conditionFactory.create(bundle);

    // then
    assertThat(actualConditionDTO).isNull();
  }
}
