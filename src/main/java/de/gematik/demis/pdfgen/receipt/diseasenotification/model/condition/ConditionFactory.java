package de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition;

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

import de.gematik.demis.pdfgen.FeatureFlags;
import de.gematik.demis.pdfgen.fhir.extract.ConditionQueries;
import de.gematik.demis.pdfgen.translation.TranslationService;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConditionFactory {

  private final ConditionQueries conditionQueries;
  private final TranslationService translationService;
  private final FeatureFlags featureFlags;

  @Nullable
  public ConditionDTO create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    return this.conditionQueries
        .getCondition(bundle)
        .map(fhirCondition -> create(fhirCondition, isNonNominal(bundle)))
        .orElse(null);
  }

  private ConditionDTO create(Condition fhirCondition, final boolean isNonNominal) {
    ConditionDTO.ConditionDTOBuilder builder = ConditionDTO.builder();
    setDisease(fhirCondition, builder);
    setOnsetDate(fhirCondition, builder);
    setRecordedDate(fhirCondition, builder);
    setSymptoms(fhirCondition, builder);
    setNotes(fhirCondition, builder);
    setClinicalStatus(fhirCondition, builder);
    setVerificationStatus(fhirCondition, builder);
    setDisplayDateFields(fhirCondition, isNonNominal, builder);
    setDisplaySymptoms(fhirCondition, builder);
    return builder.build();
  }

  private void setDisease(Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    builder.disease(this.translationService.resolveCodeableConceptValues(fhirCondition.getCode()));
    builder.diseaseCode(fhirCondition.getCode().getCodingFirstRep().getCode());
  }

  private void setDisplayDateFields(
      Condition fhirCondition, boolean isNonNominal, ConditionDTO.ConditionDTOBuilder builder) {
    final String diseaseCode = fhirCondition.getCode().getCodingFirstRep().getCode();
    final var relevantCodes = Set.of("toxd", "echd");
    final boolean showFields =
        !featureFlags.isWithoutDateFields73()
            || !isNonNominal
            || relevantCodes.contains(diseaseCode);

    builder.displayDateFields(showFields);
  }

  private void setDisplaySymptoms(
      Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    final String diseaseCode = fhirCondition.getCode().getCodingFirstRep().getCode();
    builder.displaySymptoms(!"hivd".equals(diseaseCode));
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
        .map(this.translationService::resolveCodeableConceptValues)
        .toList();
  }

  private void setNotes(Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    builder.notes(
        fhirCondition.getNote().stream()
            .map(Annotation::getText)
            .filter(StringUtils::isNotBlank)
            .toList());
  }

  private void setVerificationStatus(
      Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    final var status = fhirCondition.getVerificationStatus();
    if (status != null) {
      builder.verificationStatus(this.translationService.resolveCodeableConceptValues(status));
    }
  }

  private void setClinicalStatus(
      Condition fhirCondition, ConditionDTO.ConditionDTOBuilder builder) {
    final var status = fhirCondition.getClinicalStatus();
    if (status != null) {
      builder.clinicalStatus(this.translationService.resolveCodeableConceptValues(status));
    }
  }

  private boolean isNonNominal(final Bundle bundle) {
    final List<CanonicalType> metaProfile = bundle.getMeta().getProfile();
    if (metaProfile != null && !metaProfile.isEmpty()) {
      final String metaProfileUrl = metaProfile.getFirst().getValue();
      return metaProfileUrl.contains("NonNominal");
    }
    return false;
  }
}
