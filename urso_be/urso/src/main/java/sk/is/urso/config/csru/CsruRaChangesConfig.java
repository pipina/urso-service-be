package sk.is.urso.config.csru;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.alfa.exception.CommonException;
import org.alfa.model.UserInfo;
import org.alfa.service.KeycloakTokenService;
import org.alfa.utils.DateUtils;
import org.alfa.utils.Utils;
import org.alfa.utils.XmlUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import sk.is.urso.be.Application;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataRequestCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.GetConsolidatedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;
import sk.is.urso.rest.model.CsruTypeEnum;
import sk.is.urso.controller.ZaznamRegistraController;
import sk.is.urso.csru.ra.changes.ChangeBaseCType;
import sk.is.urso.csru.ra.changes.ChangeBuildingNumberCType;
import sk.is.urso.csru.ra.changes.ChangeBuildingUnitSCType;
import sk.is.urso.csru.ra.changes.ChangeCountyCType;
import sk.is.urso.csru.ra.changes.ChangeDistrictCType;
import sk.is.urso.csru.ra.changes.ChangeMunicipalityCType;
import sk.is.urso.csru.ra.changes.ChangePropertyRegistrationNumberCType;
import sk.is.urso.csru.ra.changes.ChangeRegionCType;
import sk.is.urso.csru.ra.changes.ChangeRegisterCType;
import sk.is.urso.csru.ra.changes.ChangeStreetNameCType;
import sk.is.urso.csru.ra.changes.ConfirmChanges;
import sk.is.urso.csru.ra.changes.GetChanges;
import sk.is.urso.csru.ra.changes.ObjectFactory;
import sk.is.urso.model.csru.CsruChange;
import sk.is.urso.model.rachange.AbstractChangeBaseCType;
import sk.is.urso.model.rachange.BuildingNumberChangeRaInternal;
import sk.is.urso.model.rachange.BuildingUnitChangeRaInternal;
import sk.is.urso.model.rachange.Change;
import sk.is.urso.model.rachange.CountyChangeRaInternal;
import sk.is.urso.model.rachange.Data;
import sk.is.urso.model.rachange.DistrictChangeRaInternal;
import sk.is.urso.model.rachange.MunicipalityChangeRaInternal;
import sk.is.urso.model.rachange.PropertyRegistrationNumberChangeRaInternal;
import sk.is.urso.model.rachange.RegionChangeRaInternal;
import sk.is.urso.model.rachange.RegisterRaInternal;
import sk.is.urso.model.rachange.StreetNameChangeRaInternal;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.service.csru.CsruChangeService;
import sk.is.urso.util.EncryptionUtils;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@XmlSeeAlso(GetChanges.class)
public class CsruRaChangesConfig {

    private static final String RA_CHANGE_XML_TXT = "ra_change_xml.txt";
    @Autowired
    CsruEndpoint csruEndpoint;

    @Autowired
    EncryptionUtils encryptionUtils;

    @Autowired
    KeycloakTokenService keycloakTokenService;

    @Autowired
    CsruChangeService csruChangeService;

    @Autowired
    ZaznamRegistraController zaznamRegistraController;

    @Value("${integration.csru.url}")
    private String csruUrl;

    @Value("${integration.csru.username}")
    public String username;

    @Value("${integration.csru.password}")
    public String password;

    @Value("${integration.csru.ovmIsId}")
    private String csruOvmIsId;

    @Value("${mock.user}")
    public boolean mockUser;

    @Value("${user.system.login}")
    private String userSystemLogin;

    @Value("${integration.csru.ra.changes.xml.files}")
    private String raoChangesXmlFiles;

    private static final String XML_EXTENSION = ".xml";

    private static final String RA_INTERNAL = "RA_INTERNAL";
    private static final Integer RA_INTERNAL_REGISTER_VERSION = 1;

    private static final String RA_CHANGE = "RA_Change";
    private static final String CHANGES = "Changes";
    private static final String CONFIRM_CHANGES = "ConfirmChanges";
    private static final String ISEE = "ISEE";

