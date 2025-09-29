package sk.is.urso.model.fo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
public class TRVERVXOList {

    @XmlElement(name = "RVE", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected List<TRVERVXO> rve;

}
