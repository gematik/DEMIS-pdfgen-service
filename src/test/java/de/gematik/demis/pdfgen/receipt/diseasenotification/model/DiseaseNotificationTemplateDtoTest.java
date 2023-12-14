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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.section.Metadata;
import de.gematik.demis.pdfgen.receipt.common.model.section.Notification;
import de.gematik.demis.pdfgen.receipt.common.model.section.NotifiedPersonDTO;
import de.gematik.demis.pdfgen.receipt.common.model.section.Notifier;
import de.gematik.demis.pdfgen.receipt.common.model.section.Recipient;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.commonquestions.CommonQuestions;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.condition.ConditionDTO;
import de.gematik.demis.pdfgen.receipt.diseasenotification.model.covid19questions.Covid19Questions;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class DiseaseNotificationTemplateDtoTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    List<Metadata> metadatas = Arrays.asList(null, Metadata.builder().build());
    List<Notification> notifications = Arrays.asList(null, Notification.builder().build());
    List<Recipient> recipients = Arrays.asList(null, Recipient.builder().build());
    List<Notifier> notifiers = Arrays.asList(null, Notifier.builder().build());
    List<NotifiedPersonDTO> notifiedPersonDTOs =
        Arrays.asList(null, NotifiedPersonDTO.builder().build());
    List<ConditionDTO> conditionDTOs = Arrays.asList(null, ConditionDTO.builder().build());
    List<CommonQuestions> commonQuestionsList =
        Arrays.asList(null, CommonQuestions.builder().build());
    List<Covid19Questions> covid19QuestionsList =
        Arrays.asList(null, Covid19Questions.builder().build());

    metadatas.forEach(
        metadata -> {
          notifications.forEach(
              notification -> {
                recipients.forEach(
                    recipient -> {
                      notifiers.forEach(
                          notifier -> {
                            notifiedPersonDTOs.forEach(
                                notifiedPerson -> {
                                  conditionDTOs.forEach(
                                      condition -> {
                                        commonQuestionsList.forEach(
                                            commonQuestions -> {
                                              covid19QuestionsList.forEach(
                                                  covid19Questions -> {
                                                    // when
                                                    DiseaseNotificationTemplateDto dto =
                                                        DiseaseNotificationTemplateDto.builder()
                                                            .metadata(metadata)
                                                            .notification(notification)
                                                            .recipient(recipient)
                                                            .notifier(notifier)
                                                            .notifiedPersonDTO(notifiedPerson)
                                                            .conditionDTO(condition)
                                                            .commonQuestions(commonQuestions)
                                                            .covid19Questions(covid19Questions)
                                                            .build();

                                                    // then
                                                    assertThat(dto.getMetadata())
                                                        .isEqualTo(metadata);
                                                    assertThat(dto.getNotification())
                                                        .isEqualTo(notification);
                                                    assertThat(dto.getRecipient())
                                                        .isEqualTo(recipient);
                                                    assertThat(dto.getNotifier())
                                                        .isEqualTo(notifier);
                                                    assertThat(dto.getNotifiedPersonDTO())
                                                        .isEqualTo(notifiedPerson);
                                                    assertThat(dto.getConditionDTO())
                                                        .isEqualTo(condition);
                                                    assertThat(dto.getCommonQuestions())
                                                        .isEqualTo(commonQuestions);
                                                    assertThat(dto.getCovid19Questions())
                                                        .isEqualTo(covid19Questions);
                                                  });
                                            });
                                      });
                                });
                          });
                    });
              });
        });
  }
}
