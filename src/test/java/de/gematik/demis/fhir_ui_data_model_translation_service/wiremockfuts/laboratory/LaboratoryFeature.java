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

package de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.laboratory;

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
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.codes.ValueSetFeature;
import java.util.LinkedList;
import java.util.List;

/** DEMIS-feature: laboratory (Erregernachweismeldung) */
public class LaboratoryFeature implements Feature {

  private static Feature createLaboratoryTestCvdpValueSet() {
    var testSarsCov2 = new ValueSetFeature("https://demis.rki.de/fhir/ValueSet/laboratoryTestCVDP");
    testSarsCov2.add(
        "94660-8",
        """
                                                        {
                                                             "code" : "94660-8",
                                                             "display" : "SARS-CoV-2 (COVID-19) RNA [Presence] in Serum or Plasma by NAA with probe detection"
                                                           }""");
    return testSarsCov2;
  }

  private static Feature createNotificationCategoryCodeSystem() {
    var categories =
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/notificationCategory");
    categories.add(
        "cvdp",
        """
                                                            {
                                                                   "code": "cvdp",
                                                                   "display": "Severe-Acute-Respiratory-Syndrome-Coronavirus-2 (SARS-CoV-2)",
                                                                   "designations": [
                                                                       {
                                                                           "language": "de",
                                                                           "value": "SARS-CoV-2"
                                                                       }
                                                                   ]
                                                            }""");
    categories.add(
        "invp",
        """
                                                                                                                {
                                                                                                                    "code": "invp",
                                                                                                                    "display": "Influenza",
                                                                                                                    "designations": []
                                                                                                                }""");
    return categories;
  }

  private static Feature createConclusionCodes() {
    var conclusionCodes =
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/conclusionCode");
    conclusionCodes.add(
        "pathogenDetected",
        """
                                                        {
                                                             "code" : "pathogenDetected",
                                                             "display" : "Meldepflichtiger Erreger nachgewiesen"
                                                           }""");
    conclusionCodes.add(
        "pathogenNotDetected",
        """
                                                        {
                                                             "code" : "pathogenNotDetected",
                                                             "display" : "Meldepflichtiger Erreger nicht nachgewiesen"
                                                           }""");
    return conclusionCodes;
  }

  private static Feature createLoincCodeSystem() {
    var loinc = new CodeSystemFeature("http://loinc.org");
    loinc.add(
        "LA11882-0",
        """
                                                            {
                                                                  "code": "LA11882-0",
                                                                  "display": "Detected"
                                                              }""");
    loinc.add(
        "94660-8",
        """
                                                            {
                                                                  "code": "94660-8",
                                                                  "display": "SARS-CoV-2 (COVID-19) RNA [Presence] in Serum or Plasma by NAA with probe detection"
                                                              }""");
    loinc.add(
        "99623-1",
        """
    {
      "code": "99623-1",
      "display": "Influenza virus A N1 RNA [Presence] in Specimen by NAA with probe detection"
    }""");
    return loinc;
  }

  private static Feature createSnomed() {
    var snomed = new CodeSystemFeature("http://snomed.info/sct");
    snomed.add(
        "s001",
        """
                                                                {
                                                                      "code": "s001",
                                                                      "display": "Upper respiratory swab sample (specimen)"
                                                                  }""");
    snomed.add(
        "s002",
        """
                                                            {
                                                                  "code": "s002",
                                                                  "display": "Nucleic acid assay (procedure)"
                                                              }""");
    snomed.add(
        "67122001",
        """
    {
      "code": "67122001",
      "display": "Acid fast stain method (procedure)"
    }""");
    snomed.add(
        "258607008",
        """
    {
      "code": "258607008",
      "display": "Bronchoalveolar lavage fluid specimen (specimen)"
    }""");
    return snomed;
  }

  @Override
  public void run() {
    final List<Feature> features = new LinkedList<>();
    features.add(createNotificationCategoryCodeSystem());
    features.add(createConclusionCodes());
    features.add(createLoincCodeSystem());
    features.add(createLaboratoryTestCvdpValueSet());
    features.add(createSnomed());
    features.forEach(Feature::run);
  }

  @Override
  public String toString() {
    return "laboratory";
  }
}
