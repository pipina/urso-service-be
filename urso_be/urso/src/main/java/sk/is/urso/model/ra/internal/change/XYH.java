package sk.is.urso.model.ra.internal.change;


import lombok.Getter;

import javax.xml.bind.annotation.XmlElement;

@Getter
public class XYH {
    @XmlElement(name="AxisX")
    private String axisX;

    @XmlElement(name="AxisY")
    private String axisY;

    @XmlElement(name="HeightH")
    private String heightH;
}
