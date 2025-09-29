package sk.is.urso.model.csru.api.async.GetReferenceDataByIdentifiersServiceAsync;

import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;
import org.jvnet.jaxb2_commons.lang.ToString2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import sk.is.urso.model.csru.api.async.common.ParameterListCType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetReferenceDataByIdentifiersRequestCType complex type.
 *
 *
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 *
 *
 * <pre>
 *
 * &lt;complexType name="GetReferenceDataByIdentifiersRequestCType"&gt;
 *
 * &lt;complexContent&gt;
 *
 * &lt;restriction base="{
 * http://www.w3.org/2001/XMLSchema}anyType"
 * &gt;
 *
 * &lt;sequence&gt;
 *
 * &lt;element name="ovmIsId" type="{
 * http://csru.gov.sk/common/v1.4}OvmIsIdType"/
 * &gt;
 *
 * &lt;element name="oeId" type="{
 * http://csru.gov.sk/common/v1.4}OeIdType"/
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
 * &lt;element name="parameters" type="{
 * http://csru.gov.sk/common/v1.4}ParameterListCType"/
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
@XmlType(name = "GetReferenceDataByIdentifiersRequestCType", propOrder = {"ovmIsId",
        "oeId",
        "ovmTransactionId",
        "ovmCorrelationId",
        "parameters"})
public class GetReferenceDataByIdentifiersRequestCType implements ToString2 {

    @XmlElement(required = true)
    protected String ovmIsId;

    @XmlElement(required = true)
    protected String oeId;

    @XmlElement(required = true)
    protected String ovmTransactionId;

    @XmlElement(required = true)
    protected String ovmCorrelationId;

    @XmlElement(required = true)
    protected ParameterListCType parameters;

    /**
     * Gets the value of the ovmIsId property.
     *
     * @return possible object is
     * <p>
     * {@link String }
     */

    public String getOvmIsId() {
        return ovmIsId;
    }

    /**
     * Sets the value of the ovmIsId property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link String }
     */

    public void setOvmIsId(String value) {
        this.ovmIsId = value;
    }

    /**
     * Gets the value of the oeId property.
     *
     * @return possible object is
     * <p>
     * {@link String }
     */

    public String getOeId() {
        return oeId;
    }

    /**
     * Sets the value of the oeId property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link String }
     */

    public void setOeId(String value) {
        this.oeId = value;
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
     * Gets the value of the parameters property.
     *
     * @return possible object is
     * <p>
     * {@link ParameterListCType }
     */

    public ParameterListCType getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     *
     * @param value allowed object is
     *              <p>
     *              {@link ParameterListCType }
     */

    public void setParameters(ParameterListCType value) {
        this.parameters = value;
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
            String theOvmIsId;
            theOvmIsId = this.getOvmIsId();
            strategy.appendField(locator, this, "ovmIsId", buffer, theOvmIsId, (this.ovmIsId != null));
        }
        {
            String theOeId;
            theOeId = this.getOeId();
            strategy.appendField(locator, this, "oeId", buffer, theOeId, (this.oeId != null));
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
            ParameterListCType theParameters;
            theParameters = this.getParameters();
            strategy.appendField(locator, this, "parameters", buffer, theParameters, (this.parameters != null));
        }
        return buffer;
    }
}
