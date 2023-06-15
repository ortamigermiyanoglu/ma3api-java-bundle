package tr.gov.tubitak.uekae.esya.api.MobileSignatureClient;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import tr.gov.tubitak.uekae.esya.api.asn.cms.ESignerIdentifier;
import tr.gov.tubitak.uekae.esya.api.asn.cms.ESigningCertificate;
import tr.gov.tubitak.uekae.esya.api.asn.cms.ESigningCertificateV2;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.util.Base64;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.DigestAlg;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.MSSPClientConnector;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.MultiSignResult;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.Operator;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.PhoneNumberAndOperator;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.SigningMode;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.Status;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.UserIdentifier;
import tr.gov.tubitak.uekae.esya.api.schemas.signature.*;
import tr.gov.tubitak.uekae.esya.api.webservice.mssclient.wrapper.StringUtil;
import tr.gov.tubitak.uekae.esya.asn.cms.SigningCertificate;
import tr.gov.tubitak.uekae.esya.asn.cms.SigningCertificateV2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class EMSSPClientConnector implements MSSPClientConnector
{
	static Logger logger = LoggerFactory.getLogger(EMSSPClientConnector.class);
	
	private SOAPClient client;

	public SOAPClient getClient() {
		return client;
	}

	public void setClient(SOAPClient client) {
		this.client = client;
	}

	@Override
	public DigestAlg getDigestAlg(){
		try {
			DigestAlgRequest request = new DigestAlgRequest();
			DigestAlgResponse response = client.getDigestAlg(request);
			return DigestAlg.fromName(response.getDigestAlgReturn());
		} catch (Exception ex) {

			logger.error("Error in getting digest algorithm", ex);
			return null;		
		}
	}

	@Override
	public ESignerIdentifier getSignerIdentifier() {	
    try {		
    	
		SignerIdentifierRequest request = new SignerIdentifierRequest();
		SignerIdentifierResponse response = client.getSignerIdentifier(request);			
		return new ESignerIdentifier(Base64.decode(response.getSignerIdentifierReturn()));
				 	
		}  catch (Exception ex) {			
			
			logger.error("Error in getting signerIdentifier", ex);
			return null;
		}		
	}

	@Override
	public ECertificate getSigningCert() {
		try {
			SigningCertRequest request = new SigningCertRequest();
			SigningCertResponse response = client.getSigningCert(request);
			return new ECertificate(Base64.decode(response.getSigningCertReturn()));
		}  catch (Exception ex) {
						
			logger.error("Error in getting signingCert", ex);
			return null;
		}
	}

	@Override
	public boolean isMultipleSignSupported() {
		MultipleSignSupportedRequest request = new MultipleSignSupportedRequest();
		MultipleSignSupportedResponse response = client.getIsMultipleSignSupported(request);
		return response.isMultipleSignSupportedReturn();
	}

	@Override
	public SigningCertificate getSigningCertAttr() {
		try {
			SigningCertAttrRequest request = new SigningCertAttrRequest();
			SigningCertAttrResponse response = client.getSigningCertAttr(request);
			return new ESigningCertificate(Base64.decode(response.getSigningCertAttrReturn())).getObject();
			
		}  catch (Exception ex) {
			
			logger.error("Error in getting signingCertAttr", ex);
			return null;
		}
	}

	@Override
	public SigningCertificateV2 getSigningCertAttrv2() {		
		try {
			SigningCertAttrV2Request request = new SigningCertAttrV2Request();
			SigningCertAttrV2Response response = client.getSigningCertAttrV2(request);
			return new ESigningCertificateV2(Base64.decode(response.getSigningCertAttrV2Return())).getObject();
		} catch (Exception ex) {
			
			logger.error("Error in getting signingCertAttrV2", ex);
			return null;		
		}
	}

	
	@Override
	public void setCertificateInitials(UserIdentifier aUserID) throws ESYAException {
		
		PhoneNumberAndOperator phoneNumberAndOperator = (PhoneNumberAndOperator) aUserID;
		try {
			
			CertificateInitialsRequest request = new CertificateInitialsRequest();
			request.setOperator(phoneNumberAndOperator.getOperator().ordinal());
			request.setPhoneNumber(phoneNumberAndOperator.getPhoneNumber());			
			
			CertificateInitialsResponse cir = client.setCertificateInitials(request);
			 
			if(cir.isCertificateInitialsReturn() == true)
				logger.info("Certificate initials are set successfully");
				
				
		} catch (Exception ex) {
			logger.error("Error in setting certificate initials", ex);
			throw new ESYAException("Error in setting certificate initials", ex);
		}
		
	}

	@Override
	public byte[] sign(byte[] dataToBeSigned, SigningMode aMode, UserIdentifier aUserID, 
	    		ECertificate aSigningCert, String informativeText, String aSigningAlg, AlgorithmParameterSpec algSpec) throws ESYAException
	{		
		if(aMode!=SigningMode.SIGNHASH)   	
    		throw new ESYAException("Unsupported signing mode. Only SIGNHASH supported.");    		
    	   	    	   		
    	PhoneNumberAndOperator phoneNumberAndOperator = (PhoneNumberAndOperator) aUserID;  	
    	String dataTobeSigned64 = Base64.encode(dataToBeSigned);            
    	Operator opes= phoneNumberAndOperator.getOperator();        	
    	String signatureBase64;
    	try {
    		SignHashRequest request = new SignHashRequest();
    		request.setHashForSign(dataTobeSigned64);
    		request.setDisplayText(informativeText);
    		request.setPhoneNumber(phoneNumberAndOperator.getPhoneNumber());
    		request.setOperator(opes.ordinal());
    		request.setSigningAlg(aSigningAlg);
    		request.setMultiSignature(false);
    		
    		SignHashResponse response = client.sign(request);
    		signatureBase64 = response.getSignHashReturn();
    	} catch (Exception ex) {    
    		String errorString = "EMSSPClientConnector: Error in single signing";	
    		logger.error(errorString, ex);
			throw new ESYAException(errorString, ex);
    	}
     
    	return Base64.decode(signatureBase64);                   	
	}

	@Override
	public ArrayList<MultiSignResult> sign(ArrayList<byte[]> dataToBeSigned, SigningMode aMode, UserIdentifier aUserID,
			ECertificate eCertificate, ArrayList<String> informativeText, String aSigningAlg,
			AlgorithmParameterSpec aParams) throws ESYAException {
		
		if(aMode!=SigningMode.SIGNHASH)
          throw new ESYAException("Unsupported signing mode. Only SIGNHASH supported.");
    		    
		ArrayList<MultiSignResult> retSignatureAndValidationResult = new ArrayList<MultiSignResult>();
		
    	PhoneNumberAndOperator phoneNumberAndOperator = (PhoneNumberAndOperator) aUserID;
    	Operator opes= phoneNumberAndOperator.getOperator(); 
    	  	
    	String dataTobeSignedList = getBase64EncodedData(dataToBeSigned); 
    	String informativeTextList = getInformativeTexts(informativeText);
    	       	  	
    	String commaSeparatedSignatureValues;
    	try {
    		
     		SignHashRequest request = new SignHashRequest();
    		request.setHashForSign(dataTobeSignedList);
    		request.setDisplayText(informativeTextList);
    		request.setPhoneNumber(phoneNumberAndOperator.getPhoneNumber());
    		request.setOperator(opes.ordinal());
    		request.setSigningAlg(aSigningAlg);
    		request.setMultiSignature(true);
    		
    		SignHashResponse response = client.sign(request);
    		commaSeparatedSignatureValues = response.getSignHashReturn();
    	    	
    	} catch(Exception ex) {    		
    		String errorString = "EMSSPClientConnector: Error in multiple signing";		
    		logger.error(errorString, ex);
			throw new ESYAException(errorString, ex);
    	}  
    	    	
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
	
    public String getBase64EncodedData(ArrayList<byte[]> dataToBeSigned){
		
		String digestForSign64 = null;
		int signatureCount = dataToBeSigned.size();
        StringBuilder multiSignDigests = new StringBuilder();
     	
		 for(int i=0; i < signatureCount; i++)
	     {			   
			 digestForSign64 = Base64.encode(dataToBeSigned.get(i));              
			 multiSignDigests.append(digestForSign64);
	           
             if(i+1 != signatureCount)
	            multiSignDigests.append(";");   		   		   
	     }
		return multiSignDigests.toString();	
		
	}
	
	public String getInformativeTexts(ArrayList<String> informativeText){
		    
       int informativeTextCount = informativeText.size();
    	StringBuilder informativeTexts = new StringBuilder();
    	
		 for(int i=0; i < informativeTextCount; i++)
	     {
			    informativeTexts.append(informativeText.get(i));
	            if(i+1 != informativeTextCount)
	            	informativeTexts.append(";");
			 
	     }
		return informativeTexts.toString() ;
	}	
}
	

