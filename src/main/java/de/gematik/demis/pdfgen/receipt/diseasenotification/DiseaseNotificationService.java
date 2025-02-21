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

import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.pdfgen.pdf.PdfData;
import de.gematik.demis.pdfgen.pdf.PdfGenerationException;
import de.gematik.demis.pdfgen.pdf.PdfGenerator;
import de.gematik.demis.pdfgen.receipt.common.service.html.HtmlTemplateParser;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDto;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDtoFactory;
import de.gematik.demis.pdfgen.utils.ResourceTextFile;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiseaseNotificationService {

  private static final String INIT_FILE = "fhir/DiseaseNotificationBundle.json";

  private final FhirParser fhirParser;
  private final PdfGenerator pdfGenerator;
  private final HtmlTemplateParser templateParser;
  private final DiseaseNotificationTemplateDtoFactory diseaseNotificationTemplateDtoFactory;

  private @Value("${pdfgen.template.disease-notification}") String diseaseNotificationTemplate;

  @Observed(
      name = "disease-json",
      contextualName = "disease-json",
      lowCardinalityKeyValues = {"disease", "json"})
  public PdfData generatePdfFromBundleJsonString(String bundleAsJsonString)
      throws PdfGenerationException {
    Bundle bundle = (Bundle) fhirParser.parseFromJson(bundleAsJsonString);
    return generatePdfFromBundle(bundle);
  }

  @Observed(
      name = "disease-xml",
      contextualName = "disease-xml",
      lowCardinalityKeyValues = {"disease", "xml"})
  public PdfData generatePdfFromBundleXmlString(String bundleAsXmlString)
      throws PdfGenerationException {
    Bundle bundle = (Bundle) fhirParser.parseFromXml(bundleAsXmlString);
    return generatePdfFromBundle(bundle);
  }

  /**
   * Run a complete PDF rendering to initialize FHIR, Thymeleaf and Flying Sourcer.
   *
   * @throws PdfGenerationException initialization failed
   */
  @PostConstruct
  void generateInitialPdf() throws PdfGenerationException {
    long startMillis = System.currentTimeMillis();
    log.debug("Initializing disease notifications service");
    PdfData pdf = generatePdfFromBundleJsonString(new ResourceTextFile(INIT_FILE).get());
    log.debug(
        "Initialized disease notifications service! Duration: {} ms PdfBytes: {}",
        (System.currentTimeMillis() - startMillis),
        pdf.bytes().length);
  }

  private PdfData generatePdfFromBundle(final Bundle bundle) throws PdfGenerationException {
    DiseaseNotificationTemplateDto data = diseaseNotificationTemplateDtoFactory.create(bundle);
    String html = createHtml(data);
    return new PdfData(this.pdfGenerator.generatePdfFromHtml(html));
  }

  private String createHtml(DiseaseNotificationTemplateDto dto) {
    return this.templateParser.process(dto, this.diseaseNotificationTemplate);
  }
}
