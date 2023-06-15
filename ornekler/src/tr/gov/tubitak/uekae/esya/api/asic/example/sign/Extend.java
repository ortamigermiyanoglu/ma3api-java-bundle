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

/**
 * Adds new signatures to given containers
 */
public class Extend extends AsicSampleBase {

    @Test
    public void appendContainer_XAdES() throws Exception {

        SignaturePackage sp = read(PackageType.ASiC_E, SignatureFormat.XAdES, SignatureType.ES_BES);
        Signature existing = sp.getContainers().get(0).getSignatures().get(0);
        SignatureContainer container = sp.createContainer();
        Signature signature = container.createSignature(getCertificate());
        // get signable from signature
        signature.addContent(existing.getContents().get(0), false);
        signature.sign(getSigner());
        String fileName = fileName(PackageType.ASiC_E, SignatureFormat.XAdES, SignatureType.ES_BES) + "extended.asice";
        sp.write(new FileOutputStream(fileName));

        SignaturePackage sp2 = SignaturePackageFactory.readPackage(createContext(), new File(fileName));
        PackageValidationResult pvr = sp2.verifyAll();
        System.out.println(pvr);

        assert pvr.getResultType() == PackageValidationResultType.ALL_VALID;
    }

    @Test
    public void appendContainer_CAdES() throws Exception {

        SignaturePackage sp = read(PackageType.ASiC_E, SignatureFormat.CAdES, SignatureType.ES_BES);
        SignatureContainer sc = sp.createContainer();
        Signature signature = sc.createSignature(getCertificate());
        // get signable from package
        signature.addContent(sp.getDatas().get(0), false);
        signature.sign(getSigner());
        String fileName = fileName(PackageType.ASiC_E, SignatureFormat.CAdES, SignatureType.ES_BES) + "extended.asice";
        sp.write(new FileOutputStream(fileName));

        SignaturePackage sp2 = SignaturePackageFactory.readPackage(createContext(), new File(fileName));
        PackageValidationResult pvr = sp2.verifyAll();
        System.out.println(pvr);

        assert pvr.getResultType() == PackageValidationResultType.ALL_VALID;
    }
}