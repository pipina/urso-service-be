package sk.is.urso.model.fo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
public class TZPOO {

    @XmlElement(name = "DZ", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dz;
    @XmlElement(name = "DK", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dk;
    @XmlElement(name = "PO", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String po;
    @XmlElement(name = "ZPZList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TZPZOList zpzList;
    @XmlAttribute(name = "ID", required = true)
    protected int id;
}
