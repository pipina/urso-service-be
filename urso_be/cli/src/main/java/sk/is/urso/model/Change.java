package sk.is.urso.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

public class Change {

    @XmlElement(name = "count")
    public String count;

    @XmlElementRefs({
            @XmlElementRef(name = "regionChange", type = RegionChangeRaInternal.class, required = true),
            @XmlElementRef(name = "countychange", type = CountyChangeRaInternal.class, required = true),
            @XmlElementRef(name = "municipalitychange", type = MunicipalityChangeRaInternal.class, required = true),
            @XmlElementRef(name = "districtChange", type = DistrictChangeRaInternal.class, required = true),
            @XmlElementRef(name = "streetNameChange", type = StreetNameChangeRaInternal.class, required = true),
            @XmlElementRef(name = "buildingUnitChange", type = BuildingUnitChangeRaInternal.class, required = true),
            @XmlElementRef(name = "BuildingNumberChange", type = BuildingNumberChangeRaInternal.class, required = true),
            @XmlElementRef(name = "propertyRegistrationNumberChange", type = PropertyRegistrationNumberChangeRaInternal.class, required = true)
    })
    public AbstractChangeBaseCType changeRaInternal;
}
