package sk.is.urso.service;

import lombok.NonNull;
import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.alfa.model.ListRequestModel;
import org.alfa.model.OrderEnumModel;
import org.alfa.service.UserInfoService;
import org.alfa.utils.DateUtils;
import org.alfa.utils.PagingUtils;
import org.alfa.utils.XmlUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import sk.is.urso.config.RegisterConfig;
import sk.is.urso.config.RegisterFilter;
import sk.is.urso.config.Registers;

import sk.is.urso.data.XsdMetadata;
import sk.is.urso.model.Register;
import sk.is.urso.model.Udalost;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityDataHistory;
import sk.is.urso.reg.AbstractRegEntityIndex;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.RegisterEntryHistoryKey;
import sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou;
import sk.is.urso.reg.model.ReferenciaZaModul;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.repository.RegisterRepository;
import sk.is.urso.repository.ciselniky.HodnotaCiselnikaRepository;
import sk.is.urso.rest.model.RegisterList;
import sk.is.urso.rest.model.RegisterListRequestFilter;
import sk.is.urso.rest.model.RegisterPole;
import sk.is.urso.rest.model.TypHodnotyEnum;
import sk.is.urso.rest.model.XpathDataUpdateType;
import sk.is.urso.rest.model.ZaznamRegistraXPathData;
import sk.is.urso.rest.model.ZaznamRegistraXPathDataUpdate;
import sk.is.urso.rest.model.ZaznamRegistraXPathZmena;
import sk.is.urso.common.regconfig.plugin.v1.FormioFieldType;
import sk.is.urso.common.regconfig.plugin.v1.RegisterEntryField;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.plugin.v1.XsdFile;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.alfa.utils.SearchUtils.likeSanitized;
import static org.alfa.utils.SearchUtils.sanitizeValue;

@Service
public class RegisterService implements IException {
    private static final String CHILD_ELEMENTS_XPATH = "schema/*[@name='%s']//element";

    private static final String ELEMENT_BY_NAME_XPATH = "schema//element[@name='%s']";

    private static final String SEQUENCE_ATTRIBUTE_ELEMENTS_XPATH = "//*[@sequence]";

    private static final String ATTRIBUTE_XPATH = "schema/*[@name='%s']//attribute";
    private static final String EXTENSION_XPATH = "schema/*[@name='%s']//extension";
    private static final String SCHEMA = "schema";
    private static final String CURRENT = "current";
    private static final String EFFECTIVE_FROM = "effectiveFrom";
    private static final String EFFECTIVE_TO = "effectiveTo";
    private static final String VALID = "valid";
    private static final String SEQUENCE = "sequence";
    private static final String ITEM_VALUE = "itemValue";
    private static final String CODELIST_CODE = "codeListCode";
    private static final String BASE = "base";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String MAX_OCCURS = "maxOccurs";
    private static final String MIN_OCCURS = "minOccurs";
    private static final String USE = "use";
    private static final String NUMBER_1 = "1";
    private static final String REQUIRED = "required";
    private static final String BOOLEAN = "boolean";
    private static final String DATE = "date";
    private static final String DATE_TIME = "dateTime";
    private static final String DATA = "data";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String UNBOUNDED = "unbounded";
    private static final String REGID_VERID = "regid_verid";
    private static final String REGISTER_ID = "registerId";
    private static final String VERSION_ID = "versionId";
    private static final String DESCRIPTION = "description";
    private static final String IS_EXTERNAL = "isExternal";
    private static final String IS_VALIDATED = "isValidated";
    private static final String IS_ENABLED = "isEnabled";
    private static final String IS_IDENTIFICATION_USED = "isIdentificationUsed";
    private static final String IS_GDPR_RELEVANT = "isGdprRelevant";
    private static final String HISTORICAL_XPATH = "historicalXpath";
    private static final String CHANGE_XPATH = "changeXpath";
    private static final String CHANGE_VALUE = "changeValue";
    private static final String REG_CODELIST_ITEM_T = "REG_codelistItem_t";
    private static final String REG_CODELIST_ITEM_OPTIONAL_T = "REG_codelistItemOptional_t";
    private static final String RB_ITEM_CODE = "rb:itemCode";
    private static final String RB_ITEM_VALUE = "rb:itemValue";
    private static final String ITEM_CODE = "itemCode";
    private static final String MSG_XML_VALIDATION_ERROR = "Chyba pri validácii XML súboru pre register '%s' (%s)! ";

    private static final String RELATIVE_XPATH_ERROR = "XPath nesmie byť relatívny";
    private static final String MULTIPE_XPATH_RESULTS_ERROR = "XPath '%s' odkazuje na viacero nodov!";
    private static final String MULTIPE_XPATHS_SAME_RESULT_ERROR = "XPath-y sa nesmú odkazovať na rovnaký element";
    private static final String HISTORICAL_XPATH_DOES_NOT_CONTAIN_SEQUENCE_ERROR = "XPath '%s' pre historický element neobsahuje konkrétnu sekvenciu, napr. '[@sequence=1]'!";
    private static final String ELEMENT_EFFECTIVE_TO_ALREADY_SET_ERROR = "Aktualizovaný element %s už má nastavené effectiveTo a nie je možné ho aktualizovať. Aktualizujete aktuálnu verziu dát? Chceli ste použiť isOldValid='false'?";
    private static final String EMPTY_CHANGES_ERROR = "Neboli zadané zmeny!";
    private static final String INVALID_XPATH_ERROR = "'%s' nie je platný xpath!";
    private static final String NO_RESULT_XPATH_ERROR = "Pre xpath '%s' nebol nájdený žiaden záznam!";
    private static final String NO_XPATH_ERROR = "Musí byť zadaný aspoň jeden xpath výraz.";

    private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private RegisterFilter registerFilter;

    @Autowired
    private HodnotaCiselnikaRepository hodnotaCiselnikaRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private Registers registers;

    @Autowired
    private RegisterConfig registerConfig;

    private final ModelMapper modelMapper = new ModelMapper();

    public List<Register> findAll() {
        return registerRepository.findAll();
    }

    public RegisterList findAll(@NonNull RegisterListRequestFilter filter, @NonNull ListRequestModel listRequest, @NonNull Registers registerPlugins) {
        listRequest.setDefault(REGID_VERID, OrderEnumModel.ASC);
        listRequest.check(REGISTER_ID, VERSION_ID, NAME, DESCRIPTION, EFFECTIVE_FROM, EFFECTIVE_TO, IS_EXTERNAL, IS_VALIDATED, IS_ENABLED, IS_IDENTIFICATION_USED, IS_GDPR_RELEVANT, REGID_VERID);
        List<RegisterId> registerList;

        if (filter.getRegisterId() != null && filter.getVerziaId() != null) {
            registerList = registerPlugins.getIdSet().stream()
                    .filter(registerPlugin -> likeSanitized(registerPlugin.getRegisterId(), filter.getRegisterId()) && registerPlugin.getVerziaRegistraId() == filter.getVerziaId())
                    .collect(Collectors.toList());
        } else if (filter.getRegisterId() != null) {
            registerList = registerPlugins.getIdSet().stream().filter(registerPlugin -> likeSanitized(registerPlugin.getRegisterId(), filter.getRegisterId())).collect(Collectors.toList());
        } else if (filter.getVerziaId() != null) {
            registerList = registerPlugins.getIdSet().stream().filter(registerPlugin -> registerPlugin.getVerziaRegistraId() == filter.getVerziaId()).collect(Collectors.toList());
        } else {
            registerList = new ArrayList<>(registerPlugins.getIdSet());
        }

        List<AbstractRegPlugin> plugins = new ArrayList<>();
        for (RegisterId register : registerList) {
            plugins.add(registerPlugins.getPlugin(register));
        }

        if (filter.getNazov() != null && filter.getPovoleny() != null) {
            plugins = plugins.stream().filter(plugin -> likeSanitized(plugin.getInfo().getName(), filter.getNazov()) && filter.getPovoleny().compareTo(plugin.getInfo().isEnabled()) == 0)
                    .collect(Collectors.toList());
        } else if (filter.getNazov() != null) {
            plugins = plugins.stream().filter(plugin -> likeSanitized(plugin.getInfo().getName(), filter.getNazov())).collect(Collectors.toList());
        } else if (filter.getPovoleny() != null) {
            plugins = plugins.stream().filter(plugin -> filter.getPovoleny().compareTo(plugin.getInfo().isEnabled()) == 0).collect(Collectors.toList());
        }

        List<sk.is.urso.rest.model.Register> resultList = new ArrayList<>();
        for (AbstractRegPlugin plugin : plugins) {
            resultList.add(prepareRegister(plugin));
        }

        if (listRequest.getOrder().equals(OrderEnumModel.ASC)) {
            resultList = registerFilter.getAscRegisterSorting().get(listRequest.getSort()).apply(resultList);
        } else {
            resultList = registerFilter.getDescRegisterSorting().get(listRequest.getSort()).apply(resultList);
        }
        return new RegisterList().total((long) resultList.size()).result(PagingUtils.pagingElements(resultList, listRequest.getPage(), listRequest.getLimit()));
    }

    public sk.is.urso.rest.model.Register prepareRegister(AbstractRegPlugin plugin) {

        sk.is.urso.rest.model.Register register = modelMapper.map(plugin.getInfo(), sk.is.urso.rest.model.Register.class);
        if (plugin.getInfo().getPublicRegisterId() != null) {
            register.setRegisterId(plugin.getInfo().getPublicRegisterId());
        }
        register.setVerziaId(plugin.getInfo().getVersion());
        register.setPovoleny(plugin.getInfo().isEnabled());
        register.setPlatnostOd(DateUtils.toLocalDate(plugin.getInfo().getEffectiveFrom()));
        if (plugin.getInfo().getEffectiveTo() != null) {
            register.setPlatnostDo(DateUtils.toLocalDate(plugin.getInfo().getEffectiveTo()));
        }
        register.setExterny(plugin.isExternal());
        register.setOvereny(!plugin.getPluginConfig().getValidations().isEmpty());
        register.setIdentifikovany(plugin.getPluginConfig().getMDMParams() != null);
        register.setGdprRelevantny(plugin.getPluginConfig().getGdprParams().isIsGdprRelevant());
        return register;
    }

    public boolean existsById(RegisterId id) {
        return registerRepository.existsById(id);
    }

    public Optional<Register> findById(RegisterId id) {
        return registerRepository.findById(id);
    }

    public Register save(Register register) {
        return registerRepository.save(register);
    }

    public void exists(String registerId, Integer versionId) {

        RegisterId register = new RegisterId(registerId, versionId);

        if (registerRepository.findById(register).isEmpty())
            throw new CommonException(HttpStatus.NOT_FOUND, "Daný register neexistuje", null);
    }

