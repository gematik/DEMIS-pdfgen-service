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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.Feature;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.codes.CodeSystemFeature;

final class TranslationEvidenceCodes implements Feature {

  private final CodeSystemFeature translationEvidence =
      new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/translationEvidence");

  @Override
  public void run() {
    addFever();
    addSoreThroat();
    addCough();
    addPneumonia();
    addSniffles();
    addArds();
    this.translationEvidence.run();
  }

  private void addArds() {
    this.translationEvidence.add(
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
    this.translationEvidence.add(
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
    this.translationEvidence.add(
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
    this.translationEvidence.add(
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
    this.translationEvidence.add(
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
    this.translationEvidence.add(
        "386661006",
        """
                                                                {
                                                                    "code": "386661006",
                                                                    "display": "Fever (finding)",
                                                                    "designations": [
                                                                        {
                                                                            "language": "de",
                                                                            "value": "Fieber"
                                                                        }
                                                                    ]
                                                                }""");
  }
}
