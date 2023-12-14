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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.covid19questions;

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.createCovidQuestionnaireResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.utils.QuestionnaireResponseHelper;
import de.gematik.demis.pdfgen.translation.TranslationImmunizationProvider;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImmunizationFactoryTest {

  @InjectMocks private ImmunizationFactory immunizationFactory;

  private QuestionnaireResponseHelper questionnaireResponseHelper;
  @Mock private TranslationService displayTranslationService;
  @Mock private TranslationImmunizationProvider translationImmunizationProvider;

  private String diseaseCode = "cvdd";

  @BeforeEach
  void setup() {
    questionnaireResponseHelper = new QuestionnaireResponseHelper(displayTranslationService);
    immunizationFactory =
        new ImmunizationFactory(questionnaireResponseHelper, translationImmunizationProvider);
  }

  @Test
  void create_shouldHandleNullGracefully() {
    assertThat(immunizationFactory.create(null, "vaccinecvdd")).isEmpty();
    assertThat(immunizationFactory.create(null, "vaccinecvdd")).isEmpty();
  }

  @Test
  void create_shouldTestFactoryCreation() {
    when(translationImmunizationProvider.translateCode(any())).thenReturn("Comirnaty");

    List<Immunization> immunizations =
        immunizationFactory.create(createCovidQuestionnaireResponse(), "cvdd");

    assertThat(immunizations).isNotNull().hasSize(4);
    Immunization immunization = immunizations.get(0);
    assertThat(immunization.getVaccineCodeText()).isEqualTo("Comirnaty");
    assertThat(immunization.getOccurrenceDate()).hasToString("07.2021");
    assertThat(immunization.getNote()).isEqualTo("Zusatzinfo2");
  }
}
