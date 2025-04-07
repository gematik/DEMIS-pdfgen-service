package de.gematik.demis.pdfgen.receipt.bedoccupancy.model;

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

import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.PHONE;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.HOME;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.section.Metadata;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.AddressDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import org.junit.jupiter.api.Test;

class BedOccupancyTest {
  @Test
  void hasOperableData_shouldEvaluateIfOperableIsPresent() {
    // given
    BedOccupancy noData = createBedOccupancy(null, null);
    BedOccupancy onlyChildren = createBedOccupancy(null, "operableChildren");
    BedOccupancy onlyAdults = createBedOccupancy("operableAdult", null);
    BedOccupancy bothPresent = createBedOccupancy("operableAdult", "operableChildren");

    // then
    assertThat(noData.hasOperableData()).isFalse();
    assertThat(onlyChildren.hasOperableData()).isTrue();
    assertThat(onlyAdults.hasOperableData()).isTrue();
    assertThat(bothPresent.hasOperableData()).isTrue();
  }

  private BedOccupancy createBedOccupancy(String operableAdult, String operableChildren) {
    return BedOccupancy.builder()
        .metadata(Metadata.builder().identifier("id").date(DateTimeHolder.now()).build())
        .notificationId("notificationId")
        .organization(createOrganization())
        .numberOccupiedBedsGeneralWardAdults("numberOccupiedBedsGeneralWardAdults")
        .numberOccupiedBedsGeneralWardChildren("numberOccupiedBedsGeneralWardChildren")
        .numberOperableBedsGeneralWardAdults(operableAdult)
        .numberOperableBedsGeneralWardChildren(operableChildren)
        .build();
  }

  private OrganizationDTO createOrganization() {
    AddressDTO address =
        AddressDTO.builder().line("line").postalCode("postalCode").city("city").build();
    Telecom phone = Telecom.builder().system(PHONE).value("phone1").use(HOME).build();
    List<Telecom> telecoms = List.of(phone);
    return OrganizationDTO.builder()
        .id("orgId")
        .name("orgName")
        .addressDTO(address)
        .telecoms(telecoms)
        .build();
  }
}
