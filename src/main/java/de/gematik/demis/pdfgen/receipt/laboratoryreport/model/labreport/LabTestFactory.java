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

package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport;

import de.gematik.demis.pdfgen.fhir.extract.LaboratoryFhirQueries;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums.LabTestInterpretationEnum;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums.LabTestStatusEnum;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabTestFactory {

  private final LaboratoryFhirQueries laboratoryFhirQueries;
  private final SpecimenFactory specimenFactory;
  private final TranslationService displayTranslationService;

  @Nonnull
  public List<LabTest> createLabTests(final Bundle bundle) {
    return laboratoryFhirQueries.getPathogenDetections(bundle).stream()
        .map(pathogenDetection -> create(pathogenDetection, bundle))
        .toList();
  }

  private LabTest create(final Observation pathogenDetection, final Bundle bundle) {
    String code =
        displayTranslationService.resolveCodeableConceptValues(pathogenDetection.getCode());
    LabTestStatusEnum status = LabTestStatusEnum.of(pathogenDetection.getStatus());
    String value = getValue(pathogenDetection);
    Optional<LabTestInterpretationEnum> interpretationOptional =
        getInterpretation(pathogenDetection);
    String method = extractMethodWithDV1Fallback(pathogenDetection);
    List<String> notes = getNotes(pathogenDetection);
    Specimen specimen = specimenFactory.create(pathogenDetection, bundle);

    LabTest.LabTestBuilder builder =
        LabTest.builder()
            .status(status)
            .notes(notes)
            .code(code)
            .specimen(specimen)
            .method(method)
            .value(value);
    interpretationOptional.ifPresent(builder::interpretation);

    return builder.build();
  }

  private String extractMethodWithDV1Fallback(Observation pathogenDetection) {
    CodeableConcept methodCoding = pathogenDetection.getMethod();
    return methodCoding.getCodingFirstRep().getCode() != null
        ? displayTranslationService.resolveCodeableConceptValues(methodCoding)
        : methodCoding.getText() != null ? methodCoding.getText() : "";
  }

  private Optional<LabTestInterpretationEnum> getInterpretation(Observation pathogenDetection) {
    Coding coding = pathogenDetection.getInterpretationFirstRep().getCodingFirstRep();
    return LabTestInterpretationEnum.ofCode(coding.getCode());
  }

  private List<String> getNotes(Observation pathogenDetection) {
    return pathogenDetection.getNote().stream().map(Annotation::getText).toList();
  }

  @Nullable
  private String getValue(Observation observation) {
    if (!observation.hasValue()) {
      return null;
    }

    Type type = observation.getValue();

    if (type instanceof StringType) {
      return type.castToString(type).getValue();

    } else if (type instanceof Quantity) {
      return resolveQuantityValues(type.castToQuantity(type));

    } else if (type instanceof CodeableConcept) {
      // TODO check if really needed
      return displayTranslationService.resolveCodeableConceptValues(
          type.castToCodeableConcept(type));
    }
    return null;
  }

  private String resolveQuantityValues(Quantity quantity) {
    String value = "";

    if (quantity != null) {
      value += quantity.getSystem() != null ? quantity.getSystem() + " " : "";
      value += quantity.getCode() != null ? quantity.getCode() + " " : "";
      value += quantity.getUnit() != null ? quantity.getUnit() + " " : "";
      value += quantity.getComparator() != null ? quantity.getComparator().toCode() : "";
      value += quantity.getValue() != null ? quantity.getValue() : "";
    }

    return value;
  }
}
