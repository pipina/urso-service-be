package sk.is.urso.service;

import org.alfa.exception.CommonException;
import org.alfa.utils.DateUtils;
import org.alfa.utils.Utils;
import org.alfa.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;
import sk.is.urso.common.regconfig.v1.RegistersConfig;
import sk.is.urso.model.BLH;
import sk.is.urso.model.BuildingNumberChangeRaInternal;
import sk.is.urso.model.BuildingUnit;
import sk.is.urso.model.BuildingUnitChangeRaInternal;
import sk.is.urso.model.Change;
import sk.is.urso.model.ChangeRa;
import sk.is.urso.model.Codelist;
import sk.is.urso.model.CodelistItem;
import sk.is.urso.model.CountyChangeRaInternal;
import sk.is.urso.model.Data;
import sk.is.urso.model.DataChange;
import sk.is.urso.model.DistrictChangeRaInternal;
import sk.is.urso.model.MunicipalityChangeRaInternal;
import sk.is.urso.model.PropertyRegistrationNumberChangeRaInternal;
import sk.is.urso.model.RegionChangeRaInternal;
import sk.is.urso.model.RegisterRa;
import sk.is.urso.model.RegisterRaInternal;
import sk.is.urso.model.StreetNameChangeRaInternal;
import sk.is.urso.model.XYH;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegPlugin;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Component
public class RegisterRaHandler extends DefaultHandler {

    InitializeRa initializeRa;

    private RegisterRa register;

    private StringBuilder elementValue = new StringBuilder();

    private ChangeRa changeRa;

    private static final String TYPE = "type";
    private static final String COUNT = "count";

    private static final String REGION_CHANGE = "regionChange";
    private static final String COUNTY_CHANGE = "countyChange";
    private static final String MUNICIPALITY_CHANGE = "municipalityChange";
    private static final String DISTRICT_CHANGE = "districtChange";
    private static final String STREET_NAME_CHANGE = "streetNameChange";
    private static final String PROPERTY_REGISTRATION_NUMBER_CHANGE = "propertyRegistrationNumberChange";
    private static final String BUILDING_NUMBER_CHANGE = "buildingNumberChange";
    private static final String BUILDING_UNIT_CHANGE = "buildingUnitChange";

    private static final String DATABASE_OPERATION = "databaseOperation";
    private static final String OBJECT_ID = "objectId";
    private static final String VERSION_ID = "versionId";
    private static final String CREATED_REASON = "createdReason";
    private static final String VALID_FROM = "validFrom";
    private static final String VALID_TO = "validTo";
    private static final String EFFECTIVE_DATE = "effectiveDate";

    private static final String REGION = "Region";
    private static final String COUNTY = "County";
    private static final String MUNICIPALITY = "Municipality";
    private static final String DISTRICT = "District";
    private static final String STREET_NAME = "StreetName";
    private static final String ADDRESS_POINT = "AddressPoint";
    private static final String BUILDING = "Building";
    private static final String BUILDING_UNIT = "BuildingUnit";

    private static final String CODELISTCODE = "CodelistCode";
    private static final String ITEMCODE = "ItemCode";
    private static final String ITEMNAME = "ItemName";

    private static final String REGION_IDENTIFIER = "regionIdentifier";
    private static final String COUNTY_IDENTIFIER = "countyIdentifier";
    private static final String MUNICIPALITY_IDENTIFIER = "municipalityIdentifier";
    private static final String DISTRICT_IDENTIFIER = "districtIdentifier";
    private static final String PROPERTY_REGISTRATION_NUMBER_IDENTIFIER = "propertyRegistrationNumberIdentifier";
    private static final String STREET_NAME_IDENTIFIER = "streetNameIdentifier";
    private static final String BUILDING_NUMBER_IDENTIFIER = "buildingNumberIdentifier";

    private static final String STATUS = "status";
    private static final String UNIQUE_NUMBERING = "UniqueNumbering";

