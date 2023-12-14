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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.commonquestions;

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExposurePlaceFactory {

  private static final String PLACE_EXPOSURE_COMPONENT_NAME = "placeexposure";

  private final QuestionnaireResponseHelper questionnaireResponseHelper;

  public String hasExposurePlace(final QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponseHelper.getCodingDisplayAnswerValue(
        questionnaireResponse, PLACE_EXPOSURE_COMPONENT_NAME);
  }

  @Nullable
  public List<ExposurePlace> create(final QuestionnaireResponse questionnaireResponse) {
    if (questionnaireResponse == null) {
      return null;
    }
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> componentOpt =
        questionnaireResponseHelper.getComponent(
            questionnaireResponse, PLACE_EXPOSURE_COMPONENT_NAME);
    if (componentOpt.isEmpty()) {
      return null;
    }
    QuestionnaireResponse.QuestionnaireResponseItemComponent component = componentOpt.get();

    if (!component.hasAnswer() || !component.getAnswerFirstRep().hasItem()) {
      return null;
    }

    Stream<List<QuestionnaireResponse.QuestionnaireResponseItemComponent>> exposurePlacesData =
        component.getAnswerFirstRep().getItem().stream()
            .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getItem);

    return exposurePlacesData.map(this::createSingleEntry).toList();
  }

  private ExposurePlace createSingleEntry(
      final List<QuestionnaireResponse.QuestionnaireResponseItemComponent> placeExposureValues) {
    String region =
        questionnaireResponseHelper.getDisplayValueByLinkId(
            placeExposureValues, "placeExposureRegion");
    DateTimeHolder begin =
        questionnaireResponseHelper.getDateTypeByLinkId(placeExposureValues, "placeExposureBegin");
    DateTimeHolder end =
        questionnaireResponseHelper.getDateTypeByLinkId(placeExposureValues, "placeExposureEnd");

    String note =
        questionnaireResponseHelper.getStringTypeByLinkId(placeExposureValues, "placeExposureHint");
    return ExposurePlace.builder().region(region).begin(begin).end(end).note(note).build();
  }
}
