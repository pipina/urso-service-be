package sk.is.urso.model.ra.internal.change;

import lombok.Getter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "regionChange")
@Getter
public class RegionChange extends AbstractChange {

    // spolocne
    ///////////////////////////////////////////////////
//    @XmlElement(name="databaseOperation")
//    public String databaseOperation;
//
//    @XmlElement(name="objectId")
//    public String objectId;
//
//    @XmlElement(name="versionId")
//    public String versionId;
//
//    @XmlElement(name="createdReason")
//    public String createdReason;
    ///////////////////////////////////////////////////

    // regionChange
    ///////////////////////////////////////////////////
//    @XmlElement(name="Municipality")
//    public Municipality municipality;

//    @XmlElement(name="countyIdentifier")
//    public String countyIdentifier;
//
//    @XmlElement(name="status")
//    public String status;

    @XmlElement(name="Region")
    private Region region;




}
