package tr.gov.tubitak.uekae.esya.api.cades.example.sign;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cades.example.validation.CadesSignatureValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableFile;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.io.File;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class ExternalContentSign extends CadesSampleBase {

    private String docFile = "D:\\Docs\\MA3API.docx";
    private String movieFile = "D:\\Movie\\DocumentaryMovie.mkv";

    /**
     * creates BES type signature with normal size external content and validate it.
     *
     * @throws Exception
     */
    @Test
    public void testSignSmallFile() throws Exception {

        BaseSignedData baseSignedData = new BaseSignedData();

        File file = new File(docFile);
        ISignable externalContent = new SignableFile(file, 32 * 1024);

        //create parameters necessary for signature creation
        HashMap<String, Object> params = new HashMap<String, Object>();

        //necessary for certificate validation.By default,certificate validation is done.But if the user
        //does not want certificate validation,he can add P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        //By default, QC statement is checked,and signature wont be created if it is not a qualified certificate
        boolean checkQCStatement = isQualified();
        baseSignedData.addContent(externalContent, false);

        //Get qualified or non-qualified certificate.
        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        //add signer
        //Since the specified attributes are mandatory for bes,null is given as parameter for optional attributes
        baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

        SmartCardManager.getInstance().logout();

        byte[] signature = baseSignedData.getEncoded();

        //write the contentinfo to file
        AsnIO.dosyayaz(signature, getTestDataFolder() + "SmallExternalContent.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(signature, externalContent);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    /**
     * creates BES type signature with huge external content and validate it. Use external signature for huge files.
     *
     * @throws Exception
     */
    @Test
    public void testSignHugeFile() throws Exception {

        BaseSignedData baseSignedData = new BaseSignedData();

        File file = new File(movieFile);
        ISignable externalContent = new SignableFile(file, 32 * 1024);

        //create parameters necessary for signature creation
        HashMap<String, Object> params = new HashMap<String, Object>();

        //necessary for certificate validation.By default,certificate validation is done.But if the user
        //does not want certificate validation,he can add P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        //By default, QC statement is checked,and signature wont be created if it is not a qualified certificate
        boolean checkQCStatement = isQualified();
        params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);

        baseSignedData.addContent(externalContent, false);

        //Get qualified or non-qualified certificate.
        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        //add signer
        //Since the specified attributes are mandatory for bes,null is given as parameter for optional attributes
        baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

        SmartCardManager.getInstance().logout();

        byte[] signature = baseSignedData.getEncoded();

        //write the contentinfo to file
        AsnIO.dosyayaz(signature, getTestDataFolder() + "HugeExternalContent.p7s");

//		SignedDataValidationResult validationResult = XadesSignatureValidation.validate(signature, externalContent);
//		validationResult.printDetails();
//		assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    @Test
    public void testAddingParalelSignature() throws Exception {

        byte[] signature = AsnIO.dosyadanOKU(getTestDataFolder() + "SmallExternalContent.p7s");

        File file = new File(docFile);
        ISignable externalContent = new SignableFile(file, 2048);

        BaseSignedData baseSignedData = new BaseSignedData(signature);
        HashMap<String, Object> params = new HashMap<String, Object>();

        //necessary for certificate validation.By default,certificate validation is done.But if the user
        //does not want certificate validation,he can add P_VALIDATE_CERTIFICATE_BEFORE_SIGNING parameter with its value set to false
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        //By default, QC statement is checked,and signature wont be created if it is not a qualified certificate
        boolean checkQCStatement = isQualified();
        params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);
        params.put(EParameters.P_EXTERNAL_CONTENT, externalContent);

        //Get qualified or non-qualified certificate.
        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        //add signer
        //Since the specified attributes are mandatory for bes,null is given as parameter for optional attributes
        baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

        SmartCardManager.getInstance().logout();

        byte[] signatureWithTwoSigner = baseSignedData.getEncoded();

        AsnIO.dosyayaz(signatureWithTwoSigner, getTestDataFolder() + "twoSigner.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(signature, externalContent);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

}
