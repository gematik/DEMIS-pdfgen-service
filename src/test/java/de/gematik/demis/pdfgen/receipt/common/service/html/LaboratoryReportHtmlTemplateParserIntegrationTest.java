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

import static de.gematik.demis.pdfgen.test.helper.FhirFactory.LABORATORY_REPORT_BUNDLE_DV2_JSON;

import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.LaboratoryReportTemplateDto;
import de.gematik.demis.pdfgen.receipt.laboratoryreport.model.LaboratoryReportTemplateDtoFactory;
import de.gematik.demis.pdfgen.test.helper.HtmlContentMatcher;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LaboratoryReportHtmlTemplateParserIntegrationTest {

  private static final String LABORATORY_REPORT_TEMPLATE =
      "receipt/laboratoryReport/laboratoryReportTemplate";

  @Autowired private LaboratoryReportTemplateDtoFactory dtoFactory;

  @Autowired private FhirParser fhirParser;

  @Autowired private HtmlTemplateParser htmlParser;

  @Test
  @Disabled(
      value =
          "Dieser Test funktioniert nicht. Die Prüfung eines Frameworks auf Funktionalität nicht unbedingt sinnvoll, daher ist fraglich, ob dieser Test noch notwending ist, da die ControllerIT bereits die Inhalte der PDF prüfen")
  void shouldCreateHtmlFromBedOccupancy() throws Exception {
    // given
    LaboratoryReportTemplateDto laboratoryReportTemplateDto = createLaboratoryReportTemplateDto();

    // when
    String html = htmlParser.process(laboratoryReportTemplateDto, LABORATORY_REPORT_TEMPLATE);

    // then
    HtmlContentMatcher matcher =
        new HtmlContentMatcher(html)
            .addRow("Meldevorgangs-ID", "a5e00874-bb26-45ac-8eea-0bde76456703")
            .addRow("Zeitpunkt des Eingangs", "04.03.2021 20:16")
            .addRow("Meldungsidentifier", "e8d8cc43-32c2-4f93-8eaf-b2f3e6deb2a9")
            .addRow("Meldungsstatus", "Final")
            .addRow("Meldungserstellung/-änderung", "04.03.2021 20:16")
            .addRow("Meldungsverweis \\(Primärlabor\\)", "appends ABC123")
            .addSectionTitle("Meldende Person")
            .addRow("Name", "Dr Adam Careful")
            .addRow("Adresse", "534 Erewhon St, 3999 PleasantVille")
            .addRow("Kontakt", "Telefon: 030 1234 \\(Dienstlich\\)")
            .addSectionTitle("Meldende Einrichtung")
            .addRow("Name", "Primärlabor \\(Erregerdiagnostische Untersuchungsstelle\\)")
            .addRow("Identifier", "BSNR: 98765430\nDEMIS-Id: 13589")
            .addRow("Adresse", "Dingsweg 321, 13055 Berlin, Deutschland")
            .addRow("Kontakt", "Telefon: 0309876543210 \\(Dienstlich\\)")
            .addSectionTitle("Betroffene Person")
            .addRow("Name", "Maxime Mustermann")
            .addRow("Geschlecht", "Weiblich")
            .addRow("Geburtsdatum", "11.02.1950")
            .addRow("Adresse \\(Hauptwohnsitz\\)", "Teststrasse 123, 13055 Berlin, Deutschland")
            .addRow(
                "Adresse \\(Derzeitiger Aufenthaltsort\\)",
                "Friedrichstraße 987, 66666 Berlin, Deutschland")
            .addRow("Kontakt", "Telefon: 030 555 \\(Privat\\)")
            .addSectionTitle("Einsendende Person")
            .addRow("Name", "Dr. Dr. Otto Muster")
            .addRow("Adresse", "Strasse 123, 33445 Stuttgart")
            .addRow("Kontakt", "Fax: 030 1111 \\(Dienstlich\\)")
            .addSectionTitle("Laborbericht")
            .addRow("Erstellungs-/Bearbeitungszeitpunkt", "04.03.2021 20:15")
            .addRow("Berichtsstatus", "Final")
            .addRow(
                "Meldetatbestand",
                "Severe-Acute-Respiratory-Syndrome-Coronavirus-2 \\(SARS-CoV-2\\)")
            .addRow("Ergebnis \\(Zusammenfassung\\)", "Meldepflichtiger Erreger nachgewiesen")
            .addRow("Erläuterung", "Ich bin die textuelle Conclusion ...")
            .addRow("Auftragsnummer \\(E2E-Referenz\\)", "2021-000672922")
            .addSectionTitle(
                "Test - SARS-CoV-2 \\(COVID-19\\) RNA \\[Presence\\] in Serum or Plasma by NAA with probe detection")
            .addRow("Teststatus", "Final")
            .addRow("Ergebniswert", "Detected")
            .addRow("Befund", "Positiv")
            .addRow("Nachweismethode", "Nucleic acid assay \\(procedure\\)")
            .addRow("Erläuterung \\(Test\\)", "Nette Zusatzinformation …")
            .addRow("Probeneingang", "04.03.2021 15:40")
            .addRow("Probenentnahme", "04.03.2021 09:50")
            .addRow("Probenmaterialstatus", "Verfügbar")
            .addRow("Probenmaterial", "Upper respiratory swab sample \\(specimen\\)")
            .addRow("Erläuterung \\(Probe\\)", "Ich bin eine interessante Zusatzinformation ...")
            .addRow("Transaktions-Id \\(DESH\\)", "IMS-12345-CVDP-00001");
    matcher.assertMatches();
  }

  private LaboratoryReportTemplateDto createLaboratoryReportTemplateDto() throws Exception {
    Bundle bundle = (Bundle) fhirParser.parseFromJson(LABORATORY_REPORT_BUNDLE_DV2_JSON);
    return dtoFactory.create(bundle, true);
  }
}