    private static final String REGION = "REGION";
    private static final String COUNTY = "COUNTY";
    private static final String MUNICIPALITY = "MUNICIPALITY";
    private static final String DISTRICT = "DISTRICT";
    private static final String STREET_NAME = "STREET_NAME";
    private static final String PROPERTY_REGISTRATION_NUMBER = "PROPERTY_REGISTRATION_NUMBER";
    private static final String BUILDING_NUMBER = "BUILDING_NUMBER";
    private static final String BUILDING_UNIT = "BUILDING_UNIT";

    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String INSERT_SEQUENCE = "1";

    private static final String SPRACOVANIE_ZAZNAMU_Z_CSRU_SA_NEPODARILO = "Spracovanie %s záznamu z CSRU sa nepodarilo. %s | %s";
    private static final String CHYBA_PRI_UKLADANI_ZAZNAMU_S_OBJECTID = "Chyba pri ukladaní záznamu s objectId %s. %s";
    private static final String CHYBA_PRI_AKTUALIZOVANI_ZAZNAMU_S_OBJECTID = "Chyba pri aktualizovaní záznamu s objectId %s. %s";
    private static final String TYP_CREATED_REASON_NEEXISTUJE = "Typ created reason %s neexistuje.";
    private static final String TYP_NEEXISTUJE = "Typ %s neexistuje.";
    private static final String SCENARIO_NEEXISTUJE = "Scenario %s neexistuje.";
    private static final String ZAZNAM_S_OBJECTID_SA_NEPODARILO_AKTULIZOVAT = "Záznam s objectId %s sa nepodarilo aktulizovať. %s";
    private static final String ZAZNAM_S_OBJECTID_SA_NEPODARILO_AKTULIZOVAT_PRETOZE_NEEXISTUJE_V_DB = "Záznam s objectId %s sa nepodarilo aktulizovať, pretože neexistuje v databáze.";
    private static final String PRI_SPRACOVANI_ZMIEN_RA_Z_CSRU_NASTALA_CHYBA = "Pri spracovaní zmien RA z CSRU s ID %s nastala chyba. %s";
    private static final String ZIADNE_ZMENY_V_RA_Z_CSRU_NEBOLI_NAJDENE = "Žiadne zmeny v RA z CSRU neboli nájdené.";


