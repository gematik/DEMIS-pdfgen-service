package de.gematik.demis.pdfgen.receipt.common.model.subsection;

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

import de.gematik.demis.pdfgen.fhir.extract.NotifierFhirQueries;
import de.gematik.demis.pdfgen.fhir.extract.SubmitterFhirQueries;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityFactory {

  private final NotifierFhirQueries notifierFhirQueries;
  private final SubmitterFhirQueries submitterFhirQueries;
  private final AddressFactory addressFactory;
  private final TelecomFactory telecomFactory;
  private final ContactFactory contactFactory;
  private final IdentifierFactory identifierFactory;
  private final TranslationService displayTranslationService;

  @Nullable
  public Facility createNotifierFacility(final Bundle bundle) {
    Optional<Organization> notifierFacilityOptional =
        notifierFhirQueries.getFhirNotifierFacility(bundle);
    if (notifierFacilityOptional.isEmpty()) {
      return null;
    }
    return createFacilityFromOrganization(notifierFacilityOptional.get());
  }

  public Facility createSubmitterFacility(final Bundle bundle) {
    Optional<Organization> submittingFacilityOptional =
        submitterFhirQueries.getSubmittingFacility(bundle);
    if (submittingFacilityOptional.isEmpty()) {
      return null;
    }
    return createFacilityFromOrganization(submittingFacilityOptional.get());
  }

  /** Try to retrieve the department name from an organization */
  public static Optional<String> selectDepartment(
      Organization.OrganizationContactComponent contact) {
    Optional<String> result;
    try {
      String departmentName = contact.getAddress().getLine().getFirst().getValue();
      result = Optional.ofNullable(departmentName);
    } catch (NoSuchElementException e) {
      result = Optional.empty();
    }
    return result;
  }

  private Facility createFacilityFromOrganization(final Organization organization) {
    String name = organization.hasName() ? organization.getName() : "";
    String type =
        displayTranslationService.resolveCodeableConceptValues(organization.getTypeFirstRep());
    Identifier identifier = identifierFactory.create(organization.getIdentifier());
    AddressDTO addressDTO = addressFactory.create(organization.getAddressFirstRep());
    List<Telecom> telecoms = telecomFactory.createTelecoms(organization.getTelecom());
    NameDTO nameDTO = contactFactory.createContact(organization);
    String department =
        FacilityFactory.selectDepartment(organization.getContactFirstRep()).orElse("");

    Facility.FacilityBuilder builder =
        Facility.builder()
            .name(name)
            .department(department)
            .identifier(identifier)
            .addressDTO(addressDTO)
            .type(type)
            .telecoms(telecoms)
            .nameDTO(nameDTO);

    return builder.build();
  }
}
