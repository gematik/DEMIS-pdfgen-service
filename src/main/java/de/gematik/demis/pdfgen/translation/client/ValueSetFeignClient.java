package de.gematik.demis.pdfgen.translation.client;

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

import de.gematik.demis.pdfgen.translation.model.CodeDisplay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "valueSetFeignClient",
    url = "${demis.network.fhir-ui-data-model-translation-address}")
public interface ValueSetFeignClient {

  /*
   * DEMIS-610
   * No Spring Cache annotation is used here, because requests on value sets are outdated.
   * Value set codes are not used in the DEMIS FHIR documents anymore.
   * We keep this feature for now for backward compatibility.
   */

  @GetMapping("/fhir-ui-data-model-translation/ValueSet")
  CodeDisplay getInfoForCodeFromValueSet(@RequestParam String system, @RequestParam String code);
}
