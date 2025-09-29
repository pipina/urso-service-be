package sk.is.urso.model.rachange;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class ChangeRa {

    @XmlElement(name="AddressPointID")
    public String addressPointID;

    @XmlAttribute(name="ContainsFlats")
    public String containsFlats;

    @XmlElement(name="BuildingName")
    public String buildingName;

    @XmlElement(name="BuildingPurpose")
    public ChangeRa buildingPurpose;

    @XmlElement(name="BuildingTypeCode")
    public ChangeRa buildingTypeCode;
}