    protected final ModelMapper modelMapper = new ModelMapper();
    public static final Logger log = LoggerFactory.getLogger(Application.class);//NOSONAR

//    @Bean
    public void runAtStartCsruRaChangesConfig() {
        Thread thread = new Thread(this::scheduledCsruRaImport);
        thread.start();
    }

//    @Scheduled(cron = "${cron.CsruRaChangesConfig.expression}")
//    @SchedulerLock(name = "TaskScheduler_csruRaChangesConfig", lockAtLeastForString = "${shedlock.least}")
    public void scheduledCsruRaImport() {
    	log.info("[RA][CHANGES][START]");
        JAXBElement<GetConsolidatedReferenceDataResponseCType> response = (JAXBElement<GetConsolidatedReferenceDataResponseCType>) csruEndpoint.callWebService(csruUrl, username, password, createGetConsolidatedReferenceDataRequestCType(CHANGES, -1));
        
        log.info("[RA][CHANGES]CSRU response1 from CsruRaChangesConfig.scheduledCsruRaImport: " + response.toString());

        DataPlaceholderCType dataPlaceholderCType = response.getValue().getPayload();
        log.info("[RA][CHANGES]CSRU response2 from CsruRaChangesConfig.scheduledCsruRaImport: " + dataPlaceholderCType.toString());
        sk.is.urso.csru.ra.changes.GetChangesResponse getChangesResponse = ((JAXBElement<sk.is.urso.csru.ra.changes.GetChangesResponse>) dataPlaceholderCType.getAny()).getValue();

        log.info("[RA][CHANGES]CSRU response3 from CsruRaChangesConfig.scheduledCsruRaImport: " + getChangesResponse.toString());
        CsruChange csruChange = new CsruChange();

        try {

            csruChange = csruChangeService.initialChange(CsruTypeEnum.RA.getValue(), null, null);

            ObjectFactory objectFactory = new ObjectFactory();
            String xml = XmlUtils.xmlToString(objectFactory.createGetChangesResponse(getChangesResponse));
            Files.writeString(Path.of(raoChangesXmlFiles, RA_CHANGE + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_mm").format(new Timestamp(System.currentTimeMillis())) + XML_EXTENSION), xml, StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);

            if (getChangesResponse.getReturn().getRegister().isEmpty()) {
                log.info(ZIADNE_ZMENY_V_RA_Z_CSRU_NEBOLI_NAJDENE);
                csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RA.getValue(), ZIADNE_ZMENY_V_RA_Z_CSRU_NEBOLI_NAJDENE);
                return;
            }

            int processedItems = 0;
            csruChange.setStart(new Timestamp(System.currentTimeMillis()));
            csruChangeService.save(csruChange);

            for (ChangeRegisterCType register : getChangesResponse.getReturn().getRegister()) {
                switch (register.getType().value()) {
                    case REGION:
                        processRecords(register, register.getRegionChange(), REGION);
                        break;
                    case COUNTY:
                        processRecords(register, register.getCountyChange(), COUNTY);
                        break;
                    case MUNICIPALITY:
                        processRecords(register, register.getMunicipalityChange(), MUNICIPALITY);
                        break;
                    case DISTRICT:
                        processRecords(register, register.getDistrictChange(), DISTRICT);
                        break;
                    case STREET_NAME:
                        processRecords(register, register.getStreetNameChange(), STREET_NAME);
                        break;
                    case PROPERTY_REGISTRATION_NUMBER:
                        processRecords(register, register.getPropertyRegistrationNumberChange(), PROPERTY_REGISTRATION_NUMBER);
                        break;
                    case BUILDING_NUMBER:
                        processRecords(register, register.getBuildingNumberChange(), BUILDING_NUMBER);
                        break;
                    case BUILDING_UNIT:
                        processRecords(register, register.getBuildingUnitChange(), BUILDING_UNIT);
                        break;
                    default:
                        log.error(String.format(TYP_NEEXISTUJE, register.getType().value()));
                        break;
                }
                processedItems++;
            }

            csruChange.setProcessedItems(processedItems);
            log.info("[RA][CHANGES]Send response");
            sendResponseToCsru(getChangesResponse.getReturn().getChangesId(), csruChange);
            log.info("[RA][CHANGES][END]");

        } catch (Exception ex) {
            csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RA.getValue(), ex.getMessage());
            ex.printStackTrace();
        }
    }

