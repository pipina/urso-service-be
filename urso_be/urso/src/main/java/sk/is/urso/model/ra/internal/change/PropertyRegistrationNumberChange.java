package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "propertyRegistrationNumberChange")
@Getter
public class PropertyRegistrationNumberChange extends AbstractChange {

    @XmlElement(name="PropertyRegistrationNumber")
    private String propertyRegistrationNumber;

    @XmlElement(name="Building")
    private Building region;

    @XmlElement(name="municipalityIdentifier")
    private String municipalityIdentifier;

    @XmlElement(name="districtIdentifier")
    private String districtIdentifier;


}
