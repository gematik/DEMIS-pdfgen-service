package de.gematik.demis.pdfgen.receipt.common.service.transmittingsite;

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

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransmittingSiteService {
  private static final TransmittingSite TEST_TRANSMITTING_SITE =
      TransmittingSite.builder()
          .name("Test Gesundheitsamt")
          .department("Test Department")
          .email("test@test.com")
          .street("Teststraße")
          .place("Teststadt")
          .postalCode("abcde")
          .phone("")
          .fax("")
          .build();
  private final List<String> testHealthDepartmentCode;
  private final Map<String, TransmittingSite> transmittingSiteByCode;

  public TransmittingSiteService(
      @Value("${transmitting.site.file.location}") final String transmittingSiteFileLocation,
      @Value("${transmitting.site.test.ga.identifier}") String testHealthDepartmentCode) {
    this.transmittingSiteByCode = getTransmittingSiteByCode(transmittingSiteFileLocation);
    this.testHealthDepartmentCode =
        Arrays.stream(testHealthDepartmentCode.split(",")).map(String::trim).toList();
  }

  @Nullable
  public TransmittingSite getTransmittingSite(final String code) {
    if (code != null) {
      if (transmittingSiteByCode.containsKey(code)) {
        return transmittingSiteByCode.get(code);
      }
      if (testHealthDepartmentCode.contains(code)) {
        return TEST_TRANSMITTING_SITE;
      }
    }
    log.warn("Could not identify transmission site code {}", code);
    return null;
  }

  private Map<String, TransmittingSite> getTransmittingSiteByCode(
      final String transmittingSiteFileLocation) {
    try (final var fileInputStream =
        new ClassPathResource(transmittingSiteFileLocation).getInputStream()) {
      log.info("Loading {}", transmittingSiteFileLocation);
      final XmlMapper xmlMapper = new XmlMapper();
      // ignore unknown, new properties
      xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      final TransmittingSites transmittingSites =
          xmlMapper.readValue(fileInputStream, TransmittingSites.class);
      return transmittingSites.getItems().stream()
          .collect(toMap(TransmittingSite::getCode, i -> i));
    } catch (final IOException e) {
      throw new IllegalStateException("Could not load transmission site map", e);
    }
  }
}
