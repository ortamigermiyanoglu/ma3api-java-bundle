package tr.gov.tubitak.uekae.esya.api.MobileSignatureService;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;


@EnableWs
@Configuration
public class Config extends WsConfigurerAdapter 
{
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) 
    {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<MessageDispatcherServlet>(servlet, "/service/*");
    }
 
    @Bean(name = "MobileSignatureService")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) 
    {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("MobileSignatureServicePort");
        wsdl11Definition.setLocationUri("/service/MobileSignatureService");
        wsdl11Definition.setTargetNamespace("http://www.tubitak.gov.tr/xml/signature");
        wsdl11Definition.setSchema(countriesSchema);
        return wsdl11Definition;
    }
 
    @Bean
    public XsdSchema signatureSchema() 
    {
        return new SimpleXsdSchema(new ClassPathResource("signature.xsd"));
    }
}
