package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "regionChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RegionChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "Region")
    public ChangeRa region;

}
