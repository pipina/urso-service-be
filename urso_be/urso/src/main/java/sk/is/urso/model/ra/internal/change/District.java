package sk.is.urso.model.ra.internal.change;


import lombok.Getter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
@Getter
public class District {

    @XmlElement(name="Codelist")
    private Codelist codelist;

    @XmlAttribute(name="UniqueNumbering")
    private String uniqueNumbering;
}