    private static final String BUILDING_NUMBER = "BuildingNumber";
    private static final String BUILDING_INDEX = "BuildingIndex";
    private static final String POSTAL_CODE = "PostalCode";
    private static final String VERIFIED_AT = "verifiedAt";
    private static final String ADDRESS_POINT_ID = "AddressPointID";
    private static final String AXIS_X = "AxisX";
    private static final String AXIS_Y = "AxisY";
    private static final String HEIGHT_H = "HeightH";
    private static final String AXIS_B = "AxisB";
    private static final String AXIS_L = "AxisL";
    private static final String AXIS_H = "AxisH";
    private static final String PROPERTY_REGISTRATION_NUMBER = "PropertyRegistrationNumber";
    private static final String BUILDING_NAME = "BuildingName";
    private static final String BUILDING_PURPOSE = "BuildingPurpose";
    private static final String BUILDING_TYPE_CODE = "BuildingTypeCode";

    private static final String SEQUENCE = "sequence";
    private static final String CURRENT = "current";
    private static final String EFFECTIVE_FROM = "effectiveFrom";
    private static final String EFFECTIVE_TO = "effectiveTo";

    private Date now = new Date();

    public RegisterRaHandler(InitializeRa initializeRa) {
        this.initializeRa = initializeRa;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (changeRa == null ||
                (changeRa.codelist != null && changeRa.codelist.codelistCode != null && changeRa.codelist.codelistCode.equals(""))
                || (changeRa.codelist != null && changeRa.codelist.codelistItem.itemCode != null && changeRa.codelist.codelistItem.itemCode.equals(""))
                || (changeRa.codelist != null && changeRa.codelist.codelistItem.itemName != null && changeRa.codelist.codelistItem.itemName.equals(""))
                || (register.dataChange != null && register.dataChange.buildingNumberIdentifier != null && register.dataChange.buildingNumberIdentifier.length() == 0)
                || (changeRa.buildingUnit != null && changeRa.buildingUnit.length() == 0)
                || (changeRa.BLH != null && changeRa.BLH.AxisB != null && changeRa.BLH.AxisB.length() == 0)
                || (changeRa.BLH != null && changeRa.BLH.AxisH != null && changeRa.BLH.AxisH.length() == 0)
                || (changeRa.BLH != null && changeRa.BLH.AxisL != null && changeRa.BLH.AxisL.length() == 0)
                || (changeRa.XYH != null && changeRa.XYH.AxisX != null && changeRa.XYH.AxisX.length() == 0)
                || (changeRa.XYH != null && changeRa.XYH.AxisY != null && changeRa.XYH.AxisY.length() == 0)
                || (changeRa.XYH != null && changeRa.XYH.HeightH != null && changeRa.XYH.HeightH.length() == 0)
                || (changeRa.buildingTypeCode != null && changeRa.buildingTypeCode.codelist != null && changeRa.buildingTypeCode.codelist.codelistCode != null)
                || (changeRa.buildingPurpose != null && changeRa.buildingPurpose.codelist != null && changeRa.buildingPurpose.codelist.codelistCode != null))
            elementValue.append(ch, start, length);
    }

