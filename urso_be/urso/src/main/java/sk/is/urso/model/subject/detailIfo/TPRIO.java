package sk.is.urso.model.subject.detailIfo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TPRIO
{

    @XmlElement(name = "PO", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int po;
    @XmlElement(name = "PR", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String pr;
    @XmlAttribute(name = "ID", required = true)
    protected int id;

}
