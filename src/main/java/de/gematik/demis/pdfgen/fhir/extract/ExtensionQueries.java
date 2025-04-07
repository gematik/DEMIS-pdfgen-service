package de.gematik.demis.pdfgen.fhir.extract;

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

import javax.annotation.Nullable;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;

public final class ExtensionQueries {
  private ExtensionQueries() {}

  @Nullable
  public static <T> T resolve(Extension extension, Class<T> clazz) {
    if (extension == null || clazz == null) {
      return null;
    }

    Type type = extension.getValue();

    if (clazz.isInstance(type)) {
      return clazz.cast(type);
    }
    return null;
  }
}