    private <T extends ChangeBaseCType> void processRecords(ChangeRegisterCType register, List<T> changes, String registerRaType) throws IOException {
        for (var change : changes) {
            try {
                process(register, change);
            } catch (Exception ex) {
                Files.writeString(Path.of(raoChangesXmlFiles, RA_CHANGE_XML_TXT), "\nChyba pri spracovani RA register type: " + register.getType().value() + "s objectId : " + change.getObjectId(), StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
                log.error(String.format(SPRACOVANIE_ZAZNAMU_Z_CSRU_SA_NEPODARILO, registerRaType, ex.getMessage(), change));
            }
        }
    }

    private <T extends ChangeBaseCType> void process(ChangeRegisterCType register, T changeCType) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.setLogin(userSystemLogin);
        userInfo.setAdministrator(true);
        
        //fix ked v PropertyRegistrationNumber v tagu <Building><BuildingPurpose><Codelist> posielaju "<CodelistItem/>"
        if(changeCType instanceof sk.is.urso.csru.ra.changes.ChangePropertyRegistrationNumberCType)
        {
        	sk.is.urso.csru.ra.changes.ChangePropertyRegistrationNumberCType change = (sk.is.urso.csru.ra.changes.ChangePropertyRegistrationNumberCType)changeCType;
        	if(change.getBuilding()!=null && change.getBuilding().getBuildingPurpose() !=null && change.getBuilding().getBuildingPurpose().getCodelist() !=null && change.getBuilding().getBuildingPurpose().getCodelist().getCodelistItem() !=null  && change.getBuilding().getBuildingPurpose().getCodelist().getCodelistItem().getItemCode() ==null && change.getBuilding().getBuildingPurpose().getCodelist().getCodelistItem().getItemName() ==null)
        		change.getBuilding().getBuildingPurpose().getCodelist().setCodelistItem(null);
        }

        Files.writeString(Path.of(raoChangesXmlFiles, RA_CHANGE_XML_TXT), "\nSpracovavam RA register type: " + register.getType().value() + "s objectId : " + changeCType.getObjectId(), StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);

        switch (changeCType.getCreatedReason()) {
            case CREATE:
            case IMPORT:
                RegisterRaInternal registerRaInternalForPut = prepareRegiserRaInternalForPut(register, changeCType);
                try {
                    ZaznamRegistraInputDetail registerEntryInsertDetail = prepareRegisterEntryInsertDetail(XmlUtils.objectToXml(registerRaInternalForPut), RA_INTERNAL);
                    registerEntryInsertDetail.setPouzivatel(userSystemLogin);
                    zaznamRegistraController.zaznamRegistraPut(registerEntryInsertDetail, userInfo);
                } catch (Exception ex) {
                    throw new CommonException(String.format(CHYBA_PRI_UKLADANI_ZAZNAMU_S_OBJECTID, changeCType.getObjectId(), ex.getMessage()), ex);
                }
                break;
            case UPDATE:
            case REVERT:
            case CORRECT:
            case CANCEL:
                RegisterRaInternal registerRaInternalForPost = prepareRegiserRaInternalForPost(register, changeCType, userInfo);
                try {
                    ZaznamRegistraInputDetail registerEntryUpdateDetail = prepareRegisterEntryUpdateDetail(XmlUtils.objectToXml(registerRaInternalForPost), RA_INTERNAL, changeCType.getObjectId());
                    registerEntryUpdateDetail.setPouzivatel(userSystemLogin);
                    zaznamRegistraController.zaznamRegistraPost(registerEntryUpdateDetail, userInfo);
                } catch (Exception ex) {
                    throw new CommonException(String.format(CHYBA_PRI_AKTUALIZOVANI_ZAZNAMU_S_OBJECTID, changeCType.getObjectId(), ex.getMessage()), ex);
                }
                break;
            default:
                throw new CommonException(String.format(TYP_CREATED_REASON_NEEXISTUJE, changeCType.getCreatedReason()));
        }
        Files.writeString(Path.of(raoChangesXmlFiles, RA_CHANGE_XML_TXT), "\nKoncim spracovanie RA register type: " + register.getType().value() + "s objectId : " + changeCType.getObjectId(), StandardCharsets.UTF_8, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
    }

    private RegisterRaInternal prepareRegiserRaInternalForPut(ChangeRegisterCType register, ChangeBaseCType changeCType) {
        RegisterRaInternal registerRaInternal = new RegisterRaInternal();
        registerRaInternal.setData(new ArrayList<>());
        Data data = new Data();
        Change change = new Change();

        AbstractChangeBaseCType abstractChangeBaseCType = getChangeRaInternal(register, changeCType);

        change.setChangeRaInternal(abstractChangeBaseCType);
        change.setCount(String.valueOf(register.getCount()));

        data.setChange(change);
        data.setCurrent(isValidToday(changeCType.getValidFrom(), changeCType.getValidTo()));
        data.setEffectiveFrom(changeCType.getValidFrom().toString().substring(0, 10));
        data.setEffectiveTo(changeCType.getValidTo().toString().substring(0, 10));
        data.setSequence(INSERT_SEQUENCE);

        registerRaInternal.setObjectId(String.valueOf(changeCType.getObjectId()));
        registerRaInternal.setType(register.getType().value());
        registerRaInternal.getData().add(data);

        return registerRaInternal;
    }

    private RegisterRaInternal prepareRegiserRaInternalForPost(ChangeRegisterCType register, ChangeBaseCType changeCType, UserInfo userInfo) {
        RegisterRaInternal registerRaInternalToBeUpdated;
        String seq;
        try {
            ResponseEntity<ZaznamRegistraOutputDetail> registerRarecord = zaznamRegistraController.zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(RA_INTERNAL, 1, changeCType.getObjectId(), userInfo);
            registerRaInternalToBeUpdated = XmlUtils.parse(registerRarecord.getBody().getData(), RegisterRaInternal.class);
            seq = String.valueOf(Utils.objectToInt(registerRaInternalToBeUpdated.data.get(registerRaInternalToBeUpdated.data.size() - 1).sequence) + 1);
        } catch (CommonException ex) {
            if (ex.getStatus() == HttpStatus.NOT_FOUND) {
                throw new CommonException(String.format(ZAZNAM_S_OBJECTID_SA_NEPODARILO_AKTULIZOVAT_PRETOZE_NEEXISTUJE_V_DB, changeCType.getObjectId()));
            } else {
                throw new CommonException(String.format(ZAZNAM_S_OBJECTID_SA_NEPODARILO_AKTULIZOVAT, changeCType.getObjectId(), ex.getMessage()));
            }
        }
        RegisterRaInternal registerRaInternal = prepareRegiserRaInternalForPut(register, changeCType);
        // update
        registerRaInternalToBeUpdated.data.get(registerRaInternalToBeUpdated.data.size() - 1).setCurrent(FALSE);
        registerRaInternal.data.get(0).sequence = seq;
        registerRaInternalToBeUpdated.data.add(registerRaInternal.data.get(0));
        return registerRaInternalToBeUpdated;
    }

    private AbstractChangeBaseCType getChangeRaInternal(ChangeRegisterCType register, ChangeBaseCType changeCType) {
        AbstractChangeBaseCType abstractChangeBaseCType;
        switch (register.getType().value()) {
            case REGION:
                abstractChangeBaseCType = getRegionChangeRaInternal((ChangeRegionCType) changeCType);
                break;
            case COUNTY:
                abstractChangeBaseCType = getCountyChangeRaInternal((ChangeCountyCType) changeCType);
                break;
            case MUNICIPALITY:
                abstractChangeBaseCType = getMunicipalityChangeRaInternal((ChangeMunicipalityCType) changeCType);
                break;
            case DISTRICT:
                abstractChangeBaseCType = getDistrictChangeRaInternal((ChangeDistrictCType) changeCType);
                break;
            case STREET_NAME:
                abstractChangeBaseCType = getStreetNameChangeRaInternal((ChangeStreetNameCType) changeCType);
                break;
            case PROPERTY_REGISTRATION_NUMBER:
                abstractChangeBaseCType = getPropertyRegistrationNumberChangeRaInternal((ChangePropertyRegistrationNumberCType) changeCType);
                break;
            case BUILDING_NUMBER:
                abstractChangeBaseCType = getBuildingNumberChangeRaInternal((ChangeBuildingNumberCType) changeCType);
                break;
            case BUILDING_UNIT:
                abstractChangeBaseCType = getBuildingUnitChangeRaInternal((ChangeBuildingUnitSCType) changeCType);
                break;
            default:
                throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, String.format(TYP_NEEXISTUJE, register.getType().value()));
        }

        abstractChangeBaseCType.setDatabaseOperation(changeCType.getDatabaseOperation().value());
        abstractChangeBaseCType.setObjectId(String.valueOf(changeCType.getObjectId()));
        abstractChangeBaseCType.setVersionId(String.valueOf(changeCType.getVersionId()));
        abstractChangeBaseCType.setCreatedReason(changeCType.getCreatedReason().value());

        return abstractChangeBaseCType;
    }

