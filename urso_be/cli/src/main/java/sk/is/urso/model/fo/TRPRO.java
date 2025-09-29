package sk.is.urso.model.fo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TRPRO {
    @XmlElement(name = "PO", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int po;
    @XmlElement(name = "RP", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String rp;
    @XmlAttribute(name = "ID", required = true)
    protected int id;
}
