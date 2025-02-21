package de.gematik.demis.pdfgen.test.helper;

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

import java.util.Objects;
import org.hl7.fhir.r4.model.Coding;
import org.mockito.ArgumentMatcher;

/**
 * Mockito argument matcher for FHIR coding. You have to use this to support multiple codings in a
 * translation service mock because FHIR coding does not have an implementation of <code>
 * boolean equals(Object)</code>.
 *
 * @param expected expected coding
 */
public record CodingMatcher(Coding expected) implements ArgumentMatcher<Coding> {
  @Override
  public boolean matches(Coding actual) {
    return Objects.nonNull(actual)
        && Objects.equals(this.expected.getSystem(), actual.getSystem())
        && Objects.equals(this.expected.getCode(), actual.getCode())
        && Objects.equals(this.expected.getDisplay(), actual.getDisplay());
  }
}
