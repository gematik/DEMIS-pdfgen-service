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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static org.springframework.util.CollectionUtils.isEmpty;

import javax.annotation.Nullable;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Service;

@Service
public class ContactFactory {
  @Nullable
  public NameDTO create(HumanName humanName) {
    if (humanName == null) {
      return null;
    }

    return NameDTO.builder()
        .prefix(humanName.getPrefixAsSingleString())
        .givenName(humanName.getGivenAsSingleString())
        .familyName(humanName.getFamily())
        .build();
  }

  @Nullable
  public NameDTO createContact(Organization organization) {
    if (isEmpty(organization.getContact()) || organization.getContact().get(0) == null) {
      return null;
    }
    return create(organization.getContact().get(0).getName());
  }
}
