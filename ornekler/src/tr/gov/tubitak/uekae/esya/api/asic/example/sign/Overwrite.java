package tr.gov.tubitak.uekae.esya.api.asic.example.sign;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asic.example.AsicSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.Signature;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureContainer;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureType;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.*;

import java.io.File;
import java.io.FileOutputStream;

import static junit.framework.TestCase.assertEquals;

/**
 * Adds new signatures to given containers, new extended signature
 * will have the same name with the old one
 */
public class Overwrite extends AsicSampleBase {

    @Test
    public void update_CAdES() throws Exception {

        SignaturePackage signaturePackage = read(PackageType.ASiC_E, SignatureFormat.CAdES, SignatureType.ES_BES);
        String fileName = fileName(PackageType.ASiC_E, SignatureFormat.CAdES, SignatureType.ES_BES) + "-upgraded.asice";
        File toUpgrade = new File(fileName);

        // create a copy to update package
        signaturePackage.write(new FileOutputStream(fileName));

        // add signature
        SignaturePackage signaturePackage2 = SignaturePackageFactory.readPackage(createContext(), toUpgrade);
        SignatureContainer sc = signaturePackage2.createContainer();
        Signature signature = sc.createSignature(getCertificate());
        signature.addContent(signaturePackage.getDatas().get(0), false);
        signature.sign(getSigner());

        // write on to read file!
        signaturePackage2.write();

        // read again to verify
        SignaturePackage sp3 = SignaturePackageFactory.readPackage(createContext(), new File(fileName));
        PackageValidationResult validationResult = sp3.verifyAll();
        System.out.println(validationResult);

        assertEquals(PackageValidationResultType.ALL_VALID, validationResult.getResultType());
    }
}