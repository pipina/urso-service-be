package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlTransient
@XmlSeeAlso({CountyChangeRaInternal.class, RegionChangeRaInternal.class, MunicipalityChangeRaInternal.class, DistrictChangeRaInternal.class, StreetNameChangeRaInternal.class, BuildingUnitChangeRaInternal.class, BuildingNumberChangeRaInternal.class, PropertyRegistrationNumberChangeRaInternal.class})
//Instructs JAXB to also bind other classes when binding this class
public class AbstractChangeBaseCType {
    @XmlElement(name = "databaseOperation")
    public String databaseOperation;

    @XmlElement(name = "objectId")
    public String objectId;

    @XmlElement(name = "versionId")
    public String versionId;

    @XmlElement(name = "createdReason")
    public String createdReason;
}
