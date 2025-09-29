package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "municipalityChange")
@Getter
public class MunicipalityChange extends AbstractChange {

    @XmlElement(name="Municipality")
    private Municipality municipality;

    @XmlElement(name="countyIdentifier")
    private String countyIdentifier;

    @XmlElement(name="status")
    private String status;
}
