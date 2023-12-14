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

package de.gematik.demis.pdfgen.test.helper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import de.gematik.demis.fhirparserlibrary.FhirParser;
import de.gematik.demis.pdfgen.fhir.extract.FhirQueries;
import de.gematik.demis.pdfgen.receipt.common.service.transmittingsite.TransmittingSite;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.*;

/** Helper Class for instantiating Fhir Resources for tests */
public final class FhirFactory {

  public static final String ORGANIZATION_RESOURCE_JSON =
      readResourceFile("bundles/organization.json");
  public static final String ORGANIZATION_RESOURCE_XML =
      readResourceFile("bundles/organization.xml");
  public static final String BED_OCCUPANCY_BUNDLE_JSON =
      readResourceFile("bundles/BedOccupancyBundle.json");
  public static final String BED_OCCUPANCY_BUNDLE_XML =
      readResourceFile("bundles/BedOccupancyBundle.xml");
  public static final String LABORATORY_REPORT_BUNDLE_DV1_JSON =
      readResourceFile("bundles/LaboratoryReportBundleDv1.json");
  public static final String LABORATORY_REPORT_BUNDLE_DV2_JSON =
      readResourceFile("bundles/LaboratoryReportBundleDv2.json");
  public static final String LABORATORY_PARTICIPANT_REPORT_BUNDLE_DV2_JSON =
      readResourceFile("bundles/LaboratoryReportBundleDv2_participantid.json");
  public static final String LABORATORY_REPORT_BUNDLE_DV2_PATHOGENS_JSON =
      readResourceFile("bundles/LaboratoryReportBundleDv2_pathogens.json");
  public static final String LABORATORY_REPORT_BUNDLE_DV2_XML =
      readResourceFile("bundles/LaboratoryReportBundleDv2.xml");
  public static final String DISEASE_NOTIFICATION_BUNDLE_JSON =
      readResourceFile("bundles/DiseaseNotificationBundle.json");
  public static final String DISEASE_NOTIFICATION_BUNDLE_XML =
      readResourceFile("bundles/DiseaseNotificationBundle.xml");
  public static final String LABORATORY_NOTIFICATION_WITH_MISSING_BIRTHDAY =
      readResourceFile("bundles/specialTestCases/LaboratoryNotificationWithMissingBirthday.json");
  public static final String EMPTY_BUNDLE_JSON = readResourceFile("bundles/EmptyBundle.json");

  private static final FhirParser FHIR_PARSER = new FhirParser(FhirContext.forR4());

  public static String convertJsonBundleToXml(String jsonBundle) {
    FhirContext ctx = FhirContext.forR4();
    IParser jsonParser = ctx.newJsonParser();
    IParser xmlParser = ctx.newXmlParser().setPrettyPrint(true);
    Bundle bundle = jsonParser.parseResource(Bundle.class, jsonBundle);
    return xmlParser.encodeResourceToString(bundle);
  }

  public static Bundle createBundle(String jsonBundle) {
    try {
      return (Bundle) FHIR_PARSER.parseFromJson(jsonBundle);
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not instantiate FHIR bundle!", e);
    }
  }

  public static Bundle createBedOccupancyBundle() {
    try {
      return (Bundle) FHIR_PARSER.parseFromJson(BED_OCCUPANCY_BUNDLE_JSON);
    } catch (Exception e) {
      throw new IllegalStateException("Could not instantiate bed occupancy bundle for tests", e);
    }
  }

  public static Bundle createLaboratoryReportBundle() {
    try {
      return (Bundle) FHIR_PARSER.parseFromJson(LABORATORY_REPORT_BUNDLE_DV2_JSON);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Could not instantiate laboratory report bundle for tests", e);
    }
  }

  public static Bundle createLaboratoryParticipantReportBundle() {
    try {
      return (Bundle) FHIR_PARSER.parseFromJson(LABORATORY_PARTICIPANT_REPORT_BUNDLE_DV2_JSON);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Could not instantiate laboratory participant report bundle for tests", e);
    }
  }

  public static Bundle createLaboratoryReportPathogensBundle() {
    try {
      return (Bundle) FHIR_PARSER.parseFromJson(LABORATORY_REPORT_BUNDLE_DV2_PATHOGENS_JSON);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Could not instantiate laboratory report bundle for tests", e);
    }
  }

  public static Bundle createDiseaseNotificationBundle() {
    try {
      return (Bundle) FHIR_PARSER.parseFromJson(DISEASE_NOTIFICATION_BUNDLE_JSON);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Could not instantiate laboratory report bundle for tests", e);
    }
  }

  public static Bundle createEmptyBundle() {
    try {
      return (Bundle) FHIR_PARSER.parseFromJson(EMPTY_BUNDLE_JSON);
    } catch (Exception e) {
      throw new IllegalStateException("Could not instantiate empty bundle for tests", e);
    }
  }

  public static org.hl7.fhir.r4.model.Organization createFhirOrganization() {
    try {
      return (Organization) FHIR_PARSER.parseFromJson(ORGANIZATION_RESOURCE_JSON);
    } catch (Exception e) {
      throw new IllegalStateException("Could not instantiate bed occupancy bundle for tests", e);
    }
  }

  public static QuestionnaireResponse createDiseaseQuestionsCommonQuestionnaireResponse() {
    return createQuestionnaireResponse(
        "https://demis.rki.de/fhir/Questionnaire/DiseaseQuestionsCommon");
  }

  public static QuestionnaireResponse createCovidQuestionnaireResponse() {
    return createQuestionnaireResponse(
        "https://demis.rki.de/fhir/Questionnaire/DiseaseQuestionsCVDD");
  }

  public static QuestionnaireResponse createQuestionnaireResponse(String questionnaireId) {
    FhirQueries fhirQueries = new FhirQueries();
    Bundle bundle = createDiseaseNotificationBundle();
    return fhirQueries
        .findResources(
            bundle, QuestionnaireResponse.class, i -> questionnaireId.equals(i.getQuestionnaire()))
        .stream()
        .findFirst()
        .orElse(null);
  }

  public static Address createFhirAddress() {
    return createFhirOrganization().getAddressFirstRep();
  }

  public static Observation createPathogenDetectionObservation() {
    Observation observation = new Observation();
    observation.setId("observationId");
    return observation;
  }

  public static TransmittingSite createTransmittingSite() {
    return TransmittingSite.builder()
        .name("Test Gesundheitsamt")
        .department("Test Department")
        .email("test@test.com")
        .street("Teststraße")
        .place("Teststadt")
        .phone("")
        .fax("")
        .build();
  }

  private static String readResourceFile(String path) {
    try (InputStream is = FhirFactory.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) {
        throw new IllegalStateException("Failed to load resource " + path);
      }
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load resource " + path, e);
    }
  }

  private FhirFactory() {
    // hidden
  }
}
