package sk.is.urso.plugin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import sk.is.urso.common.regconfig.plugin.v1.RegisterEntryField;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;
import sk.is.urso.plugin.entity.RaInternalReg1DataEntity;
import sk.is.urso.plugin.entity.RaInternalReg1DataHistoryEntity;
import sk.is.urso.plugin.entity.RaInternalReg1DataReferenceEntity;
import sk.is.urso.plugin.entity.RaInternalReg1IndexEntity;
import sk.is.urso.plugin.entity.RaInternalReg1NaturalIdEntity;
import sk.is.urso.plugin.repository.RaInternalReg1DataHistoryRepository;
import sk.is.urso.plugin.repository.RaInternalReg1DataReferenceRepository;
import sk.is.urso.plugin.repository.RaInternalReg1DataRepository;
import sk.is.urso.plugin.repository.RaInternalReg1IndexRepository;
import sk.is.urso.plugin.repository.RaInternalReg1NaturalIdRepository;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.PluginRepository;

import javax.xml.xpath.XPathExpressionException;
import java.util.Arrays;
import java.util.List;

@Getter
public class RaInternalReg1 extends AbstractRegPlugin {

    public static final List<String> RA_LEVELS = Arrays.asList("BuildingUnit", "BuildingNumber", "PropertyRegistrationNumber", "Municipality", "County", "Region", "Country");
    public static final List<String> RA_INTERNAL_LEVELS = Arrays.asList("BuildingUnit", "BuildingNumber", "PropertyRegistrationNumber", "MunicipalityItemName", "CountyItemName", "RegionItemName", "Country");

    public static final String STREET_NAME = "StreetName";
    public static final String STREET_NAME_ID = "StreetNameId";
    public static final String CITY = "City";
    public static final String CITY_ID = "CityId";
    public static final String FIELD_KEY = "key";
    public static final String RA_INTERNAL_INDEX = "raInternalIndex";
    public static final String RA_INTERNAL_DATA = "raInternalData";
    public static final String RA_INTERNAL_NATURAL_ID = "raInternalNaturalId";
    public static final String REGISTER_ID = "RA_INTERNAL";

    @Autowired
    RaInternalReg1DataRepository dataRepo;
    @Autowired
    RaInternalReg1IndexRepository indexRepo;
    @Autowired
    RaInternalReg1DataReferenceRepository dataReferenceRepo;
    @Autowired
    RaInternalReg1DataHistoryRepository dataHistoryRepo;
    @Autowired
    RaInternalReg1NaturalIdRepository naturalIdRepository;

    public RaInternalReg1(RegisterPlugin info, RegisterPluginConfig plugin) {
        super(info, plugin, RaInternalReg1DataEntity.class, RaInternalReg1IndexEntity.class, RaInternalReg1DataReferenceEntity.class, RaInternalReg1DataHistoryEntity.class, RaInternalReg1NaturalIdEntity.class);
    }

    @Override
    public RaInternalReg1DataRepository getDataRepository() {
        return this.dataRepo;
    }

    @Override
    public RaInternalReg1IndexRepository getIndexRepository() {
        return this.indexRepo;
    }

    @Override
    public RaInternalReg1DataReferenceRepository getDataReferenceRepository() {
        return this.dataReferenceRepo;
    }

    @Override
    public RaInternalReg1DataHistoryRepository getDataHistoryRepository() {
        return this.dataHistoryRepo;
    }

    @Override
    public RaInternalReg1NaturalIdRepository getNaturalIdRepository() {
        return this.naturalIdRepository;
    }

    /**
     * Tu nevraciame dalsie id nakolko idcka su z XMLka a musia sediet! Jednoducho vratime vstupne entityId.
     */
    @Override
    protected <T extends AbstractRegEntityData> Long getNextEmptyId(Long entityId, PluginRepository<T, Long> repo) {
        return entityId;
    }

    /**
     * Tu na rozdiel od default implementácie nastavíme entryId podľa objectId z XMLka
     *
     * @throws XPathExpressionException
     */
    @Override
    public <T extends AbstractRegEntityData> Document prepareXmlForInsert(Document inputXml, T entity) throws XPathExpressionException {
        RegisterEntryField field = getIdField();
        var idNode = this.getIdFromXml(inputXml, field);
        var objectId = idNode.getTextContent();
        entity.setId(Long.parseLong(objectId));
        checkId(objectId, field);
        return inputXml;
    }
}
