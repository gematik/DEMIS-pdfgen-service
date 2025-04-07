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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;

/**
 * Hospitalization data (FHIR encounter)
 *
 * @param serviceType <a
 *     href="https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType">hospitalization
 *     service type</a>, which basically comes down to the individual hospital department (Station)
 * @param start start
 * @param end end
 * @param note note
 * @param organizationType type of organization
 * @param organization organization resource
 * @param reason reason for hospitalization
 */
public record Hospitalization(
    String serviceType,
    DateTimeHolder start,
    DateTimeHolder end,
    String note,
    String organizationType,
    OrganizationDTO organization,
    String reason) {}
