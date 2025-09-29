package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "districtChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DistrictChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "District")
    public ChangeRa region;

    @XmlElement(name = "municipalityIdentifier")
    public String municipalityIdentifier;

}
