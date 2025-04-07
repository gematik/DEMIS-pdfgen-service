package de.gematik.demis.pdfgen.fhir.extract;

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

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifiedFhirQueries {
  private final NotificationFhirQueries notificationFhirQueries;

  public Optional<Patient> getNotifiedPerson(final Bundle bundle) {
    Optional<Composition> fhirNotificationOptional =
        notificationFhirQueries.getNotification(bundle);
    if (fhirNotificationOptional.isEmpty() || !fhirNotificationOptional.get().hasSubject()) {
      return Optional.empty();
    }
    IBaseResource notifiedPerson = fhirNotificationOptional.get().getSubject().getResource();
    return Optional.ofNullable(notifiedPerson instanceof Patient patient ? patient : null);
  }
}
