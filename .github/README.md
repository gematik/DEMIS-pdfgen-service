<img align="right" alt="gematik" width="250" height="47" src="../media/Gematik_Logo_Flag.png"/> <br/>

# pdfgen-Service

## About The Project

This service processes FHIR messages and converts them into PDF receipts. The goal is to encapsulate all pertinent business information into a printable format. Currently, the service supports the generation of PDF files from the following types of FHIR messages:
- Bed occupancy report
- Disease notification
- Pathogen notification (aka. laboratory report)

The media type must be provided as a request header and must be of type
"application/json" or "application/xml".

### Quality Gate

[![Quality Gate Status](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Apdfgen-service&metric=alert_status&token=7f24a1dd3efdc568effea24956accd1e8057f3ba)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Apdfgen-service) [![Vulnerabilities](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Apdfgen-service&metric=vulnerabilities&token=7f24a1dd3efdc568effea24956accd1e8057f3ba)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Apdfgen-service) [![Bugs](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Apdfgen-service&metric=bugs&token=7f24a1dd3efdc568effea24956accd1e8057f3ba)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Apdfgen-service) [![Code Smells](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Apdfgen-service&metric=code_smells&token=7f24a1dd3efdc568effea24956accd1e8057f3ba)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Apdfgen-service) [![Lines of Code](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Apdfgen-service&metric=ncloc&token=7f24a1dd3efdc568effea24956accd1e8057f3ba)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Apdfgen-service) [![Coverage](https://sonar.prod.ccs.gematik.solutions/api/project_badges/measure?project=de.gematik.demis%3Apdfgen-service&metric=coverage&token=7f24a1dd3efdc568effea24956accd1e8057f3ba)](https://sonar.prod.ccs.gematik.solutions/dashboard?id=de.gematik.demis%3Apdfgen-service)

### Release Notes

See [ReleaseNotes](../ReleaseNotes.md) for all information regarding the (newest) releases.

## Getting Started

### Installation

The Docker image associated to the service can be built alternatively with the extra profile `docker`:

```docker
mvn clean install -Pdocker
```

## Usage

### Swagger

When running locally, swagger can be found at:

http://localhost:8080/swagger-ui/index.html

### Properties

| Property                             | Default Value                                             | Description                                                 |
|--------------------------------------|-----------------------------------------------------------|-------------------------------------------------------------|
| pdfgen.template.bed-occupancy        | `receipt/bedOccupancy/bedOccupancyTemplate`               | Path to Thymeleaf template of bed occupancy report receipts |
| pdfgen.template.disease-notification | `receipt/diseaseNotification/diseaseNotificationTemplate` | Path to Thymeleaf template of disease notification receipts |
| pdfgen.template.laboratory-report    | `receipt/laboratoryReport/laboratoryReportTemplate`       | Path to Thymeleaf template of laboratory report receipts    |
| pdfgen.lastpage.qrcode.enabled       | `true`                                                    | Flag to activate/deactivate QR Code on last page            |

### Endpoints

Endpoints offered by the service.

| Endpoint             | Method | Input                                | Output         |
|----------------------|--------|--------------------------------------|----------------|
| /bedOccupancy        | POST   | FHIR bed-occupancy report JSON/XML   | PDF file bytes |
| /diseaseNotification | POST   | FHIR disease notification JSON/XML   | PDF file bytes |
| /laboratoryReport    | POST   | FHIR pathogen notification JSON/XML  | PDF file bytes |


### Configuration

The Spring application properties of the service.

| Feature             | Parameter                            | Description                                                 | Example values                                  |
|---------------------|--------------------------------------|-------------------------------------------------------------|-------------------------------------------------|
| Spring cache (FUTS) | SPRING_CACHE_TYPE                    | On-off switch for Spring caching. Remove to enable caching. | none                                            |
|                     | SPRING_CACHE_CAFFEINE_SPEC           | All-in-one text to configure all caching parameters.        | "expireAfterWrite=1h,expireAfterAccess=15m"     |
|                     | SPRING_CACHE_CACHE_NAMES             | Listing of all enabled caches.                              | "futs-code-systems,futs-disease-questionnaires" |
|                     | SPRING_CLOUD_OPENFEIGN_CACHE_ENABLED | On-off switch for Feign caching.                            | false                                           |


## Contributing

If you want to contribute, please check our [CONTRIBUTING.md](CONTRIBUTING.md).

## Security Policy

If you want to see the security policy, please check our [SECURITY.md](SECURITY.md).

## License

EUROPEAN UNION PUBLIC LICENCE v. 1.2

EUPL © the European Union 2007, 2016

Copyright (c) 2023 gematik GmbH

See [LICENSE](../LICENSE.md).

## Contact

E-Mail to [DEMIS Entwicklung](mailto:demis-entwicklung@gematik.de?subject=[GitHub]%20PDFGen-Service)
