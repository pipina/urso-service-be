package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;

@Getter
public class CodelistItem {

    @XmlElement(name="ItemCode")
    private String itemCode;

    @XmlElement(name="ItemName")
    private String itemName;
}
