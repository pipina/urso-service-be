package sk.is.urso.model.ra;


import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.List;

@XmlRootElement(name="ns0:Address")
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressR {
    @XmlAttribute(name = "xmlns:ns0")
    public String xmlns = "http://www.minv.sk/ra";

    @XmlElement(name = "FamilyName", required = true)
    protected List<FamilyNameSCType> familyName;
    @XmlElement(name = "AddressLine")
    protected String addressLine;
    @XmlElement(name = "Country")
    protected CodelistDataElementCType country;
    @XmlElement(name = "Region")
    protected CodelistDataElementCType region;
    @XmlElement(name = "County")
    protected CodelistDataElementCType county;
    @XmlElement(name = "Municipality")
    protected CodelistDataElementCType municipality;
    @XmlElement(name = "District")
    protected DistrictCType district;
    @XmlElement(name = "StreetName")
    protected String streetName;
    @XmlElement(name = "AdditionalStreetData")
    protected AdditionalStreetDataCType additionalStreetData;
    @XmlElement(name = "BuildingNumber")
    protected String buildingNumber;
    @XmlElement(name = "PropertyRegistrationNumber")
    protected BigInteger propertyRegistrationNumber;
    @XmlElement(name = "AddressPoint")
    protected AddressPointCType addressPoint;
    @XmlElement(name = "BuildingIndex")
    protected String buildingIndex;
    @XmlElement(name = "DeliveryAddress")
    protected DeliveryAddressCType deliveryAddress;
    @XmlElement(name = "Building")
    protected BuildingCType building;
    @XmlElement(name = "AddressType")
    protected AddressTypeCType addressType;
    @XmlElement(name = "BuildingUnit")
    protected List<BuildingUnitSCType> buildingUnit;
}
