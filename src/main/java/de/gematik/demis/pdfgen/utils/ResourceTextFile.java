package de.gematik.demis.pdfgen.utils;

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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

/** Bundled resource text file */
@RequiredArgsConstructor
public final class ResourceTextFile implements Supplier<String> {

  private final String resourceFile;

  /**
   * Get text of bundled resource text file
   *
   * @return text
   */
  @Override
  public String get() {
    try (InputStream in =
        Optional.ofNullable(
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(this.resourceFile))
            .orElseThrow()) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to read resource text file: " + resourceFile, e);
    }
  }

  @Override
  public String toString() {
    return this.resourceFile;
  }
}
