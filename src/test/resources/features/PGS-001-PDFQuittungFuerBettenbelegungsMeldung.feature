Feature: PDF generation Service auf Basis von Thymeleaf Template

  #Story: https://service.gematik.de/browse/DSC2-2595
  # Dieser erhält als Input die  DEMIS Meldung als FHIR-Resource im JSON-Format.
  # Rückgabewert des Rest-Aufruf ist die PDF-Quittung zur Bettenbelegungs-Meldung.
  # Zunächst wird als DEMIS-Meldung nur die Bettenbelegung unterstützt und als Template umgesetzt.

  Scenario: PDF-Service gibt PDF-Quittung für Bettenbelegungs-Meldung zurück
    Given es existiert ein Endpunkt fuer die Annahme der Fhirmeldungen im JSON Format
    When Eine Bettenbelegungsmeldung gesendet wird
    Then Wird eine Antwort als PDF-Binary erwartet