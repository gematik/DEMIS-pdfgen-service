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

package de.gematik.demis.pdfgen.receipt.bedoccupancy;

import de.gematik.demis.pdfgen.pdf.PdfGenerationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BedOccupancyController {
  private final BedOccupancyService service;

  public BedOccupancyController(BedOccupancyService service) {
    this.service = service;
  }

  @PostMapping(
      value = "/bedOccupancy",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> createBedOccupancyPdfFromJson(
      @RequestBody String fhirResourceAsString) throws PdfGenerationException {
    return service.createBedOccupancyPdfFromJson(fhirResourceAsString).createOkResponse();
  }

  @PostMapping(
      value = "/bedOccupancy",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> createBedOccupancyPdfFromXml(
      @RequestBody String fhirResourceAsString) throws PdfGenerationException {
    return service.createBedOccupancyPdfFromXml(fhirResourceAsString).createOkResponse();
  }
}
