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

package de.gematik.demis.pdfgen.receipt.common.model.subsection;

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
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum;
import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.ContactPoint;
import org.springframework.stereotype.Service;

@Service
public class TelecomFactory {

  public List<Telecom> createTelecoms(List<ContactPoint> contactPoints) {
    if (isEmpty(contactPoints)) {
      return emptyList();
    }

    List<Telecom> telecoms = new ArrayList<>();
    for (ContactPoint contactPoint : contactPoints) {
      TelecomTypeEnum system = TelecomTypeEnum.of(contactPoint.getSystem());
      TelecomUseEnum use = contactPoint.hasUse() ? TelecomUseEnum.of(contactPoint.getUse()) : null;
      String value = contactPoint.getValue();
      Telecom telecom = Telecom.builder().system(system).use(use).value(value).build();
      telecoms.add(telecom);
    }
    return telecoms;
  }

  public List<Telecom> createTelecoms(TransmittingSite site) {
    if (site == null) {
      return emptyList();
    }
    List<Telecom> telecoms = new ArrayList<>();
    if (isNotBlank(site.getPhone())) {
      Telecom telecom =
          Telecom.builder().system(TelecomTypeEnum.PHONE).value(site.getPhone()).build();
      telecoms.add(telecom);
    }
    if (isNotBlank(site.getFax())) {
      Telecom telecom = Telecom.builder().system(TelecomTypeEnum.FAX).value(site.getFax()).build();
      telecoms.add(telecom);
    }
    if (isNotBlank(site.getEmail())) {
      Telecom telecom =
          Telecom.builder().system(TelecomTypeEnum.EMAIL).value(site.getEmail()).build();
      telecoms.add(telecom);
    }
    return telecoms;
  }
}
