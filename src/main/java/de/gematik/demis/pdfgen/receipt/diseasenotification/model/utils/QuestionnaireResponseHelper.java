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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils;

import de.gematik.demis.pdfgen.translation.TranslationService;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.BaseReference;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionnaireResponseHelper {

  private final TranslationService displayTranslationService;

  @Nullable
  public String getCodingDisplayAnswerValue(
      QuestionnaireResponse questionnaireResponse, String name) {
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> component =
        getComponent(questionnaireResponse, name);
    return component.map(this::getCodingDisplayOfFirstAnswer).orElse(null);
  }

  @Nullable
  public String getAnswerStringValue(QuestionnaireResponse questionnaireResponse, String name) {
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> component =
        getComponent(questionnaireResponse, name);
    return component.map(this::getStringTypeOfFirstAnswer).orElse(null);
  }

  public Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> getComponent(
      QuestionnaireResponse questionnaireResponse, String name) {
    return questionnaireResponse.getItem().stream()
        .filter(item -> name.equalsIgnoreCase(item.getLinkId()))
        .findFirst();
  }

  private String getCodingDisplayOfFirstAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemComponent itemComponent) {
    return itemComponent.getAnswer().stream()
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(Coding.class::isInstance)
        .map(Coding.class::cast)
        .map(displayTranslationService::resolveCodeableConceptValues)
        .findFirst()
        .orElse("");
  }

  public String getStringTypeOfFirstAnswer(
      QuestionnaireResponse.QuestionnaireResponseItemComponent itemComponent) {

    return itemComponent.getAnswer().stream()
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(StringType.class::isInstance)
        .map(StringType.class::cast)
        .map(StringType::getValue)
        .findFirst()
        .orElse("");
  }

  public String getDisplayValueByLinkId(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> placeExposureValues,
      String linkId) {
    return placeExposureValues.stream()
        .filter(item -> linkId.equals(item.getLinkId()))
        .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getAnswer)
        .flatMap(Collection::stream)
        .filter(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::hasValue)
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(Coding.class::isInstance)
        .map(Coding.class::cast)
        .map(c -> c.hasDisplay() ? c.getDisplay() : c.getCode())
        .findFirst()
        .orElse(null);
  }

  @Nullable
  public DateTimeHolder getDateTypeByLinkId(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> placeExposureValues,
      String linkId) {
    return placeExposureValues.stream()
        .filter(item -> linkId.equals(item.getLinkId()))
        .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getAnswer)
        .flatMap(Collection::stream)
        .filter(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::hasValue)
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(DateType.class::isInstance)
        .map(DateType.class::cast)
        .map(DateTimeHolder::new)
        .findFirst()
        .orElse(null);
  }

  public Optional<org.hl7.fhir.r4.model.Organization> getOrganizationByLinkId(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> placeExposureValues,
      String linkId) {
    return placeExposureValues.stream()
        .filter(item -> linkId.equals(item.getLinkId()))
        .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getAnswer)
        .flatMap(Collection::stream)
        .filter(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::hasValue)
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(Reference.class::isInstance)
        .map(Reference.class::cast)
        .map(BaseReference::getResource)
        .filter(org.hl7.fhir.r4.model.Organization.class::isInstance)
        .map(org.hl7.fhir.r4.model.Organization.class::cast)
        .findFirst();
  }

  public String getStringTypeByLinkId(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> placeExposureValues,
      String linkId) {
    return placeExposureValues.stream()
        .filter(item -> linkId.equals(item.getLinkId()))
        .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getAnswer)
        .flatMap(Collection::stream)
        .filter(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::hasValue)
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(StringType.class::isInstance)
        .map(StringType.class::cast)
        .map(StringType::getValue)
        .findFirst()
        .orElse(null);
  }
}
