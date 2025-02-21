package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources;

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

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import lombok.Getter;

/** Resource wrapper for when a questionnaire response item is actually a resource. */
@Getter
public final class Resource {

  private final Hospitalization hospitalization;
  private final Immunization immunization;
  private final OrganizationDTO laboratory;
  private final OrganizationDTO organization;

  private Resource(
      Hospitalization hospitalization,
      Immunization immunization,
      OrganizationDTO laboratory,
      OrganizationDTO organization) {
    this.hospitalization = hospitalization;
    this.immunization = immunization;
    this.laboratory = laboratory;
    this.organization = organization;
  }

  public static Resource hospitalization(Hospitalization hospitalization) {
    return new Resource(hospitalization, null, null, null);
  }

  public static Resource immunization(Immunization immunization) {
    return new Resource(null, immunization, null, null);
  }

  public static Resource laboratory(OrganizationDTO laboratory) {
    return new Resource(null, null, laboratory, null);
  }

  public static Resource organization(OrganizationDTO organization) {
    return new Resource(null, null, null, organization);
  }

  public boolean isSameType(Resource other) {
    return bothHospitalization(other)
        || bothImmunization(other)
        || bothLaboratory(other)
        || bothOrganization(other);
  }

  private boolean bothHospitalization(Resource other) {
    return (this.hospitalization != null) && (other.hospitalization != null);
  }

  private boolean bothImmunization(Resource other) {
    return (this.immunization != null) && (other.immunization != null);
  }

  private boolean bothLaboratory(Resource other) {
    return (this.laboratory != null) && (other.laboratory != null);
  }

  private boolean bothOrganization(Resource other) {
    return (this.organization != null) && (other.organization != null);
  }
}
