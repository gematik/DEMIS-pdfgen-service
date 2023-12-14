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

import de.gematik.demis.pdfgen.fhir.extract.ConditionQueries;
import de.gematik.demis.pdfgen.translation.TranslationConditionProvider;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConditionFactory {

  private final ConditionQueries conditionQueries;
  private final TranslationConditionProvider translationConditionProvider;

  @Nullable
  public ConditionDTO create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    return this.conditionQueries.getCondition(bundle).map(this::create).orElse(null);
  }

  private ConditionDTO create(Condition fhirCondition) {
    ConditionDTO.ConditionDTOBuilder builder = ConditionDTO.builder();
    setOnsetDate(fhirCondition, builder);
    setRecordedDate(fhirCondition, builder);
    setSymptoms(fhirCondition, builder);
    setNote(fhirCondition, builder);
    return builder.build();
  }

  private void setOnsetDate(Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    builder.onsetDate(new DateTimeHolder(fhirCondition.getOnsetDateTimeType()));
  }

  private void setRecordedDate(Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    builder.recordedDate(new DateTimeHolder(fhirCondition.getRecordedDateElement()));
  }

  private void setSymptoms(Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    builder.symptoms(getSymptoms(fhirCondition));
  }

  @NotNull
  private List<String> getSymptoms(Condition fhirCondition) {
    return fhirCondition.getEvidence().stream()
        .map(ConditionEvidenceComponent::getCode)
        .flatMap(Collection::stream)
        .map(CodeableConcept::getCoding)
        .flatMap(Collection::stream)
        .map(this.translationConditionProvider::translateCode)
        .toList();
  }

  private void setNote(Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    String note =
        fhirCondition.getNote().stream()
            .map(Annotation::getText)
            .collect(Collectors.joining(System.lineSeparator()));
    if (StringUtils.isNotBlank(note)) {
      builder.note(note);
    }
  }
}
