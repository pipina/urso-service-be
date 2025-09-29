package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Getter
public class Data {

    @XmlAttribute(name="current")
    private String current;

    @XmlAttribute(name="effectiveFrom")
    private String effectiveFrom;

    @XmlAttribute(name="effectiveTo")
    private String effectiveTo;

    @XmlAttribute(name="sequence")
    private String sequence;

    @XmlElement(name="change")
    private Change change;
}
