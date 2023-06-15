package tr.gov.tubitak.uekae.esya.api.cades.example.validation;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.profile.TurkishESigProfile;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.Hashtable;

import static junit.framework.TestCase.assertEquals;

public class CadesSignatureValidation extends CadesSampleBase {

    @Test
    public void testValidation() throws Exception {
        byte[] input = AsnIO.dosyadanOKU(getTestDataFolder() + "ESXLong-1.p7s");

        SignedDataValidationResult validationResult = validate(input, null);

        validationResult.printDetails();

        assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
    }

    public SignedDataValidationResult validate(byte[] signature, ISignable externalContent) throws Exception {
        return validate(signature, externalContent, getPolicy(), null);
    }

    public SignedDataValidationResult validate(byte[] signature, ISignable externalContent, TurkishESigProfile turkishESigProfile) throws Exception {
        return validate(signature, externalContent, getPolicy(), turkishESigProfile);
    }

    public SignedDataValidationResult validate(byte[] signature, ISignable externalContent, ValidationPolicy policy, TurkishESigProfile turkishESigProfile) throws Exception {
        Hashtable<String, Object> params = new Hashtable<String, Object>();

        if(turkishESigProfile != null)
            params.put(EParameters.P_VALIDATION_PROFILE, turkishESigProfile);

        params.put(EParameters.P_CERT_VALIDATION_POLICY, policy);

        if (externalContent != null)
            params.put(EParameters.P_EXTERNAL_CONTENT, externalContent);

        //Use only reference and their corresponding value to validate signature
        params.put(EParameters.P_FORCE_STRICT_REFERENCE_USE, true);

        //Ignore grace period which means allow usage of CRL published before signature time
        //params.put(EParameters.P_IGNORE_GRACE, true);

        //Use multiple policies if you want to use different policies to validate different types of certificate
        //CertValidationPolicies certificateValidationPolicies = new CertValidationPolicies();
        //certificateValidationPolicies.register(CertificateType.DEFAULT.toString(), policy);
        //ValidationPolicy maliMuhurPolicy=PolicyReader.readValidationPolicy(new FileInputStream("./config/certval-policy-malimuhur.xml"));
        //certificateValidationPolicies.register(CertificateType.MaliMuhurCertificate.toString(), maliMuhurPolicy);
        //params.put(EParameters.P_CERT_VALIDATION_POLICIES, certificateValidationPolicies);

        SignedDataValidation sdv = new SignedDataValidation();
        SignedDataValidationResult validationResult = sdv.verify(signature, params);

        return validationResult;
    }

}
