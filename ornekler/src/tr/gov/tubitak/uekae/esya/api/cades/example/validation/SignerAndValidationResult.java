package tr.gov.tubitak.uekae.esya.api.cades.example.validation;

import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignatureValidationResult;

/**
 * Matchs the signer and validation result
 */
public class SignerAndValidationResult {

    Signer signer;
    SignatureValidationResult validationResult;

    public SignerAndValidationResult(Signer aSigner, SignatureValidationResult aValidationResult) {
        signer = aSigner;
        validationResult = aValidationResult;
    }

    @Override
    public String toString() {
        return signer.getSignerCertificate().getSubject().getCommonNameAttribute();
    }

    public SignatureValidationResult getValidationResult() {
        return validationResult;
    }
}
