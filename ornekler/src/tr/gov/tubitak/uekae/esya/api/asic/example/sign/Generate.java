package tr.gov.tubitak.uekae.esya.api.asic.example.sign;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asic.example.AsicSampleBase;
import tr.gov.tubitak.uekae.esya.api.signature.*;
import tr.gov.tubitak.uekae.esya.api.signature.impl.SignableFile;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.PackageType;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.PackageValidationResult;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.SignaturePackage;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.SignaturePackageFactory;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Generates ASiC-S and -E signatures with CAdES and XAdES BES
 */
public class Generate extends AsicSampleBase {

    public void createBES(PackageType packageType, SignatureFormat format) throws Exception {

        Context c = createContext();

        SignaturePackage signaturePackage = SignaturePackageFactory.createPackage(c, packageType, format);

        // add into zip
        Signable inPackage = signaturePackage.addData(new SignableFile(getFile(), "text/plain"), "sample.txt");

        SignatureContainer container = signaturePackage.createContainer();

        Signature signature = container.createSignature(getCertificate());

        // pass document in ZIP to signature
        signature.addContent(inPackage, false);

        signature.sign(getSigner());

        String fileName = fileName(packageType, format, SignatureType.ES_BES);
        signaturePackage.write(new FileOutputStream(fileName));

        // read it back
        signaturePackage = SignaturePackageFactory.readPackage(c, new File(fileName));

        // validate
        PackageValidationResult pvr = signaturePackage.verifyAll();

        // output results
        System.out.println(pvr);
    }

    @Test
    public void createASiC_S_CAdES() throws Exception {
        createBES(PackageType.ASiC_S, SignatureFormat.CAdES);
    }

    @Test
    public void createASiC_E_CAdES() throws Exception {
        createBES(PackageType.ASiC_E, SignatureFormat.CAdES);
    }

    @Test
    public void createASiC_S_XAdES() throws Exception {
        createBES(PackageType.ASiC_S, SignatureFormat.XAdES);
    }

    @Test
    public void createASiC_E_XAdES() throws Exception {
        createBES(PackageType.ASiC_E, SignatureFormat.XAdES);
    }
}