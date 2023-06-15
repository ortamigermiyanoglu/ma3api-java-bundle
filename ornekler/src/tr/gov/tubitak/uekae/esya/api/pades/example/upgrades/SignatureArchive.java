package tr.gov.tubitak.uekae.esya.api.pades.example.upgrades;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESContainer;
import tr.gov.tubitak.uekae.esya.api.pades.example.PadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SignatureArchive extends PadesSampleBase {

    @Test
    public void createXL() throws Exception{
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestFile()), createContext());

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

        // add signature
        Signature signature = signatureContainer.createSignature(eCertificate);
        signature.sign(signer);

        // add timestamp
        signature.upgrade(SignatureType.ES_XL);
        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-lt.pdf"));
    }

    @Test
    public void archiveXL() throws Exception{

        FileInputStream fileToBeArchive =  new FileInputStream(getTestDataFolder() + "signed-lt.pdf");

        PAdESContainer signatureContainer = (PAdESContainer) SignatureFactory.readContainer(SignatureFormat.PAdES,
                fileToBeArchive, createContext());

        signatureContainer.getLatestSignerSignature().upgrade(SignatureType.ES_A);

        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "archived-lt.pdf"));
    }

}
