package sk.is.urso.model.subject.detailIfo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
public class TZUDZUNOList
{
    @XmlElement(name = "ZUD", namespace = "http://www.dominanz.sk/UVZ/Reg/FO")
    protected List<TZUDZUNO> zud;
}
