package sk.is.urso.model.fo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TZUDZUNO {
    @XmlElement(name = "ZO", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int zo;
    @XmlElement(name = "ZOBZONA", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String zobzona;
    @XmlAttribute(name = "ID", required = true)
    protected int id;

}