    private RegionChangeRaInternal getRegionChangeRaInternal(ChangeRegionCType changeRegionCType) {
        RegionChangeRaInternal regionChangeRaInternal = new RegionChangeRaInternal();

        regionChangeRaInternal.setRegion(changeRegionCType.getRegion());

        return regionChangeRaInternal;
    }

    private CountyChangeRaInternal getCountyChangeRaInternal(ChangeCountyCType changeCountyCType) {
        CountyChangeRaInternal countyChangeRaInternal = new CountyChangeRaInternal();

        countyChangeRaInternal.setCounty(changeCountyCType.getCounty());
        countyChangeRaInternal.setRegionIdentifier(changeCountyCType.getRegionIdentifier());

        return countyChangeRaInternal;
    }

    private MunicipalityChangeRaInternal getMunicipalityChangeRaInternal(ChangeMunicipalityCType changeMunicipalityCType) {
        MunicipalityChangeRaInternal municipalityChangeRaInternal = new MunicipalityChangeRaInternal();

        municipalityChangeRaInternal.setMunicipality(changeMunicipalityCType.getMunicipality());
        municipalityChangeRaInternal.setCountyIdentifier(changeMunicipalityCType.getCountyIdentifier());
        municipalityChangeRaInternal.setStatus(changeMunicipalityCType.getStatus());
        municipalityChangeRaInternal.setCityIdentifier(changeMunicipalityCType.getCityIdentifier());

        return municipalityChangeRaInternal;
    }

