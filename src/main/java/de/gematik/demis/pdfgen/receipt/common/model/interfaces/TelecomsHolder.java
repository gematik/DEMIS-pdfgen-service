package de.gematik.demis.pdfgen.receipt.common.model.interfaces;

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

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import de.gematik.demis.pdfgen.utils.StringUtils;
import java.util.List;
import java.util.stream.Stream;

public interface TelecomsHolder {
  List<Telecom> getTelecoms();

  default String getTelecomsAsMultipleLines() {
    if (getTelecoms() == null) {
      return "";
    }
    Stream<String> sortedTelecom =
        getTelecoms().stream()
            .sorted(comparing(Telecom::getSystem, nullsLast(naturalOrder())))
            .map(Telecom::asSingleLine);
    return StringUtils.concatenateWithDelimiter(StringUtils.LINE_BREAK, sortedTelecom);
  }
}
