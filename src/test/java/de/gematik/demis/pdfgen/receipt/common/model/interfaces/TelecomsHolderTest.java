package de.gematik.demis.pdfgen.receipt.common.model.interfaces;

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

import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.EMAIL;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.FAX;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.PHONE;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum.URL;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.HOME;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.MOBILE;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.OLD;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.TEMP;
import static de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum.WORK;
import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomTypeEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.TelecomUseEnum;
import de.gematik.demis.pdfgen.receipt.common.model.subsection.Telecom;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class TelecomsHolderTest {

  @Test
  void getTelecomsAsMultipleLines_shouldHandleNullOrEmptyTelecomsGracefully() {
    // given
    TestTelecomsHolder holderNull = new TestTelecomsHolder();
    TestTelecomsHolder holderEmpty = new TestTelecomsHolder();
    holderEmpty.setTelecoms(new ArrayList<>());

    // then
    assertThat(holderNull.getTelecomsAsMultipleLines()).isEmpty();
    assertThat(holderEmpty.getTelecomsAsMultipleLines()).isEmpty();
  }

  @Test
  void getTelecomsAsMultipleLines_shouldCreateStringForHolderWithJustOneTelecom() {
    // given
    Telecom email = createTelecom("email1", EMAIL, HOME);
    Telecom url = createTelecom("url1", URL, TEMP);
    Telecom fax = createTelecom("fax1", FAX, WORK);
    Telecom phone = createTelecom("phone1", PHONE, MOBILE);

    TestTelecomsHolder emailHolder = new TestTelecomsHolder(List.of(email));
    TestTelecomsHolder urlHolder = new TestTelecomsHolder(List.of(url));
    TestTelecomsHolder faxHolder = new TestTelecomsHolder(List.of(fax));
    TestTelecomsHolder phoneHolder = new TestTelecomsHolder(List.of(phone));

    // then
    assertThat(emailHolder.getTelecomsAsMultipleLines()).isEqualTo(email.asSingleLine());
    assertThat(urlHolder.getTelecomsAsMultipleLines()).isEqualTo(url.asSingleLine());
    assertThat(faxHolder.getTelecomsAsMultipleLines()).isEqualTo(fax.asSingleLine());
    assertThat(phoneHolder.getTelecomsAsMultipleLines()).isEqualTo(phone.asSingleLine());
  }

  @Test
  void getTelecomsAsMultipleLines_shouldCreateStringForTelecomsWithNullOrEmptyValues() {
    // given
    Telecom nullUse = createTelecom("phone1", PHONE, null);
    Telecom nullType = createTelecom("phone2", null, WORK);
    Telecom nullTypeAndUse = createTelecom("phone3", null, null);
    Telecom nullValue = createTelecom(null, PHONE, WORK);
    Telecom emptyValue = createTelecom("", PHONE, WORK);
    Telecom blankValue = createTelecom("   ", PHONE, WORK);

    TestTelecomsHolder nullUseHolder = new TestTelecomsHolder(List.of(nullUse));
    TestTelecomsHolder nullTypeHolder = new TestTelecomsHolder(List.of(nullType));
    TestTelecomsHolder nullTypeAndUseHolder = new TestTelecomsHolder(List.of(nullTypeAndUse));
    TestTelecomsHolder nullValueHolder = new TestTelecomsHolder(List.of(nullValue));
    TestTelecomsHolder emptyValueHolder = new TestTelecomsHolder(List.of(emptyValue));
    TestTelecomsHolder blankValueHolder = new TestTelecomsHolder(List.of(blankValue));

    // then
    assertThat(nullUseHolder.getTelecomsAsMultipleLines()).isEqualTo(nullUse.asSingleLine());
    assertThat(nullTypeHolder.getTelecomsAsMultipleLines()).isEqualTo(nullType.asSingleLine());
    assertThat(nullTypeAndUseHolder.getTelecomsAsMultipleLines())
        .isEqualTo(nullTypeAndUse.asSingleLine());
    assertThat(nullValueHolder.getTelecomsAsMultipleLines()).isEqualTo(nullValue.asSingleLine());
    assertThat(emptyValueHolder.getTelecomsAsMultipleLines()).isEqualTo(emptyValue.asSingleLine());
    assertThat(blankValueHolder.getTelecomsAsMultipleLines()).isEqualTo(blankValue.asSingleLine());
  }

  @Test
  void getTelecomsAsMultipleLines_shouldSortTelecomsAndCreateMultilineString() {
    // given
    Telecom email1 = createTelecom("email1", EMAIL, HOME);
    Telecom url1 = createTelecom("url1", URL, TEMP);
    Telecom fax1 = createTelecom("fax1", FAX, WORK);
    Telecom email2 = createTelecom("email2", EMAIL, WORK);
    Telecom phone1 = createTelecom("phone1", PHONE, MOBILE);
    Telecom fax2 = createTelecom("fax2", FAX, OLD);
    Telecom url2 = createTelecom("url2", URL, WORK);
    Telecom phone2 = createTelecom("phone2", PHONE, WORK);
    List<Telecom> telecoms = List.of(email1, url1, fax1, email2, phone1, fax2, url2, phone2);

    TestTelecomsHolder holder = new TestTelecomsHolder(telecoms);
    String expected =
        phone1.asSingleLine()
            + "\n"
            + phone2.asSingleLine()
            + "\n"
            + fax1.asSingleLine()
            + "\n"
            + fax2.asSingleLine()
            + "\n"
            + email1.asSingleLine()
            + "\n"
            + email2.asSingleLine()
            + "\n"
            + url1.asSingleLine()
            + "\n"
            + url2.asSingleLine();

    // then
    assertThat(holder.getTelecomsAsMultipleLines()).isEqualTo(expected);
  }

  @Test
  void getTelecomsAsMultipleLines_sortingWorksEvenWhenFieldsAreNull() {
    // given
    Telecom empty = Telecom.builder().build();
    TestTelecomsHolder holder = new TestTelecomsHolder(List.of(empty, empty));

    // then
    assertThat(holder.getTelecomsAsMultipleLines()).isEmpty();
  }

  private Telecom createTelecom(String value, TelecomTypeEnum type, TelecomUseEnum use) {
    return Telecom.builder().value(value).system(type).use(use).build();
  }

  @NoArgsConstructor
  @AllArgsConstructor
  private static class TestTelecomsHolder implements TelecomsHolder {
    @Getter @Setter private List<Telecom> telecoms;
  }
}
