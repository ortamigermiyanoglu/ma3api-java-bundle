package tr.gov.tubitak.uekae.esya.api.MobileSignatureClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.PolicyReader;
import tr.gov.tubitak.uekae.esya.api.certificate.validation.policy.ValidationPolicy;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableFile;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.IAttribute;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.SigningTimeAttr;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;

import tr.gov.tubitak.uekae.esya.api.crypto.alg.SignatureAlg;

import tr.gov.tubitak.uekae.esya.api.infra.mobile.IMobileSigner;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.MobileSigner;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.MultiMobileSigner;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.MultiMobileSigner.MultiMobileSignerForOne;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.Operator;
import tr.gov.tubitak.uekae.esya.api.infra.mobile.PhoneNumberAndOperator;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

@SpringBootApplication
public class MobileSignatureClientApplication {
	
	 @Autowired
	 private SOAPClient client;
	
	 static Logger logger = LoggerFactory.getLogger(MobileSignatureClientApplication.class);
	    
	 static int MOBILE_SIGNATURE_TIMEOUT = 120000;
     static int TIMEOUT_PER_SIGNATURE = 30000;
 	
     static String file1Path = "C:/Users/ma3/Desktop/hello1.txt"; // document which will be signed
     static String file2Path = "C:/Users/ma3/Desktop/hello2.txt"; // document which will be signed
     static String file3Path = "C:/Users/ma3/Desktop/hello3.txt"; // document which will be signed
		
     static MobileSigner mobileSigner = null;
	 static EMSSPClientConnector emsspClientConnector = null;

	static String licenseFilePath = "C:/Users/ma3/Desktop/ValidationFiles/lisans/lisans.xml";
     
	 @PostConstruct	
	 public void init() throws ESYAException {	
		
		loadLicense();
        
		try {
			Operator operator = Operator.TURKCELL;
			String phoneNumber = "05*********";

	         PhoneNumberAndOperator phoneNumberAndOperator = new PhoneNumberAndOperator(phoneNumber, operator);
	        
	         emsspClientConnector = new EMSSPClientConnector();
	         emsspClientConnector.setClient(client);
	         emsspClientConnector.setCertificateInitials(phoneNumberAndOperator);

			ECertificate signerCert = null;
	         
	         mobileSigner = new MobileSigner(emsspClientConnector, phoneNumberAndOperator, signerCert, "Rapor", SignatureAlg.RSA_SHA256.getName(), null);
	        	        	        	        	         	         	         
		} catch (Exception ex) {			
			logger.error("Error in initialization:", ex);
			throw new ESYAException("Error in initialization:", ex);
		}
	}
	
	public static void main(String[] args) {
		
		ConfigurableApplicationContext context = SpringApplication.run(MobileSignatureClientApplication.class, args);
					
		try {
			
			//creating single signature
			createSingleSignature(mobileSigner);
			 
			//creating multiple signature
			//multi-signature is only supported by Turkcell operator
			//createMultipleSigning(mobileSigner);
			
		} catch (Exception ex) {			
			logger.error("Error in TestMobileSignClient:", ex);
		}finally {			
			context.close();
		}				
	}
	
	public static void createSingleSignature(MobileSigner mobileSigner)throws ESYAException {
		
		try {
			
			SignatureResult result = signData(file1Path, mobileSigner);
			
			if(result.isExceptionOccured() == true) {
       		    System.out.println("Exception occured for: " + result.getInformativeText());
       		    result.getException().printStackTrace();
          	} else
          		System.out.println("Signature created for: " + result.getInformativeText());
             
		}catch (Exception ex) {
			String errorString = "Error in creating single signature";
			logger.error(errorString, ex);
			throw new ESYAException(errorString, ex);
		}			
	}
	
