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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import de.gematik.demis.pdfgen.receipt.common.model.section.Authentication;
import de.gematik.demis.pdfgen.receipt.common.model.section.Metadata;
import de.gematik.demis.pdfgen.receipt.common.model.section.Notification;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonDTO;
import de.gematik.demis.pdfgen.receipt.common.model.section.Notifier;
import de.gematik.demis.pdfgen.receipt.common.model.section.Recipient;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition.ConditionDTO;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.Questionnaire;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiseaseNotificationTemplateDto {

  private Metadata metadata;
  private Notification notification;
  private Recipient recipient;
  private Notifier notifier;
  private NotifiedPersonDTO notifiedPersonDTO;
  private ConditionDTO conditionDTO;
  private Questionnaire commonQuestionnaire;
  private Questionnaire specificQuestionnaire;
  private Authentication authentication;
  private Map<String, String> additionalConfig;
}
