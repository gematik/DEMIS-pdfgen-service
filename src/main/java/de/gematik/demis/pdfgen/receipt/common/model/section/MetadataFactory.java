package de.gematik.demis.pdfgen.receipt.common.model.section;

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

import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.Bundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class MetadataFactory {

  @Nullable
  public static Metadata create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    return Metadata.builder()
        .identifier(getIdentifier(bundle))
        .date(getReceptionTimestamp(bundle))
        .build();
  }

  private static String getIdentifier(Bundle bundle) {
    if (bundle.hasIdentifier()) {
      return bundle.getIdentifier().getValue();
    }
    return null;
  }

  private static DateTimeHolder getReceptionTimestamp(Bundle bundle) {
    return new ReceptionTimestampFactory(bundle).get();
  }
}
