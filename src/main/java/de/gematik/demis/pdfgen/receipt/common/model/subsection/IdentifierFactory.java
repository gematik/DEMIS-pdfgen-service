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

package de.gematik.demis.pdfgen.receipt.common.model.subsection;

import static de.gematik.demis.pdfgen.lib.profile.DemisSystems.*;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Collection;
import javax.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class IdentifierFactory {

  /**
   * Processes an arbitrary number of FHIR identifiers and extracts DEMIS identifier details
   * addressing a certain DEMIS participant.
   *
   * @param fhirIdentifiers all FHIR identifiers of a document
   * @return DEMIS identifier details
   */
  @Nullable
  public Identifier create(Collection<org.hl7.fhir.r4.model.Identifier> fhirIdentifiers) {
    if (isEmpty(fhirIdentifiers)) {
      return null;
    }
    Identifier.IdentifierBuilder id = Identifier.builder();
    for (org.hl7.fhir.r4.model.Identifier fhirIdentifier : fhirIdentifiers) {
      applyBsnrAndDemisId(fhirIdentifier, id);
    }
    return id.build();
  }

  private static void applyBsnrAndDemisId(
      org.hl7.fhir.r4.model.Identifier fhirIdentifier, Identifier.IdentifierBuilder builder) {
    switch (fhirIdentifier.getSystem()) {
      case BSNR_ORGANIZATION_ID -> builder.bsnr(fhirIdentifier.getValue());
      case DEMIS_LABORATORY_ID, DEMIS_PARTICIPANT_ID -> builder.demisId(fhirIdentifier.getValue());
    }
  }
}
