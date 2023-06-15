package tr.gov.tubitak.uekae.esya.api.smartcard.example.smartcardmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.StringUtil;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.LoginException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartOp;

import java.security.cert.CertificateEncodingException;
import java.security.spec.AlgorithmParameterSpec;

/**
 * SmartCardManager handles user smart card operations. Uses APDU access to card as default.
 */
public class SmartCardManager extends SmartCardManagerBase {

    private static Logger LOGGER = LoggerFactory.getLogger(SmartCardManager.class);

    private static Object lockObject = new Object();
    private static SmartCardManager mSCManager;

    /**
     * @throws SmartCardException
     */
    public SmartCardManager() throws SmartCardException {
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
    public static SmartCardManager getInstance() throws SmartCardException {

        synchronized (lockObject) {
            if (mSCManager == null) {
                mSCManager = new SmartCardManager();
                return mSCManager;
            } else {
                //Check is there any change
                try {
                    //If there is a new card in the system, user will select a smartcard.
                    //Create new SmartCard.
                    if (mSCManager.getSlotCount() < SmartOp.getCardTerminals().length) {
                        LOGGER.debug("New card pluged in to system");
                        mSCManager = null;
                        return getInstance();
                    }

                    //If used card is removed, select new card.
                    String availableSerial = null;
                    try {
                        availableSerial = StringUtil.toString(mSCManager.getBasicSmartCard().getSerial());
                    } catch (SmartCardException ex) {
                        LOGGER.debug("Card removed");
                        mSCManager = null;
                        return getInstance();
                    }
                    if (!mSCManager.getSelectedSerialNumber().equals(availableSerial)) {
                        LOGGER.debug("Serial number changed. New card is placed to system");
                        mSCManager = null;
                        return getInstance();
                    }

                    return mSCManager;
                } catch (SmartCardException e) {
                    mSCManager = null;
                    throw e;
                }
            }
        }
    }

    public static void reset() throws SmartCardException {
        synchronized (lockObject) {
            mSCManager = null;
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
    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert.asX509Certificate());
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
    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert, String aSigningAlg, AlgorithmParameterSpec aParams) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert.asX509Certificate(), aSigningAlg, aParams);
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
    public synchronized ECertificate getSignatureCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException{

        byte[] encodedX509Cert = null;
        try {
            encodedX509Cert = getSignatureCertificateBase(checkIsQualified, checkBeingNonQualified).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ESYAException("Error in encoding X509 Certificate");
        }

        return new ECertificate(encodedX509Cert);
    }

    public synchronized ECertificate getSignatureCertificate(boolean isQualified) throws ESYAException {
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
    public synchronized ECertificate getEncryptionCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException {
        byte[] encodedX509Cert = null;
        try {
           encodedX509Cert = getEncryptionCertificateBase(checkIsQualified,checkBeingNonQualified).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ESYAException("Error in encoding X509 Certificate");
        }
        return new ECertificate(encodedX509Cert);
    }
}
