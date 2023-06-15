package tr.gov.tubitak.uekae.esya.api.certificate.example;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.SampleBase;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.CertificateValidation;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.ValidationSystem;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;

import java.util.List;

public class TrustedCertTests extends SampleBase {

    private static String POLICY_FILE_NES = getRootDir() + "/config/certval-policy-test.xml";
    private static String POLICY_FILE_MM = getRootDir() + "/config/certval-policy-malimuhur.xml";

    @Test
    public void testListTrustedCertsForNES() throws Exception{
        listTrustedCerts(POLICY_FILE_NES);
    }

    @Test
    public void testListTrustedCertsForMM() throws Exception{
        listTrustedCerts(POLICY_FILE_MM);
    }

    public void listTrustedCerts(String policyFile) throws Exception{

        ValidationPolicy policy = PolicyReader.readValidationPolicy(policyFile);
        ValidationSystem validationSystem = CertificateValidation.createValidationSystem(policy);

        validationSystem.getFindSystem().findTrustedCertificates();
        List<ECertificate> trustedCertificates = validationSystem.getFindSystem().getTrustedCertificates();

        System.out.println("Toplam Güvenilir Sertifika Adedi: " + trustedCertificates.size());
        for (ECertificate aCert: trustedCertificates ) {
            System.out.println(aCert.toString());
        }
    }

}
