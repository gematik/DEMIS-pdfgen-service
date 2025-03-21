package de.gematik.demis.pdfgen.receipt.laboratoryreport.model.labreport;

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

import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.List;
import org.junit.jupiter.api.Test;

class LabReportTest {

  @Test
  void hasContent_and_hasIssued_checkIfLabReportHasContent() {
    // given
    DateTimeHolder issued = DateTimeHolder.now();
    LabTest labTest = LabTest.builder().build();
    List<LabTest> labTests = List.of(labTest);

    // when
    LabReport nullIssuedAndNullTestLabReport =
        LabReport.builder().issued(null).labTests(null).build();
    LabReport nullIssuedAndEmptyTestLabReport =
        LabReport.builder().issued(null).labTests(emptyList()).build();
    LabReport nullIssuedAndNonEmptyTestLabReport =
        LabReport.builder().issued(null).labTests(labTests).build();

    LabReport issuedAndNullTestLabReport =
        LabReport.builder().issued(issued).labTests(null).build();
    LabReport issuedAndEmptyTestLabReport =
        LabReport.builder().issued(issued).labTests(emptyList()).build();
    LabReport issuedAndNonEmptyTestLabReport =
        LabReport.builder().issued(issued).labTests(labTests).build();

    // then
    assertThat(nullIssuedAndNullTestLabReport.hasIssued()).isFalse();
    assertThat(nullIssuedAndEmptyTestLabReport.hasIssued()).isFalse();
    assertThat(nullIssuedAndNonEmptyTestLabReport.hasIssued()).isFalse();
    assertThat(issuedAndNullTestLabReport.hasIssued()).isTrue();
    assertThat(issuedAndEmptyTestLabReport.hasIssued()).isTrue();
    assertThat(issuedAndNonEmptyTestLabReport.hasIssued()).isTrue();

    assertThat(nullIssuedAndNullTestLabReport.hasLabTest()).isFalse();
    assertThat(nullIssuedAndEmptyTestLabReport.hasLabTest()).isFalse();
    assertThat(nullIssuedAndNonEmptyTestLabReport.hasLabTest()).isTrue();
    assertThat(issuedAndNullTestLabReport.hasLabTest()).isFalse();
    assertThat(issuedAndEmptyTestLabReport.hasLabTest()).isFalse();
    assertThat(issuedAndNonEmptyTestLabReport.hasLabTest()).isTrue();

    assertThat(nullIssuedAndNullTestLabReport.hasContent()).isFalse();
    assertThat(nullIssuedAndEmptyTestLabReport.hasContent()).isFalse();
    assertThat(nullIssuedAndNonEmptyTestLabReport.hasContent()).isTrue();
    assertThat(issuedAndNullTestLabReport.hasContent()).isTrue();
    assertThat(issuedAndEmptyTestLabReport.hasContent()).isTrue();
    assertThat(issuedAndNonEmptyTestLabReport.hasContent()).isTrue();
  }
}
