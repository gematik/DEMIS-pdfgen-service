package de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.codes;

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

import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.Feature;
import de.gematik.demis.fhir_ui_data_model_translation_service.wiremockfuts.FeatureCollection;
import java.util.Collection;

/** Default code systems and value sets */
public class DefaultTranslationFeature extends FeatureCollection {

  private static Feature createCountries() {
    var countries = new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/country");
    countries.add(
        "20422",
        """
                                                            {
                                                                "code": "20422",
                                                                "display": "Deutschland",
                                                                "designations": []
                                                            }""");
    return countries;
  }

  private static Feature createOrganizationTypesValueSet() {
    var types = new ValueSetFeature("https://demis.rki.de/fhir/ValueSet/organizationType");
    types.add(
        "hospital",
        """
            {
                "code": "hospital",
                "display": "Krankenhaus",
                "designations": []
            }""");
    types.add(
        "laboratory",
        """
                                                        {
                                                           "code" : "laboratory",
                                                           "display" : "Erregerdiagnostische Untersuchungsstelle",
                                                           "designations" : [ {
                                                             "language" : "en",
                                                             "value" : "Laboratory"
                                                           }, {
                                                             "language" : "de",
                                                             "value" : "Erregerdiagnostische Untersuchungsstelle"
                                                           } ]
                                                         }""");
    return types;
  }

  private static Feature createYesOrNoAnswer() {
    var yesOrNoAnswer = new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/yesOrNoAnswer");
    yesOrNoAnswer.add(
        "yes",
        """
                                                        {
                                                              "code": "yes",
                                                              "display": "Ja",
                                                              "designations": []
                                                          }""");
    yesOrNoAnswer.add(
        "no",
        """
                                                        {
                                                              "code": "no",
                                                              "display": "Nein",
                                                              "designations": []
                                                          }""");
    return yesOrNoAnswer;
  }

  private static Feature createMilitaryAffiliation() {
    var militaryAffiliation =
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/militaryAffiliation");
    militaryAffiliation.add(
        "memberOfBundeswehr",
        """
                                                        {
                                                            "code": "memberOfBundeswehr",
                                                            "display": "Soldat/BW-Angehöriger",
                                                            "designations": []
                                                        }""");
    return militaryAffiliation;
  }

  private static Feature createNullFlavor() {
    var nullFlavor = new CodeSystemFeature("http://terminology.hl7.org/CodeSystem/v3-NullFlavor");
    nullFlavor.add(
        "ASKU",
        """
                                                                                                                         {
                                                          "code" : "ASKU",
                                                          "display" : "asked but unknown",
                                                          "designations" : [ {
                                                            "language" : "de",
                                                            "value" : "nicht ermittelbar"
                                                          } ]
                                                        }""");
    return nullFlavor;
  }

  private static Feature createAddressUses() {
    var addressUses = new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/addressUse");
    addressUses.add(
        "current",
        """
                                                            {
                                                                 "code" : "current",
                                                                 "display" : "Derzeitiger Aufenthaltsort",
                                                                 "designations" : [ {
                                                                   "language" : "en",
                                                                   "value" : "Current Residence"
                                                                 } ]
                                                               }""");
    addressUses.add(
        "primary",
        """
                                                        {
                                                            "code" : "primary",
                                                            "display" : "Hauptwohnsitz",
                                                            "designations" : [ {
                                                              "language" : "en",
                                                              "value" : "Primary Residence"
                                                            } ]
                                                          }""");
    return addressUses;
  }

  private static Feature createOrganizationTypesCodeSystem() {
    var organizationTypes =
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/organizationType");
    organizationTypes.add(
        "laboratory",
        """
                                                        {
                                                            "code" : "laboratory",
                                                            "display" : "Erregerdiagnostische Untersuchungsstelle",
                                                            "designations" : [ {
                                                              "language": "de-DE",
                                                              "use": {
                                                                "system": "http://snomed.info/sct",
                                                                "code": "900000000000003001"
                                                              },
                                                              "value": "Erregerdiagnostische Untersuchungsstelle"
                                                            } ]
                                                          }""");
    organizationTypes.add(
        "hospital",
        """
        {
          "code": "hospital",
          "display": "Krankenhaus",
          "designations": [
            {
              "language": "en-US",
              "use": {
                "system": "http://snomed.info/sct",
                "code": "900000000000003001"
              },
              "value": "Hospital"
            },
            {
              "language": "de-DE",
              "use": {
                "system": "http://snomed.info/sct",
                "code": "900000000000003001"
              },
              "value": "Krankenhaus"
            }
          ]
        }""");
    organizationTypes.add(
        "othPrivatLab",
        """
        {
           "code" : "othPrivatLab",
           "display" : "othPrivatLab",
           "designations" : [ {
             "language" : "en",
             "value" : "othPrivatLab"
           }, {
             "language" : "de",
             "value" : "andere Untersuchungsstelle"
           } ]
         }""");
    return organizationTypes;
  }

