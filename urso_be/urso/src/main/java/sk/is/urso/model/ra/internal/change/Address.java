package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement(name="ns0:Address")
@Getter
public class Address {
    @XmlElement(name="data")
    private List<Data> data;

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "objectId")
    private String objectId;
}
