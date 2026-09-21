package de.gematik.demis.pdfgen.receipt.diseasenotification;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_HIV_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_TOXD_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_WITHOUT_LAB_SPECIMEN_LAB_DETAILS_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_WITHOUT_LAB_SPECIMEN_TAKEN_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_WITH_CONTACT_NAME_TEXT_JSON;
import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_XML;
import static de.gematik.demis.pdfgen.test.helper.PdfExtractorHelper.extractPdfText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.pdfgen.pdf.PdfData;
import de.gematik.demis.pdfgen.pdf.PdfGenerator;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.NameDTO;
import de.gematik.demis.pdfgen.receipt.common.service.html.HtmlTemplateParser;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDto;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDtoFactory;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

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
    when(diseaseNotificationTemplateDtoFactory.create(any(Bundle.class))).thenReturn(dto);
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
    when(diseaseNotificationTemplateDtoFactory.create(any(Bundle.class))).thenReturn(dto);
    when(templateParser.process(dto, diseaseNotificationTemplate)).thenReturn("resultString");
    byte[] bytes = "resultString".getBytes();
    when(pdfGenerator.generatePdfFromHtml("resultString")).thenReturn(bytes);

    PdfData pdfData =
        diseaseNotificationService.generatePdfFromBundleXmlString(DISEASE_NOTIFICATION_BUNDLE_XML);
    assertThat(pdfData.bytes()).isEqualTo(bytes);
  }

  @Nested
  @SpringBootTest(properties = {"feature.flag.without-date-fields-7-3=false"})
  class WithoutDateFields73Disabled {
    @Autowired private DiseaseNotificationService diseaseNotificationService;

    @Test
    void generatePdfFromBundleJsonString_doesContainDateEntry() throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_JSON));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung 02.01.2022 \nErkrankungsbeginn 01.01.2022");
    }

    @Test
    void generatePdfFromBundleJsonString_withoutDateFields_doesContainDefaultDateEntry()
        throws Exception {
      String bundleWithoutDateFields =
          DISEASE_NOTIFICATION_BUNDLE_JSON
              .replace("\"onsetDateTime\": \"2022-01-01\",", "")
              .replace("\"recordedDate\": \"2022-01-02\",", "");

      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleWithoutDateFields));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung Keine Angabe \nErkrankungsbeginn Keine Angabe");
    }

    @Test
    void generatePdfFromBundleJsonString_NonNominal_doesContainDateEntry() throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_HIV_JSON));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung 02.01.2022 \nErkrankungsbeginn 01.01.2022");
    }

    @Test
    void generatePdfFromBundleJsonString_NonNominal_withoutDateFields_doesContainDefaultDateEntry()
        throws Exception {
      String bundleWithoutDateFields =
          DISEASE_NOTIFICATION_BUNDLE_HIV_JSON
              .replace("\"onsetDateTime\": \"2022-01-01\",", "")
              .replace("\"recordedDate\": \"2022-01-02\",", "");

      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleWithoutDateFields));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung Keine Angabe \nErkrankungsbeginn Keine Angabe");
    }

    @Test
    void generatePdfFromBundleJsonString_Toxd_doesContainDateEntry() throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_TOXD_JSON));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung 19.08.2026 \nErkrankungsbeginn 18.08.2026");
    }

    @Test
    void generatePdfFromBundleJsonString_Toxd_withoutDateFields_doesContainDefaultDateEntry()
        throws Exception {
      String bundleWithoutDateFields =
          DISEASE_NOTIFICATION_BUNDLE_TOXD_JSON
              .replace("\"onsetDateTime\": \"2026-08-18\",", "")
              .replace("\"recordedDate\": \"2026-08-19\",", "");

      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleWithoutDateFields));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung Keine Angabe \nErkrankungsbeginn Keine Angabe");
    }
  }

  @Nested
  @SpringBootTest(properties = {"feature.flag.without-date-fields-7-3=true"})
  class WithoutDateFields73Enabled {
    @Autowired private DiseaseNotificationService diseaseNotificationService;

    @Test
    void generatePdfFromBundleJsonString_doesContainDateEntry() throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_JSON));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung 02.01.2022 \nErkrankungsbeginn 01.01.2022");
    }

    @Test
    void generatePdfFromBundleJsonString_withoutDateFields_doesContainDefaultDateEntry()
        throws Exception {
      String bundleWithoutDateFields =
          DISEASE_NOTIFICATION_BUNDLE_JSON
              .replace("\"onsetDateTime\": \"2022-01-01\",", "")
              .replace("\"recordedDate\": \"2022-01-02\",", "");

      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleWithoutDateFields));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung Keine Angabe \nErkrankungsbeginn Keine Angabe");
    }

    @Test
    void generatePdfFromBundleJsonString_NonNominal_doesNotContainDateEntry() throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_HIV_JSON));
      assertThat(pdfText)
          .doesNotContain("Datum der Diagnosestellung 02.01.2022 \nErkrankungsbeginn 01.01.2022")
          .doesNotContain(
              "Datum der Diagnosestellung Keine Angabe \nErkrankungsbeginn Keine Angabe");
    }

    @Test
    void generatePdfFromBundleJsonString_Toxd_containsDateEntry() throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_TOXD_JSON));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung 19.08.2026 \nErkrankungsbeginn 18.08.2026");
    }

    @Test
    void generatePdfFromBundleJsonString_Toxd_containsDefaultDateEntry() throws Exception {
      String bundleWithoutDateFields =
          DISEASE_NOTIFICATION_BUNDLE_TOXD_JSON
              .replace("\"onsetDateTime\": \"2026-08-18\",", "")
              .replace("\"recordedDate\": \"2026-08-19\",", "");
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleWithoutDateFields));
      assertThat(pdfText)
          .contains("Datum der Diagnosestellung Keine Angabe \nErkrankungsbeginn Keine Angabe");
    }

    @Test
    void generatePdfFromBundleJsonString_NonNominal_withoutDateFields_doesNotContainDateEntry()
        throws Exception {
      String bundleWithoutDateFields =
          DISEASE_NOTIFICATION_BUNDLE_HIV_JSON
              .replace("\"onsetDateTime\": \"2022-01-01\",", "")
              .replace("\"recordedDate\": \"2022-01-02\",", "");

      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleWithoutDateFields));
      assertThat(pdfText)
          .doesNotContain(
              "Datum der Diagnosestellung Keine Angabe \nErkrankungsbeginn Keine Angabe");
    }
  }

  @Nested
  @SpringBootTest
  class PdfContentTest {
    @Autowired private DiseaseNotificationService diseaseNotificationService;

    @Test
    void generatePdfFromBundleJsonString_shouldHaveContactPersonEntry() throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_JSON));
      assertThat(pdfText).contains("Kontaktperson Dr. Anna Beate Carolin Ansprechpartner");
    }

    @Test
    void generatePdfFromBundleJsonString_shouldHaveContactPersonEntry_fromContactNameText()
        throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_WITH_CONTACT_NAME_TEXT_JSON));
      assertThat(pdfText).contains("Kontaktperson Frau Dr. Anna Beate Carolin Ansprechpartner");
    }

    @Test
    void generatePdfFromBundleJsonString_withoutSymptoms_doesContainDefaultSymptomsEntry()
        throws Exception {
      final String bundleWithoutEvidence =
          DISEASE_NOTIFICATION_BUNDLE_JSON.replaceAll("(?s)\"evidence\"\\s*:\\s*\\[.*?]\\s*,", "");
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleWithoutEvidence));
      assertThat(pdfText).contains("Symptome Keine Angabe");
    }

    @Test
    void
        generatePdfFromBundleJsonString_NonNominal_NotHIV_withoutSymptoms_doesContainDefaultSymptomsEntry()
            throws Exception {
      final String bundleNotHivd = DISEASE_NOTIFICATION_BUNDLE_HIV_JSON.replace("hivd", "chtd");
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(bundleNotHivd));
      assertThat(pdfText).contains("Symptome Keine Angabe");
    }

    @Test
    void
        generatePdfFromBundleJsonString_NonNominal_HIV_withoutSymptoms_doesNotContainDefaultSymptomsEntry()
            throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_HIV_JSON));
      assertThat(pdfText).doesNotContain("Symptome Keine Angabe");
    }

    @Test
    void generatePdfFromBundleJsonString_doesContainLabQuestionEntry_withAnswerYes()
        throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_JSON));
      assertThat(pdfText)
          .containsPattern(
              "Diagnosehinweise Textueller Hinweis ?\\n"
                  + "labSpecimenTaken Ja ?\\n"
                  + "labSpecimenLab ?\\n"
                  + "Name QuickScan Labor \\(Erregerdiagnostische Untersuchungsstelle\\) ?\\n"
                  + "Adresse Laborstraße 345, 21481 Buchhorst, .* 20422 ?\\n"
                  + "Kontakt Telefon: 666555444 E-Mail: mail@labor\\.de");
    }

    @Test
    void generatePdfFromBundleJsonString_doesContainLabQuestionEntry_withAnswerNo()
        throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_WITHOUT_LAB_SPECIMEN_TAKEN_JSON));
      assertThat(pdfText)
          .containsPattern("Diagnosehinweise Textueller Hinweis\\n" + "labSpecimenTaken Nein")
          .doesNotContain("labSpecimenLab");
    }

    @Test
    void
        generatePdfFromBundleJsonString_doesContainLabQuestionEntry_missingLabDetails_hasDefaultText()
            throws Exception {
      final String pdfText =
          generateAndValidateDiseaseNotificationPdf(
              diseaseNotificationService.generatePdfFromBundleJsonString(
                  DISEASE_NOTIFICATION_BUNDLE_WITHOUT_LAB_SPECIMEN_LAB_DETAILS_JSON));
      assertThat(pdfText)
          .containsPattern(
              "Diagnosehinweise Textueller Hinweis ?\\n"
                  + "labSpecimenTaken Ja ?\\n"
                  + "labSpecimenLab ?\\n"
                  + "Name QuickScan Labor \\(Erregerdiagnostische Untersuchungsstelle\\) ?\\n"
                  + "Adresse Keine Angabe ?\\n"
                  + "Kontakt Keine Angabe ?\\n");
    }
  }

  private @NotNull String generateAndValidateDiseaseNotificationPdf(PdfData pdfData)
      throws IOException {
    assertThat(pdfData.bytes()).isNotNull();
    String pdfText = extractPdfText(pdfData);
    validateDiseaseNotificationPdfText(pdfText);
    return pdfText;
  }

  private void validateDiseaseNotificationPdfText(String pdfText) {
    assertThat(pdfText)
        .containsAnyOf(
            "Empfangsbestätigung",
            "Vielen Dank für Ihre Meldung. Die Daten wurden an das zuständige Gesundheitsamt gemeldet",
            "Meldevorgangs-ID a5e00874-bb26-45ac-8eea-0bde76456703",
            "Meldungsidentifier e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9");
  }
}
