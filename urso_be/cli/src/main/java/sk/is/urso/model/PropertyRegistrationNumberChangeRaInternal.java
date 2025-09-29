package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "propertyRegistrationNumberChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PropertyRegistrationNumberChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "PropertyRegistrationNumber")
    public String propertyRegistrationNumber;

    @XmlElement(name = "Building")
    public ChangeRa region;

    @XmlElement(name = "municipalityIdentifier")
    public String municipalityIdentifier;

    @XmlElement(name = "districtIdentifier")
    public String districtIdentifier;
}
