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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.covid19questions;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import de.gematik.demis.pdfgen.translation.TranslationImmunizationProvider;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class ImmunizationFactory {

  private static final String IMMUNIZATION_COMPONENT_NAME = "immunization";
  private final QuestionnaireResponseHelper questionnaireResponseHelper;

  private final TranslationImmunizationProvider translationImmunizationProvider;

  public String hasImmunization(final QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponseHelper.getCodingDisplayAnswerValue(
        questionnaireResponse, IMMUNIZATION_COMPONENT_NAME);
  }

  public List<Immunization> create(
      final QuestionnaireResponse questionnaireResponse, String diseaseCode) {
    if (questionnaireResponse == null) {
      return emptyList();
    }
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> componentOpt =
        questionnaireResponseHelper.getComponent(
            questionnaireResponse, IMMUNIZATION_COMPONENT_NAME);
    if (componentOpt.isEmpty()) {
      return emptyList();
    }
    QuestionnaireResponse.QuestionnaireResponseItemComponent component = componentOpt.get();

    List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answerToImmunization =
        component.getAnswer();
    if (answerToImmunization.get(0).getItem().isEmpty()) {
      return emptyList();
    }

    List<org.hl7.fhir.r4.model.Immunization> fhirImmunizations =
        createImmunizationList(answerToImmunization);
    if (CollectionUtils.isEmpty(fhirImmunizations)) {
      return emptyList();
    }

    return fhirImmunizations.stream()
        .sorted(comparing(o -> o.getOccurrence().toString()))
        .map(fhirImmunization -> createSingleEntry(fhirImmunization, diseaseCode))
        .toList();
  }

  private Immunization createSingleEntry(
      final org.hl7.fhir.r4.model.Immunization fhirImmunization, String diseaseCode) {

    String vaccineCodeText =
        translationImmunizationProvider.translateCode(fhirImmunization.getVaccineCode());

    DateTimeHolder occurrenceDate =
        new DateTimeHolder(fhirImmunization.getOccurrenceDateTimeType());
    String note = fhirImmunization.hasNote() ? fhirImmunization.getNote().get(0).getText() : null;

    return Immunization.builder()
        .vaccineCodeText(vaccineCodeText)
        .occurrenceDate(occurrenceDate)
        .note(note)
        .build();
  }

  private List<org.hl7.fhir.r4.model.Immunization> createImmunizationList(
      List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answerToImmunization) {
    List<org.hl7.fhir.r4.model.Immunization> immunizationList = new LinkedList<>();
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> immunizationItems =
        answerToImmunization.get(0).getItem();
    for (QuestionnaireResponse.QuestionnaireResponseItemComponent immunizationRefItem :
        immunizationItems) {
      Type value = immunizationRefItem.getAnswer().get(0).getValue();
      IBaseResource immunizationToAddResource = ((Reference) value).getResource();
      if (immunizationToAddResource
          instanceof org.hl7.fhir.r4.model.Immunization immunizationToAdd) {
        immunizationList.add(immunizationToAdd);
      }
    }
    return immunizationList;
  }
}
