package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlTransient
@XmlSeeAlso({RegionChange.class, CountyChange.class, MunicipalityChange.class, DistrictChange.class, StreetNameChange.class, PropertyRegistrationNumberChange.class, BuildingNumberChange.class, BuildingUnitChange.class}) //Instructs JAXB to also bind other classes when binding this class
@Getter
public class AbstractChange {
    @XmlElement(name="databaseOperation")
    private String databaseOperation;

    @XmlElement(name="objectId")
    private String objectId;

    @XmlElement(name="versionId")
    private String versionId;

    @XmlElement(name="createdReason")
    private String createdReason;
}
