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

package de.gematik.demis.pdfgen.receipt.bedoccupancy.model;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.TelecomFactory;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BedOccupancyOrganizationFactory {
  private final BedOccupancyAddressFactory addressFactory;
  private final TelecomFactory telecomFactory;

  @Nullable
  public BedOccupancyOrganization create(Optional<Organization> fhirOrganizationOptional) {
    if (fhirOrganizationOptional.isEmpty()) {
      return null;
    }
    Organization fhirOrganization = fhirOrganizationOptional.get();

    String id = getId(fhirOrganization);
    String name = fhirOrganization.getName();
    BedOccupancyAddress address = addressFactory.create(fhirOrganization.getAddressFirstRep());
    List<Telecom> telecoms = telecomFactory.createTelecoms(fhirOrganization.getTelecom());

    return BedOccupancyOrganization.builder()
        .id(id)
        .name(name)
        .address(address)
        .telecoms(telecoms)
        .build();
  }

  private String getId(Organization fhirOrganization) {
    if (fhirOrganization.getIdElement() == null) {
      return "";
    }
    return fhirOrganization.getIdElement().getIdPart();
  }
}
