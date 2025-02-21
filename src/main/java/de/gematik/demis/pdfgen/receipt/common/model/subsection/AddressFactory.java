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

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressFactory {

  private static final String HEALTH_DEPARTMENT_NAME_DELIMITER = " | ";

  private final AddressTranslationService addressTranslationService;
  private final DemisAddressUseService demisAddressUseService;

  @Nullable
  public AddressDTO create(final org.hl7.fhir.r4.model.Address fhirAddress) {
    return create(fhirAddress, null);
  }

  @Nullable
  public AddressDTO create(
      final org.hl7.fhir.r4.model.Address fhirAddress,
      final org.hl7.fhir.r4.model.Organization fhirOrganization) {
    if (fhirAddress == null) {
      return null;
    }

    String countryName = addressTranslationService.translateCountryCode(fhirAddress.getCountry());
    AddressDTO.AddressDTOBuilder addressBuilder =
        AddressDTO.builder()
            .postalCode(fhirAddress.getPostalCode())
            .city(fhirAddress.getCity())
            .country(countryName)
            .use(this.demisAddressUseService.toString(fhirAddress))
            .useEnum(this.demisAddressUseService.toUseEnum(fhirAddress))
            .primaryAddress(this.demisAddressUseService.isPrimaryAddress(fhirAddress));

    if (fhirOrganization != null) {
      OrganizationNameAndDepartment nameAndDepartment =
          new OrganizationNameAndDepartment(fhirOrganization);
      addressBuilder.department(nameAndDepartment.department).organization(nameAndDepartment.name);
    }

    String addressLine = getAddressLine(fhirAddress);
    if (addressLine != null) {
      addressBuilder.line(addressLine);
    }
    return addressBuilder.build();
  }

  @Nullable
  public AddressDTO create(TransmittingSite site) {
    if (site == null) {
      return null;
    }
    return AddressDTO.builder()
        .line(site.getStreet())
        .postalCode(site.getPostalCode())
        .city(site.getPlace())
        .build();
  }

  @Nullable
  private String getAddressLine(org.hl7.fhir.r4.model.Address fhirAddress) {
    if (isEmpty(fhirAddress.getLine()) || fhirAddress.getLine().getFirst() == null) {
      return null;
    }
    return fhirAddress.getLine().getFirst().getValue();
  }

  private static class OrganizationNameAndDepartment {

    public final String name;
    public final String department;

    public OrganizationNameAndDepartment(
        final org.hl7.fhir.r4.model.Organization fhirOrganization) {
      String organizationName = fhirOrganization.getName();
      if (isBlank(organizationName)) {
        this.name = null;
        this.department = null;
      } else {
        String[] nameAndDepartment =
            StringUtils.splitByWholeSeparator(organizationName, HEALTH_DEPARTMENT_NAME_DELIMITER);
        if (nameAndDepartment.length == 2) {
          this.name = nameAndDepartment[0];
          this.department = nameAndDepartment[1];
        } else {
          log.error("Failed to resolve name and department from health-department organization.");
          this.name = organizationName;
          this.department = null;
        }
      }
    }
  }
}
