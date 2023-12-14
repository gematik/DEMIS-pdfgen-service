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

import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.hl7.fhir.r4.model.Extension;
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
  private final TranslationService displayTranslationService;

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
    if (fhirOrganizationOptional.isEmpty()) {
      return null;
    }
    org.hl7.fhir.r4.model.Organization fhirOrganization = fhirOrganizationOptional.get();
    String name = fhirOrganization.getName();
    String type =
        displayTranslationService.resolveCodeableConceptValues(fhirOrganization.getTypeFirstRep());
    AddressDTO addressDTO = addressFactory.create(fhirOrganization.getAddressFirstRep());
    List<Telecom> telecoms = telecomFactory.createTelecoms(fhirOrganization.getTelecom());
    NameDTO nameDTO = contactFactory.createContact(fhirOrganization);

    return OrganizationDTO.builder()
        .name(name)
        .type(type)
        .addressDTO(addressDTO)
        .telecoms(telecoms)
        .nameDTO(nameDTO)
        .build();
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
