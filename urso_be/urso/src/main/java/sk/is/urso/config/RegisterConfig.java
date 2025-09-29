package sk.is.urso.config;

import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.alfa.utils.PerformaceUtils;
import org.alfa.utils.XmlUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sk.is.urso.data.XsdMetadata;
import sk.is.urso.model.Register;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.RegisterEntryHistoryKey;
import sk.is.urso.reg.RegisterEntryReferenceKey;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.repository.ciselniky.CiselnikRepository;
import sk.is.urso.rest.model.FormioSchemaTyp;
import sk.is.urso.service.RegisterService;
import sk.is.urso.common.regconfig.plugin.v1.CustomFormioExpressionType;
import sk.is.urso.common.regconfig.plugin.v1.FormioFieldType;
import sk.is.urso.common.regconfig.plugin.v1.RegisterEntryField;
import sk.is.urso.common.regconfig.plugin.v1.RegisterField;
import sk.is.urso.common.regconfig.plugin.v1.RegisterJoinKeyType;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.plugin.v1.XsdFile;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;
import sk.is.urso.common.regconfig.v1.RegistersConfig;
import sk.is.urso.formio.schema.Button;
import sk.is.urso.formio.schema.Checkbox;
import sk.is.urso.formio.schema.ComponentType;
import sk.is.urso.formio.schema.ComponentTypeType;
import sk.is.urso.formio.schema.Components;
import sk.is.urso.formio.schema.ConditionalType;
import sk.is.urso.formio.schema.Container;
import sk.is.urso.formio.schema.Datagrid;
import sk.is.urso.formio.schema.Datetime;
import sk.is.urso.formio.schema.DatetimeWidgetType;
import sk.is.urso.formio.schema.InputType;
import sk.is.urso.formio.schema.ObjectFactory;
import sk.is.urso.formio.schema.Panel;
import sk.is.urso.formio.schema.Select;
import sk.is.urso.formio.schema.SelectDataHeaderType;
import sk.is.urso.formio.schema.SelectDataType;
import sk.is.urso.formio.schema.SelectDataValueType;
import sk.is.urso.formio.schema.Textarea;
import sk.is.urso.formio.schema.Textfield;
import sk.is.urso.formio.schema.ValidateType;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

@Configuration
public class RegisterConfig implements IException {

	public static final String TYPES_XSD = "_TYPES.xsd";
	public static final String MULTIPLICITA_ATRIBUTU_X_V_REGISTRI_X_NIE_SU_SPRAVNE_NASTAVENE = "Multiplicita id atribútu %s v registri %s nie je správne nastavená - musí byť 1..1!";
	public static final String FILE_REGISTERS_CONFIGURATION = "registersConfig_v1.xml";

	@Value("${registers-file-path}")
	public String registersFilePath;
	private static final Logger log = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

	@Value("${registers-file-path}")
	private File registersDir;

	@Value("${enumValues.url}")
	private String enumValuesUrl;

	@Value("${formio-max-nesting-depth}")
	private int formioNestingDepth;

	@Value("${tmp-file-path}")
	private String tmpPath;

	@Autowired
	private DefaultListableBeanFactory beanFactory;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private RegisterService registerService;

	@Autowired
	private CiselnikRepository ciselnikRepository;

	private final Properties localizedProperties = new Properties();

	public static final Map<RegisterId, AbstractRegPlugin> registerPlugins = new HashMap<>();
	public static final Map<RegisterId, String> registerNames = new HashMap<>();

	private static final String FORMIO_DATE_FORMAT = "dd.MM.yyyy";
	private static final String FORMIO_DATETIME_FORMAT = "dd.MM.yyyy hh:mm";

	private static final String FORMIO_CUSTOM_CLASS = "uvzFormio%s";
	private static final String FORMIO_CUSTOM_CLASS_HIST = "uvzFormio%sHist";
	private static final String REG_CODELIST_ITEM_XPATH = "schema//element[@type='rb:REG_codelistItem_t']";
	private static final String REG_CODELIST_ITEM_OPTIONAL_XPATH = "schema//element[@type='rb:REG_codelistItemOptional_t']";
	private static final String SIMPLE_OBJECTS_XPATH = "schema//simpleType";
	private static final String COMPLEX_OBJECTS_XPATH = "schema//complexType";
	private static final String FOREIGN_OBJECTS_XPATH = "schema//element[not(contains(@type, '%s'))]";
	private static final String CHILD_ELEMENTS_XPATH = "schema/*[@name='%s']//element";

	private static final String SIMPLE_XSD_OBJECTS_XPATH = "schema//element[starts-with(@type, 'xsd:')] | schema//attribute[starts-with(@type, 'xsd:')]";
	private static final String ATTRIBUTE_XPATH = "schema/*[@name='%s']//attribute";
	private static final String EXTENSION_XPATH = "schema/*[@name='%s']//extension";
	private static final String EXTAND_ATT_XPATH = "schema/*[@name='%s']//extension | schema/*[@name='%s']//attributeGroup";
	private static final String RESTRICTION_XPATH = "schema/*[@name='%s']//restriction";
	private static final String ATT_RESTRICTION_XPATH = "schema/*[@name='%s']/attribute[@name='%s']//restriction";
	private static final String ELE_RESTRICTION_XPATH = "//*[@name='%s']//*[@name='%s']//*[@base='%s']";
	private static final String ELE_BASE_XPATH = "schema/*[@name='%s']//element[@name='%s']//restriction//@base";
	private static final String NAME_PARAM_XPATH = "schema/*[@name='%s']";
	private static final String SCHEMA_XPATH = "schema/%s";
	private static final String XMLNS_XPATH = "xmlns:%s";
	private static final String SCHEMA = "schema";
	private static final String VALID = "valid";
	private static final String ELEMENT = "element";
	private static final String IMPORT = "import";
	private static final String ITEM_VALUE = "itemValue";
	private static final String SUBMIT = "submit";
	private static final String BASE = "base";
	private static final String REF = "ref";
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String MAX_OCCURS = "maxOccurs";
	private static final String MIN_OCCURS = "minOccurs";
	private static final String USE = "use";
	private static final String NUMBER_1 = "1";
	private static final String REQUIRED = "required";
	private static final String BOOLEAN = "boolean";
	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String DATE_TIME = "dateTime";
	private static final String VALUE = "value";
	private static final String BOTTOM = "bottom";
	private static final String ADD_SK = "Pridať";
	private static final String SHOW = "show";
	private static final String DATA = "_data";
	private static final String NAME_SPACE = "namespace";
	private static final String SCHEMA_LOCATION = "schemaLocation";
	private static final String UNDERSCORE = "_";
	private static final String TARGET_NAMESPACE_PARAM = "@targetNamespace";
	private static final String REG_CODELIST_ITEM = "REG_codelistItem";
	private static final String REG_CODELIST_ITEM_T = "REG_codelistItem_t";
	private static final String REG_CODELIST_ITEM_OPTIONAL_T = "REG_codelistItemOptional_t";
	private static final String REG_ITEM_WITH_HISTORY = "REG_itemWithHistory_t";
	private static final String RB_ITEM_CODE = "rb:itemCode";
	private static final String ITEM_CODE = "itemCode";
	private static final String FORMIO_SCHEMAS = "formio_schemas";
	private static final String JSON_EXTENSION = ".json";
	private static final String XML_EXTENSION = ".xml";

	private static final String UVZ_REG_COMMON_CONFIGURATION = "../uvz_reg_common/configuration/";

	private static final String XS_NS = "xs";

	private static final String INVALID_DATA_FORMAT = "Dáta zadané v nesprávnom formáte!";
	private static final String INVALID_SUBJECT_ID_FORMAT = "Nesprávny formát ID subjektu!";

	private static final String INDEX = "_index";
	private static final String DATA_REFERENCE = "_data_reference";
	private static final String DATA_HISTORY = "_data_history";

	/**
	 * Nájde plugin podľa jeho ID.
	 * Ošetruje aj prípad kedy hľadáme podľa interného ID ale register je vedený pod public ID.
	 * Ak nenájde plugin vráti null
	 *
	 * @param regId id registra, interné alebo public
	 * @return
	 */
	public static AbstractRegPlugin findPlugin(RegisterId regId) {
		AbstractRegPlugin plugin = registerPlugins.get(regId);
		if (plugin == null) {
			// asi je pod public ID ale to nepozname
			plugin = registerPlugins.values().stream().filter(p -> p.getInternalId().equals(regId)).findFirst().orElse(null);
		}
		return plugin;
	}

	public Element getRootElement(String root, Document xsdDocument, XPath xPath) throws Exception {
		if (root == null) {
			return (Element) xPath.compile(String.format(SCHEMA_XPATH, ELEMENT)).evaluate(xsdDocument, XPathConstants.NODE);
		}
		return (Element) xPath.compile(String.format(NAME_PARAM_XPATH, root)).evaluate(xsdDocument, XPathConstants.NODE);
	}

	public String getNodeName(String nodeName) {
		if (nodeName.contains(":")) {
			return nodeName.split(":")[1];
		}
		return nodeName;
	}

	private String xmlToJson(String xml) throws IOException {
		String json = XmlUtils.xmlToJson(xml);
		json = json.substring("{\"components\": ".length(), json.length() - 1);
		json = Pattern.compile("\\{[^{]+\"type\": \"none\"[^}]+},", Pattern.MULTILINE).matcher(json).replaceAll("");
		return json;
	}

