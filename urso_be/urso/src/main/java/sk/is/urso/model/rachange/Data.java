package sk.is.urso.model.rachange;

import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Setter
public class Data {

    @XmlAttribute(name="current")
    public String current;

    @XmlAttribute(name="effectiveFrom")
    public String effectiveFrom;

    @XmlAttribute(name="effectiveTo")
    public String effectiveTo;

    @XmlAttribute(name="sequence")
    public String sequence;

    @XmlElement(name="change")
    public Change change = null;
}
