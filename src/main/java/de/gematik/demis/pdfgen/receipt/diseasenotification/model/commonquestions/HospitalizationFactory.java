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

import static java.util.Collections.emptyList;

import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import de.gematik.demis.pdfgen.translation.TranslationService;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HospitalizationFactory {

  private static final String HOSPITALIZATION_COMPONENT_NAME = "hospitalized";

  private static final String CODE_SYSTEM_HOSPITALIZATION_TYPE =
      "https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType";

  private final OrganizationFactory organizationFactory;
  private final QuestionnaireResponseHelper questionnaireResponseHelper;
  private final TranslationService translationService;

  public String wasHospitalized(final QuestionnaireResponse questionnaireResponse) {
    return questionnaireResponseHelper.getCodingDisplayAnswerValue(
        questionnaireResponse, HOSPITALIZATION_COMPONENT_NAME);
  }

  public List<Hospitalization> create(final QuestionnaireResponse questionnaireResponse) {
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items =
        getItems(questionnaireResponse);
    return getEncounters(items).stream()
        .filter(Objects::nonNull)
        .map(this::createSingleEntry)
        .toList();
  }

  private List<QuestionnaireResponse.QuestionnaireResponseItemComponent> getItems(
      QuestionnaireResponse questionnaireResponse) {
    if (questionnaireResponse == null) {
      return emptyList();
    }
    Optional<QuestionnaireResponse.QuestionnaireResponseItemComponent> componentOpt =
        this.questionnaireResponseHelper.getComponent(
            questionnaireResponse, HOSPITALIZATION_COMPONENT_NAME);
    if (componentOpt.isEmpty()) {
      return emptyList();
    }
    List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent> answer =
        componentOpt.get().getAnswer();
    if (answer.isEmpty() || !answer.get(0).hasItem()) {
      return emptyList();
    }
    return answer.get(0).getItem();
  }

  private Hospitalization createSingleEntry(final Encounter encounter) {
    Hospitalization.HospitalizationBuilder builder = Hospitalization.builder();
    setPeriod(encounter, builder);
    setDepartment(encounter, builder);
    setOrganization(encounter, builder);
    return builder.build();
  }

  private static void setPeriod(
      Encounter encounter, Hospitalization.HospitalizationBuilder builder) {
    Period period = encounter.getPeriod();
    if (period != null) {
      builder.begin(new DateTimeHolder(period.getStartElement()));
      builder.end(new DateTimeHolder(period.getEndElement()));
    }
  }

  private void setOrganization(
      Encounter encounter, Hospitalization.HospitalizationBuilder builder) {
    Reference serviceProvider = encounter.getServiceProvider();
    if ((serviceProvider != null)
        && (serviceProvider.getResource() instanceof Organization fhirOrganization)) {
      setOrganization(encounter, builder, fhirOrganization);
    }
  }

  private void setOrganization(
      Encounter encounter,
      Hospitalization.HospitalizationBuilder builder,
      Organization fhirOrganization) {
    OrganizationDTO organizationDTO =
        this.organizationFactory.create(Optional.of(fhirOrganization));
    builder.organizationDTO(organizationDTO);
    setServiceType(builder, fhirOrganization);
    String note =
        encounter.getExtension().stream()
            .filter(e -> e.getUrl().equals(DemisExtensions.EXTENSION_URL_HOSPITALIZATION_NOTE))
            .map(e -> String.valueOf(e.getValue()))
            .findFirst()
            .orElse(null);
    builder.note(note);
  }

  /**
   * Sets service type, defined by: <a
   * href="https://demis.rki.de/fhir/CodeSystem/organizationType">DEMIS organization type</a>
   *
   * @param builder builder
   * @param fhirOrganization organization
   */
  private void setServiceType(
      Hospitalization.HospitalizationBuilder builder, Organization fhirOrganization) {
    Coding serviceType = fhirOrganization.getTypeFirstRep().getCodingFirstRep();
    String serviceTypeDisplay = this.translationService.resolveCodeableConceptValues(serviceType);
    builder.serviceType(serviceTypeDisplay);
  }

  private static List<Encounter> getEncounters(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    return items.stream()
        .flatMap(i -> i.getItem().stream())
        .flatMap(i -> i.getAnswer().stream())
        .filter(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::hasValue)
        .map(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent::getValue)
        .filter(Reference.class::isInstance)
        .map(Reference.class::cast)
        .map(BaseReference::getResource)
        .filter(Objects::nonNull)
        .filter(Encounter.class::isInstance)
        .map(Encounter.class::cast)
        .toList();
  }

  private void setDepartment(Encounter encounter, Hospitalization.HospitalizationBuilder builder) {
    encounter.getServiceType().getCoding().stream()
        .filter(HospitalizationFactory::isHospitalizationType)
        .findFirst()
        .map(this.translationService::resolveCodeableConceptValues)
        .ifPresent(builder::department);
  }

  private static boolean isHospitalizationType(Coding coding) {
    return CODE_SYSTEM_HOSPITALIZATION_TYPE.equals(coding.getSystem());
  }
}
