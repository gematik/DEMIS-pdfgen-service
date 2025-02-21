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

final class InfectionEnvironmentSettingCodes implements Feature {

  private final CodeSystemFeature settings =
      new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/infectionEnvironmentSetting");

  @Override
  public void run() {
    addHealthOrganization();
    this.settings.run();
  }

  private void addHealthOrganization() {
    settings.add(
        "3",
        """
    {
      "code": "3",
      "display": "Gesundheitseinrichtung",
      "concept": [
        {
          "code": "120",
          "display": "medizinische Einrichtung",
          "concept": [
            {
              "code": "200",
              "display": "Patient/-in"
            },
            {
              "code": "204",
              "display": "Besucher/-in"
            },
            {
              "code": "201",
              "display": "medizinisches Personal"
            },
            {
              "code": "202",
              "display": "Pflegepersonal"
            },
            {
              "code": "203",
              "display": "sonstiges Personal"
            }
          ]
        },
        {
          "code": "121",
          "display": "Pflegeeinrichtung",
          "concept": [
            {
              "code": "220",
              "display": "Betreute/-r"
            },
            {
              "code": "223",
              "display": "Besucher/-in"
            },
            {
              "code": "221",
              "display": "Pflegepersonal"
            },
            {
              "code": "222",
              "display": "sonstiges Personal"
            }
          ]
        },
        {
          "code": "122",
          "display": "ambulanter Pflegedienst",
          "concept": [
            {
              "code": "143",
              "display": "Betreute/-r"
            },
            {
              "code": "240",
              "display": "Pflegepersonal"
            },
            {
              "code": "241",
              "display": "sonstiges Personal"
            }
          ]
        },
        {
          "code": "124",
          "display": "Reha-Einrichtung",
          "concept": [
            {
              "code": "362",
              "display": "Patient/-in"
            },
            {
              "code": "363",
              "display": "Besucher/-in"
            },
            {
              "code": "364",
              "display": "medizinisches Personal"
            },
            {
              "code": "365",
              "display": "Pflegepersonal"
            },
            {
              "code": "366",
              "display": "sonstiges Personal"
            }
          ]
        },
        {
          "code": "123",
          "display": "Labor"
        }
      ]
    }""");
  }
}
