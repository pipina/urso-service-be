package sk.is.urso.model.ra.internal.change;


import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;

@Getter
public class Codelist {

    @XmlElement(name="CodelistCode")
    private String codelistCode;

    @XmlElement(name="CodelistItem")
    private CodelistItem codelistItem;
}
