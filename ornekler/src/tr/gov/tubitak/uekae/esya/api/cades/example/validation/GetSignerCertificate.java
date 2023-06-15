package tr.gov.tubitak.uekae.esya.api.cades.example.validation;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

/**
 * Gets signer certificate from BasedSignedData
 */
public class GetSignerCertificate extends CadesSampleBase {

    /***
     * Gets certificate of the first signature.
     * @throws Exception
     */
    @Test
    public void testGetCertificate() throws Exception {

        byte[] sign = AsnIO.dosyadanOKU(getTestDataFolder() + "BES-1.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(sign);
        System.out.println("Certificate Owner Name: " + baseSignedData.getSignerList().get(0).getSignerCertificate().getSubject().getCommonNameAttribute());
        System.out.println("Certificate Owner TC Kimlik No: " + baseSignedData.getSignerList().get(0).getSignerCertificate().getSubject().getSerialNumberAttribute());
    }
}
