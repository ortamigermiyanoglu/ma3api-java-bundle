package tr.gov.tubitak.uekae.esya.api.asic.example.upgrades;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asic.example.AsicSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.PackageType;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.PackageValidationResult;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.SignaturePackage;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.SignaturePackageFactory;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Upgrades given ASiC signatures
 */
public class Upgrade extends AsicSampleBase {

    public void upgrade(PackageType packageType, SignatureFormat format, SignatureType current, SignatureType next)
            throws Exception {

        Context c = createContext();//new Context();
        SignaturePackage signaturePackage = SignaturePackageFactory.readPackage(c, new File(fileName(packageType, format, current)));
        SignatureContainer sc = signaturePackage.getContainers().get(0);
        Signature signature = sc.getSignatures().get(0);

        // upgrade
        signature.upgrade(next);

        signaturePackage.write(new FileOutputStream(fileName(packageType, format, next)));

        // validate
        PackageValidationResult pvr = signaturePackage.verifyAll();

        // output results
        System.out.println(pvr);
    }

    @Test
    public void upgrade_BES_T_X_E() throws Exception {
        upgrade(PackageType.ASiC_E, SignatureFormat.XAdES, SignatureType.ES_BES, SignatureType.ES_T);
    }

    @Test
    public void upgrade_BES_T_C_E() throws Exception {
        upgrade(PackageType.ASiC_E, SignatureFormat.CAdES, SignatureType.ES_BES, SignatureType.ES_T);
    }
}
