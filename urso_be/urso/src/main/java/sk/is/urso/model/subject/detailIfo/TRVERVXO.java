package sk.is.urso.model.subject.detailIfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
public class TRVERVXO
{

    @XmlElement(name = "IF", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String _if;
    @XmlElement(name = "TR", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int tr;
    @XmlElement(name = "TRZTRNA", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String trztrna;
    @XmlElement(name = "DV", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dv;
    @XmlElement(name = "MV", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String mv;
    @XmlElement(name = "UC", required = true, type = Integer.class, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected Integer uc;
    @XmlElement(name = "UCEUCNA", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String uceucna;
    @XmlElement(name = "TL", required = true, type = Integer.class, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected Integer tl;
    @XmlElement(name = "TRRTLNA", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String trrtlna;
    @XmlElement(name = "TE", required = true, type = Integer.class, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected Integer te;
    @XmlElement(name = "TRRTENA", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String trrtena;
    @XmlElement(name = "SM", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String sm;
    @XmlElement(name = "PA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer pa;
    @XmlAttribute(name = "ID", required = true)
    protected int id;

}
