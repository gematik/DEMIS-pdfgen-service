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

import de.gematik.demis.pdfgen.fhir.extract.NotifiedFhirQueries;
import de.gematik.demis.pdfgen.lib.profile.DemisExtensions;
import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressFactory;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.ContactFactory;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.TelecomFactory;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifiedPersonFactory {

  private final NotifiedFhirQueries notifiedFhirQueries;
  private final ContactFactory contactFactory;
  private final TelecomFactory telecomFactory;
  private final AddressFactory addressFactory;
  private final OrganizationFactory organizationFactory;

  @Nullable
  public NotifiedPersonDTO create(final Bundle bundle) {
    Patient patient = patient(bundle);
    if (patient == null) {
      return null;
    }
    NotifiedPersonDTO.NotifiedPersonDTOBuilder notifiedPerson = NotifiedPersonDTO.builder();
    setName(patient, notifiedPerson);
    setGender(patient, notifiedPerson);
    setBirthdate(patient, notifiedPerson);
    setTelecoms(patient, notifiedPerson);
    setAddressesAndOrganizations(patient, notifiedPerson);
    return notifiedPerson.build();
  }

  private void setName(Patient patient, NotifiedPersonDTO.NotifiedPersonDTOBuilder notifiedPerson) {
    notifiedPerson.nameDTO(this.contactFactory.create(patient.getNameFirstRep()));
  }

  private static void setGender(
      Patient patient, NotifiedPersonDTO.NotifiedPersonDTOBuilder notifiedPerson) {
    notifiedPerson.gender(GenderEnum.of(patient.getGender()));
  }

  private static void setBirthdate(
      Patient patient, NotifiedPersonDTO.NotifiedPersonDTOBuilder notifiedPerson) {

    /*
     Use {@link org.hl7.fhir.r4.model.DateType} to get the birthdate and keep track of precision,
     in cases where the Day and/or Month of birth are missing, {@link java.util.Date} will deliver
     wrong values, when the birthdate is incomplete (§7.3/§7.4 Notifications).
    */
    notifiedPerson.birthdate(
        patient.hasBirthDate() ? new DateTimeHolder(patient.getBirthDateElement()) : null);
  }

  private void setTelecoms(
      Patient patient, NotifiedPersonDTO.NotifiedPersonDTOBuilder notifiedPerson) {
    notifiedPerson.telecoms(this.telecomFactory.createTelecoms(patient.getTelecom()));
  }

  private void setAddressesAndOrganizations(
      Patient patient, NotifiedPersonDTO.NotifiedPersonDTOBuilder notifiedPerson) {
    List<AddressDTO> addresses = new LinkedList<>();
    List<OrganizationDTO> organizations = new LinkedList<>();
    patient
        .getAddress()
        .forEach(address -> addAddressAndOrganizations(address, addresses, organizations));
    notifiedPerson.addressDTOs(addresses);
    notifiedPerson.organizationDTOs(organizations);
  }

  /**
   * A notified persons address may have multiple uses and multiple organizations attached.
   *
   * @param fhirAddress address to process notified persons address as FHIR object
   * @param addresses add new address object here
   * @param organizations add organizations of address object here
   */
  private void addAddressAndOrganizations(
      Address fhirAddress, List<AddressDTO> addresses, List<OrganizationDTO> organizations) {
    AddressDTO address = this.addressFactory.create(fhirAddress);
    if (getOrganizationExtensions(fhirAddress).isEmpty()) {
      // addresses that are referencing other organisations will end up empty and are not helpful,
      // so we filter them here
      addresses.add(address);
    }
    addOrganizations(fhirAddress, address, organizations);
  }

  private void addOrganizations(
      Address fhirAddress, AddressDTO address, List<OrganizationDTO> organizations) {
    getOrganizationExtensions(fhirAddress).stream()
        .map(o -> createOrganization(o, address))
        .filter(Objects::nonNull)
        .forEach(organizations::add);
  }

  /**
   * @return a list of address references to organization addresses
   */
  private static List<Extension> getOrganizationExtensions(Address fhirAddress) {
    return fhirAddress.getExtensionsByUrl(
        DemisExtensions.EXTENSION_URL_FACILTY_ADDRESS_NOTIFIED_PERSON);
  }

  private OrganizationDTO createOrganization(Extension parameters, AddressDTO address) {
    OrganizationDTO organization = this.organizationFactory.create(parameters);
    if (organization != null) {
      AddressDTO organizationAddress = organization.getAddressDTO();
      if (organizationAddress != null) {
        organizationAddress.setUse(address.getUse());
        organizationAddress.setUseEnum(address.getUseEnum());
      }
    }
    return organization;
  }

  private Patient patient(Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    return this.notifiedFhirQueries.getNotifiedPerson(bundle).orElse(null);
  }
}
