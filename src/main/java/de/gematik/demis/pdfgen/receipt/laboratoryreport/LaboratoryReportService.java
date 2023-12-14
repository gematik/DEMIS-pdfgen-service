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

package de.gematik.demis.pdfgen.receipt.laboratoryreport;

import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.pdfgen.pdf.PdfData;
import de.gematik.demis.pdfgen.pdf.PdfGenerationException;
import de.gematik.demis.pdfgen.pdf.PdfGenerator;
import de.gematik.demis.pdfgen.receipt.common.service.html.HtmlTemplateParser;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.LaboratoryReportTemplateDto;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.LaboratoryReportTemplateDtoFactory;
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
public class LaboratoryReportService {

  private static final String INIT_FILE = "fhir/LaboratoryReportBundleDv2.json";

  private final FhirParser fhirParser;
  private final PdfGenerator pdfGenerator;
  private final HtmlTemplateParser templateParser;
  private final LaboratoryReportTemplateDtoFactory laboratoryReportTemplateDtoFactory;
  private @Value("${pdfgen.template.laboratory-report}") String laboratoryReportTemplate;
  private @Value("${pdfgen.lastpage.qrcode.enabled}") boolean qrCodeOnLastPage;

  @Observed(
      name = "laboratory-json",
      contextualName = "laboratory-json",
      lowCardinalityKeyValues = {"laboratory", "json"})
  public PdfData generatePdfFromBundleJsonString(String bundleAsJsonString)
      throws PdfGenerationException {
    Bundle bundle = (Bundle) fhirParser.parseFromJson(bundleAsJsonString);
    return generatePdfFromBundle(bundle);
  }

  @Observed(
      name = "laboratory-xml",
      contextualName = "laboratory-xml",
      lowCardinalityKeyValues = {"laboratory", "xml"})
  public PdfData generatePdfFromBundleXmlString(String bundleAsXmlString)
      throws PdfGenerationException {
    Bundle bundle = (Bundle) fhirParser.parseFromXml(bundleAsXmlString);
    return generatePdfFromBundle(bundle);
  }

  private PdfData generatePdfFromBundle(final Bundle bundle) throws PdfGenerationException {
    LaboratoryReportTemplateDto laboratoryReportTemplateDto =
        this.laboratoryReportTemplateDtoFactory.create(bundle, this.qrCodeOnLastPage);
    return generatePdfFromDto(laboratoryReportTemplateDto);
  }

  public PdfData generatePdfFromDto(final LaboratoryReportTemplateDto dto)
      throws PdfGenerationException {
    return new PdfData(this.pdfGenerator.generatePdfFromHtml(createHtml(dto)));
  }

  private String createHtml(LaboratoryReportTemplateDto dto) {
    return this.templateParser.process(dto, this.laboratoryReportTemplate);
  }

  /**
   * Run a complete PDF rendering to initialize FHIR, Thymeleaf and Flying Sourcer.
   *
   * @throws PdfGenerationException initialization failed
   */
  @PostConstruct
  void generateInitialPdf() throws PdfGenerationException {
    long startMillis = System.currentTimeMillis();
    log.debug("Initializing laboratory reports service");
    PdfData pdf = generatePdfFromBundleJsonString(new ResourceTextFile(INIT_FILE).get());
    log.debug(
        "Initialized laboratory reports service! Duration: {} ms PdfBytes: {}",
        (System.currentTimeMillis() - startMillis),
        String.valueOf(pdf.bytes().length));
  }
}
