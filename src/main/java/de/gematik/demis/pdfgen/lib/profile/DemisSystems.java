package de.gematik.demis.pdfgen.lib.profile;

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

import lombok.experimental.UtilityClass;

/**
 * Collection of systems that are used in the application. The systems are applied to
 * interpretations, codings and others. Keeping all systems (URLs) at one place allows for easier
 * search and navigation.
 */
@UtilityClass
public class DemisSystems {

  public static final String RESPONSIBLE_HEALTH_DEPARTMENT_CODING_SYSTEM =
      DemisProfiles.PROFILE_BASE_URL + "CodeSystem/ResponsibleDepartment";
  public static final String BSNR_ORGANIZATION_ID =
      "https://fhir.kbv.de/NamingSystem/KBV_NS_Base_BSNR";

  /** Outdated, legacy naming system of DEMIS laboratories/participants */
  public static final String DEMIS_LABORATORY_ID =
      DemisProfiles.PROFILE_BASE_URL + "NamingSystem/DemisLaboratoryId";

  /** Standardized naming of DEMIS participants */
  public static final String DEMIS_PARTICIPANT_ID =
      DemisProfiles.PROFILE_BASE_URL + "NamingSystem/DemisParticipantId";
}
