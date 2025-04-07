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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import static de.gematik.demis.pdfgen.utils.StringUtils.concatenateWithDelimiter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import de.gematik.demis.pdfgen.receipt.common.model.interfaces.TelecomsHolder;
import de.gematik.demis.pdfgen.utils.MessageUtil;
import de.gematik.demis.pdfgen.utils.StringUtils;
import java.util.List;
import java.util.Objects;

/**
 * This interface contains the essential organization information and provides higher printing
 * methods that combine those information.
 */
public interface PrintableOrganization extends TelecomsHolder {

  String getName();

  String getType();

  AddressDTO getAddressDTO();

  List<Telecom> getTelecoms();

  NameDTO getNameDTO();

  String getDepartment();

  default String getSubmitterDetails() {
    return concatenateWithDelimiter(
        StringUtils.LINE_BREAK,
        getFullNameNullSafe(),
        getDepartment(),
        getTelecomsAsMultipleLines());
  }

  default String getFullNameNullSafe() {
    final NameDTO nameDTO = getNameDTO();
    if (nameDTO != null) {
      return nameDTO.getFullName();
    } else {
      return "";
    }
  }

  default String getOrganizationInfoIncludingName() {
    return concatenateWithDelimiter(
        StringUtils.LINE_BREAK,
        getNameAndType(),
        getAddressDTO().getOrganizationAddressAsSingleLine(),
        getTelecomsAsMultipleLines(),
        getContactWithLabel());
  }

  default String getOrganizationInfo() {
    return concatenateWithDelimiter(
        StringUtils.LINE_BREAK,
        getAddressDTO().getOrganizationAddressAsSingleLine(),
        getTelecomsAsMultipleLines(),
        getContactWithLabel());
  }

  default String getNameAndType() {
    StringBuilder text = new StringBuilder();
    appendName(text);
    appendType(text);
    return text.toString();
  }

  default String getNameAndStationOrType() {
    StringBuilder text = new StringBuilder();
    appendName(text);

    if (Objects.nonNull(getDepartment()) && !getDepartment().isBlank()) {
      text.append(" (").append(getDepartment()).append(')');
    } else {
      appendType(text);
    }

    return text.toString();
  }

  default String getContactWithLabel() {
    NameDTO name = getNameDTO();
    if (name == null) {
      return "";
    }
    String fullName = name.getFullName();
    if (isBlank(fullName)) {
      return "";
    }
    return MessageUtil.get("common.organization.contact.prefix").trim() + " " + fullName;
  }

  /**
   * Check if name is set and not blank
   *
   * @return <code>true</code> if name is set and not blank, otherwise <code>false</code>
   */
  default boolean isNotEmpty() {
    return isNotBlank(getName());
  }

  private void appendName(StringBuilder text) {
    String name = trimToNull(getName());
    if (name != null) {
      text.append(name);
    }
  }

  private void appendType(StringBuilder text) {
    String type = trimToNull(getType());
    if (type != null) {
      if (!text.isEmpty()) {
        text.append(' ');
      }
      text.append('(').append(type).append(')');
    }
  }
}
