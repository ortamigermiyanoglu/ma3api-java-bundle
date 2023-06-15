package tr.gov.tubitak.uekae.esya.api.cades.example.validation;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.AllEParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.CertificateRevocationInfoCollector;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignatureValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignatureValidator;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.Hashtable;
import java.util.List;

public class ValidateOnlyOneSignature extends CadesSampleBase {

    @Test
    public void testValidateOnlyASignature() throws Exception {

        byte[] sign = AsnIO.dosyadanOKU(getTestDataFolder() + "counterSignatures.p7s");

        Hashtable<String, Object> params = new Hashtable<String, Object>();
        params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        BaseSignedData baseSignedData = new BaseSignedData(sign);

        CertificateRevocationInfoCollector collector = new CertificateRevocationInfoCollector();
        collector._extractAll(baseSignedData.getSignedData(), params);

        List<Signer> signerList = baseSignedData.getSignerList();

        for (Signer signer : signerList) {
            SignatureValidator validator = new SignatureValidator(sign);
            validator.setCertificates(collector.getAllCertificates());
            validator.setCRLs(collector.getAllCRLs());
            validator.setOCSPs(collector.getAllBasicOCSPResponses());
            SignatureValidationResult validationResult = new SignatureValidationResult();
            params.put(AllEParameters.P_PARENT_SIGNER_INFO, signer.getSignerInfo());
            validator.verify(validationResult, signer.getCounterSigners().get(0), true, params);

            System.out.println(validationResult);
        }

    }
}
