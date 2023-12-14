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

import static org.assertj.core.api.Assertions.assertThat;

import de.gematik.demis.pdfgen.pdf.PdfData;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.Assertions;

@Slf4j
public final class PdfExtractorHelper {

  private static final boolean FILES_ENABLED = false;
  private static final String FILES_DIRECTORY = "target/pdfgen-tests/files/";

  /**
   * Verifies there is an editable field with the given name on the expected page.
   *
   * @param pdfBytes PDF file as bytes
   * @param name name of the editable field
   * @param pageIndex expected index of page the field is at, 0...n
   * @throws IOException failed to process PDF file data
   */
  public static void verifyField(byte[] pdfBytes, String name, int pageIndex) throws IOException {
    PDDocument document = Loader.loadPDF(pdfBytes);
    PDField field = document.getDocumentCatalog().getAcroForm().getField(name);
    assertThat(field).as("field with id: " + name).isNotNull();
    int actualPageIndex = pageIndex(document, field);
    assertThat(actualPageIndex).as("page index of field").isEqualTo(pageIndex);
  }

  /**
   * Writes bytes as PDF file
   *
   * @param pdfBytes bytes of a PDF document
   */
  private static void writeFile(byte[] pdfBytes) {
    if (FILES_ENABLED && isDevClient()) {
      String timestamp =
          DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS").format(ZonedDateTime.now());
      String filename = FILES_DIRECTORY + timestamp + ".pdf";
      try {
        Files.createDirectories(Path.of(FILES_DIRECTORY));
        Files.write(Path.of(filename), pdfBytes, StandardOpenOption.CREATE_NEW);
      } catch (Exception e) {
        throw new IllegalStateException("Failed to write PDF file: " + filename, e);
      }
    }
  }

  private static boolean isDevClient() {
    try {
      return InetAddress.getLocalHost().getHostName().startsWith("GNDEV");
    } catch (Exception e) {
      log.trace("Failed to get name of local host", e);
      return false;
    }
  }

  private static int pageIndex(PDDocument document, PDField field) throws IOException {
    Integer pageIndex;
    for (PDAnnotationWidget widget : field.getWidgets()) {
      PDPage page = widget.getPage();
      if (page == null) {
        pageIndex = pageIndex(document, widget);
        if (pageIndex == null) {
          continue;
        }
        return pageIndex;
      }
      return document.getPages().indexOf(page);
    }
    Assertions.fail("Failed to find page of PDF field");
    return 0;
  }

  /**
   * Plan B: try all pages to check the annotations.
   *
   * @param widget field widget
   * @return <code>null</code> or page index
   * @throws IOException failed to process annotations
   */
  private static Integer pageIndex(PDDocument document, PDAnnotationWidget widget)
      throws IOException {
    for (int p = 0; p < document.getNumberOfPages(); ++p) {
      List<PDAnnotation> annotations;
      annotations = document.getPage(p).getAnnotations();
      for (PDAnnotation ann : annotations) {
        if (ann.getCOSObject() == widget.getCOSObject()) {
          return p;
        }
      }
    }
    return null;
  }

  public static String extractPdfText(PdfData pdfData) throws IOException {
    return extractPdfText(pdfData.bytes());
  }

  public static String extractPdfText(byte[] pdfBytes) throws IOException {
    writeFile(pdfBytes);
    final var pdfStripper = new PDFTextStripper();
    pdfStripper.setSortByPosition(true);
    pdfStripper.setLineSeparator("\n");
    final var document = Loader.loadPDF(pdfBytes);
    StringBuilder text = new StringBuilder();
    for (int i = 1; i <= document.getNumberOfPages(); ++i) {
      // Set the page interval to extract, the layout on last page isn't properly separated
      pdfStripper.setStartPage(i);
      pdfStripper.setEndPage(i);
      text.append(pdfStripper.getText(document));
    }
    return text.toString().replaceAll("\r\n", "\n").replaceAll("^\\s+", "").replaceAll("\\s+$", "");
  }

  // Taken from https://stackoverflow.com/a/37664125
  public static List<RenderedImage> getAllImagesFromPdfData(byte[] pdfBytes) throws IOException {
    List<RenderedImage> images = new ArrayList<>();
    try (PDDocument document = Loader.loadPDF(pdfBytes)) {
      for (PDPage page : document.getPages()) {
        images.addAll(getImagesFromResources(page.getResources()));
      }
    }
    return images;
  }

  private static List<RenderedImage> getImagesFromResources(PDResources resources)
      throws IOException {
    List<RenderedImage> images = new ArrayList<>();

    for (COSName xObjectName : resources.getXObjectNames()) {
      PDXObject xObject = resources.getXObject(xObjectName);

      if (xObject instanceof PDFormXObject) {
        images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
      } else if (xObject instanceof PDImageXObject) {
        images.add(((PDImageXObject) xObject).getImage());
      }
    }

    return images;
  }

  private PdfExtractorHelper() {
    // hidden
  }
}
