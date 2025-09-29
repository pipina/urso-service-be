package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "countyChange")
@Getter
public class CountyChange extends AbstractChange {

    @XmlElement(name="County")
    private County county;

    @XmlElement(name="regionIdentifier")
    private String regionIdentifier;
}
