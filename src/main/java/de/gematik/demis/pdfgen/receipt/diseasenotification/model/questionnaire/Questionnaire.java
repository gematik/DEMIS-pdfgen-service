package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import java.util.List;
import java.util.Optional;

/** Generic questionnaire response */
public record Questionnaire(String title, List<Item> items, String status) {

  static final String LAB_SPECIMEN_TAKEN_LINK_ID = "labSpecimenTaken";

  /**
   * Find the laboratory diagnostic question item (labSpecimenTaken) if present.
   *
   * @return Optional containing the laboratory item if found
   */
  public Optional<Item> findLabSpecimenTakenItem(final boolean pdfSecondPageCrEnabled) {
    if (items == null) {
      return Optional.empty();
    }
    return items.stream()
        .filter(item -> Questionnaire.isLabSpecimenTakenItem(item, pdfSecondPageCrEnabled))
        .findFirst();
  }

  private static boolean isLabSpecimenTakenItem(Item item, final boolean pdfSecondPageCrEnabled) {
    if (item.getAnswers().isEmpty()) {
      return false;
    }
    if (pdfSecondPageCrEnabled) {
      return LAB_SPECIMEN_TAKEN_LINK_ID.equals(item.getLinkId());
    }
    List<Item> subitems = item.getAnswers().getFirst().subitems();
    return !subitems.isEmpty()
        && subitems.stream()
            .anyMatch(
                subitem ->
                    subitem.getResource() != null && subitem.getResource().getLaboratory() != null);
  }
}
