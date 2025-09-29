package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "buildingUnitChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class BuildingUnitChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "BuildingUnit")
    public BuildingUnit buildingUnit;

    @XmlElement(name = "buildingNumberIdentifier")
    public String buildingNumberIdentifier;
}
