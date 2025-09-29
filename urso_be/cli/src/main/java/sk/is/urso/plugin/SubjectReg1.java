package sk.is.urso.plugin;


import lombok.Getter;
import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.alfa.service.UserInfoService;
import org.alfa.utils.SearchUtils;
import org.alfa.utils.XmlUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Document;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.plugin.entity.SubjectReg1DataHistoryEntity;
import sk.is.urso.plugin.entity.SubjectReg1DataReferenceEntity;
import sk.is.urso.plugin.entity.SubjectReg1IndexEntity;
import sk.is.urso.plugin.entity.SubjectReg1NaturalIdEntity;
import sk.is.urso.plugin.repository.SubjectReg1DataHistoryRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataReferenceRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataRepository;
import sk.is.urso.plugin.repository.SubjectReg1IndexRepository;
import sk.is.urso.plugin.repository.SubjectReg1NaturalIdRepository;
import sk.is.urso.plugin.repository.SubjectReg1RfoIdentificationRepository;
import sk.is.urso.plugin.repository.SubjectReg1RpoIdentificationRepository;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.AdditionalPluginOps;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.subject.v1.SubjectT;
import sk.is.urso.subject.v1.SubjectTypeT;

import java.util.List;

@Getter
public class SubjectReg1 extends AbstractRegPlugin implements IException {
    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public static final String REGISTER_ID = "SUBJECT";
    public static final RegisterId INTERNAL_REGISTER_ID = new RegisterId(REGISTER_ID, 1);
    public static final String ENUMERATION_COUNTRY_SLOVAKIA = "703";

    public static final String ENTITY_FIELD_SUBJECT_ID = "subjectId";
    public static final String EMAIL = "email";
    public static final String URI = "uri";
    public static final String PHONE = "phone";
    public static final String ADR_ZIP = "adr_zip";
    public static final String ADR_STATE = "adr_state";
    public static final String ADR_STREET = "adr_street";
    private static final String ADR_REGION = "adr_region";
    public static final String ADR_MUNICIPALITY = "adr_municipality";
    public static final String ADR_MUNICIPALITY_VALUE = "adr_municipality_itemValue";
    public static final String ADR_ADDRESS_USAGE = "adr_addressUsage";
    public static final String ADR_FORMATTED_ADDRESS = "adr_formattedAddress";
    public static final String ADR_REGISTRATION_NUMBER = "adr_registrationNumber";

    public static final String SU_NAME = "su_name";
    public static final String SU_SURNAME = "su_surname";
    public static final String SU_BIRTH_DATE = "su_birthDate";
    public static final String SU_ALTERNATIVE_NAME = "su_alternativeName";

    public static final String IDENTIFIER_VALUE = "identifierValue";
    public static final String IDENTIFIER_TYPE_CODE = "identifierTypeCode";

    public static final String ADDITIONAL_DATA_KEY = "additionalData_key";
    public static final String ADDITIONAL_DATA_VALUE = "additionalData_value";
    public static final String ADDITIONAL_DATA_MODULE_NAME = "additionalData_moduleName";

    public static final String REG = "REG";
    public static final String RFO = "RFO";
    public static final String RPO = "RPO";

    public static final String PO_LEGAL_FORM = "po_legalForm";
    public static final String SUBJ_FORMATTED_NAME = "subj_formattedname";

    public static final String ZP_ID = "zp_id";

    private static final String COUNTY = "County";
    private static final String COUNTRY = "Country";
    private static final String LEGAL_FORM = "LegalForm";
    private static final String POSTAL_CODE = "PostalCode";
    private static final String POST_OFFICE_BOX = "PostOfficeBox";
    private static final String BUILDING_NUMBER = "BuildingNumber";
    private static final String STREET_NAME = "StreetName";
    private static final String ADDRESS_TYPE = "AddressType";
    private static final String ADDRESS_LINE = "AddressLine";
    private static final String MUNICIPALITY = "Municipality";
    private static final String SOURCE_REGISTER_TYPE = "SourceRegisterType";
    private static final String CORPORATE_BODY_FULL_NAME = "CorporateBodyFullName";
    private static final String PROPERTY_REGISTRATION_NUMBER = "PropertyRegistrationNumber";
    private static final String CORPORATE_BODY_ALTERNATIVE_NAME = "CorporateBodyAlternativeName";

