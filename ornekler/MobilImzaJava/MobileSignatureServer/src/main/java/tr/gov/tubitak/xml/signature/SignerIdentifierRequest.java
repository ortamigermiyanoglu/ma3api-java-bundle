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
 *         &lt;element name="SignerIdentifier" type="{http://www.tubitak.gov.tr/xml/signature}void"/>
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
    "signerIdentifier"
})
@XmlRootElement(name = "SignerIdentifierRequest")
public class SignerIdentifierRequest {

    @XmlElement(name = "SignerIdentifier", required = true)
    protected Void signerIdentifier;

    /**
     * Gets the value of the signerIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link Void }
     *     
     */
    public Void getSignerIdentifier() {
        return signerIdentifier;
    }

    /**
     * Sets the value of the signerIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link Void }
     *     
     */
    public void setSignerIdentifier(Void value) {
        this.signerIdentifier = value;
    }

}
