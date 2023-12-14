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

package de.gematik.demis.pdfgen.receipt.common.service.html;

import de.gematik.demis.pdfgen.receipt.bedoccupancy.model.BedOccupancy;
import de.gematik.demis.pdfgen.receipt.bedoccupancy.model.BedOccupancyAddress;
import de.gematik.demis.pdfgen.receipt.bedoccupancy.model.BedOccupancyOrganization;
import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum;
import de.gematik.demis.pdfgen.receipt.common.model.section.Metadata;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import de.gematik.demis.pdfgen.test.helper.HtmlContentMatcher;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BedOccupancyHtmlTemplateParserIT {

  private static final String BED_OCCUPANCY_TEMPLATE = "receipt/bedOccupancy/bedOccupancyTemplate";
  private static final String URL_WITHOUT_SCHEME = "vd6gU";
  private static final String URL_WITH_HTTP = "http://oVRHV";
  private static final String URL_WITH_HTTPS = "https://jIg1p";
  private static final String EMAIL = "mXZbO";
  private static final String PHONE_WORK = "O6GUk";
  private static final String PHONE_MOBILE = "9QhN1";
  private static final String FAX = "jgwaw";

  @Autowired private HtmlTemplateParser parser;

  @Test
  void shouldCreateHtmlFromBedOccupancy() {
    // given
    BedOccupancyOrganization organization = createOrganization();
    BedOccupancy bedOccupancy = createBedOccupancy(organization);

    // when
    String html = parser.process(bedOccupancy, BED_OCCUPANCY_TEMPLATE);

    // then
    Metadata metadata = bedOccupancy.getMetadata();
    HtmlContentMatcher matcher =
        new HtmlContentMatcher(html)
            .addRow("Meldevorgangs-ID", metadata.getIdentifier())
            .addDateTimeRow("Zeitpunkt des Eingangs", metadata.getDate())
            .addRow("Meldungs-ID", bedOccupancy.getNotificationId())
            .addSectionTitle("Meldende Einrichtung")
            .addRow("Name", organization.getName())
            .addRow("Identifier", organization.getId())
            .addRow("Adresse", createExpectedAddress(organization.getAddress()))
            .addTelecomTitle("Kontakt")
            .addTelecomEntry("Telefon:", PHONE_WORK, "Dienstlich")
            .addTelecomEntry("Telefon:", PHONE_MOBILE, "Mobil")
            .addTelecomEntry("Fax:", FAX, null)
            .addTelecomEntry("E-Mail:", EMAIL, null)
            .addTelecomEntry("URL:", "https://" + URL_WITHOUT_SCHEME, "Privat")
            .addTelecomEntry("URL:", URL_WITH_HTTP, null)
            .addTelecomEntry("URL:", URL_WITH_HTTPS, null)
            .addSectionTitle("Belegte Betten auf den Normalstationen des meldenden Standortes")
            .addRow("Erwachsene", bedOccupancy.getNumberOccupiedBedsGeneralWardAdults())
            .addRow("Kinder", bedOccupancy.getNumberOccupiedBedsGeneralWardChildren())
            .addSectionTitle("Betreibbare Betten auf den Normalstationen des meldenden Standortes")
            .addRow("Erwachsene", bedOccupancy.getNumberOperableBedsGeneralWardAdults())
            .addRow("Kinder", bedOccupancy.getNumberOperableBedsGeneralWardChildren());
    matcher.assertMatches();
  }

  private BedOccupancy createBedOccupancy(BedOccupancyOrganization organization) {
    return BedOccupancy.builder()
        .metadata(Metadata.builder().identifier("bT87D").date(DateTimeHolder.now()).build())
        .notificationId("7Q4ef")
        .organization(organization)
        .numberOperableBedsGeneralWardAdults("QCZrY")
        .numberOccupiedBedsGeneralWardAdults("T6uG6")
        .numberOperableBedsGeneralWardChildren("IiHF1")
        .numberOccupiedBedsGeneralWardChildren("Cbhp2")
        .build();
  }

  private BedOccupancyOrganization createOrganization() {
    return BedOccupancyOrganization.builder()
        .id("fYyBP")
        .name("rQE5D")
        .address(createAddress())
        .telecoms(createTelecoms())
        .build();
  }

  private BedOccupancyAddress createAddress() {
    return BedOccupancyAddress.builder().line("jlIRE").city("KNaUz").postalCode("WhYRX").build();
  }

  private List<Telecom> createTelecoms() {
    Telecom telecom1 =
        Telecom.builder()
            .system(TelecomTypeEnum.URL)
            .use(TelecomUseEnum.HOME)
            .value(URL_WITHOUT_SCHEME)
            .build();
    Telecom telecom2 =
        Telecom.builder().system(TelecomTypeEnum.URL).use(null).value(URL_WITH_HTTP).build();
    Telecom telecom3 =
        Telecom.builder().system(TelecomTypeEnum.URL).use(null).value(URL_WITH_HTTPS).build();
    Telecom telecom4 =
        Telecom.builder().system(TelecomTypeEnum.EMAIL).use(null).value(EMAIL).build();
    Telecom telecom5 =
        Telecom.builder()
            .system(TelecomTypeEnum.PHONE)
            .use(TelecomUseEnum.WORK)
            .value(PHONE_WORK)
            .build();
    Telecom telecom6 =
        Telecom.builder()
            .system(TelecomTypeEnum.PHONE)
            .use(TelecomUseEnum.MOBILE)
            .value(PHONE_MOBILE)
            .build();
    Telecom telecom7 = Telecom.builder().system(TelecomTypeEnum.FAX).use(null).value(FAX).build();

    return List.of(telecom1, telecom2, telecom3, telecom4, telecom5, telecom6, telecom7);
  }

  private String createExpectedAddress(BedOccupancyAddress address) {
    return address.getLine() + ", " + address.getPostalCode() + " " + address.getCity();
  }
}
