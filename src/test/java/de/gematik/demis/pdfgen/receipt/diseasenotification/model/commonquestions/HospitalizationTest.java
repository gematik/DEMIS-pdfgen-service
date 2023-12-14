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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.subsection.OrganizationDTO;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.BaseDateTimeType;
import org.hl7.fhir.r4.model.DateTimeType;
import org.junit.jupiter.api.Test;

class HospitalizationTest {

  @Test
  void builderAndGetters_shouldHandleNullAndBlankGracefully() {
    // given
    DateTimeHolder emptyDateTimeHolder = new DateTimeHolder((BaseDateTimeType) null);
    DateTimeHolder dateHolder = new DateTimeHolder(new Date());
    DateTimeHolder dateTimeHolder = new DateTimeHolder(new DateTimeType());

    List<String> serviceTypes = Arrays.asList(null, "", "  ", "serviceType1");
    List<String> departments = Arrays.asList(null, "Intensivmedizin", "Intermediäre Station");
    List<OrganizationDTO> organizationDTOS = Arrays.asList(null, OrganizationDTO.builder().build());
    List<DateTimeHolder> dates =
        Arrays.asList(null, emptyDateTimeHolder, dateHolder, dateTimeHolder);
    List<String> notes = Arrays.asList(null, "", "  ", "note1");

    serviceTypes.forEach(
        serviceType -> {
          departments.forEach(
              department -> {
                organizationDTOS.forEach(
                    organization -> {
                      notes.forEach(
                          note -> {
                            dates.forEach(
                                begin -> {
                                  dates.forEach(
                                      end -> {
                                        // when
                                        Hospitalization hospitalization =
                                            Hospitalization.builder()
                                                .serviceType(serviceType)
                                                .department(department)
                                                .organizationDTO(organization)
                                                .note(note)
                                                .begin(begin)
                                                .end(end)
                                                .build();

                                        // then
                                        assertThat(hospitalization.getServiceType())
                                            .as("service type")
                                            .isEqualTo(serviceType);
                                        assertThat(hospitalization.getDepartment())
                                            .as("department")
                                            .isEqualTo(department);
                                        assertThat(hospitalization.getOrganizationDTO())
                                            .isEqualTo(organization);
                                        assertThat(hospitalization.getNote()).isEqualTo(note);
                                        assertThat(hospitalization.getBegin()).isEqualTo(begin);
                                        assertThat(hospitalization.getEnd()).isEqualTo(end);
                                      });
                                });
                          });
                    });
              });
        });
  }
}
