package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "municipalityChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class MunicipalityChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "Municipality")
    public ChangeRa region;

    @XmlElement(name = "countyIdentifier")
    public String countyIdentifier;

    @XmlElement(name = "status")
    public String status;
}
