package PDFFile.signature;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.graphics.PdfFont;
import com.spire.pdf.graphics.PdfFontFamily;
import com.spire.pdf.graphics.PdfFontStyle;
import com.spire.pdf.graphics.PdfImage;
import com.spire.pdf.security.GraphicMode;
import com.spire.pdf.security.PdfCertificate;
import com.spire.pdf.security.PdfCertificationFlags;
import com.spire.pdf.security.PdfSignature;
import com.spire.pdf.widget.PdfFormFieldWidgetCollection;
import com.spire.pdf.widget.PdfFormWidget;
import com.spire.pdf.widget.PdfSignatureFieldWidget;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class SignPDF {

    private static final String PDF_FILE_INPUT_NAME = "signfile/pdffile/sample.pdf";
    private static final String PDF_FILE_OUTPUT_NAME = "signfile/pdffile/output.pdf";
    private static final String PDF_FILE_TEST = "signfile/SampleSignedPDFDocument.pdf";
    private static final String CRT_FILE = "p12cert/keystore.crt";
    private static final String SIGNATURE_IMAGE = "img/img_1.png";

    public static void main(String[] args) {
        SignPDF signPDF = new SignPDF();
//        By Spire
//        signPDF.signPdfNotImage();
//        signPDF.signPdfWithImage();
        signPDF.verifySignature();

    }

    public void signPdfNotImage() {
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile(PDF_FILE_INPUT_NAME);

        PdfCertificate cert = new PdfCertificate(CRT_FILE, "e-iceblue");

        PdfSignature signature = new PdfSignature(doc, doc.getPages().get(doc.getPages().getCount() - 1), cert, "MySignature");
        signature.setDocumentPermissions(PdfCertificationFlags.Forbid_Changes);
        signature.setDocumentPermissions(PdfCertificationFlags.Allow_Form_Fill);

        doc.saveToFile(PDF_FILE_OUTPUT_NAME);
        doc.close();
    }

    public void signPdfWithImage() {
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile(PDF_FILE_INPUT_NAME);

        PdfCertificate cert = new PdfCertificate(CRT_FILE, "123456");

        PdfSignature signature = new PdfSignature(doc, doc.getPages().get(doc.getPages().getCount()-1),
                cert, "My Signature");
        Rectangle2D rect = new Rectangle2D.Float();
        rect.setFrame(new Point2D.Float((float) doc.getPages().get(0).getActualSize().getWidth() - 320,
                (float) doc.getPages().get(0).getActualSize().getHeight() - 140),
                new Dimension(270, 100));
        signature.setBounds(rect);

        signature.setGraphicMode(GraphicMode.Sign_Image_And_Sign_Detail);

        signature.setNameLabel("Signer:");
        signature.setName("Gary");
        signature.setContactInfoLabel("ContactInfo:");
        signature.setContactInfo("02881705109");
        signature.setDateLabel("Date:");
        signature.setDate(new java.util.Date());
        signature.setLocationInfoLabel("Location:");
        signature.setLocationInfo("Chengdu");
        signature.setReasonLabel("Reason: ");
        signature.setReason("The certificate of this document");
        signature.setDistinguishedNameLabel("DN: ");
        signature.setDistinguishedName(signature.getCertificate().get_IssuerName().getName());
        signature.setSignImageSource(PdfImage.fromFile(SIGNATURE_IMAGE));

        signature.setSignDetailsFont(new PdfFont(PdfFontFamily.Helvetica, 10f, PdfFontStyle.Regular));

        signature.setDocumentPermissions(PdfCertificationFlags.Forbid_Changes);
        signature.setCertificated(true);

        doc.saveToFile(PDF_FILE_OUTPUT_NAME);
        doc.close();
        System.out.println("cert: " + signature.getCertificate());
    }

    public void verifySignature() {
        PdfDocument doc = new PdfDocument();
        doc.loadFromFile("signfile/pdffile/output/signature_appearance4.pdf");

        PdfFormWidget pdfFormWidget = (PdfFormWidget) doc.getForm();
        PdfFormFieldWidgetCollection collection = pdfFormWidget.getFieldsWidget();

        for (int i = 0; i < collection.getCount(); i++) {
            if (collection.get(i) instanceof PdfSignatureFieldWidget) {
                PdfSignatureFieldWidget signatureFieldWidget = (PdfSignatureFieldWidget) collection.get(i);

                System.out.println(signatureFieldWidget.getSignature().getSignatureName());
                PdfSignature signature = signatureFieldWidget.getSignature();

                System.out.println(signature.verifySignature() ? "verify: true" : "verify: false");
            }
        }
    }
}
