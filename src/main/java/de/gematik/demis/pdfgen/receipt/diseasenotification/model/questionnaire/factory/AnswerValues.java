package de.gematik.demis.pdfgen.receipt.diseasenotification.model.questionnaire.factory;

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

import de.gematik.demis.pdfgen.translation.TranslationService;
import de.gematik.demis.pdfgen.utils.DateTimeHolder;
import de.gematik.demis.pdfgen.utils.MessageUtil;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Type;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnswerValues
    implements Function<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent, String> {

  private static final char BLANK = ' ';

  private final List<Printer> printers;

  public AnswerValues(TranslationService translationService) {
    this.printers =
        List.of(
            AnswerValues::printSimpleValues,
            AnswerValues::printPrimitiveValues,
            AnswerValues::printTemporalValues,
            a -> printComplexValues(a, translationService),
            AnswerValues::printFallbackText);
  }

  private static String printPrimitiveValues(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (answer.hasValueStringType()) {
      return answer.getValueStringType().getValue();
    }
    if (answer.hasValueBooleanType()) {
      if (answer.getValueBooleanType().booleanValue()) {
        return MessageUtil.get("enum.boolean.true");
      }
      return MessageUtil.get("enum.boolean.false");
    }
    if (answer.hasValueIntegerType()) {
      return answer.getValueIntegerType().asStringValue();
    }
    if (answer.hasValueDecimalType()) {
      return answer.getValueDecimalType().asStringValue();
    }
    return null;
  }

  private static String printTemporalValues(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (answer.hasValueDateTimeType()) {
      return new DateTimeHolder(answer.getValueDateTimeType()).toString();
    }
    if (answer.hasValueDateType()) {
      DateType date = answer.getValueDateType();
      DateTimeType dateTime = new DateTimeType(date.getValue(), date.getPrecision());
      return new DateTimeHolder(dateTime).toString();
    }
    if (answer.hasValueTimeType()) {
      return answer.getValueTimeType().asStringValue();
    }
    return null;
  }

  private static String printFallbackText(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    Type value = answer.getValue();
    log.warn(
        "Failed to print questionnaire response item answer value. Unsupported type! Value: {}",
        value);
    return value.primitiveValue();
  }

  private static String printComplexValues(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer,
      TranslationService translationService) {
    if (answer.hasValueCoding()) {
      return translationService.resolveCodeableConceptValues(answer.getValueCoding());
    }
    if (answer.hasValueQuantity()) {
      Quantity quantity = answer.getValueQuantity();
      return print(quantity);
    }
    return null;
  }

  private static String print(Quantity quantity) {
    StringBuilder text = new StringBuilder();
    if (quantity.hasComparator()) {
      text.append(quantity.getComparator().getDisplay());
    }
    if (quantity.hasValue()) {
      if (!text.isEmpty()) {
        text.append(BLANK);
      }
      text.append(quantity.getValue());
    }
    if (quantity.hasUnit()) {
      if (!text.isEmpty()) {
        text.append(BLANK);
      }
      text.append(quantity.getUnit());
    }
    return text.toString();
  }

  private static String printSimpleValues(
      QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (answer.hasValueUriType()) {
      return answer.getValueUriType().asStringValue();
    }
    if (answer.hasValueReference()) {
      return answer.getValueReference().getReference();
    }
    return null;
  }

  /**
   * Print value as text
   *
   * @param answer item answer
   * @return text or <code>null</code>
   */
  @Override
  public String apply(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer) {
    if (!answer.hasValue()) {
      return null;
    }
    return this.printers.stream()
        .map(p -> p.apply(answer))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private interface Printer
      extends Function<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent, String> {
    /**
     * Prints value if supported
     *
     * @param answer item answer
     * @return text or <code>null</code> if not supported
     */
    @Override
    String apply(QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent answer);
  }
}
