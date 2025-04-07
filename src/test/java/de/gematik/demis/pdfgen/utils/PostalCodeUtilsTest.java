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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PostalCodeUtilsTest {

  @Test
  void testAnonymizeWithPlaceholders_NullPostalCode() {
    assertNull(PostalCodeUtils.anonymizeWithPlaceholders(null));
  }

  @Test
  void testAnonymizeWithPlaceholders_EmptyPostalCode() {
    assertEquals("", PostalCodeUtils.anonymizeWithPlaceholders(""));
  }

  @Test
  void testAnonymizeWithPlaceholders_BlankPostalCode() {
    assertEquals("   ", PostalCodeUtils.anonymizeWithPlaceholders("   "));
  }

  @Test
  void testAnonymizeWithPlaceholders_ShortPostalCode() {
    assertEquals("12---", PostalCodeUtils.anonymizeWithPlaceholders("12"));
  }

  @Test
  void testAnonymizeWithPlaceholders_ExactLengthPostalCode() {
    assertEquals("12345", PostalCodeUtils.anonymizeWithPlaceholders("12345"));
  }

  @Test
  void testAnonymizeWithPlaceholders_LongPostalCode() {
    assertEquals("123456", PostalCodeUtils.anonymizeWithPlaceholders("123456"));
  }

  @Test
  void testShortPostalCode_NullPostalCode() {
    assertNull(PostalCodeUtils.shortPostalCode(null));
  }

  @Test
  void testShortPostalCode_EmptyPostalCode() {
    assertEquals("", PostalCodeUtils.shortPostalCode(""));
  }

  @Test
  void testShortPostalCode_BlankPostalCode() {
    assertEquals("   ", PostalCodeUtils.shortPostalCode("   "));
  }

  @Test
  void testShortPostalCode_ShortPostalCode() {
    assertEquals("12-", PostalCodeUtils.shortPostalCode("12"));
  }

  @Test
  void testShortPostalCode_ExactLengthPostalCode() {
    assertEquals("123", PostalCodeUtils.shortPostalCode("123"));
  }

  @Test
  void testShortPostalCode_LongPostalCode() {
    assertEquals("123", PostalCodeUtils.shortPostalCode("123456"));
  }
}
