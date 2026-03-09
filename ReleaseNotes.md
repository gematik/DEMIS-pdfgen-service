<div style="text-align:right"><img src="https://raw.githubusercontent.com/gematik/gematik.github.io/master/Gematik_Logo_Flag_With_Background.png" width="250" height="47" alt="gematik GmbH Logo"/> <br/> </div> <br/>    

# Release pdfgen-Service

## 2.9.2
- changed label for disease notifications for relates-to id entries in produced pdf
- remove throwing exception for disease notification without CommonQuestionnaire

## 2.9.1
- added specific font for watermark in PDF receipt
- removed istio helm charts
- upgraded spring parent to 2.14.21

## 2.9.0
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

## 2.8.4
- update spring-parent to 2.14.2
- remove feature flag FEATURE_FLAG_HOSPITALIZATION_ORDER
- fixed the display of the hospitalization station in the Disease PDF receipt based on the strict profile (from version 7.x.x)

## 2.8.3
- use unit for printed quantities and code in case that there is no unit available
- update parent to 2.14.1
- add text from observation value if codeable concept and text field is filled
- format quantities correctly for laboratory
- show only one specimen material per specimen on laboratory page 2
- fix patient finding for bundles with broken patient id
 
## 2.8.2
- Added support for MeinUnternehmenskonto
- Remove feature flag PDFGEN.WARMUP

## 2.8.1
- Edit text for authentication method output for authentication via SMC-B
- Adding extra header for requests to FUTS new APIs

## 2.8.0
- Add support for new FUTS API endpoints
- Edit authentication method output for authenticator and token-exchange

## 2.7.2
- updated dependencies
- updated base image
- python script to clean up TransmittingSiteSearchText.xml 
- Update TransmittingSiteSearchText
- Add script to minimize TransmittingSiteSearchText

## 2.7.1
- Updated ospo-resources for adding additional notes and disclaimer
- setting new resources in helm chart
- setting new timeouts and retries in helm chart
- change base chart to istio hostnames
- updating dependencies
- Fix provenance in PDF receipt for notifications sent over meldung.demis.rki.de

## 2.7.0
- Add service API documentation
- Align order of hospitalization information in UI and PDF

## 2.6.4
- Fix handling of PostalCodes

## 2.6.3
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

## 2.1.5
- first official GitHub-Release

## 2.1.0
### changed
- Supporting multiple observations in laboratory reports for §7.1 notifications

## 2.0.2
### changed
- Notification ID as QR Code

## 2.0.1
### fixed
- Bugfix Notification-ID OccupancyBed missing in PDF

## 2.0.0
### changed
- Implemented Hospital Notifications PDFs, logging

## 1.1.1
### changed
- Implemented PDF-Generator Functionalities

## 1.1.0
### changed
- fix Apache Tomcat CVE-2022-45143, upgrade SpringBoot

## 1.0.0
- Initial release
