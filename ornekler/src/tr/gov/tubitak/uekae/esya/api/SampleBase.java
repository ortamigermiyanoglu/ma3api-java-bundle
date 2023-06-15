package tr.gov.tubitak.uekae.esya.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;
import tr.gov.tubitak.uekae.esya.api.common.util.VersionUtil;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides required variables and functions
 */
public class SampleBase {

    protected static Logger logger = LoggerFactory.getLogger(SampleBase.class);

    // bundle root directory of project
    private static final String ROOT_DIR = "C:/ma3api-java-bundle";

    // gets only qualified certificates in smart card
    private static final boolean IS_QUALIFIED = true;

    // the pin of the smart card
    private static final String PIN_SMARTCARD = "12345";

    static {

        try {

            LicenseUtil.setLicenseXml(new FileInputStream(ROOT_DIR + "/lisans/lisans.xml"));

            Date expirationDate = LicenseUtil.getExpirationDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("License expiration date :" + dateFormat.format(expirationDate));
            System.out.println("MA3 API version: " + VersionUtil.getAPIVersion());

            /* // To set class path
            URL root = SampleBase.class.getResource("/");
            String classPath = root.getPath();
            File binDir = new File(classPath);
            ROOT_DIR = binDir.getParentFile().getParent();
            */

            /* // To sign with pfx file
            String PFX_FILE = ROOT_DIR + "/sertifika deposu/test1@test.com_745418.pfx";
            String PFX_PASS = "745418";
            PfxSignTest pfxSigner = new PfxSignTest(SignatureAlg.RSA_SHA256, PFX_FILE, PFX_PASS.toCharArray());
            certificate = pfxSigner.getSignersCertificate();
            */

        } catch (Exception e) {
            logger.error("Error in SampleBase", e);
        }
    }

    /**
     * Gets the bundle root directory of project
     *
     * @return the root dir
     */
    protected static String getRootDir() {
        return ROOT_DIR;
    }

    /**
     * Gets the pin of the smart card
     *
     * @return the pin
     */
    protected static String getPin() throws ESYAException {
        throw new ESYAException("Set the pin of the smart card!");
        //return PIN_SMARTCARD;
    }

    /**
     * The parameter to choose the qualified certificates in smart card
     *
     * @return the
     */
    protected static boolean isQualified() {
        return IS_QUALIFIED;
    }

}