	public Map<String, Document> loadImportXsdFiles(Document typesXsd, File currentDir, Map<String, Document> imports, XPath xPath) throws Exception {

		String targetNamespace = (String) xPath.compile(String.format(SCHEMA_XPATH, TARGET_NAMESPACE_PARAM)).evaluate(typesXsd, XPathConstants.STRING);
		if (!imports.containsKey(targetNamespace)) {
			imports.put(targetNamespace, typesXsd);
		}

		NodeList xmlnsElements = (NodeList) xPath.compile(String.format(SCHEMA_XPATH, IMPORT)).evaluate(typesXsd, XPathConstants.NODESET);
		for (int i = 0; i < xmlnsElements.getLength(); i++) {

			Element xmlnsElement = (Element) xmlnsElements.item(i);
			String namespace = xmlnsElement.getAttribute(NAME_SPACE);
			if (!imports.containsKey(namespace)) {
				String schemaLocation = xmlnsElement.getAttribute(SCHEMA_LOCATION);
				File xsdFilePath = new File(currentDir, schemaLocation);
				File parentFile = xsdFilePath.getParentFile();
				typesXsd = XmlUtils.parse(xsdFilePath);
				imports.put(namespace, typesXsd);
				imports = loadImportXsdFiles(typesXsd, parentFile, imports, xPath);
			}
		}
		return imports;
	}

	public void isDuplicateElement(String elementName, Set<String> duplicateElements, Document xsdDocument, XPath xPath) throws XPathExpressionException {
		NodeList nodeList = (NodeList) xPath.compile("//*[@name='" + elementName + "']").evaluate(xsdDocument, XPathConstants.NODESET);
		if (nodeList.getLength() > 1) {
			String firstType = ((Element) nodeList.item(0)).getAttribute(TYPE);
			for (int i = 1; i < nodeList.getLength(); i++) {
				Element element = (Element) nodeList.item(i);
				String type = element.getAttribute(TYPE);
				if (!Objects.equals(firstType, type)) {
					duplicateElements.add(elementName);
				}
			}
		}
	}

	public Set<String> getXsdObjects(String objectsXPath, Document typesXsdDocument, Set<String> duplicateElements, XPath xPath) throws XPathExpressionException {
		Set<String> xsdObjects = new HashSet<>();
		NodeList objectList = (NodeList) xPath.compile(objectsXPath).evaluate(typesXsdDocument, XPathConstants.NODESET);
		for (int i = 0; i < objectList.getLength(); i++) {
			Element element = (Element) objectList.item(i);
			String name = element.getAttribute(NAME);
			isDuplicateElement(name, duplicateElements, typesXsdDocument, xPath);
			xsdObjects.add(name);
		}
		return xsdObjects;
	}

