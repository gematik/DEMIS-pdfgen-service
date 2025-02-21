/*
 * Copyright [2024], gematik GmbH
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
package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 gematik GmbH
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
 * #L%
 */

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
import org.hl7.fhir.r4.model.BaseReference;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class HospitalizationFactory {

  private static final String HOSPITALIZATION_COMPONENT_NAME = "hospitalized";

  private static final String CODE_SYSTEM_HOSPITALIZATION_TYPE =
      "https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType";

  private final OrganizationFactory organizationFactory;
  private final QuestionnaireResponseHelper questionnaireResponseHelper;
  private final TranslationService translationService;

  private static DateTimeHolder getStart(Encounter encounter) {
    final Period period = encounter.getPeriod();
    return period != null ? new DateTimeHolder(period.getStartElement()) : null;
  }

  private static DateTimeHolder getEnd(Encounter encounter) {
    final Period period = encounter.getPeriod();
    return period != null ? new DateTimeHolder(period.getEndElement()) : null;
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

  private static boolean isHospitalizationType(Coding coding) {
    return CODE_SYSTEM_HOSPITALIZATION_TYPE.equals(coding.getSystem());
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
    String serviceType = getServiceType(encounter);
    DateTimeHolder start = getStart(encounter);
    DateTimeHolder end = getEnd(encounter);
    String note = getNote(encounter);
    String organizationType = getOrganizationType(encounter);
    OrganizationDTO organization = getOrganization(encounter);
    String reason = getReason(encounter);
    return new Hospitalization(
        serviceType, start, end, note, organizationType, organization, reason);
  }

  private String getReason(Encounter encounter) {
    List<CodeableConcept> concepts = encounter.getReasonCode();
    if ((concepts != null) && !concepts.isEmpty()) {
      return this.translationService.resolveCodeableConceptValues(concepts.getFirst());
    }
    return null;
  }

  private String getServiceType(Encounter encounter) {
    return encounter.getServiceType().getCoding().stream()
        .filter(HospitalizationFactory::isHospitalizationType)
        .findFirst()
        .map(this.translationService::resolveCodeableConceptValues)
        .orElse(null);
  }

  private String getNote(Encounter encounter) {
    return encounter.getExtension().stream()
        .filter(e -> e.getUrl().equals(DemisExtensions.EXTENSION_URL_HOSPITALIZATION_NOTE))
        .map(e -> String.valueOf(e.getValue()))
        .findFirst()
        .orElse(null);
  }

  private String getOrganizationType(Encounter encounter) {
    final Reference serviceProvider = encounter.getServiceProvider();
    if ((serviceProvider != null)
        && (serviceProvider.getResource() instanceof Organization fhirOrganization)) {
      final Coding serviceType = fhirOrganization.getTypeFirstRep().getCodingFirstRep();
      return this.translationService.resolveCodeableConceptValues(serviceType);
    }
    return null;
  }

  private OrganizationDTO getOrganization(Encounter encounter) {
    final Reference serviceProvider = encounter.getServiceProvider();
    if (serviceProvider != null
        && serviceProvider.getResource() instanceof Organization fhirOrganization) {
      return this.organizationFactory.create(Optional.of(fhirOrganization));
    }
    return null;
  }
}
