package de.gematik.demis.pdfgen.receipt.diseasenotification.model;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createDiseaseNotificationBundle;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import java.util.List;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Composition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiseaseNotificationTemplateDtoFactoryIntegrationTest {

  @Autowired private DiseaseNotificationTemplateDtoFactory dtoFactory;

  private static void verifyRequiredParameters(DiseaseNotificationTemplateDto dto) {
    assertThat(dto).isNotNull();
    assertThat(dto.getMetadata()).isNotNull();
    assertThat(dto.getMetadata().getIdentifier()).isEqualTo("2d66a331-102a-4047-b666-1b2f18ee955e");
    assertThat(dto.getNotification()).isNotNull();
    assertThat(dto.getNotification().getIdentifier())
        .isEqualTo("7f562b87-f2c2-4e9d-b3fc-37f6b5dca3a5");
    assertThat(dto.getRecipient()).isNotNull();
    assertThat(dto.getRecipient().getOrganizationDTO()).isNotNull();
    assertThat(dto.getRecipient().getOrganizationDTO().getName())
        .isEqualTo("Kreis Herzogtum Lauenburg | Gesundheitsamt");
    assertThat(dto.getNotifier()).isNotNull();
    assertThat(dto.getNotifier().getFacility()).isNotNull();
    assertThat(dto.getNotifier().getFacility().getName()).isEqualTo("SlowHealing Klinik");
    assertThat(dto.getNotifiedPersonDTO()).isNotNull();
    assertThat(dto.getNotifiedPersonDTO().getGender()).isEqualTo(GenderEnum.FEMALE);
    assertThat(dto.getConditionDTO()).isNotNull();
    assertThat(dto.getCommonQuestionnaire()).isNotNull();
  }

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(this.dtoFactory.create(null)).isNull();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    // when
    DiseaseNotificationTemplateDto dto = this.dtoFactory.create(createDiseaseNotificationBundle());

    // then
    verifyRequiredParameters(dto);
    assertThat(dto.getSpecificQuestionnaire()).isNotNull();
  }

  @Test
  void create_shouldHandleMissingSpecificQuestionnaire() {
    // given
    Bundle input = createDiseaseNotificationBundle();
    List<Bundle.BundleEntryComponent> entries = input.getEntry();
    ((Composition) entries.getFirst().getResource()).getSection().remove(2);
    entries.remove(11);

    // when
    DiseaseNotificationTemplateDto dto = this.dtoFactory.create(input);

    // then
    verifyRequiredParameters(dto);
    assertThat(dto.getSpecificQuestionnaire()).isNull();
  }
}
