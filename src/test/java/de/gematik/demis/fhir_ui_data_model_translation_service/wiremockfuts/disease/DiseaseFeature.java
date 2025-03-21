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
import java.util.Collection;

/** DEMIS-feature: disease (Erkrankungsmeldung) */
public class DiseaseFeature extends FeatureCollection {

  @Override
  protected void add(Collection<Feature> features) {
    features.add(new CvddCodes());
    features.add(new InfectionEnvironmentSettingCodes());
    features.add(new QuestionnaireTranslations());
    features.add(new SnomedCodes());
    features.add(new TranslationEvidenceCodes());
    features.add(new VaccineCodes());
    features.add(new ConditionCodes());
  }

  @Override
  public String toString() {
    return "disease";
  }
}
