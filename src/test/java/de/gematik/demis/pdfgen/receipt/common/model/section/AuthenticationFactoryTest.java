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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createProvenanceBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.AuthenticationLevelOfAssuranceEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.AuthenticationMethodEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.AuthenticationRoutingEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthenticationFactoryTest {

  @Autowired private AuthenticationFactory authenticationFactory;

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(this.authenticationFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // given
    Authentication authentication = this.authenticationFactory.create(createProvenanceBundle());

    // then
    verify(authentication);
  }

  private static void verify(Authentication authentication) {
    assertThat(authentication).isNotNull();
    assertThat(authentication.getMethod()).isNotNull();
    assertThat(authentication.getRouting()).isNotNull();
    assertThat(authentication.getLevelOfAssurance()).isNotNull();
    assertThat(authentication.getRouting().toString())
        .hasToString(AuthenticationRoutingEnum.INTERFACE.toString());
    assertThat(authentication.getMethod().toString())
        .hasToString(AuthenticationMethodEnum.CERTIFICATE.toString());
    assertThat(authentication.getLevelOfAssurance().toString())
        .hasToString(AuthenticationLevelOfAssuranceEnum.SUBSTANTIAL.toString());
  }
}
