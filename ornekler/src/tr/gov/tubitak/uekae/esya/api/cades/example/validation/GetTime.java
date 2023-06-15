package tr.gov.tubitak.uekae.esya.api.cades.example.validation;

import org.junit.Assert;
import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.cms.EAttribute;
import tr.gov.tubitak.uekae.esya.api.asn.pkixtsp.ETSTInfo;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ETime;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.AttributeOIDs;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.EST;
import tr.gov.tubitak.uekae.esya.api.signature.attribute.TimestampInfo;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import java.util.Calendar;
import java.util.List;

/**
 * Get times from signature.
 */
public class GetTime extends CadesSampleBase {
    /**
     * Gets signature time stamp. It indicates when the sign was created.
     *
     * @throws Exception
     */
    @Test
    public void testSignatureTS() throws Exception {

        byte[] input = AsnIO.dosyadanOKU(getTestDataFolder() + "EST-1.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(input);
        EST estSign = (EST) baseSignedData.getSignerList().get(0);
        Calendar time = estSign.getTime();
        System.out.println(time.getTime().toString());
    }

    /**
     * Gets signing time attribute time. It indicates the declared time when the signature is created.
     *
     * @throws Exception
     */
    @Test
    public void testSigningTme() throws Exception {

        byte[] input = AsnIO.dosyadanOKU(getTestDataFolder() + "BES-2.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(input);
        List<EAttribute> attrs = baseSignedData.getSignerList().get(0).getSignedAttribute(AttributeOIDs.id_signingTime);
        ETime time = new ETime(attrs.get(0).getValue(0));
        System.out.println(time.getTime().getTime().toString());
    }

    /**
     * Gets archive time stamp. It indicated then signature is converted to ESA.
     *
     * @throws Exception
     */
    @Test
    public void testarchiveTimestamp() throws Exception {

        byte[] input = AsnIO.dosyadanOKU(getTestDataFolder() + "ESA-Converted-1.p7s");
        BaseSignedData baseSignedData = new BaseSignedData(input);
        List<TimestampInfo> timestampInfos = baseSignedData.getSignerList().get(0).getAllArchiveTimeStamps();

        if (timestampInfos.size() == 0) {
            Assert.fail("Could not find ETS attributes in the provided input file");
        }

        for (TimestampInfo timestampInfo : timestampInfos) {
            ETSTInfo tstInfo = timestampInfo.getTSTInfo();
            System.out.println(tstInfo.getTime().getTime().toString());
        }
    }
}
