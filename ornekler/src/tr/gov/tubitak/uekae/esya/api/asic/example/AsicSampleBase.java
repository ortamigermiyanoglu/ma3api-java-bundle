package tr.gov.tubitak.uekae.esya.api.asic.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.SampleBase;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.signature.Context;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureFormat;
import tr.gov.tubitak.uekae.esya.api.signature.SignatureType;
import tr.gov.tubitak.uekae.esya.api.signature.config.Config;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.PackageType;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.SignaturePackage;
import tr.gov.tubitak.uekae.esya.api.signature.sigpackage.SignaturePackageFactory;
import tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager.SmartCardManager;

import java.io.File;

/**
 * Provides required variables and functions for ASiC examples
 */
public class AsicSampleBase extends SampleBase {

    private static Logger logger = LoggerFactory.getLogger(AsicSampleBase.class);

    private static String baseDir;            // base directory where signatures created
    private static File file;                 // file to be signed
    private static ECertificate certificate;  // certificate
    private static BaseSigner signer;         // signer

    static {

        try {

            baseDir = getRootDir() + "/testdata/";
            file = new File(baseDir + "sample.txt");
            certificate = SmartCardManager.getInstance().getSignatureCertificate(isQualified());
            signer = SmartCardManager.getInstance().getSigner(getPin(), certificate);

        } catch (Exception e) {
            logger.error("Error in AsicSampleBase", e);
        }
    }

    /**
     * Creates an appropriate file name for ASiC signatures
     *
     * @param packageType package type of the signature, ASiC_S or ASiC_E
     * @param format      format of the signature, CAdES or XAdES
     * @param type        type of the signature, BES etc.
     * @return file name of associated signature as string
     */
    protected String fileName(PackageType packageType, SignatureFormat format, SignatureType type) throws Exception {
        String fileName = baseDir + packageType + "-" + format.name() + "-" + type;
        switch (packageType) {
            case ASiC_S:
                return fileName + ".asics";
            case ASiC_E:
                return fileName + ".asice";
        }
        return null;
    }

    /**
     * Reads an ASiC signature
     *
     * @param packageType type of the ASiC signature to be read, ASiC_S or ASiC_E
     * @param format      format of the ASiC signature to be read, CAdES or XAdES
     * @param type        type of the ASiC signature to be read, BES etc.
     * @return signature package of ASiC signature
     * @throws Exception
     */
    protected SignaturePackage read(PackageType packageType, SignatureFormat format, SignatureType type) throws Exception {
        Context context = createContext();
        File f = new File(fileName(packageType, format, type));
        return SignaturePackageFactory.readPackage(context, f);
    }

    /**
     * Creates context for signature creation and validation
     *
     * @return created context
     */
    protected Context createContext() throws Exception {
        Context context = new Context(new File(baseDir).toURI());
        context.setConfig(new Config(getRootDir() + "/config/esya-signature-config.xml"));
        //context.setData(getContent()); //for detached CAdES signatures validation
        return context;
    }

    /**
     * Gets file to be signed
     *
     * @return the file
     * @throws Exception
     */
    protected File getFile() throws Exception {
        return file;
    }

    /**
     * Gets certificate from smartcard
     *
     * @return the certificate
     */
    protected ECertificate getCertificate() {
        return certificate;
    }

    /**
     * Gets signer from smartcard
     *
     * @return the signer
     */
    protected BaseSigner getSigner() {
        return signer;
    }

}
