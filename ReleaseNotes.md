<img align="right" alt="gematik" width="250" height="47" src="media/Gematik_Logo_Flag.png"/> <br/>    

# Release pdfgen-Service

## Release 2.8.3
- use unit for printed quantities and code in case that there is no unit available
- update parent to 2.14.1
- add text from observation value if codeable concept and text field is filled
- format quantities correctly for laboratory
- show only one specimen material per specimen on laboratory page 2
 
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
### changed
- Supporting multiple observations in laboratory reports for §7.1 notifications

## Release 2.0.2
### changed
- Notification ID as QR Code

## Release 2.0.1
### fixed
- Bugfix Notification-ID OccupancyBed missing in PDF

## Release 2.0.0
### changed
- Implemented Hospital Notifications PDFs, logging

## Release 1.1.1
### changed
- Implemented PDF-Generator Functionalities

## Release 1.1.0
### changed
- fix Apache Tomcat CVE-2022-45143, upgrade SpringBoot

## Release 1.0.0
- Initial release
