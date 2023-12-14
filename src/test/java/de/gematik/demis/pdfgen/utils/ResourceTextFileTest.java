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

package de.gematik.demis.pdfgen.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import org.junit.jupiter.api.Test;

class ResourceTextFileTest {

  public static final String RESOURCE_TEXT_FILE = "fhir/BedOccupancyBundle.json";

  @Test
  void shouldReadFile() {
    ResourceTextFile textFile = new ResourceTextFile(RESOURCE_TEXT_FILE);
    assertThat(textFile).hasToString(RESOURCE_TEXT_FILE);
    String text = textFile.get();
    assertThat(text).isNotEmpty();
  }

  @Test
  void shouldThrowExceptionOnMissingFile() {
    String missingFile = "foo" + RESOURCE_TEXT_FILE;
    ResourceTextFile textFile = new ResourceTextFile(missingFile);
    assertThat(textFile).hasToString(missingFile);
    assertThatException()
        .isThrownBy(textFile::get)
        .isNotNull()
        .withMessage("Failed to read resource text file: " + missingFile);
  }
}
