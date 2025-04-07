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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.pdfgen.receipt.common.model.enums.AddressUseEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import java.util.Comparator;
import java.util.Objects;

/**
 * Provides unified access to address information for a notified person and others for use in
 * templates. <br>
 * <br>
 * The type of address information differs based on origin. A notified person that references
 * address information of an organization will include telecomunication data, contact person and
 * more. A regular postal address attached to a notified person doesn't carry that data. <br>
 * <br>
 * To be able to render the correct address information we need to retrieve information from an
 * organization. At the same time we want to be able to sort all addresses for a person based on use
 * to provide a determinate order. By default this is not possible when an address is hidden in an
 * organization. <br>
 * <br>
 * We can achieve both goals by wrapping organization-based address information and regular address
 * information and then exposing a unified interface for use in templates.
 */
public class AddressTemplateAdapter {

  private final OrganizationDTO organization;
  private final AddressDTO address;

  private AddressTemplateAdapter(AddressDTO address, OrganizationDTO organization) {
    this.address = address;
    this.organization = organization;
  }

  public static AddressTemplateAdapter from(final OrganizationDTO organization) {
    return new AddressTemplateAdapter(organization.getAddressDTO(), organization);
  }

  public static AddressTemplateAdapter from(final AddressDTO address) {
    return new AddressTemplateAdapter(address, null);
  }

  public static Comparator<? super AddressTemplateAdapter> naturalOrder() {
    return Comparator.comparing(
        adapter -> Objects.requireNonNullElse(adapter.getUse(), AddressUseEnum.NULL));
  }

  /** Visible for testing */
  AddressUseEnum getUse() {
    return address.getUseEnum();
  }

  public boolean isFromOrganization() {
    return organization != null;
  }

  public String getTemplateString() {
    if (organization == null) {
      return address.getFullAddressAsSingleLine();
    } else {
      return organization.getOrganizationInfoIncludingName();
    }
  }

  public String getAddressUseWithPrefix(final String prefix) {
    return address.getAddressUseWithPrefix(prefix);
  }
}
