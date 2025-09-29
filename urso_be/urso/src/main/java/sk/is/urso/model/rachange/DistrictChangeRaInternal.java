package sk.is.urso.model.rachange;

import lombok.Setter;
import sk.is.urso.csru.ra.changes.DistrictCType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "districtChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Setter
public class DistrictChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "District")
    public DistrictCType district;

    @XmlElement(name = "municipalityIdentifier")
    public Long municipalityIdentifier;
}
