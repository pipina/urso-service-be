package sk.is.urso.plugin;

import lombok.Getter;
import lombok.NonNull;
import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.alfa.model.ListRequestModel;
import org.alfa.utils.SearchUtils;
import org.alfa.utils.Utils;
import org.alfa.utils.XmlUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;
import sk.is.urso.model.Udalost;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.plugin.entity.SubjectReg1DataHistoryEntity;
import sk.is.urso.plugin.entity.SubjectReg1DataReferenceEntity;
import sk.is.urso.plugin.entity.SubjectReg1IndexEntity;
import sk.is.urso.plugin.entity.SubjectReg1NaturalIdEntity;
import sk.is.urso.plugin.entity.SubjectReg1RfoIdentificationEntity;
import sk.is.urso.plugin.repository.SubjectReg1DataHistoryRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataReferenceRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataRepository;
import sk.is.urso.plugin.repository.SubjectReg1IndexRepository;
import sk.is.urso.plugin.repository.SubjectReg1NaturalIdRepository;
import sk.is.urso.plugin.repository.SubjectReg1RfoIdentificationRepository;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityDataReference;
import sk.is.urso.reg.AbstractRegEntityIndex;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.RegisterEntryReferenceKey;
import sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou;
import sk.is.urso.reg.model.DvojicaKlucHodnotaVolitelna;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.reg.model.ZaznamRegistra;
import sk.is.urso.reg.model.ZaznamRegistraList;
import sk.is.urso.reg.model.ZaznamRegistraListRequestFilter;
import sk.is.urso.rest.model.UdalostDomenaEnum;
import sk.is.urso.rest.model.UdalostKategoriaEnum;
import sk.is.urso.service.UdalostService;
import sk.is.urso.subject.v1.ForeignPersonT;
import sk.is.urso.subject.v1.FormattedAddressT;
import sk.is.urso.subject.v1.FormattedNameT;
import sk.is.urso.subject.v1.ObjectFactory;
import sk.is.urso.subject.v1.PhysicalPersonT;
import sk.is.urso.subject.v1.PostalAddressT;
import sk.is.urso.subject.v1.REGCodelistItemT;
import sk.is.urso.subject.v1.SimplifiedAddressT;
import sk.is.urso.subject.v1.SubjectT;
import sk.is.urso.subject.v1.SubjectTypeT;

import javax.persistence.criteria.Predicate;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Moja testovacia implementácia registrov
 */
@Getter
public class SubjectReg1 extends AbstractRegPlugin implements IException {
    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    public static final String REGISTER_ID = "SUBJECT";
    public static final RegisterId INTERNAL_REGISTER_ID = new RegisterId(REGISTER_ID, 1);
    public static final String ENUMERATION_COUNTRY_SLOVAKIA = "703";

    public static final String ENTITY_FIELD_SUBJECT_ID = "subjektId";
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
    private static final String IDENTIFIER_TYPE_DIC = "8";
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
    public static final String EFFECTIVE_TO = "effectiveTo";
    public static final String CURRENT = "current";


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
    RfoReg1 rfoReg;
    @Autowired
    RpoReg1 rpoReg;

    @Autowired
    UdalostService udalostService;

    @Value("${integration.csru.sleepMillis:#{1000}}")
    private long sleepMillis;

    private final ModelMapper modelMapper = new ModelMapper();
    private final sk.is.urso.csru.fo.zoznamIfo.ObjectFactory objectFactory = new sk.is.urso.csru.fo.zoznamIfo.ObjectFactory();

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

        String stringSubjectType = subjectT.getType().name();
        SubjectReg1DataEntity entity = new SubjectReg1DataEntity();

