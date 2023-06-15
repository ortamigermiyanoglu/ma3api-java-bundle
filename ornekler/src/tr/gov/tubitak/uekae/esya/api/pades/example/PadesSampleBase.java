package tr.gov.tubitak.uekae.esya.api.pades.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.SampleBase;
import tr.gov.tubitak.uekae.esya.api.pades.pdfbox.PAdESContext;
import tr.gov.tubitak.uekae.esya.api.signature.config.Config;

import java.io.File;

public class PadesSampleBase extends SampleBase {

    protected static Logger logger = LoggerFactory.getLogger(PadesSampleBase.class);

    private static String testDataFolder;
    private static File testFile;

    static {
        try {

            testDataFolder = getRootDir() + "/testdata/";
            testFile = new File(testDataFolder + "sample.pdf");

        } catch (Exception e) {
            logger.error("Error in PadesSampleBase", e);
        }
    }

    /**
     * Creates context for signature creation and validation
     *
     * @return created context
     */
    protected PAdESContext createContext() {
        PAdESContext c = new PAdESContext(new File(testDataFolder).toURI());
        c.setConfig(new Config(getRootDir() + "/config/esya-signature-config.xml"));
        return c;
    }

    /**
     * Gets the test data folder
     *
     * @return the test data folder
     */
    protected String getTestDataFolder() {
        return testDataFolder;
    }

    /**
     * Gets the test file
     *
     * @return the test file
     */
    protected File getTestFile() {
        return testFile;
    }

}
