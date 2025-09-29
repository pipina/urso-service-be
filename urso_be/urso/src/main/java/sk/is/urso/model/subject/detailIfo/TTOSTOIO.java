package sk.is.urso.model.subject.detailIfo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TTOSTOIO
{

    @XmlElement(name = "TITTINA", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String tittina;
    @XmlElement(name = "TTITTNA", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String ttittna;
    @XmlElement(name = "TI", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int ti;
    @XmlElement(name = "TT", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int tt;
    @XmlElement(name = "FR", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fr;
    @XmlAttribute(name = "ID", required = true)
    protected int id;

}
