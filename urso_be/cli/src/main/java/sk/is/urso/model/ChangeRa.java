package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ChangeRa {

    @XmlElement(name = "Codelist")
    public Codelist codelist;

    @XmlAttribute(name = "UniqueNumbering")
    public String uniqueNumbering;

    public String unitNumber;

    public Integer floor;

    public String buildingUnit;

    @XmlElement(name = "XYH")
    public XYH XYH;

    @XmlElement(name = "BLH")
    public BLH BLH;

    @XmlElement(name = "AddressPointID")
    public String addressPointID;

    @XmlAttribute(name = "ContainsFlats")
    public String containsFlats;

    @XmlElement(name = "BuildingName")
    public String buildingName;

    @XmlElement(name = "BuildingPurpose")
    public ChangeRa buildingPurpose;

    @XmlElement(name = "BuildingTypeCode")
    public ChangeRa buildingTypeCode;
}
