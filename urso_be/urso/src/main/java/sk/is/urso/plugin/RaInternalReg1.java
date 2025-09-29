package sk.is.urso.plugin;

import lombok.Getter;
import lombok.NonNull;
import org.alfa.exception.CommonException;
import org.alfa.model.ListRequestModel;
import org.alfa.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
import sk.is.urso.reg.AbstractRegEntityIndex;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.PluginRepository;
import sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou;
import sk.is.urso.reg.model.DvojicaKlucHodnotaVolitelna;
import sk.is.urso.reg.model.ZaznamRegistra;
import sk.is.urso.reg.model.ZaznamRegistraList;
import sk.is.urso.reg.model.ZaznamRegistraListRequestFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    // tu by mala stacit default implementacia ktoru aj tak volame
//    @Override
//    public <T extends AbstractRegEntityData> void saveDataEntity(T entity) {
//        super.saveDataEntity(entity);
//        saveNaturalIdEntity(new RaInternalReg1NaturalIdEntity(), entity);
//    }

    // entryId som presunul do prepareXmlForInsert, teda default implementacia by mala stacit
//    @Override
//    public AbstractRegEntityData createNewDataEntityForInsert(Document document){
//        RaInternalReg1DataEntity entity = (RaInternalReg1DataEntity) super.createNewDataEntityForInsert(document);
//        String objectId = document.getElementsByTagName("objectId").item(0).getFirstChild().getNodeValue();
//        entity.setId(Utils.objectToLong(objectId));
//        return entity;
//    }

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

    // objectId je nastavené ako ID pole, teda impl. z AbstractRegPlugin by mala stačiť