	 public static void createMultipleSigning(MobileSigner mobileSigner) throws ESYAException {
		if(!emsspClientConnector.isMultipleSignSupported()){
			throw new ESYAException("Multiple sign not supported");
		}
	 	try {
			String[] filesToBeSigned = {file1Path, file2Path, file3Path};
			String[] informativeTexts = {"rapor1", "rapor2", "rapor3"};
			int signatureCount = filesToBeSigned.length;  	
				
				
			MultiMobileSigner multiMobileSigner = new MultiMobileSigner(mobileSigner, signatureCount);	
				
			ExecutorService executorService = Executors.newCachedThreadPool();
				
			int TOTAL_TIMEOUT_MS = MOBILE_SIGNATURE_TIMEOUT + TIMEOUT_PER_SIGNATURE * multiMobileSigner.getSignatureCount();
				       	        
		    List<Callable<SignatureResult>> callables = Arrays.asList(
		      		() -> signData(filesToBeSigned[0], new MultiMobileSignerForOne(multiMobileSigner, informativeTexts[0], 0)),
			       	() -> signData(filesToBeSigned[1], new MultiMobileSignerForOne(multiMobileSigner, informativeTexts[1], 1)),
			       	() -> signData(filesToBeSigned[2], new MultiMobileSignerForOne(multiMobileSigner, informativeTexts[2], 2))
		    );
		        
		    List<SignatureResult> results = executorService.invokeAll(callables, TOTAL_TIMEOUT_MS, TimeUnit.MILLISECONDS).stream().map(future ->
		    {
		       try {
		        	return future.get();
		        	
		       }catch(CancellationException ex) {
		        	System.out.println("Timeout occured");
		        	throw ex;
		       }
		       	catch(Exception ex) {
		         	System.out.println("Future get: " + ex.toString());
		        	ex.printStackTrace();
		        	throw new IllegalStateException(ex);
		        }	        	
		     }).collect(Collectors.toList());
		        
		    
		     for(SignatureResult aResult : results)
		     {
		        	if(aResult.isExceptionOccured() == true) {
		        		 System.out.println("Exception occured for: " + aResult.getInformativeText());
		        		 aResult.getException().printStackTrace();
		        	}	        		        	   
		     }
		       
		     try {
		        	executorService.shutdown();
			        executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
		     }catch(InterruptedException ex) {
	        		throw new InterruptedException("Tasks interrupted");
	         }finally{
	        		if(!executorService.isTerminated()) {
	        			System.err.println("Cancel non finish tasks");
	        		}
	        		executorService.shutdownNow();    	        		
	         }
		        	        	        	        						
		}catch (Exception ex){	
				String errorString = "Error in creating multiple signature";
				logger.error(errorString, ex);
				throw new ESYAException(errorString, ex);
		}	
    }
	
	public static SignatureResult signData(String filePath, IMobileSigner signer) throws Exception
	{			
			try {
													       
		        BaseSignedData bs = new BaseSignedData();
		        bs.addContent(new SignableFile(new File(filePath)), true);
		       
		        // Since SigningTime attribute is optional, add it to optional attributes list
				java.util.List<IAttribute> optionalAttributes = new ArrayList<IAttribute>();
			    optionalAttributes.add(new SigningTimeAttr(Calendar.getInstance()));
		
				     			      	        
				ValidationPolicy validationPolicy = PolicyReader.readValidationPolicy(new FileInputStream("C:\\Users\\ma3\\Desktop\\ValidationFiles\\certval-policy-test.xml"));
				HashMap<String, Object> params = new HashMap<String, Object>();
		
		        params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);		      		      
		        params.put(EParameters.P_CERT_VALIDATION_POLICY, validationPolicy);
				 		     	
		        bs.addSigner(ESignatureType.TYPE_BES, null, signer, optionalAttributes, params);		       		        
		        		        		        
		        AsnIO.dosyayaz(bs.getEncoded(), filePath + ".p7s");			        
		   }            
		    catch (Exception ex) { 
		    	
				  return new SignatureResult(ex, true, signer.getInformativeText());
			}
			 
			return new SignatureResult(null, false, signer.getInformativeText());
	} 
	
	
	private static final Map<Integer, Operator> intToTypeMap = new HashMap<Integer, Operator>();
	static {
	   for (Operator type : Operator.values()) {
	        intToTypeMap.put(type.ordinal(), type);
	    }
	}

	private static Operator fromInt(int i) {
		Operator type = intToTypeMap.get(Integer.valueOf(i));
	    if (type == null) 
	        return Operator.TURKCELL;
	    return type;
	}
	
    private static void loadLicense() {
	    FileInputStream fis;
		try {
			//write license path below
			fis = new FileInputStream(getLicenseFilePath());
			LicenseUtil.setLicenseXml(fis);
			fis.close();
		} catch (Exception exc) {
			logger.error("Error in TestMobileSignClient:", exc);
		}
	}

	public static String getLicenseFilePath() throws ESYAException
	{
		throw new ESYAException("Set licenseFilePath!");
		//return licenseFilePath;
	}
}
