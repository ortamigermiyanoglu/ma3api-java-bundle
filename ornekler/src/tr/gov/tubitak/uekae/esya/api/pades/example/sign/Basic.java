package tr.gov.tubitak.uekae.esya.api.pades.example.sign;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.pades.example.PadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Basic extends PadesSampleBase {

    @Test
    public void signBES() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestFile()), createContext());

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

        // add signature
        Signature signature = signatureContainer.createSignature(eCertificate);
        signature.setSigningTime(Calendar.getInstance());
        signature.sign(signer);
        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-bes.pdf"));

        // read and validate
        SignatureContainer readContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-bes.pdf"), createContext());

        ContainerValidationResult validationResult = readContainer.verifyAll();
        System.out.println(validationResult);
        assert validationResult.getResultType() == ContainerValidationResultType.ALL_VALID;
    }

    @Test
    public void validateSignedPdf() throws Exception {

        SignatureContainer sc = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-bes.pdf"), createContext());

        ContainerValidationResult validationResult = sc.verifyAll();
        System.out.println(validationResult);
        assert validationResult.getResultType() == ContainerValidationResultType.ALL_VALID;
    }

    @Test
    public void readWriteTest() throws Exception {

        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-bes.pdf"), createContext());

        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-rewrite.pdf"));
    }

}
