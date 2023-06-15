package tr.gov.tubitak.uekae.esya.api.cades.example.validation;

import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignatureValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.check.CheckerResult;

import javax.swing.*;
import java.util.List;

/**
 * Shows the signature validation result.
 */
public class TextPanel extends JFrame {

    public TextPanel(SignatureValidationResult validationResult) {
        //For older version of the API, use this interpreter.
        //JTextArea textArea = new JTextArea(interpretResult(validationResult));
        JTextArea textArea = new JTextArea(validationResult.getValidationDetails());
        this.add(textArea);
        this.setSize(550, 250);
    }

    private String interpretResult(SignatureValidationResult validationResult) {
        List<CheckerResult> checkDetails = validationResult.getCheckerResults();
        StringBuilder result = new StringBuilder();
        if (checkDetails != null) {
            result.append("Imza Kontrolcu Sonuclari:\n");
            for (CheckerResult cr : checkDetails) {
                result.append(cr.toString());
            }
        }
        return result.toString();
    }
}
