//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.05.18 at 03:36:27 PM EET 
//


package tr.gov.tubitak.xml.signature;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CertificateInitialsReturn" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "certificateInitialsReturn"
})
@XmlRootElement(name = "CertificateInitialsResponse")
public class CertificateInitialsResponse {

    @XmlElement(name = "CertificateInitialsReturn")
    protected boolean certificateInitialsReturn;

    /**
     * Gets the value of the certificateInitialsReturn property.
     * 
     */
    public boolean isCertificateInitialsReturn() {
        return certificateInitialsReturn;
    }

    /**
     * Sets the value of the certificateInitialsReturn property.
     * 
     */
    public void setCertificateInitialsReturn(boolean value) {
        this.certificateInitialsReturn = value;
    }

}