    public static final String INDEXED_FIELD_KEY_SUBJ_TYPE = "subj_type";
    public static final String INDEXED_FIELD_KEY_SUBJECT_ID = "subjectID";
    public static final String INDEXED_FIELD_KEY_RFO_ENTRY_ID = "rfoEntryId";
    public static final String INDEXED_FIELD_KEY_RFO_EXTERNAL_ID = "rfoExternalId";
    public static final String INDEXED_FIELD_KEY_RPO_ENTRY_ID = "rpoEntryId";
    public static final String INDEXED_FIELD_KEY_RPO_EXTERNAL_ID = "rpoExternalId";
    public static final String INDEXED_FIELD_KEY_SUBJECT_FORMATTED_NAME = "subj_formattedname";
    public static final String INDEXED_FIELD_KEY_NAME = "su_name";
    public static final String INDEXED_FIELD_KEY_SURNAME = "su_surname";
    public static final String INDEXED_FIELD_KEY_IDENTIFIER_TYPE = "identifierTypeCode";
    public static final String INDEXED_FIELD_KEY_IDENTIFIER_VALUE = "identifierValue";
    public static final String INDEXED_FIELD_KEY_BIRTH_DATE = "su_birthDate";
    public static final String INDEXED_FIELD_KEY_MUNICIPALITY_ITEM_CODE = "adr_municipality";
    public static final String INDEXED_FIELD_KEY_MUNICIPALITY_TEXT = "adr_municipality_itemValue";
    public static final String INDEXED_FIELD_KEY_ZIP = "adr_zip";
    public static final String INDEXED_FIELD_KEY_STATE = "adr_state";
    public static final String INDEXED_FIELD_KEY_STREET = "adr_street";
    public static final String INDEXED_FIELD_KEY_REGISTRATION_NUMBER = "adr_registrationNumber";
    public static final String INDEXED_FIELD_KEY_ADDRESS_USAGE = "adr_addressUsage";
    public static final String INDEXED_FIELD_KEY_REGION = "adr_region";
    public static final String INDEXED_FIELD_KEY_EMAIL = "email";
    public static final String INDEXED_FIELD_KEY_PHONE = "phone";

    public static final String FO = SubjectTypeT.FO.name();
    public static final String SZ = SubjectTypeT.SZ.name();
    public static final String ZO = SubjectTypeT.ZO.name();
    public static final String PO = SubjectTypeT.PO.name();
    public static final String ZP = SubjectTypeT.ZP.name();
    public static final String IDENTIFIER_TYPE_ID_CARD_NUMBER = "1";
    public static final String IDENTIFIER_TYPE_ICO = "7";
    public static final String IDENTIFIER_TYPE_DIC = "8";
    public static final String IDENTIFIER_TYPE_IC_DPH = "18";
    public static final String IDENTIFIER_TYPE_BIRTH_NUMBER = "9";
    public static final String IDENTIFIER_TYPE_PERSON_RECORD = "14";
    public static final String IDENTIFIER_TYPE_VERIFIED_PERSON_RECORD = "15";

    private static final String RPO_READ_ERROR = "Failed to read RPO register";
    private static final String RFO_READ_ERROR = "Failed to read RFO register";
    private static final String NO_IDENTIFIER_TYPE_ERROR = "Hodnota identifikátora nesmie byť uvedená bez typu identifikátora";

    public static final String ADDRESS_TYPE_ELECTRONIC = "12";
    public static final String ADDRESS_TYPE_CONTACT = "10";
    public static final String POSTAL_ADDRESS = "postalAddress";
    public static final String VALID = "valid";
    private static final String EFFECTIVE_FROM = "EffectiveFrom";
    public static final String EFFECTIVE_TO = "effectiveTo";
    private static final String RPO_CURRENT = "Current";
    public static final String CURRENT = "current";
    private static final String FORMATTED_ADDRESS = "formattedAddress";
    private static final String RFO_PERSON = "RFO_Person";
    private static final String ID_BY_ATTRIBUTES = "idByAttributes";
    private static final String RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0 = "RFO_PS_ZOZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_IN_1_0";
    private static final String RFO_PODP_EXT_OZNAM_IFO_PODLA_VYHLADAVACICH_KRITERII_BEZ_ZEP_WS_1_0 = "RFO_Podp_Ext_oznam_IFO_Podla_Vyhladavacich_Kriterii_Bez_Zep_WS_1_0";
    public static final String POSKYTNUTIE_ZOZNAMU_IFOPODLA_VYHLADAVACICH_KRITERII_WS_V_1_0_XSD = "http://www.egov.sk/mvsr/RFO/datatypes/Podp/Ext/PoskytnutieZoznamuIFOPodlaVyhladavacichKriteriiWS-v1.0.xsd";

