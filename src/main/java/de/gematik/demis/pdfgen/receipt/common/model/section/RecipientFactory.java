package de.gematik.demis.pdfgen.receipt.common.model.section;

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

import static de.gematik.demis.pdfgen.lib.profile.DemisSystems.RESPONSIBLE_HEALTH_DEPARTMENT_CODING_SYSTEM;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSiteService;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipientFactory {
  private final OrganizationFactory organizationFactory;
  private final TransmittingSiteService transmittingSiteService;

  @Nullable
  public Recipient create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    OrganizationDTO organizationDTO = getHealthDepartmentOrganization(bundle);
    if (organizationDTO == null) {
      return null;
    }
    return Recipient.builder().organizationDTO(organizationDTO).build();
  }

  @Nullable
  private OrganizationDTO getHealthDepartmentOrganization(final Bundle bundle) {
    if (!bundle.hasMeta() || !bundle.getMeta().hasTag()) {
      return null;
    }
    Optional<String> healthDepartmentCodeOpt =
        bundle.getMeta().getTag().stream()
            .filter(this::hasHealthDepartmentCode)
            .map(Coding::getCode)
            .findFirst();
    if (healthDepartmentCodeOpt.isEmpty()) {
      return null;
    }
    TransmittingSite transmittingSite =
        transmittingSiteService.getTransmittingSite(healthDepartmentCodeOpt.get());
    return organizationFactory.create(transmittingSite);
  }

  private boolean hasHealthDepartmentCode(Coding coding) {
    return coding.hasSystem()
        && RESPONSIBLE_HEALTH_DEPARTMENT_CODING_SYSTEM.equals(coding.getSystem());
  }
}
