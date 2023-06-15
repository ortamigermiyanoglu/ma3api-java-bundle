package tr.gov.tubitak.uekae.esya.api.cades.example.validation;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cmssignature.CMSSignatureException;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignatureValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SignersInJTree extends CadesSampleBase {

    @Test
    public void testJTree() throws Exception {

        byte[] content = AsnIO.dosyadanOKU(getTestDataFolder() + "counterSignatures.p7s");

        CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
        SignedDataValidationResult validationResult = signatureValidation.validate(content, null);
        showJTree(content, validationResult);
    }

    public void showJTree(byte[] signature, SignedDataValidationResult sdvr) throws Exception {

        String contentName = "content";

        List<SignatureValidationResult> validationResults = sdvr.getSDValidationResults();

        //Get signatures in document
        BaseSignedData baseSignedData = new BaseSignedData(signature);
        List<Signer> signerList = baseSignedData.getSignerList();

        DefaultMutableTreeNode signatures = new DefaultMutableTreeNode(contentName);

        //Travel first level signatures
        for (int i = 0; i < signerList.size(); i++) {
            //get Signer
            Signer signer = signerList.get(i);
            //get signature validation result
            SignatureValidationResult validationResult = validationResults.get(i);
            SignerAndValidationResult signerAndValidationResult = new SignerAndValidationResult(signer, validationResult);
            //create node
            DefaultMutableTreeNode signerNode = new DefaultMutableTreeNode(signerAndValidationResult);
            signatures.add(signerNode);
            //search tree with deep first search
            deepFirstSearch(signerNode, signer, validationResult);
        }

        JFrame frame = new JFrame("Manual Nodes");
        final JTree tree = new JTree(signatures);

        //Double click shows the signature validation details
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouse) {
                if (mouse.getClickCount() == 2) {
                    TreePath treePath = tree.getPathForLocation(mouse.getX(), mouse.getY());
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                    SignerAndValidationResult signerAndValidationRst = (SignerAndValidationResult) treeNode.getUserObject();
                    TextPanel validationViewer = new TextPanel(signerAndValidationRst.getValidationResult());
                    validationViewer.setVisible(true);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tree);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setSize(300, 150);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Deep first search in the signature structure.
     *
     * @param parentNode
     * @param signer
     * @param validationResult
     * @throws CMSSignatureException
     */
    private void deepFirstSearch(DefaultMutableTreeNode parentNode, Signer signer, SignatureValidationResult validationResult) throws CMSSignatureException {

        List<Signer> signerList = signer.getCounterSigners();
        List<SignatureValidationResult> validationResultList = validationResult.getCounterSigValidationResults();
        for (int i = 0; i < signerList.size(); i++) {
            Signer counterSigner = signerList.get(i);
            SignatureValidationResult counterValidationResult = validationResultList.get(i);
            SignerAndValidationResult signerAndValidationResult = new SignerAndValidationResult(counterSigner, counterValidationResult);
            DefaultMutableTreeNode signerNode = new DefaultMutableTreeNode(signerAndValidationResult);
            parentNode.add(signerNode);
            deepFirstSearch(signerNode, counterSigner, counterValidationResult);
        }
    }
}
