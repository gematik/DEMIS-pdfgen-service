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

package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.enums;

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

import static de.gematik.demis.pdfgen.utils.StringUtils.makeEnumNameHumanFriendly;

import de.gematik.demis.pdfgen.utils.MessageUtil;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Specimen;

@Slf4j
@RequiredArgsConstructor
public enum SpecimenStatusEnum {
  AVAILABLE("enum.lab-test.specimen.status.available"),
  UNAVAILABLE("enum.lab-test.specimen.status.unavailable"),
  UNSATISFACTORY("enum.lab-test.specimen.status.unsatisfactory"),
  ENTERED_IN_ERROR("enum.lab-test.specimen.status.entered-in-error");

  private final String messageKey;

  @Nullable
  public static SpecimenStatusEnum of(Specimen.SpecimenStatus fhirStatus) {
    if (Specimen.SpecimenStatus.AVAILABLE == fhirStatus) {
      return AVAILABLE;
    } else if (Specimen.SpecimenStatus.UNAVAILABLE == fhirStatus) {
      return UNAVAILABLE;
    } else if (Specimen.SpecimenStatus.UNSATISFACTORY == fhirStatus) {
      return UNSATISFACTORY;
    } else if (Specimen.SpecimenStatus.ENTEREDINERROR == fhirStatus) {
      return ENTERED_IN_ERROR;
    } else if (fhirStatus == null) {
      return null;
    } else {
      log.warn("Received unexpected Specimen.SpecimenStatus value {}", fhirStatus);
      return null;
    }
  }

  @Override
  public String toString() {
    return MessageUtil.getOrDefault(messageKey, makeEnumNameHumanFriendly(name()));
  }
}
