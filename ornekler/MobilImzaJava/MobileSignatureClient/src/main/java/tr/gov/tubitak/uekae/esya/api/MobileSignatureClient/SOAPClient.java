package tr.gov.tubitak.uekae.esya.api.MobileSignatureClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import tr.gov.tubitak.uekae.esya.api.schemas.signature.*;

@Service
public class SOAPClient {

    @Autowired
	private Jaxb2Marshaller marshaller;

	private WebServiceTemplate template;
	
	private String signatureServiceAddress = "http://localhost:8181/service/MobileSignatureService";
	
	public SignerIdentifierResponse getSignerIdentifier(SignerIdentifierRequest request) {
		template = new WebServiceTemplate(marshaller);
		SignerIdentifierResponse signerIdentifierResponse = (SignerIdentifierResponse) template.marshalSendAndReceive(signatureServiceAddress, request);
		
		return signerIdentifierResponse;
	}
	
	public CertificateInitialsResponse setCertificateInitials(CertificateInitialsRequest request) {
		template = new WebServiceTemplate(marshaller);
		CertificateInitialsResponse certificateInitialsResponse = (CertificateInitialsResponse)template.marshalSendAndReceive(signatureServiceAddress, request);
		
		return certificateInitialsResponse;
	}


	public MultipleSignSupportedResponse getIsMultipleSignSupported(MultipleSignSupportedRequest request) {
		template = new WebServiceTemplate(marshaller);
		MultipleSignSupportedResponse multipleSignSupportedResponse = (MultipleSignSupportedResponse) template.marshalSendAndReceive(signatureServiceAddress, request);

		return multipleSignSupportedResponse;
	}
	
	public SigningCertResponse getSigningCert(SigningCertRequest request) {
		template = new WebServiceTemplate(marshaller);
		SigningCertResponse signingCertResponse = (SigningCertResponse) template.marshalSendAndReceive(signatureServiceAddress, request);
		
		return signingCertResponse;				
	}
	
	public SigningCertAttrResponse getSigningCertAttr(SigningCertAttrRequest request) {
		template = new WebServiceTemplate(marshaller);
		SigningCertAttrResponse signingCertAttrResponse = (SigningCertAttrResponse) template.marshalSendAndReceive(signatureServiceAddress, request);
		
		return signingCertAttrResponse;				
	}
	
	public SigningCertAttrV2Response getSigningCertAttrV2(SigningCertAttrV2Request request) {
		template = new WebServiceTemplate(marshaller);
		SigningCertAttrV2Response signingCertAttrV2Response = (SigningCertAttrV2Response) template.marshalSendAndReceive(signatureServiceAddress, request);
		
		return signingCertAttrV2Response;				
	}
	
	public DigestAlgResponse getDigestAlg(DigestAlgRequest request) {
		template = new WebServiceTemplate(marshaller);
		DigestAlgResponse digestAlgResponse = (DigestAlgResponse) template.marshalSendAndReceive(signatureServiceAddress, request);
		
		return digestAlgResponse;				
	}
	
	public SignHashResponse sign(SignHashRequest request) {
		template = new WebServiceTemplate(marshaller);
		SignHashResponse signHashResponse = (SignHashResponse) template.marshalSendAndReceive(signatureServiceAddress, request);
		
		return signHashResponse;				
	}
}



