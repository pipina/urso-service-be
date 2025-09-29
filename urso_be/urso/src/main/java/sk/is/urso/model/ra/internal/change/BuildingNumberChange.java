package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "buildingNumberChange")
@Getter
public class BuildingNumberChange extends AbstractChange {

    @XmlElement(name="BuildingNumber")
    public String buildingNumber;

    @XmlElement(name="BuildingIndex")
    public String buildingIndex;

    @XmlElement(name="PostalCode")
    public String postalCode;

    @XmlElement(name="AddressPoint")
    public AddressPoint addressPoint;

    @XmlElement(name="propertyRegistrationNumberIdentifier")
    public String propertyRegistrationNumberIdentifier;

    @XmlElement(name="streetNameIdentifier")
    public String streetNameIdentifier;

    @XmlElement(name="verifiedAt")
    public String verifiedAt;

}
