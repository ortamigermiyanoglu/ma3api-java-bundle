package tr.gov.tubitak.uekae.esya.api.pades.example.sign;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.pades.example.PadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Multiple extends PadesSampleBase {

    @Test
    public void signSequentalBES() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(new FileInputStream(getTestFile()), createContext());

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

        // add signature
        Signature signature = signatureContainer.createSignature(eCertificate);
        signature.sign(signer);
        Signature signature2 = signatureContainer.createSignature(eCertificate);
        signature2.sign(signer);

        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-seq.pdf"));

        // read and validate
        SignatureContainer readContainer = SignatureFactory.readContainer(new FileInputStream(
                getTestDataFolder() + "signed-seq.pdf"), createContext());
        ContainerValidationResult validationResult = readContainer.verifyAll();
        System.out.println(validationResult);
        assert validationResult.getResultType() == ContainerValidationResultType.ALL_VALID;
    }

}
