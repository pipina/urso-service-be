package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "streetNameChange")
@Getter
public class StreetNameChange extends AbstractChange {

    @XmlElement(name="StreetName")
    private String streetName;

    @XmlElement(name="municipalityIdentifier")
    private String municipalityIdentifier;

    @XmlElement(name="districtIdentifier")
    private String districtIdentifier;
}
