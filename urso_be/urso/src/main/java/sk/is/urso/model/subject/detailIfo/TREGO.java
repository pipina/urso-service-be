package sk.is.urso.model.subject.detailIfo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TREGO
{

    @XmlElement(name = "PO", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int po;
    @XmlElement(name = "RE", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String re;
    @XmlAttribute(name = "ID", required = true)
    protected int id;
}
