package de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.disease;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.Feature;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.codes.CodeSystemFeature;

final class SnomedCodes implements Feature {
  private final CodeSystemFeature snomed = new CodeSystemFeature("http://snomed.info/sct");

  @Override
  public void run() {
    addArds();
    addCough();
    addFever();
    addPneumonia();
    addSniffles();
    addSoreThroat();
    addDiabetes1();
    addLiver();
    addPregnancy();
    addSecondTrimester();
    this.snomed.run();
  }

  private void addSecondTrimester() {
    this.snomed.add(
        "255247007",
        """
        {
              "code": "255247007",
              "display": "2. Trimester"
            }""");
  }

  private void addPregnancy() {
    this.snomed.add(
        "77386006",
        """
        {
              "code": "77386006",
              "display": "Schwangerschaft"
            }""");
  }

  private void addLiver() {
    this.snomed.add(
        "235856003",
        """
        {
              "code": "235856003",
              "display": "Lebererkrankung"
            }""");
  }

  private void addDiabetes1() {
    this.snomed.add(
        "46635009",
        """
        {
              "code": "46635009",
              "display": "Diabetes Typ 1"
            }""");
  }

  private void addArds() {
    this.snomed.add(
        "67782005",
        """
    {
      "code": "67782005",
      "display": "Acute respiratory distress syndrome (disorder)",
      "designations": [
        {
          "language": "de",
          "value": "akutes schweres Atemnotsyndrom (ARDS)"
        }
      ]
    }""");
  }

  private void addSniffles() {
    this.snomed.add(
        "275280004",
        """
    {
      "code": "275280004",
      "display": "Sniffles (finding)",
      "designations": [
        {
          "language": "de",
          "value": "Schnupfen"
        }
      ]
    }""");
  }

  private void addPneumonia() {
    this.snomed.add(
        "233604007",
        """
    {
      "code": "233604007",
      "display": "Pneumonia (disorder)",
      "designations": [
        {
          "language": "de",
          "value": "Pneunomie (Lungenentzündung)"
        }
      ]
    }""");
  }

  private void addCough() {
    this.snomed.add(
        "49727002",
        """
    {
      "code": "49727002",
      "display": "Cough (finding)",
      "designations": [
        {
          "language": "de",
          "value": "Husten"
        }
      ]
    }""");
  }

  private void addSoreThroat() {
    this.snomed.add(
        "267102003",
        """
    {
      "code": "267102003",
      "display": "Sore throat symptom (finding)",
      "designations": [
        {
          "language": "de",
          "value": "Halsschmerzen/-entzündung"
        }
      ]
    }""");
  }

  private void addFever() {
    this.snomed.add(
        "386661006",
        """
    {
      "code": "386661006",
      "display": "Fieber"
    }""");
  }
}
