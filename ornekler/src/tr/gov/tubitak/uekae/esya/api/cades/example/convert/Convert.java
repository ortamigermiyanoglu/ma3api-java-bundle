package tr.gov.tubitak.uekae.esya.api.cades.example.convert;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cades.example.validation.CadesSignatureValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableFile;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

/**
 * Conversion to ESA. ESA signature can not be created directly. They must be converted from other signature types.
 * Firstly run sign operations in order to create signatures to be converted.
 */
public class Convert extends CadesSampleBase {

    private String docFile = "D:\\Docs\\MA3API.docx";
    private String movieFile = "D:\\Movie\\DocumentaryMovie.mkv";

    private String signatureofHugeFile = getTestDataFolder() + "HugeExternalContent.p7s";
    private String signatureofSmallFile = getTestDataFolder() + "SmallExternalContent.p7s";

    /**
     * Converting BES signature to ESA
     *
     * @throws Exception
     */
    @Test
    public void testConvertBES_1() throws Exception {

        byte[] signatureFile = AsnIO.dosyadanOKU(getTestDataFolder() + "BES-1.p7s");

        BaseSignedData baseSignedData = new BaseSignedData(signatureFile);

        Map<String, Object> parameters = new HashMap<String, Object>();

        //Time stamp will be needed.
        parameters.put(EParameters.P_TSS_INFO, getTSSettings());

        parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        baseSignedData.getSignerList().get(0).convert(ESignatureType.TYPE_EST, parameters);

        AsnIO.dosyayaz(baseSignedData.getEncoded(), getTestDataFolder() + "EST-Converted.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(baseSignedData.getEncoded(), null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    /**
     * Converting BES signature to ESA
     *
     * @throws Exception
     */
    @Test
    public void testConvertEST() throws Exception {

        byte[] signatureFile = AsnIO.dosyadanOKU(getTestDataFolder() + "EST-Converted.p7s");

        BaseSignedData baseSignedData = new BaseSignedData(signatureFile);

        Map<String, Object> parameters = new HashMap<String, Object>();

        //Time stamp will be needed.
        parameters.put(EParameters.P_TSS_INFO, getTSSettings());

        parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        baseSignedData.getSignerList().get(0).convert(ESignatureType.TYPE_ESXLong, parameters);

        AsnIO.dosyayaz(baseSignedData.getEncoded(), getTestDataFolder() + "XLONG-Converted.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(baseSignedData.getEncoded(), null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    /**
     * Converting XLong signature to ESA.
     *
     * @throws Exception
     */
    @Test
    public void testConvertExternalXLong_2() throws Exception {

        byte[] signatureFile = AsnIO.dosyadanOKU(getTestDataFolder() + "ESXLong-1.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(signatureFile);

        Map<String, Object> parameters = new HashMap<String, Object>();

        //Archive time stamp is added to signature, so time stamp settings are needed.
        parameters.put(EParameters.P_TSS_INFO, getTSSettings());
        parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        baseSignedData.getSignerList().get(0).convert(ESignatureType.TYPE_ESA, parameters);

        AsnIO.dosyayaz(baseSignedData.getEncoded(), getTestDataFolder() + "ESA-Converted-1.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(baseSignedData.getEncoded(), null);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    /**
     * Converting external signature to ESA.
     *
     * @throws Exception
     */
    @Test
    public void testConvertExternalContentSignature_3() throws Exception {

        File file = new File(docFile);
        ISignable signable = new SignableFile(file, 2048);

        byte[] content = AsnIO.dosyadanOKU(signatureofSmallFile);
        BaseSignedData baseSignedData = new BaseSignedData(content);

        Map<String, Object> parameters = new HashMap<String, Object>();

        //Archive time stamp is added to signature, so time stamp settings are needed.
        parameters.put(EParameters.P_TSS_INFO, getTSSettings());

        parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());
        parameters.put(EParameters.P_EXTERNAL_CONTENT, signable);

        baseSignedData.getSignerList().get(0).convert(ESignatureType.TYPE_ESA, parameters);

        AsnIO.dosyayaz(baseSignedData.getEncoded(), getTestDataFolder() + "ESA-Converted-2.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(baseSignedData.getEncoded(), signable);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    /**
     * Converting external signature of a huge file to ESA.
     *
     * @throws Exception
     */
    @Test
    public void testConvertHugeExternalContentSignature_4() throws Exception {

        File file = new File(movieFile);
        ISignable signable = new SignableFile(file, 2048);

        byte[] content = AsnIO.dosyadanOKU(signatureofHugeFile);
        BaseSignedData baseSignedData = new BaseSignedData(content);

        Map<String, Object> parameters = new HashMap<String, Object>();

        //Archive time stamp is added to signature, so time stamp settings are needed.
        parameters.put(EParameters.P_TSS_INFO, getTSSettings());

        parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());
        parameters.put(EParameters.P_EXTERNAL_CONTENT, signable);

        baseSignedData.getSignerList().get(0).convert(ESignatureType.TYPE_ESA, parameters);

        AsnIO.dosyayaz(baseSignedData.getEncoded(), getTestDataFolder() + "ESA-Converted-3.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(baseSignedData.getEncoded(), signable);
        validationResult.printDetails();
        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    @Test
    public void testConvertToESASerialSignatures() throws Exception {

        byte[] signatureFile = AsnIO.dosyadanOKU(getTestDataFolder() + "counterSignatures.p7s");

        BaseSignedData baseSignedData = new BaseSignedData(signatureFile);

        // To keep long years convert signatures to XLong type.
        List<Signer> allSigners = baseSignedData.getAllSigners();
        for (Signer signer : allSigners) {
            Map<String, Object> parameters = new HashMap<String, Object>();

            parameters.put(EParameters.P_TSS_INFO, getTSSettings());
            parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

            signer.convert(ESignatureType.TYPE_ESXLong, parameters);
        }

        //It is sufficient to convert to ESA first level parallel signatures.
        List<Signer> allParallelSigners = baseSignedData.getSignerList();
        for (Signer signer : allParallelSigners) {
            Map<String, Object> parameters = new HashMap<String, Object>();

            //Archive time stamp is added to signature, so time stamp settings are needed.
            parameters.put(EParameters.P_TSS_INFO, getTSSettings());
            parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

            signer.convert(ESignatureType.TYPE_ESA, parameters);
        }
        AsnIO.dosyayaz(baseSignedData.getEncoded(), getTestDataFolder() + "counterSignaturesESA-converted.p7s");

    }

}
