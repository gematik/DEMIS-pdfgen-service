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

package de.gematik.demis.pdfgen.receipt.common.model.section;

import de.gematik.demis.pdfgen.receipt.common.model.enums.GenderEnum;
import de.gematik.demis.pdfgen.receipt.common.model.interfaces.TelecomsHolder;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.NameDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 *
 *
 * <h1>Notified person</h1>
 *
 * <h2>Addresses</h2>
 *
 * <p>Addresses of a notified person apply a special code system for "use", the "DEMIS address use".
 * This is different from the use of an address that's referenced under an organization entity. The
 * use information of an address linked to an organization is the standard FHIR address use.
 *
 * <h3>DEMIS address use</h3>
 *
 * <ul>
 *   <li>primary
 *   <li>current
 *   <li>ordinary
 * </ul>
 *
 * <h3>FHIR address use</h3>
 *
 * <ul>
 *   <li>home
 *   <li>work
 *   <li>temp
 *   <li>old
 * </ul>
 *
 * <h2>Organizations</h2>
 *
 * <p>A notified person may have facility (organization) details attached at a linked address. One
 * example of this is when elder people are living within nursing homes. There are legal
 * consequences in difference to when a person leads a fully independently in a private flat.
 *
 * <h2>FHIR specifications</h2>
 *
 * <ul>
 *   <li><a href="https://simplifier.net/demis/notifiedperson">Notified person</a>
 *   <li><a href="https://simplifier.net/demis/notifiedpersonfacility">Notified person facility</a>
 * </ul>
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class NotifiedPersonDTO extends TelecomsHolder {

  private NameDTO nameDTO;
  private GenderEnum gender;
  private DateTimeHolder birthdate;
  private List<AddressDTO> addressDTOs;
  private List<Telecom> telecoms;
  private List<OrganizationDTO> organizationDTOs;

  /** Using <code>AtomicReference</code> to exclude at Lombok builder */
  @Getter(AccessLevel.NONE)
  private final AtomicReference<AnonymizedNotifiedPerson> anonymized = new AtomicReference<>();

  public String getNotifiedName() {
    if (nameDTO == null || nameDTO.getFullName() == null) {
      return "";
    }
    return nameDTO.getFullName();
  }

  public AnonymizedNotifiedPerson anonymized() {
    /*
    This is not covered by the factory of notified persons because it is a cross-cutting feature
    that requires access to multiple details at once. This would make the factory unnecessarily more complex.
     */
    AnonymizedNotifiedPerson person = this.anonymized.get();
    if (person == null) {
      person =
          new AnonymizedNotifiedPersonFactory(this.gender, this.birthdate, this.addressDTOs).get();
      this.anonymized.set(person);
    }
    return person;
  }
}
