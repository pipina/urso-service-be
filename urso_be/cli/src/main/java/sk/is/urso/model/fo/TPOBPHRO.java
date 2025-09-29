package sk.is.urso.model.fo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.XMLGregorianCalendar;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TPOBPHRO {

    @XmlElement(name = "TP", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int tp;
    @XmlElement(name = "TB", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String tb;
    @XmlElement(name = "DP", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dp;
    @XmlElement(name = "DU", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar du;
    @XmlElement(name = "NO", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String no;
    @XmlElement(name = "SC", required = true, type = Integer.class, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected Integer sc;
    @XmlElement(name = "NC", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String nc;
    @XmlElement(name = "NK", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String nk;
    @XmlElement(name = "NU", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String nu;
    @XmlElement(name = "ST", required = true, type = Integer.class, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected Integer st;
    @XmlElement(name = "NS", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String ns;
    @XmlElement(name = "MP", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String mp;
    @XmlElement(name = "OP", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String op;
    @XmlElement(name = "OO", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String oo;
    @XmlElement(name = "CC", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String cc;
    @XmlElement(name = "UM", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String um;
    @XmlElement(name = "OS", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String os;
    @XmlElement(name = "SI", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String si;
    @XmlElement(name = "CU", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String cu;
    @XmlElement(name = "PM", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected boolean pm;
    @XmlElement(name = "PC", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String pc;
    @XmlElement(name = "CB", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String cb;
    @XmlElement(name = "IA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected String ia;
    @XmlElement(name = "VD", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer vd;
    @XmlElement(name = "DI", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer di;
    @XmlElement(name = "UI", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer ui;
    @XmlElement(name = "CE", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer ce;
    @XmlElement(name = "OA", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer oa;
    @XmlElement(name = "UE", namespace = "http://www.dominanz.sk/UVZ/Reg/FO", required = false)
    protected Integer ue;
    @XmlElement(name = "OL", required = true, nillable = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected String ol;
    @XmlElement(name = "REGList", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected TREGOList regList;
    @XmlAttribute(name = "ID", required = true, namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected int id;


}
