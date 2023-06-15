
* Örnekler içerisinde sunucu ve istemci-sunucu yapısında iki farklı proje bulunmaktadır.
* Projeler çalıştırılmadan önce
  * Pom.xml dosyasındaki versiyon bilgisi ve kullanılacak kütüphanelerin bulunduğu dizini gösteren alanlar uygun şekilde değiştirilmelidir.
    <ma3api.version>2.2.24</ma3api.version>
    <libdirectory>${project.basedir}/../../../lib</libdirectory>
  * Lisans.xml ve imza doğrulama için kullanılan certval-policy-test.xml dosya dizinleri uygun şekilde değiştirilmelidir.
  * Operatör tarafından sağlanan mobil imza parametreleri örnek projedekiler ile değiştirilmelidir. (Operatör mobil imza servisi adresi, kullanıcı adı yerine geçen APID ve Parola)
  * MobileSignatureClient projesinin bağlanacağı sunucunun adresi SOAPClient sınıfı içerisinden ayarlanmalıdır.
* Spring Boot kullanılarak oluşturulmuş uygulamalar mvn clean install komutu ile derlendikten sonra mvn spring-boot:run komutu ile çalıştırılabilir.
  
  
  
  
  


