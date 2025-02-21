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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createLaboratoryReportBundle;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PersonFactoryIntegrationTest {

  @Autowired private PersonFactory personFactory;

  @Test
  void createNotifierPerson_shouldHandleNullGracefully() {
    assertThat(personFactory.createNotifierPerson(null)).isNull();
  }

  @Test
  void createNotifierPerson_shouldTestFactoryCreation() {
    // given
    Person person = personFactory.createNotifierPerson(createLaboratoryReportBundle());

    // then
    assertThat(person).isNotNull();
    assertThat(person.getNameDTO()).isNotNull();
    assertThat(person.getAddressDTO()).isNotNull();
    assertThat(person.getTelecoms()).isNotNull();
    assertThat(person.getTelecoms()).isNotEmpty();
  }

  @Test
  void createSubmitterPerson_shouldHandleNullGracefully() {
    assertThat(personFactory.createSubmitterPerson(null)).isNull();
  }

  @Test
  void createSubmitterPerson_shouldTestFactoryCreation() {
    // given
    Person person = personFactory.createSubmitterPerson(createLaboratoryReportBundle());

    // then
    assertThat(person).isNotNull();
    assertThat(person.getNameDTO()).isNotNull();
    assertThat(person.getAddressDTO()).isNotNull();
    assertThat(person.getTelecoms()).isNotNull();
    assertThat(person.getTelecoms()).isNotEmpty();
  }
}
