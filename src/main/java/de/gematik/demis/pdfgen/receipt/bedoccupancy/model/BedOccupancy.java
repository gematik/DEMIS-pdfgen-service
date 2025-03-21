package de.gematik.demis.pdfgen.receipt.bedoccupancy.model;

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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.pdfgen.receipt.common.model.section.Authentication;
import de.gematik.demis.pdfgen.receipt.common.model.section.Metadata;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import lombok.Builder;
import lombok.Getter;

/** Bed occupancy report data */
@Getter
@Builder
public class BedOccupancy {
  private Metadata metadata;

  /** Meldungs-ID */
  private String notificationId;

  private OrganizationDTO organization;
  private String numberOccupiedBedsGeneralWardAdults;
  private String numberOccupiedBedsGeneralWardChildren;
  private String numberOperableBedsGeneralWardAdults;
  private String numberOperableBedsGeneralWardChildren;
  private Authentication authentication;

  public boolean hasOperableData() {
    return isNotBlank(numberOperableBedsGeneralWardAdults)
        || isNotBlank(numberOperableBedsGeneralWardChildren);
  }
}
