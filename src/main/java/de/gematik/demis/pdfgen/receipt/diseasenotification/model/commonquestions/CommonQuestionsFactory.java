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

import de.gematik.demis.pdfgen.fhir.extract.ConditionQueries;
import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.DeathInfoHelper;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonQuestionsFactory {

  private static final String MILITARY_AFFILIATION_COMPONENT_NAME = "militaryaffiliation";
  private static final String ORGAN_DONATION_COMPONENT_NAME = "organdonation";
  private static final String NOTE_COMPONENT_NAME = "additionalinformation";

  private final ExposurePlaceFactory exposurePlaceFactory;
  private final HospitalizationFactory hospitalizationFactory;
  private final InfectProtectFacilityFactory infectProtectFacilityFactory;
  private final LaboratoryFactory laboratoryFactory;
  private final ConditionQueries conditionQueries;
  private final DeathInfoHelper deathInfoHelper;
  private final QuestionnaireResponseHelper questionnaireResponseHelper;

  @Nullable
  public CommonQuestions create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    Optional<QuestionnaireResponse> questionnaireResponseOptional =
        conditionQueries.getQuestionnaireResponse(bundle, DemisProfiles.DISEASE_INFORMATION_COMMON);
    if (questionnaireResponseOptional.isEmpty()) {
      return null;
    }

    QuestionnaireResponse questionnaireResponse = questionnaireResponseOptional.get();

    String isDead = deathInfoHelper.isDead(questionnaireResponse);
    DateTimeHolder deathDate = deathInfoHelper.getDeathDate(questionnaireResponse);

    String militaryAffiliation =
        questionnaireResponseHelper.getCodingDisplayAnswerValue(
            questionnaireResponse, MILITARY_AFFILIATION_COMPONENT_NAME);

    String wasLabSpecimenTaken = laboratoryFactory.wasLabSpecimenTaken(questionnaireResponse);
    Laboratory laboratory = laboratoryFactory.create(questionnaireResponse);

    String wasHospitalized = hospitalizationFactory.wasHospitalized(questionnaireResponse);
    List<Hospitalization> hospitalizations = hospitalizationFactory.create(questionnaireResponse);

    String wasInInfectProtectFacility =
        infectProtectFacilityFactory.wasInInfectProtectFacility(questionnaireResponse);
    InfectProtectFacility infectProtectFacility =
        infectProtectFacilityFactory.create(questionnaireResponse);

    String hasExposurePlace = exposurePlaceFactory.hasExposurePlace(questionnaireResponse);
    List<ExposurePlace> exposurePlaces = exposurePlaceFactory.create(questionnaireResponse);

    String organDonation =
        questionnaireResponseHelper.getCodingDisplayAnswerValue(
            questionnaireResponse, ORGAN_DONATION_COMPONENT_NAME);
    String note =
        questionnaireResponseHelper.getAnswerStringValue(
            questionnaireResponse, NOTE_COMPONENT_NAME);

    return CommonQuestions.builder()
        .isDead(isDead)
        .deathDate(deathDate)
        .hasMilitaryAffiliation(militaryAffiliation)
        .wasLabSpecimenTaken(wasLabSpecimenTaken)
        .laboratory(laboratory)
        .wasHospitalized(wasHospitalized)
        .hospitalizations(hospitalizations)
        .wasInInfectProtectFacility(wasInInfectProtectFacility)
        .infectProtectFacility(infectProtectFacility)
        .hasExposurePlace(hasExposurePlace)
        .exposurePlaces(exposurePlaces)
        .organDonation(organDonation)
        .note(note)
        .build();
  }
}