    public void validateData(String data, String xsdFilePath, AbstractRegPlugin plugin) {
        try {
            var schema = XmlUtils.loadXsdSchema(new File(xsdFilePath));
            StringReader stringReader = new StringReader(data);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(stringReader));
        } catch (SAXParseException ex) {
            //toto je standardna chyba validacie podla XSD
            String localizedMessage = ex.getLocalizedMessage();
            // priklad lokalizovanej spravy : cvc-complex-type.2.4.a: Invalid content was found starting with element '{\"http://www.dominanz.sk/UVZ/Reg/SimplifiedAddress\":postalAddress}'. One of '{\"h
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(MSG_XML_VALIDATION_ERROR, plugin.getInfo().getName(), plugin.getFullRegisterId()) + localizedMessage, ex);
        } catch (SAXException e) {
            // toto neviem kedy nastava ale pravdepodobne to tiez bude zle XML
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(MSG_XML_VALIDATION_ERROR, plugin.getInfo().getName(), plugin.getFullRegisterId()), e);
        } catch (IOException e) {
            // toto predpokladam nema standardne nastat
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, String.format(MSG_XML_VALIDATION_ERROR, plugin.getInfo().getName(), plugin.getFullRegisterId()), e);
        }
    }

    public void validateData(String data, AbstractRegPlugin plugin, Schema schema) {
        try {
            StringReader stringReader = new StringReader(data);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(stringReader));
        } catch (SAXParseException ex) {
            //toto je standardna chyba validacie podla XSD
            String localizedMessage = ex.getLocalizedMessage();
            // priklad lokalizovanej spravy : cvc-complex-type.2.4.a: Invalid content was found starting with element '{\"http://www.dominanz.sk/UVZ/Reg/SimplifiedAddress\":postalAddress}'. One of '{\"h
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(MSG_XML_VALIDATION_ERROR, plugin.getInfo().getName(), plugin.getFullRegisterId()) + localizedMessage, ex);
        } catch (SAXException e) {
            // toto neviem kedy nastava ale pravdepodobne to tiez bude zle XML
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(MSG_XML_VALIDATION_ERROR, plugin.getInfo().getName(), plugin.getFullRegisterId()), e);
        } catch (IOException e) {
            // toto predpokladam nema standardne nastat
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, String.format(MSG_XML_VALIDATION_ERROR, plugin.getInfo().getName(), plugin.getFullRegisterId()), e);
        }
    }

    public AbstractRegEntityIndex createNewIndex(AbstractRegEntityIndex regEntryIndex, Long entryId, String key, String value, String context, DvojicaKlucHodnotaSHistoriou pairWithHistory, AbstractRegEntityData regEntityData) {
        regEntryIndex.setData(regEntityData);
        regEntryIndex.setZaznamId(entryId);
        regEntryIndex.setKluc(key);
        regEntryIndex.setHodnota(value);
        regEntryIndex.setHodnotaZjednodusena(sanitizeValue(value));
        regEntryIndex.setKontext(context);
        //pozor tu mozu byt aj polia ako effectiveFrom null, lebo indexovat sa mozu aj nehistorizovane zaznamy!
        if (pairWithHistory.getUcinnostOd() != null) {
            regEntryIndex.setUcinnostOd(DateUtils.toDate(pairWithHistory.getUcinnostOd()));
        }
        if (pairWithHistory.getAktualna() != null) {
            regEntryIndex.setAktualny(pairWithHistory.getAktualna());
        } else {
            regEntryIndex.setAktualny(true);
        }
        if (pairWithHistory.getSekvencia() != null) {
            regEntryIndex.setSekvencia(pairWithHistory.getSekvencia());
        } else {
            regEntryIndex.setSekvencia(0);
        }
        regEntryIndex.setUcinnostDo(DateUtils.toDate(pairWithHistory.getUcinnostDo()));
        return regEntryIndex;
    }

    @Transactional(readOnly = true)
    public ZaznamRegistraOutputDetail prepareZaznamRegistraOutputDetail(AbstractRegEntityData data, AbstractRegPlugin plugin) {

        ZaznamRegistraOutputDetail zaznamRegistraOutputDetail = new ZaznamRegistraOutputDetail();
        if (plugin.getInfo().getPublicRegisterId() != null) {
            zaznamRegistraOutputDetail.setRegisterId(plugin.getInfo().getPublicRegisterId());
        }
        else {
            zaznamRegistraOutputDetail.setRegisterId(plugin.getInfo().getRegisterId());
        }
        zaznamRegistraOutputDetail.setVerziaRegistraId(plugin.getInfo().getVersion());
        zaznamRegistraOutputDetail.setZaznamId(data.getId());
        zaznamRegistraOutputDetail.setPlatny(!data.isNeplatny());
        zaznamRegistraOutputDetail.setPlatnostOd(DateUtils.toLocalDate(data.getPlatnostOd()));
        zaznamRegistraOutputDetail.setData(data.getXml());
        zaznamRegistraOutputDetail.setUcinnostOd(DateUtils.toLocalDate(data.getUcinnostOd()));
        zaznamRegistraOutputDetail.setUcinnostDo(DateUtils.toLocalDate(data.getUcinnostDo()));
        if (data.getEntityDataReferences() != null) {
            data.getEntityDataReferences().forEach(element -> zaznamRegistraOutputDetail.addReferencieItem(new ReferenciaZaModul().modul(element.getId().getModul()).pocetReferencii(element.getPocetReferencii())));
        }
        zaznamRegistraOutputDetail.polia(prepareRegisterFields(plugin, data.getId()));
        return zaznamRegistraOutputDetail;
    }

    public RegisterPole prepareRegisterField(RegisterEntryField entryField) {

        RegisterPole registerIndex = new RegisterPole();
        registerIndex.setPopis(entryField.getDescription());
        registerIndex.setNazov(entryField.getKeyName());
        registerIndex.setTypHodnoty(TypHodnotyEnum.fromValue(entryField.getValueType()));
        registerIndex.setxPath(entryField.getXPathValue());
        registerIndex.setFunkcia(entryField.isIsFunction());
        registerIndex.setIndex(entryField.isIsIndexed());
        registerIndex.setVystup(entryField.isIsOutputField());
        registerIndex.setNazovZobrazenia(entryField.getDisplayName());

        if (entryField.getKeyName() != null) {
            if (entryField.getEnumeration() != null) {
                String description = ((registerIndex.getPopis() == null || registerIndex.getPopis().length() == 0) ? "" : registerIndex.getPopis() + " ");
                registerIndex.setPopis(description + "Číselník " + entryField.getEnumeration().getCodelistCode() + " - '" + registerIndex.getPopis() + "'");
            } else if (entryField.getRegister() != null) {
                String description = ((registerIndex.getPopis() == null || registerIndex.getPopis().length() == 0) ? "" : registerIndex.getPopis() + " ");
                registerIndex.setPopis(description + "Register " + entryField.getRegister().getRegisterId() + "_" + entryField.getRegister().getVersion() + " - " + RegisterConfig.registerNames.get(new RegisterId(entryField.getRegister())) + ". Odkaz na pole " + entryField.getRegister().getRegisterJoinKey().getTarget() + ".");
            }
        }
        return registerIndex;
    }

    public void checkEnumerationFieldsValues(AbstractRegPlugin plugin, Document document) throws XPathExpressionException {

        for (RegisterEntryField field : plugin.getHistoricalFields()) {
            if (field.getHistorical().getCurrentMax() != null) {

                int currentMax = field.getHistorical().getCurrentMax();
                String parentXPathValue = getParentXPathValue(field.getXPathValue());
                XPathExpression exp = plugin.getXPath().compile(parentXPathValue);
                NodeList nodeLists = (NodeList) exp.evaluate(document, XPathConstants.NODESET);
                for (int i = 0; i < nodeLists.getLength(); i++) {

                    int currentCount = 0;
                    String childXPath = parentXPathValue + "[" + (i + 1) + "]/" + field.getXPathValue().split("/")[field.getXPathValue().split("/").length - 1];
                    XPathExpression childExp = plugin.getXPath().compile(childXPath);
                    NodeList nodeList = (NodeList) childExp.evaluate(document, XPathConstants.NODESET);

                    for (int j = 0; j < nodeList.getLength(); j++) {
                        Element childElement = (Element) nodeList.item(j);
                        String currentString = childElement.getAttribute(CURRENT);
                        String validString = childElement.getAttribute(VALID);
                        boolean current = Boolean.parseBoolean(currentString);
                        boolean valid = Boolean.parseBoolean(validString); // valid default = true
                        if (current && valid && ++currentCount > currentMax) {
                            throw new CommonException(HttpStatus.BAD_REQUEST, "Dáta obsahujú nepovolené množstvo elementov '" + childElement.getLocalName() + "' s atribútom \"current\"", null);
                        }
                    }
                }
            }
        }

        for (RegisterEntryField field : plugin.getEnumerationFields()) {
            try {
                checkEnumerationFieldValue(field, plugin, document);
            } catch (Exception e) {
                throw toException("Check enumeration field value failed for field " + field, e);
            }
        }
    }

    private String getParentXPathValue(String xPathValue) {

        List<String> xPathValues = Arrays.asList(xPathValue.split("/"));
        if (xPathValues.size() > 1) {
            return String.join("/", xPathValues.subList(0, xPathValues.size() - 1));
        }
        return xPathValues.get(0);
    }

    private void checkEnumerationFieldValue(RegisterEntryField field, AbstractRegPlugin plugin, Document document) throws XPathExpressionException {

        String enumerationXpath = field.getXPathValue().substring(0, field.getXPathValue().length() - RB_ITEM_CODE.length() - "/".length());
        for (RegisterEntryField historicalField : plugin.getHistoricalFields()) {
            if (field.getXPathValue().startsWith(historicalField.getXPathValue())) {
                enumerationXpath = historicalField.getXPathValue() + "[@valid='true' or not(@valid)]" + enumerationXpath.substring(historicalField.getXPathValue().length());
                break;
            }
        }
        String itemCodeXpath = field.getXPathValue().substring(field.getXPathValue().length() - RB_ITEM_CODE.length());
        String itemValue = RB_ITEM_VALUE;
        String codelistCode = field.getEnumeration().getCodelistCode();

        setNamespace(plugin.getXPath(), plugin.getPluginConfig());
        XPathExpression expr = plugin.getXPath().compile(enumerationXpath);
        NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        String fieldName = null;
        if (field.getKeyName() != null) {
            fieldName = field.getKeyName();
        } else if (field.getDisplayName() != null) {
            fieldName = field.getDisplayName();
        } else if (field.getDescription() != null) {
            fieldName = field.getDescription();
        }

        for (int i = 0; i < list.getLength(); i++) {
            expr = plugin.getXPath().compile(itemCodeXpath);
            Node itemCodeNode = (Node) expr.evaluate(list.item(i), XPathConstants.NODE);
            if (field.getEnumeration().isIsItemCodeRequired() || itemCodeNode != null) {
                if (itemCodeNode == null) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "Nebol zadaný ItemCode, aj keď je pre pole " + fieldName + " povinný", null);
                }

                Node parent = itemCodeNode.getParentNode();
                String keyValue = itemCodeNode.getTextContent();

                Optional<HodnotaCiselnika> optionalItemCodeName = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(keyValue, codelistCode);
                if (optionalItemCodeName.isEmpty()) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "V číselníku " + codelistCode + " sa nenachádza efektívna hodnota s kódom " + keyValue, null);
                }
                String itemCodeName = optionalItemCodeName.get().getNazovPolozky();
                while (true) {
                    Node sibiling = itemCodeNode.getNextSibling();
                    if (sibiling == null) {
                        break;
                    }
                    parent.removeChild(sibiling);
                }

                Node newNode = document.createElementNS(itemCodeNode.getNamespaceURI(), ITEM_VALUE);

                ((Element) parent).setAttribute(CODELIST_CODE, codelistCode);
                newNode.setTextContent(itemCodeName);
                parent.appendChild(newNode);
            } else {
                expr = plugin.getXPath().compile(itemValue);
                log.info(itemValue);
                Node itemValueNode = (Node) expr.evaluate(list.item(i), XPathConstants.NODE);

                if (itemValueNode == null) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "Pole číselníka " + fieldName + " neobsahuje ItemCode ani ItemValue", null);
                }
            }
        }
    }

    public void checkReferenceRegisterValues(AbstractRegPlugin plugin, Document document) throws XPathExpressionException {

        for (RegisterEntryField field : plugin.getRegisterFields()) {

            String enumerationXpath = field.getXPathValue();
            for (RegisterEntryField historicalField : plugin.getHistoricalFields()) {
                if (field.getXPathValue().startsWith(historicalField.getXPathValue())) {
                    enumerationXpath = historicalField.getXPathValue() + "[@valid='true' or not(@valid)]" + enumerationXpath.substring(historicalField.getXPathValue().length());
                    break;
                }
            }

            setNamespace(plugin.getXPath(), plugin.getPluginConfig());
            XPathExpression expr = plugin.getXPath().compile(enumerationXpath);
            NodeList list = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            AbstractRegPlugin registerPlugin = registers.getPlugins().stream().filter(reg -> reg.getInfo().getPublicRegisterId() != null && reg.getInfo().getRegisterId().equals(field.getRegister().getRegisterId())).findFirst().orElse(null);
            if (registerPlugin == null) {
                registerPlugin = registers.getPlugin(new RegisterId(field.getRegister()));
            }

            AbstractRegEntityIndex index = registerPlugin.createNewIndexEntity();
            String target = field.getRegister().getRegisterJoinKey().getTarget(); // TODO toto je len quick fix pre rfo
            if (target.equals("entryId")) {
                index.setKluc("zaznamId");
            } else {
                index.setKluc(field.getRegister().getRegisterJoinKey().getTarget());
            }

            for (int i = 0; i < list.getLength(); i++) {
                String keyValue = list.item(i).getTextContent();
                index.setHodnotaZjednodusena(sanitizeValue(keyValue));
                if (registerPlugin.findAllIndexEntity(Example.of(index)).isEmpty()) {
                    throw new CommonException(HttpStatus.BAD_REQUEST, "V registri '" + registerPlugin.getInfo().getName() + "' (" + registerPlugin.getFullRegisterId() + ") sa nenachádza záznam s ID '" + keyValue + "'", null);
                }
            }
        }
    }

    private List<sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou> prepareRegisterFields(AbstractRegPlugin registerPlugin, Long entryId) {
        final List<sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou> registerFields = new ArrayList<>();
        for (RegisterEntryField entryField : registerPlugin.getPluginConfig().getField()) {
            if (entryField.isIsOutputField()) {
                if (entryField.isIsIndexed()) {
                    for (AbstractRegEntityIndex entityIndex : registerPlugin.findAllIndexEntity(entryId, entryField.getKeyName())) {
                        registerFields.add(new sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou().kluc(entityIndex.getKluc()).hodnota(entityIndex.getHodnota())
                                                                                                               .platna(true).sekvencia(entityIndex.getSekvencia()).aktualna(entityIndex.getAktualny())
                                                                                                               .nazovZobrazenia(entryField.getDisplayName()).kontext(entityIndex.getKontext())
                                                                                                               .ucinnostDo(DateUtils.toLocalDate(entityIndex.getUcinnostDo()))
                                                                                                               .ucinnostOd(DateUtils.toLocalDate(entityIndex.getUcinnostOd())));
                    }
                } else {
                    throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "All returned fields must be indexed");
                }
            }
        }
        return registerFields;
    }

    private void checkNodeAttributes(Node node, DvojicaKlucHodnotaSHistoriou dvojicaKlucHodnotaSHistoriou) {

        if (node.getAttributes() == null) {
            return;
        }

        Node sequence = node.getAttributes().getNamedItem(SEQUENCE);
        if (sequence != null) {
            dvojicaKlucHodnotaSHistoriou.sekvencia(Integer.valueOf(sequence.getNodeValue()));
        }
        Node current = node.getAttributes().getNamedItem(CURRENT);
        if (current != null) {
            dvojicaKlucHodnotaSHistoriou.aktualna(Boolean.valueOf(current.getNodeValue()));
        }
        Node effectiveFrom = node.getAttributes().getNamedItem(EFFECTIVE_FROM);
        if (effectiveFrom != null) {
            dvojicaKlucHodnotaSHistoriou.ucinnostOd(LocalDate.parse(effectiveFrom.getNodeValue()));
        }
        Node effectiveTo = node.getAttributes().getNamedItem(EFFECTIVE_TO);
        if (effectiveTo != null) {
            dvojicaKlucHodnotaSHistoriou.ucinnostDo(LocalDate.parse(effectiveTo.getNodeValue()));
        }

        if (sequence == null && current == null && node.getParentNode() != null) {
            checkNodeAttributes(node.getParentNode(), dvojicaKlucHodnotaSHistoriou);
        }
    }

    public void updateNewDataEntity(AbstractRegEntityData regEntryData, ZaznamRegistraInputDetail registerEntryDetail, Date today) {
        regEntryData.setPlatnostOd(today);
        regEntryData.setUcinnostOd(DateUtils.toDate(registerEntryDetail.getUcinnostOd()));
        if (registerEntryDetail.getUcinnostDo() != null) {
            regEntryData.setUcinnostDo(DateUtils.toDate(registerEntryDetail.getUcinnostDo()));
        }
        if (registerEntryDetail.getPlatny() != null) {
            regEntryData.setNeplatny(!registerEntryDetail.getPlatny());
        }
        if (registerEntryDetail.getPouzivatel() != null) {
            regEntryData.setPouzivatel(registerEntryDetail.getPouzivatel());
        } else {
            regEntryData.setPouzivatel(userInfoService.getUserInfo().getLogin());
        }
        if (registerEntryDetail.getModul() != null) {
            regEntryData.setModul(registerEntryDetail.getModul());
        }
        regEntryData.setDatumCasPoslednejReferencie(LocalDateTime.now());
    }

    public AbstractRegEntityDataHistory createNewDataHistoryEntity(AbstractRegEntityDataHistory regEntryDataHistory, AbstractRegEntityData regEntryData, Udalost udalost) {
        regEntryData.getEntityDataHistory().add(regEntryDataHistory);
        regEntryDataHistory.setId(new RegisterEntryHistoryKey(regEntryData.getId(), udalost.getId()));
        regEntryDataHistory.setZaznamId(regEntryData);
        regEntryDataHistory.setXml(regEntryData.getXml());
        regEntryDataHistory.setPlatnostOd(regEntryData.getPlatnostOd());
        regEntryDataHistory.setUcinnostOd(regEntryData.getUcinnostOd());
        regEntryDataHistory.setUcinnostDo(regEntryData.getUcinnostDo());
        regEntryDataHistory.setNeplatny(regEntryData.isNeplatny());
        regEntryDataHistory.setDatumCasVytvorenia(LocalDateTime.now());
        regEntryDataHistory.setPouzivatel(regEntryData.getPouzivatel());
        regEntryDataHistory.setModul(regEntryData.getModul());
        return regEntryDataHistory;
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

    public String getActualData(AbstractRegPlugin plugin, AbstractRegEntityData data) throws Exception {

        Document doc = XmlUtils.parse(data.getXml());

        String currentDate = new SimpleDateFormat(YYYY_MM_DD).format(new Date());

        String enumerationXpath;
        for (RegisterEntryField historicalField : plugin.getHistoricalFields()) {

            XPath xpath = XPathFactory.newInstance().newXPath();
            setNamespace(xpath, plugin.getPluginConfig());

            enumerationXpath = historicalField.getXPathValue()
                    + "[@valid='false' "
                    + "or @current='false' "
                    + "or translate(@effectiveFrom,'-','') > translate('" + currentDate + "', '-', '') "
                    + "or (@effectiveTo "
                    + "and translate(@effectiveTo,'-','') < translate('" + currentDate + "', '-', ''))]";

            XPathExpression expr = xpath.compile(enumerationXpath);

            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                Node parent = node.getParentNode();
                parent.removeChild(node);
                removeEmptyLines(parent);
            }
        }
        return XmlUtils.xmlToString(doc);
    }

    public void createRegisterIndexes(AbstractRegPlugin plugin, AbstractRegEntityData data, Document doc) throws Exception {
        data.setEntityIndexes(new ArrayList<>());// for update we also set indexes from start!
        data.getEntityIndexes().add(plugin.createEntryIdIndex(data));
        for (RegisterEntryField field : plugin.getIndexedFields()) {

            Map<String, Integer> arrayNodes = new HashMap<>();

            XPathExpression expr = plugin.getXPath().compile(field.getXPathValue());

            String enumerationXpath = null;
            for (RegisterEntryField historicalField : plugin.getHistoricalFields()) {
                if (field.getXPathValue().startsWith(historicalField.getXPathValue())) {
                    enumerationXpath = "(" + historicalField.getXPathValue() + "[@valid='true' or not(@valid)])[%d]" + field.getXPathValue().substring(historicalField.getXPathValue().length());
                    break;
                }
            }

            if (field.isIsFunction()) {
                String value = (String) expr.evaluate(doc, XPathConstants.STRING);

                if (enumerationXpath != null) {
                    XPathExpression exp = plugin.getXPath().compile(String.format(enumerationXpath, 0));
                    Node node = (Node) exp.evaluate(doc, XPathConstants.NODE);
                    if (node == null) {
                        continue;
                    }
                }

                AbstractRegEntityIndex entityIndex = createNewIndex(plugin.createNewIndexEntity(), data.getId(), field.getKeyName(), value, "", new DvojicaKlucHodnotaSHistoriou(), data);
                //plugin.saveIndexEntity(entityIndex);
                data.getEntityIndexes().add(entityIndex);
            } else {
                NodeList nodeListResult = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < nodeListResult.getLength(); i++) {
                    Node node = nodeListResult.item(i);

                    String context = "";
                    if (isArrayNode(node.getParentNode(), node.getNodeName())) {
                        context = node.getNodeName() + "[" + i + "]";
                    }
                    if (node.getParentNode() != null) {
                        context = createContextPath(node.getParentNode(), context, arrayNodes);
                    }

                    if (enumerationXpath != null) {
                        XPathExpression exp = plugin.getXPath().compile(String.format(enumerationXpath, i + 1));
                        node = (Node) exp.evaluate(doc, XPathConstants.NODE);
                        if (node == null) {
                            break;
                        }
                    }

                    DvojicaKlucHodnotaSHistoriou pairWithHistory = new DvojicaKlucHodnotaSHistoriou();
                    checkNodeAttributes(node, pairWithHistory);
                    AbstractRegEntityIndex entityIndex = createNewIndex(plugin.createNewIndexEntity(), data.getId(), field.getKeyName(), node.getTextContent(), context, pairWithHistory, data);
                    //plugin.saveIndexEntity(entityIndex);
                    data.getEntityIndexes().add(entityIndex);
                }
            }
        }
    }

    private boolean isArrayNode(Node parentNode, String nodeName) {
        int match = 0;
        if (parentNode != null && parentNode.hasChildNodes()) {
            NodeList children = parentNode.getChildNodes();
            for (var i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child.getNodeName().equalsIgnoreCase(nodeName)) {
                    match++;
                }
            }
        }
        return match > 1;
    }

    private String createContextPath(Node node, String context, Map<String, Integer> arrayNodes) {

        Node parentNode = node.getParentNode();
        if (parentNode != null && parentNode.hasChildNodes()) {
            if (isArrayNode(parentNode, node.getNodeName())) {
                if (arrayNodes.containsKey(node.getNodeName())) {
                    int i = arrayNodes.get(node.getNodeName());
                    context = Objects.equals(context, "") ? node.getNodeName() + "[" + ++i + "]" : node.getNodeName() + "[" + ++i + "]/" + context;
                    arrayNodes.put(node.getNodeName(), i);
                }
                else {
                    context = Objects.equals(context, "") ? node.getNodeName() + "[0]" : node.getNodeName() + "[0]/" + context;
                    arrayNodes.put(node.getNodeName(), 0);
                }
            }
            else {
                context = Objects.equals(context, "") ? node.getNodeName() + "[0]" : node.getNodeName() + "[0]/" + context;
            }
            return createContextPath(parentNode, context, arrayNodes);
        }
        return context;
    }

    public File createZipFile(String register, String xsdFilePath, String tmpPath, List<XsdFile> xsdFiles) throws Exception {

        File zipFileDir = null;

        try {

            Path zipDirPath = Paths.get(xsdFilePath + File.separator + register + File.separator + "zip");
            zipFileDir = new File(zipDirPath.toUri());
            if (zipFileDir.exists()) {
                deleteZipDirectory(zipFileDir);
            }
            Files.createDirectories(zipDirPath);

            for (XsdFile xsdFile : xsdFiles) {

                String[] xsdFilePaths = xsdFile.getPath().split("/");
                for (int i = 0; i < xsdFilePaths.length - 1; i++) {

                    StringBuilder path = null;
                    if (i == 0) {
                        path = new StringBuilder(File.separator + xsdFilePaths[i]);
                    } else {
                        for (int j = 0; j <= i; j++) {
                            if (j == 0) {
                                path = new StringBuilder(File.separator + xsdFilePaths[j]);
                            } else {
                                path.append(File.separator).append(xsdFilePaths[j]);
                            }
                        }
                    }

                    Path folderPath = Paths.get(zipDirPath + path.toString());
                    File folderFile = new File(folderPath.toUri());
                    if (!folderFile.exists()) {
                        Files.createDirectories(folderPath);
                    }
                }

                Path sourceRegisterBasePath = Paths.get(xsdFilePath + File.separator + xsdFile.getPath());
                Path destRegisterBasePath = Paths.get(zipDirPath + File.separator + xsdFile.getPath());
                Files.copy(sourceRegisterBasePath, destRegisterBasePath);
            }

            Path zipFilePath = Paths.get(tmpPath + File.separator + register + "_DATA.xsd.zip");
            File zipFile = new File(zipFilePath.toUri());
            if (zipFile.exists()) {
                Files.delete(zipFile.toPath());
            }

            return prepareZipFile(zipFilePath.toString(), zipFileDir);

        } finally {
            if (zipFileDir != null) {
                deleteZipDirectory(zipFileDir);
            }
        }
    }

    private File prepareZipFile(String zipFilePath, File zipFile) throws Exception {

        try (FileOutputStream fos = new FileOutputStream(zipFilePath); ZipOutputStream zos = new ZipOutputStream(fos)) {
            zos.setLevel(9);
            createZip(zipFile, zos, zipFile.getPath().length() + 1);
            return new File(zipFilePath);
        }
    }

    private void deleteZipDirectory(File directory) throws IOException {

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteZipDirectory(file);
            }
        }
        Files.delete(directory.toPath());
    }

    private void createZip(File file, ZipOutputStream zos, int prefixLength) throws Exception {

        for (File fileToZip : file.listFiles()) {

            if (fileToZip.isFile()) {

                ZipEntry zipEntry = new ZipEntry(fileToZip.getPath().substring(prefixLength));
                zos.putNextEntry(zipEntry);
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    for (int c = fis.read(); c != -1; c = fis.read()) {
                        zos.write(c);
                    }
                    zos.flush();
                }
                zos.closeEntry();

            } else if (fileToZip.isDirectory()) {
                createZip(fileToZip, zos, prefixLength);
            }
        }
    }

    public void setNamespace(XPath xpath, RegisterPluginConfig pluginConfig) {
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                XsdFile xsdFile = pluginConfig.getXsdFile().stream().filter(file -> prefix.equals(file.getNamespacePrefix())).findFirst().orElse(null);
                if (xsdFile != null) {
                    return xsdFile.getNamespaceUrl();
                }
                return XMLConstants.DEFAULT_NS_PREFIX;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                throw new UnsupportedOperationException();
            }
        });
    }

    public String createFormioJsonFromXml(String xmlData, AbstractRegPlugin registerPlugin) throws Exception {
        JSONObject dataJson = new JSONObject();
        Document xmlDocument = XmlUtils.parse(xmlData, false);
        Element root = xmlDocument.getDocumentElement();
        XsdMetadata xsdMetadata = registerConfig.prepareXsdMetadata(root, registerPlugin, XPathFactory.newInstance().newXPath());

        JSONObject nodeObject = createJsonObject(root, xmlDocument, registerConfig.getNodeName(root.getNodeName()), 1, xsdMetadata, new HashSet<>(), new JSONObject());
        dataJson.put(DATA, nodeObject);
        return dataJson.toString();
    }

    private JSONObject createJsonObject(Node node, Document xmlDocument, String nodePath, int position, XsdMetadata xsdMetadata, Set<String> checkedNodes, JSONObject resultObject) throws Exception {

        String nodeName = registerConfig.getNodeName(node.getNodeName());

        Boolean isCodelistCode = null;
        if (xsdMetadata.getDuplicateElements().contains(nodeName)) {
            isCodelistCode = checkElementType(nodeName, nodePath, xsdMetadata);
        }

        if (!xsdMetadata.getArrayElements().contains(nodeName)) {

            Node xmlNode = (Node) xsdMetadata.getXPath().compile(nodePath).evaluate(xmlDocument, XPathConstants.NODE);

            if (xsdMetadata.getCodelistItemElements().contains(nodeName) && (isCodelistCode == null || Boolean.TRUE.equals(isCodelistCode))) {
                resultObject.put(nodeName, xsdMetadata.getXPath().compile(nodePath.concat("/").concat(ITEM_CODE)).evaluate(xmlDocument, XPathConstants.STRING));
            } else if (xsdMetadata.getCodelistItemOptionalElements().contains(nodeName) && (isCodelistCode == null || Boolean.TRUE.equals(isCodelistCode))) {
                JSONObject nodeObject = new JSONObject();
                Element itemCode = (Element) xsdMetadata.getXPath().compile(nodePath.concat("/").concat(ITEM_CODE)).evaluate(xmlDocument, XPathConstants.NODE);
                Element itemValue = (Element) xsdMetadata.getXPath().compile(nodePath.concat("/").concat(ITEM_VALUE)).evaluate(xmlDocument, XPathConstants.NODE);
                if (itemCode != null) {
                    nodeObject.put(ITEM_CODE, itemCode.getTextContent());
                } else if (itemValue != null) {
                    nodeObject.put(ITEM_VALUE, itemValue.getTextContent());
                }
                resultObject.put(nodeName, nodeObject);
            } else if (xsdMetadata.getSimpleElements().containsKey(nodeName)) {
                putSimpleNode(node, nodeName, resultObject);
            } else if (xsdMetadata.getComplexElements().containsKey(nodeName)) {

                XsdMetadata xsd = registerConfig.getXsdElements(xsdMetadata.getComplexElements().get(nodeName), null, xsdMetadata);
                JSONObject nodeObject = createJsonObject(xmlNode, xmlDocument, nodePath, 1, xsd, new HashSet<>(), new JSONObject());
                for (String key : nodeObject.keySet()) {
                    checkedNodes.add(key.substring(key.lastIndexOf(".") + 1));
                }
                addNodeAttributes(nodeObject, node);
                resultObject.put(registerConfig.getNodeName(xmlNode.getNodeName()), nodeObject);
            } else if (xsdMetadata.getForeignElements().containsKey(nodeName)) {
                String nodeNamespace = xsdMetadata.getForeignElementsNs().get(nodeName);
                String nodeType = xsdMetadata.getForeignElements().get(nodeName);
                Document typesXsd = registerConfig.getTypesXsdFile(nodeNamespace, xsdMetadata);
                XsdMetadata xsd = registerConfig.getXsdElements(nodeType, nodeNamespace, new XsdMetadata(typesXsd, xsdMetadata.getDuplicateElements(), xsdMetadata.getImportXsd(), xsdMetadata.getXPath(), xsdMetadata.getXmlDocument(), xsdMetadata.getObjectFactory(), xsdMetadata.getPluginConfig(), xsdMetadata.getFormioSchemaTyp()));

                if (xsd.getSimpleObjects().contains(nodeType)) {
                    putSimpleNode(node, nodeName, resultObject);
                } else {
                    JSONObject nodeObject = createJsonObject(node, xmlDocument, nodePath, 1, xsd, new HashSet<>(), new JSONObject());
                    for (String key : nodeObject.keySet()) {
                        checkedNodes.add(key.substring(key.lastIndexOf(".") + 1));
                    }
                    addNodeAttributes(nodeObject, node);
                    resultObject.put(nodeName, nodeObject);
                }
            }
        } else {

            JSONArray ja = new JSONArray();
            NodeList nodeList = (NodeList) xsdMetadata.getXPath().compile(nodePath).evaluate(xmlDocument, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {

                if (xsdMetadata.getCodelistItemElements().contains(nodeName) && (isCodelistCode == null || Boolean.TRUE.equals(isCodelistCode))) {
                    JSONObject nodeObject = new JSONObject();
                    ja.put(nodeObject.put(nodeName, ((Element) xsdMetadata.getXPath().compile(nodePath + "[" + (i + 1) + "]/" + ITEM_CODE).evaluate(xmlDocument, XPathConstants.NODE)).getTextContent()));

                } else if (xsdMetadata.getCodelistItemOptionalElements().contains(nodeName) && (isCodelistCode == null || Boolean.TRUE.equals(isCodelistCode))) {
                    JSONObject nodeObject = new JSONObject();
                    Element itemCode = (Element) xsdMetadata.getXPath().compile(nodePath + "[" + (i + 1) + "]/" + ITEM_CODE).evaluate(xmlDocument, XPathConstants.NODE);
                    Element itemValue = (Element) xsdMetadata.getXPath().compile(nodePath + "[" + (i + 1) + "]/" + ITEM_VALUE).evaluate(xmlDocument, XPathConstants.NODE);
                    if (itemCode != null) {
                        nodeObject.put(ITEM_CODE, itemCode.getTextContent());
                    } else if (itemValue != null) {
                        nodeObject.put(ITEM_VALUE, itemValue.getTextContent());
                    }
                    ja.put(nodeObject);
                } else if (xsdMetadata.getSimpleElements().containsKey(nodeName)) {
                    putSimpleNode(nodeList.item(i), nodeName, ja);
                } else if (xsdMetadata.getComplexElements().containsKey(nodeName)) {
                    XsdMetadata xsd = registerConfig.getXsdElements(xsdMetadata.getComplexElements().get(nodeName), null, xsdMetadata);
                    position = i;
                    JSONObject nodeObject = createJsonObject(nodeList.item(i), xmlDocument, nodePath, ++position, xsd, new HashSet<>(), new JSONObject());
                    for (String key : nodeObject.keySet()) {
                        checkedNodes.add(key.substring(key.lastIndexOf(".") + 1));
                    }
                    addNodeAttributes(nodeObject, nodeList.item(i));
                    JSONObject object = new JSONObject();
                    object.put(nodeName, nodeObject);
                    ja.put(object);
                } else if (xsdMetadata.getForeignElements().containsKey(nodeName)) {
                    String nodeNamespace = xsdMetadata.getForeignElementsNs().get(nodeName);
                    String nodeType = xsdMetadata.getForeignElements().get(nodeName);
                    Document typesXsd = registerConfig.getTypesXsdFile(nodeNamespace, xsdMetadata);
                    XsdMetadata xsd = registerConfig.getXsdElements(nodeType, nodeNamespace, new XsdMetadata(typesXsd, xsdMetadata.getDuplicateElements(), xsdMetadata.getImportXsd(), xsdMetadata.getXPath(), xsdMetadata.getXmlDocument(), xsdMetadata.getObjectFactory(), xsdMetadata.getPluginConfig(), xsdMetadata.getFormioSchemaTyp()));

                    if (xsd.getSimpleObjects().contains(nodeType)) {
                        putSimpleNode(nodeList.item(i), nodeName, ja);
                    }
                    else {
                        position = i;
                        JSONObject nodeObject = createJsonObject(nodeList.item(i), xmlDocument, nodePath, ++position, xsd, new HashSet<>(), new JSONObject());
                        for (String key : nodeObject.keySet()) {
                            checkedNodes.add(key.substring(key.lastIndexOf(".") + 1));
                        }
                        addNodeAttributes(nodeObject, node);
                        JSONObject object = new JSONObject();
                        object.put(nodeName, nodeObject);
                        ja.put(object);
                    }
                }
                resultObject.put(nodeName, ja);
            }
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            String currentNodeName = registerConfig.getNodeName(currentNode.getNodeName());
            if (currentNode.getNodeType() == Node.ELEMENT_NODE && !checkedNodes.contains(currentNodeName)) {
                checkedNodes.add(currentNodeName);
                String nodePositionPath = nodePath + "[" + position + "]/" + currentNodeName;
                resultObject = createJsonObject(currentNode, xmlDocument, nodePositionPath, position, xsdMetadata, checkedNodes, resultObject);
            }
        }
        return resultObject;
    }

    private boolean checkElementType(String nodeName, String nodePath, XsdMetadata xsdMetadata) throws XPathExpressionException {

        String parentName = getParentName(nodePath);

        for (Map.Entry<String, Document> xsdDocument : xsdMetadata.getImportXsd().entrySet()) {
            Element parentElement = (Element) xsdMetadata.getXPath().compile("//*[@name='"+ parentName +"']").evaluate(xsdDocument.getValue(), XPathConstants.NODE);
            if (parentElement != null) {
                String parentType = registerConfig.getNodeName(parentElement.getAttribute("type"));
                if (!Objects.equals(parentType, "")) {
                    Node xsdNode = (Node) xsdMetadata.getXPath().compile("//*[@name='" + parentType + "']//*[@name='" + nodeName + "']").evaluate(xsdDocument.getValue(), XPathConstants.NODE);
                    if (xsdNode != null) {
                        String nodeType = ((Element) xsdNode).getAttribute("type");
                        if (nodeType.contains(REG_CODELIST_ITEM_T) || nodeType.contains(REG_CODELIST_ITEM_OPTIONAL_T)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private String getParentName(String nodePath) {
        List<String> paths = List.of(nodePath.split("/"));
        if (paths.size() == 1) {
            return null;
        }
        String parent = paths.get(paths.size() - 2);
        return parent.split("\\[")[0];
    }

    private void addNodeAttributes(JSONObject object, Node node) {
        if (node.hasAttributes()) {
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                Node attribute = node.getAttributes().item(i);
                if (attribute.getNodeValue().equalsIgnoreCase(TRUE) || attribute.getNodeValue().equalsIgnoreCase(FALSE)) {
                    object.put(registerConfig.getNodeName(attribute.getNodeName()), Boolean.valueOf(attribute.getNodeValue()));
                } else {
                    object.put(registerConfig.getNodeName(attribute.getNodeName()), attribute.getNodeValue());
                }
            }
        }
    }

    private void putSimpleNode(Node node, String parentNamePath, JSONArray jsonArray) {

        JSONObject jo = new JSONObject();
        putSimpleNode(node, parentNamePath, jo);
        if (!jo.isEmpty()) {
            jsonArray.put(jo);
        }
    }

    private void putSimpleNode(Node node, String parentNamePath, JSONObject resultObject) {
        if (node.getFirstChild() != null && node.getFirstChild().getNodeValue() != null) {
            if (node.getFirstChild().getNodeValue().equalsIgnoreCase(TRUE) || node.getFirstChild().getNodeValue().equalsIgnoreCase(FALSE)) {
                resultObject.put(parentNamePath, Boolean.valueOf(node.getFirstChild().getNodeValue()));
            } else {
                resultObject.put(parentNamePath, node.getFirstChild().getNodeValue());
            }
        }
    }

    public String createXmlFromXsdAndJson(String jsonData, AbstractRegPlugin registerPlugin) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        Document xmlDocument = XmlUtils.newDocument();
        JSONObject rootJsonObject = new JSONObject(jsonData).getJSONObject(DATA);

        XsdMetadata xsdMetadata = registerConfig.prepareXsdMetadata(null, registerPlugin, xPath);
        xsdMetadata.setXmlDocument(xmlDocument);

        Element rootElement = (Element) processComplexElement(xsdMetadata.getName(), xsdMetadata.getElementType(), xsdMetadata.getNamespace(), rootJsonObject, xsdMetadata);
        Element schemaElement = (Element) xPath.compile(SCHEMA).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);
        if (schemaElement.hasAttributes()) {
            for (int i = 0; i < schemaElement.getAttributes().getLength(); i++) {
                Node attribute = schemaElement.getAttributes().item(i);
                if (attribute.getNodeName().contains("xmlns:")) {
                    rootElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
                }
            }
        }
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlDocument.appendChild(rootElement);
        return XmlUtils.xmlToString(xmlDocument);
    }

    private Node processComplexElement(String name, String type, String parentNamespace, JSONObject jsonObject, XsdMetadata xsdMetadata) throws Exception {

        try {
            Element element = xsdMetadata.getXmlDocument().createElement(parentNamespace.concat(":").concat(name));
            Element extensionElement = (Element) xsdMetadata.getXPath().compile(String.format(EXTENSION_XPATH, type)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);
            if (extensionElement != null) {
                String extensionBase = extensionElement.getAttribute(BASE);
                String extensionNs = extensionBase.split(":")[0];
                String extensionType = extensionBase.split(":")[1];
                element = processExtensionElement(element, name, extensionNs, extensionType, jsonObject, xsdMetadata);
            }

            NodeList childAttributes = (NodeList) xsdMetadata.getXPath().compile(String.format(ATTRIBUTE_XPATH, type)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODESET);
            processElementAttributes(jsonObject, element, childAttributes);

            NodeList childElements = (NodeList) xsdMetadata.getXPath().compile(String.format(CHILD_ELEMENTS_XPATH, type)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODESET);
            for (int i = 0; i < childElements.getLength(); i++) {

                Element childElement = (Element) childElements.item(i);
                String elementName = childElement.getAttribute(NAME);
                String elementNsType = childElement.getAttribute(TYPE);
                String elementNs = elementNsType.split(":")[0];
                String elementType = elementNsType.split(":")[1];
                String maxOccurs = childElement.getAttribute(MAX_OCCURS);
                String minOccurs = childElement.getAttribute(MIN_OCCURS);
                String use = childElement.getAttribute(USE);
                boolean isRequired = ((maxOccurs.isEmpty() || maxOccurs.equalsIgnoreCase(NUMBER_1)) && (minOccurs.isEmpty() || minOccurs.equalsIgnoreCase(NUMBER_1))) || (use.equalsIgnoreCase(REQUIRED));
                FormioFieldType formioField = registerConfig.findFormioField(elementName, "", xsdMetadata);
                if (!jsonObject.keySet().contains(elementName) && formioField != null) {
                    jsonObject.put(elementName, formioField.getDefaultValue());
                }

                if (jsonObject.keySet().contains(elementName)) {

                    if (xsdMetadata.getCodelistItemElements().contains(elementName)) {
                        if (isRequired) {
                            element.appendChild(processCodelistItemElement(elementName, elementNs, xsdMetadata.getNamespace(), jsonObject, xsdMetadata));
                        } else {
                            JSONArray array = jsonObject.getJSONArray(elementName);
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);
                                Object value = object.get(elementName);
                                if (value != null) {
                                    element.appendChild(processCodelistItemElement(elementName, elementNs, xsdMetadata.getNamespace(), object, xsdMetadata));
                                }
                            }
                        }
                    } else if (xsdMetadata.getCodelistItemOptionalElements().contains(elementName)) {
                        if (isRequired) {
                            element.appendChild(processCodelistItemOptionalElement(elementName, elementNs, xsdMetadata.getNamespace(), jsonObject.getJSONObject(elementName), xsdMetadata));
                        } else {
                            JSONArray array = jsonObject.getJSONArray(elementName);
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);


                                Object itemCode = ((JSONObject)object.get(elementName)).has(ITEM_CODE) ? ((JSONObject)object.get(elementName)).get(ITEM_CODE) : null;
                                Object itemValue = ((JSONObject)object.get(elementName)).has(ITEM_VALUE) ? ((JSONObject)object.get(elementName)).get(ITEM_VALUE) : null;
                                if (itemCode != null && !itemCode.toString().equals("") || itemValue != null && !itemValue.toString().equals("")) {
                                    element.appendChild(processCodelistItemOptionalElement(elementName, elementNs, xsdMetadata.getNamespace(), (JSONObject)object.get(elementName), xsdMetadata));
                                }

                            }
                        }
                    } else if (xsdMetadata.getSimpleObjects().contains(elementType)) {
                        if (isRequired) {
                            element.appendChild(processSimpleElement(elementName, xsdMetadata.getNamespace(), elementType, null, jsonObject, xsdMetadata));
                        } else {
                            JSONArray array = jsonObject.getJSONArray(elementName);
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);
                                Object value = object.get(elementName);
                                if (value != null && !value.toString().isEmpty()) {
                                    element.appendChild(processSimpleElement(elementName, xsdMetadata.getNamespace(), elementType, value.toString(), null, xsdMetadata));
                                }
                            }
                        }
                    } else if (xsdMetadata.getComplexObjects().contains(elementType)) {
                        if (isRequired) {
                            element.appendChild(processComplexElement(elementName, elementType, xsdMetadata.getNamespace(), jsonObject.getJSONObject(elementName), xsdMetadata));
                        } else {
                            JSONArray array = jsonObject.getJSONArray(elementName);
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);
                                if (!object.isEmpty()) {
                                    element.appendChild(processComplexElement(elementName, elementType, xsdMetadata.getNamespace(), object.getJSONObject(elementName), xsdMetadata));
                                }
                            }
                        }
                    } else {
                        if (isRequired) {
                            processForeignElement(element, elementName, elementNs, elementType, jsonObject, xsdMetadata);
                        } else {
                            JSONArray array = jsonObject.getJSONArray(elementName);
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);
                                if (!object.isEmpty()) {
                                    processForeignElement(element, elementName, elementNs, elementType, object, xsdMetadata);
                                }
                            }
                        }
                    }
                }
            }
            return element;
        } catch (JSONException e) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Zlá štruktúra vstupných dát", null, e);
        }
    }

    private void processElementAttributes(JSONObject jsonObject, Element element, NodeList childAttributes) {
        for (int i = 0; i < childAttributes.getLength(); i++) {

            Element childAttribute = (Element) childAttributes.item(i);
            String attributeName = childAttribute.getAttribute(NAME);
            String attributeType = childAttribute.getAttribute(TYPE);

            if (!attributeType.isEmpty() && jsonObject.keySet().contains(attributeName)) {
                Object jsonValue = jsonObject.get(attributeName);
                if (jsonValue.toString() != null && !jsonValue.toString().isEmpty()) {
                    if (attributeType.contains(DATE) || attributeType.contains(DATE_TIME)) {
                        String[] splitDate = jsonValue.toString().split("T", 2);
                        element.setAttribute(attributeName, splitDate[0]);
                    } else {
                        element.setAttribute(attributeName, jsonValue.toString());
                    }
                }
            }
        }
    }

    private Element processExtensionElement(Element element, String elementName, String namespace, String elementType, JSONObject jsonObject, XsdMetadata xsdMetadata) throws Exception {

        Document typesXsd = registerConfig.getTypesXsdFile(namespace, xsdMetadata);
        XsdMetadata xsd = registerConfig.getXsdElements(elementType, namespace, new XsdMetadata(typesXsd, xsdMetadata.getDuplicateElements(), xsdMetadata.getImportXsd(), xsdMetadata.getXPath(), xsdMetadata.getXmlDocument(), xsdMetadata.getObjectFactory(), xsdMetadata.getPluginConfig(), xsdMetadata.getFormioSchemaTyp()));

        NodeList childAttributes = (NodeList) xsd.getXPath().compile(String.format(ATTRIBUTE_XPATH, elementType)).evaluate(xsd.getTypesXsdDocument(), XPathConstants.NODESET);
        if (childAttributes.getLength() == 0) {
            if (xsd.getSimpleObjects().contains(elementType)) {
                return (Element) processSimpleElement(elementName, namespace, elementType, null, jsonObject, xsd);
            }
            return (Element) processComplexElement(elementName, elementType, namespace, jsonObject, xsd);
        }

        processElementAttributes(jsonObject, element, childAttributes);
        return element;
    }

    private Node processSimpleElement(String name, String namespace, String elementType, String value, JSONObject jsonObject, XsdMetadata xsdMetadata) {

        if (jsonObject != null) {
            if (elementType.contains(BOOLEAN)) {
                value = String.valueOf(jsonObject.getBoolean(name));
            } else {
                value = jsonObject.getString(name);
            }
        }
        if (elementType.contains(DATE) || elementType.contains(DATE_TIME)) {
            String[] splitDate = value.split("T", 2);
            value = splitDate[0];
        }

        Element element = xsdMetadata.getXmlDocument().createElement(namespace.concat(":").concat(name));
        element.appendChild(xsdMetadata.getXmlDocument().createTextNode(value));
        return element;
    }

    private Node processCodelistItemElement(String elementName, String elementNamespace, String parentNamespace, JSONObject jsonObject, XsdMetadata xsdMetadata) {

        Object jsonValue = jsonObject.get(elementName);
        Element element = xsdMetadata.getXmlDocument().createElement(parentNamespace.concat(":").concat(elementName));
        Element itemCodeElement = xsdMetadata.getXmlDocument().createElement(elementNamespace.concat(":").concat(ITEM_CODE));
        itemCodeElement.appendChild(xsdMetadata.getXmlDocument().createTextNode(jsonValue.toString()));
        element.appendChild(itemCodeElement);
        return element;
    }

    private Node processCodelistItemOptionalElement(String elementName, String elementNamespace, String parentNamespace, JSONObject jsonObject, XsdMetadata xsdMetadata) {

        Object itemCodeValue = jsonObject.has(ITEM_CODE) ? jsonObject.get(ITEM_CODE) : null;
        Object itemNameValue = jsonObject.has(ITEM_VALUE) ? jsonObject.get(ITEM_VALUE) : null;
        Element element = xsdMetadata.getXmlDocument().createElement(parentNamespace.concat(":").concat(elementName));
        if (itemCodeValue != null && !itemCodeValue.toString().equals("")) {
            Element itemCodeElement = xsdMetadata.getXmlDocument().createElement(elementNamespace.concat(":").concat(ITEM_CODE));
            itemCodeElement.appendChild(xsdMetadata.getXmlDocument().createTextNode(itemCodeValue.toString()));
            element.appendChild(itemCodeElement);
        } else if (itemNameValue != null && !itemNameValue.toString().equals("")) {
            Element itemNameElement = xsdMetadata.getXmlDocument().createElement(elementNamespace.concat(":").concat(ITEM_VALUE));
            itemNameElement.appendChild(xsdMetadata.getXmlDocument().createTextNode(itemNameValue.toString()));
            element.appendChild(itemNameElement);
        }
        return element;
    }

    private void processForeignElement(Element element, String name, String namespace, String elementType, JSONObject jsonObject, XsdMetadata xsdMetadata) throws Exception {

        Document typesXsd = registerConfig.getTypesXsdFile(namespace, xsdMetadata);
        XsdMetadata xsd = registerConfig.getXsdElements(elementType, namespace, new XsdMetadata(typesXsd, xsdMetadata.getDuplicateElements(), xsdMetadata.getImportXsd(), xsdMetadata.getXPath(), xsdMetadata.getXmlDocument(), xsdMetadata.getObjectFactory(), xsdMetadata.getPluginConfig(), xsdMetadata.getFormioSchemaTyp()));

        if (xsd.getSimpleObjects().contains(elementType)) {
            if (jsonObject.keySet().contains(name)) {
                element.appendChild(processSimpleElement(name, xsdMetadata.getNamespace(), elementType, null, jsonObject, xsdMetadata));
            }
        } else {
            JSONObject object = jsonObject.getJSONObject(name);
            if (!object.isEmpty()) {
                element.appendChild(processComplexElement(name, elementType, xsdMetadata.getNamespace(), object, xsd));
            }
        }
    }

    private String ignoreNameSpace(String xPath) {
        List<String> elementList = Arrays.asList(xPath.split("/"));

        StringBuilder stringBuilder = new StringBuilder();

        elementList.forEach(element -> {
            if (!element.equals("")) {
                element = element.replaceAll("[a-zA-Z]*.:", "");
                stringBuilder.append("//*[local-name() = '").append(element).append("']");
            }
        });
        return stringBuilder.toString();
    }

    public List<ZaznamRegistraXPathData> getXPathData(String xml, List<String> xpaths, AbstractRegPlugin registerPlugin) throws IOException, ParserConfigurationException, SAXException {

        if (xpaths == null || xpaths.isEmpty()) {
            throw new CommonException(HttpStatus.BAD_REQUEST, NO_XPATH_ERROR);
        }
        Document doc = XmlUtils.parse(xml);
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        setNamespace(xpath, registerPlugin.getPluginConfig());

        List<ZaznamRegistraXPathData> ZaznamRegistraXPathDataList = new ArrayList<>();
        for (String xpathFor : xpaths) {
            XPathExpression expr;
            NodeList nodeList;
            try {
                expr = xpath.compile(xpathFor);
                nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            } catch (@SuppressWarnings("unused") Exception ex) {
                throw new CommonException(HttpStatus.BAD_REQUEST, String.format(INVALID_XPATH_ERROR, xpathFor));
            }

            if (nodeList.getLength() == 0) {
                throw new CommonException(HttpStatus.BAD_REQUEST, String.format(NO_RESULT_XPATH_ERROR, xpathFor));
            }

            for (int i = 0; i < nodeList.getLength(); i++) {
                ZaznamRegistraXPathData ZaznamRegistraXPathData = new ZaznamRegistraXPathData();
                ZaznamRegistraXPathData.setXpath(xpathFor);

                /**Kontrola ci je xpath pre koncovy node (ci nema childa alebo kontroluje sa ci ma prave jedneho childa a ci je dany child textovy)*/
                if (!nodeList.item(i).hasChildNodes() || (nodeList.item(i).getChildNodes().getLength() == 1 && nodeList.item(i).getFirstChild().getNodeType() == Node.TEXT_NODE)) {
                    ZaznamRegistraXPathData.setHodnota(nodeList.item(i).getTextContent());
                } else {
                    String string = XmlUtils.nodeListToXml(nodeList);
                    /**ostranenie prveho xml tagu a namespaceURI*/
                    ZaznamRegistraXPathData.setHodnota(string);
                }
                ZaznamRegistraXPathDataList.add(ZaznamRegistraXPathData);
            }
        }
        return ZaznamRegistraXPathDataList;
    }

    public String changeXml(ZaznamRegistraXPathZmena registerEntryXPathChanges, AbstractRegPlugin plugin, Document xmlData) throws Exception {
        List<ZaznamRegistraXPathDataUpdate>  changes = registerEntryXPathChanges.getZmeny();

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        setNamespace(xpath, plugin.getPluginConfig());

        checkXpaths(changes, xmlData, xpath);

        boolean isOldValid = registerEntryXPathChanges.getStaryPlatny();
        boolean isOldCurrent = registerEntryXPathChanges.getStaryAktualny();

        XsdMetadata xsdMetadata = registerConfig.prepareXsdMetadata(null, plugin, xpath);

        Map<Node, List<Map<String, String>>> insertHistoricalMap = new HashMap<>();
        Map<Node, List<Map<String, String>>> updateHistoricalMap = new HashMap<>();
        Map<Node, List<Map<String, String>>> deleteHistoricalMap = new HashMap<>();

        for (var change : changes) {
            String changeXpath = trimmXpath(change.getXpath());
            String changeValue = change.getValue();
            XpathDataUpdateType changeType = change.getAction() == null ? XpathDataUpdateType.UPDATE : change.getAction();

            boolean historical = false;

            for (RegisterEntryField historicalField : plugin.getHistoricalFields()) {

                if (changeXpath.startsWith(historicalField.getXPathValue())) {
                    if (!changeXpath.contains("@sequence=") && !changeType.equals(XpathDataUpdateType.INSERT))
                        throw new CommonException(HttpStatus.BAD_REQUEST, String.format(HISTORICAL_XPATH_DOES_NOT_CONTAIN_SEQUENCE_ERROR, changeXpath), null);

                    String historicalXpath = changeXpath.substring(0, changeXpath.indexOf(']') + 1);

                    if (historicalXpath.equals("")) {
                        historicalXpath = historicalField.getXPathValue();
                    }

                    Node node = null;
                    if (!changeType.equals(XpathDataUpdateType.INSERT)) {
                        node = (Node) xpath.compile(historicalXpath).evaluate(xmlData, XPathConstants.NODE);

                        if (isElementMultiplicity(node.getLocalName(), NUMBER_1, xsdMetadata)) {
                            while (!isElementMultiplicity(node.getLocalName(), UNBOUNDED, xsdMetadata))
                                node = node.getParentNode();
                        }
                    }

                    Map<String, String> changeMap = new HashMap<>();
                    changeMap.put(HISTORICAL_XPATH, historicalXpath);
                    changeMap.put(CHANGE_XPATH, changeXpath);
                    changeMap.put(CHANGE_VALUE, changeValue);

                    if (changeType.equals(XpathDataUpdateType.INSERT)) {
                        if (insertHistoricalMap.containsKey(node)) {
                            insertHistoricalMap.get(node).add(changeMap);
                        } else {
                            List<Map<String, String>> list = new ArrayList<>();
                            list.add(changeMap);
                            insertHistoricalMap.put(node, list);
                        }
                    } else if (changeType.equals(XpathDataUpdateType.UPDATE)) {
                        if (updateHistoricalMap.containsKey(node)) {
                            updateHistoricalMap.get(node).add(changeMap);
                        } else {
                            List<Map<String, String>> list = new ArrayList<>();
                            list.add(changeMap);
                            updateHistoricalMap.put(node, list);
                        }
                    } else if (changeType.equals(XpathDataUpdateType.DELETE)) {
                        if (deleteHistoricalMap.containsKey(node)) {
                            deleteHistoricalMap.get(node).add(changeMap);
                        } else {
                            List<Map<String, String>> list = new ArrayList<>();
                            list.add(changeMap);
                            deleteHistoricalMap.put(node, list);
                        }
                    }
                    historical = true;
                    break;
                }
            }
            if (!historical) {
                if (changeType.equals(XpathDataUpdateType.INSERT)) {
                    insertElement(changeXpath, changeValue, xpath, xmlData, plugin);
                } else if (changeType.equals(XpathDataUpdateType.UPDATE)) {
                    updateElement(changeXpath, changeValue, xpath, xmlData);
                } else if (changeType.equals(XpathDataUpdateType.DELETE)) {
                    deleteElement(changeXpath, xpath, xmlData);
                }
            }
        }
        mergeRelatedNodes(insertHistoricalMap);
        mergeRelatedNodes(updateHistoricalMap);
        mergeRelatedNodes(deleteHistoricalMap);

        insertHistorical(insertHistoricalMap, xmlData, xpath, isOldValid, isOldCurrent);
        updateHistorical(updateHistoricalMap, xmlData, xpath, isOldValid, isOldCurrent);
        deleteHistorical(deleteHistoricalMap, xmlData, xpath);

        return XmlUtils.xmlToString(xmlData);
    }

    /**
     * Zisti ci je element s danym menom v dokumente
     */
    private boolean elementInXml(String elementName, Document document, XPath xpath) throws XPathExpressionException {
        NodeList nodeList = (NodeList) xpath.compile(elementName).evaluate(document, XPathConstants.NODESET);
        if (nodeList.getLength() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Vrati predchadzajuceho surodenca nodu v dokumente
     */
    private Node getPreviousSiblingElement(Node node, Document document, XPath xpath) throws XPathExpressionException {
        if (node == null)
            return null;

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String elementName = ignoreNameSpace(node.getAttributes().getNamedItem("name").getNodeValue());
            boolean elementInXml = elementInXml(elementName, document, xpath);

            if (elementInXml) {
                return node;
            }
        }
        return getPreviousSiblingElement(node.getPreviousSibling(), document, xpath);
    }

    /**
     * Vrati nasledujuceho surodenca nodu v dokumente
     */
    private Node getNextSiblingElement(Node node, Document document, XPath xpath) throws XPathExpressionException {
        if (node == null)
            return null;

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String elementName = ignoreNameSpace(node.getAttributes().getNamedItem("name").getNodeValue());
            boolean elementInXml = elementInXml(elementName, document, xpath);

            if (elementInXml) {
                return node;
            }
        }
        return getNextSiblingElement(node.getNextSibling(), document, xpath);
    }

    private String removeNamespace(String element) {
        return element.replaceAll("[a-zA-Z]*.:", "");
    }

    /**
     * Vlozi novy element do dokumentu
     */
    private void insertElement(String xpathString, String value, XPath xpath, Document document, AbstractRegPlugin plugin) throws Exception {
        XsdMetadata xsdMetadata = registerConfig.prepareXsdMetadata(null, plugin, xpath);

        Node newNode = stringToNode(value);
        String newNodeName = removeNamespace(newNode.getNodeName());
        Node node = (Node) xsdMetadata.getXPath().compile(String.format(ELEMENT_BY_NAME_XPATH, newNodeName))
                .evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);

        int maxOccurs = Integer.parseInt(node.getAttributes().getNamedItem(MAX_OCCURS).getNodeValue());
        int numberOfOccurances = ((NodeList) xpath.compile(ignoreNameSpace(newNodeName)).evaluate(document, XPathConstants.NODESET)).getLength();

        if (numberOfOccurances + 1 > maxOccurs) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Záznam nemôže byť vložený, pretože bol prekročený počet záznamov rovnakého typu", null);
        }

        Node previousSiblingFromXsd = getPreviousSiblingElement(node.getPreviousSibling(), document, xpath);
        String previousSiblingName = ignoreNameSpace(previousSiblingFromXsd.getAttributes().getNamedItem("name").getNodeValue());

        Node previousSiblingFromXml = (Node) xpath.compile(previousSiblingName).evaluate(document, XPathConstants.NODE);

        node = (Node) xsdMetadata.getXPath().compile(String.format(ELEMENT_BY_NAME_XPATH, previousSiblingFromXml.getLocalName()))
                .evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);

        newNode = stringToNode(value);
        Node parentNode = (Node) xpath.compile(xpathString).evaluate(document, XPathConstants.NODE);
        Node nextSiblingFromXsd = getNextSiblingElement(node.getNextSibling(), document, xpath);

        if (nextSiblingFromXsd != null) {
            String nextSiblingName = nextSiblingFromXsd.getAttributes().getNamedItem("name").getNodeValue();
            Node nextSiblingFromXml = (Node) xpath.compile(ignoreNameSpace(nextSiblingName)).evaluate(document, XPathConstants.NODE);
            parentNode.insertBefore(document.importNode(newNode, true), nextSiblingFromXml);
        } else {
            parentNode.appendChild(document.importNode(newNode, true));
        }
    }

    /**
     * Updatne nehistoricky element v dokumente
     */
    private void updateElement(String xpathString, String value, XPath xpath, Document document) throws XPathExpressionException {
        Node node = (Node) xpath.compile(xpathString).evaluate(document, XPathConstants.NODE);
        if (node.getFirstChild() != null) {
            node.getFirstChild().setNodeValue(value);
        } else {
            node.setTextContent(value);
        }
    }

    /**
     * Vymaze nehistoricky element z dokumentu
     */
    private void deleteElement(String xpathString, XPath xpath, Document document) throws XPathExpressionException {
        Node node = (Node) xpath.compile(xpathString).evaluate(document, XPathConstants.NODE);
        Element element = (Element) node;
        element.getParentNode().removeChild(element);
    }

    private void insertHistorical(Map<Node, List<Map<String, String>>> insertHistoricalMap, Document xmlData, XPath xpath, boolean isOldValid, boolean isOldCurrent) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        for (var historical : insertHistoricalMap.entrySet()) {
            List<Map<String, String>> historicalMap = historical.getValue();
            String historicalXpath = historicalMap.get(0).get(HISTORICAL_XPATH);

            Node historicalNode = (Node) xpath.compile(historicalXpath).evaluate(xmlData, XPathConstants.NODE);
            Document nodeCopyDocument = nodeToDocument(historicalNode.cloneNode(true));

            for (var change : historicalMap) {
                String inputXpath = change.get(CHANGE_XPATH);
                String inputValue = change.get(CHANGE_VALUE);

                if (inputXpath.equals(historicalXpath)) {
                    Node newNode = stringToNode(inputValue);
                    Node node = getNodeWithMaxSequence(inputXpath, xmlData, xpath);
                    node.getParentNode().insertBefore(xmlData.adoptNode(newNode), node);
                } else {
                    String diff = xpathDiff(inputXpath, historicalXpath);
                    Node node = (Node) xpath.compile("/" + diff).evaluate(nodeCopyDocument, XPathConstants.NODE);

                    String parentElementTag = diff.replace("/", "");
                    Node newNode = createNodeFromXmlImputValue(inputValue, parentElementTag);

                    node.getParentNode().replaceChild(nodeCopyDocument.importNode(newNode, true), node);

                    long maxSequenceNumber  = findMaxSequence(xmlData, historicalNode.getNodeName(), xpath);
                    nodeCopyDocument.getDocumentElement().getAttributes().getNamedItem(SEQUENCE).setNodeValue(String.valueOf(maxSequenceNumber + 1));

                }
            }
            Node originalDocumentNode = (Node) xpath.compile(historicalXpath).evaluate(xmlData, XPathConstants.NODE);
            originalDocumentNode.getParentNode().insertBefore(xmlData.importNode(nodeCopyDocument.getDocumentElement(), true), originalDocumentNode);

            // aktualizovanie atributov povodneho zaznamu
            Node updateNode = (Node) xpath.compile(historicalXpath).evaluate(xmlData, XPathConstants.NODE);
            updateOriginalNode(updateNode, isOldValid, isOldCurrent);
        }
    }

    private void updateHistorical(Map<Node, List<Map<String, String>>> historicalMap, Document xmlData, XPath xpath, boolean isOldValid, boolean isOldCurrent) throws XPathExpressionException, ParserConfigurationException {
        Map<String, Long> maxSequencesByElement = new HashMap<>();

        for (var entry : historicalMap.entrySet()) {
            Node node = entry.getKey();
            var changeMap = entry.getValue();

            for (var change : changeMap) { // aktualizovanie atributov povodnych zaznamov
                String historicalXpath = change.get(HISTORICAL_XPATH);
                Node updateNode = (Node) xpath.compile(historicalXpath).evaluate(xmlData, XPathConstants.NODE);
                updateOriginalNode(updateNode, isOldValid, isOldCurrent);
            }

            Document nodeCopyDocument = nodeToDocument(node.cloneNode(true));

            Set<Node> skipNodes = new HashSet<>();
            for (var change : changeMap) { // mazanie nechcenych historickych zaznamov v kopii
                String historicalXpath = change.get(HISTORICAL_XPATH);
                Node skipNode = (Node) xpath.compile("//" + historicalXpath.substring(historicalXpath.lastIndexOf('/') + 1))
                        .evaluate(nodeCopyDocument, XPathConstants.NODE);
                skipNodes.add(skipNode);
            }
            Node nodeCopyDocumentRoot = nodeCopyDocument.getDocumentElement();
            deleteHistoricalNodes(nodeCopyDocumentRoot, skipNodes);

            for (var change : changeMap) { // aktualizovanie historickych zaznamov v kopii
                String changeXpath = change.get(CHANGE_XPATH);
                String changeValue = change.get(CHANGE_VALUE);
                String historicalXpath = change.get(HISTORICAL_XPATH);

                String xpathWithoutHistorical = xpathDiff(changeXpath, historicalXpath);

                String lastElementHistoricalXpath = "//" + historicalXpath.substring(historicalXpath.lastIndexOf('/') + 1);

                updateAttributes(lastElementHistoricalXpath, xpath, nodeCopyDocument);
                updateElement(lastElementHistoricalXpath + xpathWithoutHistorical, changeValue, xpath, nodeCopyDocument);
            }
            updateSequences(xpath, xmlData, nodeCopyDocument, maxSequencesByElement);
            if (nodeCopyDocumentRoot != null) {
                removeEmptyLines(nodeCopyDocumentRoot);
            }
            assert nodeCopyDocumentRoot != null;
            node.getParentNode().insertBefore(xmlData.adoptNode(nodeCopyDocumentRoot.cloneNode(true)), node);
        }
    }

    private void deleteHistorical(Map<Node, List<Map<String, String>>> historicalMap, Document xmlData, XPath xpath) throws XPathExpressionException {
        for (var entry : historicalMap.entrySet()) {
            var changeMap = entry.getValue();

            for (var change : changeMap) { // aktualizovanie atributov povodnych zaznamov
                String historicalXpath = change.get(HISTORICAL_XPATH);
                Node updateNode = (Node) xpath.compile(historicalXpath).evaluate(xmlData, XPathConstants.NODE);
                invalidateOriginalNode(updateNode);
            }
        }
    }

    /**
     * Z dokumentu vrati node s najvyssiou hodnotou podla zadaneho xpathu
     */
    private Node getNodeWithMaxSequence(String xpathString, Document xmlData, XPath xpath) throws XPathExpressionException { // TODO
        String[] aaa = xpathString.split("/");
        String aaaaa = aaa[aaa.length - 1];
        String str = xpathString + "[not(@sequence<=preceding-sibling::" + aaaaa +"/@sequence) and not(@sequence<=following-sibling::" + aaaaa + "/@sequence)]";
        return (Node) xpath.compile(str).evaluate(xmlData, XPathConstants.NODE);
    }

    /**
     * Vyrobi zo stringu v xml formate node
     */
    private Node stringToNode(String fragment) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return docBuilder.parse(new InputSource(new StringReader(fragment))).getDocumentElement();
    }

    /**
     * Vyrobi zo stringu v xml formate node
     */
    private Node createNodeFromXmlImputValue(String inputValue, String parentElementTag) throws ParserConfigurationException, IOException, SAXException {
        try {
            return  DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream((inputValue).getBytes())).getDocumentElement();
        } catch (SAXParseException e) {//FIXME cez exception by sme to isto robiť nemali!
            return  DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(("<" + parentElementTag + ">" + inputValue + "</" + parentElementTag + ">").getBytes())).getDocumentElement();
        }
    }

    private String trimmXpath(String originalXpath) {
        if (originalXpath.charAt(0) == '/')
            return originalXpath.substring(1);
        return originalXpath;
    }

    private void checkXpaths(List<ZaznamRegistraXPathDataUpdate> changes, Document xmlData, XPath xpath) {
        if (changes.isEmpty()) {
            throw new CommonException(HttpStatus.BAD_REQUEST, EMPTY_CHANGES_ERROR, null);
        }

        XPathExpression expr;

        List<Node> nodeList = new ArrayList<>();

        for (var change : changes) {
            String changeXpath = change.getXpath();
            XpathDataUpdateType changeType = change.getAction();

            if (changeXpath.startsWith("//")) {
                throw new CommonException(HttpStatus.BAD_REQUEST, RELATIVE_XPATH_ERROR, null);
            }

            NodeList list;
            try {
                expr = xpath.compile(changeXpath);
                list = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
            } catch (@SuppressWarnings("unused") Exception ex) {
                throw new CommonException(HttpStatus.BAD_REQUEST, String.format(INVALID_XPATH_ERROR, changeXpath), null);
            }

            if (list.getLength() > 1 && !changeType.equals(XpathDataUpdateType.INSERT)) {
                throw new CommonException(HttpStatus.BAD_REQUEST, String.format(MULTIPE_XPATH_RESULTS_ERROR, changeXpath), null);
            }

            Node node = list.item(0);

            if (node == null) {
                throw new CommonException(HttpStatus.BAD_REQUEST, String.format(NO_RESULT_XPATH_ERROR, changeXpath), null);
            }

            if (nodeList.contains(node)) {
                throw new CommonException(HttpStatus.BAD_REQUEST, MULTIPE_XPATHS_SAME_RESULT_ERROR, null);
            }

            nodeList.add(node);
        }
    }

    /**
     * Prejde mapu a zistí, či sú vrcholy príbuzné. Ak áno tak predchodcovi pridá hodnotu potomka.
     * Nakoniec vymaze vsetkych potomkov.
     */
    private void mergeRelatedNodes(Map<Node, List<Map<String, String>>> historicalMap) {
        List<Node> removeList = new ArrayList<>();

        for (Map.Entry<Node, List<Map<String, String>>> entry1 : historicalMap.entrySet()) {
            Node predecessor = findPredecesor(new ArrayList<>(historicalMap.keySet()), entry1.getKey());

            if (predecessor != null) {
                historicalMap.get(predecessor).addAll(entry1.getValue());
                removeList.add(entry1.getKey());
            }
        }

        for (Node node : removeList)
            historicalMap.remove(node);
    }

    /**
     * Nastavi hodnotu valid na false
     */
    private void invalidateOriginalNode(Node node) {
        if (node == null)
            return;

        if (node.getAttributes().getNamedItem(SEQUENCE) != null) {
            ((Element) node).setAttribute(VALID, FALSE);
        }

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                invalidateOriginalNode(children.item(i));
        }
    }

    private void updateOriginalNode(Node node, boolean isOldValid, boolean isOldCurrent) {
        if (node == null)
            return;

        if (node.getAttributes().getNamedItem(SEQUENCE) != null) {
            String effectiveto = new SimpleDateFormat(YYYY_MM_DD).format(new Date());

            if (node.getAttributes().getNamedItem(EFFECTIVE_TO) != null)
                throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ELEMENT_EFFECTIVE_TO_ALREADY_SET_ERROR, node.getNodeName()), null);

            if (isOldValid)
                ((Element) node).setAttribute(EFFECTIVE_TO, effectiveto);
            else
                ((Element) node).setAttribute(VALID, FALSE);

            if (!isOldCurrent)
                ((Element) node).setAttribute(CURRENT, FALSE);
        }

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                updateOriginalNode(children.item(i), isOldValid, isOldCurrent);
        }
    }

    /**
     * Vymaže všetky historické elementy zadaného vrcholu a jeho detí okrem tých,
     * ktoré sú v množine skipNode.
     */
    private void deleteHistoricalNodes(Node node, Set<Node> skipNode) {
        if (node == null || skipNode.contains(node))
            return;

        if (node.getAttributes().getNamedItem(SEQUENCE) != null)
            node.getParentNode().removeChild(node);

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                deleteHistoricalNodes(children.item(i), skipNode);
        }
    }

    /**
     * Pre vrcholy nájdené xpathString-om v document-e aktualizuje hodnoty.
     */
    private void updateAttributes(String xpathString, XPath xpath, Document document) throws XPathExpressionException {
        Node node = (Node) xpath.compile(xpathString).evaluate(document, XPathConstants.NODE);
        String effectiveFrom = new SimpleDateFormat(YYYY_MM_DD).format(new Date());
        setAttribues(node, effectiveFrom);
    }

    /**
     * Aktualizuje sekvencie pre vrcholy v dokumente.
     *
     * @param originalDocument      dokument, z ktorého sa zisťujú aktuálne maximálne sekvencie
     * @param document              aktualizovaný dokument
     * @param maxSequencesByElement mapa, ktorá drží maximálnu sekvenciu elementov
     */
    private void updateSequences(XPath xpath, Document originalDocument, Document document, Map<String, Long> maxSequencesByElement) throws XPathExpressionException {
        NodeList nodeList = (NodeList) xpath.compile(SEQUENCE_ATTRIBUTE_ELEMENTS_XPATH).evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();

            if (maxSequencesByElement.get(nodeName) == null) {
                maxSequencesByElement.put(nodeName, findMaxSequence(originalDocument, node.getNodeName(), xpath) + 1);
            } else {
                maxSequencesByElement.put(nodeName, maxSequencesByElement.get(nodeName) + 1);
            }
            node.getAttributes().getNamedItem(SEQUENCE).setNodeValue(String.valueOf(maxSequencesByElement.get(nodeName)));
        }
    }

    /**
     * Zistí, či element so zadaným názvom má požadovanú multiplicitu.
     *
     * @param name           názov elementu
     * @param maxOccursValue maximálna multiplicita
     * @param xsdMetadata    xsd
     */
    private boolean isElementMultiplicity(String name, String maxOccursValue, XsdMetadata xsdMetadata) throws XPathExpressionException {
        Node node = (Node) xsdMetadata.getXPath().compile(String.format(ELEMENT_BY_NAME_XPATH, name))
                .evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);

        if (node == null) {
            for (Map.Entry<String, Document> entry : xsdMetadata.getImportXsd().entrySet()) {
                Document importedDocument = entry.getValue();
                node = (Node) xsdMetadata.getXPath().compile(String.format(ELEMENT_BY_NAME_XPATH, name))
                        .evaluate(importedDocument, XPathConstants.NODE);

                if (node != null) {
                    break;
                }
            }
        }
        assert node != null;
        String maxOccurs = node.getAttributes().getNamedItem(MAX_OCCURS).getNodeValue();
        String minOccurs = node.getAttributes().getNamedItem(MIN_OCCURS).getNodeValue();

        return (minOccurs.equals("0") || minOccurs.equals(NUMBER_1)) && maxOccurs.equals(maxOccursValue);
    }

    /**
     * Pre zadanému vrcholu nastavý nové atribúty
     */
    private void setAttribues(Node node, String effectiveFrom) {
        if (node == null)
            return;

        if (node.getAttributes().getNamedItem(SEQUENCE) != null) {
            ((Element) node).setAttribute(CURRENT, TRUE);
            ((Element) node).setAttribute(EFFECTIVE_FROM, effectiveFrom);
            node.getAttributes().removeNamedItem(EFFECTIVE_TO);
            ((Element) node).setAttribute(VALID, TRUE);
        }

        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
                setAttribues(children.item(i), effectiveFrom);
        }
    }

    /**
     * Vráti xpath1 - xpath2 (rozdiel)
     */
    private static String xpathDiff(String xpath1, String xpath2) {
        int index = xpath1.lastIndexOf(xpath2);
        if (index > -1)
            return xpath1.substring(xpath2.length());
        return xpath1;
    }

    /**
     * Nájde najvyššiu sekvenciu elementu v dokumente
     */
    private Long findMaxSequence(Document xmlData, String elementName, XPath xpath) throws XPathExpressionException {
        String maxSequenceXpath = "//" + elementName;

        NodeList nodeList = (NodeList) xpath.compile(maxSequenceXpath).evaluate(xmlData, XPathConstants.NODESET);

        long maxSequence = 0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            Node sequence = node.getAttributes().getNamedItem(SEQUENCE);

            if (sequence == null)
                continue;

            long sequenceVlaue = Long.parseLong(sequence.getNodeValue());
            if (sequenceVlaue > maxSequence)
                maxSequence = sequenceVlaue;
        }
        return maxSequence;
    }

    /**
     * Vytvorí z vrcholu dokument
     */
    private Document nodeToDocument(Node node) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();
        Node importedNode = newDocument.importNode(node, true);
        newDocument.appendChild(importedNode);
        return newDocument;
    }

    /**
     * Ak s v nodeList nachádza predchodca node vrcholu, tak ho nájde, inak vráti null
     */
    private Node findPredecesor(List<Node> nodeList, Node node) {
        for (Node predecesor : nodeList) {
            if (predecesor == node)
                continue;
            if (isPredecessor(predecesor, node))
                return predecesor;
        }
        return null;
    }

    /**
     * Zistí či je node vrchol predchodcom ancestor vrcholu
     */
    private boolean isPredecessor(Node node, Node ancestor) {
        while (ancestor != null) {
            if (node == ancestor)
                return true;
            ancestor = ancestor.getParentNode();
        }
        return false;
    }

}
