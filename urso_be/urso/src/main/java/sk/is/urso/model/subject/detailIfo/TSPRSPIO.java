package sk.is.urso.model.subject.detailIfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TSPRSPIO
{

    @XmlElement(name = "STASTNA", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String stastna;
    @XmlElement(name = "ST")
    protected int st;
    @XmlAttribute(name = "ID", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int id;

}