    public static final String ADDRESS_XPATH = "su:subject/su:address";
    public static final String SUBJECT_ID_XPATH = "su:subject/su:subjectID";
    public static final String SUBJECT_XPATH = "su:subject";

    @Autowired
    SubjectReg1DataRepository dataRepo;
    @Autowired
    SubjectReg1IndexRepository indexRepo;
    @Autowired
    SubjectReg1DataReferenceRepository dataReferenceRepo;
    @Autowired
    SubjectReg1DataHistoryRepository dataHistoryRepo;
    @Autowired
    SubjectReg1NaturalIdRepository naturalIdRepository;
    @Autowired
    SubjectReg1RfoIdentificationRepository rfoIdentificationRepository;
    @Autowired
    SubjectReg1RpoIdentificationRepository rpoIdentificationRepository;

    @Autowired
    RpoReg1 rpoReg;

    @Autowired
    AdditionalPluginOps additionalOps;

    @Autowired
    UserInfoService userInfoService;


    @Value("${integration.csru.ovmIsId}")
    private String ovmIsId;

    @Value("${integration.csru.identifikatorSubjektu}")
    private String identifikatorSubjektu;

    @Value("${integration.csru.sleepMillis:#{1000}}")
    private long sleepMillis;

    @Value("${integration.csru.waitMillis}")
    private long waitMillis;

    @Value("${integration.rpo.sz.sourceRegister}")
    private List<String> szSourceRegisters;

    @Value("${registers-file-path}")
    private String registersFilePath;

    private final ModelMapper modelMapper = new ModelMapper();
    private final sk.is.urso.subject.v1.ObjectFactory subjectObjectFactory = new sk.is.urso.subject.v1.ObjectFactory();

    public SubjectReg1(RegisterPlugin info, RegisterPluginConfig plugin) {
        super(info, plugin, SubjectReg1DataEntity.class, SubjectReg1IndexEntity.class, SubjectReg1DataReferenceEntity.class, SubjectReg1DataHistoryEntity.class, SubjectReg1NaturalIdEntity.class);
    }

    @Override
    public SubjectReg1DataRepository getDataRepository() {
        return this.dataRepo;
    }

    @Override
    public SubjectReg1IndexRepository getIndexRepository() {
        return this.indexRepo;
    }

    @Override
    public SubjectReg1DataReferenceRepository getDataReferenceRepository() {
        return this.dataReferenceRepo;
    }

    @Override
    public SubjectReg1DataHistoryRepository getDataHistoryRepository() {
        return this.dataHistoryRepo;
    }

    @Override
    public SubjectReg1NaturalIdRepository getNaturalIdRepository() {
        return this.naturalIdRepository;
    }

    @Override
    public AbstractRegEntityData createNewDataEntityForInsert(Document document) {
        final String xml = XmlUtils.xmlToString(document);
        SubjectT subjectT = XmlUtils.parse(xml, SubjectT.class);

        if (subjectT == null || subjectT.getType() == null) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Xml dokument neobsahuje subject alebo subject type! Vstupné xml:\n" + xml, null);
        }
        String stringSubjectId = subjectT.getSubjectID();

        if (stringSubjectId == null) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Xml dokument neobsahuje subjectId! Vstupné xml:\n" + xml, null);
        }
        SubjectReg1DataEntity entity = new SubjectReg1DataEntity();

        entity.setPovodneId(SearchUtils.sanitizeValue(stringSubjectId));
        entity.setSubjectId(stringSubjectId);
        return entity;
    }
}