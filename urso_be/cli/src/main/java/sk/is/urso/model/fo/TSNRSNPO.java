package sk.is.urso.model.fo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)

public class TSNRSNPO {

    @XmlElement(name = "SN", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int sn;
    @XmlElement(name = "SNPSNNA", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String snpsnna;
    @XmlElement(name = "DZ", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dz;
    @XmlElement(name = "DK", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dk;
    @XmlElement(name = "PZ", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String pz;
    @XmlAttribute(name = "ID", required = true)
    protected int id;

}
