package sk.is.urso.model.rachange;

import lombok.Setter;
import sk.is.urso.csru.ra.changes.BuildingUnitSCType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "buildingUnitChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Setter
public class BuildingUnitChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name="BuildingUnit")
    public BuildingUnitSCType buildingUnit;

    @XmlElement(name="buildingNumberIdentifier")
    public Long buildingNumberIdentifier;
}
