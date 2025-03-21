package de.gematik.demis.pdfgen.receipt.common.model.subsection;

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

import de.gematik.demis.notification.builder.demis.fhir.notification.builder.technicals.AddressDataBuilder;
import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public final class DemisAddressUseServiceTest {

  public static final String PRIMARY_CODE = DemisAddressUseService.CODE_PRIMARY;
  public static final String CURRENT_CODE = "current";
  public static final String ORDINARY_CODE = "ordinary";
  public static final String PRIMARY_GERMAN = "Hauptwohnsitz";
  public static final String CURRENT_GERMAN = "Derzeitiger Aufenthaltsort";

  /**
   * Add DEMIS address use as address extension
   *
   * @param address FHIR address object
   * @param code DEMIS address use code to add
   */
  public static void addUseExtension(Address address, String code) {
    Extension useExtension = new Extension();
    useExtension.setUrl(DemisExtensions.EXTENSION_URL_ADDRESS_USE);
    useExtension.setValue(
        new Coding().setSystem(DemisExtensions.EXTENSION_URL_ADDRESS_USE).setCode(code));
    address.addExtension(useExtension);
  }

  @Mock private AddressTranslationService addressTranslationService;
  private DemisAddressUseService demisAddressUseService;

  @BeforeEach
  void setDemisAddressUse() {
    this.demisAddressUseService = new DemisAddressUseService(this.addressTranslationService);
  }

  @ParameterizedTest
  @ValueSource(strings = {PRIMARY_CODE, "PRIMARY", "PRImary"})
  void primaryShouldDetectPrimarySingleton(String code) {
    Address address = new AddressDataBuilder().buildExampleAddress();
    addUseExtension(address, code);
    Assertions.assertThat(this.demisAddressUseService.isPrimaryAddress(address))
        .as("detected primary address")
        .isTrue();
  }

  @ParameterizedTest
  @CsvSource({"primary,current", "current,primary", "ordinary,primary", "foobar,PRImary"})
  void primaryShouldDetectPrimaryInCollection(String code1, String code2) {
    Address address = new AddressDataBuilder().buildExampleAddress();
    Stream.of(code1, code2)
        .filter(StringUtils::isNotBlank)
        .forEach(code -> addUseExtension(address, code));
    Assertions.assertThat(this.demisAddressUseService.isPrimaryAddress(address))
        .as("detected primary address")
        .isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {CURRENT_CODE, ORDINARY_CODE, "primari"})
  void primaryShouldReturnFalse(String code) {
    Address address = new AddressDataBuilder().buildExampleAddress();
    addUseExtension(address, code);
    Assertions.assertThat(this.demisAddressUseService.isPrimaryAddress(address))
        .as("there is no primary address")
        .isFalse();
  }

  @Test
  void toStringShouldTranslate() {
    Mockito.when(this.addressTranslationService.translateUse(Mockito.any()))
        .thenReturn(PRIMARY_GERMAN);
    Address address = new AddressDataBuilder().buildExampleAddress();
    addUseExtension(address, PRIMARY_CODE);
    String uses = this.demisAddressUseService.toString(address);
    Assertions.assertThat(uses).isEqualTo(PRIMARY_GERMAN);
  }

  @Test
  void toStringShouldConcatenate() {
    Mockito.when(this.addressTranslationService.translateUse(Mockito.any()))
        .thenReturn(PRIMARY_GERMAN, CURRENT_GERMAN);
    Address address = new AddressDataBuilder().buildExampleAddress();
    addUseExtension(address, PRIMARY_CODE);
    addUseExtension(address, CURRENT_CODE);
    String uses = this.demisAddressUseService.toString(address);
    Assertions.assertThat(uses).isEqualTo(PRIMARY_GERMAN + ", " + CURRENT_GERMAN);
  }
}
