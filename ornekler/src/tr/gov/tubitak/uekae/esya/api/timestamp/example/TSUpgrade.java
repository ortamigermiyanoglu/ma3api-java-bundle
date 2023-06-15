package tr.gov.tubitak.uekae.esya.api.timestamp.example;

import org.junit.Test;
import tr.gov.tubitak.uekae.esya.api.asn.cms.ESignedData;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cades.example.CadesSampleBase;
import tr.gov.tubitak.uekae.esya.api.cades.example.validation.CadesSignatureValidation;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.Signer;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedDataValidationResult;
import tr.gov.tubitak.uekae.esya.api.cmssignature.validation.SignedData_Status;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.signature.attribute.TimestampInfo;
import tr.gov.tubitak.uekae.esya.asn.util.AsnIO;


import java.util.*;

import static org.junit.Assert.assertEquals;

public class TSUpgrade extends CadesSampleBase {

    @Test
    public void upgradeTimeStampTest() throws Exception{

        String timeStampFile = "zdFile";
        boolean upgraded = upgradeTimeStamp(timeStampFile);

        if(upgraded)
            System.out.printf("Upgrade edildi.");
        else
            System.out.println("Upgrade edilmedi.");
    }

    private boolean upgradeTimeStamp(String fileToBeUpgraded) throws Exception {

        boolean upgraded = false;

        byte[] fileToBeUpgradedInBytes = AsnIO.dosyadanOKU(fileToBeUpgraded);

        BaseSignedData baseSignedData = new BaseSignedData(fileToBeUpgradedInBytes);
        Signer signer = baseSignedData.getSignerList().get(0);

        if(signer.getType().equals(ESignatureType.TYPE_BES)){

            ECertificate signerCertificate = signer.getSignerCertificate();
            if(isInTheTimeOfUpgrade(signerCertificate)){

                Map<String, Object> parameters = getParametersForUpgrade();

                signer.convert(ESignatureType.TYPE_ESXLong, parameters);
                upgraded = true;

                CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
                SignedDataValidationResult validationResult = signatureValidation.validate(signer.getBaseSignedData().getEncoded(), null);
                System.out.println(validationResult);

                assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
            }
            else {
                System.out.println(fileToBeUpgraded + "dosyasının upgrade edilmesi için daha vakit var");
            }
        }
        else if(signer.getType().equals(ESignatureType.TYPE_EST) ||  signer.getType().equals(ESignatureType.TYPE_ESXLong) || signer.getType().equals(ESignatureType.TYPE_ESA)){
            List<TimestampInfo> allTimeStamps = signer.getAllTimeStamps();
            TimestampInfo latestTimestampInfo = allTimeStamps.get(allTimeStamps.size() - 1);

            ESignedData tsSignedData = latestTimestampInfo.getSignedData();
            ECertificate tsCert = tsSignedData.getSignerInfo(0).getSignerCertificate(tsSignedData.getCertificates());

            if(isInTheTimeOfUpgrade(tsCert)){

                Map<String, Object> parameters = getParametersForUpgrade();

                signer.convert(ESignatureType.TYPE_ESA, parameters);
                upgraded = true;

                CadesSignatureValidation signatureValidation = new CadesSignatureValidation();
                SignedDataValidationResult validationResult = signatureValidation.validate(signer.getBaseSignedData().getEncoded(), null);
                System.out.println(validationResult);

                assertEquals(SignedData_Status.ALL_VALID, validationResult.getSDStatus());
            }
            else {
                System.out.println(fileToBeUpgraded + "dosyasının upgrade edilmesi için daha vakit var");
            }
        }
        return upgraded;
    }

    private Map<String, Object> getParametersForUpgrade() throws ESYAException {

        Map<String, Object> parameters = new HashMap<String, Object>();

        //Time stamp will be needed for XLONG-type signature
        //Archive time stamp will be needed for ESA-type signature
        parameters.put(EParameters.P_TSS_INFO, getTSSettings());
        parameters.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());

        return parameters;
    }

    private boolean isInTheTimeOfUpgrade(ECertificate cert) throws ESYAException {

        Calendar certStartTime = cert.getNotBefore();
        Calendar certEndTime = cert.getNotAfter();
        Calendar now = Calendar.getInstance();

        if(!(now.after(certStartTime) && now.before(certEndTime)))
            throw new ESYAException("Certificate validity period is between " + cert.getNotBefore().getTime() + " and " + cert.getNotAfter().getTime());

        now.add(Calendar.MONTH,3);
        long threeMonthsAheadInMilliseconds = now.getTimeInMillis();
        long certEndTimeInMilliseconds = certEndTime.getTimeInMillis();

        if(threeMonthsAheadInMilliseconds >= certEndTimeInMilliseconds)
            return true;
        else
            return false;
    }
}
