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

package de.gematik.demis.pdfgen.receipt.diseasenotification.model.commonquestions;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.jupiter.api.Test;

class CommonQuestionsTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    DateTimeHolder emptyDateTimeHolder = new DateTimeHolder((BaseDateTimeType) null);
    DateTimeHolder dateHolder = new DateTimeHolder(new Date());
    DateTimeHolder dateTimeHolder = new DateTimeHolder(new DateTimeType());

    List<String> isDeads = Arrays.asList(null, "", "  ", "isDead1");
    List<DateTimeHolder> deathDates =
        Arrays.asList(null, emptyDateTimeHolder, dateHolder, dateTimeHolder);
    List<String> hasMilitaryAffiliations = Arrays.asList(null, "", "  ", "hasMilitaryAffiliation1");
    List<String> wasLabSpecimenTakens = Arrays.asList(null, "", "  ", "wasLabSpecimenTaken1");
    List<Laboratory> laboratories = Arrays.asList(null, Laboratory.builder().build());
    List<String> wasHospitalizeds = Arrays.asList(null, "", "  ", "wasHospitalized1");
    List<List<Hospitalization>> hospitalizationsLists =
        Arrays.asList(null, emptyList(), singletonList(Hospitalization.builder().build()));
    List<String> wasInInfectProtectFacilities =
        Arrays.asList(null, "", "  ", "wasInInfectProtectFacility1");
    List<InfectProtectFacility> infectProtectFacilities =
        Arrays.asList(null, InfectProtectFacility.builder().build());
    List<String> hasExposurePlaces = Arrays.asList(null, "", "  ", "hasExposurePlace1");
    List<List<ExposurePlace>> exposurePlacesLists =
        Arrays.asList(null, emptyList(), singletonList(ExposurePlace.builder().build()));
    List<String> organDonations = Arrays.asList(null, "", "  ", "organDonation1");
    List<String> notes = Arrays.asList(null, "", "  ", "note1");

    isDeads.forEach(
        isDead -> {
          deathDates.forEach(
              deathDate -> {
                hasMilitaryAffiliations.forEach(
                    hasMilitaryAffiliation -> {
                      wasLabSpecimenTakens.forEach(
                          wasLabSpecimenTaken -> {
                            laboratories.forEach(
                                laboratory -> {
                                  wasHospitalizeds.forEach(
                                      wasHospitalized -> {
                                        hospitalizationsLists.forEach(
                                            hospitalizations -> {
                                              wasInInfectProtectFacilities.forEach(
                                                  wasInInfectProtectFacility -> {
                                                    infectProtectFacilities.forEach(
                                                        infectProtectFacility -> {
                                                          hasExposurePlaces.forEach(
                                                              hasExposurePlace -> {
                                                                exposurePlacesLists.forEach(
                                                                    exposurePlaces -> {
                                                                      organDonations.forEach(
                                                                          organDonation -> {
                                                                            notes.forEach(
                                                                                note -> {
                                                                                  // when
                                                                                  CommonQuestions
                                                                                      commonQuestions =
                                                                                          CommonQuestions
                                                                                              .builder()
                                                                                              .isDead(
                                                                                                  isDead)
                                                                                              .deathDate(
                                                                                                  deathDate)
                                                                                              .hasMilitaryAffiliation(
                                                                                                  hasMilitaryAffiliation)
                                                                                              .wasLabSpecimenTaken(
                                                                                                  wasLabSpecimenTaken)
                                                                                              .laboratory(
                                                                                                  laboratory)
                                                                                              .wasHospitalized(
                                                                                                  wasHospitalized)
                                                                                              .hospitalizations(
                                                                                                  hospitalizations)
                                                                                              .wasInInfectProtectFacility(
                                                                                                  wasInInfectProtectFacility)
                                                                                              .infectProtectFacility(
                                                                                                  infectProtectFacility)
                                                                                              .hasExposurePlace(
                                                                                                  hasExposurePlace)
                                                                                              .exposurePlaces(
                                                                                                  exposurePlaces)
                                                                                              .organDonation(
                                                                                                  organDonation)
                                                                                              .note(
                                                                                                  note)
                                                                                              .build();

                                                                                  // then
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getIsDead())
                                                                                      .isEqualTo(
                                                                                          isDead);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getDeathDate())
                                                                                      .isEqualTo(
                                                                                          deathDate);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getHasMilitaryAffiliation())
                                                                                      .isEqualTo(
                                                                                          hasMilitaryAffiliation);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getWasLabSpecimenTaken())
                                                                                      .isEqualTo(
                                                                                          wasLabSpecimenTaken);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getLaboratory())
                                                                                      .isEqualTo(
                                                                                          laboratory);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getWasHospitalized())
                                                                                      .isEqualTo(
                                                                                          wasHospitalized);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getHospitalizations())
                                                                                      .isEqualTo(
                                                                                          hospitalizations);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getWasInInfectProtectFacility())
                                                                                      .isEqualTo(
                                                                                          wasInInfectProtectFacility);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getInfectProtectFacility())
                                                                                      .isEqualTo(
                                                                                          infectProtectFacility);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getHasExposurePlace())
                                                                                      .isEqualTo(
                                                                                          hasExposurePlace);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getExposurePlaces())
                                                                                      .isEqualTo(
                                                                                          exposurePlaces);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getOrganDonation())
                                                                                      .isEqualTo(
                                                                                          organDonation);
                                                                                  assertThat(
                                                                                          commonQuestions
                                                                                              .getNote())
                                                                                      .isEqualTo(
                                                                                          note);
                                                                                });
                                                                          });
                                                                    });
                                                              });
                                                        });
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
