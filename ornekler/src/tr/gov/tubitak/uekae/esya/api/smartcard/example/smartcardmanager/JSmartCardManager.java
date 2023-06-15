package tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.StringUtil;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.LoginException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartOp;

import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;

/**
 * SmartCardManager handles user smart card operations. Uses APDU access to card as default.
 */
public class JSmartCardManager extends SmartCardManagerBase{

    private static Logger LOGGER = LoggerFactory.getLogger(JSmartCardManager.class);

    private static Object lockObject = new Object();
    private static JSmartCardManager mJSCManager;

    /**
     * @throws SmartCardException
     */
    public JSmartCardManager() throws SmartCardException {
        super();
    }

    /**
     * Singleton is used for this class. If many card placed, it wants to user to select one of cards.
     * If there is a influential change in the smart card environment, it  repeat the selection process.
     * The influential change can be:
     * If there is a new smart card connected to system.
     * The cached card is removed from system.
     * These situations are checked in getInstance() function. So for your non-squential SmartCard Operation,
     * call getInstance() function to check any change in the system.
     * <p>
     * In order to reset thse selections, call reset function.
     *
     * @return SmartCardManager instance
     * @throws SmartCardException
     */
    public static JSmartCardManager getInstance() throws SmartCardException {
        synchronized (lockObject) {
            if (mJSCManager == null) {
                mJSCManager = new JSmartCardManager();
                return mJSCManager;
            } else {
                //Check is there any change
                try {
                    //If there is a new card in the system, user will select a smartcard.
                    //Create new SmartCard.
                    if (mJSCManager.getSlotCount() < SmartOp.getCardTerminals().length) {
                        LOGGER.debug("New card pluged in to system");
                        mJSCManager = null;
                        return getInstance();
                    }

                    //If used card is removed, select new card.
                    String availableSerial = null;
                    try {
                        availableSerial = StringUtil.toString(mJSCManager.getBasicSmartCard().getSerial());
                    } catch (SmartCardException ex) {
                        LOGGER.debug("Card removed");
                        mJSCManager = null;
                        return getInstance();
                    }
                    if (!mJSCManager.getSelectedSerialNumber().equals(availableSerial)) {
                        LOGGER.debug("Serial number changed. New card is placed to system");
                        mJSCManager = null;
                        return getInstance();
                    }

                    return mJSCManager;
                } catch (SmartCardException e) {
                    mJSCManager = null;
                    throw e;
                }
            }
        }
    }

    public static void reset() throws SmartCardException {
        synchronized (lockObject) {
            mJSCManager = null;
        }
    }

    /**
     * BaseSigner interface for the requested certificate. Do not forget to logout after your crypto
     * operation finished
     *
     * @param aCardPIN
     * @param aCert
     * @return
     * @throws SmartCardException
     * @throws LoginException
     */
    public synchronized BaseSigner getSigner(String aCardPIN, X509Certificate aCert) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert);
    }

    /**
     * BaseSigner interface for the requested certificate. Do not forget to logout after your crypto
     * operation finished
     *
     * @param aCardPIN
     * @param aCert
     * @return
     * @throws SmartCardException
     * @throws LoginException
     */
    public synchronized BaseSigner getSigner(String aCardPIN, X509Certificate aCert, String aSigningAlg, AlgorithmParameterSpec aParams) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert, aSigningAlg, aParams);
    }

    /**
     * Returns for the signature certificate. If there are more than one certificates in the card in requested
     * attributes, it wants user to select the certificate. It caches the selected certificate, to reset cache,
     * call reset function.
     *
     * @param checkIsQualified       Only selects the qualified certificates if it is true.
     * @param checkBeingNonQualified Only selects the non-qualified certificates if it is true.
     *                               if the two parameters are false, it selects all certificates.
     *                               if the two parameters are true, it throws ESYAException. A certificate can not be qualified and non qualified at
     *                               the same time.
     * @return certificate
     * @throws SmartCardException
     * @throws ESYAException
     */
    public synchronized X509Certificate getSignatureCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws SmartCardException, ESYAException {
        return getSignatureCertificateBase(checkIsQualified, checkBeingNonQualified);
    }

    /**
     * Returns for the signature certificate. If there are more than one certificates in the card in requested
     * attributes, it wants user to select the certificate. It caches the selected certificate, to reset cache,
     * call reset function.
     *
     * @param isQualified Only selects the qualified certificates if it is true. If it is false only non-qualified
     *                    certificates are selected.
     * @return certificate
     * @throws SmartCardException
     * @throws ESYAException
     */
    public synchronized X509Certificate getSignatureCertificate(boolean isQualified) throws ESYAException {
        return getSignatureCertificate(isQualified, !isQualified);
    }

    /**
     * Returns for the encryption certificate. If there are more than one certificates in the card in requested
     * attributes, it wants user to select the certificate. It caches the selected certificate, to reset cache,
     * call reset function.
     *
     * @param checkIsQualified
     * @param checkBeingNonQualified
     * @return
     * @throws SmartCardException
     * @throws ESYAException
     */
    public synchronized X509Certificate getEncryptionCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws SmartCardException, ESYAException {
        return getEncryptionCertificateBase(checkIsQualified, checkBeingNonQualified);
    }
}
