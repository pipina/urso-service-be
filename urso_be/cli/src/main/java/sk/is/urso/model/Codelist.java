package sk.is.urso.model;

import javax.xml.bind.annotation.XmlElement;

public class Codelist {

    @XmlElement(name = "CodelistCode")
    public String codelistCode;

    @XmlElement(name = "CodelistItem")
    public CodelistItem codelistItem;
}