        if (stringSubjectId.equals(AbstractRegPlugin.ID_NULL)) {
            stringSubjectId = generateSubjectId(stringSubjectType);
        } else {
            if (subjectExists(stringSubjectId)) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "Subjekt " + stringSubjectId + " už existuje", null);
            }
        }
        entity.setPovodneId(SearchUtils.sanitizeValue(stringSubjectId));
        entity.setSubjektId(stringSubjectId);
        return entity;
    }

    @Override
    public <T extends AbstractRegEntityData> Document prepareXmlForInsert(Document inputXml, T entity) {

        long generatedId = getNextSequence();
        entity.setId(generatedId);

        ObjectFactory factory = new ObjectFactory();
        SubjectT subjectT = XmlUtils.parse(inputXml, SubjectT.class);
        checkSubjectBody(subjectT);

        if (!subjectT.getSubjectID().equals(AbstractRegPlugin.ID_NULL)) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Bolo vyplnené ID", getIdField().getXPathValue(), null);
        }

        subjectT.setSubjectID(((SubjectReg1DataEntity) entity).getSubjektId());

        if (subjectT.getType().equals(SubjectTypeT.FO) || subjectT.getType().equals(SubjectTypeT.SZ) || subjectT.getType().equals(SubjectTypeT.ZO)) {

            subjectT.getFormattedName().clear();

            if (subjectT.getType().equals(SubjectTypeT.FO) || subjectT.getType().equals(SubjectTypeT.SZ)) {
                if (subjectT.getPhysicalPerson() == null) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "Neboli nájdené údaje o fyzickej osobe", null);
                }
                for (PhysicalPersonT physicalPerson : subjectT.getPhysicalPerson()) {
                    subjectT.getFormattedName().add(createFormattedName(physicalPerson, factory));
                }
            } else {
                if (subjectT.getForeignPhysicalPerson() == null) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "Neboli nájdené údaje o zahraničnej osobe", null);
                }
                for (ForeignPersonT foreignPerson : subjectT.getForeignPhysicalPerson()) {
                    subjectT.getFormattedName().add(createFormattedName(foreignPerson, factory));
                }
            }
        }

        for (SimplifiedAddressT address : subjectT.getAddress()) {
            if (address.getPostalAddress() != null) {
                address.setFormattedAddress(createFormattedAddress(address.getPostalAddress()));
            }
        }
        return convertSubjectToDocument(subjectT);
    }

    @Override
    public Document prepareXmlForUpdate(Document currentXml, Document inputXml) throws XPathExpressionException {

        if (inputXml != null) {
            inputXml.normalize();
        }
        if (currentXml != null) {
            currentXml.normalize();
        }

        XPath xpath = createXPath();

        if (currentXml != null) {
            var currentIdNode = (Node) xpath.compile(getIdField().getXPathValue()).evaluate(currentXml, XPathConstants.NODE);
            var inputIdNode = (Node) xpath.compile(getIdField().getXPathValue()).evaluate(inputXml, XPathConstants.NODE);
            if (currentIdNode == null || inputIdNode == null || !currentIdNode.getFirstChild().getNodeValue().equalsIgnoreCase(inputIdNode.getFirstChild().getNodeValue())) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V nových dátach bola zmenená hodnota ID elementu '" + getIdField().getKeyName() + "'", null);
            }
        }

        String subjectType = (String) xpath.compile("su:subject/su:type/text()").evaluate(inputXml, XPathConstants.STRING);

        boolean foType = subjectType.equals(FO);
        boolean szType = subjectType.equals(SZ);
        boolean foSzType = foType || szType;
        boolean zoType = subjectType.equals(ZO);
        boolean poType = subjectType.equals(PO);
        boolean zpType = subjectType.equals(ZP);
        if (foSzType || zoType) {
            NodeList formattedNames = (NodeList) xpath.compile("su:subject/su:formattedName").evaluate(inputXml, XPathConstants.NODESET);
            for (int i = 0; i < formattedNames.getLength(); i++) {
                Node node = formattedNames.item(i);
                node.getParentNode().removeChild(node);
            }
            //TODO co ak je SZ vytvorene z PO casti a FO cast tam nie je?
        }
        if (foType || zoType) {
            if (!xpath.compile("su:subject/su:rpoReference").evaluate(inputXml, XPathConstants.STRING).equals("")) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "Pre subjekt typu FO/ZO nemôže byť vyplnené rpoReference");
            }
        } else if ((poType || zpType) && !xpath.compile("su:subject/su:rfoReference").evaluate(inputXml, XPathConstants.STRING).equals("")) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Pre subjekt typu PO/ZP nemôže byť vyplnené rpoReference");
        }

        Map<String, Map<String, Node>> documentMap1 = new HashMap<>();
        Map<String, Map<String, Node>> documentMap2 = new HashMap<>();
        recursiveMapBuild(currentXml, documentMap1, "");
        recursiveMapBuild(inputXml, documentMap2, "");

        if (foSzType && documentMap2.get(":null:subject:foreignPhysicalPerson") != null) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Pre subjekt typu FO/SZ nemôže byť vyplnený element foreignPhysicalPerson", null);
        }

        if (foSzType || zoType) {
            documentMap1.remove(":null:subject:formattedName");
        }

        Set<String> keys = documentMap1.keySet();
        for (String key : keys) {

            if (!documentMap2.containsKey(key)) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V nových dátach bol zmazaný Node " + key, null);
            }

            Set<String> sequences = documentMap1.get(key).keySet();
            for (String sequence : sequences) {

                boolean updateFormattedNode = compareNodes(key, sequence, documentMap1, documentMap2);

                if (updateFormattedNode && key.contains(POSTAL_ADDRESS)) {

                    Node oldFormattedAddress = (Node) xpath.compile("su:subject/su:address/sa:formattedAddress[@sequence='" + sequence + "']").evaluate(currentXml, XPathConstants.NODE);
                    if (oldFormattedAddress != null) {
                        String xmlPrefix = inputXml.getFirstChild().getPrefix();
                        xmlPrefix = xmlPrefix == null ? null : "sa";

                        Node newFormattedAddress = oldFormattedAddress.cloneNode(true);
                        newFormattedAddress.setPrefix(xmlPrefix);
                        setPrefixForNodes(inputXml, newFormattedAddress);
                        removeEmptyLines(newFormattedAddress);
                        updateNodeAttributes(documentMap2, key, sequence, newFormattedAddress);

                        Node postalAddress = (Node) xpath.compile("su:subject/su:address/sa:postalAddress[@sequence='" + sequence + "']").evaluate(inputXml, XPathConstants.NODE);
                        Node formattedAddress = (Node) xpath.compile("su:subject/su:address/sa:formattedAddress[@sequence='" + sequence + "']").evaluate(inputXml, XPathConstants.NODE);
                        if (formattedAddress == null) {
                            Node addressUsage = postalAddress.getPreviousSibling();
                            addressUsage.getParentNode().insertBefore(newFormattedAddress, addressUsage);
                        } else {
                            formattedAddress.getParentNode().replaceChild(newFormattedAddress, formattedAddress);
                        }
                        documentMap1.get(":null:subject:address:formattedAddress").remove(sequence);
                    } else {
                        SubjectT subject = XmlUtils.parse(inputXml, SubjectT.class);
                        createFormattedAddress(subject, Integer.parseInt(sequence));
                        inputXml = convertSubjectToDocument(subject);
                    }
                }

                if (foSzType && key.contains("physicalPerson") || zoType && key.contains("foreignPhysicalPerson")) {

                    Node formattedName = (Node) xpath.compile("su:subject/su:formattedName[@sequence='" + sequence + "']").evaluate(currentXml, XPathConstants.NODE);
                    if (formattedName != null) {
                        Node updatedFormattedName = formattedName.cloneNode(true);
                        updatedFormattedName.setPrefix(inputXml.getFirstChild().getPrefix());
                        setPrefixForNodes(inputXml, updatedFormattedName);

                        NodeList persons;
                        if (foSzType) {
                            persons = (NodeList) xpath.compile("su:subject/su:physicalPerson").evaluate(inputXml, XPathConstants.NODESET);
                        } else {
                            persons = (NodeList) xpath.compile("su:subject/su:foreignPhysicalPerson").evaluate(inputXml, XPathConstants.NODESET);
                        }
                        if (persons != null && persons.getLength() > 0) {
                            persons.item(0).getParentNode().insertBefore(updatedFormattedName, persons.item(0));
                            Node emptyNode = inputXml.getFirstChild().getChildNodes().item(0).cloneNode(true);
                            if (emptyNode.getNodeType() == Node.TEXT_NODE) {
                                persons.item(0).getParentNode().insertBefore(emptyNode, persons.item(0));
                            }
                        }
                        if (updateFormattedNode) {
                            updateNodeAttributes(documentMap2, key, sequence, updatedFormattedName);
                        }
                    } else {
                        SubjectT subject = XmlUtils.parse(inputXml, SubjectT.class);
                        createFormattedName(subject, Integer.parseInt(sequence));
                        inputXml = convertSubjectToDocument(subject);
                    }
                }
            }

            SubjectT subject = null;
            for (String sequence : documentMap2.get(key).keySet()) {
                if (sequences.contains(sequence)) {
                    continue;
                }
                checkNewNode(key, sequence, documentMap2);
                if (foSzType && key.contains("physicalPerson") || zoType && key.contains("foreignPhysicalPerson")) {
                    if (subject == null) {
                        subject = XmlUtils.parse(inputXml, SubjectT.class);
                    }
                    createFormattedName(subject, Integer.parseInt(sequence));
                }
                if (key.contains(POSTAL_ADDRESS)) {
                    if (subject == null) {
                        subject = XmlUtils.parse(inputXml, SubjectT.class);
                    }
                    createFormattedAddress(subject, Integer.parseInt(sequence));
                }
            }
            if (subject != null) {
                inputXml = convertSubjectToDocument(subject);
            }
        }

        for (String key : documentMap2.keySet()) {
            for (String sequence : documentMap2.get(key).keySet()) {
                checkNewNode(key, sequence, documentMap2);
            }
        }

        if (foSzType || zoType) {
            return removeEmptyLines(inputXml);
        }
        return inputXml;
    }

    private void setPrefixForNodes(Document inputXml, Node node) {
        if (node.getChildNodes() != null) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    children.item(i).setPrefix(node.getPrefix());
                }
            }
        }
        inputXml.adoptNode(node);
    }

    private void updateNodeAttributes(Map<String, Map<String, Node>> documentMap2, String key, String sequence, Node node) {
        String current2 = documentMap2.get(key).get(sequence).getAttributes().getNamedItem(CURRENT).getNodeValue();
        node.getAttributes().getNamedItem(CURRENT).setNodeValue(current2);
        Node effectiveTo2 = documentMap2.get(key).get(sequence).getAttributes().getNamedItem(EFFECTIVE_TO);
        if (effectiveTo2 != null) {
            if (node.getAttributes().getNamedItem(EFFECTIVE_TO) != null) {
                node.getAttributes().getNamedItem(EFFECTIVE_TO).setNodeValue(effectiveTo2.getNodeValue());
            } else {
                ((Element) node).setAttribute(EFFECTIVE_TO, effectiveTo2.getNodeValue());
            }
        }
        Node valid2 = documentMap2.get(key).get(sequence).getAttributes().getNamedItem(VALID);
        if (valid2 != null) {
            if (node.getAttributes().getNamedItem(VALID) != null) {
                node.getAttributes().getNamedItem(VALID).setNodeValue(valid2.getNodeValue());
            } else {
                ((Element) node).setAttribute(VALID, valid2.getNodeValue());
            }
        }
    }

    private Document removeEmptyLines(Document inputXml) {

        NodeList children = inputXml.getFirstChild().getChildNodes();
        int childrenCount = children.getLength();
        for (int i = 0; i < childrenCount; i++) {
            Node child = children.item(i);
            Node prevChild = children.item(i - 1);
            if (child != null && prevChild != null && child.getNodeType() == Node.TEXT_NODE && prevChild.getNodeType() == Node.TEXT_NODE) {
                child.getParentNode().removeChild(child);
            }
        }
        return inputXml;
    }

    private void removeEmptyLines(Node node) {

        NodeList children = node.getChildNodes();
        int childrenCount = children.getLength();
        for (int i = childrenCount - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                child.getParentNode().removeChild(child);
            }
        }
    }

    @Override
    public void throwException(List<Node> nodes, String key, String sequence, String errMessage) {

        if (key.contains(POSTAL_ADDRESS)) {
            Node addressInSyncWithREG = nodes.stream().filter(child -> child.getNodeName().contains("addressInSyncWithREG")).findFirst().orElse(null);
            if (addressInSyncWithREG != null && Boolean.parseBoolean(addressInSyncWithREG.getFirstChild().getTextContent())) {
                throw new CommonException(HttpStatus.CONFLICT, "Nie je možné zmeniť adresu z externého registra", null);
            }
        }
        super.throwException(nodes, key, sequence, errMessage);
    }

    public void createFormattedAddress(SubjectT subject, int position) {
        subject.getAddress().stream().filter(addressT -> addressT.getPostalAddress() != null && addressT.getPostalAddress().getSequence() == position).findFirst().ifPresent(simplifiedAddressT -> simplifiedAddressT.setFormattedAddress(createFormattedAddress(simplifiedAddressT.getPostalAddress())));
    }

    /**
     * Transformuje element poštovej adresy na príslušný element formátovanej adresy
     *
     * @param postalAddress poštová adresa na konverziu
     * @return príslušná formátovaná adresa
     */
    public static FormattedAddressT createFormattedAddress(PostalAddressT postalAddress) {
        StringBuilder address = new StringBuilder();
        final var street = postalAddress.getStreet();
        final var municipality = postalAddress.getMunicipality();
        final var municipalitySet = municipality != null && Utils.isFilled(municipality.getItemValue());
        if (street != null) {
            final var streetNameSet = Utils.isFilled(street.getStreetName());
            final var registrationNumberSet = Utils.isFilled(street.getRegistrationNumber());
            final var streetNumberSet = Utils.isFilled(street.getStreetNumber());
            if (streetNameSet) {
                address.append(street.getStreetName());
            } else {
                if (postalAddress.getPobox() == null && municipalitySet && municipality.getItemValue() != null) {
                    address.append(municipality.getItemValue());
                }
            }
            if (registrationNumberSet) {
                Utils.appendOpt(address, " ");
                address.append(street.getRegistrationNumber());
            }
            if (streetNumberSet) {
                if (registrationNumberSet) { // Napr. Krátka 134/1
                    address.append("/");
                } else if (streetNameSet) { // Napr. Krátka 1
                    address.append(" ");
                } else if (municipalitySet) { // FIXME toto nesedí. Adresa s obcou by mala používať registrationNumber
                    address.append(municipality.getItemValue());
                    address.append(" ");
                }
                address.append(street.getStreetNumber());
            }
        }
        if (postalAddress.getPobox() != null && postalAddress.getPobox().getPoBox() != null && !postalAddress.getPobox().getPoBox().isEmpty()) {
            if (address.length() == 0)
                address.append("P. O. Box ");
            else
                address.append(", P. O. Box ");
            address.append(postalAddress.getPobox().getPoBox());
        }
        if (postalAddress.getZIP() != null) {
            Utils.appendOpt(address, ", ");
            address.append(postalAddress.getZIP());
        }
        if (municipalitySet) {
            if (postalAddress.getZIP() != null) {
                Utils.appendOpt(address, " ");
            } else {
                Utils.appendOpt(address, ", ");
            }
            address.append(municipality.getItemValue());
        }
        if (postalAddress.getState() != null && postalAddress.getState().getItemValue() != null && !postalAddress.getState().getItemValue().isEmpty()) {
            Utils.appendOpt(address, ", ");
            address.append(postalAddress.getState().getItemValue());
        }

        ModelMapper modelMapper = new ModelMapper();
        FormattedAddressT formattedAddress = modelMapper.map(postalAddress, FormattedAddressT.class);
        formattedAddress.setAddress(address.toString());
        return formattedAddress;
    }

    public void createFormattedName(SubjectT subject, int position) {

        ObjectFactory factory = new ObjectFactory();

        if (subject.getType().equals(SubjectTypeT.FO) || subject.getType().equals(SubjectTypeT.SZ) || subject.getType().equals(SubjectTypeT.ZO)) {

            if (subject.getType().equals(SubjectTypeT.FO) || subject.getType().equals(SubjectTypeT.SZ)) {
                PhysicalPersonT physicalPersonT = subject.getPhysicalPerson().stream().filter(p -> p.getSequence() == position).findFirst().orElse(null);
                if (physicalPersonT == null) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "Neboli nájdené údaje o fyzickej osobe", null);
                }
                subject.getFormattedName().add(createFormattedName(physicalPersonT, factory));
            } else {
                ForeignPersonT foreignPersonT = subject.getForeignPhysicalPerson().stream().filter(p -> p.getSequence() == position).findFirst().orElse(null);
                if (foreignPersonT == null) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "Neboli nájdené údaje o zahraničnej osobe", null);
                }
                subject.getFormattedName().add(createFormattedName(foreignPersonT, factory));
            }
        }
    }

    private FormattedNameT createFormattedName(PhysicalPersonT physicalPerson, ObjectFactory objectFactory) {
        FormattedNameT formattedNameT = objectFactory.createFormattedNameT();
        formattedNameT.setName(prepareName(physicalPerson.getName(), physicalPerson.getSurname(), physicalPerson.getTitleBefore(), physicalPerson.getTitleAfter()));
        formattedNameT.setCurrent(physicalPerson.isCurrent());
        formattedNameT.setSequence(physicalPerson.getSequence());
        formattedNameT.setValid(physicalPerson.isValid());
        formattedNameT.setEffectiveFrom(physicalPerson.getEffectiveFrom());
        formattedNameT.setEffectiveTo(physicalPerson.getEffectiveTo());
        return formattedNameT;
    }

    private FormattedNameT createFormattedName(ForeignPersonT foreignPerson, ObjectFactory objectFactory) {
        FormattedNameT formattedNameT = objectFactory.createFormattedNameT();
        formattedNameT.setName(prepareFullName(foreignPerson.getName(), foreignPerson.getSurname(), foreignPerson.getTitleBefore(), foreignPerson.getTitleAfter()));
        formattedNameT.setCurrent(foreignPerson.isCurrent());
        formattedNameT.setSequence(foreignPerson.getSequence());
        formattedNameT.setValid(foreignPerson.isValid());
        formattedNameT.setEffectiveFrom(foreignPerson.getEffectiveFrom());
        formattedNameT.setEffectiveTo(foreignPerson.getEffectiveTo());
        return formattedNameT;
    }

    private String prepareFullName(String name, String surname, List<String> titlesBefore, List<String> titlesAfter) {

        StringBuilder fullName = new StringBuilder();

        if (titlesBefore != null) {
            titlesBefore.forEach(title -> fullName.append(title).append(" "));
        }
        fullName.append(name).append(" ");
        fullName.append(surname);
        if (titlesAfter != null) {
            for (String title : titlesAfter) {
                if (titlesAfter.indexOf(title) == 0) {
                    fullName.append(" ");
                }
                fullName.append(title);
                if (titlesAfter.indexOf(title) < titlesAfter.size() - 1) {
                    fullName.append(" ");
                }
            }
        }
        return fullName.toString();
    }

    private String prepareName(String name, String surname, List<REGCodelistItemT> titlesBefore, List<REGCodelistItemT> titlesAfter) {
        StringBuilder fullName = new StringBuilder();
        if (titlesBefore != null) {
            titlesBefore.forEach(title -> fullName.append(title.getItemValue()).append(" "));
        }
        fullName.append(name).append(" ");
        fullName.append(surname);
        if (titlesAfter != null) {
            for (REGCodelistItemT title : titlesAfter) {
                if (titlesAfter.indexOf(title) == 0) {
                    fullName.append(" ");
                }
                fullName.append(title.getItemValue());
                if (titlesAfter.indexOf(title) < titlesAfter.size() - 1) {
                    fullName.append(" ");
                }
            }
        }
        return fullName.toString();
    }

    public SubjectReg1DataEntity findByEntryId(Long entryId) {
        Specification<SubjectReg1DataEntity> subjectReg1DataEntity = (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("id"), entryId));
            return builder.and(predicates.toArray(new Predicate[0]));
        };

        List<SubjectReg1DataEntity> results = dataRepo.findAll(subjectReg1DataEntity);
        if (!results.isEmpty()) {
            return results.get(0);
        } else {
            return null;
        }
    }

    private DvojicaKlucHodnotaVolitelna getKeyValuePairOptional(String key, String value, boolean optional) {
        DvojicaKlucHodnotaVolitelna result = new DvojicaKlucHodnotaVolitelna();
        result.setKluc(key);
        result.setHodnota(value);
        if (optional) result.volitelna(true);
        return result;
    }

    private DvojicaKlucHodnotaSHistoriou getKeyValuePairWithHistory(DvojicaKlucHodnotaSHistoriou dvojicaKlucHodnotaSHistoriou, String newKey, String newValue) {
        DvojicaKlucHodnotaSHistoriou result = modelMapper.map(dvojicaKlucHodnotaSHistoriou, DvojicaKlucHodnotaSHistoriou.class);//TODO toto je cele zle! Tento modelMapper vlastne nic nerobi lebo pouzije uz existujuci objekt!!!
        result.setKluc(newKey);
        result.setHodnota(newValue);
        return result;
    }

    private DvojicaKlucHodnotaSHistoriou convertToSubjectField(DvojicaKlucHodnotaSHistoriou subjectFieldKey) {
        String key = subjectFieldKey.getKluc();
        String value = subjectFieldKey.getHodnota();
        return switch (key) {
            case SOURCE_REGISTER_TYPE ->
                    getKeyValuePairWithHistory(subjectFieldKey, INDEXED_FIELD_KEY_SUBJ_TYPE, value);
            case CORPORATE_BODY_FULL_NAME -> getKeyValuePairWithHistory(subjectFieldKey, SUBJ_FORMATTED_NAME, value);
            case IDENTIFIER_TYPE_CODE -> getKeyValuePairWithHistory(subjectFieldKey, IDENTIFIER_TYPE_CODE, value);
            case IDENTIFIER_VALUE -> getKeyValuePairWithHistory(subjectFieldKey, IDENTIFIER_VALUE, value);
            case ADDRESS_TYPE -> getKeyValuePairWithHistory(subjectFieldKey, ADR_ADDRESS_USAGE, value);
            case LEGAL_FORM -> getKeyValuePairWithHistory(subjectFieldKey, PO_LEGAL_FORM, value);
            case CORPORATE_BODY_ALTERNATIVE_NAME ->
                    getKeyValuePairWithHistory(subjectFieldKey, SU_ALTERNATIVE_NAME, value);
            case ADDRESS_LINE -> getKeyValuePairWithHistory(subjectFieldKey, ADR_FORMATTED_ADDRESS, value);
            case STREET_NAME -> getKeyValuePairWithHistory(subjectFieldKey, ADR_STREET, value);
            case MUNICIPALITY -> getKeyValuePairWithHistory(subjectFieldKey, ADR_MUNICIPALITY, value);
            case POSTAL_CODE -> getKeyValuePairWithHistory(subjectFieldKey, ADR_ZIP, value);
            case COUNTRY -> getKeyValuePairWithHistory(subjectFieldKey, ADR_STATE, value);
            case PROPERTY_REGISTRATION_NUMBER ->
                    getKeyValuePairWithHistory(subjectFieldKey, ADR_REGISTRATION_NUMBER, value);
            case COUNTY -> getKeyValuePairWithHistory(subjectFieldKey, ADR_REGION, value);
            case INDEXED_FIELD_KEY_RPO_ENTRY_ID -> subjectFieldKey;
            case INDEXED_FIELD_KEY_RPO_EXTERNAL_ID -> subjectFieldKey;
            default -> subjectFieldKey;
        };
        //getKeyValuePairWithHistory(subjectFieldKey, null, null);
    }

    private void toRpoFields(DvojicaKlucHodnotaVolitelna subjectFieldKey, List<DvojicaKlucHodnotaVolitelna> fields) {
        String key = subjectFieldKey.getKluc();
        String value = subjectFieldKey.getHodnota();
        switch (key) {
            case INDEXED_FIELD_KEY_SUBJ_TYPE -> fields.add(getKeyValuePairOptional(SOURCE_REGISTER_TYPE, value, false));
            case SUBJ_FORMATTED_NAME -> fields.add(getKeyValuePairOptional(CORPORATE_BODY_FULL_NAME, value, false));
            case IDENTIFIER_TYPE_CODE -> fields.add(getKeyValuePairOptional(IDENTIFIER_TYPE_CODE, value, false));
            case IDENTIFIER_VALUE -> fields.add(getKeyValuePairOptional(IDENTIFIER_VALUE, value, false));
            case ADR_ADDRESS_USAGE -> fields.add(getKeyValuePairOptional(ADDRESS_TYPE, value, false));
            case PO_LEGAL_FORM -> fields.add(getKeyValuePairOptional(LEGAL_FORM, value, false));
            case SU_ALTERNATIVE_NAME ->
                    fields.add(getKeyValuePairOptional(CORPORATE_BODY_ALTERNATIVE_NAME, value, false));
            case ADR_FORMATTED_ADDRESS -> {
                fields.add(getKeyValuePairOptional(ADDRESS_LINE, value, true));
                if (fields.stream().filter(field -> !field.getVolitelna() && field.getKluc().equals(STREET_NAME)).findAny().isEmpty()) {
                    fields.add(getKeyValuePairOptional(STREET_NAME, value, true));
                }
            }
            case ADR_MUNICIPALITY -> fields.add(getKeyValuePairOptional(MUNICIPALITY, value, false));
            case ADR_ZIP -> fields.add(getKeyValuePairOptional(POSTAL_CODE, value, false));
            case ADR_STATE -> fields.add(getKeyValuePairOptional(COUNTRY, value, false));
            case ADR_STREET -> fields.add(getKeyValuePairOptional(STREET_NAME, value, false));
            case ADR_REGISTRATION_NUMBER ->
                    fields.add(getKeyValuePairOptional(PROPERTY_REGISTRATION_NUMBER, value, false));
            case ADR_REGION -> fields.add(getKeyValuePairOptional(COUNTY, value, false));
        }
    }

    private ZaznamRegistraListRequestFilter subjectFilterToRPOfilter(ZaznamRegistraListRequestFilter subjectFilter) {
        ZaznamRegistraListRequestFilter rpoFilter = new ZaznamRegistraListRequestFilter();
        List<DvojicaKlucHodnotaVolitelna> fields = new ArrayList<>();

        modelMapper.map(subjectFilter, rpoFilter);

        for (DvojicaKlucHodnotaVolitelna field : subjectFilter.getPolia()) {
            toRpoFields(field, fields);
        }
        rpoFilter.setPolia(fields);
        return rpoFilter;
    }

    private boolean rpoSearch(ZaznamRegistraListRequestFilter filter) {
        if (filter.getReferencujuciModul() != null || filter.getPolia().stream().anyMatch(field -> {
                    String key = field.getKluc();
                    String value = field.getHodnota();
                    return key.equals(INDEXED_FIELD_KEY_SUBJ_TYPE) && (value.equals(FO) || value.equals(ZO));
                }
        )
        )
            return false;
        return (filter.getPolia().stream().anyMatch(field -> {
                    String key = field.getKluc();
                    return key.equals(SUBJ_FORMATTED_NAME) || key.equals(IDENTIFIER_TYPE_CODE) || key.equals(IDENTIFIER_VALUE)
                            || key.equals(ADR_ADDRESS_USAGE) || key.equals(PO_LEGAL_FORM) || key.equals(SU_ALTERNATIVE_NAME)
                            || key.equals(ADR_FORMATTED_ADDRESS) || key.equals(ADR_MUNICIPALITY) || key.equals(ADR_ZIP)
                            || key.equals(ADR_STATE) || key.equals(ADR_STREET) || key.equals(ADR_REGISTRATION_NUMBER)
                            || key.equals(ADR_REGION);
                }
        )
        );
    }

    private Future<ZaznamRegistraList> searchRpoLocal(ZaznamRegistraListRequestFilter filter, ListRequestModel listRequest) {
        if (rpoSearch(filter)) {
            ZaznamRegistraListRequestFilter rpoFilter = subjectFilterToRPOfilter(filter);
            ListRequestModel rpoListRequest = toRpoListRequest(listRequest);
            return CompletableFuture.completedFuture(rpoReg.findRegisterEntries(rpoFilter, rpoListRequest));
        }
        return null;
    }

    private ListRequestModel toRpoListRequest(ListRequestModel subjectListRequest) {
        if (subjectListRequest.getSort() == null) {
            return subjectListRequest;
        }
        ListRequestModel rpoListRequest = new ListRequestModel();
        modelMapper.map(subjectListRequest, rpoListRequest);

        String rpoSort = null;
        switch (subjectListRequest.getSort()) {
            case INDEXED_FIELD_KEY_SUBJ_TYPE:
                rpoSort = SOURCE_REGISTER_TYPE;
                break;
            case SUBJ_FORMATTED_NAME:
                rpoSort = CORPORATE_BODY_FULL_NAME;
                break;
            case IDENTIFIER_TYPE_CODE:
                rpoSort = IDENTIFIER_TYPE_CODE;
                break;
            case IDENTIFIER_VALUE:
                rpoSort = IDENTIFIER_VALUE;
                break;
            case ADR_ADDRESS_USAGE:
                rpoSort = ADDRESS_TYPE;
                break;
            case PO_LEGAL_FORM:
                rpoSort = LEGAL_FORM;
                break;
            case SU_ALTERNATIVE_NAME:
                rpoSort = CORPORATE_BODY_ALTERNATIVE_NAME;
                break;
            case ADR_FORMATTED_ADDRESS:
                String[] formattedAddresSort = {ADDRESS_LINE, STREET_NAME, PROPERTY_REGISTRATION_NUMBER, BUILDING_NUMBER,
                        MUNICIPALITY, COUNTRY, POST_OFFICE_BOX};
                rpoSort = String.join(",", formattedAddresSort);
                break;
            case ADR_MUNICIPALITY:
                rpoSort = MUNICIPALITY;
                break;
            case ADR_ZIP:
                rpoSort = POSTAL_CODE;
                break;
            case ADR_STATE:
                rpoSort = COUNTRY;
                break;
            case ADR_STREET:
                rpoSort = STREET_NAME;
                break;
            case ADR_REGISTRATION_NUMBER:
                rpoSort = PROPERTY_REGISTRATION_NUMBER;
                break;
            case ADR_REGION:
                rpoSort = COUNTY;
                break;
        }
        rpoListRequest.setSort(rpoSort);
        return rpoListRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public ZaznamRegistraList findRegisterEntries(@NonNull ZaznamRegistraListRequestFilter filter, @NonNull ListRequestModel listRequest) {

        Future<ZaznamRegistraList> futureFo = null;

        if (filter.getPolia().stream().anyMatch(field1 -> field1.getKluc().equals(IDENTIFIER_VALUE) && filter.getPolia().stream().noneMatch(field2 -> field2.getKluc().equals(IDENTIFIER_TYPE_CODE)))) {
            throw new CommonException(HttpStatus.BAD_REQUEST, NO_IDENTIFIER_TYPE_ERROR, IDENTIFIER_TYPE_CODE, null);
        }

        if (filter.getReferencujuciModul() == null
                && filter.getPolia().stream().anyMatch(field -> field.getKluc().equals(SU_BIRTH_DATE)
                || (field.getKluc().equals(IDENTIFIER_TYPE_CODE) && filter.getPolia().size() > 1 && !field.getHodnota().equals(IDENTIFIER_TYPE_ICO) && !field.getHodnota().equals(IDENTIFIER_TYPE_DIC) && !(field.getHodnota().equals(IDENTIFIER_TYPE_ID_CARD_NUMBER) && filter.getPolia().size() == 2 && filter.getPolia().stream().anyMatch(field2 -> field2.getKluc().equals(IDENTIFIER_VALUE))))
                || field.getKluc().equals(SU_SURNAME) || field.getKluc().equals(SU_NAME))
                && (filter.getPolia().stream().noneMatch(field -> field.getKluc().equals(INDEXED_FIELD_KEY_SUBJ_TYPE))
                || (filter.getPolia().stream().anyMatch(field -> field.getKluc().equals(INDEXED_FIELD_KEY_SUBJ_TYPE) && (field.getHodnota().equals(FO) || field.getHodnota().equals(ZO) || field.getHodnota().equals(SZ)))))
                && filter.getPolia().stream().noneMatch(field -> field.getKluc().equals(INDEXED_FIELD_KEY_SUBJECT_ID))) {

            //futureFo = searchRfoWs(filter);
        }
        Future<ZaznamRegistraList> futurePo = searchRpoLocal(filter, listRequest);

        ZaznamRegistraList registerEntries = super.findRegisterEntries(filter, listRequest);

        if (registerEntries.getResult().size() == listRequest.getLimit()) {
            if (futureFo != null) futureFo.cancel(true);
            if (futurePo != null) futurePo.cancel(true);
            return registerEntries;
        }
        ZaznamRegistraList futureFoEntries = new ZaznamRegistraList();
        futureFoEntries.setTotal(0L);
        ZaznamRegistraList futurePoEntries = new ZaznamRegistraList();
        futurePoEntries.setTotal(0L);

        if (futureFo != null) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                futureFoEntries = futureFo.get(100, TimeUnit.MILLISECONDS);
                checkIfRfoOrRpoIsInSubjects(futureFoEntries, RFO);
            } catch (Exception e) {
                log.warn(RFO_READ_ERROR, e);
                return registerEntries; // ok, register moze byt nedostupny, teda to ignorujeme
            }
        }

        if (futurePo != null) {
            try {
                futurePoEntries = futurePo.get(100, TimeUnit.MILLISECONDS);

                for (ZaznamRegistra futurePoEntry : futurePoEntries.getResult()) {
                    futurePoEntry.getPolia().add(new DvojicaKlucHodnotaSHistoriou().kluc(INDEXED_FIELD_KEY_RPO_ENTRY_ID).hodnota(futurePoEntry.getZaznamId().toString()));

                    String value = null;
                    for (DvojicaKlucHodnotaSHistoriou pair : futurePoEntry.getPolia()) {
                        if (pair.getKluc().equals(IDENTIFIER_VALUE)) {
                            value = pair.getHodnota();
                        }
                    }
                    if (value != null)
                        futurePoEntry.getPolia().add(new DvojicaKlucHodnotaSHistoriou().kluc(INDEXED_FIELD_KEY_RPO_EXTERNAL_ID).hodnota(value));
                }

                checkIfRfoOrRpoIsInSubjects(futurePoEntries, RPO);

                for (ZaznamRegistra futurePoEntry : futurePoEntries.getResult()) {
                    List<DvojicaKlucHodnotaSHistoriou> fieldsToRemove = new ArrayList<>();
                    for (DvojicaKlucHodnotaSHistoriou pair : futurePoEntry.getPolia()) {
                        if (pair.getKluc() == null)
                            fieldsToRemove.add(pair);
                    }
                    futurePoEntry.getPolia().removeAll(fieldsToRemove);
                }
            } catch (Exception e) {
                log.warn(RPO_READ_ERROR, e);
                return registerEntries; // ok, register moze byt nedostupny, teda to ignorujeme
            }
        }

        ZaznamRegistraList futureEntries = new ZaznamRegistraList();
        futureEntries.setResult(Stream.concat(futureFoEntries.getResult().stream(), futurePoEntries.getResult().stream()).collect(Collectors.toList()));
        futureEntries.setTotal(futureFoEntries.getTotal() + futurePoEntries.getTotal());

        if (futureFo != null || futurePo != null) {
            int requiredCount = listRequest.getLimit() - registerEntries.getResult().size();
            if (futureEntries.getResult().size() > requiredCount) {
                registerEntries.getResult().addAll(futureEntries.getResult().subList(0, requiredCount));
            } else {
                registerEntries.getResult().addAll(futureEntries.getResult());
            }
            registerEntries.total(registerEntries.getTotal() + futureEntries.getTotal());
        }
        return registerEntries;
    }

    private void checkIfRfoOrRpoIsInSubjects(ZaznamRegistraList zaznamRegistraList, String type) {
        String externalId;
        if (type.equals(RFO)) {
            externalId = INDEXED_FIELD_KEY_RFO_EXTERNAL_ID;
        } else if (type.equals(RPO)) {
            externalId = INDEXED_FIELD_KEY_RPO_EXTERNAL_ID;
        } else {
            return;
        }
        for (ZaznamRegistra zaznamRegistra : zaznamRegistraList.getResult()) {
            zaznamRegistra.setRegisterId(REGISTER_ID);
            for (DvojicaKlucHodnotaSHistoriou field : zaznamRegistra.getPolia()) {
                convertToSubjectField(field);
                if (field.getKluc() != null && field.getKluc().equals(IDENTIFIER_VALUE)) {
                    var externalIdIndexOpt = indexRepo.findByKlucAndHodnotaAndAktualny(externalId, field.getHodnota(), true);
                    if (externalIdIndexOpt.isPresent()) {
                        var externalIdIndex = externalIdIndexOpt.get();

                        Optional<SubjectReg1IndexEntity> optionalSubjectIdIndex = indexRepo.findByZaznamIdAndKlucAndAktualny(externalIdIndex.getZaznamId(), INDEXED_FIELD_KEY_SUBJECT_ID, true);
                        SubjectReg1IndexEntity subjectIdIndex = null;
                        if (optionalSubjectIdIndex.isPresent()) {
                            subjectIdIndex = optionalSubjectIdIndex.get();
                        }

                        Optional<SubjectReg1IndexEntity> optionalSubjectTypeIndex = indexRepo.findByZaznamIdAndKlucAndAktualny(externalIdIndex.getZaznamId(), INDEXED_FIELD_KEY_SUBJ_TYPE, true);
                        SubjectReg1IndexEntity subjectTypeIndex = null;
                        if (optionalSubjectTypeIndex.isPresent()) {
                            subjectTypeIndex = optionalSubjectTypeIndex.get();
                        }

                        zaznamRegistra.setZaznamId(externalIdIndex.getZaznamId());

                        DvojicaKlucHodnotaSHistoriou subjectIdField = new DvojicaKlucHodnotaSHistoriou();
                        subjectIdField.setKluc(INDEXED_FIELD_KEY_SUBJECT_ID);
                        assert subjectIdIndex != null;
                        subjectIdField.setHodnota(subjectIdIndex.getHodnota());
                        zaznamRegistra.getPolia().add(subjectIdField);
                        DvojicaKlucHodnotaSHistoriou subjectTypeField = new DvojicaKlucHodnotaSHistoriou();
                        subjectTypeField.setKluc(INDEXED_FIELD_KEY_SUBJ_TYPE);
                        assert subjectTypeIndex != null;
                        subjectTypeField.setHodnota(subjectTypeIndex.getHodnota());
                        zaznamRegistra.getPolia().add(subjectTypeField);
                        break;
                    } else {
                        zaznamRegistra.setZaznamId(null);
                    }
                }
            }
        }
    }

    public SubjectReg1DataEntity findBySubjectId(String subjectId) {
        Specification<SubjectReg1DataEntity> subjectReg1DataEntity = (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get(ENTITY_FIELD_SUBJECT_ID), subjectId));
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Optional<SubjectReg1DataEntity> results = dataRepo.findOne(subjectReg1DataEntity);
        return results.orElse(null);
    }

    private boolean subjectExists(String subjectId) {
        Specification<SubjectReg1DataEntity> subjectReg1DataEntity = (root, query, builder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get(ENTITY_FIELD_SUBJECT_ID), subjectId));
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return !dataRepo.findAll(subjectReg1DataEntity).isEmpty();
    }

    public String generateSubjectId(String type) throws CommonException {
        long id;
        switch (type) {
            case "PO":
                id = dataRepo.getSubjectPoIdSequence().longValue();
                break;
            case "FO":
                id = dataRepo.getSubjectFoIdSequence().longValue();
                break;
            case "ZP":
                id = dataRepo.getSubjectZpIdSequence().longValue();
                break;
            case "SZ":
                id = dataRepo.getSubjectSzIdSequence().longValue();
                break;
            case "ZO":
                id = dataRepo.getSubjectZoIdSequence().longValue();
                break;
            default:
                throw new CommonException(HttpStatus.BAD_REQUEST, "Chybný typ subjektu", null);
        }

        String stringId = String.valueOf(id);
        return type + "0".repeat(Math.max(0, 10 - stringId.length())) + stringId;
    }

    private void checkSubjectBody(SubjectT subject) {
        if (subject.getType().equals(SubjectTypeT.FO) || subject.getType().equals(SubjectTypeT.SZ)) {
            if (subject.getPhysicalPerson() == null || subject.getPhysicalPerson().isEmpty()) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach chýbajú údaje o fyzickej osobe", "subject.physicalPerson", null);
            }
            if (subject.getType().equals(SubjectTypeT.FO) && (subject.getCorporateBody() != null && !subject.getCorporateBody().isEmpty()) || (subject.getForeignCorporateBody() != null && !subject.getForeignCorporateBody().isEmpty())
                    || (subject.getForeignPhysicalPerson() != null && !subject.getForeignPhysicalPerson().isEmpty())) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach sa nachádzajú údaje o zahraničnej fyzickej osobe alebo právnickej osobe", null);
            }
        } else if (subject.getType().equals(SubjectTypeT.PO)) {
            if (subject.getCorporateBody() == null || subject.getCorporateBody().isEmpty()) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach chýbajú údaje o právnickej osobe", "subject.corporateBody", null);
            }
            if ((subject.getPhysicalPerson() != null && !subject.getPhysicalPerson().isEmpty()) || (subject.getForeignCorporateBody() != null && !subject.getForeignCorporateBody().isEmpty())
                    || (subject.getForeignPhysicalPerson() != null && !subject.getForeignPhysicalPerson().isEmpty())) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach sa nachádzajú údaje o zahraničnej právnickej osobe alebo fyzickej osobe", null);
            }
        } else if (subject.getType().equals(SubjectTypeT.ZO)) {
            if (subject.getForeignPhysicalPerson() == null || subject.getForeignPhysicalPerson().isEmpty()) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach chýbajú údaje o zahraničnej fyzickej osobe", "subject.foreignPhysicalPerson", null);
            }
            if ((subject.getPhysicalPerson() != null && !subject.getPhysicalPerson().isEmpty()) || (subject.getForeignCorporateBody() != null && !subject.getForeignCorporateBody().isEmpty())
                    || (subject.getCorporateBody() != null && !subject.getCorporateBody().isEmpty())) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach sa nachádzajú údaje o právnickej osobe alebo nie zahraničnej fyzickej osobe", null);
            }
        } else if (subject.getType().equals(SubjectTypeT.ZP)) {
            if (subject.getForeignCorporateBody() == null || subject.getForeignCorporateBody().isEmpty()) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach chýbajú údaje o zahraničnej právnickej osobe", "subject.foreignCorporateBody", null);
            }
            if ((subject.getPhysicalPerson() != null && !subject.getPhysicalPerson().isEmpty()) || (subject.getForeignPhysicalPerson() != null && !subject.getForeignPhysicalPerson().isEmpty())
                    || (subject.getCorporateBody() != null && !subject.getCorporateBody().isEmpty())) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "V dátach sa nachádzajú údaje o fyzickej osobe alebo nie zahraničnej právnickej osobe", null);
            }
        }
    }

    public Document convertSubjectToDocument(SubjectT subjectT) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JAXBContext context = JAXBContext.newInstance(SubjectT.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(new ObjectFactory().createSubject(subjectT), outputStream);
            return XmlUtils.parse(outputStream.toByteArray());

        } catch (Exception ex) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri úprave vstupného XML", ex);
        }
    }

    @Override
    public <T extends AbstractRegEntityData> void updateDataEntity(T entity) {
        entity.setPovodneId(SearchUtils.sanitizeValue(((SubjectReg1DataEntity) entity).getSubjektId()));
        super.updateDataEntity(entity);
    }

    @Override
    @Transactional
    public void onFirstRefInsert(AbstractRegEntityData data) throws IOException, ParserConfigurationException, SAXException {
        String subjectType = data.getEntityIndexes().stream().filter(index -> index.getKluc().equalsIgnoreCase(INDEXED_FIELD_KEY_SUBJ_TYPE)).findFirst().get().getHodnota();

        if (subjectType.equals(FO) || subjectType.equals(SZ)) {
            Optional<AbstractRegEntityIndex> rfoExternalId = data.getEntityIndexes().stream().filter(index -> index.getKluc().equalsIgnoreCase(INDEXED_FIELD_KEY_RFO_EXTERNAL_ID)).findFirst();
            String rfoId = rfoExternalId.map(AbstractRegEntityIndex::getHodnota).orElse(null);
            SubjectReg1RfoIdentificationEntity dbRfoIdentification = rfoId != null ? rfoIdentificationRepository.findByRfoId(rfoId) : null;
            if (dbRfoIdentification != null) {

                Document dbDocument = XmlUtils.parse(dbRfoIdentification.getZaznamId().getXml());
                Node subjectID = dbDocument.getElementsByTagName(INDEXED_FIELD_KEY_SUBJECT_ID).item(0);

                Document document = XmlUtils.parse(data.getXml());
                Node replacementID = document.createElement("replacementID");
                replacementID.setTextContent(subjectID.getTextContent());
                document.getFirstChild().appendChild(replacementID);
                data.setXml(XmlUtils.xmlToString(document));

                Udalost udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.IDENTIFICATION);

            } else {
                if (!rfoIdentificationRepository.existsByZaznamId(data)) {
                    SubjectReg1RfoIdentificationEntity rfoIdentification = new SubjectReg1RfoIdentificationEntity();
                    rfoIdentification.setZaznamId(data);
                    rfoIdentification.setRfoId(rfoId);
                    rfoIdentification.setIdentifikovany(false);
                    rfoIdentification.setOznaceny(false);
                    rfoIdentification.setZluceny(false);
                    rfoIdentification.setChybny(false);
                    rfoIdentification.setDatumCasVytvorenia(LocalDateTime.now());
                    rfoIdentificationRepository.save(rfoIdentification);

                    SubjectReg1DataEntity dataEntity = (SubjectReg1DataEntity) data;
                    dataEntity.setFoId(rfoId);
                    dataRepo.save(dataEntity);
                }
            }
        }
    }

    @Override
    @Transactional
    public void onLastRefDelete(AbstractRegEntityData data) {

        Optional<AbstractRegEntityIndex> rfoExternalId = data.getEntityIndexes().stream().filter(index -> index.getKluc().equalsIgnoreCase(INDEXED_FIELD_KEY_RFO_EXTERNAL_ID)).findFirst();
        String rfoId = rfoExternalId.map(AbstractRegEntityIndex::getHodnota).orElse(null);
        SubjectReg1RfoIdentificationEntity rfoIdentification = rfoId != null ? rfoIdentificationRepository.findByRfoId(rfoId) : null;
        if (rfoIdentification != null) {
            if (rfoIdentification.getOznaceny()) {
                rfoIdentification.setZaznamId(null);
                rfoIdentificationRepository.save(rfoIdentification);
            } else {
                rfoIdentificationRepository.delete(rfoIdentification);
            }
        }
    }

    @Override
    public AbstractRegEntityDataReference incrementReference(Long entryId, String moduleId, String registerId, AbstractRegEntityData data) {
        SubjectReg1DataReferenceEntity dataReference = this.getDataReferenceRepository().findById(new RegisterEntryReferenceKey(entryId, moduleId)).orElse(null);
        if (dataReference == null) {
            dataReference = new SubjectReg1DataReferenceEntity();
            dataReference.setId(new RegisterEntryReferenceKey(entryId, moduleId));
            dataReference.setZaznamId(data);
            dataReference.setPocetReferencii(0);
            dataReference.setSubjektId(((SubjectReg1DataEntity) data).getSubjektId());
        }

        dataReference.setPocetReferencii(dataReference.getPocetReferencii() + 1);
        this.saveDataReferenceEntity(dataReference);
        log.info("[REG][REFERENCE][INCREMENT] register = {}, entryId = {}, module = {}, count = {}", registerId, entryId, moduleId, dataReference.getPocetReferencii());

        return dataReference;
    }

    @Override
    public void validateData(Document doc) {
        XPath xpath = createXPath();
        try {
            NodeList postalAddresses = (NodeList) xpath.compile(
                            "su:subject/su:address[sa:postalAddress[@current='true' and not(@valid='false')] and sa:addressUsage/rb:itemCode[text() = '5']]")
                    .evaluate(doc, XPathConstants.NODESET);

            if (postalAddresses.getLength() > 1) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Aktívna adresa sídla môže byť len jedna!");
            }
        } catch (XPathExpressionException e) {
            throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri validácii adresy sídla!");
        }
    }
}