package tr.gov.tubitak.uekae.esya.api.cades.example.removesignature;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import static junit.framework.TestCase.assertEquals;

/**
 * Removes signature.
 * Firstly run sign operations in order to create signatures to be removed.
 */
public class RemoveSignature extends CadesSampleBase {

    /**
     * Removes the first counter signature and remains no signer.
     *
     * @throws Exception
     */
    @Test
    public void testRemoveAll() throws Exception {
        byte[] content = AsnIO.dosyadanOKU(getTestDataFolder() + "counterSignatures.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(content);
        baseSignedData.getSignerList().get(0).remove();
        byte[] noSign = baseSignedData.getEncoded();

        BaseSignedData removedBsd = new BaseSignedData(noSign);
        assertEquals(0, removedBsd.getAllSigners().size());
    }

    /**
     * Removes the second counter signature and remains one signer.
     *
     * @throws Exception
     */
    @Test
    public void testKeepOne() throws Exception {
        byte[] content = AsnIO.dosyadanOKU(getTestDataFolder() + "counterSignatures.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(content);
        baseSignedData.getSignerList().get(0).getCounterSigners().get(0).remove();
        byte[] noSign = baseSignedData.getEncoded();

        BaseSignedData removedBsd = new BaseSignedData(noSign);
        assertEquals(1, removedBsd.getAllSigners().size());

    }

    /**
     * Removes the third counter signature and remains two signer.
     *
     * @throws Exception
     */
    @Test
    public void testKeepTwo() throws Exception {
        byte[] content = AsnIO.dosyadanOKU(getTestDataFolder() + "counterSignatures.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(content);

        baseSignedData.getSignerList().get(0).getCounterSigners().get(0).getCounterSigners().get(0).remove();
        byte[] noSign = baseSignedData.getEncoded();

        BaseSignedData removedBsd = new BaseSignedData(noSign);
        assertEquals(2, removedBsd.getAllSigners().size());
    }

    /**
     * Removes the fourth counter signature and remains three signer.
     *
     * @throws Exception
     */
    @Test
    public void testKeepThree() throws Exception {
        byte[] content = AsnIO.dosyadanOKU(getTestDataFolder() + "counterSignatures.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(content);
        baseSignedData.getSignerList().get(0).getCounterSigners().get(0).getCounterSigners().get(0).getCounterSigners().get(0).remove();
        byte[] noSign = baseSignedData.getEncoded();

        BaseSignedData removedBsd = new BaseSignedData(noSign);
        assertEquals(3, removedBsd.getAllSigners().size());
    }

}
