package tr.gov.tubitak.uekae.esya.api.cades.example.sign;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cades.example.validation.CadesSignatureValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

/**
 * Creates content info structures that has several paralel signatures
 */
public class ParallelSign extends CadesSampleBase {

    public final int SIGNATURE_COUNT = 5;

    /**
     * Creates a signature structure that has two different signers. Each signer has signatures as much as SIGNATURE_COUNT
     * variable.
     *
     * @throws Exception
     */
    @Test
    public void testParallelSign() throws Exception {

        boolean checkQCStatement = isQualified();

        //Wants user to select two cards for parallel signatures.
        System.out.println("Select card - 1");
        //SmartCard - 1
        SmartCardManager scm1 = SmartCardManager.getInstance();
        //Get Non-Qualified certificate.
        ECertificate cert1 = scm1.getSignatureCertificate(checkQCStatement);
        BaseSigner signer1 = scm1.getSigner(getPin(), cert1);

        //reset to select second card.
        SmartCardManager.reset();

        System.out.println("Select card - 2");
        //SmartCard - 2
        SmartCardManager scm2 = SmartCardManager.getInstance();
        //Get Non-Qualified certificate.
        ECertificate cert2 = scm2.getSignatureCertificate(checkQCStatement);
        BaseSigner signer2 = scm2.getSigner(getPin(), cert2);

        SmartCardManager.reset();

        byte[] signature = null;
        byte[] content = "test".getBytes("ASCII");
        for (int i = 0; i < SIGNATURE_COUNT; i++) {
            //adds a paralel signature of signer one
            signature = sign(content, signature, signer1, cert1);
            //adds a paralel signature of signer two
            signature = sign(content, signature, signer2, cert2);
        }

        try {
            scm1.logout();
            scm2.logout();
        } catch (SmartCardException sce) {
            System.out.println(sce);
        }

        AsnIO.dosyayaz(signature, getTestDataFolder() + "paralelSignatures.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(signature, null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    /**
     * adds a parallel signature to a signature structure.
     *
     * @param content
     * @param sign
     * @param signer
     * @param cert
     * @return
     * @throws Exception
     */
    private byte[] sign(byte[] content, byte[] sign, BaseSigner signer, ECertificate cert) throws Exception {

        BaseSignedData baseSignedData;

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        //If sign is null, there is no signature before.
        if (sign == null) {
            baseSignedData = new BaseSignedData();
            baseSignedData.addContent(new SignableByteArray(content));
        } else {
            baseSignedData = new BaseSignedData(sign);
        }

        //add signer
        baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

        //returns content info
        return baseSignedData.getEncoded();
    }

}

