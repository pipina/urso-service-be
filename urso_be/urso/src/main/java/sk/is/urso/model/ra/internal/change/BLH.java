package sk.is.urso.model.ra.internal.change;


import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;

@Getter
public class BLH {
    @XmlElement(name="AxisB")
    private String axisB;

    @XmlElement(name="AxisL")
    private String axisL;

    @XmlElement(name="AxisH")
    private String axisH;
}
