package sk.is.urso.model.ra.internal.change;


import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BuildingUnit")
@Getter
public class BuildingUnit {

    @XmlAttribute(name="UnitNumber")
    public String unitNumber;

    @XmlAttribute(name="Floor")
    public Integer floor;

    @XmlValue
    public String value;
}
