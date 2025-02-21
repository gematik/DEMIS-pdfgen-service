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
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.FeatureCollection;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.codes.CodeSystemFeature;
import java.util.Collection;

final class VaccineCodes extends FeatureCollection {

  private CodeSystemFeature communityRegister =
      new CodeSystemFeature("https://ec.europa.eu/health/documents/community-register/html");

  @Override
  protected void add(Collection<Feature> features) {
    addCommunityRegister(features);
    addVaccine(features);
  }

  private static void addVaccine(Collection<Feature> features) {
    features.add(
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/vaccine")
            .add(
                "otherVaccine",
                """
    {
      "code": "otherVaccine",
      "display": "Anderer Impfstoff"
    }"""));
  }

  private void addCommunityRegister(Collection<Feature> features) {
    createCommunityRegister();
    features.add(this.communityRegister);
  }

  private void createCommunityRegister() {
    this.communityRegister.add(
        "EU/1/20/1528",
        """
    {
      "code": "EU/1/20/1528",
      "display": "Comirnaty",
      "property": [
        {
          "code": "inactive",
          "valueBoolean": false
        }
      ]
    }""");
  }
}
