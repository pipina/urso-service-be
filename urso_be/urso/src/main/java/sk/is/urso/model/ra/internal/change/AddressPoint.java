package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "AddressPoint")
@Getter
public class AddressPoint {

    @XmlElement(name="XYH")
    public XYH XYH;

    @XmlElement(name="BLH")
    public BLH BLH;

    @XmlElement(name="AddressPointID")
    private String addressPointID;
}
