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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createEmptyBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class NotifiedFhirQueriesTest {

  private final Bundle laboratoryReportBundle = createLaboratoryReportBundle();
  private final Bundle emptyBundle = createEmptyBundle();
  private final NotifiedFhirQueries notifiedFhirQueries =
      new NotifiedFhirQueries(new NotificationFhirQueries());

  @Test
  void getNotifiedPerson_shouldHandleNullGracefully() {
    assertThat(notifiedFhirQueries.getNotifiedPerson(null)).isEmpty();
  }

  @Test
  void getNotifiedPerson_shouldNotLoadNotificationWhenNoneExist() {
    // when
    Optional<Patient> optional = notifiedFhirQueries.getNotifiedPerson(emptyBundle);
    // then
    assertThat(optional).isEmpty();
  }

  @Test
  void getNotifiedPerson_shouldLoadNotification() {
    // when
    Optional<Patient> optional = notifiedFhirQueries.getNotifiedPerson(laboratoryReportBundle);
    // then
    assertThat(optional)
        .isPresent()
        .get()
        .extracting("Id")
        .isEqualTo("https://demis.rki.de/fhir/Patient/c9201e8c-0425-4fb4-89c3-95f2405f290a");
  }
}
