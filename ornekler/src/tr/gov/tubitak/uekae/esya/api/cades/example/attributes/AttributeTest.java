package tr.gov.tubitak.uekae.esya.api.cades.example.attributes;

import com.objsys.asn1j.runtime.Asn1ObjectIdentifier;
import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.cms.EAttribute;
import tr.gov.tubitak.uekae.esya.api.asn.cms.EContentHints;
import tr.gov.tubitak.uekae.esya.api.asn.cms.EMimeType;
import tr.gov.tubitak.uekae.esya.api.asn.cms.ESignerLocation;
import tr.gov.tubitak.uekae.esya.api.asn.profile.TurkishESigProfile;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.*;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.infra.tsclient.TSSettings;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class AttributeTest extends CadesSampleBase {

    @Test
    public void testAttribute() throws Exception {

        BaseSignedData baseSignedData = new BaseSignedData();

        ISignable content = new SignableByteArray("test".getBytes());
        TSSettings tsSettings = getTSSettings();
        ValidationPolicy policy = getPolicy();

        //add content which will be signed
        baseSignedData.addContent(content);

        Calendar signingTimeAttr = Calendar.getInstance();

        EContentHints chAttr = new EContentHints("text/plain", new Asn1ObjectIdentifier(new int[]{1, 2, 840, 113549, 1, 7, 1}));
        EMimeType mimeTypeAttr = new EMimeType("text/plain");

        //Specified attributes are optional,add them to optional attributes list
        List<IAttribute> optionalAttributes = new ArrayList<IAttribute>();
        optionalAttributes.add(new SigningTimeAttr(signingTimeAttr));
        optionalAttributes.add(new SignerLocationAttr("TURKEY", "KOCAELİ", new String[]{"TUBITAK UEKAE", "GEBZE"}));
        optionalAttributes.add(new CommitmentTypeIndicationAttr(CommitmentType.CREATION));
        optionalAttributes.add(new ContentHintsAttr(chAttr));
        optionalAttributes.add(new ContentIdentifierAttr("PL123456789".getBytes("ASCII")));
        optionalAttributes.add(new SignaturePolicyIdentifierAttr(TurkishESigProfile.P2_1));
        optionalAttributes.add(new ContentTimeStampAttr());
        optionalAttributes.add(new MimeTypeAttr(mimeTypeAttr));


        //create parameters necessary for signature creation
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put(EParameters.P_CERT_VALIDATION_POLICY, policy);

        params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);

        //parameters for ContentTimeStamp attribute
        params.put(EParameters.P_TSS_INFO, tsSettings);

        //By default, QC statement is checked,and signature wont be created if it is not a
        //qualified certificate.
        boolean checkQCStatement = isQualified();

        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        //add signer
        baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, optionalAttributes, params);

        byte[] encoded = baseSignedData.getEncoded();

        BaseSignedData bsController = new BaseSignedData(encoded);
        Signer aSigner = bsController.getSignerList().get(0);
        List<EAttribute> attrs;

        attrs = aSigner.getAttribute(SigningTimeAttr.OID);
        Calendar st = SigningTimeAttr.toTime(attrs.get(0));
        //because of fraction, it is not exactly equal
        assertTrue((signingTimeAttr.getTimeInMillis() - st.getTimeInMillis()) < 1000);

        attrs = aSigner.getAttribute(SignerLocationAttr.OID);
        ESignerLocation sl = SignerLocationAttr.toSignerLocation(attrs.get(0));
        assertEquals("TURKEY", sl.getCountry());
        assertEquals("KOCAELİ", sl.getLocalityName());
        assertTrue(Arrays.equals(new String[]{"TUBITAK UEKAE", "GEBZE"}, sl.getPostalAddress()));

        attrs = aSigner.getAttribute(ContentHintsAttr.OID);
        EContentHints ch = ContentHintsAttr.toContentHints(attrs.get(0));
        assertTrue(ch.equals(chAttr));

        attrs = aSigner.getAttribute(MimeTypeAttr.OID);
        EMimeType mt = MimeTypeAttr.toMimeType(attrs.get(0));
        assertTrue(mt.equals(mimeTypeAttr));

        attrs = aSigner.getAttribute(ContentIdentifierAttr.OID);
        byte[] ci = ContentIdentifierAttr.toIdentifier(attrs.get(0));
        assertTrue(Arrays.equals(ci, "PL123456789".getBytes("ASCII")));

        attrs = aSigner.getAttribute(CommitmentTypeIndicationAttr.OID);
        CommitmentType ct = CommitmentTypeIndicationAttr.toCommitmentType(attrs.get(0));
        assertEquals(CommitmentType.CREATION, ct);

        attrs = aSigner.getAttribute(ContentTimeStampAttr.OID);
        Calendar cal = ContentTimeStampAttr.toTime(attrs.get(0));
        Calendar now = Calendar.getInstance();

        cal.add(Calendar.MINUTE, 10);
        assertTrue("ContentTimeStampAttr error", now.compareTo(cal) < 0);

    }
}