  private static Feature createHospitalizationServiceTypeCodeSystem() {
    var hospitalizationServiceTypeCodeSystem =
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/hospitalizationServiceType");
    hospitalizationServiceTypeCodeSystem.add(
        "3600",
"""
    {
      "code": "3600",
      "display": "Intensivmedizin",
      "concept": [
        {
          "code": "3601",
          "display": "Schwerpunkt Innere Medizin"
        },
        {
          "code": "3603",
          "display": "Schwerpunkt Kardiologie"
        },
        {
          "code": "3610",
          "display": "Schwerpunkt Pädiatrie"
        },
        {
          "code": "3617",
          "display": "Schwerpunkt Neurochirurgie"
        },
        {
          "code": "3618",
          "display": "Schwerpunkt Chirurgie"
        },
        {
          "code": "3621",
          "display": "Schwerpunkt Herzchirurgie"
        },
        {
          "code": "3622",
          "display": "Schwerpunkt Urologie"
        },
        {
          "code": "3624",
          "display": "Schwerpunkt Frauenheilkunde und Geburtshilfe"
        },
        {
          "code": "3626",
          "display": "Schwerpunkt Hals-, Nasen-, Ohrenheilkunde"
        },
        {
          "code": "3628",
          "display": "Schwerpunkt Neurologie"
        }
      ]
    }
""");
    return hospitalizationServiceTypeCodeSystem;
  }

  private static Feature createOrganizationAssociationCodeSystem() {
    var organizationAssociationCodeSystem =
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/organizationAssociation");
    organizationAssociationCodeSystem.add(
        "employment",
        """
    {
      "code": "employment",
      "display": "Tätigkeit"
    }""");
    organizationAssociationCodeSystem.add(
        "accommodation",
        """
    {
      "code": "accommodation",
      "display": "Unterbringung"
    }""");
    organizationAssociationCodeSystem.add(
        "care",
        """
    {
      "code": "care",
      "display": "Betreuung"
    }""");
    return organizationAssociationCodeSystem;
  }

  private static Feature createGeographicRegionCodeSystem() {
    var geographicRegion =
        new CodeSystemFeature("https://demis.rki.de/fhir/CodeSystem/geographicRegion");
    geographicRegion.add(
        "21000316",
        """
                {
                  "code": "21000316",
                  "display": "Libyen",
                  "concept": [
                    {
                      "code": "41248001",
                      "display": "Ajdabiya (agedabia)"
                    },
                    {
                      "code": "41248002",
                      "display": "Al Aziziyah"
                    },
                    {
                      "code": "41248003",
                      "display": "Al Fatah"
                    },
                    {
                      "code": "41248004",
                      "display": "Al Jabal Al Akhdar"
                    },
                    {
                      "code": "41248005",
                      "display": "Al Jufrah"
                    },
                    {
                      "code": "41248006",
                      "display": "Al Khoms"
                    },
                    {
                      "code": "41248007",
                      "display": "Al Kufrah"
                    },
                    {
                      "code": "41248008",
                      "display": "Ash Shati"
                    },
                    {
                      "code": "41248009",
                      "display": "Awbari (ubari)"
                    },
                    {
                      "code": "41248010",
                      "display": "Az Zawia (azzawiya)"
                    },
                    {
                      "code": "41248011",
                      "display": "Banghazi"
                    },
                    {
                      "code": "41248012",
                      "display": "Darnah"
                    },
                    {
                      "code": "41248013",
                      "display": "Ghadamis"
                    },
                    {
                      "code": "41248014",
                      "display": "Gharyan"
                    },
                    {
                      "code": "41248015",
                      "display": "Misurata"
                    },
                    {
                      "code": "41248016",
                      "display": "Murzuq"
                    },
                    {
                      "code": "41248017",
                      "display": "Nuqat Al Khams"
                    },
                    {
                      "code": "41248018",
                      "display": "Sabha"
                    },
                    {
                      "code": "41248019",
                      "display": "Sawfajjin (sofuljeen)"
                    },
                    {
                      "code": "41248020",
                      "display": "Surt (sirte)"
                    },
                    {
                      "code": "41248021",
                      "display": "Tarhunah"
                    },
                    {
                      "code": "41248022",
                      "display": "Tripoli (tarabulus)"
                    },
                    {
                      "code": "41248023",
                      "display": "Tubruq (tobruk)"
                    },
                    {
                      "code": "41248024",
                      "display": "Yafran (yefren)"
                    },
                    {
                      "code": "41248025",
                      "display": "Zeleitin (zliten)"
                    }
                  ]
                }""");
    return geographicRegion;
  }

  @Override
  protected void add(Collection<Feature> features) {
    features.add(createAddressUses());
    features.add(createCountries());
    features.add(createGeographicRegionCodeSystem());
    features.add(createHospitalizationServiceTypeCodeSystem());
    features.add(createMilitaryAffiliation());
    features.add(createNullFlavor());
    features.add(createOrganizationAssociationCodeSystem());
    features.add(createOrganizationTypesCodeSystem());
    features.add(createOrganizationTypesValueSet());
    features.add(createYesOrNoAnswer());
  }

  @Override
  public String toString() {
    return "default code translations";
  }
}
