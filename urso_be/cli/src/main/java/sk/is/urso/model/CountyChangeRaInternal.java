package sk.is.urso.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "countyChange")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CountyChangeRaInternal extends AbstractChangeBaseCType {

    @XmlElement(name = "County")
    public ChangeRa region;

    @XmlElement(name = "regionIdentifier")
    public String regionIdentifier;
}