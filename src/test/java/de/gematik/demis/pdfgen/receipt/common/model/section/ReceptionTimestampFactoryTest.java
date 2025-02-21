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

import de.gematik.demis.pdfgen.test.helper.FhirFactory;
import org.assertj.core.api.Assertions;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Test;

class ReceptionTimestampFactoryTest {

  /**
   * This is the default case. Every received bundle is expected to contain a notification
   * identifier. The other cases are error cases.
   */
  @Test
  void toString_shouldHandleNotificationIdentifier() {
    ReceptionTimestampFactory factory =
        new ReceptionTimestampFactory(FhirFactory.createLaboratoryReportBundle());
    Assertions.assertThat(factory)
        .hasToString("Notification: a5e00874-bb26-45ac-8eea-0bde76456703");
  }

  /**
   * To be clear: a null bundle is not a use case. This test was written to satisfy Sonar regarding
   * the defensive <code>toString()</code>. We will not write any more code on a null bundle,
   * especially not in the production code.
   */
  @Test
  void toString_shouldHandleNullBundle() {
    ReceptionTimestampFactory factory = new ReceptionTimestampFactory(null);
    Assertions.assertThat(factory).hasToString("Bundle: null");
  }

  @Test
  void toString_shouldHandleNullNotificationIdentifier() {
    Bundle bundle = createNullNotificationIdentifierBundle();
    ReceptionTimestampFactory factory = new ReceptionTimestampFactory(bundle);
    Assertions.assertThat(factory).hasToString("Bundle without notification bundle ID!");
  }

  private static Bundle createNullNotificationIdentifierBundle() {
    String json = FhirFactory.LABORATORY_REPORT_BUNDLE_DV2_JSON;
    int from = json.indexOf("\"identifier\": {");
    int to = json.indexOf("},", from) + 2;
    json = json.substring(0, from) + json.substring(to);
    return FhirFactory.createBundle(json);
  }
}
