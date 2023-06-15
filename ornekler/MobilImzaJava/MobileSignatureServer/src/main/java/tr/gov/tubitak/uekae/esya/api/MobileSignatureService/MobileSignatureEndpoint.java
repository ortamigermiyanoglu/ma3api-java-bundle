package tr.gov.tubitak.uekae.esya.api.MobileSignatureService;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.Operator;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.PhoneNumberAndOperator;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.SigningMode;
import tr.gov.tubitak.uekae.esya.api.webservice.mssclient.wrapper.EMSSPRequestHandler;
import tr.gov.tubitak.uekae.esya.api.webservice.mssclient.wrapper.MSSParams;
import tr.gov.tubitak.xml.signature.*;
import tr.gov.tubitak.uekae.esya.api.asn.cms.ESigningCertificate;
import tr.gov.tubitak.uekae.esya.api.asn.cms.ESigningCertificateV2;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.util.Base64;

@Endpoint
public class MobileSignatureEndpoint
{
  
    static final String NAMESPACE_URI = "http://www.tubitak.gov.tr/xml/signature";

	static String licenseFilePath = "C:/Users/ma3/Desktop/ValidationFiles/lisans/lisans.xml";
   
    static EMSSPRequestHandler msspRequestHandler;
    
    public MobileSignatureEndpoint() throws ESYAException {
    	init();   	
    }
    
    public void init() throws ESYAException {
    	
    	loadLicense();
		initializeMsspRequestHandler();	
    }
    
  	
    static final Map<Integer, Operator> intToTypeMap = new HashMap<Integer, Operator>();
 		static {			
 			for (Operator type : Operator.values()) {
 				intToTypeMap.put(type.ordinal(), type);
 			}
 	}

 	static Operator fromInt(int i) {
 			Operator type = intToTypeMap.get(Integer.valueOf(i));
 			if (type == null)
 				return Operator.TURKCELL;
 			return type;
 	}
     
	static void loadLicense() throws ESYAException {
		// write license path below
		FileInputStream fis;
		try {
			fis = new FileInputStream(getLicenseFilePath());
			LicenseUtil.setLicenseXml(fis);
			fis.close();
		} catch (Exception ex) {
			throw new ESYAException("Error in loading license", ex);
		}
	}
	
	static void initializeMsspRequestHandler() throws ESYAException {
		
		try {
			
			MSSParams mobilParams = new MSSParams("http://MImzaTubitak", "*****", "www.turkcelltech.com");
			mobilParams.setMsspSignatureQueryUrl("https://msign-test.turkcell.com.tr:443/MSSP2/services/MSS_Signature");
			mobilParams.setMsspProfileQueryUrl("https://msign-test.turkcell.com.tr:443/MSSP2/services/MSS_ProfileQueryPort");

			/*MSSParams mobilParams = new MSSParams("Tubitak_KamuSM", "*****", "");
			mobilParams.setMsspSignatureQueryUrl("https://mobilimza.turktelekom.com.tr/EGAMsspWSAP2/MSS_SignatureService");
			mobilParams.setMsspProfileQueryUrl("https://mobilimza.turktelekom.com.tr/EGAMsspWSAP2/MSS_ProfileQueryService");*/

			/*MSSParams mobilParams = new MSSParams("tbtkbilgem", "******", "mobilimza.vodafone.com.tr");
			mobilParams.setMsspSignatureQueryUrl("https://mobilimza.vodafone.com.tr:443/Dianta2/MSS_SignatureService");*/

			mobilParams.setMsspRequestTimeout(5000);
			mobilParams.setConnectionTimeoutMs(120000);
			
			msspRequestHandler = new EMSSPRequestHandler(mobilParams);	
			
		}catch(Exception ex) {
			throw new ESYAException("Error in initializing MSSP request handler", ex);
		}
	}

