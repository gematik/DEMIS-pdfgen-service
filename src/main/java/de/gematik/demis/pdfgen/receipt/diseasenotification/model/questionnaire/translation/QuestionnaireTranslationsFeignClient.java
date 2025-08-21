package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.translation;

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

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    value = "diseaseQuestionnaireFeignClient",
    url = "${demis.network.fhir-ui-data-model-translation.address}")
interface QuestionnaireTranslationsFeignClient {
  @GetMapping(
      "${demis.network.fhir-ui-data-model-translation.context-path}disease/questionnaire/{code}/items")
  @Cacheable("futs-disease-questionnaires")
  QuestionnaireTranslationDto getQuestionnaireTranslations(@PathVariable String code);
}
