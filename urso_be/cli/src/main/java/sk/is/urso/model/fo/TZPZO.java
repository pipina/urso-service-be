package sk.is.urso.model.fo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)

public class TZPZO {

    @XmlElement(name = "UC")
    protected int uc;
    @XmlElement(name = "UCEUCNA", required = true)
    protected String uceucna;
    @XmlAttribute(name = "ID", required = true)
    protected int id;
}