    private DistrictChangeRaInternal getDistrictChangeRaInternal(ChangeDistrictCType changeDistrictCType) {
        DistrictChangeRaInternal districtChangeRaInternal = new DistrictChangeRaInternal();

        districtChangeRaInternal.setDistrict(changeDistrictCType.getDistrict());
        districtChangeRaInternal.setMunicipalityIdentifier(changeDistrictCType.getMunicipalityIdentifier());

        return districtChangeRaInternal;
    }

    private StreetNameChangeRaInternal getStreetNameChangeRaInternal(ChangeStreetNameCType changeStreetNameCType) {
        StreetNameChangeRaInternal streetNameChangeRaInternal;
        streetNameChangeRaInternal = modelMapper.map(changeStreetNameCType, StreetNameChangeRaInternal.class);
        streetNameChangeRaInternal.setStreetName(changeStreetNameCType.getStreetName());
        if (!changeStreetNameCType.getDistrictIdentifier().isEmpty()) {
            streetNameChangeRaInternal.setDistrictIdentifier(changeStreetNameCType.getDistrictIdentifier());
        }
        if (!changeStreetNameCType.getMunicipalityIdentifier().isEmpty()) {
            streetNameChangeRaInternal.setMunicipalityIdentifier(changeStreetNameCType.getMunicipalityIdentifier());
        }

        return streetNameChangeRaInternal;
    }

    private PropertyRegistrationNumberChangeRaInternal getPropertyRegistrationNumberChangeRaInternal(ChangePropertyRegistrationNumberCType propertyRegistrationNumberCType) {
        PropertyRegistrationNumberChangeRaInternal propertyRegistrationNumberChangeRaInternal = new PropertyRegistrationNumberChangeRaInternal();

        propertyRegistrationNumberChangeRaInternal.setBuilding(propertyRegistrationNumberCType.getBuilding());
        propertyRegistrationNumberChangeRaInternal.setPropertyRegistrationNumber(propertyRegistrationNumberCType.getPropertyRegistrationNumber());
        propertyRegistrationNumberChangeRaInternal.setMunicipalityIdentifier(propertyRegistrationNumberCType.getMunicipalityIdentifier());
        propertyRegistrationNumberChangeRaInternal.setDistrictIdentifier(propertyRegistrationNumberCType.getDistrictIdentifier());

        return propertyRegistrationNumberChangeRaInternal;
    }

