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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createBedOccupancyBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import java.util.Optional;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.hl7.fhir.r4.model.Organization;
import org.junit.jupiter.api.Test;

class FhirQueriesTest {

  private final Bundle bedOccupancyBundle = createBedOccupancyBundle();
  private final FhirQueries fhirQueries = new FhirQueries();

  @Test
  void findResourceWithProfile_shouldHandleNullGracefully() {
    assertThat(fhirQueries.findResourceWithProfile(null, Bundle.class, DemisProfiles.profile("")))
        .isEmpty();
  }

  @Test
  void findResourceWithProfile_shouldFindCompositionWhenPresent() {
    Optional<Composition> compositionOptional =
        fhirQueries.findResourceWithProfile(
            bedOccupancyBundle,
            Composition.class,
            DemisProfiles.profile(
                "https://demis.rki.de/fhir/StructureDefinition/ReportBedOccupancy"));

    assertThat(compositionOptional)
        .isPresent()
        .get()
        .extracting("Identifier")
        .extracting("Value")
        .isEqualTo("5e1e89ce-7a44-4ec1-801c-0f988992e8fe");
  }

  @Test
  void findResourceWithProfile_shouldNotFindCompositionWhenProfileNotPresent() {
    Optional<Composition> compositionOptional =
        fhirQueries.findResourceWithProfile(
            bedOccupancyBundle, Composition.class, DemisProfiles.profile("FakeProfile"));
    assertThat(compositionOptional).isEmpty();
  }

  @Test
  void findResourceWithProfile_shouldNotFindObjectWhenTypeDoesntMatch() {
    Optional<Organization> notReallyAnOrganization =
        fhirQueries.findResourceWithProfile(
            bedOccupancyBundle,
            Organization.class,
            DemisProfiles.profile(
                "https://demis.rki.de/fhir/StructureDefinition/ReportBedOccupancy"));

    assertThat(notReallyAnOrganization).isEmpty();
  }
}
