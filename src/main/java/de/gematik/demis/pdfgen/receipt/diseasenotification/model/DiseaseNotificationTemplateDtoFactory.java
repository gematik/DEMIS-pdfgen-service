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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model;

import de.gematik.demis.pdfgen.receipt.common.model.section.MetadataFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotificationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifierFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.RecipientFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.commonquestions.CommonQuestionsFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition.ConditionFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.covid19questions.Covid19QuestionsExtractionSrv;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiseaseNotificationTemplateDtoFactory {

  private final NotificationFactory notificationFactory;
  private final RecipientFactory recipientFactory;
  private final NotifierFactory notifierFactory;
  private final NotifiedPersonFactory notifiedPersonFactory;
  private final ConditionFactory conditionFactory;
  private final CommonQuestionsFactory commonQuestionsFactory;
  private final Covid19QuestionsExtractionSrv covid19QuestionsFactory;

  @Nullable
  public DiseaseNotificationTemplateDto create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    return DiseaseNotificationTemplateDto.builder()
        .metadata(MetadataFactory.create(bundle))
        .notification(notificationFactory.create(bundle))
        .recipient(recipientFactory.create(bundle))
        .notifier(notifierFactory.create(bundle))
        .notifiedPersonDTO(notifiedPersonFactory.create(bundle))
        .conditionDTO(conditionFactory.create(bundle))
        .commonQuestions(commonQuestionsFactory.create(bundle))
        .covid19Questions(covid19QuestionsFactory.create(bundle))
        .build();
  }
}
