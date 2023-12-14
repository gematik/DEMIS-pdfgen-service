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

package de.gematik.demis.pdfgen.fhir.extract;

import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmitterFhirQueries {
  private final FhirQueries fhirQueries;

  public Optional<Practitioner> getSubmittingPerson(Bundle bundle) {
    return fhirQueries.findResourceWithProfile(
        bundle, Practitioner.class, DemisProfiles.SUBMITTING_PERSON_PROFILE);
  }

  public Optional<Organization> getSubmittingFacility(Bundle bundle) {
    return fhirQueries.findResourceWithProfile(
        bundle, Organization.class, DemisProfiles.SUBMITTING_FACILITY_PROFILE);
  }
}
