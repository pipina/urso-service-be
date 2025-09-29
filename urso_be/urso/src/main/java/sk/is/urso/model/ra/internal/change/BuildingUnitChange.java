package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "buildingUnitChange")
@Getter
public class BuildingUnitChange extends AbstractChange {

    // ChangeBaseCType

    @XmlElement(name="BuildingUnit")
    private BuildingUnit buildingUnit;

    @XmlElement(name="buildingNumberIdentifier")
    private String buildingNumberIdentifier;
}
