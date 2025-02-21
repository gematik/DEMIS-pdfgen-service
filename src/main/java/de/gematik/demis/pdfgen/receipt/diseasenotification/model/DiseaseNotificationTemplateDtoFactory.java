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

import de.gematik.demis.pdfgen.receipt.common.model.section.AuthenticationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.MetadataFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotificationFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifierFactory;
import de.gematik.demis.pdfgen.receipt.common.model.section.RecipientFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition.ConditionFactory;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Questionnaire;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.QuestionnaireService;
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
  private final QuestionnaireService questionnaireService;
  private final AuthenticationFactory authenticationFactory;

  @Nullable
  public DiseaseNotificationTemplateDto create(final Bundle bundle) {
    if (bundle == null) {
      return null;
    }
    final var builder =
        DiseaseNotificationTemplateDto.builder()
            .metadata(MetadataFactory.create(bundle))
            .notification(notificationFactory.create(bundle))
            .recipient(recipientFactory.create(bundle))
            .notifier(notifierFactory.create(bundle))
            .notifiedPersonDTO(notifiedPersonFactory.create(bundle))
            .conditionDTO(conditionFactory.create(bundle))
            .commonQuestionnaire(questionnaireService.createCommonQuestionnaire(bundle))
            .authentication(authenticationFactory.create(bundle));
    Questionnaire specific = questionnaireService.createSpecificQuestionnaire(bundle);
    if (specific != null) {
      builder.specificQuestionnaire(specific);
    }
    return builder.build();
  }
}
