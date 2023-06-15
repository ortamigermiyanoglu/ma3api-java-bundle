package tr.gov.tubitak.uekae.esya.api.pades.example.optional;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.pades.example.PadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESContainer;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESSignature;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.SignaturePanel;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.VisibleSignature;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class VisibleSignatureExample extends PadesSampleBase {

    @Test
    public void setVisibleSignatureAndSign() throws Exception {

        // read
        PAdESContainer padesContainer = (PAdESContainer)SignatureFactory.readContainer(SignatureFormat.PAdES, new FileInputStream(
                getTestFile()), createContext());

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

        SignaturePanel signaturePanel = new SignaturePanel(50, 500, 450, 50);
        Dimension imageSize = new Dimension(50,50);
        String visibleText = "Bu belge "+ eCertificate.getSubject().getCommonNameAttribute() + " tarafından elektronik olarak imzalanmıştır.";
        byte[] imageBytes = VisibleSignatureImageCreator.createImage("EnterTheImagePath", visibleText, signaturePanel, imageSize);

        VisibleSignature visibleSignature = new VisibleSignature();
        visibleSignature.setPosition(1, signaturePanel);
        visibleSignature.setImage(new ByteArrayInputStream(imageBytes));
        visibleSignature.setLocation("Istanbul");

        // add signature
        PAdESSignature signature = (PAdESSignature)padesContainer.createSignature(eCertificate);
        signature.setVisibleSignature(visibleSignature);

        signature.sign(signer);

        padesContainer.write(new FileOutputStream(getTestDataFolder() + "signed-visible.pdf"));

        // read and validate
        SignatureContainer readContainer = SignatureFactory.readContainer(SignatureFormat.PAdES, new FileInputStream(
                getTestDataFolder() + "signed-visible.pdf"), createContext());
        ContainerValidationResult validationResult = readContainer.verifyAll();
        System.out.println(validationResult);
        assert validationResult.getResultType() == ContainerValidationResultType.ALL_VALID;
    }
}
