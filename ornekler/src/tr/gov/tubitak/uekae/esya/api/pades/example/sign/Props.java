package tr.gov.tubitak.uekae.esya.api.pades.example.sign;

import org.junit.Assert;
import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.pades.example.PadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.signature.attribute.TimestampInfo;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

public class Props extends PadesSampleBase {

    @Test
    public void signBES() throws Exception {
        // read
        SignatureContainer signatureContainer = SignatureFactory.readContainer(new FileInputStream(getTestFile()), createContext());

        ECertificate eCertificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), eCertificate);
        // add signature
        Signature signature = signatureContainer.createSignature(eCertificate);

        // set signing time
        Calendar signingTime = Calendar.getInstance();
        signingTime.set(Calendar.MILLISECOND, 0);
        signature.setSigningTime(signingTime);

        signature.sign(signer);
        signatureContainer.write(new FileOutputStream(getTestDataFolder() + "props.pdf"));

        // read and validate
        SignatureContainer readContainer = SignatureFactory.readContainer(new FileInputStream(getTestDataFolder() + "props.pdf"), createContext());
        Calendar signingTimeRead = readContainer.getSignatures().get(0).getSigningTime();

        assert signingTime.compareTo(signingTimeRead) == 0; // equals dont work!

        ContainerValidationResult validationResult = readContainer.verifyAll();
        System.out.println(validationResult);
        assert validationResult.getResultType() == ContainerValidationResultType.ALL_VALID;
    }

    @Test
    public void testTimestampsInfoEST() throws Exception {
        SignatureContainer signatureContainer = SignatureFactory.readContainer(new FileInputStream(getTestDataFolder() + "signed-est.pdf"), createContext());
        List<TimestampInfo> all = signatureContainer.getSignatures().get(0).getAllTimestampInfos();
        System.out.println(all);
        Assert.assertEquals(1, all.size());
    }

    @Test
    public void testTimestampsInfoESA() throws Exception {
        SignatureContainer signatureContainer = SignatureFactory.readContainer(new FileInputStream(getTestDataFolder() + "signed-lta.pdf"), createContext());
        List<TimestampInfo> all = signatureContainer.getSignatures().get(0).getAllTimestampInfos();
        System.out.println(all);
        Assert.assertEquals(2, all.size());
    }

}
