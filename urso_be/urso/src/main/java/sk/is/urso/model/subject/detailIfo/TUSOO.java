package sk.is.urso.model.subject.detailIfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
public class TUSOO
{

    @XmlElement(name = "DP", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dp;
    @XmlElement(name = "UD", required = true, type = Boolean.class, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected Boolean ud;
    @XmlAttribute(name = "ID", required = true)
    protected int id;

}
