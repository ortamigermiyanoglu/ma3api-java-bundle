package tr.gov.tubitak.uekae.esya.api.MobileSignatureOnlyServer;


public class SignatureResult {
	
	private Exception exception;
	private Boolean isExceptionOccured;
	private String informativeText;
		
		
	public SignatureResult(Exception exception, Boolean isExceptionOccured, String informativeText) {
		super();
		this.exception = exception;
		this.isExceptionOccured = isExceptionOccured;
		this.informativeText = informativeText;
	}


	public Exception getException() {
		return exception;
	}


	public Boolean isExceptionOccured() {
		return isExceptionOccured;
	}


	public String getInformativeText() {
		return informativeText;
	}


	public void setException(Exception exception) {
		this.exception = exception;
	}


	public void setExceptionOccured(Boolean isExceptionOccured) {
		this.isExceptionOccured = isExceptionOccured;
	}


	public void setInformativeText(String informativeText) {
		this.informativeText = informativeText;
	}
	
}
