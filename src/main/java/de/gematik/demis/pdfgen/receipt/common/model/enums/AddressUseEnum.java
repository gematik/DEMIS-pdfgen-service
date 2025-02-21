package de.gematik.demis.pdfgen.receipt.common.model.enums;

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

import org.hl7.fhir.r4.model.Coding;

/**
 * Used for sorting addresses e.g. in {@link
 * de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonDTO}. This duplicates
 * information available in FHIR, so that we can avoid adding FHIR as dependency to our DTOs.
 */
public enum AddressUseEnum {
  // NOTE: the ordinal here is crucial for sorting. Define order the way you want it sorted (i.e.
  // shown in PDF)
  PRIMARY,
  ORDINARY,
  CURRENT,
  NULL; // for unknown address codings (don't crash the service)

  public static AddressUseEnum from(Coding type) {
    return switch (type.getCode()) {
      case "primary" -> PRIMARY;
      case "ordinary" -> ORDINARY;
      case "current" -> CURRENT;
      default -> NULL;
    };
  }
}
