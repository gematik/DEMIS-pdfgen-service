package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport;

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

import de.gematik.demis.pdfgen.fhir.extract.LaboratoryFhirQueries;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums.LabTestStatusEnum;
import de.gematik.demis.pdfgen.translation.TranslationService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
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
    Optional<String> interpretationOptional = getInterpretation(pathogenDetection);
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
    final CodeableConcept methodCoding = pathogenDetection.getMethod();
    final String methodCodingText = methodCoding.getText() != null ? methodCoding.getText() : "";
    return methodCoding.getCodingFirstRep().getCode() != null
        ? displayTranslationService.resolveCodeableConceptValues(methodCoding)
        : methodCodingText;
  }

  private Optional<String> getInterpretation(Observation pathogenDetection) {
    return Optional.of(
        displayTranslationService.resolveCodeableConceptValues(
            pathogenDetection.getInterpretationFirstRep()));
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

    if (type instanceof StringType stringType) {
      return stringType.getValue();
    } else if (type instanceof Quantity quantity) {
      return resolveQuantityValues(quantity);
    } else if (type instanceof CodeableConcept codeableConcept) {
      String resolvedCoding =
          displayTranslationService.resolveCodeableConceptValues(codeableConcept);
      String text =
          codeableConcept.getText() != null
              ? System.lineSeparator() + codeableConcept.getText()
              : "";
      return resolvedCoding + text;
    }
    if (type instanceof Ratio ratio) {
      return resolveRatioValues(ratio);
    } else if (type instanceof Range range) {
      return resolveRangeValues(range);
    }
    return null;
  }

  private String resolveRangeValues(Range range) {
    BigDecimal low = range.getLow().getValue();
    BigDecimal high = range.getHigh().getValue();
    String code = displayTranslationService.getValueQuantityUnit(range.getHigh().getCode());
    return String.format("von %s bis %s %s", low, high, code).trim();
  }

  private String resolveRatioValues(Ratio ratio) {
    BigDecimal numerator = ratio.getNumerator().getValue();
    BigDecimal denominator = ratio.getDenominator().getValue();
    String code = displayTranslationService.getValueQuantityUnit(ratio.getNumerator().getCode());
    return String.format("%s:%s %s", numerator, denominator, code).trim();
  }

  private String resolveQuantityValues(Quantity quantity) {
    String comparator =
        quantity.getComparator() != null ? quantity.getComparator().toCode() + " " : "";
    String value = quantity.getValue() + " ";
    String code = quantity.getCode();
    String unitOrCode = quantity.getUnit() != null ? quantity.getUnit() : code;
    return String.format("%s%s%s", comparator, value, unitOrCode).trim();
  }
}