    private BuildingNumberChangeRaInternal getBuildingNumberChangeRaInternal(ChangeBuildingNumberCType changeBuildingNumberCType) {
        BuildingNumberChangeRaInternal buildingNumberChangeRaInternal = new BuildingNumberChangeRaInternal();

        buildingNumberChangeRaInternal.setBuildingNumber(changeBuildingNumberCType.getBuildingNumber());
        buildingNumberChangeRaInternal.setBuildingIndex(changeBuildingNumberCType.getBuildingIndex());
        buildingNumberChangeRaInternal.setPostalCode(changeBuildingNumberCType.getPostalCode());
        buildingNumberChangeRaInternal.setPropertyRegistrationNumberIdentifier(changeBuildingNumberCType.getPropertyRegistrationNumberIdentifier());
        buildingNumberChangeRaInternal.setStreetNameIdentifier(changeBuildingNumberCType.getStreetNameIdentifier());
        buildingNumberChangeRaInternal.setVerifiedAt(changeBuildingNumberCType.getVerifiedAt());
        buildingNumberChangeRaInternal.setAddressPoint(changeBuildingNumberCType.getAddressPoint());

        return buildingNumberChangeRaInternal;
    }

    private BuildingUnitChangeRaInternal getBuildingUnitChangeRaInternal(ChangeBuildingUnitSCType changeBuildingUnitSCType) {
        BuildingUnitChangeRaInternal buildingUnitChangeRaInternal = new BuildingUnitChangeRaInternal();

        buildingUnitChangeRaInternal.setBuildingNumberIdentifier(changeBuildingUnitSCType.getBuildingNumberIdentifier());
        buildingUnitChangeRaInternal.setBuildingUnit(changeBuildingUnitSCType.getBuildingUnit());

        return buildingUnitChangeRaInternal;
    }

    private String isValidToday(XMLGregorianCalendar effectiveFrom, XMLGregorianCalendar effectiveTo) {
        if (effectiveFrom.compare(DateUtils.nowXmlDate()) == -1 && effectiveTo.compare(DateUtils.nowXmlDate()) == 1)
            return TRUE;
        return FALSE;
    }

    private JAXBElement<GetConsolidatedReferenceDataRequestCType> createGetConsolidatedReferenceDataRequestCType(String scenario, long changesId) {
        GetConsolidatedReferenceDataRequestCType dataRequestCType = new GetConsolidatedReferenceDataRequestCType();
        dataRequestCType.setOvmIsId(csruOvmIsId);
        dataRequestCType.setOeId(RA_CHANGE);
        dataRequestCType.setOvmCorrelationId(UUID.randomUUID().toString());
        dataRequestCType.setOvmTransactionId(UUID.randomUUID().toString());
        dataRequestCType.setScenario(scenario);

        switch (scenario) {
            case CHANGES:
                dataRequestCType.setPayload(getPayloadGetChanges());
                break;
            case CONFIRM_CHANGES:
                dataRequestCType.setPayload(getPayloadConfirmChanges(changesId));
                break;
            default:
                throw new CommonException(String.format(SCENARIO_NEEXISTUJE, scenario));
        }

        log.info("[RA][CHANGES]CSRU request from CsruRaChangesConfig.createGetConsolidatedReferenceDataRequestCType: " + dataRequestCType);

        sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory of = new sk.is.urso.model.csru.api.sync.GetConsolidatedReferenceDataServiceSync.ObjectFactory();
        return of.createGetConsolidatedReferenceDataRequest(dataRequestCType);
    }

