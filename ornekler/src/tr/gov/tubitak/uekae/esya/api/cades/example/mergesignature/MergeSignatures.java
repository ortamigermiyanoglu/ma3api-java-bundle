package tr.gov.tubitak.uekae.esya.api.cades.example.mergesignature;

import com.objsys.asn1j.runtime.Asn1DerDecodeBuffer;
import com.objsys.asn1j.runtime.Asn1OctetString;
import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.cms.EAttribute;
import tr.gov.tubitak.uekae.esya.api.asn.cms.ESignerInfo;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cmssignature.CMSSignatureException;
import tr.gov.tubitak.uekae.esya.api.cmssignature.CMSSignatureUtil;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.MessageDigestAttr;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;
import tr.gov.tubitak.uekae.esya.api.crypto.exceptions.CryptoException;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Merges two parallel signature.
 */
public class MergeSignatures extends CadesSampleBase {

    @Test
    public void testCombineTwoSignatures() throws Exception {

        //First Signature
        BaseSignedData firstBaseSignedData = new BaseSignedData();
        ISignable content = new SignableByteArray("test".getBytes());
        firstBaseSignedData.addContent(content);

        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);
        boolean checkQCStatement = isQualified();

        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(checkQCStatement);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(getPin(), cert);

        firstBaseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

        //Second Signature
        BaseSignedData secondBaseSignedData = new BaseSignedData();
        secondBaseSignedData.addContent(content);
        secondBaseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);
        SmartCardManager.getInstance().logout();

        //Merge Signatures
        BaseSignedData[] toBeMerged = new BaseSignedData[]{firstBaseSignedData, secondBaseSignedData};
        BaseSignedData mergedSignature = mergeSignatures(toBeMerged, content);
        System.out.println(mergedSignature.getAllSigners().size());
    }

    private BaseSignedData mergeSignatures(BaseSignedData[] toBeMerged, ISignable content) throws CMSSignatureException {

        BaseSignedData combined = new BaseSignedData(toBeMerged[0].getEncoded());
        for (int i = 1; i < toBeMerged.length; i++) {
            BaseSignedData baseSignedData = toBeMerged[i];
            List<Signer> signers = baseSignedData.getSignerList();

            for (Signer aSigner : signers) {
                ESignerInfo signerInfo = aSigner.getSignerInfo();
                //Check the correct document is signed.
                if (checkMessageDigestAttr(signerInfo, content)) {
                    combined.getSignedData().addSignerInfo(signerInfo);
                    CMSSignatureUtil.addCerIfNotExist(combined.getSignedData(), aSigner.getSignerCertificate());
                    CMSSignatureUtil.addDigestAlgIfNotExist(combined.getSignedData(), aSigner.getSignerInfo().getDigestAlgorithm());
                } else
                    throw new CMSSignatureException("İmzalanan içerik aynı değil");
            }
        }
        //new signature file
        return combined;
    }

    private boolean checkMessageDigestAttr(ESignerInfo aSignerInfo, ISignable content) throws CMSSignatureException {

        EAttribute attr = aSignerInfo.getSignedAttribute(MessageDigestAttr.OID).get(0);
        Asn1OctetString octetS = new Asn1OctetString();
        try {
            Asn1DerDecodeBuffer decBuf = new Asn1DerDecodeBuffer(attr.getValue(0));
            octetS.decode(decBuf);
        } catch (Exception ex) {
            throw new CMSSignatureException("Mesaj özeti çözülemedi.", ex);
        }

        DigestAlg digestAlg = DigestAlg.fromOID(aSignerInfo.getDigestAlgorithm().getAlgorithm().value);
        try {
            byte[] contentDigest = content.getMessageDigest(digestAlg);
            return Arrays.equals(octetS.value, contentDigest);
        } catch (CryptoException e) {
            throw new CMSSignatureException("Mesaj özeti hesaplanamadı.", e);
        } catch (IOException e) {
            throw new CMSSignatureException("İmzalanan dosya okunamadı.", e);
        }
    }
}
