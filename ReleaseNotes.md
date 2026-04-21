<div style="text-align:right"><img src="https://raw.githubusercontent.com/gematik/gematik.github.io/master/Gematik_Logo_Flag_With_Background.png" width="250" height="47" alt="gematik GmbH Logo"/> <br/> </div> <br/>    

# Release pdfgen-Service

## Release 2.10.2
- set up max mamory to 650 Mi and max heap usage to 30%
- updated spring-parent to version 2.15.7
- set thymeleaf version to 3.1.4.RELEASE to fix CVE

## Release 2.10.1
- updated spring-parent to version 2.15.6

## Release 2.10.0
- updated base-image and updated from java 21 to java 25
- changed garbage collector to G1GC
- increased RAM-Limit
- decreased MaxRAMPercentage from 80% to 65%
- activated feign header forwarding
- added identifier to laboratory facilities in questionnaire answers of disease receipts
- updated dependencies
- removed FEATURE_FLAG_DISEASE_SECOND_PAGE
- removed FEATURE_FLAG_PDF_OPTIMIZATION

## Release 2.9.2
- changed label for disease notifications for relates-to id entries in produced pdf
- remove throwing exception for disease notification without CommonQuestionnaire

## Release 2.9.1
- added specific font for watermark in PDF receipt
- removed istio helm charts
- upgraded spring parent to 2.14.21

## Release 2.9.0
- corrected label for IGS transaction ID
- updated dependencies
- added a QR code page to the Disease PDF
- updated spring-parent to 2.14.20
- added call to FUTS to get code representation for value quantity codes
- updated gender mapping in the PDF output using the extension (D = diverse, X = no gender entry)
- added ratio handling for observations
- added feature flag LABORATORY_VALUE_QUANTITY_CODE
- removed dependency to JDK in docker-image
- added logic to find long common name use in designation to translate codes
- seted ram limit to 850
- removed feature flag FEATURE_FLAG_NEW_API_ENDPOINTS
- fixed missing postal code on second PDF page by adding fallback to organization address

## Release 2.8.4
- update spring-parent to 2.14.2
- remove feature flag FEATURE_FLAG_HOSPITALIZATION_ORDER
- fixed the display of the hospitalization station in the Disease PDF receipt based on the strict profile (from version 7.x.x)

## Release 2.8.3
- use unit for printed quantities and code in case that there is no unit available
- update parent to 2.14.1
- add text from observation value if codeable concept and text field is filled
- format quantities correctly for laboratory
- show only one specimen material per specimen on laboratory page 2
- fix patient finding for bundles with broken patient id
 
## Release 2.8.2
- Added support for MeinUnternehmenskonto
- Remove feature flag PDFGEN.WARMUP

## Release 2.8.1
- Edit text for authentication method output for authentication via SMC-B
- Adding extra header for requests to FUTS new APIs

## Release 2.8.0
- Add support for new FUTS API endpoints
- Edit authentication method output for authenticator and token-exchange

## Release 2.7.2
- updated dependencies
- updated base image
- python script to clean up TransmittingSiteSearchText.xml 
- Update TransmittingSiteSearchText
- Add script to minimize TransmittingSiteSearchText

## Release 2.7.1
- Updated ospo-resources for adding additional notes and disclaimer
- setting new resources in helm chart
- setting new timeouts and retries in helm chart
- change base chart to istio hostnames
- updating dependencies
- Fix provenance in PDF receipt for notifications sent over meldung.demis.rki.de

## Release 2.7.0
- Add service API documentation
- Align order of hospitalization information in UI and PDF

## Release 2.6.4
- Fix handling of PostalCodes

## Release 2.6.3
- Updates:
  - Dependency-Updates (CVEs et al.)
  - Introducing resistances after §7.1 
  - Extend internal data model
  - Adding FUTS caching
  - PDF receipt enriched with context information
  - Link to info package in PDF Receipt for illness reports
  - Add sampling and sample material to PDF Receipt
  - Update Error-Handling
  - Allow multiple samples in the PDF receipt
  - Include the reason for the examination in the PDF receipt
  - Presentation of data from the non-formal notification
  - Address order updated
  - Update reason for hospitalization

## Release 2.1.5
- first official GitHub-Release

## Release 2.1.0
## Release# changed
- Supporting multiple observations in laboratory reports for §7.1 notifications

## Release 2.0.2
## Release# changed
- Notification ID as QR Code

## Release 2.0.1
## Release# fixed
- Bugfix Notification-ID OccupancyBed missing in PDF

## Release 2.0.0
## Release# changed
- Implemented Hospital Notifications PDFs, logging

## Release 1.1.1
## Release# changed
- Implemented PDF-Generator Functionalities

## Release 1.1.0
## Release# changed
- fix Apache Tomcat CVE-2022-45143, upgrade SpringBoot

## Release 1.0.0
- Initial release