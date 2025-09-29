package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "buildingNumberChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BuildingNumberChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "BuildingNumber")
    public String buildingNumber;

    @XmlElement(name = "BuildingIndex")
    public String buildingIndex;

    @XmlElement(name = "PostalCode")
    public String postalCode;

    @XmlElement(name = "AddressPoint")
    public ChangeRa region;

    @XmlElement(name = "propertyRegistrationNumberIdentifier")
    public String propertyRegistrationNumberIdentifier;

    @XmlElement(name = "streetNameIdentifier")
    public String streetNameIdentifier;

    @XmlElement(name = "verifiedAt")
    public String verifiedAt;
}
