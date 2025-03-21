package de.gematik.demis.pdfgen.receipt.common.model.section;

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

import static de.gematik.demis.pdfgen.receipt.common.model.enums.NotificationStatusEnum.FINAL;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.NotificationStatusEnum;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    List<String> identifiers = Arrays.asList(null, "", "  ", "id1");
    List<NotificationStatusEnum> statusList = Arrays.asList(null, FINAL);
    List<DateTimeHolder> dates = Arrays.asList(null, DateTimeHolder.now());
    List<List<String>> relationLists =
        Arrays.asList(null, emptyList(), List.of("rel1", "rel2", "rel3"));

    identifiers.forEach(
        identifier -> {
          statusList.forEach(
              status -> {
                dates.forEach(
                    date -> {
                      relationLists.forEach(
                          relations -> {
                            // when
                            Notification notification =
                                Notification.builder()
                                    .identifier(identifier)
                                    .status(status)
                                    .dateTime(date)
                                    .relations(relations)
                                    .build();

                            // then
                            assertThat(notification.getIdentifier()).isEqualTo(identifier);
                            assertThat(notification.getStatus()).isEqualTo(status);
                            assertThat(notification.getDateTime()).isEqualTo(date);
                            assertThat(notification.getRelations()).isEqualTo(relations);
                            assertThat(notification.getAllRelatesTo()).isNotNull();
                          });
                    });
              });
        });
  }

  @Test
  void getAllRelatesTo_shouldBuildRelationsAsMultilineString() {
    // given
    List<String> nullList = null;
    List<String> emptyList = emptyList();
    List<String> singleEntryList = List.of("rel1");
    List<String> fullList = List.of("rel1", "rel2", "rel3");

    Notification nullRelationNotification = Notification.builder().relations(nullList).build();
    Notification emptyListNotification = Notification.builder().relations(emptyList).build();
    Notification singleEntryListNotification =
        Notification.builder().relations(singleEntryList).build();
    Notification fullListNotification = Notification.builder().relations(fullList).build();

    String expectedSingleEntry = "rel1";
    String expectedFullList =
        """
                rel1
                rel2
                rel3""";

    assertThat(nullRelationNotification.getAllRelatesTo()).isEmpty();
    assertThat(emptyListNotification.getAllRelatesTo()).isEmpty();
    assertThat(singleEntryListNotification.getAllRelatesTo()).isEqualTo(expectedSingleEntry);
    assertThat(fullListNotification.getAllRelatesTo()).isEqualTo(expectedFullList);
  }
}
