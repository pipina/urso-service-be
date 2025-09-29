package sk.is.urso.model.fo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TSPRSPIOList {

    @XmlElement(name = "SPR", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected List<TSPRSPIO> spr;

}
