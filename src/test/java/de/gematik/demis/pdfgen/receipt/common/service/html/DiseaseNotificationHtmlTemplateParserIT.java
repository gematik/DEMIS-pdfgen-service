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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.DISEASE_NOTIFICATION_BUNDLE_JSON;

import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDto;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.DiseaseNotificationTemplateDtoFactory;
import de.gematik.demis.pdfgen.test.helper.HtmlContentMatcher;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiseaseNotificationHtmlTemplateParserIT {

  private static final String DISEASE_NOTIFICATION_TEMPLATE =
      "receipt/diseaseNotification/diseaseNotificationTemplate";

  @Autowired private DiseaseNotificationTemplateDtoFactory dtoFactory;

  @Autowired private FhirParser fhirParser;

  @Autowired private HtmlTemplateParser htmlParser;

  @Test
  @Disabled(
      value =
          "Dieser Test funktioniert nicht. Die Prüfung eines Frameworks auf Funktionalität nicht unbedingt sinnvoll, daher ist fraglich, ob dieser Test noch notwending ist, da die ControllerIT bereits die Inhalte der PDF prüfen")
  void shouldCreateHtmlFromBedOccupancy() throws Exception {
    // given
    DiseaseNotificationTemplateDto diseaseNotificationTemplateDto =
        createDiseaseNotificationTemplateDto();

    // when
    String html = htmlParser.process(diseaseNotificationTemplateDto, DISEASE_NOTIFICATION_TEMPLATE);

    // then
    HtmlContentMatcher matcher =
        new HtmlContentMatcher(html)
            .addRow("Meldevorgangs-ID", "2d66a331-102a-4047-b666-1b2f18ee955e")
            .addRow("Zeitpunkt des Eingangs", "10.03.2022 14:57")
            .addRow("Meldungsidentifier", "7f562b87-f2c2-4e9d-b3fc-37f6b5dca3a5")
            .addRow("Meldungsstatus", "Final")
            .addRow("Meldungserstellung/-änderung", "10.03.2022 14:57")
            .addRow("Meldungsverweis \\(Primärlabor\\)", "appends ABC123")
            .addSectionTitle("Meldungsempfänger")
            .addRow("Name", "Kreis Herzogtum Lauenburg \\| Gesundheitsamt")
            .addRow("Adresse", "Barlachstr. 4, 23909 Ratzeburg")
            .addRow(
                "Kontakt",
                "Telefon: \\+49 4541 888-380\nFax: \\+49 4541 888-259\nE-Mail: gesundheitsdienste@kreis-rz.de")
            .addSectionTitle("Meldende Einrichtung")
            .addRow("Name", "TEST Organisation \\(Krankenhaus\\)")
            .addRow("Identifier", "BSNR: 123456789")
            .addRow("Adresse", "Krankenhausstraße 1, 21481 Buchhorst, Deutschland")
            .addRow("Kontakt", "Telefon: 01234567\nE-Mail: anna@ansprechpartner.de")
            .addRow("Kontaktperson", "Dr. Anna Beate Carolin Ansprechpartner")
            .addSectionTitle("Betroffene Person")
            .addRow("Name", "Bertha-Luise Hanna Karin Betroffen")
            .addRow("Geschlecht", "Weiblich")
            .addRow("Geburtsdatum", "09.06.1999")
            .addRow(
                "Adresse \\(Hauptwohnsitz\\)",
                "Berthastraße 123, 12345 Betroffenenstadt, Deutschland")
            .addRow(
                "Einrichtungsadresse \\(Derzeitiger Aufenthaltsort\\)",
                "TEST Organisation \\(Krankenhaus\\)\nKrankenhausstraße 1, 21481 Buchhorst, Deutschland\nTelefon: 01234567\nE-Mail: anna@ansprechpartner.de\nKontaktperson: Dr. Anna Beate Carolin Ansprechpartner")
            .addRow("Kontakt", "Telefon: 01234567\nE-Mail: bertha@betroffen.de")
            .addSectionTitle("Erkrankung")
            .addRow("Erkrankungsbeginn", "01.01.2022")
            .addRow("Datum der Diagnosestellung", "02.01.2022")
            //            .addRow(
            //                "Symptome",
            //                "Fieber\n"
            //                    + "Halsschmerzen/-entzündung\n"
            //                    + "Husten\n"
            //                    + "Pneunomie \\(Lungenentzündung\\)\n"
            //                    + "Schnupfen\n"
            //                    + "akutes schweres Atemnotsyndrom \\(ARDS\\)\n"
            //                    + "beatmungspflichtige Atemwegserkrankung\n"
            //                    + "Dyspnoe \\(Atemstörung\\)\n"
            //                    + "Durchfall, nicht näher bezeichnet\n"
            //                    + "Geruchsverlust\n"
            //                    + "Geschmacksverlust\n"
            //                    + "Tachypnoe \\(beschleunigte Atmung\\)\n"
            //                    + "Tachykardie \\(Herzrhythmusstörung mit Anstieg der
            // Herzfrequenz\\)\n"
            //                    + "allgemeine Krankheitszeichen\n"
            //                    + "Frösteln\n"
            //                    + "Kopfschmerzen\n"
            //                    + "Muskel-, Glieder- oder Rückenschmerzen")
            .addSectionTitle("Meldetatbestandsübergreifende klinische und epidemiologische Angaben")
            .addRow("Verstorben", "Ja")
            .addRow("Datum des Todes", "22.01.2022")
            .addRow("Zugehörigkeit zur Bundeswehr", "Soldat/BW-Angehöriger")
            .addRow("Laborbeauftragung", "Ja")
            .addSectionTitle("Beauftragtes Labor")
            .addRow("Name", "Labor")
            .addRow("Adresse", "Laborstraße 345, 21481 Buchhorst, Deutschland")
            .addRow("Kontakt", "Telefon: 666555444\nE-Mail: mail@labor.de")
            .addRow("Aufnahme in ein Krankenhaus", "Ja")
            .addSectionTitle("Krankenhaus 1")
            .addRow("Typ der Einrichtung", "Krankenhaus")
            .addRow("Intensivstation", "Nein")
            .addRow(
                "Einrichtungsadresse",
                "Krankenhausstraße 1, 21481 Buchhorst, Deutschland\n"
                    + "Telefon: 01234567\n"
                    + "E-Mail: anna@ansprechpartner.de\n"
                    + "Kontaktperson: Dr. Anna Beate Carolin Ansprechpartner")
            .addRow("Beginn", "05.01.2022 00:00")
            .addRow("Ende", "09.01.2022 00:00")
            .addRow("Hinweise zur Hospitalisierung", "wichtige Zusatzinformation")
            .addSectionTitle("Krankenhaus 2")
            .addRow("Typ der Einrichtung", "Krankenhaus")
            .addRow("Intensivstation", "Ja")
            .addRow(
                "Einrichtungsadresse",
                "Krankenhausstraße 1, 21481 Buchhorst, Deutschland\n"
                    + "Telefon: 01234567\n"
                    + "E-Mail: anna@ansprechpartner.de\n"
                    + "Kontaktperson: Dr. Anna Beate Carolin Ansprechpartner")
            .addRow("Beginn", "07.01.2022 00:00")
            .addRow(
                "Tätigkeit, Betreuung oder Unterbringung in Einrichtungen mit Relevanz für den Infektionsschutz \\(siehe § 23 Abs. 3 IfSG oder § 36 Abs. 1 oder 2 IfSG\\)",
                "Ja")
            .addRow("Beginn der Tätigkeit/Betreuung/Unterbringung", "01.12.2021")
            .addRow("Ende der Tätigkeit/Betreuung/Unterbringung", "05.01.2022")
            .addRow("Rolle", "Tätigkeit")
            .addRow(
                "Einrichtungsadresse",
                "Einrichtungsname\n"
                    + "Straße 123, 21481 Buchhorst, Deutschland\n"
                    + "Telefon: 0123456789\n"
                    + "E-Mail: mail@einrichtung.de")
            .addRow("Wahrscheinlicher Expositionsort bekannt", "Ja")
            .addRow("Wahrscheinlicher Expositionsort", "Libyen")
            .addRow(
                "Beginn des Aufenthalts am wahrscheinlichen Expositionsort/Datum des Aufenthalts",
                "20.12.2021")
            .addRow("Ende des Aufenthalts am wahrscheinlichen Expositionsort", "28.12.2021")
            .addRow("Anmerkungen zum Expositionsort", "Anmerkung")
            .addRow(
                "Spender für eine Blut-, Organ-, Gewebe- oder Zellspende in den letzten 6 Monaten",
                "Ja")
            .addRow(
                "Wichtige Zusatzinformationen",
                "Zusatzinformationen zu den meldetatbestandsübergreifenden klinischen und epidemiologischen Angaben")
            .addSectionTitle("Covid-19-spezifische klinische und epidemiologische Angaben")
            .addRow("Infektionsumfeld vorhanden", "Ja")
            .addRow("Wahrscheinliches Infektionsumfeld", "Gesundheitseinrichtung")
            .addRow("Beginn Infektionsumfeld", "28.12.2021")
            .addRow("Ende Infektionsumfeld", "30.12.2021")
            .addRow("Wurde die betroffene Person jemals in Bezug auf die Krankheit geimpft?", "Ja")
            .addSectionTitle("Impfung 1")
            .addRow("Impfstoff", "Comirnaty")
            .addRow("Datum der Impfung", "07.2021")
            .addRow("Hinweis zur Impfung", "Zusatzinfo2")
            .addSectionTitle("Impfung 2")
            // .addRow("Impfstoff", "nicht ermittelbar")
            .addRow("Datum der Impfung", "30.11.2021")
            .addRow("Hinweis zur Impfung", "Zusatzinfo3")
            .addSectionTitle("Impfung 3")
            .addRow("Impfstoff", "Anderer Impfstoff")
            .addRow("Datum der Impfung", "25.12.2021")
            .addRow("Hinweis zur Impfung", "Zusatzinfo4")
            .addSectionTitle("Impfung 4")
            .addRow("Impfstoff", "Comirnaty")
            .addRow("Datum der Impfung", "2021")
            .addRow("Hinweis zur Impfung", "Zusatzinfo1");

    matcher.assertMatches();
  }

  private DiseaseNotificationTemplateDto createDiseaseNotificationTemplateDto() throws Exception {
    Bundle bundle = (Bundle) fhirParser.parseFromJson(DISEASE_NOTIFICATION_BUNDLE_JSON);
    return dtoFactory.create(bundle);
  }
}
