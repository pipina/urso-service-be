package sk.is.urso.model.rachange;

import lombok.Setter;
import sk.is.urso.csru.ra.changes.AddressPointCType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name = "buildingNumberChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Setter
public class BuildingNumberChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name="BuildingNumber")
    public String buildingNumber;

    @XmlElement(name="BuildingIndex")
    public String buildingIndex;

    @XmlElement(name="PostalCode")
    public String postalCode;

    @XmlElement(name="AddressPoint")
    public AddressPointCType addressPoint;

    @XmlElement(name="propertyRegistrationNumberIdentifier")
    public Long propertyRegistrationNumberIdentifier;

    @XmlElement(name="streetNameIdentifier")
    public Long streetNameIdentifier;

    @XmlElement(name="verifiedAt")
    public XMLGregorianCalendar verifiedAt;
}
