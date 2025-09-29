package sk.is.urso.model.subject.detailIfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)

public class TPZMOList {

        @XmlElement(name = "PZM", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
        protected List<TPZMO> pzm;

}
