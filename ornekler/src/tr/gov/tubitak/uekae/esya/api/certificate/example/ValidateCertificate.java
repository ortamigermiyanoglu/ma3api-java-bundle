package tr.gov.tubitak.uekae.esya.api.certificate.example;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.CertificateStatus;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.CertificateValidation;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.ValidationSystem;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.check.certificate.CertificateStatusInfo;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.FileInputStream;
import java.util.Calendar;

import static junit.framework.TestCase.assertEquals;

public class ValidateCertificate {

    private static String POLICY_FILE_NES = "./config/certval-policy.xml";
    private static String POLICY_FILE_MM = "./config/certval-policy-malimuhur.xml";

    public ValidationPolicy getPolicy(String fileName) throws Exception {
        return PolicyReader.readValidationPolicy(new FileInputStream(fileName));
    }

    @Test
    public void testValidateNESCertificate() throws Exception {

        boolean QCStatement = true; //Qualified certificate		
        validateCertificate(QCStatement, POLICY_FILE_NES);
    }

    @Test
    public void testValidateMMCertificate() throws Exception {

        boolean QCStatement = false; //Unqualified certificate
        validateCertificate(QCStatement, POLICY_FILE_MM);
    }

    private void validateCertificate(boolean QCStatement, String policyFileNes) throws Exception {

        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(QCStatement);

        ValidationSystem validationSystem = CertificateValidation.createValidationSystem(getPolicy(policyFileNes));
        validationSystem.setBaseValidationTime(Calendar.getInstance());
        CertificateStatusInfo statusInfo = CertificateValidation.validateCertificate(validationSystem, cert);
        statusInfo.printDetailedValidationReport();
        assertEquals("Not Verified", CertificateStatus.VALID, statusInfo.getCertificateStatus());
    }
}
