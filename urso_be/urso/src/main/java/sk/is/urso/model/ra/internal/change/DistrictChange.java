package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "districtChange")
@Getter
public class DistrictChange extends AbstractChange {

    @XmlElement(name="District")
    private District district;

    @XmlElement(name="municipalityIdentifier")
    private String municipalityIdentifier;
}