    @Override
    public void startDocument() {
        register = new RegisterRa();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        elementValue.setLength(0);
        switch (qName) {
            case TYPE:
            case COUNT:
            case DATABASE_OPERATION:
            case OBJECT_ID:
            case VERSION_ID:
            case CREATED_REASON:
            case VALID_FROM:
            case VALID_TO:
            case EFFECTIVE_DATE:
            case REGION_IDENTIFIER:
            case COUNTY_IDENTIFIER:
            case MUNICIPALITY_IDENTIFIER:
            case DISTRICT_IDENTIFIER:
            case STATUS:
            case BUILDING_NUMBER:
            case BUILDING_INDEX:
            case POSTAL_CODE:
            case PROPERTY_REGISTRATION_NUMBER_IDENTIFIER:
            case STREET_NAME_IDENTIFIER:
            case VERIFIED_AT:
            case PROPERTY_REGISTRATION_NUMBER:
            case STREET_NAME:
                elementValue.setLength(0);
                break;
            case REGION_CHANGE:
            case COUNTY_CHANGE:
            case MUNICIPALITY_CHANGE:
            case DISTRICT_CHANGE:
            case STREET_NAME_CHANGE:
            case BUILDING_NUMBER_CHANGE:
            case PROPERTY_REGISTRATION_NUMBER_CHANGE:
            case BUILDING_UNIT_CHANGE:
                register.dataChange = new DataChange();
                break;
            case BUILDING_NUMBER_IDENTIFIER:
                register.dataChange.buildingNumberIdentifier = "";
                elementValue.setLength(0);
                break;
            case REGION:
            case COUNTY:
            case MUNICIPALITY:
                changeRa = new ChangeRa();
                changeRa.codelist = (new Codelist());
                changeRa.codelist.codelistItem = (new CodelistItem());
                break;
            case DISTRICT:
                changeRa = new ChangeRa();
                changeRa.uniqueNumbering = attr.getValue(0);
                changeRa.codelist = (new Codelist());
                changeRa.codelist.codelistItem = (new CodelistItem());
                break;
            case BUILDING:
                changeRa = new ChangeRa();
                changeRa.containsFlats = attr.getValue(0);
                break;
            case ADDRESS_POINT:
                changeRa = new ChangeRa();
                break;
            case BUILDING_PURPOSE:
                changeRa.buildingPurpose = new ChangeRa();
                changeRa.buildingPurpose.codelist = (new Codelist());
                changeRa.buildingPurpose.codelist.codelistItem = (new CodelistItem());
                changeRa.buildingPurpose.codelist.codelistCode = "";

                break;
            case BUILDING_TYPE_CODE:
                changeRa.buildingTypeCode = new ChangeRa();
                changeRa.buildingTypeCode.codelist = (new Codelist());
                changeRa.buildingTypeCode.codelist.codelistItem = (new CodelistItem());
                break;
            case BUILDING_UNIT:
                changeRa = new ChangeRa();
                elementValue.setLength(0);
                changeRa.buildingUnit = ("");
                changeRa.unitNumber = attr.getValue(0);
                if (attr.getValue(1) != null) {
                    changeRa.floor = Utils.objectToInt(attr.getValue(1));
                }
                break;
            case CODELISTCODE:
                if (changeRa.codelist != null) {
                    changeRa.codelist.codelistCode = ("");
                }
                elementValue.setLength(0);
                break;
            case ITEMCODE:
                if (changeRa.codelist != null) {
                    changeRa.codelist.codelistItem.itemCode = ("");
                }
                elementValue.setLength(0);
                break;
            case ITEMNAME:
                if (changeRa.codelist != null) {
                    changeRa.codelist.codelistItem.itemName = ("");
                }
                elementValue.setLength(0);
                break;
            case UNIQUE_NUMBERING:
                changeRa.uniqueNumbering = "";
                elementValue.setLength(0);
                break;
            case BUILDING_NAME:
                changeRa.buildingName = "";
                elementValue.setLength(0);
                break;
            case ADDRESS_POINT_ID:
                changeRa.addressPointID = "";
                elementValue.setLength(0);
                break;
            case AXIS_X:
                if (changeRa.XYH == null) changeRa.XYH = new XYH();
                changeRa.XYH.AxisX = "";
                elementValue.setLength(0);
                break;
            case AXIS_Y:
                if (changeRa.XYH == null) changeRa.XYH = new XYH();
                changeRa.XYH.AxisY = "";
                elementValue.setLength(0);
                break;
            case HEIGHT_H:
                if (changeRa.XYH == null) changeRa.XYH = new XYH();
                changeRa.XYH.HeightH = "";
                elementValue.setLength(0);
                break;
            case AXIS_B:
                if (changeRa.BLH == null) changeRa.BLH = new BLH();
                changeRa.BLH.AxisB = "";
                elementValue.setLength(0);
                break;
            case AXIS_L:
                if (changeRa.BLH == null) changeRa.BLH = new BLH();
                changeRa.BLH.AxisL = "";
                elementValue.setLength(0);
                break;
            case AXIS_H:
                if (changeRa.BLH == null) changeRa.BLH = new BLH();
                changeRa.BLH.AxisH = "";
                elementValue.setLength(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case TYPE:
                register.type = elementValue.toString();
                break;
            case COUNT:
                register.count = elementValue.toString();
                break;
            case DATABASE_OPERATION:
                register.dataChange.databaseOperation = elementValue.toString();
                break;
            case OBJECT_ID:
                register.dataChange.objectId = (elementValue.toString());
                break;
            case VERSION_ID:
                register.dataChange.versionId = (elementValue.toString());
                break;
            case CREATED_REASON:
                register.dataChange.createdReason = (elementValue.toString());
                break;
            case VALID_FROM:
                register.dataChange.validFrom = (elementValue.toString());
                break;
            case VALID_TO:
                register.dataChange.validTo = (elementValue.toString());
                break;
            case EFFECTIVE_DATE:
                register.dataChange.effectiveDate = (elementValue.toString());
                break;
            case REGION_IDENTIFIER:
                register.dataChange.regionIdentifier = (elementValue.toString());
                break;
            case COUNTY_IDENTIFIER:
                register.dataChange.countyIdentifier = (elementValue.toString());
                break;
            case MUNICIPALITY_IDENTIFIER:
                register.dataChange.municipalityIdentifier = (elementValue.toString());
                break;
            case DISTRICT_IDENTIFIER:
                register.dataChange.districtIdentifier = (elementValue.toString());
                break;
            case BUILDING_NUMBER_IDENTIFIER:
                register.dataChange.buildingNumberIdentifier = (elementValue.toString());
                break;
            case BUILDING_NUMBER:
                register.dataChange.buildingNumber = (elementValue.toString());
                break;
            case BUILDING_INDEX:
                register.dataChange.buildingIndex = (elementValue.toString());
                break;
            case POSTAL_CODE:
                register.dataChange.postalCode = (elementValue.toString());
                break;
            case PROPERTY_REGISTRATION_NUMBER_IDENTIFIER:
                register.dataChange.propertyRegistrationNumberIdentifier = (elementValue.toString());
                break;
            case STREET_NAME_IDENTIFIER:
                register.dataChange.streetNameIdentifier = (elementValue.toString());
                break;
            case VERIFIED_AT:
                register.dataChange.verifiedAt = (elementValue.toString().substring(0, 9));
                break;
            case PROPERTY_REGISTRATION_NUMBER:
                register.dataChange.propertyRegistrationNumber = (elementValue.toString());
                break;
            case STATUS:
                register.dataChange.status = (elementValue.toString());
                break;
            case STREET_NAME:
                register.dataChange.StreetName = (elementValue.toString());
                break;
            case REGION_CHANGE:
            case COUNTY_CHANGE:
            case MUNICIPALITY_CHANGE:
            case DISTRICT_CHANGE:
            case STREET_NAME_CHANGE:
            case PROPERTY_REGISTRATION_NUMBER_CHANGE:
            case BUILDING_NUMBER_CHANGE:
            case BUILDING_UNIT_CHANGE:
                convertToRegisterRaInternal(register);
                register.dataChange = null;
                changeRa = null;
                break;
            case MUNICIPALITY:
            case REGION:
            case COUNTY:
            case DISTRICT:
            case ADDRESS_POINT:
            case BUILDING:
                register.dataChange.region = (changeRa);
                changeRa = null;
                break;
            case BUILDING_UNIT:
                changeRa.buildingUnit = elementValue.toString();
                break;
            case BUILDING_PURPOSE:
            case BUILDING_TYPE_CODE:
                break;
            case BUILDING_NAME:
                changeRa.buildingName = elementValue.toString();
                break;
            case CODELISTCODE:
                if (changeRa.codelist != null) {
                    changeRa.codelist.codelistCode = (elementValue.toString());
                } else if (changeRa.buildingPurpose != null && changeRa.buildingTypeCode == null) {
                    changeRa.buildingPurpose.codelist.codelistCode = (elementValue.toString());
                } else if (changeRa.buildingTypeCode != null) {
                    changeRa.buildingTypeCode.codelist.codelistCode = (elementValue.toString());
                }
                break;
            case ITEMCODE:
                if (changeRa.codelist != null) {
                    changeRa.codelist.codelistItem.itemCode = (elementValue.toString());
                } else if (changeRa.buildingPurpose != null && changeRa.buildingTypeCode == null) {
                    changeRa.buildingPurpose.codelist.codelistItem.itemCode = (elementValue.toString());
                } else if (changeRa.buildingTypeCode != null) {
                    changeRa.buildingTypeCode.codelist.codelistItem.itemCode = (elementValue.toString());
                }
                break;
            case ITEMNAME:
                if (changeRa.codelist != null) {
                    changeRa.codelist.codelistItem.itemName = (elementValue.toString());
                } else if (changeRa.buildingPurpose != null && changeRa.buildingTypeCode == null) {
                    changeRa.buildingPurpose.codelist.codelistItem.itemName = (elementValue.toString());
                } else if (changeRa.buildingTypeCode != null) {
                    changeRa.buildingTypeCode.codelist.codelistItem.itemName = (elementValue.toString());
                }
                break;
            case UNIQUE_NUMBERING:
                changeRa.uniqueNumbering = (elementValue.toString());
                break;
            case ADDRESS_POINT_ID:
                changeRa.addressPointID = (elementValue.toString());
                break;
            case AXIS_X:
                changeRa.XYH.AxisX = (elementValue.toString());
                break;
            case AXIS_Y:
                changeRa.XYH.AxisY = (elementValue.toString());
                break;
            case HEIGHT_H:
                changeRa.XYH.HeightH = (elementValue.toString());
                break;
            case AXIS_B:
                changeRa.BLH.AxisB = (elementValue.toString());
                break;
            case AXIS_L:
                changeRa.BLH.AxisL = (elementValue.toString());
                break;
            case AXIS_H:
                changeRa.BLH.AxisH = (elementValue.toString());
                break;
            default:
                break;
        }
    }

    public RegisterRa getRegister() {
        return register;
    }

    private String isValidToday(XMLGregorianCalendar effectiveFrom, XMLGregorianCalendar effectiveTo) {
        if (effectiveFrom.compare(DateUtils.nowXmlDate()) == -1 && effectiveTo.compare(DateUtils.nowXmlDate()) == 1)
            return "true";
        return "false";
    }

    private RegisterRaInternal getRegisterRaInternal(RegisterRa registerRa, DataChange regionChange) {
        var registerRaInternal = new RegisterRaInternal();
        registerRaInternal.objectId = regionChange.objectId;
        registerRaInternal.type = registerRa.type;
        registerRaInternal.data = new ArrayList<>();
        registerRaInternal.data.add(new Data());
        registerRaInternal.data.get(0).current = isValidToday(DateUtils.toXmlDate(regionChange.validFrom), DateUtils.toXmlDate(regionChange.validTo));
        registerRaInternal.data.get(0).effectiveFrom = regionChange.validFrom.substring(0, 10);
        registerRaInternal.data.get(0).effectiveTo = regionChange.validTo.substring(0, 10);
        try {
            Date validFrom = new SimpleDateFormat("yyyy-MM-dd").parse(regionChange.validFrom.substring(0, 10));
            Date validTo = new SimpleDateFormat("yyyy-MM-dd").parse(regionChange.validTo.substring(0, 10));
            //ak je validFrom > validTo, tak do validTo nastavime validFrom
            if (validFrom.after(validTo))
                registerRaInternal.data.get(0).effectiveTo = regionChange.validFrom.substring(0, 10);
        } catch (ParseException e) {
        }
        registerRaInternal.data.get(0).sequence = "1";
        registerRaInternal.data.get(0).change = new Change();
        return registerRaInternal;
    }

    private RegionChangeRaInternal getRegionChangeRaInternal(DataChange regionChange) {
        RegionChangeRaInternal changeRaInternal = new RegionChangeRaInternal();
        changeRaInternal.region = regionChange.region;
        return changeRaInternal;
    }

    private CountyChangeRaInternal getCountyChangeRaInternal(DataChange regionChange) {
        var changeRaInternal = new CountyChangeRaInternal();
        changeRaInternal.region = regionChange.region;
        changeRaInternal.regionIdentifier = regionChange.regionIdentifier;
        return changeRaInternal;
    }

    private MunicipalityChangeRaInternal getMunicipalityChangeRaInternal(DataChange regionChange) {
        var changeRaInternal = new MunicipalityChangeRaInternal();
        changeRaInternal.region = regionChange.region;
        changeRaInternal.countyIdentifier = regionChange.countyIdentifier;
        changeRaInternal.status = regionChange.status;
        return changeRaInternal;
    }

    private DistrictChangeRaInternal getDistrictChangeRaInternal(DataChange regionChange) {
        DistrictChangeRaInternal changeRaInternal = new DistrictChangeRaInternal();
        changeRaInternal.region = regionChange.region;
        changeRaInternal.municipalityIdentifier = regionChange.municipalityIdentifier;
        return changeRaInternal;
    }

    private StreetNameChangeRaInternal getStreetNameChangeRaInternal(DataChange regionChange) {
        var changeRaInternal = new StreetNameChangeRaInternal();
        changeRaInternal.StreetName = regionChange.StreetName;
        changeRaInternal.municipalityIdentifier = regionChange.municipalityIdentifier;
        changeRaInternal.districtIdentifier = regionChange.districtIdentifier;
        return changeRaInternal;
    }

    private BuildingUnitChangeRaInternal getBuildingUnitChangeRaInternal(DataChange regionChange) {
        var changeRaInternal = new BuildingUnitChangeRaInternal();
        changeRaInternal.buildingNumberIdentifier = regionChange.buildingNumberIdentifier;
        BuildingUnit bu = new BuildingUnit();
        bu.unitNumber = changeRa.unitNumber;
        bu.floor = changeRa.floor;
        bu.value = changeRa.buildingUnit;
        changeRaInternal.buildingUnit = bu;
        return changeRaInternal;
    }

    private BuildingNumberChangeRaInternal getBuildingNumberChangeRaInternal(DataChange regionChange) {
        var changeRaInternal = new BuildingNumberChangeRaInternal();

        changeRaInternal.buildingNumber = regionChange.buildingNumber;
        changeRaInternal.buildingIndex = regionChange.buildingIndex;
        changeRaInternal.postalCode = regionChange.postalCode;
        changeRaInternal.propertyRegistrationNumberIdentifier = regionChange.propertyRegistrationNumberIdentifier;
        changeRaInternal.streetNameIdentifier = regionChange.streetNameIdentifier;
        changeRaInternal.verifiedAt = regionChange.verifiedAt;

        if (regionChange.region != null) {
            changeRaInternal.region = regionChange.region;
        }
        return changeRaInternal;
    }

    private PropertyRegistrationNumberChangeRaInternal getPropertyRegistrationNumberChangeRaInternal(DataChange regionChange) {
        var changeRaInternal = new PropertyRegistrationNumberChangeRaInternal();
        changeRaInternal.propertyRegistrationNumber = regionChange.propertyRegistrationNumber;
        changeRaInternal.municipalityIdentifier = regionChange.municipalityIdentifier;
        changeRaInternal.districtIdentifier = regionChange.districtIdentifier;
        changeRaInternal.region = regionChange.region;
        return changeRaInternal;
    }

    public void convertToRegisterRaInternal(RegisterRa registerRa) {
        var registerRaInternal = getRegisterRaInternal(registerRa, registerRa.dataChange);

        switch (registerRa.type) {
            case "REGION":
                registerRaInternal.data.get(0).change.changeRaInternal = getRegionChangeRaInternal(registerRa.dataChange);
                break;
            case "COUNTY":
                registerRaInternal.data.get(0).change.changeRaInternal = getCountyChangeRaInternal(registerRa.dataChange);
                break;
            case "MUNICIPALITY":
                registerRaInternal.data.get(0).change.changeRaInternal = getMunicipalityChangeRaInternal(registerRa.dataChange);
                break;
            case "DISTRICT":
                registerRaInternal.data.get(0).change.changeRaInternal = getDistrictChangeRaInternal(registerRa.dataChange);
                break;
            case "STREET_NAME":
                registerRaInternal.data.get(0).change.changeRaInternal = getStreetNameChangeRaInternal(registerRa.dataChange);
                break;
            case "BUILDING_UNIT":
                registerRaInternal.data.get(0).change.changeRaInternal = getBuildingUnitChangeRaInternal(registerRa.dataChange);
                break;
            case "BUILDING_NUMBER":
                registerRaInternal.data.get(0).change.changeRaInternal = getBuildingNumberChangeRaInternal(registerRa.dataChange);
                break;
            case "PROPERTY_REGISTRATION_NUMBER":
                registerRaInternal.data.get(0).change.changeRaInternal = getPropertyRegistrationNumberChangeRaInternal(registerRa.dataChange);
                break;
            default:
                throw new IllegalArgumentException("Unsupported address type " + registerRa.type);
        }

        registerRaInternal.data.get(0).change.changeRaInternal.objectId = registerRa.dataChange.objectId;
        registerRaInternal.data.get(0).change.changeRaInternal.versionId = registerRa.dataChange.versionId;
        registerRaInternal.data.get(0).change.changeRaInternal.databaseOperation = registerRa.dataChange.databaseOperation;
        registerRaInternal.data.get(0).change.changeRaInternal.createdReason = registerRa.dataChange.createdReason;
        registerRaInternal.data.get(0).change.count = registerRa.count;

        try {
            Date validTo = new SimpleDateFormat("yyyy-MM-dd").parse(registerRa.dataChange.validTo.substring(0, 10));
            if (validTo.after(now)) {
                try {
                    checkInitializedRecordsNum();
                    createValues(registerRaInternal);
                    initializeRa.initializedRecordsNumPerFile++;
                    initializeRa.initializedRecordsAll++;
                } catch (Exception e) {
                    initializeRa.initialize.error("Error at type " + registerRaInternal.getType() + " with objectId " + registerRaInternal.getObjectId() + ". ", e);
                    initializeRa.initialize.failCount++;
                    if (initializeRa.initialize.endAfterError) {
                        throw new CommonException(HttpStatus.BAD_GATEWAY, "Initialization ended because of an error.", e);
                    }
                }
            }
        } catch (ParseException e) {
            initializeRa.initialize.error("Error at type " + registerRaInternal.getType() + " with objectId " + registerRaInternal.getObjectId() + ". Unable to parse validTo value - record is skipped. ", e);
        }
    }

    private void createValues(RegisterRaInternal registerRaInternal) {
        try {
            String xml = XmlUtils.objectToXml(registerRaInternal).replace('\n', ' ');

            try {
                initializeRa.indexWriter.write(createIndexes(registerRaInternal));
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException e) {
                e.printStackTrace();
                return;
            }
            initializeRa.dataWriter.write(getData(registerRaInternal, xml) + "\n");
            initializeRa.dataHistoryWriter.write(getDataHistory(registerRaInternal, xml) + "\n");
            initializeRa.naturalIdWriter.write(getNaturalId(registerRaInternal) + "\n");
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }
    }

    private void checkInitializedRecordsNum() {
        if (initializeRa.initializedRecordsNumPerFile == Integer.parseInt(initializeRa.initialize.csvRecordsNum)) {
            try {
                initializeRa.initializedRecordsNumPerFile = 0;
                initializeRa.closeCsvFiles();
                initializeRa.csvOrderNum++;
                initializeRa.prepareCsvFiles(InitializeRa.RA_INTERNAL_1);
            } catch (IOException e) {
                throw new CommonException(HttpStatus.BAD_GATEWAY, "Initialization ended because of an error.", e);
            }
        }
    }

    /**
     * @param registerRaInternal
     * @param xml
     * @return a ra_internal_1_data record as (disabled, effective_from, effective_to, last_reference_timestamp, module, "user", valid_from, xml)
     */
    private String getData(RegisterRaInternal registerRaInternal, String xml) {
        String effectiveFrom = registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveFrom();
        String effectiveTo = registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveTo();
        return "false;'" + effectiveFrom + "';'" + effectiveTo + "';'" + new Timestamp(System.currentTimeMillis()) + "';'REG';'" + initializeRa.initialize.userSystemLogin + "';'" + LocalDate.now() + "';'" + xml + "';" + registerRaInternal.getObjectId();
    }

    /**
     * @param registerRaInternal
     * @param xml
     * @return - a ra_internal_1_data_history record as (disabled, effective_from, effective_to, module, timestamp, "user", valid_from, xml, entry_id, event_id)
     */
    private String getDataHistory(RegisterRaInternal registerRaInternal, String xml) {
        String effectiveFrom = registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveFrom();
        String effectiveTo = registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveTo();
        return "false;'" + effectiveFrom + "';'" + effectiveTo + "';'REG';'" + new Timestamp(System.currentTimeMillis()) + "';'" + initializeRa.initialize.userSystemLogin + "';'" + LocalDate.now() + "';'" + xml + "';" + registerRaInternal.getObjectId();
    }

    /**
     * @param registerRaInternal
     * @return a ra_internal_1_natural_id record - (entry_id, natural_id)
     */
    private String getNaturalId(RegisterRaInternal registerRaInternal) {
        return registerRaInternal.getObjectId() + ";" + registerRaInternal.getObjectId();
    }

    /**
     * @param registerRaInternal
     * @return ra_internal_1_index records as (context, current, effective_from, effective_to, key, sequence, value, value_simplified, entry_id, id)
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     */
    private String createIndexes(RegisterRaInternal registerRaInternal) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Document dataDocument;
        try {
            dataDocument = XmlUtils.parse(XmlUtils.objectToXml(registerRaInternal));
        } catch (Exception ex) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Chyba pri spracovaní XML dát", ex);
        }
        final var registersFile = new File(initializeRa.initialize.rainternalRegistersConfigPath, InitializeRa.FILE_REGISTERS_CONFIGURATION);
        var registersConfig = XmlUtils.parse(registersFile, RegistersConfig.class);
        RegisterPlugin registerPlugin = null;

