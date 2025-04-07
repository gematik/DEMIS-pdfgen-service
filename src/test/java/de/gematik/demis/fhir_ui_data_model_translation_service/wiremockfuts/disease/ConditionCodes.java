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

public class ConditionCodes implements Feature {
  private final CodeSystemFeature conditionStatus =
      new CodeSystemFeature("http://terminology.hl7.org/CodeSystem/condition-ver-status");

  @Override
  public void run() {
    addVerificationStatus();
    addClinicalStatus();
    this.conditionStatus.run();
  }

  private void addVerificationStatus() {
    this.conditionStatus.add(
        "unconfirmed",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "unconfirmed",
            "display": "Unbestätigt"
          }
""");
    this.conditionStatus.add(
        "provisional",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "provisional",
            "display": "Vorläufig"
          }
""");
    this.conditionStatus.add(
        "differential",
        """
{
                       "extension": [
                         {
                           "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                           "valueInteger": 100
                         }
                       ],
                       "code": "differential",
                       "display": "Abweichend"
                     }

""");
    this.conditionStatus.add(
        "confirmed",
        """
 {
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "confirmed",
            "display": "Bestätigt"
          }
""");
    this.conditionStatus.add(
        "refuted",
        """
{
                       "extension": [
                         {
                           "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                           "valueInteger": 100
                         }
                       ],
                       "code": "refuted",
                       "display": "Widerlegt"
                     }
""");
    this.conditionStatus.add(
        "entered-in-error",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "entered-in-error",
            "display": "Irrtümlich eingegeben"
          }
""");
  }

  private void addClinicalStatus() {
    this.conditionStatus.add(
        "active",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "active",
            "display": "Aktiv"
          }
""");
    this.conditionStatus.add(
        "active",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "relapse",
            "display": "Rückfall"
          }
""");
    this.conditionStatus.add(
        "active",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "remission",
            "display": "Remission"
          }
""");
    this.conditionStatus.add(
        "active",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "resolved",
            "display": "Gelöst"
          }
""");
    this.conditionStatus.add(
        "active",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "inactive",
            "display": "Inaktiv"
          }
""");
    this.conditionStatus.add(
        "active",
        """
{
            "extension": [
              {
                "url": "http://hl7.org/fhir/StructureDefinition/valueset-conceptOrder",
                "valueInteger": 100
              }
            ],
            "code": "recurrence",
            "display": "Wiederauftreten"
          }
""");
  }
}
