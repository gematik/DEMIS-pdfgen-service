package de.gematik.demis.pdfgen.pdf;

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

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Changing resource URLs to enable Flying Sourcer to use HTTP to grab them. */
@RequiredArgsConstructor
@Slf4j
final class HttpResourcesHtml implements Supplier<String> {

  private static final String URL_PREFIX = "http://localhost:";
  private static final String MAIN_CSS = "/styles/main.css";
  private static final String DEMIS_LOGO = "/images/demis_logo.jpg";

  private final String html;
  private final int port;

  @Override
  public String get() {
    final String host = URL_PREFIX + port;
    final String mainCss = host + MAIN_CSS;
    final String demisLogo = host + DEMIS_LOGO;
    log.debug("Changing HTML resource links. MainCss: {} DemisLogo: {}", mainCss, demisLogo);
    return this.html.replaceAll(MAIN_CSS, mainCss).replaceAll(DEMIS_LOGO, demisLogo);
  }
}
