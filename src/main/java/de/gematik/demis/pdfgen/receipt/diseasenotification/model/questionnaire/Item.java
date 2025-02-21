package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire;

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

import de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.resources.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

/** Questionnaire response item or a resource. */
@Getter
public final class Item {

  private final String text;
  private final List<Answer> answers;
  private final List<Item> subitems;
  private final Resource resource;

  public Item(String text, Answer answer) {
    this(text, List.of(answer), null, null);
  }

  public Item(String text, List<Answer> answers) {
    this(text, answers, null, null);
  }

  public Item(String text, List<Answer> answers, List<Item> subitems) {
    this(text, answers, subitems, null);
  }

  /**
   * Create a resource
   *
   * @param resource resource
   */
  public Item(Resource resource) {
    this(null, null, null, resource);
  }

  /**
   * Create a labeled resource
   *
   * @param text label text
   * @param resource resource
   */
  public Item(String text, Resource resource) {
    this(text, null, null, resource);
  }

  private Item(String text, List<Answer> answers, List<Item> subitems, Resource resource) {
    this.text = text;
    this.answers = Objects.requireNonNullElseGet(answers, Collections::emptyList);
    this.subitems = Objects.requireNonNullElseGet(subitems, Collections::emptyList);
    this.resource = resource;
  }

  /**
   * Check if this item contains any subitems, either directly or attached to this item's answers.
   *
   * @return <code>true</code> if there are subitems, <code>false</code> if not
   */
  public boolean hasSubitems() {
    return !this.subitems.isEmpty()
        || (!this.answers.isEmpty()
            && this.answers.stream().map(Answer::subitems).anyMatch(si -> !si.isEmpty()));
  }

  /**
   * Check if this item is a top-level item that contains a single answer with subitems building a
   * block of uni-typed resources like hospitalizations or immunizations.
   *
   * @return <code>true</code> if root of a resource block, <code>false</code> if not
   */
  public boolean isResourceBlock() {
    if (this.answers.size() == 1) {
      final List<Item> answerSubitems = this.answers.getFirst().subitems();
      if (!answerSubitems.isEmpty() && answerSubitems.stream().allMatch(Item::isResource)) {
        final Resource expected = answerSubitems.getFirst().getResource();
        return answerSubitems.stream().map(Item::getResource).allMatch(expected::isSameType);
      }
    }
    return false;
  }

  /**
   * Check if this item contains and represents a resource
   *
   * @return <code>true</code> if this is actually a resource, <code>false</code> if this is a
   *     standard questionnaire response item
   */
  public boolean isResource() {
    return this.resource != null;
  }

  @Override
  public String toString() {
    return this.text;
  }
}