    private void sendResponseToCsru(Long changesId, CsruChange csruChange) {
        JAXBElement<GetConsolidatedReferenceDataRequestCType> request = createGetConsolidatedReferenceDataRequestCType(CONFIRM_CHANGES, changesId);
        JAXBElement<GetConsolidatedReferenceDataResponseCType> responseConfirmChanges;
        try {
            responseConfirmChanges = (JAXBElement<GetConsolidatedReferenceDataResponseCType>) csruEndpoint.callWebService(csruUrl, username, password, request);
            log.info("[RA][CHANGES]CSRU response from CsruRaChangesConfig.sendResponseToCsru: " + responseConfirmChanges.toString());
        } catch (Exception e) {
            log.error(String.format(PRI_SPRACOVANI_ZMIEN_RA_Z_CSRU_NASTALA_CHYBA, changesId, e.getMessage()));
            throw new CommonException(String.format(PRI_SPRACOVANI_ZMIEN_RA_Z_CSRU_NASTALA_CHYBA, changesId, null), e);
        }
        DataPlaceholderCType dataPlaceholderCTypeConfirmChange = responseConfirmChanges.getValue().getPayload();
        sk.is.urso.csru.ra.changes.ConfirmChangesResponse confirmChangesResponse = ((JAXBElement<sk.is.urso.csru.ra.changes.ConfirmChangesResponse>) dataPlaceholderCTypeConfirmChange.getAny()).getValue();
        if (confirmChangesResponse.getReturn().getResultCode() == 1L) {
            log.info(String.format("Spracovanie zmien RA z CSRU s ID %s prešlo.", changesId));
            csruChangeService.createOkChange(csruChange, null);
        } else {
            log.error(String.format(PRI_SPRACOVANI_ZMIEN_RA_Z_CSRU_NASTALA_CHYBA, changesId, confirmChangesResponse.getReturn().getResultReasons()));
            csruChangeService.createErrorChange(csruChange, CsruTypeEnum.RA.getValue(), String.format(PRI_SPRACOVANI_ZMIEN_RA_Z_CSRU_NASTALA_CHYBA, changesId, confirmChangesResponse.getReturn().getResultReasons()));
        }
    }

    private DataPlaceholderCType getPayloadGetChanges() {
        ObjectFactory objectFactory = new ObjectFactory();
        GetChanges getChanges = new GetChanges();
        GetChanges.Request requestPayload = new GetChanges.Request();
        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        requestPayload.setCorrId(ISEE);
        getChanges.setRequest(requestPayload);
        dataPlaceholder.setAny(objectFactory.createGetChanges(getChanges));

        return dataPlaceholder;
    }

    private DataPlaceholderCType getPayloadConfirmChanges(long changesId) {
        ObjectFactory objectFactory = new ObjectFactory();
        ConfirmChanges getChanges = new ConfirmChanges();
        ConfirmChanges.Request requestPayload = new ConfirmChanges.Request();
        DataPlaceholderCType dataPlaceholder = new DataPlaceholderCType();
        requestPayload.setCorrId(ISEE);
        requestPayload.setChangesId(changesId);
        getChanges.setRequest(requestPayload);
        dataPlaceholder.setAny(objectFactory.createConfirmChanges(getChanges));

        return dataPlaceholder;
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryInsertDetail(String xml, String registerId) {
        ZaznamRegistraInputDetail registerEntryInsertDetail = new ZaznamRegistraInputDetail();
        registerEntryInsertDetail.setData(xml);
        registerEntryInsertDetail.setUcinnostOd(LocalDate.now());
        registerEntryInsertDetail.setUcinnostDo(null);
        registerEntryInsertDetail.setRegisterId(registerId);
        registerEntryInsertDetail.setVerziaRegistraId(RA_INTERNAL_REGISTER_VERSION);
        registerEntryInsertDetail.setPlatny(true);
        registerEntryInsertDetail.setPlatnostOd(LocalDate.now());
        return registerEntryInsertDetail;
    }

    private ZaznamRegistraInputDetail prepareRegisterEntryUpdateDetail(String xml, String registerId, Long entryId) {
        ZaznamRegistraInputDetail registerEntryUpdateDetail = new ZaznamRegistraInputDetail();
        registerEntryUpdateDetail.setData(xml);
        registerEntryUpdateDetail.setUcinnostOd(DateUtils.toLocalDate(DateUtils.nowXmlDate()));
        registerEntryUpdateDetail.setUcinnostDo(null);
        registerEntryUpdateDetail.setRegisterId(registerId);
        registerEntryUpdateDetail.setVerziaRegistraId(RA_INTERNAL_REGISTER_VERSION);
        registerEntryUpdateDetail.setPlatny(true);
        registerEntryUpdateDetail.setPlatnostOd(DateUtils.toLocalDate(DateUtils.nowXmlDate()));
        registerEntryUpdateDetail.setZaznamId(entryId); // entryId berieme z nazvu suboru
        return registerEntryUpdateDetail;
    }
}
