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

import static de.gematik.demis.pdfgen.utils.StringUtils.makeEnumNameHumanFriendly;

import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import de.gematik.demis.pdfgen.utils.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;

@Slf4j
@RequiredArgsConstructor
public enum GenderEnum {
  MALE("enum.gender.male"),
  FEMALE("enum.gender.female"),
  OTHERX("enum.gender.otherx"),
  DIVERSE("enum.gender.diverse"),
  UNKNOWN("enum.gender.unknown");

  private final String messageKey;

  public static GenderEnum of(Patient patient) {
    if (patient == null) {
      return UNKNOWN;
    }
    AdministrativeGender fhirGender = patient.getGender();
    if (AdministrativeGender.MALE == fhirGender) {
      return MALE;
    } else if (AdministrativeGender.FEMALE == fhirGender) {
      return FEMALE;
    } else if (AdministrativeGender.OTHER == fhirGender) {
      if ("D".equals(getGenderExtensionCode(patient))) {
        return DIVERSE;
      }
      return OTHERX;
    } else if (AdministrativeGender.UNKNOWN == fhirGender
        || AdministrativeGender.NULL == fhirGender) {
      return UNKNOWN;
    } else {
      log.warn("Received unexpected AdministrativeGender value {}", fhirGender);
      return UNKNOWN;
    }
  }

  private static String getGenderExtensionCode(Patient patient) {
    if (patient.getGenderElement() == null) {
      return null;
    }
    Extension ext =
        patient
            .getGenderElement()
            .getExtensionByUrl(DemisExtensions.EXTENSION_URL_GENDER_AMTLICH_DE);
    if (ext != null && ext.getValue() instanceof Coding coding) {
      return coding.getCode();
    }
    return null;
  }

  @Override
  public String toString() {
    return MessageUtil.getOrDefault(messageKey, makeEnumNameHumanFriendly(name()));
  }
}
