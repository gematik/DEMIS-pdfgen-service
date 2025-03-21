package de.gematik.demis.pdfgen.receipt.diseasenotification;

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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.pdfgen.pdf.PdfData;
import de.gematik.demis.pdfgen.pdf.PdfGenerator;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.NameDTO;
import de.gematik.demis.pdfgen.receipt.common.service.html.HtmlTemplateParser;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDto;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDtoFactory;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DiseaseNotificationServiceTest {

  private DiseaseNotificationService diseaseNotificationService;

  @Mock private FhirParser fhirParser;
  @Mock private PdfGenerator pdfGenerator;
  @Mock private HtmlTemplateParser templateParser;
  @Mock private DiseaseNotificationTemplateDtoFactory diseaseNotificationTemplateDtoFactory;

  private @Value("${pdfgen.template.disease-notification}") String diseaseNotificationTemplate;

  @BeforeEach
  void setUp() {
    diseaseNotificationService =
        new DiseaseNotificationService(
            fhirParser, pdfGenerator, templateParser, diseaseNotificationTemplateDtoFactory);
  }

  @Test
  void generatePdfFromBundleJsonString_shouldCreateBinaryPdfFromJsonFhirBundle() throws Exception {
    Bundle b = new Bundle();
    NotifiedPersonDTO notifiedPersonDTO =
        NotifiedPersonDTO.builder().nameDTO(NameDTO.builder().familyName("family").build()).build();
    DiseaseNotificationTemplateDto dto =
        DiseaseNotificationTemplateDto.builder().notifiedPersonDTO(notifiedPersonDTO).build();

    when(fhirParser.parseFromJson(DISEASE_NOTIFICATION_BUNDLE_JSON)).thenReturn(b);
    when(diseaseNotificationTemplateDtoFactory.create(b)).thenReturn(dto);
    when(templateParser.process(dto, diseaseNotificationTemplate)).thenReturn("resultString");
    byte[] bytes = "resultString".getBytes();
    when(pdfGenerator.generatePdfFromHtml("resultString")).thenReturn(bytes);

    PdfData pdfData =
        diseaseNotificationService.generatePdfFromBundleJsonString(
            DISEASE_NOTIFICATION_BUNDLE_JSON);

    assertThat(pdfData.bytes()).isEqualTo(bytes);
  }

  @Test
  void generatePdfFromBundleXmlString_shouldCreateBinaryPdfFromXmlFhirBundle() throws Exception {
    Bundle b = new Bundle();
    NotifiedPersonDTO notifiedPersonDTO =
        NotifiedPersonDTO.builder().nameDTO(NameDTO.builder().familyName("family").build()).build();
    DiseaseNotificationTemplateDto dto =
        DiseaseNotificationTemplateDto.builder().notifiedPersonDTO(notifiedPersonDTO).build();

    when(fhirParser.parseFromXml(DISEASE_NOTIFICATION_BUNDLE_XML)).thenReturn(b);
    when(diseaseNotificationTemplateDtoFactory.create(b)).thenReturn(dto);
    when(templateParser.process(dto, diseaseNotificationTemplate)).thenReturn("resultString");
    byte[] bytes = "resultString".getBytes();
    when(pdfGenerator.generatePdfFromHtml("resultString")).thenReturn(bytes);

    PdfData pdfData =
        diseaseNotificationService.generatePdfFromBundleXmlString(DISEASE_NOTIFICATION_BUNDLE_XML);
    assertThat(pdfData.bytes()).isEqualTo(bytes);
  }
}
