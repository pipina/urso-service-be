package sk.is.urso.model;


import javax.xml.bind.annotation.XmlElement;

public class CodelistItem {
    @XmlElement(name = "ItemCode")
    public String itemCode;
    @XmlElement(name = "ItemName")
    public String itemName;
}
