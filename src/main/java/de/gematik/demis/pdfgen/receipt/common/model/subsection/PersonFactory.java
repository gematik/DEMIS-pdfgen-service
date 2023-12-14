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

import de.gematik.demis.pdfgen.fhir.extract.NotifierFhirQueries;
import de.gematik.demis.pdfgen.fhir.extract.SubmitterFhirQueries;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonFactory {
  private final AddressFactory addressFactory;
  private final TelecomFactory telecomFactory;
  private final ContactFactory contactFactory;
  private final NotifierFhirQueries notifierFhirQueries;
  private final SubmitterFhirQueries submitterFhirQueries;

  @Nullable
  public Person createNotifierPerson(final Bundle bundle) {
    Optional<Practitioner> notifierOptional = notifierFhirQueries.getFhirNotifierPerson(bundle);
    return notifierOptional.map(this::createPersonFromPractitioner).orElse(null);
  }

  @Nullable
  public Person createSubmitterPerson(final Bundle bundle) {
    Optional<Practitioner> submittingPersonOptional =
        submitterFhirQueries.getSubmittingPerson(bundle);
    return submittingPersonOptional.map(this::createPersonFromPractitioner).orElse(null);
  }

  private Person createPersonFromPractitioner(Practitioner practitioner) {
    NameDTO nameDTO = contactFactory.create(practitioner.getNameFirstRep());
    AddressDTO addressDTO = addressFactory.create(practitioner.getAddressFirstRep());
    List<Telecom> telecoms = telecomFactory.createTelecoms(practitioner.getTelecom());

    return Person.builder().nameDTO(nameDTO).addressDTO(addressDTO).telecoms(telecoms).build();
  }
}
