package sk.is.urso.model.rachange;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="ns0:Address")
@Getter
public class RegisterRaInternalReg {

    @XmlAttribute(name = "xmlns:ns0")
    public String xmlns = "http://www.minv.sk/ra";

    @XmlElement(name="data")
    public List<Data> data;

    @XmlElement(name = "type")
    public String type;

    @XmlElement(name = "objectId")
    public String objectId;
}
