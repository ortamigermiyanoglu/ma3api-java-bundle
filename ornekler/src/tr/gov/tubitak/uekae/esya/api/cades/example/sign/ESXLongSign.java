package tr.gov.tubitak.uekae.esya.api.cades.example.sign;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cades.example.validation.CadesSignatureValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class ESXLongSign extends CadesSampleBase {

    /**
     * creates ESXLong type signature and validate it.
     *
     * @throws Exception
     */
    @Test
    public void testEsxlongSign() throws Exception {

        BaseSignedData baseSignedData = new BaseSignedData();

        ISignable content = new SignableByteArray("test".getBytes());
        baseSignedData.addContent(content);

        HashMap<String, Object> params = new HashMap<String, Object>();

        //if you are using test certificates,without QCStatement,you must set P_CHECK_QC_STATEMENT to false.
        //By default,it is true
        boolean checkQCStatement = isQualified();

        //necessary for getting signaturetimestamp
        params.put(EParameters.P_TSS_INFO, getTSSettings());

        //necessary for validation of signer certificate according to time in signaturetimestamp attribute
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        //Get qualified or non-qualified certificate.
        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        //add signer
        baseSignedData.addSigner(ESignatureType.TYPE_ESXLong, cert, signer, null, params);

        SmartCardManager.getInstance().logout();

        byte[] signedDocument = baseSignedData.getEncoded();

        AsnIO.dosyayaz(signedDocument, getTestDataFolder() + "ESXLong-1.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(signedDocument, null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }
}
