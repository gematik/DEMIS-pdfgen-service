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

import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressFactory;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.TelecomFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.BaseReference;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LaboratoryFactory {

  private static final String LAB_COMPONENT_NAME = "labspecimentaken";

  private final AddressFactory addressFactory;
  private final TelecomFactory telecomFactory;
  private final QuestionnaireResponseHelper questionnaireResponseHelper;

  public String wasLabSpecimenTaken(final QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponseHelper.getCodingDisplayAnswerValue(
        questionnaireResponse, LAB_COMPONENT_NAME);
  }

  @Nullable
  public Laboratory create(final QuestionnaireResponse questionnaireResponse) {
    if (questionnaireResponse == null) {
      return null;
    }
    Optional<Organization> labOptional = getLabAsOrganization(questionnaireResponse);
    if (labOptional.isEmpty()) {
      return null;
    }

    Organization labAsOrganization = labOptional.get();
    org.hl7.fhir.r4.model.Address fhirAddress = labAsOrganization.getAddressFirstRep();
    List<ContactPoint> contactPoints = labAsOrganization.getTelecom();

    String name = labAsOrganization.getName();
    String department = labAsOrganization.getTypeFirstRep().getText();
    AddressDTO addressDTO = addressFactory.create(fhirAddress, labAsOrganization);
    List<Telecom> telecoms = telecomFactory.createTelecoms(contactPoints);

    return Laboratory.builder()
        .name(name)
        .department(department)
        .addressDTO(addressDTO)
        .telecoms(telecoms)
        .build();
  }

  private Optional<Organization> getLabAsOrganization(QuestionnaireResponse questionnaireResponse) {
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> component =
        questionnaireResponseHelper.getComponent(questionnaireResponse, LAB_COMPONENT_NAME);
    if (component.isEmpty()) {
      return Optional.empty();
    }
    return component.get().getAnswer().stream()
        .filter(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::hasValue)
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getItem)
        .flatMap(Collection::stream)
        .filter(QuestionnaireResponse.QuestionnaireResponseItemComponent::hasAnswer)
        .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getAnswer)
        .flatMap(Collection::stream)
        .filter(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::hasValue)
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(Reference.class::isInstance)
        .map(Reference.class::cast)
        .map(BaseReference::getResource)
        .filter(Organization.class::isInstance)
        .map(Organization.class::cast)
        .findFirst();
  }
}
