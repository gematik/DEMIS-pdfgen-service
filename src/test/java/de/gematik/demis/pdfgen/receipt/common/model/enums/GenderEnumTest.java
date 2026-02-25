package de.gematik.demis.pdfgen.receipt.common.model.enums;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import java.util.stream.Stream;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;

class GenderEnumTest {

  @Test
  void shouldCreateEnumCorrectly_withFeatureFlagEnabled() {
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.MALE), true))
        .isEqualTo(GenderEnum.MALE);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.FEMALE), true))
        .isEqualTo(GenderEnum.FEMALE);
    assertThat(GenderEnum.of(createPatientWithGenderExtension("X"), true))
        .isEqualTo(GenderEnum.OTHERX);
    assertThat(GenderEnum.of(createPatientWithGenderExtension("D"), true))
        .isEqualTo(GenderEnum.DIVERSE);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.OTHER), true))
        .isEqualTo(GenderEnum.OTHERX);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.UNKNOWN), true))
        .isEqualTo(GenderEnum.UNKNOWN);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.NULL), true))
        .isEqualTo(GenderEnum.UNKNOWN);
    assertThat(GenderEnum.of(null, true)).isEqualTo(GenderEnum.UNKNOWN);
  }

  @Test
  void shouldCreateEnumCorrectly_withFeatureFlagDisabled() {
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.MALE), false))
        .isEqualTo(GenderEnum.MALE);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.FEMALE), false))
        .isEqualTo(GenderEnum.FEMALE);
    assertThat(GenderEnum.of(createPatientWithGenderExtension("X"), false))
        .isEqualTo(GenderEnum.DIVERSE);
    assertThat(GenderEnum.of(createPatientWithGenderExtension("D"), false))
        .isEqualTo(GenderEnum.DIVERSE);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.OTHER), false))
        .isEqualTo(GenderEnum.DIVERSE);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.UNKNOWN), false))
        .isEqualTo(GenderEnum.UNKNOWN);
    assertThat(GenderEnum.of(createPatient(AdministrativeGender.NULL), false))
        .isEqualTo(GenderEnum.UNKNOWN);
    assertThat(GenderEnum.of(null, false)).isEqualTo(GenderEnum.UNKNOWN);
  }

  private Patient createPatient(AdministrativeGender gender) {
    Patient patient = new Patient();
    patient.setGender(gender);
    return patient;
  }

  @Test
  void toString_evaluatesToCapitalizedName() {
    assertThat(GenderEnum.MALE).hasToString("Männlich");
    assertThat(GenderEnum.FEMALE).hasToString("Weiblich");
    assertThat(GenderEnum.OTHERX).hasToString("Kein Geschlechtseintrag");
    assertThat(GenderEnum.DIVERSE).hasToString("Divers");
    assertThat(GenderEnum.UNKNOWN).hasToString("Unbekannt");
  }

  private Patient createPatientWithGenderExtension(String extensionCode) {
    Patient patient = new Patient();
    patient.setGender(AdministrativeGender.OTHER);

    Coding coding = new Coding();
    coding.setSystem("http://fhir.de/CodeSystem/gender-amtlich-de");
    coding.setCode(extensionCode);

    Extension extension = new Extension();
    extension.setUrl(DemisExtensions.EXTENSION_URL_GENDER_AMTLICH_DE);
    extension.setValue(coding);

    patient.getGenderElement().addExtension(extension);
    return patient;
  }

  @Test
  void toString_allEnumsHaveNonBlankToStringValue() {
    Stream.of(GenderEnum.values()).forEach(value -> assertThat(value.toString()).isNotEmpty());
  }
}
