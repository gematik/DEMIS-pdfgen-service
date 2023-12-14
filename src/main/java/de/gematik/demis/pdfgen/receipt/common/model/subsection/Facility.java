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

package de.gematik.demis.pdfgen.receipt.common.model.subsection;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.gematik.demis.pdfgen.receipt.common.model.interfaces.TelecomsHolder;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Facility extends TelecomsHolder {
  private String name;
  private String type;
  private Identifier identifier;
  private AddressDTO addressDTO;
  private List<Telecom> telecoms;
  private NameDTO nameDTO;

  public String getNameAndType() {
    if (isBlank(name)) {
      return "";
    }
    String typeSuffix = isBlank(type) ? "" : " (" + type.trim() + ")";

    return name.trim() + typeSuffix;
  }

  public boolean isNotEmpty() {
    return isNotBlank(name);
  }
}