        for (RegisterPlugin plug : registersConfig.getRegisterPlugin()) {
            if (plug.getRegisterId().equals("RA_INTERNAL"))
                registerPlugin = plug;
        }
        if (registerPlugin == null) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Plugin pre RA_INTERNAL sa nenasiel");
        }

        Constructor<?> constructor = Class.forName("sk.is.urso.plugin.RaInternalReg1").getConstructor(RegisterPlugin.class, RegisterPluginConfig.class);
        File registerPluginFile = new File(initializeRa.initialize.rainternalRegisterPluginFilePath, registerPlugin.getRegisterId() + "_" + registerPlugin.getVersion() + InitializeRa.XML_EXTENSION);
        var registerPluginConfig = XmlUtils.parse(registerPluginFile, RegisterPluginConfig.class);

        AbstractRegPlugin plugin = (AbstractRegPlugin) constructor.newInstance(registerPlugin, registerPluginConfig);
        try {

            plugin.prepareXmlForUpdate(null, dataDocument);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        AbstractRegEntityData data = plugin.createNewDataEntityForInsert(dataDocument);
        try {
            dataDocument = plugin.prepareXmlForInsert(dataDocument, data);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        try {
            if (registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveFrom() != null) {
                data.setUcinnostOd(DateUtils.toDate(registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveFrom()));
            }
            if (registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveTo() != null) {
                data.setUcinnostDo(DateUtils.toDate(registerRaInternal.getData().get(registerRaInternal.getData().size() - 1).getEffectiveTo()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            initializeRa.createRegisterIndexes(plugin, data, dataDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        (context, current, effective_from, effective_to, key, sequence, value, value_simplified, entry_id)
        StringBuilder indexRows = new StringBuilder();
        for (var ind : data.getEntityIndexes()) {
            indexRows.append("'").append(ind.getKontext()).append("';").append(ind.getAktualny()).append(";").append(data.getUcinnostOd() != null ? "'" + DateUtils.toLocalDate(data.getUcinnostOd()) + "'" : null).append(";").append(data.getUcinnostDo() != null ? "'" + DateUtils.toLocalDate(data.getUcinnostDo()) + "'" : null).append(";'").append(ind.getKluc()).append("';").append(ind.getSekvencia()).append(";'").append(ind.getHodnota()).append("';'").append(ind.getHodnotaZjednodusena()).append("';").append(registerRaInternal.getObjectId());
            indexRows.append("\n");
        }
        return indexRows.toString();
    }
}
