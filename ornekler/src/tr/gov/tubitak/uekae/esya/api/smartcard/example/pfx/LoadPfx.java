package tr.gov.tubitak.uekae.esya.api.smartcard.example.pfx;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import tr.gov.tubitak.uekae.esya.api.SampleBase;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.util.bag.Pair;
import tr.gov.tubitak.uekae.esya.api.crypto.util.PfxParser;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.CardType;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCard;

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoadPfx extends SampleBase {

    String label = "Test Sertifikasi";



    @Test
    public void test_1_LoadPfx() throws Exception {

        SmartCard sc = new SmartCard(CardType.AKIS);
        //Dogrudan ilk karta baglanılıyor.
        long session = sc.openSession(1);
        //PIN
        sc.login(session, getPin());

        //Pfx okunuyor.
        FileInputStream fis = new FileInputStream(getRootDir() + "/sertifika deposu/test1@test.com_745418.pfx");
        PfxParser pfxParser = new PfxParser(fis, "745418".toCharArray());
        List<Pair<ECertificate, PrivateKey>> entries = pfxParser.getCertificatesAndKeys();

        for (Pair<ECertificate, PrivateKey> pair : entries) {
            ECertificate cert = pair.getObject1();
            KeyPair keyPair = new KeyPair(cert.asX509Certificate().getPublicKey(), pair.getObject2());



            //Anahtar çifti ve sertifika karta yükleniyor.
            sc.importKeyPair(session, label, keyPair, null, true, false);
            sc.importCertificate(session, label, cert.asX509Certificate());
        }

        sc.logout(session);
    }


    @Test
    public void test_2_DeleteObjects() throws Exception {

        SmartCard sc = new SmartCard(CardType.AKIS);
        //Dogrudan ilk karta baglanılıyor.
        long session = sc.openSession(1);
        //PIN
        sc.login(session, getPin());

        sc.deleteCertificate(session, label);
        sc.deletePrivateObject(session, label);
        sc.deletePublicObject(session,label);

        sc.logout(session);
    }
}
