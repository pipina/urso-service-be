package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "streetNameChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class StreetNameChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "StreetName")
    public String StreetName;

    @XmlElement(name = "municipalityIdentifier")
    public String municipalityIdentifier;

    @XmlElement(name = "districtIdentifier")
    public String districtIdentifier;
}
