package sk.is.urso.model.ra.internal.change;


import lombok.Getter;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

@Getter
public class Change {

    @XmlElementRefs({
            @XmlElementRef(name = "regionChange", type = RegionChange.class, required = true),
            @XmlElementRef(name = "countyChange", type = CountyChange.class, required = true),
            @XmlElementRef(name = "municipalityChange", type = MunicipalityChange.class, required = true),
            @XmlElementRef(name = "districtChange", type = DistrictChange.class, required = true),
            @XmlElementRef(name = "streetNameChange", type = StreetNameChange.class, required = true),
            @XmlElementRef(name = "propertyRegistrationNumberChange", type = PropertyRegistrationNumberChange.class, required = true),
            @XmlElementRef(name = "buildingNumberChange", type = BuildingNumberChange.class, required = true),
            @XmlElementRef(name = "buildingUnitChange", type = BuildingUnitChange.class, required = true)
    })
    private AbstractChange abstractChange;
}
