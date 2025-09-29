package sk.is.urso.model.subject.detailIfo;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class TDCDDCIOList
{

    @XmlElement(name = "DCD", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected List<TDCDDCIO> dcd;
}