	private MSSParams getMobileParams(Operator operator) throws ESYAException {

		MSSParams mobileParams;
		if (operator == Operator.TURKCELL) {
			mobileParams = new MSSParams("http://MImzaTubitak", "***", "www.turkcelltech.com");
			mobileParams.setMsspSignatureQueryUrl("https://msign-test.turkcell.com.tr:443/MSSP2/services/MSS_Signature");
			mobileParams.setMsspProfileQueryUrl("https://msign-test.turkcell.com.tr:443/MSSP2/services/MSS_ProfileQueryPort");
		} else if (operator == Operator.AVEA) {
			mobileParams = new MSSParams("Tubitak_KamuSM", "***", "");
			mobileParams.setMsspSignatureQueryUrl("https://mobilimza.turktelekom.com.tr/EGAMsspWSAP2/MSS_SignatureService");
			mobileParams.setMsspProfileQueryUrl("https://mobilimza.turktelekom.com.tr/EGAMsspWSAP2/MSS_ProfileQueryService");
		} else if (operator == Operator.VODAFONE) {
			mobileParams = new MSSParams("tbtkbilgem", "***", "mobilimza.vodafone.com.tr");
			mobileParams.setMsspSignatureQueryUrl("https://mobilimza.vodafone.com.tr:443/Dianta2/MSS_SignatureService");
		} else {
			throw new ESYAException("Unknown mobile operator!");
		}

		return mobileParams;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "CertificateInitialsRequest")
	@ResponsePayload
    public CertificateInitialsResponse setCertificateInitials(@RequestPayload CertificateInitialsRequest request) throws ESYAException{
	    	
		CertificateInitialsResponse response = new CertificateInitialsResponse();
		
		Operator mobileOperator = fromInt(request.getOperator());
		PhoneNumberAndOperator phoneNumberAndOperator = new PhoneNumberAndOperator(request.getPhoneNumber(), mobileOperator);	
				
		try {	
			 
		     msspRequestHandler.setCertificateInitials(phoneNumberAndOperator);
		     
		} catch (ESYAException ex) {				
			throw new ESYAException("Error in setting certificate initials", ex);
		}	
		
		response.setCertificateInitialsReturn(true);
		
		return response;
    }

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "MultipleSignSupportedRequest")
	@ResponsePayload
	public MultipleSignSupportedResponse getMultiSignSupportInfo() throws ESYAException{

		MultipleSignSupportedResponse response = new MultipleSignSupportedResponse();
		try {
			response.setMultipleSignSupportedReturn(msspRequestHandler.isMultipleSignSupported());
		} catch (Exception ex) {
			throw new ESYAException("Error in getting multiple sign supported info", ex);
		}

		return response;
	}

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SignerIdentifierRequest")
    @ResponsePayload
    public SignerIdentifierResponse getSignerIdenitifer() throws ESYAException{
    	 
    	SignerIdentifierResponse response = new SignerIdentifierResponse();
    	try {			
    		response.setSignerIdentifierReturn(Base64.encode(msspRequestHandler.getSignerIdentifier().getEncoded()));    	
		} catch (Exception ex) {			
			throw new ESYAException("Error in getting signer identifier", ex);
		}
    	  	
        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DigestAlgRequest")
    @ResponsePayload
    public DigestAlgResponse getDigestAlg() throws ESYAException{
    	 
    	DigestAlgResponse response = new DigestAlgResponse();
    	try {			
    		response.setDigestAlgReturn(msspRequestHandler.getDigestAlg().getName());    
		} catch (Exception ex) {			
			throw new ESYAException("Error in getting digest algorithm", ex);
		}
    	      	
        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SigningCertAttrV2Request")
    @ResponsePayload
    public SigningCertAttrV2Response getSigningCertAttrv2() throws ESYAException{
    	 
    	SigningCertAttrV2Response response = new SigningCertAttrV2Response();
    	try {			
    		ESigningCertificateV2 scv2 = new ESigningCertificateV2(msspRequestHandler.getSigningCertAttrv2());
        	response.setSigningCertAttrV2Return(Base64.encode(scv2.getEncoded()));    
		} catch (Exception ex) {			
			throw new ESYAException("Error in getting signing certificate attribute V2", ex);
		}
    	    	      	
        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SigningCertAttrRequest")
    @ResponsePayload
    public SigningCertAttrResponse getSigningCertAttr() throws ESYAException{
    	 
    	SigningCertAttrResponse response = new SigningCertAttrResponse();
    	try {			
    		ESigningCertificate sc = new ESigningCertificate(msspRequestHandler.getSigningCertAttr());
        	response.setSigningCertAttrReturn(Base64.encode(sc.getEncoded()));        
		} catch (Exception ex) {			
			throw new ESYAException("Error in getting signing certificate attribute", ex);
		}
    	            	
        return response;
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SigningCertRequest")
    @ResponsePayload
    public SigningCertResponse getSigningCert() throws ESYAException{
    	 
    	SigningCertResponse response = new SigningCertResponse();
    	try {			
    		   		
    		response.setSigningCertReturn(Base64.encode(msspRequestHandler.getSigningCert().getEncoded()));       
		} catch (Exception ex) {			
			throw new ESYAException("Error in getting signing certificate", ex);
		}
       	        	
        return response;
    }
      
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SignHashRequest")
    @ResponsePayload
    public SignHashResponse SignHash(@RequestPayload SignHashRequest request) throws ESYAException {
		
		Operator mobileOperator = fromInt(request.getOperator());
		PhoneNumberAndOperator phoneNumberAndOperator = new PhoneNumberAndOperator(request.getPhoneNumber(), mobileOperator);
		
		SignHashResponse response = new SignHashResponse();
		
		if(request.isMultiSignature()) {
					        	
    		String[] eachDataToBeSigned = request.getHashForSign().split(";");           
    		ArrayList<byte[]> dataToBeSigned = getBase64DecodedData(eachDataToBeSigned);  
                       
            String[] eachInformativeText = request.getDisplayText().split(";");
            ArrayList<String> informativeTexts = new ArrayList<String>(Arrays.asList(eachInformativeText));
   				         
			try{		
				response.setSignHashReturn(msspRequestHandler.sign(dataToBeSigned, SigningMode.SIGNHASH, phoneNumberAndOperator, informativeTexts, request.getSigningAlg(), null));
			} catch (Exception ex) {
				throw new ESYAException("Error in multi signing", ex);		
			}
							
		}else{
			
			byte[] dataForSign = Base64.decode(request.getHashForSign());
			byte[] signedData;
			
			try {
				signedData = msspRequestHandler.sign(dataForSign, SigningMode.SIGNHASH, phoneNumberAndOperator, request.getDisplayText(), request.getSigningAlg(), null);				
			} catch (Exception ex) {				
				throw new ESYAException("Error in signing", ex);	
			}
			
			response.setSignHashReturn(Base64.encode(signedData));
		}
		
		return response;			
	}
	
 
    private ArrayList<byte[]> getBase64DecodedData(String[] base64EncodedData) {
		
		ArrayList<byte[]> base64DecodedData = new ArrayList<byte[]>();	
		for(int i=0; i<base64EncodedData.length; i++)              		 	                
			base64DecodedData.add(Base64.decode(base64EncodedData[i])); 
		
		return base64DecodedData;
	}

	public static String getLicenseFilePath() throws ESYAException
	{
		throw new ESYAException("Set licenseFilePath!");
		//return licenseFilePath;
	}
}
