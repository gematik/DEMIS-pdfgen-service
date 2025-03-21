package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.submitter;

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

import de.gematik.demis.pdfgen.receipt.common.model.subsection.FacilityFactory;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.PersonFactory;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmitterFactory {
  private final PersonFactory personFactory;
  private final FacilityFactory facilityFactory;

  @Nullable
  public Submitter create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    return Submitter.builder()
        .person(personFactory.createSubmitterPerson(bundle))
        .facility(facilityFactory.createSubmitterFacility(bundle))
        .build();
  }
}
