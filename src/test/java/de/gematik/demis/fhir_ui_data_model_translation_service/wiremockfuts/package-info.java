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

/**
 *
 *
 * <h1>FHIR UI Data Model Translation Service</h1>
 *
 * <p>WireMock-ing the DEMIS "FHIR UI Data Model Translation Service"
 *
 * <h2>Usage</h2>
 *
 * <p>Activate WireMock's <a href="https://wiremock.org/docs/junit-jupiter/">JUnit integration</a>.
 * Use <code>@WireMockTest</code> as class annotation.
 *
 * <p>The lifecycle management of WireMock will be handled by WireMock JUnit integration. The <code>
 * run()</code>-method currently only starts the WireMock server configuration.
 *
 * <h3>Laboratory tests</h3>
 *
 * <pre>
 *     WireMockFuts futs = new WireMockFuts();
 *     futs.setDefaults();
 *     futs.add(new LaboratoryFeature());
 * </pre>
 *
 * <h3>Disease tests</h3>
 *
 * <pre>
 *     new WireMockFuts().setDefaults().add(new DiseaseFeature());
 * </pre>
 *
 * <h3>Stop</h3>
 *
 * Handled by WireMock JUnit integration
 */
package de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts;

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
