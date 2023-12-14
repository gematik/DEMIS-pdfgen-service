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

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfectProtectFacilityFactory {
  private static final String INFECT_PROTECT_FACILITY_COMPONENT_NAME = "infectprotectfacility";
  private static final String FACILITY_STAY_BEGIN_DATE = "infectProtectFacilityBegin";
  private static final String FACILITY_STAY_END_DATE = "infectProtectFacilityEnd";
  private static final String FACILITY_ROLE = "infectProtectFacilityRole";
  private static final String FACILITY_ORGANIZATION = "infectProtectFacilityOrganization";

  private final OrganizationFactory organizationFactory;

  private final QuestionnaireResponseHelper questionnaireResponseHelper;

  public String wasInInfectProtectFacility(final QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponseHelper.getCodingDisplayAnswerValue(
        questionnaireResponse, INFECT_PROTECT_FACILITY_COMPONENT_NAME);
  }

  @Nullable
  public InfectProtectFacility create(final QuestionnaireResponse questionnaireResponse) {
    if (questionnaireResponse == null) {
      return null;
    }
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> componentOpt =
        questionnaireResponseHelper.getComponent(
            questionnaireResponse, INFECT_PROTECT_FACILITY_COMPONENT_NAME);
    if (componentOpt.isEmpty()) {
      return null;
    }
    QuestionnaireResponse.QuestionnaireResponseItemComponent component = componentOpt.get();

    if (!component.hasAnswer() || !component.getAnswerFirstRep().hasItem()) {
      return null;
    }

    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> infectProtectFacility =
        component.getAnswerFirstRep().getItemFirstRep().getItem();

    DateTimeHolder begin =
        questionnaireResponseHelper.getDateTypeByLinkId(
            infectProtectFacility, FACILITY_STAY_BEGIN_DATE);
    DateTimeHolder end =
        questionnaireResponseHelper.getDateTypeByLinkId(
            infectProtectFacility, FACILITY_STAY_END_DATE);
    String role =
        questionnaireResponseHelper.getDisplayValueByLinkId(infectProtectFacility, FACILITY_ROLE);
    Optional<org.hl7.fhir.r4.model.Organization> fhirOrganizationOptional =
        questionnaireResponseHelper.getOrganizationByLinkId(
            infectProtectFacility, FACILITY_ORGANIZATION);
    OrganizationDTO organizationDTO = organizationFactory.create(fhirOrganizationOptional);

    return InfectProtectFacility.builder()
        .begin(begin)
        .end(end)
        .role(role)
        .organizationDTO(organizationDTO)
        .build();
  }
}
