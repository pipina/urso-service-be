package sk.is.urso.model.ra.internal.change;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Building {

    @XmlElement(name="BuildingName")
    public String buildingName;

    @XmlElement(name="BuildingPurpose")
    public Codelist buildingPurpose;

    @XmlElement(name="BuildingTypeCode")
    public Codelist buildingTypeCode;

    @XmlAttribute(name="ContainsFlats")
    public String containsFlats;

}
