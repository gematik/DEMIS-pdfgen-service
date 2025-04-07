package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.springframework.stereotype.Service;

/** Groups questionnaire response items by their link IDs. */
@Service
@Slf4j
class GroupByLinkId
    implements UnaryOperator<List<QuestionnaireResponse.QuestionnaireResponseItemComponent>> {

  private static List<QuestionnaireResponse.QuestionnaireResponseItemComponent> build(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items,
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> groupItems) {
    List<QuestionnaireResponse.QuestionnaireResponseItemComponent> groupedItems = new ArrayList<>();
    var workbench = indexGroupItems(groupItems);
    Set<String> groupLinkIds = new HashSet<>(workbench.keySet());
    for (var item : items) {
      addItem(item, groupLinkIds, workbench, groupedItems);
    }
    return groupedItems;
  }

  private static void addItem(
      QuestionnaireResponse.QuestionnaireResponseItemComponent item,
      Set<String> groupLinkIds,
      Map<String, QuestionnaireResponse.QuestionnaireResponseItemComponent> groupItems,
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    String linkId = item.getLinkId();
    if (groupLinkIds.contains(linkId)) {
      addGroupItem(linkId, groupItems, items);
    } else {
      items.add(item);
    }
  }

  private static void addGroupItem(
      String linkId,
      Map<String, QuestionnaireResponse.QuestionnaireResponseItemComponent> groupItems,
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    var groupItem = groupItems.remove(linkId);
    if (groupItem != null) {
      items.add(groupItem);
    }
  }

  /**
   * Creates a group item index. This is a mutable map of group items indexed by link ID.
   *
   * @param groupItems group items
   * @return group item index
   */
  private static Map<String, QuestionnaireResponse.QuestionnaireResponseItemComponent>
      indexGroupItems(List<QuestionnaireResponse.QuestionnaireResponseItemComponent> groupItems) {
    return new HashMap<>(
        groupItems.stream()
            .collect(
                Collectors.toMap(
                    QuestionnaireResponse.QuestionnaireResponseItemComponent::getLinkId,
                    Function.identity())));
  }

  private static Set<String> getGroupLinkIds(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    Set<String> linkIds = new HashSet<>();
    return items.stream()
        .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getLinkId)
        .filter(linkId -> !linkIds.add(linkId))
        .collect(Collectors.toSet());
  }

  private static List<QuestionnaireResponse.QuestionnaireResponseItemComponent> createGroupItems(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items,
      Set<String> groupLinkIds) {
    return groupLinkIds.stream().map(linkId -> createGroupItem(items, linkId)).toList();
  }

  private static QuestionnaireResponse.QuestionnaireResponseItemComponent createGroupItem(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items, String linkId) {
    var groupItems = items.stream().filter(item -> item.getLinkId().equals(linkId)).toList();
    var answers =
        groupItems.stream()
            .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getAnswer)
            .flatMap(List::stream)
            .toList();
    var subitems =
        groupItems.stream()
            .map(QuestionnaireResponse.QuestionnaireResponseItemComponent::getItem)
            .flatMap(List::stream)
            .toList();
    return new QuestionnaireResponse.QuestionnaireResponseItemComponent()
        .setLinkId(linkId)
        .setAnswer(answers)
        .setItem(subitems);
  }

  @Override
  public List<QuestionnaireResponse.QuestionnaireResponseItemComponent> apply(
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent> items) {
    Set<String> groupLinkIds = getGroupLinkIds(items);
    if (groupLinkIds.isEmpty()) {
      return items;
    }
    if (log.isDebugEnabled()) {
      log.debug(
          "Disease questionnaire response grouping items. LinkIds: {}",
          groupLinkIds.stream().sorted().collect(Collectors.joining(", ", "[", "]")));
    }
    var groupItems = createGroupItems(items, groupLinkIds);
    return build(items, groupItems);
  }
}
