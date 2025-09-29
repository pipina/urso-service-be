package sk.is.urso.model.rachange;

import lombok.Setter;
import sk.is.urso.csru.ra.changes.CodelistDataElementCType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "regionChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
@Setter
public class RegionChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name="Region")
    public CodelistDataElementCType region;
}
