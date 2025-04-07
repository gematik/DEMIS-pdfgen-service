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
 *
 * *******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 * #L%
 */

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransmittingSite {
  @JacksonXmlProperty(isAttribute = true, localName = "Name")
  private String name;

  @JacksonXmlProperty(isAttribute = true, localName = "Code")
  private String code;

  @JacksonXmlProperty(isAttribute = true, localName = "Department")
  private String department;

  @JacksonXmlProperty(isAttribute = true, localName = "Street")
  private String street;

  @JacksonXmlProperty(isAttribute = true, localName = "Postalcode")
  private String postalCode;

  @JacksonXmlProperty(isAttribute = true, localName = "Place")
  private String place;

  @JacksonXmlProperty(isAttribute = true, localName = "Phone")
  private String phone;

  @JacksonXmlProperty(isAttribute = true, localName = "Fax")
  private String fax;

  @JacksonXmlProperty(isAttribute = true, localName = "Email")
  private String email;

  @JacksonXmlProperty(localName = "SearchText")
  private String searchText;
}
