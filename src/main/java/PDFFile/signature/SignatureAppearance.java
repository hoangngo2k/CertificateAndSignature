package PDFFile.signature;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.ListIterator;

public class SignatureAppearance {

    public static final String KEYSTORE = "p12cert/keystore.jks";
    public static final String SRC = "signfile/pdffile/input/sample.pdf";
    public static final String IMG = "img/img_2.png";

    public static final char[] PASSWORD = "123456".toCharArray();

    public void sign(String src, String name, String dest, Certificate[] chain, PrivateKey pk, String digestAlgorithm,
                     String provider, PdfSigner.CryptoStandard subfilter, String reason, String location,
                     PdfSignatureAppearance.RenderingMode renderingMode, ImageData image)
            throws GeneralSecurityException, IOException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src));

        String informationSignature = "Full Name:" + name + "\nLocation: " + location + "\n" + new Date();

        Rectangle rect = new Rectangle(320, 48, 200, 100);

        // Create the signature appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);

        // This name corresponds to the name of the field that already exists in the document.
        signer.setFieldName(name);

        appearance.setLayer2Text(informationSignature);

        // Set the rendering mode for this signature.
        appearance.setRenderingMode(renderingMode);
        appearance.setPageRect(rect);
        // Set the Image object to render when the rendering mode is set to RenderingMode.GRAPHIC
        // or RenderingMode.GRAPHIC_AND_DESCRIPTION.
        appearance.setPageNumber(pdfDoc.getNumberOfPages());
        appearance.setSignatureGraphic(image);

        PrivateKeySignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();

        // Sign the document using the detached mode, CMS or CAdES equivalent.
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
//        File file = new File(DEST);
//        file.mkdirs();

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(KEYSTORE), PASSWORD);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain = ks.getCertificateChain(alias);
        ImageData image = ImageDataFactory.create(IMG);

        SignatureAppearance app = new SignatureAppearance();
        String signatureName = "Hoàng Ngô";
        String location = "Hà Nội";
        app.sign(SRC, signatureName, "signfile/pdffile/output/output1.pdf", chain, pk,
                DigestAlgorithms.SHA256, provider.getName(), PdfSigner.CryptoStandard.CMS,
                "Hóa đơn điện tử", location, PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION, image);
    }
}
