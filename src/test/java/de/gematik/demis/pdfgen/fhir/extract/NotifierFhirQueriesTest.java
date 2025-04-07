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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createEmptyBundle;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Practitioner;
import org.junit.jupiter.api.Test;

class NotifierFhirQueriesTest {

  private final Bundle laboratoryReportBundle = createLaboratoryReportBundle();
  private final Bundle emptyBundle = createEmptyBundle();
  private final NotifierFhirQueries notifierFhirQueries =
      new NotifierFhirQueries(new FhirQueries());

  @Test
  void getFhirNotifierPerson_shouldHandleNullGracefully() {
    assertThat(notifierFhirQueries.getFhirNotifierPerson(null)).isEmpty();
  }

  @Test
  void getFhirNotifierPerson_shouldNotLoadNotifierPersonWhenNoneExist() {
    // when
    Optional<Practitioner> optional = notifierFhirQueries.getFhirNotifierPerson(emptyBundle);
    // then
    assertThat(optional).isEmpty();
  }

  @Test
  void getNotifiedPerson_shouldLoadNotifierPerson() {
    // when
    Optional<Practitioner> optional =
        notifierFhirQueries.getFhirNotifierPerson(laboratoryReportBundle);
    // then
    assertThat(optional)
        .isPresent()
        .get()
        .extracting("Id")
        .isEqualTo("https://demis.rki.de/fhir/Practitioner/ec6e6fc0-4b0f-4fc6-b638-eaf85d1308ee");
  }

  @Test
  void getFhirNotifierFacility_shouldHandleNullGracefully() {
    assertThat(notifierFhirQueries.getFhirNotifierFacility(null)).isEmpty();
  }

  @Test
  void getFhirNotifierFacility_shouldNotLoadNotifierFacilityWhenNoneExist() {
    // when
    Optional<Organization> optional = notifierFhirQueries.getFhirNotifierFacility(emptyBundle);
    // then
    assertThat(optional).isEmpty();
  }

  @Test
  void getFhirNotifierFacility_shouldLoadNotifierFacility() {
    // when
    Optional<Organization> optional =
        notifierFhirQueries.getFhirNotifierFacility(laboratoryReportBundle);
    // then
    assertThat(optional)
        .isPresent()
        .get()
        .extracting("id")
        .isEqualTo("https://demis.rki.de/fhir/Organization/ec6e6fc0-4b0f-4fc6-b638-eaf85d1308ef");
  }
}
