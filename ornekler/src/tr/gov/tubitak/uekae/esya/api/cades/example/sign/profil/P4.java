package tr.gov.tubitak.uekae.esya.api.cades.example.sign.profil;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.profile.TurkishESigProfile;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cades.example.validation.CadesSignatureValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.IAttribute;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.SignaturePolicyIdentifierAttr;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.SigningTimeAttr;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.infra.tsclient.TSSettings;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class P4 extends CadesSampleBase {

    @Test
    public void testCreateP4() throws Exception {

        BaseSignedData baseSignedData = new BaseSignedData();
        ISignable content = new SignableByteArray("test".getBytes());
        baseSignedData.addContent(content);

        List<IAttribute> optionalAttributes = new ArrayList<IAttribute>();
        optionalAttributes.add(new SigningTimeAttr(Calendar.getInstance()));

        optionalAttributes.add(new SignaturePolicyIdentifierAttr(TurkishESigProfile.P4_1));

        HashMap<String, Object> params = new HashMap<String, Object>();

        //if the user does not want certificate validation at generating signature,he can add
        //P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false
        //params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);

        //necessary for certificate validation.By default,certificate validation is done
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        //By default, QC statement is checked,and signature wont be created if it is not a
        //qualified certificate.
        boolean checkQCStatement = isQualified();

        //necessary for getting signature time stamp.
        TSSettings tsSettings = getTSSettings();
        params.put(EParameters.P_TSS_INFO, tsSettings);

        //Get qualified or non-qualified certificate.
        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        //add signer
        //Since the specified attributes are mandatory for bes,null is given as parameter
        //for optional attributes
        baseSignedData.addSigner(ESignatureType.TYPE_ESXLong, cert, signer, optionalAttributes, params);

        SmartCardManager.getInstance().logout();

        byte[] signedDocument = baseSignedData.getEncoded();

        //write the contentinfo to file
        AsnIO.dosyayaz(signedDocument, getTestDataFolder() + "p4.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(signedDocument, null);
        System.out.println(validationResult);

        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }
}
