package tr.gov.tubitak.uekae.esya.api.cades.example.pfx;

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
import tr.gov.tubitak.uekae.esya.api.common.util.bag.Pair;
import tr.gov.tubitak.uekae.esya.api.crypto.Crypto;
import tr.gov.tubitak.uekae.esya.api.crypto.Signer;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.SignatureAlg;
import tr.gov.tubitak.uekae.esya.api.crypto.util.PfxParser;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.io.FileInputStream;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class PfxSignTest extends CadesSampleBase {

    private static final String FilePath = getRootDir() + "/sertifika deposu/test1@test.com_745418.pfx";
    private static final String PFX_PIN = "745418";

    @Test
    public void testBESSign() throws Exception {
        BaseSignedData baseSignedData = new BaseSignedData();
        ISignable content = new SignableByteArray("test".getBytes());
        baseSignedData.addContent(content);

        HashMap<String, Object> params = new HashMap<String, Object>();

        //if the user does not want certificate validation at generating signature,he can add
        //P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false
        //params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);

        //necessary for certificate validation.By default,certificate validation is done
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        ECertificate cert = getCertificateFromPFX();
        BaseSigner signer = getSignerFromPFX();

        baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);
        byte[] signedDocument = baseSignedData.getEncoded();

        //write the contentinfo to file
        AsnIO.dosyayaz(signedDocument, getTestDataFolder() + "BES-Pfx.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(signedDocument, null);
        System.out.println(validationResult);

        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    public ECertificate getCertificateFromPFX() throws Exception {
        //Pfx okunuyor.
        FileInputStream fis = new FileInputStream(FilePath);
        PfxParser pfxParser = new PfxParser(fis, PFX_PIN.toCharArray());
        List<Pair<ECertificate, PrivateKey>> entries = pfxParser.getCertificatesAndKeys();
        return entries.get(0).getObject1();
    }

    public Signer getSignerFromPFX() throws Exception {
        //Pfx okunuyor.
        FileInputStream fis = new FileInputStream(FilePath);
        PfxParser pfxParser = new PfxParser(fis, PFX_PIN.toCharArray());
        List<Pair<ECertificate, PrivateKey>> entries = pfxParser.getCertificatesAndKeys();

        Signer signer = Crypto.getSigner(SignatureAlg.RSA_SHA256);
        signer.init(entries.get(0).getObject2());
        return signer;
    }
}