	public void prepareChildNodes(NodeList nodeList, String nodeType, XsdMetadata xsd) throws XPathExpressionException {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element childElement = (Element) nodeList.item(i);
			String name = childElement.getAttribute(NAME);
			String type = childElement.getAttribute(TYPE);
			boolean hasNamespace = type.split(":").length > 1;
			String namespace = null;
			String elementType;
			boolean isSimpleType = false;
			if (type.isEmpty()) {
				elementType = (String) xsd.getXPath().compile(String.format(ELE_BASE_XPATH, nodeType, name)).evaluate(xsd.getTypesXsdDocument(), XPathConstants.STRING);
				isSimpleType = true;
			} else {
				if (hasNamespace) {
					namespace = type.split(":")[0];
					elementType = type.split(":")[1];
				} else {
					elementType = type;
					isSimpleType = true;
				}
			}
			String maxOccurs = childElement.getAttribute(MAX_OCCURS);
			String minOccurs = childElement.getAttribute(MIN_OCCURS);
			boolean isRequired = (maxOccurs.isEmpty() || maxOccurs.equalsIgnoreCase(NUMBER_1)) && (minOccurs.isEmpty() || minOccurs.equalsIgnoreCase(NUMBER_1));

			if (!isRequired) {
				xsd.getArrayElements().add(name);
			}
			if (xsd.getSimpleObjects().contains(elementType) || isSimpleType || type.contains("xsd:")) {
				xsd.getSimpleElements().put(name, elementType);
			} else if (xsd.getComplexObjects().contains(elementType)) {
				xsd.getComplexElements().put(name, elementType);
			} else if (!namespace.equalsIgnoreCase(xsd.getNamespace())) {
				xsd.getForeignElements().put(name, elementType);
				xsd.getForeignElementsNs().put(name, namespace);
			}
		}
	}

	public Set<String> getSimpleXsdObjects(Document typesXsdDocument, XPath xPath, Set<String> simpleObjects) throws XPathExpressionException {
		NodeList objectList = (NodeList) xPath.compile(SIMPLE_XSD_OBJECTS_XPATH).evaluate(typesXsdDocument, XPathConstants.NODESET);
		for (int i = 0; i < objectList.getLength(); i++) {
			Element element = (Element) objectList.item(i);
			simpleObjects.add(element.getAttribute(TYPE).split(":")[1]);
		}
		return simpleObjects;
	}

	public XsdMetadata getXsdElements(String nodeType, String nodeNs, XsdMetadata xsdMetadata) throws Exception {
		XsdMetadata xsd = new XsdMetadata(xsdMetadata.getPluginConfig(), xsdMetadata.getXPath(), xsdMetadata.getTypesXsdDocument(), xsdMetadata.getImportXsd());
		if (nodeNs == null) {
			xsd.setNamespace(xsdMetadata.getNamespace());
		} else {
			xsd.setNamespace(nodeNs);
		}
		xsd.setDuplicateElements(xsdMetadata.getDuplicateElements());
		xsd.setObjectFactory(xsdMetadata.getObjectFactory());
		xsd.setFormioSchemaTyp(xsdMetadata.getFormioSchemaTyp());
		xsd.setXmlDocument(xsdMetadata.getXmlDocument());

		if (nodeNs == null) {
			xsd.setSimpleObjects(xsdMetadata.getSimpleObjects());
			xsd.setComplexObjects(xsdMetadata.getComplexObjects());
			xsd.setForeignObjects(xsdMetadata.getForeignObjects());
		} else {
			xsd.setSimpleObjects(getXsdObjects(SIMPLE_OBJECTS_XPATH, xsd.getTypesXsdDocument(), xsd.getDuplicateElements(), xsd.getXPath()));
			xsd.setSimpleObjects(getSimpleXsdObjects(xsd.getTypesXsdDocument(), xsd.getXPath(), xsd.getSimpleObjects()));

			xsd.setComplexObjects(getXsdObjects(COMPLEX_OBJECTS_XPATH, xsd.getTypesXsdDocument(), xsd.getDuplicateElements(), xsd.getXPath()));
			xsd.setForeignObjects(getXsdObjects(String.format(FOREIGN_OBJECTS_XPATH, nodeNs.concat(":")), xsd.getTypesXsdDocument(), xsd.getDuplicateElements(), xsd.getXPath()));

			xsd.setSimpleElements(getXsdElements(SIMPLE_OBJECTS_XPATH, xsd.getTypesXsdDocument(), xsd.getXPath()));
			xsd.setComplexElements(getXsdElements(COMPLEX_OBJECTS_XPATH, xsd.getTypesXsdDocument(), xsd.getXPath()));
		}
		xsd.setCodelistItemElements(getXsdObjects(REG_CODELIST_ITEM_XPATH, xsd.getTypesXsdDocument(), xsd.getDuplicateElements(), xsd.getXPath()));
		xsd.setCodelistItemOptionalElements(getXsdObjects(REG_CODELIST_ITEM_OPTIONAL_XPATH, xsd.getTypesXsdDocument(), xsd.getDuplicateElements(), xsd.getXPath()));

		Element extensionElement = (Element) xsdMetadata.getXPath().compile(String.format(EXTENSION_XPATH, nodeType)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);
		if (extensionElement != null) {
			String extensionBase = extensionElement.getAttribute(BASE);
			String extensionNs = extensionBase.split(":")[0];
			String extensionType = extensionBase.split(":")[1];
			if(!extensionType.equals("string"))
			{
				NodeList nodeList = (NodeList) xsd.getXPath().compile(String.format(CHILD_ELEMENTS_XPATH, extensionType)).evaluate(getTypesXsdFile(extensionNs, xsdMetadata), XPathConstants.NODESET);
				prepareChildNodes(nodeList, nodeType, xsd);
			}
		}

		NodeList childElements = (NodeList) xsd.getXPath().compile(String.format(CHILD_ELEMENTS_XPATH, nodeType)).evaluate(xsd.getTypesXsdDocument(), XPathConstants.NODESET);
		prepareChildNodes(childElements, nodeType, xsd);
		return xsd;
	}

	public Map<String, String> getXsdElements(String objectsXPath, Document typesXsdDocument, XPath xPath) throws XPathExpressionException {
		Map<String, String> xsdElements = new HashMap<>();
		NodeList objectList = (NodeList) xPath.compile(objectsXPath).evaluate(typesXsdDocument, XPathConstants.NODESET);
		for (int i = 0; i < objectList.getLength(); i++) {
			Element element = (Element) objectList.item(i);
			String type = element.getAttribute(TYPE);
			if (!type.isEmpty()) {
				type = type.contains(":") ? type.split(":")[1] : type;
			}
			xsdElements.put(element.getAttribute(NAME), type);
		}
		return xsdElements;
	}

	public XsdMetadata prepareXsdMetadata(Element root, AbstractRegPlugin registerPlugin, XPath xPath) throws Exception {
		return PerformaceUtils.measure("prepareXmlMetadata", () -> {
			try {
				String registerId = registerPlugin.getFullInternalRegisterId();
				XsdFile primaryXsdFile = registerPlugin.getPluginConfig().getXsdFile().stream().filter(XsdFile::isIsPrimary).findFirst().orElse(null);
				if (primaryXsdFile == null) {
					throw new CommonException(HttpStatus.NOT_FOUND, "Pre register '" + registerPlugin.getInfo().getName() + "' (" + registerId + ") neexistuje XSD súbor na validáciu", null);
				}
				String registerTypesXsd = registerId.concat("_TYPES.xsd");
				XsdFile typesXsdFile = registerPlugin.getPluginConfig().getXsdFile().stream().filter(xsdFile -> xsdFile.getPath().contains(registerTypesXsd)).findFirst().orElse(null);
				if (typesXsdFile == null) {
					throw new CommonException(HttpStatus.NOT_FOUND, "Pre register '" + registerPlugin.getInfo().getName() + "' (" + registerId + ") neexistuje XSD súbor na validáciu", null);
				}
				File primaryXsd = new File(registersDir, primaryXsdFile.getPath());
				File typesXsd = new File(registersDir, typesXsdFile.getPath());

				Document xsdDocument = XmlUtils.parse(primaryXsd);
				Document typesXsdDocument = XmlUtils.parse(typesXsd);
				File currentDirectory = primaryXsd.getParentFile();
				Element xsdRootElement;

				if (root == null) {
					xsdRootElement = getRootElement(null, xsdDocument, xPath);
				} else {
					xsdRootElement = getRootElement(getNodeName(root.getNodeName()), xsdDocument, xPath);
				}

				String rootName = xsdRootElement.getAttribute(NAME);
				String rootType = xsdRootElement.getAttribute(TYPE);
				String rootNamespace = rootType.split(":")[0];
				String rootElementType = rootType.split(":")[1];

				Map<String, Document> importXsd = loadImportXsdFiles(typesXsdDocument, currentDirectory, new HashMap<>(), xPath);
				XsdMetadata xsdMetadata = new XsdMetadata(registerPlugin.getPluginConfig(), xPath, typesXsdDocument, importXsd);

				xsdMetadata = getXsdElements(rootElementType, rootNamespace, xsdMetadata);
				xsdMetadata.setName(rootName);
				xsdMetadata.setType(rootType);
				xsdMetadata.setElementType(rootElementType);
				return xsdMetadata;
			} catch (Exception e) {
				throw toException("Chyba pri čítaní XSD metadát pre register " + registerPlugin.getFullInternalRegisterId(), e);
			}
		});
	}

	public String getPattern(String elementXPath, XsdMetadata xsdMetadata) throws Exception {
		String fullPattern = null;

		NodeList patterns = (NodeList) xsdMetadata.getXPath().compile(elementXPath).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODESET);
		for (int i = 0; i < patterns.getLength(); i++) {

			Element pattern = (Element) patterns.item(i);
			String value = pattern.getAttribute(VALUE);

			fullPattern = fullPattern == null ? "(".concat(value).concat(")") : fullPattern.concat("(".concat(value).concat(")"));
			if (i < patterns.getLength() - 1) {
				fullPattern = fullPattern.concat("|");
			}
		}
		return fullPattern;
	}

	public InputType processInputType(InputType inputType, FormioFieldType formioField, FormioSchemaTyp formioSchemaTyp) {
		if (formioField != null) {
			if (formioSchemaTyp.equals(FormioSchemaTyp.CREATE) && formioField.isIsGenerated()) {
				inputType.setHidden(true);
			}
			if (formioSchemaTyp.equals(FormioSchemaTyp.UPDATE) && (formioField.isIsGenerated() || !formioField.isIsUpdatable())) {
				inputType.setDisabled(true);
			}
			if (formioField.getDefaultValue() != null) {
				inputType.setDefaultValue(formioField.getDefaultValue());
				if (inputType.isHidden()) {
					inputType.setCalculateValue("value='" + formioField.getDefaultValue() + "'");
				}
			}
		}
		return inputType;
	}

	public ValidateType createValidateType(Integer minLength, Integer maxLength, Boolean isRequired, String pattern, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		ValidateType vt = xsdMetadata.getObjectFactory().createValidateType();
		vt.setMinLength(minLength);
		vt.setMaxLength(maxLength);
		vt.setRequired(isRequired);
		vt.setPattern(pattern);
		vt.setCustomMessage(pattern != null ? INVALID_SUBJECT_ID_FORMAT : INVALID_DATA_FORMAT);

		if (formioField != null && formioField.getCustom() != null) {
			for (CustomFormioExpressionType custom : formioField.getCustom()) {
				if (custom.getMark().equalsIgnoreCase(VALID)) {
					if (custom.getCustom() != null) {
						if (custom.isAs()) {
							vt.setCustom(custom.getMark().toLowerCase().concat(" = ").concat(custom.getCustom()));
						} else {
							vt.setCustom(custom.getMark().toLowerCase().concat(" = !").concat(custom.getCustom()));
						}
					} else {
						if (custom.isAs()) {
							vt.setCustom(custom.getMark().toLowerCase().concat(" = ")
									.concat(custom.getWhenField().concat(" == '").concat(custom.getEquals().concat("' ? ").concat(String.valueOf(custom.isAs()).concat(" : \"").concat(custom.getErrorMessage().concat("\""))))));
						} else {
							vt.setCustom(custom.getMark().toLowerCase().concat(" = !(")
									.concat(custom.getWhenField().concat(" == '").concat(custom.getEquals().concat("') ? ").concat(String.valueOf(!custom.isAs()).concat(" : \"").concat(custom.getErrorMessage().concat("\""))))));
						}
					}
				}
			}
		}
		return vt;
	}

	public ComponentType createSelect(String name, String parentNamePath, String url, NodeList enumerationElements, boolean isRequired, String pattern, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		Select s = xsdMetadata.getObjectFactory().createSelect();
		s.setType(ComponentTypeType.SELECT);
		s.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, s.getClass().getSimpleName()));
		s.setLabel(localizeLabel(parentNamePath, name));
		s.setKey(name);
		s.setInput(true);
		s.setData(new SelectDataType());
		s.setDataSrc("values");
		s.setValidate(createValidateType(null, null, isRequired, pattern, xsdMetadata, formioField));
		s = (Select) processInputType(s, formioField, xsdMetadata.getFormioSchemaTyp());

		if ((s.isDisabled() != null && s.isDisabled()) || url != null) {
			RegisterEntryField entryField = xsdMetadata.getPluginConfig().getField().stream().filter(field -> field.getXPathValue().equalsIgnoreCase(url)).findFirst().orElse(null);
			if (entryField != null && entryField.getEnumeration() != null) {
				s.setDataSrc("url");
				s.setSearchField("search");
				s.setSelectValues("result");
				s.setIdPath(ITEM_CODE);
				s.setValueProperty(ITEM_CODE);

				s.setSelectThreshold((float) 0.3);
				s.setTemplate("<span>{{ item.itemName }}</span>");
				enumValuesUrl = enumValuesUrl.trim();
				enumValuesUrl = enumValuesUrl.charAt(enumValuesUrl.length() - 1) == '/' ? enumValuesUrl : enumValuesUrl + "/";
				s.getData().setUrl(enumValuesUrl + entryField.getEnumeration().getCodelistCode());
				SelectDataHeaderType header = new SelectDataHeaderType();
				header.setKey("authorization");
				header.setValue("{{form.token}}");
				s.getData().getHeaders().add(header);
				s.getData().getHeaders().add(new SelectDataHeaderType());
			}

		} else if (enumerationElements != null) {
			for (int i = 0; i < enumerationElements.getLength(); i++) {
				Element enumElement = (Element) enumerationElements.item(i);
				SelectDataValueType dataValueType = new SelectDataValueType();
				dataValueType.setLabel(localizeLabel(parentNamePath.concat(".").concat(enumElement.getAttribute(VALUE)), enumElement.getAttribute(VALUE)));
				dataValueType.setValue(enumElement.getAttribute(VALUE));
				s.getData().getValues().add(dataValueType);
			}
		}
		return s;
	}

	public ComponentType createDatetime(String name, String parentNamePath, String formioDatetimeFormat, boolean isRequired, String pattern, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		Datetime dat = createDatetime(xsdMetadata.getObjectFactory());
		dat.setLabel(localizeLabel(parentNamePath, name));
		dat.setFormat(formioDatetimeFormat);
		dat.setKey(name);
		dat.setValidate(createValidateType(null, null, isRequired, pattern, xsdMetadata, formioField));
		dat = (Datetime) processInputType(dat, formioField, xsdMetadata.getFormioSchemaTyp());

		DatetimeWidgetType dw = dat.getWidget();
		dw.setFormat(formioDatetimeFormat);

		if (formioDatetimeFormat.equalsIgnoreCase(FORMIO_DATE_FORMAT)) {
			dat.setEnableTime(false);
			dw.setEnableTime(false);
		} else {
			dat.setEnableTime(true);
			dw.setEnableTime(true);
		}
		return dat;
	}

	public Datetime createDatetime(ObjectFactory objectFactory) {
		Datetime dat = objectFactory.createDatetime();
		dat.setType(ComponentTypeType.DATETIME);
		dat.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, dat.getClass().getSimpleName()));
		dat.setUseLocaleSettings(true);
		DatetimeWidgetType dw = objectFactory.createDatetimeWidgetType();
		dat.setInput(true);
		dat.setWidget(dw);
		dw.setUseLocaleSettings(true);
		dw.setTime24Hr(true);
		return dat;
	}

	public ComponentType createCheckbox(String name, String parentNamePath, String pattern, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		Checkbox cb = xsdMetadata.getObjectFactory().createCheckbox();
		cb.setType(ComponentTypeType.CHECKBOX);
		cb.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, cb.getClass().getSimpleName()));
		cb.setLabel(localizeLabel(parentNamePath, name));
		cb.setKey(name);
		cb.setInput(true);
		cb.setValidate(createValidateType(null, null, null, pattern, xsdMetadata, formioField));
		cb = (Checkbox) processInputType(cb, formioField, xsdMetadata.getFormioSchemaTyp());
		return cb;
	}

	public ComponentType createTextarea(String name, String parentNamePath, Integer minLength, Integer maxLength, boolean isRequired, String pattern, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		Textarea ta = xsdMetadata.getObjectFactory().createTextarea();
		ta.setType(ComponentTypeType.TEXTAREA);
		ta.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, ta.getClass().getSimpleName()));
		ta.setLabel(localizeLabel(parentNamePath, name));
		ta.setKey(name);
		ta.setInput(true);
		ta.setAutoExpand(true);
		ta.setValidate(createValidateType(minLength, maxLength, isRequired, pattern, xsdMetadata, formioField));
		ta = (Textarea) processInputType(ta, formioField, xsdMetadata.getFormioSchemaTyp());
		return ta;
	}

	public ComponentType createTextField(String name, String parentNamePath, Integer minLength, Integer maxLength, boolean isRequired, String pattern, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		Textfield tf = xsdMetadata.getObjectFactory().createTextfield();
		tf.setType(ComponentTypeType.TEXTFIELD);
		tf.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, tf.getClass().getSimpleName()));
		tf.setLabel(localizeLabel(parentNamePath, name));
		tf.setKey(name);
		tf.setInput(true);
		tf.setValidate(createValidateType(minLength, maxLength, isRequired, pattern, xsdMetadata, formioField));
		tf = (Textfield) processInputType(tf, formioField, xsdMetadata.getFormioSchemaTyp());
		return tf;
	}

	public ComponentType processSimpleObject(String elementType, String elementName, String parentNamePath, String elementXPath, boolean isRequired, XsdMetadata xsdMetadata, FormioFieldType formioField) throws Exception {
		String pattern;
		String baseType = (String) xsdMetadata.getXPath().compile(elementXPath.concat("/@base")).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.STRING);
		if (!baseType.isEmpty()) {
			elementType = baseType.split(":").length > 1 ? baseType.split(":")[1] : baseType;
		}
		pattern = getPattern(elementXPath.concat("/pattern"), xsdMetadata);

		if (elementType.equalsIgnoreCase("string")) {

			NodeList enumerationElements = (NodeList) xsdMetadata.getXPath().compile(elementXPath.concat("/enumeration")).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODESET);
			if (enumerationElements.getLength() > 0) {
				return createSelect(elementName, parentNamePath, null, enumerationElements, isRequired, pattern, xsdMetadata, formioField);
			}

			Double minLengthValue = ((Double) xsdMetadata.getXPath().compile(elementXPath.concat("/minLength/@value")).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NUMBER));
			Double maxLengthValue = ((Double) xsdMetadata.getXPath().compile(elementXPath.concat("/maxLength/@value")).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NUMBER));
			Integer minLength = minLengthValue.isNaN() ? null : minLengthValue.intValue();
			Integer maxLength = maxLengthValue.isNaN() ? null : maxLengthValue.intValue();

			if (maxLength == null || maxLength <= 255) {
				return createTextField(elementName, parentNamePath, minLength, maxLength, isRequired, pattern, xsdMetadata, formioField);
			}
			return createTextarea(elementName, parentNamePath, minLength, maxLength, isRequired, pattern, xsdMetadata, formioField);
		}
		if (elementType.equalsIgnoreCase(BOOLEAN)) {
			return createCheckbox(elementName, parentNamePath, pattern, xsdMetadata, formioField);
		}
		if (elementType.equalsIgnoreCase(DATE)) {
			return createDatetime(elementName, parentNamePath, FORMIO_DATE_FORMAT, isRequired, pattern, xsdMetadata, formioField);
		}
		if (elementType.equalsIgnoreCase(TIME)) {
			return createDatetime(elementName, parentNamePath, FORMIO_DATETIME_FORMAT, isRequired, pattern, xsdMetadata, formioField);
		}
		if (elementType.equalsIgnoreCase(DATE_TIME)) {
			return createDatetime(elementName, parentNamePath, FORMIO_DATETIME_FORMAT, isRequired, pattern, xsdMetadata, formioField);
		}
		return createTextField(elementName, parentNamePath, null, null, isRequired, pattern, xsdMetadata, formioField);
	}

	public Document getTypesXsdFile(String nodeNamespace, XsdMetadata xsdMetadata) throws Exception {
		Element schema = (Element) xsdMetadata.getXPath().compile(SCHEMA).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);
		if (schema != null) {
			return xsdMetadata.getImportXsd().get(schema.getAttribute(String.format(XMLNS_XPATH, nodeNamespace)));
		}
		return null;
	}

	public ComponentType createNoneContainer(ObjectFactory objectFactory) {
		Container c = objectFactory.createContainer();
		c.setType(ComponentTypeType.NONE);
		return c;
	}

	public Panel createPanel(ObjectFactory objectFactory) {
		Panel p = objectFactory.createPanel();
		p.setType(ComponentTypeType.PANEL);
		p.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, p.getClass().getSimpleName()));
		p.setCollapsible(true);
		p.setCollapsed(true);
		p.getComponents().add(createNoneContainer(objectFactory));
		return p;
	}

	public void processItemWithHistoryExtension(Container container, XsdMetadata xsdMetadata) {
		Panel panel = createPanel(xsdMetadata.getObjectFactory());
		panel.setTitle("Temporálne informácie");
		for (ComponentType component : container.getComponents()) {
			component.setCustomClass(String.format(FORMIO_CUSTOM_CLASS_HIST, component.getClass().getSimpleName()));
		}
		panel.getComponents().addAll(container.getComponents());
		container.getComponents().clear();
		container.getComponents().add(panel);
	}

	public FormioFieldType findFormioField(String elementName, String parentNamePath, XsdMetadata xsdMetadata) {
		String parentPath = parentNamePath.contains(".") ? parentNamePath : "";
		return xsdMetadata.getPluginConfig().getFormioField().stream().filter(field -> field.getName().equals(elementName) && field.getPath().equals(parentPath)).findFirst().orElse(null);
	}

	public boolean checkNestingDepth(String typePath, String parentType, String elementNsType, String parentXPath, int formioNestingDepth) {
		List<String> typePaths = Arrays.asList(typePath.split("/"));
		long count = typePaths.stream().filter(type -> type.equals(elementNsType)).count();

		String[] paths = parentXPath.split("/");
		String actNs = paths[paths.length - 1];
		String parent = actNs.concat(parentType);

		boolean maxNesting = false;

		if (count >= formioNestingDepth) {

			int occurrences = 0;
			List<Integer> indexes = new ArrayList<>();
			for (int j = 0; j < typePaths.size(); j++) {
				if (typePaths.get(j).equals(elementNsType)) {
					indexes.add(j);
				}
			}
			for (Integer index : indexes) {
				if (index - 1 >= 0) {
					String type = typePaths.get(index - 1);
					if (type.equals(parent) && ++occurrences == formioNestingDepth) {
						maxNesting = true;
						break;
					}
				}
			}
		}
		return maxNesting;
	}

	public String getParentNs(String parentXPath) {
		String[] paths = parentXPath.split("/");
		if (paths.length > 1) {
			return paths[paths.length - 1];
		}
		return paths[0];
	}

	public Datagrid createDatagrid(ObjectFactory objectFactory) {
		Datagrid d = objectFactory.createDatagrid();
		d.setType(ComponentTypeType.DATAGRID);
		d.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, d.getClass().getSimpleName()));
		d.setAddAnotherPosition(BOTTOM);
		d.setInput(true);
		d.setAddAnother(ADD_SK);
		d.setInitEmpty(true);
		d.getComponents().add(createNoneContainer(objectFactory));
		return d;
	}

	public ComponentType createPanelDatagrid(String name, String parentNamePath, Datagrid d, boolean isArray, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		final String label = localizeLabel(parentNamePath, name);
		Panel p = createPanel(xsdMetadata.getObjectFactory());
		p.setLabel(label);
		p.setTitle(label);
		p.setKey(parentNamePath);
		p = (Panel) processFormioField(p, formioField, xsdMetadata.getObjectFactory());

		d.setLabel(label);
		d.setKey(name);
		ValidateType vt = createValidateType(null, null, false, null, xsdMetadata, formioField);
		vt.setRequired(false);
		if (!isArray) {
			vt.setMaxLength(1);
		}
		d.setValidate(vt);
		d.getComponents().forEach(c -> c.setHideLabel(true));

		p.getComponents().add(d);
		return p;
	}

	public ComponentType processFormioField(ComponentType componentType, FormioFieldType formioField, ObjectFactory objectFactory) {
		if (formioField != null && formioField.getCustom() != null) {
			for (CustomFormioExpressionType custom : formioField.getCustom()) {
				if (custom.getMark().equalsIgnoreCase(SHOW)) {
					if (custom.getCustom() != null) {
						if (custom.isAs()) {
							componentType.setCustomConditional(custom.getMark().toLowerCase().concat(" = ").concat(custom.getCustom()));
						} else {
							componentType.setCustomConditional(custom.getMark().toLowerCase().concat(" = !(").concat(custom.getCustom()).concat(")"));
						}
					} else {
						ConditionalType ct = objectFactory.createConditionalType();
						ct.setShow(String.valueOf(custom.isAs()));
						ct.setWhen(custom.getWhenField());
						ct.setEq(custom.getEquals());
						componentType.setConditional(ct);
					}
				}
			}
		}
		return componentType;
	}

	public Container createContainer(ObjectFactory objectFactory) {
		Container c = objectFactory.createContainer();
		c.setCustomClass(String.format(FORMIO_CUSTOM_CLASS, c.getClass().getSimpleName()));
		c.getComponents().add(createNoneContainer(objectFactory));
		return c;
	}

	public ComponentType createPanelContainer(String name, String parentNamePath, Container c, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		final String label = localizeLabel(parentNamePath, name);
		Panel p = createPanel(xsdMetadata.getObjectFactory());
		p.setCollapsible(false);
		p.setCollapsed(false);
		p.setLabel(label);
		p.setTitle(label);
		p.setKey(parentNamePath);
		p = (Panel) processFormioField(p, formioField, xsdMetadata.getObjectFactory());

		c.setType(ComponentTypeType.CONTAINER);
		c.setKey(name);
		c.setInput(true);
		p.getComponents().add(c);
		return p;
	}

	public ComponentType createPanelDatagridContainer(String name, String parentNamePath, Container c, boolean isArray, XsdMetadata xsdMetadata, FormioFieldType formioField) {
		final String label = localizeLabel(parentNamePath, name);
		Panel p = createPanel(xsdMetadata.getObjectFactory());
		p.setLabel(label);
		p.setTitle(label);
		p.setKey(parentNamePath);
		p = (Panel) processFormioField(p, formioField, xsdMetadata.getObjectFactory());

		Datagrid d = createDatagrid(xsdMetadata.getObjectFactory());
		d.setLabel(label);
		d.setKey(name);
		ValidateType vt = createValidateType(null, null, false, null, xsdMetadata, formioField);
		vt.setRequired(false);
		if (!isArray) {
			vt.setMaxLength(1);
		}
		d.setValidate(vt);

		c.setType(ComponentTypeType.CONTAINER);
		c.setKey(name);
		c.setInput(true);
		d.getComponents().add(c);
		p.getComponents().add(d);
		return p;
	}

	private ComponentType processForeignObject(String namespace, String elementType, String elementName, String parentNamePath, String elementXPath, boolean isRequired, XsdMetadata xsdMetadata, FormioFieldType formioField, String typePath) throws Exception {
		Document typesXsd = getTypesXsdFile(namespace, xsdMetadata);
		XsdMetadata xsd = getXsdElements(elementType, namespace, new XsdMetadata(typesXsd, xsdMetadata.getDuplicateElements(), xsdMetadata.getImportXsd(), xsdMetadata.getXPath(), xsdMetadata.getXmlDocument(), xsdMetadata.getObjectFactory(), xsdMetadata.getPluginConfig(), xsdMetadata.getFormioSchemaTyp()));
		ComponentType component;
		String restrictionXPath = String.format(RESTRICTION_XPATH, elementType);

		if (xsd.getSimpleObjects().contains(elementType)) {
			if (isRequired) {
				Container container = createContainer(xsdMetadata.getObjectFactory());
				container.getComponents().add(processSimpleObject(elementType, elementName, parentNamePath, restrictionXPath, true, xsd, formioField));
				component = container;
			} else {
				Datagrid datagrid = createDatagrid(xsdMetadata.getObjectFactory());
				datagrid.getComponents().add(processSimpleObject(elementType, elementName, parentNamePath, restrictionXPath, false, xsd, formioField));
				component = datagrid;
			}
		} else {
			component = processComplexObject(createContainer(xsdMetadata.getObjectFactory()), elementType, parentNamePath, elementXPath, isRequired, xsd, formioField, typePath);
			component.setType(ComponentTypeType.CONTAINER);
		}
		return component;
	}

	private Container processComplexObject(Container container, String parentType, String parentNamePath, String parentXPath, Boolean isRequired, XsdMetadata xsdMetadata, FormioFieldType formioField, String typePath) throws Exception {
		NodeList extensions = (NodeList) xsdMetadata.getXPath().compile(String.format(EXTAND_ATT_XPATH, parentType, parentType)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODESET);
		for (int i = 0; i < extensions.getLength(); i++) {

			Element extension = (Element) extensions.item(i);
			String extensionBase = !extension.getAttribute(BASE).isEmpty() ? extension.getAttribute(BASE) : extension.getAttribute(REF);
			String extensionNs = extensionBase.split(":")[0];
			String extensionType = extensionBase.split(":")[1];

			if (xsdMetadata.getSimpleObjects().contains(extensionType) || extensionNs.contains(XS_NS)) {
				String[] namePath = parentNamePath.split("\\.");
				String name = namePath[namePath.length - 1];
				container.getComponents().add(processSimpleObject(extensionType, name, parentNamePath, String.format(RESTRICTION_XPATH, extensionType), isRequired, xsdMetadata, formioField));
			} else {
				Document typesXsd = getTypesXsdFile(extensionNs, xsdMetadata);
				XsdMetadata xsd = getXsdElements(extensionType, extensionNs, new XsdMetadata(typesXsd, xsdMetadata.getDuplicateElements(), xsdMetadata.getImportXsd(), xsdMetadata.getXPath(), xsdMetadata.getXmlDocument(), xsdMetadata.getObjectFactory(), xsdMetadata.getPluginConfig(), xsdMetadata.getFormioSchemaTyp()));
				container = processComplexObject(container, extensionType, parentNamePath, parentXPath, isRequired, xsd, formioField, typePath);
				if (extensionType.equals(REG_ITEM_WITH_HISTORY)) {
					processItemWithHistoryExtension(container, xsdMetadata);
				}
			}
		}

		NodeList childElements = (NodeList) xsdMetadata.getXPath().compile(String.format(CHILD_ELEMENTS_XPATH, parentType)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODESET);
		for (int i = 0; i < childElements.getLength(); i++) {

			Element childElement = (Element) childElements.item(i);
			String elementName = childElement.getAttribute(NAME);
			String elementNsType = childElement.getAttribute(TYPE);

			boolean hasNamespace = elementNsType.split(":").length > 1;
			String elementNs = null;
			String elementType;
			boolean isSimpleType = false;
			if (elementNsType.isEmpty()) {
				elementType = (String) xsdMetadata.getXPath().compile(String.format(ELE_BASE_XPATH, parentType, elementName)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.STRING);
				isSimpleType = true;
			} else {
				if (hasNamespace) {
					elementNs = elementNsType.split(":")[0];
					elementType = elementNsType.split(":")[1];
				} else {
					elementType = elementNsType;
					isSimpleType = true;
				}
			}

			String parentName = parentNamePath.concat(".").concat(elementName);
			String maxOccurs = childElement.getAttribute(MAX_OCCURS);
			String minOccurs = childElement.getAttribute(MIN_OCCURS);
			String use = childElement.getAttribute(USE);

			boolean isArray = !maxOccurs.isEmpty() && !maxOccurs.equalsIgnoreCase(NUMBER_1);
			isRequired = ((maxOccurs.isEmpty() || maxOccurs.equalsIgnoreCase(NUMBER_1)) && (minOccurs.isEmpty() || minOccurs.equalsIgnoreCase(NUMBER_1))) || (use.equalsIgnoreCase(REQUIRED));

			formioField = findFormioField(elementName, parentNamePath, xsdMetadata);
			String restrictionXPath = (isSimpleType) ? String.format(ELE_RESTRICTION_XPATH, parentType, elementName, elementType) : String.format(RESTRICTION_XPATH, elementType);

			if (elementNs != null && checkNestingDepth(typePath, parentType, elementNsType, parentXPath, formioNestingDepth)) {
				continue;
			}

			String elementTypePath = typePath.concat(elementNsType).concat("/");
			String elementXPath = parentXPath.concat(elementName).concat("/");
			String xPath = elementNs != null ? elementXPath.concat(elementNs).concat(":") : elementXPath.concat(getParentNs(parentXPath));

			if ((xsdMetadata.getCodelistItemElements().contains(elementName) || xsdMetadata.getCodelistItemOptionalElements().contains(elementName)) && elementType.startsWith(REG_CODELIST_ITEM)) {
				if (elementType.equals(REG_CODELIST_ITEM_T)) {
					ComponentType select = createSelect(elementName, parentName, elementXPath.concat(RB_ITEM_CODE), null, isRequired, null, xsdMetadata, formioField);
					if (isRequired) {
						container.getComponents().add(select);
					} else {
						Datagrid datagrid = createDatagrid(xsdMetadata.getObjectFactory());
						datagrid.getComponents().add(select);
						container.getComponents().add(createPanelDatagrid(elementName, parentName, datagrid, isArray, xsdMetadata, formioField));
					}
				} else if (elementType.equals(REG_CODELIST_ITEM_OPTIONAL_T)) {
					ComponentType select = createSelect(ITEM_CODE, ITEM_CODE, elementXPath.concat(RB_ITEM_CODE), null, false, null, xsdMetadata, formioField);
					ComponentType textField = createTextField(ITEM_VALUE, ITEM_VALUE, 1, 255, false, null, xsdMetadata, formioField);
					Container c = createContainer(xsdMetadata.getObjectFactory());
					c.setKey(elementName);
					c.getComponents().add(select);
					c.getComponents().add(textField);

					if (isRequired) {
						container.getComponents().add(createPanelContainer(elementName, parentName, c, xsdMetadata, formioField));
					} else {
						container.getComponents().add(createPanelDatagridContainer(elementName, parentName, c, isArray, xsdMetadata, formioField));
					}
				}
			} else if (xsdMetadata.getSimpleObjects().contains(elementType) || isSimpleType || (elementNs != null && elementNs.contains(XS_NS))) {
				if (isRequired) {
					container.getComponents().add(processSimpleObject(elementType, elementName, parentName, restrictionXPath, true, xsdMetadata, formioField));
				} else {
					Datagrid datagrid = createDatagrid(xsdMetadata.getObjectFactory());
					datagrid.getComponents().add(processSimpleObject(elementType, elementName, parentName, restrictionXPath, false, xsdMetadata, formioField));
					container.getComponents().add(createPanelDatagrid(elementName, parentName, datagrid, isArray, xsdMetadata, formioField));
				}
			} else if (xsdMetadata.getComplexObjects().contains(elementType)) {
				Container newContainer = processComplexObject(createContainer(xsdMetadata.getObjectFactory()), elementType, parentName, xPath, isRequired, xsdMetadata, formioField, elementTypePath);
				if (isRequired) {
					container.getComponents().add(createPanelContainer(elementName, parentName, newContainer, xsdMetadata, formioField));
				} else {
					container.getComponents().add(createPanelDatagridContainer(elementName, parentName, newContainer, isArray, xsdMetadata, formioField));
				}
			} else {
				ComponentType component = processForeignObject(elementNs, elementType, elementName, parentName, xPath, isRequired, xsdMetadata, formioField, elementTypePath);
				if (isRequired) {
					Container newContainer = (Container) component;
					if (newContainer.getType() == null) {
						container.getComponents().addAll(newContainer.getComponents());
					} else {
						container.getComponents().add(createPanelContainer(elementName, parentName, newContainer, xsdMetadata, formioField));
					}
				} else {
					if (component.getType() == null || component.getType().equals(ComponentTypeType.CONTAINER)) {
						Container newContainer = (Container) component;
						container.getComponents().add(createPanelDatagridContainer(elementName, parentName, newContainer, isArray, xsdMetadata, formioField));
					} else {
						Datagrid datagrid = (Datagrid) component;
						container.getComponents().add(createPanelDatagrid(elementName, parentName, datagrid, isArray, xsdMetadata, formioField));
					}
				}
			}
		}

		NodeList childAttributes = (NodeList) xsdMetadata.getXPath().compile(String.format(ATTRIBUTE_XPATH, parentType)).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODESET);
		for (int i = 0; i < childAttributes.getLength(); i++) {

			Element childAttribute = (Element) childAttributes.item(i);
			String attributeName = childAttribute.getAttribute(NAME);
			String attributeNsType = childAttribute.getAttribute(TYPE);
			String maxOccurs = childAttribute.getAttribute(MAX_OCCURS);
			String minOccurs = childAttribute.getAttribute(MIN_OCCURS);
			String use = childAttribute.getAttribute(USE);
			String attributeXPath = String.format(ATT_RESTRICTION_XPATH, parentType, attributeName);
			formioField = findFormioField(attributeName, parentNamePath, xsdMetadata);

			if (attributeNsType != null && !attributeNsType.isEmpty()) {

				boolean hasNamespace = attributeNsType.split(":").length > 1;
				boolean isSimpleType = false;
				String attributeNs = null;
				String attributeType;
				if (hasNamespace) {
					attributeNs = attributeNsType.split(":")[0];
					attributeType = attributeNsType.split(":")[1];
				} else {
					attributeType = attributeNsType;
					isSimpleType = true;
				}

				String parentName = parentNamePath.concat(".").concat(attributeName);
				isRequired = use.equalsIgnoreCase(REQUIRED);
				if (!isRequired && (!minOccurs.isEmpty() || !maxOccurs.isEmpty())) {
					isRequired = minOccurs.equalsIgnoreCase(NUMBER_1) && maxOccurs.equalsIgnoreCase(NUMBER_1);
				}

				if (xsdMetadata.getSimpleObjects().contains(attributeType) || isSimpleType) {
					container.getComponents().add(processSimpleObject(attributeType, attributeName, parentName, String.format(RESTRICTION_XPATH, attributeType), isRequired, xsdMetadata, formioField));
				} else if (xsdMetadata.getComplexObjects().contains(attributeType)) {
					container = processComplexObject(container, attributeType, parentName, parentXPath, isRequired, xsdMetadata, formioField, typePath);
				} else if (attributeNs.contains(XS_NS)) {
					container.getComponents().add(processSimpleObject(attributeType, attributeName, parentName, attributeXPath, isRequired, xsdMetadata, formioField));
				}

			} else {
				Element attributeChildElement = (Element) xsdMetadata.getXPath().compile(attributeXPath).evaluate(xsdMetadata.getTypesXsdDocument(), XPathConstants.NODE);
				if (attributeChildElement != null) {
					container.getComponents().add(processSimpleObject(parentType, attributeName, parentNamePath, attributeXPath, true, xsdMetadata, formioField));
				}
			}
		}
		return container;
	}

	private String localizeLabel(String key, String name) {
		if (localizedProperties.containsKey(key)) {
			return localizedProperties.getProperty(key);
		} else {
			String[] splitedKey = key.split("\\.");
			String relativeKey = splitedKey[splitedKey.length - 1];
			return localizedProperties.getProperty(relativeKey, name);
		}
	}

	private Components createFormioXmlSchema(ObjectFactory objectFactory, XsdMetadata xsdMetadata) throws Exception {
		return PerformaceUtils.measure("createFormioXsd", () -> {
			try {
				String typePath = xsdMetadata.getType().concat("/");
				String elementXPath = xsdMetadata.getNamespace().concat(":").concat(xsdMetadata.getName()).concat("/").concat(xsdMetadata.getNamespace()).concat(":");
				Components root = objectFactory.createComponents();
				Container rootContainer = processComplexObject(objectFactory.createContainer(), xsdMetadata.getElementType(), xsdMetadata.getName(), elementXPath, null, xsdMetadata, null, typePath);
				root.getComponents().addAll(rootContainer.getComponents());

				Button b = objectFactory.createButton();
				b.setKey(SUBMIT);
				b.setType(ComponentTypeType.BUTTON);
				b.setLabel(localizeLabel(SUBMIT, SUBMIT));
				root.getComponents().add(b);
				return root;
			} catch (Exception e) {
				throw toException("Chyba pri generovaní formio schémy - XML dát ", e);
			}
		});
	}

	private String createFormioJsonSchema(AbstractRegPlugin registerPlugin, FormioSchemaTyp formioSchemaTyp) throws Exception {

		ObjectFactory objectFactory = new ObjectFactory();

		String registerId = registerPlugin.getFullInternalRegisterId();
		File localizedPropertiesFile = new File(registersDir, registerId + "/" + registerId + "_FORMIO.properties");
		try (Reader localizedReader = new InputStreamReader(new FileInputStream(localizedPropertiesFile), StandardCharsets.UTF_8)) {
			localizedProperties.load(localizedReader);
		} catch (@SuppressWarnings("unused") FileNotFoundException ex) {
			//ignore, we use defaults
		}

		XsdMetadata xsdMetadata = prepareXsdMetadata(null, registerPlugin, XPathFactory.newInstance().newXPath());
		xsdMetadata.setObjectFactory(objectFactory);
		xsdMetadata.setFormioSchemaTyp(formioSchemaTyp);

		Components root = createFormioXmlSchema(objectFactory, xsdMetadata);
		String xml = XmlUtils.objectToXml(root);
		return xmlToJson(xml);
	}

	/**
	 * Metóda pripraví a skontroluje všetky registre, vygeneruje tabuľky registra (data, index, history, reference, naturtal_id),
	 * skontroluje sa aktuálnosť tabuliek, skontrolujú sa konfigurácie registrov, XPath pre všetky cesty v konfigurácii,
	 * skontroluje duplicitné kľúče, upozorní na chýbajúce číselníky v databáze
	 *
	 * @return Iterovatelný objekt so zoznamom registrov (ich definíciami) a pluginmi
	 * @throws Exception Chyba pri zistení nedostatku, kvôli ktorému nie je možné používať modul, chyba zastaví spúšťanie aplikácie
	 */
	@Bean
	@Transactional
	public Registers loadRegisters() throws Exception {
		final File registersFile = new File(registersFilePath, FILE_REGISTERS_CONFIGURATION);
		RegistersConfig registersConfig = XmlUtils.parse(registersFile, RegistersConfig.class);
		for (RegisterPlugin registerPlugin : registersConfig.getRegisterPlugin()) {

			File registerPluginFile = new File(registersFilePath, registerPlugin.getRegisterId() + "_" + registerPlugin.getVersion() + XML_EXTENSION);
			RegisterPluginConfig registerPluginConfig = XmlUtils.parse(registerPluginFile, RegisterPluginConfig.class);
			try {

				Constructor<?> constructor = Class.forName(registerPlugin.getPluginClass()).getConstructor(RegisterPlugin.class, RegisterPluginConfig.class);
				AbstractRegPlugin plugin = (AbstractRegPlugin) constructor.newInstance(registerPlugin, registerPluginConfig);

				if (plugin.getOutputFields().isEmpty()) {
					throw new CommonException("Pre register " + registerPlugin.getRegisterId() + " neexistujú výstupné polia.");
				}

				for (RegisterEntryField entryField : registerPluginConfig.getField()) {
					validateEntryField(entryField, plugin);
				}

				beanFactory.initializeBean(plugin, registerPlugin.getPluginClass());
				beanFactory.autowireBeanProperties(plugin, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
				beanFactory.registerSingleton(registerPlugin.getPluginClass(), plugin);

				String register = registerPlugin.getRegisterId().concat("_").concat(String.valueOf(registerPlugin.getVersion()));
				if (!registerService.existsById(plugin.getInternalId())) {

					checkTableNameAndEntityAnnotation(register.concat(DATA), Arrays.asList(plugin.getDataEntityClass().getDeclaredAnnotations()));
					checkTableNameAndEntityAnnotation(register.concat(INDEX), Arrays.asList(plugin.getIndexEntityClass().getDeclaredAnnotations()));
					checkTableNameAndEntityAnnotation(register.concat(DATA_REFERENCE), Arrays.asList(plugin.getDataReferenceEntityClass().getDeclaredAnnotations()));
					checkTableNameAndEntityAnnotation(register.concat(DATA_HISTORY), Arrays.asList(plugin.getDataHistoryEntityClass().getDeclaredAnnotations()));

					registerService.save(new Register(registerPlugin.getRegisterId(), registerPlugin.getVersion(), 0L));
					createDataTable(register.concat(DATA));
					createIndexTable(register.concat(INDEX), register.concat(DATA));
					createReferenceTable(register.concat(DATA_REFERENCE), register.concat(DATA));
					createHistoryTable(register.concat(DATA_HISTORY), register.concat(DATA));
					createNaturalIdTable(register);
				}

				//len otestovanie funkcnosti, nemusi najst nic
				validateDatabase(plugin);

				XsdFile primaryXsdFile = plugin.getPluginConfig().getXsdFile().stream().filter(XsdFile::isIsPrimary).findFirst().orElse(null);
				if (primaryXsdFile == null) {

					throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Pre register '" + plugin.getInfo().getName() + "' (" + registerPlugin.getRegisterId() + "_" + registerPlugin.getVersion() + ") neexistuje XSD súbor na validáciu", null);
				}

				//verify register XSD by loading it to schema!
				XmlUtils.loadXsdSchema(new File(registersFilePath, primaryXsdFile.getPath()));

				if (plugin.getIdField() != null) {
					checkIdFieldMultiplicity(plugin);
				}
				registerPlugins.put(plugin.getPublicId(), plugin);
				registerNames.put(plugin.getPublicId(), registerPlugin.getName());

                /*if(generateFormioSchemas) {
                    createFormioSchemaDir();
                    saveRegisterFormioSchema(plugin, FormioSchemaType.CREATE, register);
                    saveRegisterFormioSchema(plugin, FormioSchemaType.READ, register);
                    saveRegisterFormioSchema(plugin, FormioSchemaType.UPDATE, register);
                }*/
			} catch (Exception e) {
				throw toException("Failed to process register plugin " + registerPlugin, e);
			}
		}
		return new Registers(registerPlugins);
	}

	/**
	 * Spraví nad pluginom základné volania ktoré idú do databázy aby sa overilo že všetky potrebné databázové komponenty sú pripravené.
	 * @param plugin plugin ktorý validujeme
	 */
	private void validateDatabase(AbstractRegPlugin plugin) {
		plugin.getDataRepository().findById(0L);
		plugin.getIndexRepository().findById(0L);
		plugin.getDataReferenceRepository().findById(new RegisterEntryReferenceKey(0L, ""));
		plugin.getDataHistoryRepository().findById(new RegisterEntryHistoryKey(0L, 0L));
		plugin.getDataRepository().getNextSequence();
	}

	private void createFormioSchemaDir() {
		File formioDir = new File(tmpPath + File.separator + FORMIO_SCHEMAS);
		if (!formioDir.exists()){
			formioDir.mkdirs();
		}
	}

	private FileTime getFileModificationTime(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		try {
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			return  attr.lastModifiedTime();
		} catch (NoSuchFileException ex) {
			return null;
		}
	}

	/**
	 * If fileTime1 > fileTime2, returns true.
	 */
	private boolean fileTimeAfter(FileTime fileTime1, FileTime fileTime2) {
		return fileTime1.compareTo(fileTime2) > 0;
	}

	private boolean doUpdate(String register, FileTime formioSchemaModificationTime) throws IOException {
		String xmlDirectory = UVZ_REG_COMMON_CONFIGURATION + register + XML_EXTENSION;
		FileTime xmlModificationTime = getFileModificationTime(xmlDirectory);

		if (xmlModificationTime != null && fileTimeAfter(xmlModificationTime, formioSchemaModificationTime)) {
			return true;
		}

		String schemasDirectory = UVZ_REG_COMMON_CONFIGURATION + register;
		File[] schemas = new File(schemasDirectory).listFiles();

		if (schemas != null) {
			for (File schema : schemas) {
				FileTime schemaModificationTime = getFileModificationTime(schema.getPath());
				if (schemaModificationTime!= null && fileTimeAfter(schemaModificationTime, formioSchemaModificationTime)) {
					return true;
				}
			}
		}
		return false;
	}

	private void saveRegisterFormioSchema(AbstractRegPlugin plugin, FormioSchemaTyp formioSchemaTyp, String register) throws Exception {
		String formioSchemaDirectory = tmpPath + File.separator + FORMIO_SCHEMAS + File.separator + register
				+ UNDERSCORE + formioSchemaTyp + JSON_EXTENSION;

		FileTime formioSchemaModificationTime = getFileModificationTime(formioSchemaDirectory);

		if (formioSchemaModificationTime == null || doUpdate(register, formioSchemaModificationTime)) {
			String form = createFormioJsonSchema(plugin, formioSchemaTyp);
			JSONObject json = new JSONObject(form);
			FileWriter file = new FileWriter(formioSchemaDirectory);
			file.write(json.toString());
			file.close();
		}
	}

	private void validateEntryField(RegisterEntryField entryField, AbstractRegPlugin plugin) {
		if (entryField.getEnumeration() != null) {
			if (!ciselnikRepository.existsByKodCiselnikaAndDeletedIsFalse(entryField.getEnumeration().getCodelistCode())) {
				//toto nie je exception lebo pri prazdnej databaze by to znamenalo že sa modul nikdy nerozbehne a teda tam nemáme ako vytvoriť číselníky!
				//log.warn("Číselník s codelistCode '" + entryField.getEnumeration().getCodelistCode() + "' neexistuje!");
			}
			if (!entryField.getEnumeration().getCodelistCode().matches("[^\\s]{1,100}")) {
				error(plugin, "Kód číselníka '" + entryField.getEnumeration().getCodelistCode() + "' obsahuje na konci medzery!");
			}
			if (!entryField.getXPathValue().endsWith(AbstractRegPlugin.FIELD_XML_ENUMERATION_ITEM_CODE)) {
				error(plugin, "pole '" + entryField.getXPathValue() + "' obsahuje neplatný XPath pre číselník! XPath pre číselnk musí končiť '" + AbstractRegPlugin.FIELD_XML_ENUMERATION_ITEM_CODE + "'!");
			}
			if (entryField.getDisplayName() != null) {
				entryField.setDisplayName(entryField.getDisplayName().concat(" (hodnota z číselníka)"));
			}
		}

		if (entryField.getHistorical() != null && !entryField.getXPathValue().contains("/")) {
			error(plugin, "historický element nemôže byť root element!");
		}

		if (entryField.isIsOutputField()) {
			if (!entryField.isIsIndexed()) {
				error(plugin, "obsahuje atribút '" + entryField.getDisplayName() + "', ktorý je na výstupe, ale nie je indexovaný!");
			}
		}

		if (entryField.getKeyName() != null) {
			if (entryField.getKeyName().equals(AbstractRegPlugin.INDEXED_FIELD_KEY_ENTRY_ID)) {
				error(plugin, "nesmie byť indexovaný podľa '" + AbstractRegPlugin.INDEXED_FIELD_KEY_ENTRY_ID + "' pretože tento index sa generuje automaticky!");
			}
		} else {
			if (entryField.isIsIndexed() || entryField.isIsOutputField()) {
				error(plugin, "obsahuje atribút '" + entryField.getXPathValue() + "', ktorý nemá kľúč, ale je indexovaný alebo na výstupe!");
			}
		}
		final RegisterField referencedRegister = entryField.getRegister();
		if (referencedRegister != null) {
			final RegisterId referencedRegisterId = new RegisterId(referencedRegister);
			final AbstractRegPlugin referencedPlugin;
			if (plugin.getInternalId().equals(referencedRegisterId)) {
				//ok, zatial nie je inicializovany lebo to je aktualne inicializovany plugin!
				referencedPlugin = plugin;
			} else {
				if (!registerService.existsById(referencedRegisterId)) {
					error(plugin, "referencovaný register '" + referencedRegisterId + "' neexistuje! Chybná referencia v " + entryField);
				}
				referencedPlugin = findPlugin(referencedRegisterId);
			}

			if (referencedPlugin == null) {
				error(plugin, "referencovaný register '" + referencedRegisterId + " nie je inicializovaný!");
			}
			final RegisterJoinKeyType registerJoinKey = entryField.getRegister().getRegisterJoinKey();
			if (registerJoinKey == null) {
				//FIXME toto by sa asi nemuselo kontrolovať, ak to je referencovaný register tak by malo byť v XSD že musí mať join key povinne nie?
				error(plugin, "pole '" + entryField.getKeyName() + "' nemá vyplnený join kľúč na referenovaný register " + referencedRegisterId + "!");
			}

			if (!referencedPlugin.isIndexedBy(registerJoinKey.getTarget())) {
				error(referencedPlugin, "nie je indexovaný podľa '" + registerJoinKey.getTarget() + "' ako požaduje referenčné pole " + entryField + "!");
			}
			if (!plugin.isIndexedBy(registerJoinKey.getSource())) {
				error(plugin, "nie je indexovaný podľa '" + registerJoinKey.getSource() + "' ako požaduje referenčné pole " + entryField + "!");
			}
		}
	}

	private void error(AbstractRegPlugin registerPlugin, String message) {
		final String registerFullInternalId = registerPlugin.getFullInternalRegisterId();
		final String registerNameAndInternalId = "Chyba v registri '" + registerPlugin.getInfo().getName() + "' (" + registerFullInternalId + "): ";
		throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, registerNameAndInternalId + message);
	}

	private void checkIdFieldMultiplicity(AbstractRegPlugin plugin) throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		XsdFile typesXsd = plugin.getPluginConfig().getXsdFile().stream().filter(file -> file.getPath().endsWith(TYPES_XSD)).findFirst().orElse(null);
		if (typesXsd == null) {
			throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Nenašiel sa " + TYPES_XSD + " súbor pre register " + plugin.getInfo().getRegisterId() + ".");
		}
		Document typesXsdDocument = XmlUtils.parse(new File(registersFilePath + File.separator + typesXsd.getPath()));
		RegisterEntryField idField = plugin.getIdField();
		String idFieldName = getIdFieldName(idField.getXPathValue(), isEnumeration(idField));
		if (idField.isIsIdGenerated() && (idField.getRegister() != null || isEnumeration(idField))) {
			throw new IllegalStateException(String.format("Nie je možné mať v generovanom id poli %s registra %s referenciu na register alebo číselník.", idField.getKeyName(), plugin.getInfo().getRegisterId()));
		}
		Node idFieldNode = (Node) plugin.getXPath().evaluate("schema//element[@name='" + idFieldName + "' and (@minOccurs = 1 or not(@minOccurs)) and (@maxOccurs = 1 or not(@maxOccurs))]", typesXsdDocument, XPathConstants.NODE);
		if (idFieldNode == null) {
			throw new IllegalStateException(String.format(MULTIPLICITA_ATRIBUTU_X_V_REGISTRI_X_NIE_SU_SPRAVNE_NASTAVENE, idFieldName, plugin.getInfo().getRegisterId()));
		}
	}

	private String getIdFieldName(String xpath, boolean isEnumeration) {
		int enumeration = 1;
		if (isEnumeration) {
			enumeration++;
		}
		String[] splited = xpath.split("/");
		splited = splited[splited.length - enumeration].split(":");
		return splited[splited.length - 1];
	}

	private boolean isEnumeration(RegisterEntryField field) {
		return field.getEnumeration() != null;
	}

	/**
	 * Metóda vytvorí tabuľku pre ukladanie záznamov registra
	 *
	 * @param dataTableName Názov tabuľky záznamov príslušného registra
	 */
	private void createDataTable(String dataTableName) {

		String sequence = dataTableName + "_id_seq";
		entityManager.createNativeQuery("create sequence " + sequence).executeUpdate();

		String table = "create table " + dataTableName + " ("
				+ " id bigint not null primary key default next value for " + sequence + ","
				+ " xml text not null,"
				+ " valid_from date not null,"
				+ " effective_from date null,"
				+ " effective_to date default null,"
				+ " disabled boolean not null,"
				+ " last_reference_timestamp timestamp,"
				+ " `user` varchar(256),"
				+ " module varchar(4)"
				+ " );";
		entityManager.createNativeQuery(table).executeUpdate();
	}

	/**
	 * Metóda vytvorí tabuľku pre ukladanie indexov pre príslušný register
	 *
	 * @param indexTableName Názov tabuľky pre indexy
	 * @param dataTableName  Názov tabuľky záznamov príslušného registra
	 */
	private void createIndexTable(String indexTableName, String dataTableName) {

		String sequence = indexTableName + "_id_seq";
		String table = "create table " + indexTableName + " ("
				+ " id bigint not null primary key default nextval('" + sequence + "'),"
				+ " entry_id bigint not null references " + dataTableName + "(id),"
				+ " key varchar not null,"
				+ " value varchar not null,"
				+ " value_simplified varchar not null, "
				+ " effective_from date default null, " // moze byt null lebo nie vsetky indexované polia sú s históriou
				+ " effective_to date default null, "
				+ " sequence int not null, " // tu pre polia bez historie nastavime 0
				+ " current boolean not null" // tu pre polia bez historie nastavime true
				+ " )";
		entityManager.createNativeQuery("create sequence " + sequence).executeUpdate();
		entityManager.createNativeQuery(table).executeUpdate();
	}

	/**
	 * Metóda vytvorí tabuľku referencií pre príslušný register
	 *
	 * @param referenceTableName Názov tabuľky pre referencie
	 * @param dataTableName      Názov tabuľky záznamov príslušného registra
	 */
	private void createReferenceTable(String referenceTableName, String dataTableName) {

		String table = "create table " + referenceTableName + " ("
				+ " entry_id bigint not null references " + dataTableName + "(id),"
				+ " module varchar not null,"
				+ " reference_count int not null,"
				+ " primary key (entry_id, module)"
				+ " )";
		entityManager.createNativeQuery(table).executeUpdate();
	}

	/**
	 * Metóda vytvorí tabuľku histórie pre príslušný register a jeho záznamy
	 *
	 * @param historyTableName Názov tabuľky histórie záznamov
	 * @param dataTableName    Názov tabuľky záznamov príslušného registra
	 */
	private void createHistoryTable(String historyTableName, String dataTableName) {

		String table = "create table " + historyTableName + " ("
				+ " entry_id bigint not null references " + dataTableName + "(id),"
				+ " event_id bigint not null references event(id),"
				+ " xml varchar not null,"
				+ " valid_from date not null,"
				+ " effective_from date not null,"
				+ " effective_to date default null,"
				+ " disabled boolean not null,"
				+ " timestamp timestamp not null,"
				+ " `user` varchar(256),"
				+ " module varchar(4),"
				+ " primary key (entry_id, event_id)"
				+ " )";
		entityManager.createNativeQuery(table).executeUpdate();
	}

	private void createNaturalIdTable(String dataTableName) {
		String table = "create table " + dataTableName + "_natural_id ("
				+ " entry_id bigint not null references " + dataTableName + "_data (id),"
				+ " natural_id varchar(255) not null,"
				+ " primary key (natural_id)"
				+ " )";
		entityManager.createNativeQuery(table).executeUpdate();
	}

	/**
	 * Metóda skontroluje anotáciu, či sa zhoduje s názvom tabuľky
	 *
	 * @param dataTableName Názov tabuľky záznamov príslušného registra
	 * @param annotations   Anotácie triedy
	 * @throws Exception Chyba, ak sa názov a anotácia nezhodujú
	 */
	private void checkTableNameAndEntityAnnotation(String dataTableName, List<Annotation> annotations) throws Exception {

		Annotation entityAnnotation = annotations.stream().filter(annotation -> annotation instanceof Entity).findFirst().orElseThrow();
		if (!((Entity) entityAnnotation).name().equalsIgnoreCase(dataTableName)) {
			throw new Exception("Entity anotácia sa nezhoduje s vygenerovaným názvom tabuľky '" + dataTableName.toLowerCase() + "'");
		}
	}

	private void createSubjectRpoIdentification() {
		String table = "create table if not exists subject_1_rpo_identification ("
				+ "id bigserial not null primary key,"
				+ "rpo_id varchar(32) null unique default null,"
				+ "entry_id bigint null unique default null references subject_1_data(id),"
				+ "is_merged boolean not null default false,"
				+ "is_error boolean not null default false,"
				+ "msg varchar(4096) null default null,"
				+ "insert_timestamp timestamp not null default current_timestamp"
				+ " )";
		entityManager.createNativeQuery(table).executeUpdate();
	}
}
