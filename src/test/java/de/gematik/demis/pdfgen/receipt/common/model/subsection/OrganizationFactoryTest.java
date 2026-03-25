package de.gematik.demis.pdfgen.receipt.common.model.subsection;

/*-
 * #%L
 * pdfgen-service
 * %%
 * Copyright (C) 2025 - 2026 gematik GmbH
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
 * For additional notes and disclaimer from gematik and in case of changes by gematik,
 * find details in the "Readme" file.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.gematik.demis.pdfgen.FeatureFlags;
import de.gematik.demis.pdfgen.lib.profile.DemisSystems;
import de.gematik.demis.pdfgen.translation.TranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationFactoryTest {

  private static final String BSNR_VALUE = "123456789";

  private final IdentifierFactory identifierFactory = new IdentifierFactory();

  @Mock private FeatureFlags featureFlags;
  @Mock private AddressFactory addressFactory;
  @Mock private TelecomFactory telecomFactory;
  @Mock private ContactFactory contactFactory;
  @Mock private TranslationService translationService;

  private OrganizationFactory organizationFactory;

  @BeforeEach
  void createOrganizationFactory() {
    this.organizationFactory =
        new OrganizationFactory(
            featureFlags,
            identifierFactory,
            addressFactory,
            telecomFactory,
            contactFactory,
            translationService);
  }

  @Test
  void givenBsnrIdentifierWhenCreateThenIdentifierIsSet() {
    // given
    when(this.featureFlags.isDiseaseStrict()).thenReturn(Boolean.TRUE);
    org.hl7.fhir.r4.model.Organization fhirOrganization = new org.hl7.fhir.r4.model.Organization();
    fhirOrganization
        .addIdentifier()
        .setSystem(DemisSystems.BSNR_ORGANIZATION_ID)
        .setValue(BSNR_VALUE);

    // when
    OrganizationDTO organizationDTO = organizationFactory.create(fhirOrganization);

    // then
    assertThat(organizationDTO).isNotNull();
    final Identifier identifier = organizationDTO.getIdentifier();
    assertThat(identifier).isNotNull();
    assertThat(identifier.getBsnr()).isEqualTo(BSNR_VALUE);
    assertThat(identifier.getDemisId()).isNull();
  }

  @Test
  void givenNoFeatureFlagWithBsnrIdentifierWhenCreateThenIdentifierIsNotSet() {
    // given
    when(this.featureFlags.isDiseaseStrict()).thenReturn(Boolean.FALSE);
    org.hl7.fhir.r4.model.Organization fhirOrganization = new org.hl7.fhir.r4.model.Organization();
    fhirOrganization
        .addIdentifier()
        .setSystem(DemisSystems.BSNR_ORGANIZATION_ID)
        .setValue("123456789");

    // when
    OrganizationDTO organizationDTO = organizationFactory.create(fhirOrganization);

    // then
    assertThat(organizationDTO).isNotNull();
    assertThat(organizationDTO.getIdentifier()).isNull();
  }
}
