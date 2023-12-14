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

package de.gematik.demis.pdfgen.fhir.extract;

import de.gematik.demis.pdfgen.lib.profile.DemisProfiles;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;
import org.springframework.stereotype.Service;

@Service
public class FhirQueries {

  public <T extends Resource> Optional<T> findResourceWithProfile(
      Bundle bundle, @Nonnull Class<T> klass, DemisProfiles.Profile<? super T> profileChecker) {
    if (bundle == null) {
      return Optional.empty();
    }
    return findResource(bundle, klass, profileChecker::isApplied);
  }

  private <T extends Resource> Optional<T> findResource(
      Bundle bundle, Class<T> klass, Predicate<? super T> condition) {
    return findResourceStream(bundle, klass).filter(condition).findFirst();
  }

  private <T extends Resource> Stream<T> findResourceStream(Bundle bundle, Class<T> klass) {
    return bundle.getEntry().stream()
        .map(Bundle.BundleEntryComponent::getResource)
        .filter(klass::isInstance)
        .map(klass::cast);
  }

  public <T extends Resource> List<T> findResourcesWithProfile(
      Bundle content, Class<T> clazz, DemisProfiles.Profile<? super T> profileChecker) {
    return findResources(content, clazz, profileChecker::isApplied);
  }

  public <T extends Resource> List<T> findResources(
      Bundle content, Class<T> clazz, Predicate<? super T> condition) {
    return findResourceStream(content, clazz).filter(condition).toList();
  }
}
