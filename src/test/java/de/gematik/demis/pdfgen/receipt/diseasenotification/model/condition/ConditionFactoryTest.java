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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.infectious.laboratory.NotificationBundleLaboratoryDataBuilder;
import de.gematik.demis.pdfgen.fhir.extract.ConditionQueries;
import de.gematik.demis.pdfgen.translation.TranslationConditionProvider;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConditionFactoryTest {

  private final Bundle bundle =
      new NotificationBundleLaboratoryDataBuilder().buildExampleLaboratoryBundle();

  private ConditionFactory conditionFactory;
  @Mock private ConditionQueries conditionQueries;
  @Mock private TranslationConditionProvider translationConditionProvider;

  @BeforeEach
  void setUp() {
    conditionFactory = new ConditionFactory(conditionQueries, translationConditionProvider);
  }

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(conditionFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    Coding code = new Coding("system", "code", "display");
    Condition.ConditionEvidenceComponent evidence =
        new Condition.ConditionEvidenceComponent().addCode(new CodeableConcept(code));
    List<Condition.ConditionEvidenceComponent> evidenceList = singletonList(evidence);
    //
    // when(displayTranslationService.resolveCodeableConceptValues(code)).thenReturn("translation");
    when(translationConditionProvider.translateCode(code)).thenReturn("translation");
    String note = "Fascinating!";
    Condition condition =
        new Condition()
            .setOnset(new DateTimeType("2022-01-01"))
            .setRecordedDateElement(new DateTimeType("2022-01-02"))
            .setEvidence(evidenceList)
            .setNote(Arrays.asList(new Annotation().setText(note)));
    Optional<Condition> conditionOpt = Optional.of(condition);
    when(conditionQueries.getCondition(this.bundle)).thenReturn(conditionOpt);

    // given
    ConditionDTO actualConditionDTO = conditionFactory.create(this.bundle);

    // then
    assertThat(actualConditionDTO).isNotNull();
    assertThat(actualConditionDTO.onsetDate()).hasToString("01.01.2022");
    assertThat(actualConditionDTO.recordedDate()).hasToString("02.01.2022");
    assertThat(actualConditionDTO.symptoms()).containsExactly("translation");
    assertThat(actualConditionDTO.note()).isEqualTo(note);
  }

  @Test
  void shouldNullifyEmptyAnnotation() {
    Coding code = new Coding("system", "code", "display");
    Condition.ConditionEvidenceComponent evidence =
        new Condition.ConditionEvidenceComponent().addCode(new CodeableConcept(code));
    List<Condition.ConditionEvidenceComponent> evidenceList = singletonList(evidence);
    //
    // when(displayTranslationService.resolveCodeableConceptValues(code)).thenReturn("translation");
    when(translationConditionProvider.translateCode(code)).thenReturn("translation");
    Condition condition =
        new Condition()
            .setOnset(new DateTimeType("2022-01-01"))
            .setRecordedDateElement(new DateTimeType("2022-01-02"))
            .setEvidence(evidenceList)
            .setNote(Arrays.asList(new Annotation().setText(" ")));
    Optional<Condition> conditionOpt = Optional.of(condition);
    when(conditionQueries.getCondition(this.bundle)).thenReturn(conditionOpt);

    // given
    ConditionDTO actualConditionDTO = conditionFactory.create(this.bundle);

    // then
    assertThat(actualConditionDTO).isNotNull();
    assertThat(actualConditionDTO.onsetDate()).hasToString("01.01.2022");
    assertThat(actualConditionDTO.recordedDate()).hasToString("02.01.2022");
    assertThat(actualConditionDTO.symptoms()).containsExactly("translation");
    assertThat(actualConditionDTO.note()).isNull();
  }

  @Test
  void shouldAcceptMissingAnnotation() {
    Coding code = new Coding("system", "code", "display");
    Condition.ConditionEvidenceComponent evidence =
        new Condition.ConditionEvidenceComponent().addCode(new CodeableConcept(code));
    List<Condition.ConditionEvidenceComponent> evidenceList = singletonList(evidence);
    //
    // when(displayTranslationService.resolveCodeableConceptValues(code)).thenReturn("translation");
    when(translationConditionProvider.translateCode(code)).thenReturn("translation");
    Condition condition =
        new Condition()
            .setOnset(new DateTimeType("2022-01-01"))
            .setRecordedDateElement(new DateTimeType("2022-01-02"))
            .setEvidence(evidenceList);
    Optional<Condition> conditionOpt = Optional.of(condition);
    when(conditionQueries.getCondition(this.bundle)).thenReturn(conditionOpt);

    // given
    ConditionDTO actualConditionDTO = conditionFactory.create(this.bundle);

    // then
    assertThat(actualConditionDTO).isNotNull();
    assertThat(actualConditionDTO.onsetDate()).hasToString("01.01.2022");
    assertThat(actualConditionDTO.recordedDate()).hasToString("02.01.2022");
    assertThat(actualConditionDTO.symptoms()).containsExactly("translation");
    assertThat(actualConditionDTO.note()).isNull();
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
