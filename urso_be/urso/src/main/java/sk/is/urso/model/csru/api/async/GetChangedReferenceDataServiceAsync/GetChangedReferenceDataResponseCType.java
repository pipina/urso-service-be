package sk.is.urso.model.csru.api.async.GetChangedReferenceDataServiceAsync;

import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetChangedReferenceDataResponseCType complex type.
 *
 *
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 *
 *
 * <pre>
 *
 * &lt;complexType name="GetChangedReferenceDataResponseCType"&gt;
 *
 * &lt;complexContent&gt;
 *
 * &lt;restriction base="{
 * http://www.w3.org/2001/XMLSchema}anyType"
 * &gt;
 *
 * &lt;sequence&gt;
 *
 * &lt;element name="csruTransactionId" type="{
 * http://csru.gov.sk/common/v1.4}CsruTransactionIdType"/
 * &gt;
 *
 * &lt;element name="ovmTransactionId" type="{
 * http://csru.gov.sk/common/v1.4}OvmTransactionIdType"/
 * &gt;
 *
 * &lt;element name="ovmCorrelationId" type="{
 * http://csru.gov.sk/common/v1.4}OvmCorrelationIdType"/
 * &gt;
 *
 * &lt;element name="resultCode" type="{
 * http://csru.gov.sk/common/v1.4}ResultCodeType"/
 * &gt;
 *
 * &lt;element name="requestId" type="{
 * http://csru.gov.sk/common/v1.4}RequestIdType" minOccurs="0"/
 * &gt;
 *
 * &lt;element name="errorMessage" type="{
 * http://csru.gov.sk/common/v1.4}ErrorMessageType" minOccurs="0"/
 * &gt;
 *
 * &lt;/sequence&gt;
 *
 * &lt;/restriction&gt;
 *
 * &lt;/complexContent&gt;
 *
 * &lt;/complexType&gt;
 *
 * </pre>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetChangedReferenceDataResponseCType", propOrder = {"csruTransactionId",

        "ovmTransactionId",

        "ovmCorrelationId",

        "resultCode",

        "requestId",

        "errorMessage"})
public class GetChangedReferenceDataResponseCType implements ToString2 {

    @XmlElement(required = true)
    protected String csruTransactionId;

    @XmlElement(required = true)
    protected String ovmTransactionId;

    @XmlElement(required = true)
    protected String ovmCorrelationId;

    @XmlSchemaType(name = "integer")
    protected int resultCode;

    protected Long requestId;

    protected String errorMessage;

    /**
     * Gets the value of the csruTransactionId property.
     *
     * @return possible object is
     * <p>
     * {@link String }
     */

    public String getCsruTransactionId() {
        return csruTransactionId;
    }

    /**
     * Sets the value of the csruTransactionId property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link String }
     */

    public void setCsruTransactionId(String value) {
        this.csruTransactionId = value;
    }

    /**
     * Gets the value of the ovmTransactionId property.
     *
     * @return possible object is
     * <p>
     * {@link String }
     */

    public String getOvmTransactionId() {
        return ovmTransactionId;
    }

    /**
     * Sets the value of the ovmTransactionId property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link String }
     */

    public void setOvmTransactionId(String value) {
        this.ovmTransactionId = value;
    }

    /**
     * Gets the value of the ovmCorrelationId property.
     *
     * @return possible object is
     * <p>
     * {@link String }
     */

    public String getOvmCorrelationId() {
        return ovmCorrelationId;
    }

    /**
     * Sets the value of the ovmCorrelationId property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link String }
     */

    public void setOvmCorrelationId(String value) {
        this.ovmCorrelationId = value;
    }

    /**
     * Gets the value of the resultCode property.
     */

    public int getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     */

    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the requestId property.
     *
     * @return possible object is
     * <p>
     * {@link Long }
     */

    public Long getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link Long }
     */

    public void setRequestId(Long value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the errorMessage property.
     *
     * @return possible object is
     * <p>
     * {@link String }
     */

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link String }
     */

    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    public String toString() {
        final ToStringStrategy2 strategy = JAXBToStringStrategy.INSTANCE;

        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);

        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy2 strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);

        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy2 strategy) {
        {
            String theCsruTransactionId;
            theCsruTransactionId = this.getCsruTransactionId();
            strategy.appendField(locator, this, "csruTransactionId", buffer, theCsruTransactionId, (this.csruTransactionId != null));
        }
        {
            String theOvmTransactionId;
            theOvmTransactionId = this.getOvmTransactionId();
            strategy.appendField(locator, this, "ovmTransactionId", buffer, theOvmTransactionId, (this.ovmTransactionId != null));
        }
        {
            String theOvmCorrelationId;
            theOvmCorrelationId = this.getOvmCorrelationId();
            strategy.appendField(locator, this, "ovmCorrelationId", buffer, theOvmCorrelationId, (this.ovmCorrelationId != null));
        }
        {
            int theResultCode;
            theResultCode = this.getResultCode();
            strategy.appendField(locator, this, "resultCode", buffer, theResultCode, true);
        }
        {
            Long theRequestId;
            theRequestId = this.getRequestId();
            strategy.appendField(locator, this, "requestId", buffer, theRequestId, (this.requestId != null));
        }
        {
            String theErrorMessage;
            theErrorMessage = this.getErrorMessage();
            strategy.appendField(locator, this, "errorMessage", buffer, theErrorMessage, (this.errorMessage != null));
        }
        return buffer;
    }
}
