package sk.is.urso.model.fo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TDCDDCIO {

    @XmlElement(name = "DD", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int dd;
    @XmlElement(name = "DDCDDNA", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String ddcddna;
    @XmlElement(name = "CD", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String cd;
    @XmlElement(name = "DU", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected boolean du;
    @XmlAttribute(name = "ID", required = true)
    protected int id;

}
