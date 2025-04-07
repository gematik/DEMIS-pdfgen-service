package de.gematik.demis.pdfgen;

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

import ca.uhn.fhir.context.FhirContext;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.service.base.apidoc.EnableDefaultApiSpecConfig;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * This class can be registered on a Spring ApplicationContext to scan this package and all
 * sub-packages for components to be registered for dependency injection.
 */
@Configuration
@EnableDefaultApiSpecConfig
@ComponentScan()
public class PdfgenServiceConfig {

  @Bean
  public FhirContext fhirContext() {
    return FhirContext.forR4();
  }

  @Bean
  public FhirParser fhirParser(FhirContext fhirContext) {
    return new FhirParser(fhirContext);
  }

  @Bean
  ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
    return new ObservedAspect(observationRegistry);
  }
}
