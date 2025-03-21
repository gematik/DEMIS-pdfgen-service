package de.gematik.demis.pdfgen.receipt.bedoccupancy;

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
import de.gematik.demis.pdfgen.receipt.bedoccupancy.model.BedOccupancyFactory;
import de.gematik.demis.pdfgen.receipt.common.service.html.HtmlTemplateParser;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BedOccupancyService {

  private static final String INIT_FILE = "fhir/BedOccupancyBundle.json";

  private final BedOccupancyFactory bedOccupancyFactory;
  private final FhirParser fhirParser;
  private final PdfGenerator pdfGenerator;
  private final HtmlTemplateParser htmlTemplateParser;
  private @Value("${pdfgen.template.bed-occupancy}") String bedOccupancyTemplate;

  private static String readJsonBundle() {
    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(INIT_FILE)) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Failed to read bed occupancy reports initialization file: " + INIT_FILE, e);
    }
  }

  @Observed(
      name = "bed-occupancy-json",
      contextualName = "bed-occupancy-json",
      lowCardinalityKeyValues = {"bed-occupancy", "json"})
  public PdfData createBedOccupancyPdfFromJson(@Nullable String fhirResourceAsString)
      throws PdfGenerationException {
    Bundle bundle = (Bundle) fhirParser.parseFromJson(fhirResourceAsString);
    return generatePdfFromBundle(bundle);
  }

  @Observed(
      name = "bed-occupancy-xml",
      contextualName = "bed-occupancy-xml",
      lowCardinalityKeyValues = {"bed-occupancy", "xml"})
  public PdfData createBedOccupancyPdfFromXml(@Nullable String fhirResourceAsString)
      throws PdfGenerationException {
    return generatePdfFromBundle((Bundle) fhirParser.parseFromXml(fhirResourceAsString));
  }

  /**
   * Run a complete PDF rendering to initialize FHIR, Thymeleaf and Flying Sourcer.
   *
   * @throws PdfGenerationException initialization failed
   */
  @PostConstruct
  void generateInitialPdf() throws PdfGenerationException {
    long startMillis = System.currentTimeMillis();
    log.debug("Initializing bed occupancy reports service");
    if (createBedOccupancyPdfFromJson(readJsonBundle()) == null) {
      log.error("Bed occupancy reports service failed to create initial PDF file!");
    }
    log.debug(
        "Initialized bed occupancy reports service! Duration: {} ms",
        (System.currentTimeMillis() - startMillis));
  }

  private PdfData generatePdfFromBundle(Bundle bundle) throws PdfGenerationException {
    return new PdfData(this.pdfGenerator.generatePdfFromHtml(createHtml(bundle)));
  }

  private String createHtml(Bundle bundle) {
    return this.htmlTemplateParser.process(
        this.bedOccupancyFactory.create(bundle), this.bedOccupancyTemplate);
  }
}
