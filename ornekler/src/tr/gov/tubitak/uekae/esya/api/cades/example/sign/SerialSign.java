package tr.gov.tubitak.uekae.esya.api.cades.example.sign;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cades.example.validation.CadesSignatureValidation;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Creates serial signature structures
 */
public class SerialSign extends CadesSampleBase {

    protected static Logger logger = LoggerFactory.getLogger(SerialSign.class);

    public final static int SIGNATURE_COUNT = 5;

    /**
     * Creates a signature structure that has one different signer; and validates the structure.
     *
     * @throws Exception
     */
    @Test
    public void testSignInLoop() throws Exception {

        SmartCardManager smartCardManager = SmartCardManager.getInstance();

        ECertificate cert = smartCardManager.getSignatureCertificate(isQualified());
        BaseSigner signer = smartCardManager.getSigner(getPin(), cert);

        byte[] lastSign = singInLoop(cert, signer);

        smartCardManager.logout();

        AsnIO.dosyayaz(lastSign, getTestDataFolder() + "counterSignatures.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();

        SignedDataValidationResult validationResult = signatureValidation.validate(lastSign, null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    /**
     * Creates a signature structure that has two different signers; and validates the structure.
     *
     * @throws Exception
     */
    @Test
    public void testSignInLoopWith2Signer() throws Exception {

        boolean checkQCStatement = isQualified();

        System.out.println("Select card - 1");

        SmartCardManager.reset();

        SmartCardManager scm1 = SmartCardManager.getInstance();

        ECertificate cert1 = scm1.getSignatureCertificate(checkQCStatement);
        BaseSigner signer1 = scm1.getSigner(getPin(), cert1);

        SmartCardManager.reset();

        System.out.println("Select card - 2");

        SmartCardManager scm2 = SmartCardManager.getInstance();

        ECertificate cert2 = scm2.getSignatureCertificate(checkQCStatement);
        BaseSigner signer2 = scm2.getSigner(getPin(), cert2);

        SmartCardManager.reset();

        byte[] lastSign = singInLoopWith2Signer(cert1, signer1, cert2, signer2);

        try {
            scm1.logout();
            scm2.logout();
        } catch (SmartCardException sce) {
            logger.error("Error in SerialSign", sce);
        }

        AsnIO.dosyayaz(lastSign, getTestDataFolder() + "counterSignatures2.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(lastSign, null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());

    }

    /***
     * Creates signature structure that has one counter signature in every level.
     * @param cert
     * @param signer
     * @return
     */
    private byte[] singInLoop(ECertificate cert, BaseSigner signer) {

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();

            ValidationPolicy validationPolicy = getPolicy();
            params.put(EParameters.P_CERT_VALIDATION_POLICY, validationPolicy);
            boolean validateCertificate = false;
            params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, validateCertificate);

            byte[] content = "test".getBytes();
            byte[] sign = null;

            for (int i = 0; i < SIGNATURE_COUNT; i++) {
                BaseSignedData baseSignedData;
                List<Signer> signerList = null;

                if (sign == null) {
                    // there is no signature, add content
                    baseSignedData = new BaseSignedData();
                    baseSignedData.addContent(new SignableByteArray(content));
                } else {
                    // there are signatures, get signer list.
                    baseSignedData = new BaseSignedData(sign);
                    signerList = baseSignedData.getSignerList();
                }

                if (signerList != null && signerList.size() > 0) {
                    //find last counter signature of the last signature
                    Signer lastSigner = signerList.get(signerList.size() - 1);
                    while (lastSigner.getCounterSigners().size() > 0) {
                        lastSigner = lastSigner.getCounterSigners().get(lastSigner.getCounterSigners().size() - 1);
                    }
                    lastSigner.addCounterSigner(ESignatureType.TYPE_BES, cert, signer, null, params);
                } else {
                    //add first signature.
                    baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);
                }

                sign = baseSignedData.getEncoded();
            }

            return sign;
        } catch (Exception e) {
            logger.error("Error in SerialSign", e);
        }
        return null;

    }

    /**
     * Creates signature structure that has two counter signatures in every level.
     *
     * @param cert1
     * @param signer1
     * @param cert2
     * @param signer2
     * @return
     */
    private byte[] singInLoopWith2Signer(ECertificate cert1, BaseSigner signer1, ECertificate cert2, BaseSigner signer2) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();

            params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

            byte[] content = "test".getBytes();
            byte[] signature = null;

            for (int i = 0; i < SIGNATURE_COUNT; i++) {
                BaseSignedData baseSignedData;
                List<Signer> signerList = null;

                if (signature == null) {
                    // there is no signature, add content
                    baseSignedData = new BaseSignedData();
                    baseSignedData.addContent(new SignableByteArray(content));
                } else {
                    // there are signatures, get signer list.
                    baseSignedData = new BaseSignedData(signature);
                    signerList = baseSignedData.getSignerList();
                }

                if (signerList != null && signerList.size() > 0) {
                    Signer lastSigner = signerList.get(signerList.size() - 1);
                    while (lastSigner.getCounterSigners().size() > 0) {
                        lastSigner = lastSigner.getCounterSigners().get(lastSigner.getCounterSigners().size() - 1);
                    }
                    lastSigner.addCounterSigner(ESignatureType.TYPE_BES, cert1, signer1, null, params);
                    lastSigner.addCounterSigner(ESignatureType.TYPE_BES, cert2, signer2, null, params);
                } else {
                    //add first level two signatures.
                    baseSignedData.addSigner(ESignatureType.TYPE_BES, cert1, signer1, null, params);
                    baseSignedData.addSigner(ESignatureType.TYPE_BES, cert2, signer2, null, params);
                }

                signature = baseSignedData.getEncoded();
            }

            return signature;
        } catch (Exception e) {
            logger.error("Error in SerialSign", e);
        }
        return null;

    }

}
