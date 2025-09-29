package sk.is.urso.model.ra.internal.change;


import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
@Getter
public class Municipality {

    @XmlElement(name="Codelist")
    private Codelist codelist;

//    @XmlAttribute(name="UniqueNumbering")
//    public String uniqueNumbering;
}
