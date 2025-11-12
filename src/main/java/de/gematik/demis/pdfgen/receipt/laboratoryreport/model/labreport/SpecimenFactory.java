package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport;

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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.pdfgen.utils.StringUtils.LIST_DELIMITER;

import de.gematik.demis.pdfgen.fhir.extract.LaboratoryFhirQueries;
import de.gematik.demis.pdfgen.fhir.schema.SchemaVersion;
import de.gematik.demis.pdfgen.fhir.schema.SchemaVersionChecker;
import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums.SpecimenStatusEnum;
import de.gematik.demis.pdfgen.translation.TranslationService;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecimenFactory {
  private final LaboratoryFhirQueries laboratoryFhirQueries;
  private final SchemaVersionChecker schemaVersionChecker;
  private final TranslationService displayTranslationService;

  @Nullable
  public Specimen create(final Observation observation, final Bundle bundle) {
    if (schemaVersionChecker.getSchemaVersion(bundle) == SchemaVersion.V1) {
      return createFromDv1Bundle(observation);
    } else {
      return createFromDv2Bundle(observation, bundle);
    }
  }

  @Nullable
  private Specimen createFromDv1Bundle(final Observation observation) {
    if (observation.hasSpecimen()
        && observation.getSpecimen().getResource()
            instanceof org.hl7.fhir.r4.model.Specimen fhirSpecimen) {
      return create(fhirSpecimen);
    }
    return null;
  }

  @Nullable
  private Specimen createFromDv2Bundle(final Observation pathogenDetection, final Bundle bundle) {
    Map<Observation, org.hl7.fhir.r4.model.Specimen> pathogenDetectionToSpecimenMap =
        laboratoryFhirQueries.getPathogenDetectionToSpecimenMap(bundle);
    org.hl7.fhir.r4.model.Specimen fhirSpecimen =
        pathogenDetectionToSpecimenMap.get(pathogenDetection);
    return create(fhirSpecimen);
  }

  private Specimen create(final org.hl7.fhir.r4.model.Specimen fhirSpecimen) {
    if (fhirSpecimen == null) {
      return null;
    }

    DateTimeHolder receivedTime = new DateTimeHolder(fhirSpecimen.getReceivedTimeElement());
    DateTimeHolder collectionTime =
        new DateTimeHolder(fhirSpecimen.getCollection().getCollectedDateTimeType());
    SpecimenStatusEnum status = SpecimenStatusEnum.of(fhirSpecimen.getStatus());
    String material = extractMaterialWithDV1Fallback(fhirSpecimen);
    List<String> notes = getNotes(fhirSpecimen);
    String transactionId = getTransactionId(fhirSpecimen);
    String id = fhirSpecimen.getId();

    return Specimen.builder()
        .receivedTime(receivedTime)
        .collectionTime(collectionTime)
        .status(status)
        .notes(notes)
        .material(material)
        .transactionId(transactionId)
        .id(id)
        .build();
  }

  private String extractMaterialWithDV1Fallback(org.hl7.fhir.r4.model.Specimen fhirSpecimen) {
    CodeableConcept type = fhirSpecimen.getType();
    final String material;
    if (type.getCodingFirstRep().getCode() != null) {
      material = displayTranslationService.resolveCodeableConceptValues(type);
    } else {
      material = Objects.requireNonNullElse(type.getText(), "");
    }
    return material;
  }

  private List<String> getNotes(org.hl7.fhir.r4.model.Specimen fhirSpecimen) {
    return fhirSpecimen.getNote().stream().map(Annotation::getText).toList();
  }

  private String getTransactionId(org.hl7.fhir.r4.model.Specimen fhirSpecimen) {
    return getTransactionIdFromProcessing(fhirSpecimen.getProcessing());
  }

  String getTransactionIdFromProcessing(
      List<org.hl7.fhir.r4.model.Specimen.SpecimenProcessingComponent> processing) {

    return processing.parallelStream()
        .filter(entry -> entry.hasExtension(DemisExtensions.EXTENSION_URL_TRANSACTION_ID))
        .map(
            entry ->
                extractExtensionValue(
                    entry.getExtensionByUrl(DemisExtensions.EXTENSION_URL_TRANSACTION_ID)))
        .filter(value -> value != null && !value.isBlank())
        .collect(Collectors.joining(LIST_DELIMITER));
  }

  private String extractExtensionValue(final Extension extension) {
    var extensionValue = extension.getValue();
    if (extensionValue instanceof StringType stringType) {
      return stringType.getValue();
    }

    if (extensionValue instanceof Identifier identifier) {
      return identifier.getValue();
    }

    return null;
  }
}
