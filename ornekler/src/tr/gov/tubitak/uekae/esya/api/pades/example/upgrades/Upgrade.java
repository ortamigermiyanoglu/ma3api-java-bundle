package tr.gov.tubitak.uekae.esya.api.pades.example.upgrades;

import org.junit.Assert;
import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESContext;
import tr.gov.tubitak.uekae.esya.api.pades.example.PadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Upgrade extends PadesSampleBase {

    @Test
    public void createEST() throws Exception {
        // read
        PAdESContext context = createContext();
        context.setSignWithTimestamp(true);
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES, new FileInputStream(getTestFile()), context);

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

        // add signature
        Signature signature = signatureContainer.createSignature(eCertificate);
        signature.setSigningTime(Calendar.getInstance());
        signature.sign(signer);

        // write to file
        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-est.pdf"));
    }

    @Test
    public void upgradeBESToEST() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(new FileInputStream(getTestDataFolder() + "signed-bes.pdf"), createContext());

        // upgrade signature
        Signature signature = signatureContainer.getSignatures().get(0);
        signature.upgrade(SignatureType.ES_T);

        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-bes-to-est.pdf"));
    }

    @Test
    public void validateEST() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-est.pdf"), createContext());

        ContainerValidationResult validationResult = signatureContainer.verifyAll();
        System.out.println(validationResult);
        assert validationResult.getResultType() == ContainerValidationResultType.ALL_VALID;
    }

    @Test
    public void validateEST2() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-bes-to-est.pdf"), createContext());

        ContainerValidationResult validationResult = signatureContainer.verifyAll();
        System.out.println(validationResult);
        assert validationResult.getResultType() == ContainerValidationResultType.ALL_VALID;
    }

    @Test
    public void createLT() throws Exception {
        // read
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
    public void createLTA() throws Exception {
        // read
        PAdESContext context = createContext();
        context.setSignWithTimestamp(true);
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestFile()), context);

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

        // add signature
        Signature signature = signatureContainer.createSignature(eCertificate);
        signature.setSigningTime(Calendar.getInstance());
        signature.sign(signer);

        // add timestamp
        signature.upgrade(SignatureType.ES_A);
        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-lta.pdf"));
    }

    @Test
    public void upgradeMiddle() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-seq.pdf"), createContext());

        // add signature
        Signature first = signatureContainer.getSignatures().get(0);

        // add timestamp
        Exception exception = null;
        try {
            first.upgrade(SignatureType.ES_A);
        } catch (Exception x) {
            exception = x;
        }
        Assert.assertTrue("Cant upgrade signature if not last one", exception instanceof NotSupportedException);
    }

    @Test
    public void validateLT() throws Exception {
        // read
        Context context = createContext();
        context.getConfig().getCertificateValidationConfig().setUseValidationDataPublishedAfterCreation(false);
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-lt.pdf"), createContext());

        ContainerValidationResult validationResult = signatureContainer.verifyAll();
        System.out.println(validationResult);
        Assert.assertEquals(ContainerValidationResultType.ALL_VALID, validationResult.getResultType());
    }

    @Test
    public void validateLTA() throws Exception {
        // read
        Context context = createContext();
        context.getConfig().getCertificateValidationConfig().setUseValidationDataPublishedAfterCreation(false);
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-lta.pdf"), createContext());

        ContainerValidationResult validationResult = signatureContainer.verifyAll();
        System.out.println(validationResult);
        Assert.assertEquals(validationResult.getResultType(), ContainerValidationResultType.ALL_VALID);
    }

    @Test
    public void createLTAA() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestFile()), createContext());

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);

        // add signature
        Signature signature = signatureContainer.createSignature(eCertificate);
        signature.sign(signer);

        // add timestamp
        signature.upgrade(SignatureType.ES_A);

        // get last signature which is timestamp
        signature = signatureContainer.getSignatures().get(signatureContainer.getSignatures().size() - 1);
        signature.upgrade(SignatureType.ES_A);
        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "signed-lta-a.pdf"));
    }

    @Test
    public void validateLTAA() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(SignatureFormat.PAdES,
                new FileInputStream(getTestDataFolder() + "signed-lta-a.pdf"), createContext());

        ContainerValidationResult validationResult = signatureContainer.verifyAll();
        System.out.println(validationResult);
        Assert.assertEquals(validationResult.getResultType(), ContainerValidationResultType.ALL_VALID);
    }

}
