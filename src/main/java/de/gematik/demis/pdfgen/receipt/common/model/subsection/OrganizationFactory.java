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

import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationFactory {

  private final AddressFactory addressFactory;
  private final TelecomFactory telecomFactory;
  private final ContactFactory contactFactory;
  private final TranslationService translationService;

  @Nullable
  public OrganizationDTO create(final Extension extension) {
    if (extension == null) {
      return null;
    }
    Type value = extension.getValue();
    if (!(value instanceof Reference reference)) {
      return null;
    }
    if (reference.getResource() instanceof org.hl7.fhir.r4.model.Organization fhirOrganization) {
      return create(Optional.of(fhirOrganization));
    }
    return null;
  }

  public OrganizationDTO create(
      final Optional<org.hl7.fhir.r4.model.Organization> fhirOrganizationOptional) {
    return fhirOrganizationOptional.map(this::create).orElse(null);
  }

  public OrganizationDTO create(Organization fhirOrganization) {
    OrganizationDTO.OrganizationDTOBuilder organization = OrganizationDTO.builder();
    setId(fhirOrganization, organization);
    setName(fhirOrganization, organization);
    setContact(fhirOrganization, organization);
    setType(fhirOrganization, organization);
    setAddress(fhirOrganization, organization);
    setTelecoms(fhirOrganization, organization);
    setDepartment(fhirOrganization, organization);
    return organization.build();
  }

  private void setDepartment(
      Organization fhirOrganization, OrganizationDTO.OrganizationDTOBuilder organization) {
    final var address = this.addressFactory.create(fhirOrganization.getAddressFirstRep());
    if (Objects.nonNull(address)) {
      organization.department(address.getDepartment());
    }
  }

  private void setTelecoms(
      Organization fhirOrganization, OrganizationDTO.OrganizationDTOBuilder organization) {
    organization.telecoms(this.telecomFactory.createTelecoms(fhirOrganization.getTelecom()));
  }

  private void setAddress(
      Organization fhirOrganization, OrganizationDTO.OrganizationDTOBuilder organization) {
    organization.addressDTO(this.addressFactory.create(fhirOrganization.getAddressFirstRep()));
  }

  private void setType(
      Organization fhirOrganization, OrganizationDTO.OrganizationDTOBuilder organization) {
    organization.type(
        this.translationService.resolveCodeableConceptValues(fhirOrganization.getTypeFirstRep()));
  }

  private void setContact(
      Organization fhirOrganization, OrganizationDTO.OrganizationDTOBuilder organization) {
    organization.nameDTO(this.contactFactory.createContact(fhirOrganization));
  }

  private void setName(
      Organization fhirOrganization, OrganizationDTO.OrganizationDTOBuilder organization) {
    organization.name(fhirOrganization.getName());
  }

  private void setId(
      Organization fhirOrganization, OrganizationDTO.OrganizationDTOBuilder organization) {
    IdType idElement = fhirOrganization.getIdElement();
    if (idElement != null) {
      organization.id(idElement.getIdPart());
    }
  }

  @Nullable
  public OrganizationDTO create(TransmittingSite site) {
    if (site == null) {
      return null;
    }
    String nameToUse =
        Stream.of(site.getName(), site.getDepartment())
            .filter(Strings::isNotBlank)
            .collect(Collectors.joining(" | "));
    return OrganizationDTO.builder()
        .name(nameToUse)
        .addressDTO(addressFactory.create(site))
        .telecoms(telecomFactory.createTelecoms(site))
        .build();
  }
}
