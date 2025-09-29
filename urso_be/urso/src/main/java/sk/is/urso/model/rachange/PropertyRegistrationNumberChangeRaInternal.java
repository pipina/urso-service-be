package sk.is.urso.model.rachange;

import lombok.Setter;
import sk.is.urso.csru.ra.changes.BuildingCType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;

@XmlRootElement(name = "propertyRegistrationNumberChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Setter
public class PropertyRegistrationNumberChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "PropertyRegistrationNumber")
    public BigInteger propertyRegistrationNumber;

    @XmlElement(name = "Building")
    public BuildingCType building;

    @XmlElement(name = "municipalityIdentifier")
    public Long municipalityIdentifier;

    @XmlElement(name = "districtIdentifier")
    public Long districtIdentifier;
}
