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

package de.gematik.demis.pdfgen.receipt.common.model.section;

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

import de.gematik.demis.pdfgen.fhir.extract.ProvenanceFhirQueries;
import de.gematik.demis.pdfgen.receipt.common.model.enums.AuthenticationLevelOfAssuranceEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.AuthenticationMethodEnum;
import de.gematik.demis.pdfgen.receipt.common.model.enums.AuthenticationRoutingEnum;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Provenance;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Service;

/** Factory to create Authentication object from Provenance object */
@Service
@RequiredArgsConstructor
public class AuthenticationFactory {

  private final ProvenanceFhirQueries provenanceFhirQueries;

  @Nullable
  public Authentication create(final Bundle bundle) {
    Optional<Provenance> demisProvenanceOptional = provenanceFhirQueries.getDemisProvenance(bundle);
    return demisProvenanceOptional.map(this::createAuthenticationFromProvenance).orElse(null);
  }

  private Authentication createAuthenticationFromProvenance(Provenance provenance) {
    if (provenance == null || provenance.getAgentFirstRep() == null) {
      return null;
    }
    AuthenticationRoutingEnum routing = resolveAuthenticationRouting(provenance);
    AuthenticationMethodEnum method = resolveAuthenticationMethod(provenance);
    AuthenticationLevelOfAssuranceEnum levelOfAssurance =
        resolveLevelOfAssurance(provenance.getAgentFirstRep());

    Authentication.AuthenticationBuilder authentication =
        Authentication.builder().routing(routing).method(method).levelOfAssurance(levelOfAssurance);

    return authentication.build();
  }

  private AuthenticationLevelOfAssuranceEnum resolveLevelOfAssurance(
      Provenance.ProvenanceAgentComponent agentFirstRep) {
    try {
      Extension levelOfAssuranceExtension =
          agentFirstRep.getExtensionByUrl(
              "https://demis.rki.de/fhir/StructureDefinition/ProvenanceAgentLevelOfAssurance");
      CodeableConcept codeableConcept = (CodeableConcept) levelOfAssuranceExtension.getValue();
      String codeValue = codeableConcept.getCodingFirstRep().getCode();
      return AuthenticationLevelOfAssuranceEnum.of(codeValue);
    } catch (Exception e) {
      return AuthenticationLevelOfAssuranceEnum.UNKNOWN;
    }
  }

  private static AuthenticationRoutingEnum resolveAuthenticationRouting(Provenance provenance) {
    try {
      List<Provenance.ProvenanceEntityComponent> entities = provenance.getEntity();
      for (Provenance.ProvenanceEntityComponent entity : entities) {
        if (entity.getWhat() == null || entity.getWhat().getIdentifier() == null) {
          continue;
        }
        Identifier identifier = entity.getWhat().getIdentifier();
        if (identifier.getSystem().equals("https://demis.rki.de/fhir/sid/DemisClientId")) {
          return AuthenticationRoutingEnum.of(identifier.getValue());
        }
      }
      return AuthenticationRoutingEnum.UNKNOWN;
    } catch (Exception e) {
      return AuthenticationRoutingEnum.UNKNOWN;
    }
  }

  private AuthenticationMethodEnum resolveAuthenticationMethod(Provenance provenance) {
    try {
      Reference onBehalfOf = provenance.getAgentFirstRep().getOnBehalfOf();
      if (onBehalfOf == null || onBehalfOf.getIdentifier() == null) {
        return AuthenticationMethodEnum.UNKNOWN;
      }
      String authenticationMethodValue = onBehalfOf.getIdentifier().getSystem();
      return AuthenticationMethodEnum.of(authenticationMethodValue);
    } catch (Exception e) {
      return AuthenticationMethodEnum.UNKNOWN;
    }
  }
}