//    @Override
//    public Node getIdFromXml(Document xml, RegisterEntryField field) {
//        var idNode = xml.getElementsByTagName("objectId").item(0);
//
//        if (idNode == null) {
//            throw new CommonException(HttpStatus.BAD_REQUEST, NIE_JE_VYPLNENE_ID + "objectId!");
//        }
//        if (idNode.getTextContent() == null) {
//            throw new CommonException(HttpStatus.BAD_REQUEST, NIE_JE_VYPLNENE_ID + "objectId!");
//        }
//        return idNode;
//    }

    public ZaznamRegistraList getComposedAddress(Long entryId) {
        Specification<RaInternalReg1IndexEntity> specification =
                (root, query, builder) -> builder.and(builder.equal(root.get(FIELD_ENTRY_ID), entryId), root.get(FIELD_KEY).in(RA_INTERNAL_LEVELS));
        List<RaInternalReg1IndexEntity> raInternalReg1DataEntity = getIndexRepo().findAll(specification);

        if (raInternalReg1DataEntity.isEmpty()) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Záznam s entryId: " + entryId + " neexistuje.");
        }

        Specification<RaInternalReg1IndexEntity> spec = getSpecByEntryId(entryId, getLevel(raInternalReg1DataEntity.get(0)));
        List<RaInternalReg1IndexEntity> raInternalReg1IndexEntityList = indexRepo.findAll(spec);

        raInternalReg1IndexEntityList.addAll(getAdditionalData(raInternalReg1IndexEntityList));

        return joinByEntryId(raInternalReg1IndexEntityList);
    }

    private Specification<RaInternalReg1IndexEntity> getSpecByEntryId(Long entryId, int maxLevel) {
        return (root, query, builder) -> {
            query.distinct(true);

            final List<Predicate> predicates = new ArrayList<>();

            for (int i = maxLevel; i < RA_INTERNAL_LEVELS.size(); i++) {
                predicates.add(root.get(FIELD_ENTRY_ID).in(getSubquery(query, builder, null, i, entryId, maxLevel)));
            }
            return builder.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private int getLevel(RaInternalReg1IndexEntity raInternalReg1DataEntity) {
        for (int i = 0; i < RA_INTERNAL_LEVELS.size(); i++) {
            if (raInternalReg1DataEntity.getKluc().equals(RA_INTERNAL_LEVELS.get(i))) {
                return i;
            }
        }
        throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje požadovaná úroveň adresy.");
    }


    @Override
    public ZaznamRegistraList findRegisterEntries(@NonNull ZaznamRegistraListRequestFilter filter, @NonNull ListRequestModel listRequest) {
        Specification<RaInternalReg1IndexEntity> spec = getJoinedAdressesSpecifications(filter, listRequest);
        List<RaInternalReg1IndexEntity> abstractRegEntityIndexes = indexRepo.findAll(spec);

        abstractRegEntityIndexes.addAll(getAdditionalData(abstractRegEntityIndexes));
        return joinByEntryId(abstractRegEntityIndexes);
    }

    private List<RaInternalReg1IndexEntity> getAdditionalData(List<RaInternalReg1IndexEntity> data) {
        List<RaInternalReg1IndexEntity> tempIndexList = new ArrayList<>();

        for (RaInternalReg1IndexEntity raInternalReg1IndexEntity : data) {
            if (raInternalReg1IndexEntity.getKluc().equals(STREET_NAME_ID) || raInternalReg1IndexEntity.getKluc().equals(CITY_ID)) {
                Specification<RaInternalReg1IndexEntity> specification = (root, query, builder) -> {
                    final List<Predicate> predicates = new ArrayList<>();
                    predicates.add(builder.equal(root.get(FIELD_ENTRY_ID), Long.parseLong(raInternalReg1IndexEntity.getHodnota())));
                    if (raInternalReg1IndexEntity.getKluc().equals(STREET_NAME_ID)) {
                        predicates.add(builder.equal(root.get(FIELD_KEY), STREET_NAME));
                    } else {
                        predicates.add(builder.equal(root.get(FIELD_KEY), CITY));
                    }
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                };

                tempIndexList.add(indexRepo.findAll(specification).get(0));
            }
        }
        return tempIndexList;
    }

    private ZaznamRegistraList joinByEntryId(List<RaInternalReg1IndexEntity> response) {

        ZaznamRegistraList zaznamRegistraList = new ZaznamRegistraList();
        List<ZaznamRegistra> registerEntryList = new ArrayList<>();

        HashMap<Long, List<RaInternalReg1IndexEntity>> hashMap = createAndFillHashMap(response);

        hashMap.forEach((k, v) -> {
            List<DvojicaKlucHodnotaSHistoriou> dvojicaKlucHodnotaSHistoriouList = new ArrayList<>();
            ZaznamRegistra zaznamRegistra = new ZaznamRegistra()
                    .registerId("RA_INTERNAL")
                    .verziaRegistraId(1)
                    .platnostOd(null)
                    .platny(null)
                    .zaznamId(v.get(0).getZaznamId())
                    .ucinnostOd(DateUtils.toLocalDate(v.get(0).getUcinnostOd()))
                    .ucinnostDo(DateUtils.toLocalDate(v.get(0).getUcinnostOd()))
                    .polia(dvojicaKlucHodnotaSHistoriouList);
            for (AbstractRegEntityIndex value : v) {
                DvojicaKlucHodnotaSHistoriou dvojicaKlucHodnotaSHistoriou = new DvojicaKlucHodnotaSHistoriou()
                        .kluc(value.getKluc())
                        .hodnota(value.getHodnota())
                        .ucinnostOd(DateUtils.toLocalDate(value.getUcinnostOd()))
                        .ucinnostDo(DateUtils.toLocalDate(value.getUcinnostDo()));
                dvojicaKlucHodnotaSHistoriouList.add(dvojicaKlucHodnotaSHistoriou);
            }
            registerEntryList.add(zaznamRegistra);
        });

        zaznamRegistraList.setResult(registerEntryList);
        zaznamRegistraList.setTotal((long) hashMap.size());
        return zaznamRegistraList;
    }

    private HashMap<Long, List<RaInternalReg1IndexEntity>> createAndFillHashMap(List<RaInternalReg1IndexEntity> response) {
        HashMap<Long, List<RaInternalReg1IndexEntity>> hashMap = new HashMap<>();
        for (RaInternalReg1IndexEntity responseRecord : response) {
            if (hashMap.containsKey(responseRecord.getZaznamId())) {
                hashMap.get(responseRecord.getZaznamId()).add(responseRecord);

            } else {
                List<RaInternalReg1IndexEntity> array = new ArrayList<>();
                array.add(responseRecord);
                hashMap.put(responseRecord.getZaznamId(), array);
            }
        }
        return hashMap;
    }

    private Specification<RaInternalReg1IndexEntity> getJoinedAdressesSpecifications(ZaznamRegistraListRequestFilter filter, ListRequestModel listRequest) {
        return (root, query, builder) -> {
            query.distinct(true);
            final List<Predicate> predicates = new ArrayList<>();

            for (int i = 0; i < RA_INTERNAL_LEVELS.size(); i++) {
                predicates.add(root.get(FIELD_ENTRY_ID).in(getSubquery(query, builder, filter, i, null, 0)));
            }

            return builder.or(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private Subquery<RaInternalReg1IndexEntity> getSubquery(CriteriaQuery<?> query, CriteriaBuilder builder, ZaznamRegistraListRequestFilter filter, int subqueryJoinTableNumber, Long entryId, int maxLevel) {
        Subquery<RaInternalReg1IndexEntity> subquery = query.subquery(RaInternalReg1IndexEntity.class);
        Root<RaInternalReg1IndexEntity> subqueryRoot = subquery.from(RaInternalReg1IndexEntity.class);
        Join<RaInternalReg1DataEntity, RaInternalReg1IndexEntity> lastJoin = null;
        Predicate predicate = null;
        List<DvojicaKlucHodnotaVolitelna> sortedLevels = null;
        if (filter != null) {
            sortedLevels = getSortedLevels(filter.getPolia());
        }

        for (int i = maxLevel; i < RA_INTERNAL_LEVELS.size() - 1; i++) {
            Join<RaInternalReg1IndexEntity, RaInternalReg1DataEntity> r2;

            if (i == maxLevel) {
                r2 = subqueryRoot.join(RA_INTERNAL_DATA);
                predicate = builder.and(builder.equal(subqueryRoot.get(FIELD_KEY), RA_INTERNAL_LEVELS.get(i)));
                if (i == subqueryJoinTableNumber) {
                    subquery.select(subqueryRoot.get(FIELD_ENTRY_ID));
                }

                if (!sortedLevels.isEmpty() && sortedLevels.get(0).getKluc().equals(RA_INTERNAL_LEVELS.get(0))) {
                    predicate = builder.and(predicate, builder.equal(subqueryRoot.get(FIELD_VALUE_SIMPLIFIED), sortedLevels.get(0).getHodnota()));
                    sortedLevels.remove(0);
                }

                if (entryId != null) {
                    predicate = builder.and(predicate, builder.equal(subqueryRoot.get(FIELD_ENTRY_ID), entryId));
                }
            } else {
                r2 = lastJoin.join(RA_INTERNAL_DATA);
            }

            Join<RaInternalReg1DataEntity, RaInternalReg1IndexEntity> joinToNextAddressLevel = r2.join(RA_INTERNAL_INDEX).join(RA_INTERNAL_NATURAL_ID).join(RA_INTERNAL_DATA).join(RA_INTERNAL_INDEX);
            predicate = builder.and(predicate, builder.equal(joinToNextAddressLevel.get(FIELD_KEY), RA_INTERNAL_LEVELS.get(i + 1)));


            if (!sortedLevels.isEmpty() && sortedLevels.get(0).getKluc().equals(RA_INTERNAL_LEVELS.get(i + 1))) {
                predicate = builder.and(predicate, builder.equal(joinToNextAddressLevel.get(FIELD_VALUE_SIMPLIFIED), sortedLevels.get(0).getHodnota()));
                sortedLevels.remove(0);
            }

            if (subqueryJoinTableNumber == i + 1) {
                subquery.select(joinToNextAddressLevel.get(FIELD_ENTRY_ID));
            }
            lastJoin = joinToNextAddressLevel;
        }

        subquery.where(predicate);
        return subquery;
    }

    private List<DvojicaKlucHodnotaVolitelna> getSortedLevels(List<DvojicaKlucHodnotaVolitelna> keyvalueList) {
        List<DvojicaKlucHodnotaVolitelna> filterLevels = new ArrayList<>();
        if (keyvalueList == null) {
            return Collections.emptyList();
        }
        for (String raLevel : RA_INTERNAL_LEVELS) {
            if (filterLevels.size() != keyvalueList.size()) {
                for (DvojicaKlucHodnotaVolitelna keyValue : keyvalueList) {
                    if (keyValue.getKluc().equals(raLevel)) {
                        filterLevels.add(keyValue);
                        break;
                    }
                }
            } else {
                return filterLevels;
            }
        }
        return filterLevels;
    }
}
