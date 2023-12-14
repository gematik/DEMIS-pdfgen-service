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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.covid19questions;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class Covid19QuestionsTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    Immunization immunization = Immunization.builder().build();
    InfectionEnvironmentSetting infectionSetting = InfectionEnvironmentSetting.builder().build();

    List<String> infectionSources = Arrays.asList(null, "", "  ", "infectionSource");
    List<String> hasInfectionEnvironmentSettings =
        Arrays.asList(null, "", "  ", "hasInfectionEnvironmentSetting");
    List<List<InfectionEnvironmentSetting>> infectionSettingsLists =
        Arrays.asList(
            null,
            emptyList(),
            singletonList(infectionSetting),
            Arrays.asList(null, infectionSetting));
    List<String> hasImmunizations = Arrays.asList(null, "", "  ", "hasImmunization");
    List<List<Immunization>> immunizationLists =
        Arrays.asList(
            null, emptyList(), singletonList(immunization), Arrays.asList(null, immunization));

    infectionSources.forEach(
        infectionSource -> {
          hasInfectionEnvironmentSettings.forEach(
              hasInfectionEnvironmentSetting -> {
                infectionSettingsLists.forEach(
                    infectionEnvironmentSettings -> {
                      hasImmunizations.forEach(
                          hasImmunization -> {
                            immunizationLists.forEach(
                                immunizations -> {

                                  // when
                                  Covid19Questions covid19Questions =
                                      Covid19Questions.builder()
                                          .infectionSource(infectionSource)
                                          .hasInfectionEnvironmentSetting(
                                              hasInfectionEnvironmentSetting)
                                          .infectionEnvironmentSettings(
                                              infectionEnvironmentSettings)
                                          .hasImmunization(hasImmunization)
                                          .immunizations(immunizations)
                                          .build();

                                  // then
                                  assertThat(covid19Questions.getInfectionSource())
                                      .isEqualTo(infectionSource);
                                  assertThat(covid19Questions.hasInfectionSource())
                                      .isEqualTo(isNotBlank(infectionSource));
                                  assertThat(covid19Questions.getHasInfectionEnvironmentSetting())
                                      .isEqualTo(hasInfectionEnvironmentSetting);
                                  assertThat(covid19Questions.getInfectionEnvironmentSettings())
                                      .isEqualTo(infectionEnvironmentSettings);
                                  assertThat(covid19Questions.getHasImmunization())
                                      .isEqualTo(hasImmunization);
                                  assertThat(covid19Questions.getImmunizations())
                                      .isEqualTo(immunizations);
                                });
                          });
                    });
              });
        });
  }
}
