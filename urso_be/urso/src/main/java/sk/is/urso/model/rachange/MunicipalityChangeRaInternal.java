package sk.is.urso.model.rachange;

import lombok.Setter;
import sk.is.urso.csru.ra.changes.CodelistDataElementCType;
import sk.is.urso.csru.ra.changes.MunicipalityStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "municipalityChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Setter
public class MunicipalityChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name="Municipality")
    public CodelistDataElementCType municipality;

    @XmlElement(name="countyIdentifier")
    public Long countyIdentifier;

    @XmlElement(name="status")
    public MunicipalityStatus status;

    @XmlElement(name="cityIdentifier")
    public Long cityIdentifier;
}
