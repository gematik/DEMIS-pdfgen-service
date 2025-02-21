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

import static org.assertj.core.api.Assertions.assertThat;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Appointment;
import org.hl7.fhir.r4.model.Extension;
import org.junit.jupiter.api.Test;

class ExtensionQueriesTest {

  @Test
  void resolve_shouldHandleNullGracefully() {
    // given
    Extension extension = new Extension();

    // when
    Object shouldBeNull1 = ExtensionQueries.resolve(null, Object.class);
    Object shouldBeNull2 = ExtensionQueries.resolve(extension, null);

    // then
    assertThat(shouldBeNull1).isNull();
    assertThat(shouldBeNull2).isNull();
  }

  @Test
  void resolve_shouldReturnExpectedObjectWHenClassMatches() {
    // given
    Extension extension = new Extension();
    Address address = new Address();
    extension.setValue(address);

    // when
    Address shoudBeAddress = ExtensionQueries.resolve(extension, Address.class);
    Appointment shouldBeNull = ExtensionQueries.resolve(extension, Appointment.class);

    // then
    assertThat(shoudBeAddress).isEqualTo(address);
    assertThat(shouldBeNull).isNull();
  }
}
