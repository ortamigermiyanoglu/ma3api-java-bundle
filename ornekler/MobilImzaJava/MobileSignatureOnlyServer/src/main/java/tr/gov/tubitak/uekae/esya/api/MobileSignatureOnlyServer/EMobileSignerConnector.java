package tr.gov.tubitak.uekae.esya.api.MobileSignatureOnlyServer;

import tr.gov.tubitak.uekae.esya.api.asn.cms.ESignerIdentifier;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.Algorithms;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.*;
import tr.gov.tubitak.uekae.esya.api.webservice.mssclient.wrapper.EMSSPRequestHandler;
import tr.gov.tubitak.uekae.esya.api.webservice.mssclient.wrapper.MSSParams;
import tr.gov.tubitak.uekae.esya.api.webservice.mssclient.wrapper.StringUtil;
import tr.gov.tubitak.uekae.esya.asn.cms.SigningCertificate;
import tr.gov.tubitak.uekae.esya.asn.cms.SigningCertificateV2;

import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created with IntelliJ IDEA.
 * User: ramazan.girgin
 * Date: 8/1/12
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class EMobileSignerConnector implements MSSPClientConnector {
    EMSSPRequestHandler msspRequestHandler;
    public EMobileSignerConnector(MSSParams msspParams) {
        msspRequestHandler = new EMSSPRequestHandler(msspParams);
    }

	@Override
	public void setCertificateInitials(UserIdentifier userIdentifier) {
		try {
			msspRequestHandler.setCertificateInitials((PhoneNumberAndOperator) userIdentifier);
		} catch (Exception ex) {
			throw new RuntimeException("Error in setting certificate initials", ex);
		}
	}
	@Override
	public byte[] sign(byte[] dataToBeSigned, SigningMode aMode, UserIdentifier aUserID, ECertificate eCertificate, String informativeText, String aSigningAlg, AlgorithmParameterSpec aParams) throws ESYAException {
		byte [] retSignature = null;
		try {
			retSignature = msspRequestHandler.sign(dataToBeSigned,aMode,(PhoneNumberAndOperator)aUserID,informativeText,aSigningAlg,aParams);
		} catch (Exception ex) {
			throw new ESYAException("Error in creating signature", ex);
		}
		return retSignature;
	}

	@Override
	public ArrayList<MultiSignResult> sign(ArrayList<byte[]> dataToBeSigned, SigningMode aMode, UserIdentifier aUserID, ECertificate eCertificate, ArrayList<String> informativeText, String aSigningAlg, AlgorithmParameterSpec aParams) throws ESYAException {
		String commaSeparatedSignatureValues;
		try {

			commaSeparatedSignatureValues = msspRequestHandler.sign(dataToBeSigned,aMode,(PhoneNumberAndOperator)aUserID,informativeText,aSigningAlg,aParams);
		} catch (Exception ex) {
			throw new ESYAException("Error in creating signature", ex);
		}

		ArrayList<MultiSignResult> retSignatureAndValidationResult = new ArrayList<MultiSignResult>();
		String[] signaturResults = commaSeparatedSignatureValues.split(";");

		for(int i=0; i<signaturResults.length; i++) {

			Matcher matcher = Pattern.compile("\\{(.*?)\\}").matcher(signaturResults[i]);
			if(matcher.find()){
				String[] responseResult = matcher.group(1).split(":");
				Status status = new Status(responseResult[0],responseResult[1]);
				retSignatureAndValidationResult.add(new MultiSignResult(null, status, informativeText.get(i)));
			}
			else{

				byte[] signature = StringUtil.toByteArray(signaturResults[i]);
				retSignatureAndValidationResult.add(new MultiSignResult(signature, null, informativeText.get(i)));
			}
		}

		return retSignatureAndValidationResult;
	}

	@Override
	public boolean isMultipleSignSupported() {
		return msspRequestHandler.isMultipleSignSupported();
	}

	@Override
	public ECertificate getSigningCert() {
		return msspRequestHandler.getSigningCert();
	}

	@Override
	public SigningCertificate getSigningCertAttr() {
		return msspRequestHandler.getSigningCertAttr();
	}

	@Override
	public SigningCertificateV2 getSigningCertAttrv2() {
		return msspRequestHandler.getSigningCertAttrv2();
	}

	@Override
	public ESignerIdentifier getSignerIdentifier() {
		return msspRequestHandler.getSignerIdentifier();
	}

	@Override
	public DigestAlg getDigestAlg() {
		return msspRequestHandler.getDigestAlg();
	}

}