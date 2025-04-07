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

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service of disease questionnaire translations */
@RequiredArgsConstructor
@Service
@Slf4j
public class QuestionnaireTranslations implements Function<String, QuestionnaireTranslation> {

  private final QuestionnaireTranslationsFeignClient client;

  /**
   * Get questionnaire translation
   *
   * @param code four-letter code of disease or code <code>common</code> for common information
   *     questionnaire
   * @return translation
   */
  @Override
  public QuestionnaireTranslation apply(String code) {
    String lowerCode = code.toLowerCase();
    log.debug("Loading disease questionnaire translation. Code: {}", lowerCode);
    try {
      var response = this.client.getQuestionnaireTranslations(lowerCode);
      if (response != null) {
        return new ReceivedQuestionnaireTranslation(response);
      }
      log.error("Failed to get translations of disease questionnaire! Code: {}", lowerCode);
    } catch (Exception e) {
      log.error("Error when retrieving disease questionnaire translation. Code: {}", lowerCode, e);
    }
    return new FallbackQuestionnaireTranslation(lowerCode);
  }
}
