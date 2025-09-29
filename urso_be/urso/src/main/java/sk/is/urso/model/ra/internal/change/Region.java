package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;

@Getter
public class Region {
    @XmlElement(name="Codelist")
    private Codelist codelist;
}
