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
import tr.gov.tubitak.uekae.esya.api.infra.tsclient.TSSettings;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class ESTSign extends CadesSampleBase {

    /**
     * creates EST type signature and validate it.
     *
     * @throws Exception
     */
    @Test
    public void testEstSign() throws Exception {

        BaseSignedData baseSignedData = new BaseSignedData();

        ISignable content = new SignableByteArray("test".getBytes());
        baseSignedData.addContent(content);

        HashMap<String, Object> params = new HashMap<String, Object>();

        //if the user does not want certificate validation,he can add
        //P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false

        //if the user want to do timestamp validation while generating signature,he can add
        //P_VALIDATE_TIMESTAMP_WHILE_SIGNING parameter with its value set to true
        //params.put(EParameters.P_VALIDATE_TIMESTAMP_WHILE_SIGNING, true);
        boolean checkQCStatement = isQualified();
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        //necessary for getting signature time stamp.
        //for getting test TimeStamp or qualified TimeStamp account, mail to bilgi@kamusm.gov.tr
        TSSettings tsSettings = getTSSettings();
        params.put(EParameters.P_TSS_INFO, tsSettings);

        //Get qualified or non-qualified certificate.
        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        //add signer
        baseSignedData.addSigner(ESignatureType.TYPE_EST, cert, signer, null, params);

        SmartCardManager.getInstance().logout();

        byte[] signedDocument = baseSignedData.getEncoded();

        AsnIO.dosyayaz(signedDocument, getTestDataFolder() + "EST-1.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(signedDocument, null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }
}
