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
 *         &lt;element name="SigningCertAttrV2Return" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "signingCertAttrV2Return"
})
@XmlRootElement(name = "SigningCertAttrV2Response")
public class SigningCertAttrV2Response {

    @XmlElement(name = "SigningCertAttrV2Return", required = true)
    protected String signingCertAttrV2Return;

    /**
     * Gets the value of the signingCertAttrV2Return property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSigningCertAttrV2Return() {
        return signingCertAttrV2Return;
    }

    /**
     * Sets the value of the signingCertAttrV2Return property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSigningCertAttrV2Return(String value) {
        this.signingCertAttrV2Return = value;
    }

}
