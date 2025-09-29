package sk.is.urso.model.rachange;

import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "streetNameChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Setter
public class StreetNameChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "StreetName")
    public String StreetName;

    @XmlElement(name = "municipalityIdentifier")
    public List<Long> municipalityIdentifier;

    @XmlElement(name = "districtIdentifier")
    public List<Long> districtIdentifier;
}
