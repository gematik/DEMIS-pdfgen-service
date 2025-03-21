package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport.interfaces;

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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class NotesHolderTest {

  @Test
  void getNotesAsMultipleLines_shouldConvertNotesToMultipleLineString() {
    // given
    List<String> singleEntryNotes = List.of("note1");
    List<String> notes = List.of("note1", "note2", "note3");
    TestNotesHolder nullHolder = new TestNotesHolder();
    TestNotesHolder emptyHolder = new TestNotesHolder(emptyList());
    TestNotesHolder singleEntryHolder = new TestNotesHolder(singleEntryNotes);
    TestNotesHolder fullListHolder = new TestNotesHolder(notes);

    // then
    String expectedFullList =
        """
                note1
                note2
                note3""";

    assertThat(nullHolder.getNotesAsMultipleLines()).isEmpty();
    assertThat(emptyHolder.getNotesAsMultipleLines()).isEmpty();
    assertThat(singleEntryHolder.getNotesAsMultipleLines()).isEqualTo("note1");
    assertThat(fullListHolder.getNotesAsMultipleLines()).isEqualTo(expectedFullList);
  }

  @NoArgsConstructor
  @AllArgsConstructor
  private static class TestNotesHolder extends NotesHolder {
    @Getter @Setter private List<String> notes;
  }
}
