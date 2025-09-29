package sk.is.urso.reg;

import ch.qos.logback.classic.Logger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.alfa.exception.CommonException;
import org.alfa.model.ListRequestModel;
import org.alfa.model.MatchEnumModel;
import org.alfa.model.OrderEnumModel;
import org.alfa.utils.DateUtils;
import org.alfa.utils.PerformaceUtils;
import org.alfa.utils.Utils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou;
import sk.is.urso.reg.model.DvojicaKlucHodnotaVolitelna;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.reg.model.ZaznamRegistra;
import sk.is.urso.reg.model.ZaznamRegistraList;
import sk.is.urso.reg.model.ZaznamRegistraListRequestFilter;
import sk.is.urso.reg.sql.Alias;
import sk.is.urso.reg.sql.ConditionIHasParameters;
import sk.is.urso.reg.sql.Eq;
import sk.is.urso.reg.sql.Gt;
import sk.is.urso.reg.sql.IsNull;
import sk.is.urso.reg.sql.LtEq;
import sk.is.urso.reg.sql.Sql;
import sk.is.urso.common.regconfig.plugin.v1.RegisterEntryField;
import sk.is.urso.common.regconfig.plugin.v1.RegisterField;
import sk.is.urso.common.regconfig.plugin.v1.RegisterJoinKeyType;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.plugin.v1.XsdFile;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstraktná trieda ktorá musí implementovať plugin registra
 *

 *
 */
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public abstract class AbstractRegPlugin {// NOSONAR

	public static String getFullRegisterId(String id, int versionId) {
		return id + "_" + versionId;
	}
	private static final Logger log = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

	public static final String FIELD_XML_ENUMERATION_ITEM_CODE = "itemCode";
	public static final String FIELD_ENTRY_ID = "entryId";
	public static final String FIELD_VALUE_SIMPLIFIED = "valueSimplified";
	public static final String FIELD_EFFECTIVE_TO = "effectiveTo";
	public static final String FIELD_EFFECTIVE_FROM = "effectiveFrom";
	public static final String FIELD_VALID = "valid";
	public static final String ID_NULL = "NULL";
	public static final String REGISTER_TYPE_EXTERNAL = "EXTERNAL";
	private static final String LEFT = "left";
	/**
	 * Urcuje nazov vytvaranych indexov s hodnotou entryId
	 */
	public static final String INDEXED_FIELD_KEY_ENTRY_ID = "entryId";
	public static final String NIE_JE_VYPLNENE_ID = "Nie je vyplnené id ";

	private static final List<String> basicFields = Arrays.asList("registerId", "registerVersionId", FIELD_ENTRY_ID, "validFrom", FIELD_EFFECTIVE_FROM, FIELD_EFFECTIVE_TO, FIELD_VALID);
	private static final Map<String, String> basicFieldsDb = new HashMap<>();
	static {
		basicFieldsDb.put(AbstractRegEntityIndex.Fields.zaznamId, AbstractRegEntityIndex.DbFields.ZAZNAM_ID);
		basicFieldsDb.put(AbstractRegEntityIndex.Fields.ucinnostOd, AbstractRegEntityIndex.DbFields.UCINNOST_OD);
		basicFieldsDb.put(AbstractRegEntityIndex.Fields.ucinnostDo, AbstractRegEntityIndex.DbFields.UCINNOST_DO);
	}

	public static String padId(long value) {
		StringBuilder idString = new StringBuilder(String.valueOf(value));

		while (idString.length() < 15) {
			idString.insert(0, "0");
		}
		return idString.toString();
	}

	@Autowired
	private AdditionalPluginOps additionalOps;

	@Autowired
	private EntityManager entityManager;

	@ToString.Include
	@EqualsAndHashCode.Include
	private final RegisterPlugin info;
	private final RegisterPluginConfig pluginConfig;
	private final XPath xPath;
	private final Class<? extends AbstractRegEntityData> dataEntityClass;
	private final Class<? extends AbstractRegEntityIndex> indexEntityClass;
	private final Class<? extends AbstractRegEntityDataReference> dataReferenceEntityClass;
	private final Class<? extends AbstractRegEntityDataHistory> dataHistoryEntityClass;
	private final Class<? extends AbstractRegEntityNaturalId> naturalIdEntityClass;

	private final RegisterId internalId;
	private final RegisterId publicId;
	private final RegisterEntryField idField;
	private final RegisterEntryField idField2;
	private final XsdFile primaryXsdFile;

	private final List<RegisterEntryField> indexedFields = new ArrayList<>();
	private final List<RegisterEntryField> enumerationFields = new ArrayList<>();
	private final List<RegisterEntryField> registerFields = new ArrayList<>();
	private final List<RegisterEntryField> historicalFields = new ArrayList<>();
	private final Map<String, String> outputFields = new LinkedHashMap<>();

	protected AbstractRegPlugin(@NonNull RegisterPlugin info, @NonNull RegisterPluginConfig pluginConfig, @NonNull Class<? extends AbstractRegEntityData> dataEntityClass,
								@NonNull Class<? extends AbstractRegEntityIndex> indexEntityClass, @NonNull Class<? extends AbstractRegEntityDataReference> dataReferenceEntityClass,
								@NonNull Class<? extends AbstractRegEntityDataHistory> dataHistoryEntityClass, Class<? extends AbstractRegEntityNaturalId> naturalIdEntityClass) {
		this.info = info;
		this.pluginConfig = pluginConfig;
		this.xPath = createXPath();
		this.dataEntityClass = dataEntityClass;
		this.indexEntityClass = indexEntityClass;
		this.dataReferenceEntityClass = dataReferenceEntityClass;
		this.dataHistoryEntityClass = dataHistoryEntityClass;
		this.naturalIdEntityClass = naturalIdEntityClass;
		this.internalId = new RegisterId(info.getRegisterId(), info.getVersion());
		String registerId = info.getPublicRegisterId() != null ? info.getPublicRegisterId() : info.getRegisterId();
		this.publicId = new RegisterId(registerId, info.getVersion());

		primaryXsdFile = this.getPluginConfig().getXsdFile().stream().filter(XsdFile::isIsPrimary).findFirst().orElse(null);
		if (primaryXsdFile == null) {
			throw new CommonException(HttpStatus.NOT_FOUND, "Pre register '" + this.getInfo().getName() + "' (" + getFullRegisterId() + ") neexistuje XSD súbor na validáciu", null);
		}

		RegisterEntryField idField = null;// NOSONAR
		RegisterEntryField idField2 = null;// NOSONAR
		for (RegisterEntryField field : this.pluginConfig.getField()) {
			if (field.isIsId()) {
				if (idField != null && idField2 != null) {
					throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Register môže mať najviac 2 ID polia ale register " + getFullInternalRegisterId() + " už má dve ID polia a ešte ďalšie " + field + "!");
				}
				if (idField == null) {
					idField = field;
				}
				else {
					idField2 = field;
				}
			}
			initField(field);
		}
		this.idField = idField;// can be null!
		this.idField2 = idField2;// can be null!
	}

	public boolean isExternal() {
		return this.pluginConfig.getRegisterType().equals(REGISTER_TYPE_EXTERNAL);
	}

	private void initField(RegisterEntryField field) {
		if(field.isIsOutputField()) {
			if (!field.isIsIndexed()) {
				throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Field " + field + " is output but is not indexed! All output fields must be indexed fields!");
			}
			this.outputFields.put(field.getKeyName(), field.getDisplayName());
		}
		if (field.isIsIndexed()) {
			this.indexedFields.add(field);
		}
		if (field.getEnumeration() != null) {
			this.enumerationFields.add(field);
		}
		if (field.getRegister() != null) {
			this.registerFields.add(field);
		}
		if (field.getHistorical() != null) {
			this.historicalFields.add(field);
		}
	}

	/**
	 * Vráti sekvenciu ďalšieho záznamu ktorý sa má zapísať do registra. Táto metóda volá {@link PluginRepositoryData#getNextSequence()}. To je štandardná implementácia pre väčšinu registrov, ale pre
	 * napr. subjekty id ani nie je typu long a generuje sa inak!
	 *
	 * @return next sequence
	 */
	public Long getNextSequence() {
		return getDataRepository().getNextSequence();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public <T extends AbstractRegEntityData> void saveDataEntity(T entity) {
		PluginRepository<T, Long> repo = (PluginRepository<T, Long>) getDataRepository();

		entity.setId(getNextEmptyId(entity.getId(),repo));
		entityManager.persist(entity);

		AbstractRegEntityNaturalId naturalId = createNewNaturalIdEntity();
		naturalId.setPovodneId(getPovodneId(entity));
		saveNaturalIdEntity(naturalId, entity);
	}

	<T extends AbstractRegEntityData> String getPovodneId(T entity){
		if (entity.getPovodneId() != null){
			return entity.getPovodneId();
		} else {
			return entity.getId().toString();
		}
	}

	protected <T extends AbstractRegEntityData> Long getNextEmptyId(Long entityId, PluginRepository<T, Long> repo) {
		while (repo.existsById(entityId)) {
			entityId++;
		}
		return entityId;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public <T extends AbstractRegEntityData> void updateDataEntity(T entity) {
		PluginRepository<T, Long> repo = (PluginRepository<T, Long>) getDataRepository();
		if(repo.findById(entity.getId()).isEmpty()){
			throw new CommonException(HttpStatus.BAD_REQUEST, "Neexistuje záznam registra "+ getInfo().getRegisterId() + " s id: "+ entity.getId()+ " na upravenie");
		}
		repo.save(entity);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractRegEntityDataReference> void saveDataReferenceEntity(T entity) {
		PluginRepositoryReferences<T, RegisterEntryReferenceKey> repo = (PluginRepositoryReferences<T, RegisterEntryReferenceKey>) getDataReferenceRepository();
		repo.save(entity);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public <T extends AbstractRegEntityNaturalId> void saveNaturalIdEntity(T naturalId, AbstractRegEntityData entity) {
		PluginRepository<T, String> repo = (PluginRepository<T, String>) getNaturalIdRepository();
		if (naturalId.getPovodneId() == null) {
			naturalId.setPovodneId(entity.getId().toString());
		}
		naturalId.setZaznamId(entity);
		repo.save(naturalId);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractRegEntityData> void deleteDataEntityById(long entryId) {
		PluginRepositoryData<T> repo = (PluginRepositoryData<T>) getDataRepository();
		repo.deleteById(entryId);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractRegEntityDataReference> void deleteDataReferenceEntity(T entity) {
		PluginRepository<T, RegisterEntryReferenceKey> repo = (PluginRepository<T, RegisterEntryReferenceKey>) getDataReferenceRepository();
		repo.delete(entity);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractRegEntityDataHistory> void deleteDataHistoryEntityByDataEntryId(Long entryId) {
		PluginRepository<T, RegisterEntryHistoryKey> repo = (PluginRepository<T, RegisterEntryHistoryKey>) getDataHistoryRepository();

		Specification<T> specification = (root, query, builder) -> {
			final List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get(FIELD_ENTRY_ID), entryId));
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		repo.findAll(specification).forEach(repo::delete);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractRegEntityNaturalId> void deleteNaturalIdEntityByDataEntryId(Long entryId) {
		PluginRepositoryNaturalId<T> repo = (PluginRepositoryNaturalId<T>) getNaturalIdRepository();
		repo.deleteByZaznamIdId(entryId);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractRegEntityIndex> void deleteDataIndexEntityByDataEntryId(Long entryId) {
		PluginRepositoryIndexes<T> repo = (PluginRepositoryIndexes<T>) getIndexRepository();

		repo.deleteByZaznamId(entryId);
	}

	private <T> T create(Class<T> entityClass) {
		try {
			Constructor<T> constructor = entityClass.getConstructor();
			T instance = constructor.newInstance();
			return instance;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create instance for class " + entityClass, e);
		}
	}

	/**
	 * This method will create new entity for storing register data
	 *
	 * @return new entity for storing register data
	 */
	public AbstractRegEntityData createNewDataEntity() {
		return create(this.dataEntityClass);
	}

	/**
	 * This method will create new entity for storing register data
	 *
	 * @return new entity for storing register data
	 */
	public AbstractRegEntityNaturalId createNewNaturalIdEntity() {
		return create(this.naturalIdEntityClass);
	}

	/**
	 * This method will create new entity for storing register data
	 *
	 * @param document document to create
	 * @return new entity for storing register data
	 * @throws TransformerException
	 * @throws IOException
	 */
	public AbstractRegEntityData createNewDataEntityForInsert(Document document){
		return create(this.dataEntityClass);
	}

	/**
	 * This method will create new entity for storing register index
	 *
	 * @return new entity for storing register index
	 */
	public AbstractRegEntityIndex createNewIndexEntity() {
		return create(this.indexEntityClass);
	}

	/**
	 * This method will create new entity for storing register data references
	 *
	 * @return new entity for storing register data references
	 */
	public AbstractRegEntityDataReference createNewDataReferenceEntity() {
		return create(this.dataReferenceEntityClass);
	}

	/**
	 * This method will create new entity for storing register data history
	 *
	 * @return new entity for storing register data history
	 */
	public AbstractRegEntityDataHistory createNewDataHistoryEntity() {
		return create(this.dataHistoryEntityClass);
	}

	/**
	 * Metóda vráti všetky registrové záznamy vyhovujúce vstupnej špecifikácií a vyberie vyhovujúce záznamy pre požadovanú stránku
	 *
	 * @param s           Špecifikácia záznamov registra
	 * @param pageRequest Požiadavka pre stránkovanie
	 * @param <T>         Typ záznamu registra
	 * @return Stránka so záznamami registrov
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityData> Page<T> findAllDataEntityAsPage(Specification<T> s, PageRequest pageRequest) {
		PluginRepository<T, Long> repo = (PluginRepository<T, Long>) this.getDataRepository();
		return repo.findAll(s, pageRequest);
	}

	/**
	 * Metóda vráti všetky indexy ku záznamu s príslušným kľúčom
	 *
	 * @param entryId Id záznamu registra
	 * @param key     Kľúč, podľa ktorého je možné vyhľadávať
	 * @param <T>     Typ indexu registra
	 * @return Zoznam indexov registra
	 */
	@SuppressWarnings({ "unchecked" })
	@Transactional
	public <T extends AbstractRegEntityIndex> List<T> findAllIndexEntity(Long entryId, String key) {
		PluginRepositoryIndexes<T> repo = (PluginRepositoryIndexes<T>) this.getIndexRepository();
		return repo.findAllByZaznamIdAndKluc(entryId, key);
	}

	/**
	 * Metóda vráti všetky indexy podľa špecifikácie
	 *
	 * @param s   Špecifikácia indexov registra
	 * @param <T> Typ indexu registra
	 * @return Zoznam indexov registra
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityIndex> List<T> findAllIndexEntity(Specification<T> s) {
		PluginRepository<T, Long> repo = (PluginRepository<T, Long>) this.getIndexRepository();
		return repo.findAll(s);
	}

	/**
	 * Metóda vráti všetky indexy vyhovujúce špecifikácii a požadovanému stránkovaniu
	 *
	 * @param s           Špecifikácia indexov registra
	 * @param pageRequest Požiadavka na stránkovanie
	 * @param <T>         Typ indexu registra
	 * @return Stránka so zoznamom indexov registra
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityIndex> Page<T> findAllIndexEntity(Specification<T> s, PageRequest pageRequest) {
		PluginRepository<T, Long> repo = (PluginRepository<T, Long>) this.getIndexRepository();
		return repo.findAll(s, pageRequest);
	}

	/**
	 * Metóda vráti všetky indexy vyhovujúce príkladu
	 *
	 * @param s   Príklad indexu registra
	 * @param <T> Typ indexu registra
	 * @return Zoznam indexov registra
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityIndex> List<T> findAllIndexEntity(Example<T> s) {
		PluginRepositoryIndexes<T> repo = (PluginRepositoryIndexes<T>) this.getIndexRepository();
		return repo.findAll(s);
	}

	/**
	 * Metóda zistí či existuje referencia registra
	 *
	 * @param entryId id zaznamu registra
	 * @param <T>     Typ referencie registra
	 * @return Zoznam referencií registra
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityDataReference> boolean existsDataReferenceEntity(Long entryId) {
		AbstractRegEntityDataReference abstractRegEntityDataReference = this.createNewDataReferenceEntity();

		RegisterEntryReferenceKey registerEntryReferenceKey = new RegisterEntryReferenceKey();
		registerEntryReferenceKey.setZaznamId(entryId);
		abstractRegEntityDataReference.setId(registerEntryReferenceKey);

		PluginRepository<T, RegisterEntryReferenceKey> repo = (PluginRepository<T, RegisterEntryReferenceKey>) this.getDataReferenceRepository();

		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("referenceCount", FIELD_ENTRY_ID);
		Example<? extends T> example = (Example<? extends T>) Example.of(abstractRegEntityDataReference, matcher);

		return repo.exists(example);
	}

	/**
	 * Metóda vráti zoznam záznamov histórie záznamu registra podľa špecifikácie
	 *
	 * @param s   Špecifikácia histórie záznamov registra
	 * @param <T> Typ histórie záznamu registra
	 * @return Zoznam historických záznamov registra
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityDataHistory> List<T> findAllDataHistoryEntity(Specification<T> s) {
		PluginRepository<T, RegisterEntryHistoryKey> repo = (PluginRepository<T, RegisterEntryHistoryKey>) this.getDataHistoryRepository();
		return repo.findAll(s);
	}

	/**
	 * Metóda vráti NaturalId príslušného registra podľa EntryId záznamu registra
	 *
	 * @param entryId Id záznamu registra
	 * @param <T>     Príslušný typ NaturalId registra
	 * @return NaturalId príslušné k entryId
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityNaturalId> String findNaturalIdByEntryId(Long entryId) {
		if (idField == null) {
			return null;// no id field no natural id entry! For now id field means id is generated, in future there could be not generated id!
		}
		PluginRepository<T, String> repo = (PluginRepository<T, String>) this.getNaturalIdRepository();
		Specification<T> spec = (root, query, builder) -> builder.equal(root.get(FIELD_ENTRY_ID), entryId);
		Optional<T> naturalIdOpt = repo.findOne(spec);
		T entity = naturalIdOpt.orElseThrow(() -> new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Nenašlo sa naturalId pre entryId " + entryId + " v registri " + getFullRegisterId(), null));
		return entity.getPovodneId();
	}

	public Node getIdFromXml(Document xml, RegisterEntryField idField) throws XPathExpressionException {
		XPathExpression expr = xPath.compile(idField.getXPathValue());
		Node idNode = (Node) expr.evaluate(xml, XPathConstants.NODE);

		if (idNode == null) {
			throw new CommonException(HttpStatus.BAD_REQUEST, NIE_JE_VYPLNENE_ID + idField.getXPathValue());
		}
		if (idNode.getTextContent() == null) {
			throw new CommonException(HttpStatus.BAD_REQUEST, NIE_JE_VYPLNENE_ID + idField.getXPathValue());
		}
		return idNode;
	}

	/**
	 * Táto metóda pluginu registra upraví vstupné XML tak aby bolo možné ho uložiť. Napr. pre register subjektov musí v inserte upraviť subjektID. Default implementácia nerobí s XML nič a vráti
	 *
	 * @param inputXml vstupný XML DOM
	 * @param entity   entita kde uložíme údaje záznamu a kde môžeme mať pripravené informácie pre XML
	 * @return výstupné upravené xml DOM
	 * @throws XPathExpressionException
	 */
	public <T extends AbstractRegEntityData> Document prepareXmlForInsert(Document inputXml, T entity) throws XPathExpressionException {
		long generatedId = getNextSequence();
		entity.setId(generatedId);
		RegisterEntryField field = getIdField();
		if (field == null) {
			return inputXml;// nie každý register musí mať generované ID, napr pre MYTEST je to email
		}
		Node idNode = this.getIdFromXml(inputXml, field);
		String id = idNode.getTextContent();
		checkId(id, field);
		if (field.isIsIdGenerated()) {
			String idString = padId(generatedId);
			entity.setPovodneId(idString);
			idNode.setTextContent(idString);
		}
		return inputXml;
	}

	/**
	 * Overí dané ID podľa toho či sa ID má generovať a či je vyplnene.
	 * Hodí exception ak sa ID má generovať ale je vyplnené.
	 * Hodí exception ak sa ID nemá generovať ale nie je vyplnené.
	 * @param id id z XMLka
	 * @param field konfigurácia ID poľa ktoré kontrolujeme
	 */
	protected void checkId(String id, RegisterEntryField field) {
		boolean isIdfilled = !id.equals(AbstractRegPlugin.ID_NULL);
		if (field.isIsIdGenerated()) {
			if (isIdfilled)
				throw new CommonException(HttpStatus.BAD_REQUEST, "Ak je ID generované, tak dáta nesmú obsahovať vyplnené ID", getIdField().getXPathValue(), null);
		} else {
			if (!isIdfilled)
				throw new CommonException(HttpStatus.BAD_REQUEST, "Nebolo vyplnené ID");
		}
	}

	public boolean isUniqueByIdInIndexTable(Document inputXml) throws XPathExpressionException {
		RegisterEntryField field = getIdField();
		RegisterEntryField field2 = getIdField2();

		if (field != null && field2 != null) {
			return isUniqueByMultipleIds(inputXml);
		}

		if (field != null) {
			Node idNode = getIdFromXml(inputXml, field);
			String id = idNode.getTextContent();
			return !getIndexRepository().existsByKlucAndHodnotaAndAktualny(field.getKeyName(), id, true);
		}
		return true;
	}

	public Document prepareXmlForUpdate(Document currentXml, Document inputXml) throws XPathExpressionException {

		if (inputXml != null) {
			inputXml.normalize();
		}
		if (currentXml != null) {
			currentXml.normalize();
		}

		if (getIdField() != null && currentXml != null && inputXml != null) {
			XPathExpression expr = xPath.compile(getIdField().getXPathValue());
			Node currentIdNode = (Node) expr.evaluate(currentXml, XPathConstants.NODE);
			Node inputIdNode = (Node) expr.evaluate(inputXml, XPathConstants.NODE);
			if (currentIdNode == null || inputIdNode == null || !currentIdNode.getFirstChild().getNodeValue().equalsIgnoreCase(inputIdNode.getFirstChild().getNodeValue())) {
				throw new CommonException(HttpStatus.BAD_REQUEST, "V nových dátach bola zmenená hodnota ID elementu '" + getIdField().getKeyName() + "'", null);
			}
		}

		Map<String, Map<String, Node>> documentMap1 = new HashMap<>();
		Map<String, Map<String, Node>> documentMap2 = new HashMap<>();

		recursiveMapBuild(currentXml, documentMap1, "");
		recursiveMapBuild(inputXml, documentMap2, "");

		Set<String> keys = documentMap1.keySet();
		for (String key : keys) {

			if (!documentMap2.containsKey(key)) {
				throw new CommonException(HttpStatus.BAD_REQUEST, "V nových dátach bol zmazaný element " + key, null);
			}

			Set<String> sequences = documentMap1.get(key).keySet();
			for (String sequence : sequences) {
				compareNodes(key, sequence, documentMap1, documentMap2);
			}
			for (String sequence : documentMap2.get(key).keySet()) {
				if (sequences.contains(sequence)) {
					continue;
				}
				checkNewNode(key, sequence, documentMap2);
			}
		}

		for (Entry<String, Map<String, Node>> documentNodes2 : documentMap2.entrySet()) {
			for (String sequence : documentNodes2.getValue().keySet()) {
				checkNewNode(documentNodes2.getKey(), sequence, documentMap2);
			}
		}

		return inputXml;
	}

	public void checkNewNode(String key, String sequence, Map<String, Map<String, Node>> documentMap2) {
		final Node effectiveTo = documentMap2.get(key).get(sequence).getAttributes().getNamedItem(FIELD_EFFECTIVE_TO);
		final Node effectiveFrom = documentMap2.get(key).get(sequence).getAttributes().getNamedItem(FIELD_EFFECTIVE_FROM);
		if (effectiveFrom == null) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Chýba dátum začiatku platnosti v " + key + " so sekvenciou " + sequence, null);
		}
		if (effectiveTo != null) {
			checkDates(effectiveFrom.getNodeValue(), effectiveTo.getNodeValue(), key, sequence);
		}
	}

	public boolean compareNodes(String key, String sequence, Map<String, Map<String, Node>> documentMap1, Map<String, Map<String, Node>> documentMap2) {
		final Node node1 = documentMap1.get(key).get(sequence);
		final Node node2 = documentMap2.get(key).get(sequence);
		if (node2 == null) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "V nových dátach bol zmazaný element " + key + " so sekvenciou " + sequence, null);
		}
		boolean updateFormattedName = false;

		if (node2 == null){
			return false;
		}

		String effectiveFrom1 = node1.getAttributes().getNamedItem(FIELD_EFFECTIVE_FROM).getNodeValue();
		String effectiveFrom2 = node2.getAttributes().getNamedItem(FIELD_EFFECTIVE_FROM).getNodeValue();

		if (!effectiveFrom1.equals(effectiveFrom2)) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Došlo k zmene dátumu začiatku účinnosti v " + key + " v sekvencii " + sequence, null);
		}

		String current1 = node1.getAttributes().getNamedItem("current").getNodeValue();
		String current2 = node2.getAttributes().getNamedItem("current").getNodeValue();

		if (current1.equals("false") && current2.equals("true")) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Došlo k neoprávnenej zmene atribútu \"current\" v " + key + " v sekvencii " + sequence, null);
		}
		if (!current1.equals(current2)) {
			updateFormattedName = true;
		}

		Node effectiveTo1 = node1.getAttributes().getNamedItem(FIELD_EFFECTIVE_TO);
		Node effectiveTo2 = node2.getAttributes().getNamedItem(FIELD_EFFECTIVE_TO);

		if (effectiveTo1 != null && effectiveTo2 != null && !effectiveTo1.getNodeValue().equals(effectiveTo2.getNodeValue())) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Došlo k zmene dátumu konca účinnosti v " + key + " v sekvencii " + sequence, null);
		}
		if (effectiveTo1 != null && effectiveTo2 == null) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Došlo k zmene dátumu konca účinnosti v " + key + " v sekvencii " + sequence, null);
		}
		if (effectiveTo1 == null && effectiveTo2 != null) {
			updateFormattedName = true;
		}

		Node valid1 = node1.getAttributes().getNamedItem(FIELD_VALID);
		Node valid2 = node2.getAttributes().getNamedItem(FIELD_VALID);

		if (valid1 != null && valid2 == null) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Došlo k zmene atribútu \"valid\" v " + key + " v sekvencii " + sequence, null);
		}

		if (valid1 != null) {
			if (valid1.getNodeValue().equals("false") && valid1.getNodeValue().equals("true")) {
				throw new CommonException(HttpStatus.BAD_REQUEST, "Došlo k zmene atribútu \"valid\" v " + key + " v sekvencii " + sequence, null);
			}
			if (!valid1.equals(valid2)) {
				updateFormattedName = true;
			}
		}
		if (valid1 == null && valid2 != null) {
			updateFormattedName = true;
		}

		if (effectiveTo2 != null) {
			checkDates(effectiveFrom2, effectiveTo2.getNodeValue(), key, sequence);
		}

		List<Node> children1 = convertNodeListToList(node1.getChildNodes());
		List<Node> children2 = convertNodeListToList(node2.getChildNodes());

		if (children1.size() > children2.size()) {
			findRemovedNodes(key, sequence, children1, children2);
		}

		Set<String> childrenNodeNames = new HashSet<>();
		for (Node child : children2) {
			if (!childrenNodeNames.contains(child.getLocalName())) {

				List<Node> nodes1 = children1.stream().filter(node -> node.getLocalName().equals(child.getLocalName())).toList();
				List<Node> nodes2 = children2.stream().filter(node -> node.getLocalName().equals(child.getLocalName())).toList();

				if (nodes1.size() != nodes2.size()) {
					boolean historicNode = false;
					if (nodes1.size() > 0) {
						if (nodes1.get(0).getAttributes().getNamedItem("sequence") != null) {
							historicNode = true;
						}
					} else {
						if (nodes2.get(0).getAttributes().getNamedItem("sequence") != null) {
							historicNode = true;
						}
					}
					if (!historicNode) {
						throwException(children1, key, sequence, "Množstvo dát je rozdielne: '" + children1.size() + "' a '" + children2.size() + "'");
					}
				}

				for (int i = 0; i < Math.min(nodes1.size(), nodes2.size()); i++) {
					Optional<String> result = equalNodes(nodes1.get(i), nodes2.get(i));
					result.ifPresent(s -> throwException(children1, key, sequence, s));
				}
				childrenNodeNames.add(child.getLocalName());
			}
		}
		return updateFormattedName;
	}

	private void findRemovedNodes(String key, String sequence, List<Node> children1, List<Node> children2) {

		Set<String> checkedNodes = new HashSet<>();
		StringBuilder removedNodes = null;

		for (Node node : children1) {
			if (!checkedNodes.contains(node.getLocalName())) {

				int children1NodeCount = (int) children1.stream().filter(node1 -> node1.getLocalName().equalsIgnoreCase(node.getLocalName())).count();
				int children2NodeCount = (int) children2.stream().filter(node1 -> node1.getLocalName().equalsIgnoreCase(node.getLocalName())).count();
				if (children1NodeCount > children2NodeCount) {
					if (removedNodes == null) {
						removedNodes = new StringBuilder(node.getLocalName());
					} else {
						removedNodes.append(", ").append(node.getLocalName());
					}
				}
				checkedNodes.add(node.getLocalName());
			}
		}
		throw new CommonException(HttpStatus.BAD_REQUEST, "V elemente " + key + " so sekvenciou " + sequence + " došlo k zmazaniu elementov: " + removedNodes, null);
	}

	/**
	 * Throw exception for given nodes
	 * @param nodes nodes related
	 * @param key key related
	 * @param sequence sequence related
	 * @param errMessage message
	 */
	public void throwException(List<Node> nodes, String key, String sequence, String errMessage) {
		throw new CommonException(HttpStatus.BAD_REQUEST, "Boli zmenené historické dáta v " + key + " so sekvenciou " + sequence + ". " + errMessage, null);
	}

	public XPath createXPath() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {
			@Override
			public String getNamespaceURI(String prefix) {
				XsdFile xsdFile = pluginConfig.getXsdFile().stream().filter(file -> file.getNamespacePrefix().equalsIgnoreCase(prefix)).findFirst().orElse(null);
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
		return xpath;
	}

	/**
	 * Metóda kontroluje formát dátumov v XML dátach registra
	 *
	 * @param effectiveFrom Dátum, ktorý sa bude kontrolovať
	 * @param effectiveTo   Dátum, ktorý sa bude kontrolovať
	 * @param key           Cesta z XML, kde sa kontrolujú dátumy
	 * @param sequence      Sekvencia pre cestu z XML, kde sa kontrolujú dátumy
	 */
	private void checkDates(String effectiveFrom, String effectiveTo, String key, String sequence) {
		Date effectiveToDate;
		Date effectiveFromDate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			effectiveToDate = format.parse(effectiveTo);
			effectiveFromDate = format.parse(effectiveFrom);
		} catch (Exception ex) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Nebolo možné spracovať formát dátumu pre " + key + " v sekvencii " + sequence, ex);
		}

		if (effectiveFromDate.after(effectiveToDate)) {
			throw new CommonException(HttpStatus.BAD_REQUEST, "Dátum začiatku platnosti je po dátume konca platnosti v " + key + " v sekvencii " + sequence, null);
		}
	}

	public <U> U findEntryByEntryId(Long entryId, Class<U> type) {
		return getDataRepository().findById(entryId, type);
	}

	/**
	 * This method will return repository to work with data entities of this register plugin
	 *
	 * @return repository to work with data entities of this register plugin
	 */
	public abstract PluginRepositoryData<? extends AbstractRegEntityData> getDataRepository();

	/**
	 * This method will return repository to work with data entities of this register plugin
	 *
	 * @return repository to work with data entities of this register plugin
	 */
	// public abstract PluginRepository<? extends AbstractRegEntityDataWithoutXml, Long> getDataRepository();
	/**
	 * This method will return repository to work with index entities of this register plugin
	 *
	 * @return repository to work with index entities of this register plugin
	 */
	public abstract PluginRepositoryIndexes<? extends AbstractRegEntityIndex> getIndexRepository();

	/**
	 * This method will return repository to work with data reference entities of this register plugin
	 *
	 * @return repository to work with data reference entities of this register plugin
	 */
	public abstract PluginRepositoryReferences<? extends AbstractRegEntityDataReference, RegisterEntryReferenceKey> getDataReferenceRepository();

	/**
	 * This method will return repository to work with data history entities of this register plugin
	 *
	 * @return repository to work with data history entities of this register plugin
	 */
	public abstract PluginRepository<? extends AbstractRegEntityDataHistory, RegisterEntryHistoryKey> getDataHistoryRepository();

	/**
	 * This method will return repository to work with data history entities of this register plugin
	 *
	 * @return repository to work with data history entities of this register plugin
	 */
	public abstract PluginRepositoryNaturalId<? extends AbstractRegEntityNaturalId> getNaturalIdRepository();

	/**
	 * Metóda vráti zoznam indexov príslušného registra podľa EntryId
	 *
	 * @param entryId Id záznamu registra
	 * @param <T>     Typ indexu registra
	 * @return Zoznam indexov príslušného registra
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends AbstractRegEntityIndex> List<T> findAllIndexEntityByEntryId(long entryId) {
		// FIXME toto sa pouziva len na najdenie vsetkych indexov na zmazanie. Prerobit na priame volanie delete nad repozitarom!
		Specification<AbstractRegEntityIndex> spec = (root, query, builder) -> {
			final List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get(FIELD_ENTRY_ID), entryId));
			return builder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
		return (List<T>) findAllIndexEntity(spec);
	}

	/**
	 * Vráti definíciu poľa registra pre daný key a indikátor či je indexované
	 *
	 * @param key     kľúč poľa
	 * @param indexed ak not null tak hľadáme len pole s daným stavom indexácie
	 * @return nájdené pole alebo nu
	 */
	public Optional<RegisterEntryField> getField(@NotNull String key, Boolean indexed) {
		return getPluginConfig().getField().stream().filter(field -> {
			String fieldKeyName = field.getKeyName();
			if (fieldKeyName == null) {
				return false; // hladame pomenovane polia
			}
			if (!fieldKeyName.equals(key)) {
				return false;
			}
			if (indexed == null) {
				// stav indexovania nas nezaujima
				return true;
			}
			return indexed == field.isIsIndexed();
		}).findFirst();
	}

	/**
	 * Metóda vráti názov registra v tvare NAZOV_VERZIA. Ak je v konfigu publicRegisterId tak sa použije ako NAZOV to, inak sa berie registerId!
	 *
	 * @return úplné označenie registra v tvare NAZOV_VERZIA
	 */
	public String getFullRegisterId() {
		if (this.info.getPublicRegisterId() != null) {
			return getFullRegisterId(this.info.getPublicRegisterId(), this.info.getVersion());
		}
		return getFullInternalRegisterId();
	}

	public String getFullInternalRegisterId() {
		return getFullRegisterId(this.info.getRegisterId(), this.info.getVersion());
	}

	/**
	 * Metóda spracuje vstupný XML Node, ktorý spracuje tak, že rekurzívne prehľadáva potomkov Node, pričom ak prehľadávaný Node je historický záznam (v atribútoch sa nachádza sequence), tak sa pridá
	 * do Mapy, ktorá je parametrom pri volaní.
	 *
	 * @param node Aktuálne prehľadávaný XML Node
	 * @param map  Mapa, do ktorej sa ukladá Node, ak je historický
	 * @param path Aktuálna cesta vnorenia (cesta rodičov až po aktuálne spracovávaný Node)
	 */
	public void recursiveMapBuild(Node node, Map<String, Map<String, Node>> map, String path) {
		if (node == null) {
			return;
		}

		if (node.getNodeType() == Node.ELEMENT_NODE && node.getAttributes().getNamedItem("sequence") != null) {
			String sequence = node.getAttributes().getNamedItem("sequence").getTextContent();
			if (!map.containsKey(path + ":" + node.getLocalName())) {
				map.put(path + ":" + node.getLocalName(), new HashMap<>());
			}

			if (map.get(path + ":" + node.getLocalName()).containsKey(sequence)) {
				throw new CommonException(HttpStatus.BAD_REQUEST, "Duplicitné ID sekvencie pre " + node.getLocalName(), null);
			} else {
				map.get(path + ":" + node.getLocalName()).put(sequence, node);
			}
		}

		NodeList children = node.getChildNodes();
		if (children == null)
			return;

		for (int i = 0; i < children.getLength(); i++) {
			recursiveMapBuild(children.item(i), map, path + ":" + node.getLocalName());
		}
	}

	/**
	 * Metóda porovná 2 XML Node, či sú rovnaké
	 *
	 * @param node1 Prvý Node na porovnanie
	 * @param node2 Druhý Node na porovnanie
	 * @return Boolean hodnota, ak je sú rovnaké, vráti true, ak sú rozdielne, vráti false
	 */
	private Optional<String> equalNodes(Node node1, Node node2) {

		if (node1.getNodeType() != node2.getNodeType()) {
			return Optional.of("Typy elementov '" + node1.getLocalName() + "' sú rozdielne: '" + node1.getNodeType() + "' a '" + node2.getNodeType() + "'");
		}

		if (node1.getAttributes() == null ^ node2.getAttributes() == null) {
			return Optional.of("V elementoch '" + node1.getLocalName() + "' boli zmenené atribúty");
		}
		// Tu sa beru do uvahy len atributy ktore nie su historicke atributy - tych pocet sa moze zmenit napriklad pridanim effectiveTo
		if (node1.getAttributes() != null && node2.getAttributes() != null && node1.getAttributes().getNamedItem("sequence") == null && node2.getAttributes().getNamedItem("sequence") == null) {
			List<Node> attributes1 = convertAttributesNodeMapToList(node1.getAttributes());
			List<Node> attributes2 = convertAttributesNodeMapToList(node2.getAttributes());

			if (attributes1.size() != attributes2.size()) {
				return Optional.of("Počet atribútov elementov '" + node1.getLocalName() + "' je rozdielny: '" + attributes1.size() + "' a '" + attributes2.size() + "'");
			}

			for (int i = 0; i < attributes1.size(); i++) {
				if (!attributes1.get(i).isEqualNode(attributes2.get(i))) {
					return Optional.of("Atribúty '" + attributes1.get(i).getLocalName() + "' elementov '" + node1.getLocalName() + "' nie sú zhodné");
				}
			}
		}

		List<Node> children1 = convertNodeListToList(node1.getChildNodes());
		List<Node> children2 = convertNodeListToList(node2.getChildNodes());
		if (node1.getNodeType() == Node.TEXT_NODE || (node1.getChildNodes().getLength() == 1 && node1.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)) {
			if (!node1.getTextContent().equals(node2.getTextContent())) {
				return Optional.of("Hodnoty '" + node1.getParentNode().getLocalName() + "' elementov '" + node1.getLocalName() + "' sú rozdielne: '" + node1.getTextContent() + "' a '"
						+ node2.getTextContent() + "'");
			}
		}

		if (node1.getChildNodes() == null ^ node2.getChildNodes() == null) {
			return Optional.of("V elementoch '" + node1.getLocalName() + "' boli zmenené dáta");
		}

		if (children1.size() != children2.size()) {
			if (Math.max(children1.size(), children2.size()) == 1) {
				if (children1.size() == 1 && children1.get(0).getLocalName().equals("itemValue")) {
					Node child = children1.get(0).cloneNode(true);
					child.setTextContent(null);
					children2.add(child);
				} else if (children2.size() == 1 && children2.get(0).getLocalName().equals("itemValue")) {
					Node child = children2.get(0).cloneNode(true);
					child.setTextContent(null);
					children1.add(child);
				}
			} else if (Math.max(children1.size(), children2.size()) == 2) {
				if (children1.size() == 2 && children1.get(0).getLocalName().equals("itemCode") && children1.get(0).getTextContent() != null && children1.get(1).getLocalName().equals("itemValue")) {
					children2.add(children1.get(1));
				} else if (children2.size() == 2 && children2.get(0).getLocalName().equals("itemCode") && children2.get(0).getTextContent() != null
						&& children2.get(1).getLocalName().equals("itemValue")) {
					children1.add(children2.get(1));
				}
			}
		}

		for (int i = 0; i < children1.size(); i++) {
			Optional<String> result = equalNodes(children1.get(i), children2.get(i));
			if (result.isPresent()) {
				return result;
			}
		}

		if (children1.size() != children2.size()) {
			return Optional.of("Množstvo dát v elementoch '" + node1.getLocalName() + "' je rozdielne: '" + children1.size() + "' a '" + children2.size() + "'");
		}

		return Optional.empty();
	}

	/**
	 * Metóda mení NodeList na List, do ktorého vloží Nodes
	 *
	 * @param nodelist Vstupný NodeList, ktorý chceme konvertovať
	 * @return Zoznam XML Nodes po konvertovaní
	 */
	private List<Node> convertNodeListToList(NodeList nodelist) {
		List<Node> nodes = new ArrayList<>();

		if (nodelist == null) {
			return nodes;
		}

		for (int i = 0; i < nodelist.getLength(); i++) {
			if (nodelist.item(i).getNodeType() != Node.ELEMENT_NODE || nodelist.item(i).getFirstChild() == null) {
				continue;
			}
			nodes.add(nodelist.item(i));
		}

		return nodes;
	}

	/**
	 * Metóda mení NamedNodeMap na zoznam Nodes
	 *
	 * @param nodeMap Vstupná mapa Nodes
	 * @return Zoznam XML Nodes po konvertovaní
	 */
	private List<Node> convertAttributesNodeMapToList(NamedNodeMap nodeMap) {
		List<Node> nodes = new ArrayList<>();

		if (nodeMap == null) {
			return nodes;
		}

		for (int i = 0; i < nodeMap.getLength(); i++) {
			if (nodeMap.item(i).getNodeType() == Node.ATTRIBUTE_NODE) {
				if (nodeMap.item(i).getLocalName() != null && !nodeMap.item(i).getLocalName().equalsIgnoreCase("xmlns") && !nodeMap.item(i).getLocalName().equalsIgnoreCase("codelistCode")) {
					nodes.add(nodeMap.item(i));
				}
			}
		}
		return nodes;
	}

	/**
	 * Metóda vytvorí {@link AbstractRegEntityData}, do ktorého pridá zoznam indexovaných polí podľa konfigurácie registra
	 *
	 * @return {@link AbstractRegEntityData} so zoznamom polí, ktoré je možné indexovať
	 */
	public AbstractRegEntityData createDataEntityWithIndexedFields() {

		AbstractRegEntityData data = createNewDataEntity();
		data.setEntityIndexes(new ArrayList<>());

		for (RegisterEntryField field : getIndexedFields()) {
			AbstractRegEntityIndex index = createNewIndexEntity();
			index.setKluc(field.getKeyName());
			index.setHodnotaZjednodusena("");
			data.getEntityIndexes().add(index);
		}
		return data;
	}

	public AbstractRegEntityDataReference incrementReference (Long entryId, String moduleId, String registerId, AbstractRegEntityData data) {
		AbstractRegEntityDataReference dataReference = this.getDataReferenceRepository().findById(new RegisterEntryReferenceKey(entryId, moduleId)).orElse(null);
		if (dataReference == null) {
			dataReference = this.createNewDataReferenceEntity();
			dataReference.setId(new RegisterEntryReferenceKey(entryId, moduleId));
			dataReference.setZaznamId(data);
			dataReference.setPocetReferencii(0);
		}

		dataReference.setPocetReferencii(dataReference.getPocetReferencii() + 1);
		this.saveDataReferenceEntity(dataReference);
		log.info("[REG][REFERENCE][INCREMENT] register = {}, entryId = {}, module = {}, count = {}", registerId, entryId, moduleId, dataReference.getPocetReferencii());

		return dataReference;
	}

	private boolean isUniqueByMultipleIds(Document inputXml) throws XPathExpressionException {

		Node idNode = getIdFromXml(inputXml, getIdField());
		Node idNode2 = getIdFromXml(inputXml, getIdField2());
		String id = idNode.getTextContent();
		String id2 = idNode2.getTextContent();

		ZaznamRegistraListRequestFilter filter = new ZaznamRegistraListRequestFilter();
		filter.getPolia().add(new DvojicaKlucHodnotaVolitelna().kluc(getIdField().getKeyName()).hodnota(id));
		filter.getPolia().add(new DvojicaKlucHodnotaVolitelna().kluc(getIdField2().getKeyName()).hodnota(id2));
		ListRequestModel listRequest = new ListRequestModel();
		listRequest.setPage(0);
		listRequest.setLimit(10);
		ZaznamRegistraList registerEntries = findRegisterEntries(filter, listRequest);
		return registerEntries.getResult().isEmpty();
	}

	@Transactional(readOnly = true)
	public ZaznamRegistraList findRegisterEntries(@NotNull @Valid ZaznamRegistraListRequestFilter filter, @NonNull ListRequestModel listRequest) {
		final List<String> sortingValues = new ArrayList<>();
		final List<String> indexedFields = getRegisterIndexFields(getIndexedFields(), getFullRegisterId(), null);
		sortingValues.addAll(basicFields);
		sortingValues.addAll(indexedFields);
		listRequest.setDefault(FIELD_ENTRY_ID, OrderEnumModel.ASC);
		listRequest.check(sortingValues);

		for (DvojicaKlucHodnotaVolitelna keyValuePair : filter.getPolia()) {
			if (!indexedFields.contains(keyValuePair.getKluc())) {
				throw new CommonException(HttpStatus.BAD_REQUEST,
						"Register '" + info.getName() + "' (" + info.getRegisterId() + "_" + info.getVersion() + ") nie je indexovaný podľa '" + keyValuePair.getKluc() + "'", null);
			}
		}

		Sql sql = getQuery(filter, listRequest);
		String sqlString = sql.toString();
		Query query = entityManager.createNativeQuery(sqlString);
		listRequest.setPaging(query);
		log.info("[SEARCH][SQL] " + sql.toDebugString());
		sql.setParameters(query);

		@SuppressWarnings("unchecked")
		final Set<BigInteger> entryIds = new HashSet<BigInteger>(PerformaceUtils.measure("Indexes query", query::getResultList));
		long total;
		if(entryIds.isEmpty() && listRequest.getPage() > 0)
			return new ZaznamRegistraList().total(countQuery(sql));

		if(entryIds.size() < listRequest.getLimit()) {
			// viac nie je, nemusime robit count query
			// ale ak to nie je prva stranka tak total je vlastne sucet aj predoslych stranok
			total = (listRequest.getLimit().intValue() * listRequest.getPage().intValue()) + entryIds.size();
		}
		else {
			total = countQuery(sql);
		}

		final List<ZaznamRegistra> resultList = new ArrayList<>(entryIds.size());

		PerformaceUtils.measure("Prepare register entry", entryIds.size(), () -> {
			for (BigInteger index : entryIds) {
				resultList.add(prepareRegisterEntry(index.longValue()));
			}
		});
		return new ZaznamRegistraList().total(total).result(resultList);
	}

	private long countQuery(Sql sql) {
		Sql sqlCount = sql.asCountQuery();
		String sqlCountString = sqlCount.toString();
		Query query = entityManager.createNativeQuery(sqlCountString);
		sqlCount.setParameters(query);
		log.info("[SEARCH][SQL][COUNT] " + sqlCount.toDebugString());
		final Object o = PerformaceUtils.measure("[QUERY][COUNT]", query::getSingleResult);
		return Utils.objectToLong(o);
	}

	private Sql getQuery(ZaznamRegistraListRequestFilter filter, ListRequestModel listRequest) {
		final Sql sql = new Sql(getDataEntityClass(), getIndexEntityClass());
		Alias root = sql.select(AbstractRegEntityIndex.DbFields.ZAZNAM_ID, getIndexEntityClass());
		Alias entityData = sql.join(root, getDataEntityClass(), LEFT).onFields(AbstractRegEntityIndex.DbFields.ZAZNAM_ID, AbstractRegEntityData.DbFields.ID).target;
		List<ConditionIHasParameters> conditions = new ArrayList<>();
		// where root.key= 'entryId' by nemalo byt potrebne ak mame group by root.entry_id
		//conditions.add(new Eq(root, AbstractRegEntityIndex.DbFields.KEY, INDEXED_FIELD_KEY_ENTRY_ID));
		if (filter.getReferencujuciModul() != null) {
			Alias referenceJoin = sql.join(entityData, getDataReferenceEntityClass()).onFields(AbstractRegEntityData.DbFields.ID, AbstractRegEntityDataReference.DbFields.ZAZNAM_ID).target;
			conditions.add(new Eq(referenceJoin, AbstractRegEntityDataReference.DbFields.MODUL, filter.getReferencujuciModul()));
		}

		if (filter.getPlatny() != null && filter.getPlatny()) {
			conditions.add(new Eq(entityData, AbstractRegEntityData.DbFields.NEPLATNY, false));
		}

		if (filter.getDatumUcinnosti() != null) {
			conditions.add(sql.or(
					sql.and(new LtEq(entityData, AbstractRegEntityData.DbFields.UCINNOST_OD, DateUtils.toDate(filter.getDatumUcinnosti())),
							new IsNull(entityData, AbstractRegEntityData.DbFields.UCINNOST_DO)),
					sql.and(new LtEq(entityData, AbstractRegEntityData.DbFields.UCINNOST_OD, DateUtils.toDate(filter.getDatumUcinnosti())),
							new Gt(entityData, AbstractRegEntityData.DbFields.UCINNOST_DO, DateUtils.toDate(filter.getDatumUcinnosti())))));
		}

		Map<String, Map<String, List<DvojicaKlucHodnotaVolitelna>>> keyValueOptionalMap = getKeyValueOptionalMap(filter.getPolia());
		final boolean hasNonoptionalKeys = filter.getPolia().stream().anyMatch(pair -> !pair.getVolitelna());

		for (Entry<String, Map<String, List<DvojicaKlucHodnotaVolitelna>>> entry : keyValueOptionalMap.entrySet()) {
			String key = entry.getKey();
			Map<String, List<DvojicaKlucHodnotaVolitelna>> value = entry.getValue();
			conditions.add(createPredicate(key, value, hasNonoptionalKeys, root, sql, listRequest.getMatch()));
		}
		createOrderBy(sql, listRequest, root);
		sql.where(sql.and(conditions));
		sql.groupBy(root, AbstractRegEntityIndex.DbFields.ZAZNAM_ID);
		return sql;
	}

	protected void createOrderBy(Sql sql, ListRequestModel listRequest, Alias root) {
		final boolean asc = listRequest.getOrder().equals(OrderEnumModel.ASC);
		Alias relatedIndex;
		String[] sortKeys = listRequest.getSort().split(",");

		for (String sortKey : sortKeys) {
			if (basicFields.contains(sortKey)) {
				String sort = basicFieldsDb.getOrDefault(sortKey, sortKey);
				sql.orderBy(root, sort, asc);
			} else {
				if (isReferenceToOtherRegister(listRequest.getSort())) {
					sortKey = sortKey.split("\\.")[1];
					relatedIndex = joinRefReg(sql, root, listRequest.getSort());
				} else {
					relatedIndex = root;
				}

				Alias orderIndex = sql.join(relatedIndex, getIndexEntityClass(), LEFT)
						.onFields(AbstractRegEntityIndex.DbFields.ZAZNAM_ID, AbstractRegEntityIndex.DbFields.ZAZNAM_ID)
						.onTargetValue(AbstractRegEntityIndex.DbFields.KLUC, sortKey).target;

				sql.orderBy(orderIndex, AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA, asc)
						.orderBy(orderIndex, AbstractRegEntityIndex.DbFields.HODNOTA, asc);

				sql.groupBy(orderIndex, AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA);
				sql.groupBy(orderIndex, AbstractRegEntityIndex.DbFields.HODNOTA);
			}
		}
	}

	/**
	 * Spravi join z index tabulky aktualneho registra pre source key na index tabulku referencovaneho registra pre target key
	 * @param sql
	 * @param root
	 * @param fullKey kluc vyhladavaneho pola vratane vsetkych casti (teda aj s bodkou)
	 * @return
	 */
	private Alias joinRefReg(Sql sql, Alias root, String fullKey) {
		RegisterField registerField = getRegisterField(fullKey);
		AbstractRegPlugin refPlugin = additionalOps.getPlugin(registerField.getRegisterId(), registerField.getVersion());
		RegisterJoinKeyType refJoinInfo = registerField.getRegisterJoinKey();
		//najprv musime najoinovat source pole z aktualneho registra
		Alias sourceJoin = sql.join(root, getIndexEntityClass()).onFields(AbstractRegEntityIndex.DbFields.ZAZNAM_ID, AbstractRegEntityIndex.DbFields.ZAZNAM_ID)
				.onTargetValue(AbstractRegEntityIndex.DbFields.KLUC, refJoinInfo.getSource()).target;
		// potom musime najoinovat target pole z referencovaneho registra
		Alias targetJoin = sql.join(sourceJoin, refPlugin.getIndexEntityClass())
				.onFields(AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA, AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA)
				.onTargetValue(AbstractRegEntityIndex.DbFields.KLUC, refJoinInfo.getTarget())
				.target;
		return targetJoin;
	}

	private Map<String, Map<String, List<DvojicaKlucHodnotaVolitelna>>> getKeyValueOptionalMap(List<DvojicaKlucHodnotaVolitelna> fields) {
		Map<String, Map<String, List<DvojicaKlucHodnotaVolitelna>>> registerKeyValuesMap = new HashMap<>();
		Set<String> alreadyAdded = new HashSet<>();

		for (DvojicaKlucHodnotaVolitelna dvojicaKlucHodnotaVolitelna : fields) {
			String key = dvojicaKlucHodnotaVolitelna.getKluc();

			if (alreadyAdded.contains(key)) {
				continue;
			}

			if (isReferenceToOtherRegister(key)) {
				String registerId = getRegisterField(key).getRegisterId();
				if (registerKeyValuesMap.get(registerId) != null) {
					registerKeyValuesMap.get(registerId).put(key, getSameKeyFields(fields, key));
				} else {
					Map<String, List<DvojicaKlucHodnotaVolitelna>> map = new HashMap<>();
					map.put(key, getSameKeyFields(fields, key));
					registerKeyValuesMap.put(registerId, map);
				}
			} else {
				if (registerKeyValuesMap.get("root") != null) {
					registerKeyValuesMap.get("root").put(key, getSameKeyFields(fields, key));
				} else {
					Map<String, List<DvojicaKlucHodnotaVolitelna>> map = new HashMap<>();
					map.put(key, getSameKeyFields(fields, key));
					registerKeyValuesMap.put("root", map);
				}
			}
			alreadyAdded.add(key);
		}
		return registerKeyValuesMap;
	}

	private List<DvojicaKlucHodnotaVolitelna> getSameKeyFields(List<DvojicaKlucHodnotaVolitelna> fields, String key) {
		return fields.stream().filter(pair -> pair.getKluc().equals(key)).collect(Collectors.toList());
	}

	private void allOptionalSameCheck(List<DvojicaKlucHodnotaVolitelna> keyValuePairs) {
		Boolean isOptional = null;
		for (DvojicaKlucHodnotaVolitelna dvojicaKlucHodnotaVolitelna : keyValuePairs) {
			if (isOptional == null) {
				isOptional = dvojicaKlucHodnotaVolitelna.getVolitelna();
				continue;
			}
			if (!isOptional.equals(dvojicaKlucHodnotaVolitelna.getVolitelna())) {
				throw new CommonException(HttpStatus.BAD_REQUEST, "Vyhľadávanie podľa kľúča '" + dvojicaKlucHodnotaVolitelna.getKluc()
						+ "' obsahuje povinné aj nepovinné podmienky! Pre jeden kľúč ale musia byť všetky podmienky rovnakej povinnosti (všetky povinné alebo všetky nepovinné)");
			}
		}
	}

	private ConditionIHasParameters getCondition(String key, boolean hasNonoptionalKeys, List<DvojicaKlucHodnotaVolitelna> keyValuePairs, Sql sql, Alias from, MatchEnumModel match) {
		boolean isOptional = keyValuePairs.get(0).getVolitelna();
		boolean moreThanOneConditionForThisKey = keyValuePairs.size() > 1;
		String value = keyValuePairs.get(0).getHodnota();
		List<String> values = keyValuePairs.stream().map(DvojicaKlucHodnotaVolitelna::getHodnota).collect(Collectors.toList());
		// ak neexistuju non optional kluce tak sa optional podmienka povazuje za povinnu!
		if (isOptional && hasNonoptionalKeys) {
			// optionals
			if (moreThanOneConditionForThisKey) {
				// tu to treba pacnut
				switch (match) {
					case CONTAINS:
						return sql.or(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), sql.or(values, v -> createCondition(from, key, sql, v, match)), sql.likeSanitizedValue(from, AbstractRegEntityIndex.DbFields.KLUC, ""));
					case EXACTMATCH:
						return sql.or(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), sql.or(values, v -> createCondition(from, key, sql, v, match)), sql.eqSanitizedValue(from, AbstractRegEntityIndex.DbFields.KLUC, ""));
					case STARTSWITH:
						return sql.or(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), sql.or(values, v -> createCondition(from, key, sql, v, match)), sql.likeSanitizedValueStartsWith(from, AbstractRegEntityIndex.DbFields.KLUC, ""));
					default:
						return null;
				}
			} else {
				switch (match) {
					case CONTAINS:
						return sql.or(sql.and(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), createCondition(from, key, sql, value, match)), sql.likeSanitizedValue(from, AbstractRegEntityIndex.DbFields.KLUC, ""));
					case EXACTMATCH:
						return sql.or(sql.and(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), createCondition(from, key, sql, value, match)), sql.eqSanitizedValue(from, AbstractRegEntityIndex.DbFields.KLUC, ""));
					case STARTSWITH:
						return sql.or(sql.and(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), createCondition(from, key, sql, value, match)), sql.likeSanitizedValueStartsWith(from, AbstractRegEntityIndex.DbFields.KLUC, ""));
					default:
						return null;
				}
			}
		} else {
			if (moreThanOneConditionForThisKey) {
				return sql.and(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), sql.or(values, v -> createCondition(from, key, sql, v, match)));
			} else {
				return sql.and(sql.eq(from, AbstractRegEntityIndex.DbFields.KLUC, key), createCondition(from, key, sql, value, match));
			}
		}
	}

	private ConditionIHasParameters createPredicate(String registerId, Map<String, @NotNull @Valid List<DvojicaKlucHodnotaVolitelna>> keyValuePairsMap, boolean hasNonoptionalKeys,
													Alias root, Sql sql, MatchEnumModel match) {
		final List<ConditionIHasParameters> conditions = new ArrayList<>();

		for (Entry<String, List<DvojicaKlucHodnotaVolitelna>> entry : keyValuePairsMap.entrySet()) {
			List<DvojicaKlucHodnotaVolitelna> keyValuePairs = entry.getValue();
			String key = entry.getKey();

			allOptionalSameCheck(keyValuePairs);
			Alias relatedIndex;
			if (registerId.equals("root")) {
				relatedIndex = root;
			} else {
				key = key.split("\\.")[1];
				relatedIndex = joinRefReg(sql, root, entry.getKey());
			}
			Alias conditionIndex = sql.join(relatedIndex, relatedIndex.entity).onFields(AbstractRegEntityIndex.DbFields.ZAZNAM_ID, AbstractRegEntityIndex.DbFields.ZAZNAM_ID).target;
			ConditionIHasParameters condition = getCondition(key, hasNonoptionalKeys, keyValuePairs, sql, conditionIndex, match);
			conditions.add(condition);
		}
		return sql.and(conditions);
	}

	private ConditionIHasParameters createCondition(Alias alias, @NotNull String key, Sql sql, @NotNull String value, MatchEnumModel match) {
		if (doEquals(key)) {
			return sql.eqSanitizedValue(alias, AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA, value);
		}
		return switch (match) {
			case CONTAINS -> sql.likeSanitizedValue(alias, AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA, value);
			case EXACTMATCH -> sql.eqSanitizedValue(alias, AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA, value);
			case STARTSWITH ->
					sql.likeSanitizedValueStartsWith(alias, AbstractRegEntityIndex.DbFields.HODNOTA_ZJEDNODUSENA, value);
			default -> null;
		};
	}

	private boolean doEquals(@NotNull String key) {
		return getIndexedFields().stream().filter(f -> key.equals(f.getKeyName())).findFirst()
				.map(field -> field.getEnumeration() != null || field.isIsId() || field.getSearchType().equals("EQUALS"))
				.orElse(false);
	}

	private boolean isReferenceToOtherRegister(String key) {
		return key.contains(".");
	}

	protected List<String> getRegisterIndexFields(List<RegisterEntryField> fields, String fullRegisterId, String fullName) {
		List<String> sortingValues = new ArrayList<>();

		for (RegisterEntryField field : fields) {

			if (field.getKeyName() != null && !field.getKeyName().isEmpty()) {
				if (fullName == null) {
					sortingValues.add(field.getKeyName());
				} else {
					sortingValues.add(fullName.concat(".").concat(field.getKeyName()));
				}

				if (field.getRegister() != null && field.getRegister().getRegisterJoinKey() != null
						&& !field.getRegister().getRegisterId().concat("_").concat(String.valueOf(field.getRegister().getVersion())).equalsIgnoreCase(fullRegisterId)) {
					AbstractRegPlugin regPlugin = additionalOps.getRegisterPlugins().stream().filter(plugin -> plugin.getInfo().getRegisterId().equals(field.getRegister().getRegisterId())).findFirst()
							.orElse(null);
					if (regPlugin == null) {
						regPlugin = additionalOps.getPlugin(field.getRegister().getRegisterId(), field.getRegister().getVersion());
					}
					sortingValues.addAll(getRegisterIndexFields(regPlugin.getIndexedFields(), regPlugin.getFullRegisterId(), field.getRegister().getRegisterJoinKey().getSource()));
				}
			}
		}
		return sortingValues;
	}

	private sk.is.urso.common.regconfig.plugin.v1.RegisterField getRegisterField(String keyNameOrig) {
		String keyName;
		if (isReferenceToOtherRegister(keyNameOrig)) {
			keyName = keyNameOrig.split("\\.")[0];
		} else {
			keyName = keyNameOrig;
		}

		for (RegisterEntryField field : getRegisterFields()) {
			if (field.getRegister().getRegisterJoinKey() != null && field.getRegister().getRegisterJoinKey().getSource().equals(keyName)) {
				return field.getRegister();
			}
		}
		throw new CommonException(HttpStatus.BAD_REQUEST, "Pre kľúč '" + keyName + "' nie je definícia na iný register, teda vyhľadávací kľúč '" + keyNameOrig + "' je neplatný!");
	}

	private ZaznamRegistra prepareRegisterEntry(Long entryId) {
		ZaznamRegistra zaznamRegistra = new ZaznamRegistra();
		AbstractRegEntityDataWithoutXml data = getDataRepository().findById(entryId, AbstractRegEntityDataWithoutXml.class);
		zaznamRegistra.setVerziaRegistraId(info.getVersion());
		zaznamRegistra.setRegisterId(info.getRegisterId());
		zaznamRegistra.setZaznamId(entryId);
		zaznamRegistra.setPlatny(!data.isNeplatny());
		zaznamRegistra.setPlatnostOd(DateUtils.toLocalDate(data.getPlatnostOd()));
		zaznamRegistra.setUcinnostOd(DateUtils.toLocalDate(data.getUcinnostOd()));
		if (data.getUcinnostDo() != null) {
			zaznamRegistra.setUcinnostDo(DateUtils.toLocalDate(data.getUcinnostDo()));
		}
		final List<DvojicaKlucHodnotaSHistoriou> fields = prepareRegisterFields(entryId);
		zaznamRegistra.polia(fields);
		return zaznamRegistra;
	}

	private List<DvojicaKlucHodnotaSHistoriou> prepareRegisterFields(Long entryId){
		final List<DvojicaKlucHodnotaSHistoriou> fields = new ArrayList<>();
		for (Entry<String,String> outputField : this.outputFields.entrySet()) {
			String keyName = outputField.getKey();
			for (AbstractRegEntityIndex entityIndex : findRegisterEntityIndexes(entryId, keyName)) {
				DvojicaKlucHodnotaSHistoriou pairWithHistory = new DvojicaKlucHodnotaSHistoriou().kluc(entityIndex.getKluc()).hodnota(entityIndex.getHodnota()).platna(true)
																								 .sekvencia(entityIndex.getSekvencia()).aktualna(entityIndex.getAktualny()).nazovZobrazenia(outputField.getValue())
																								 .kontext(entityIndex.getKontext()).ucinnostOd(DateUtils.toLocalDate(entityIndex.getUcinnostOd()));
				if (entityIndex.getUcinnostDo() != null) {
					pairWithHistory.ucinnostDo(DateUtils.toLocalDate(entityIndex.getUcinnostDo()));
				}
				fields.add(pairWithHistory);
			}
		}
		return fields;
	}

	@SuppressWarnings({ "unchecked" })
	private <T extends AbstractRegEntityIndex> List<T> findRegisterEntityIndexes(Long entryId, String keyName) {

		AbstractRegEntityData entityData = createNewDataEntity();
		entityData.setId(entryId);

		T entityIndex = (T) createNewIndexEntity();
		entityIndex.setData(entityData);
		entityIndex.setKluc(keyName);

		return findAllIndexEntity(entityData.getId(), keyName);
	}

	/**
	 * Do something when first reference is inserted. 
	 * By default nothing is done
	 * @param data for which first reference is added
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void onFirstRefInsert(AbstractRegEntityData data) throws IOException, ParserConfigurationException, SAXException {
	}

	/**
	 * Do something when last reference is deleted.
	 * By defaylt nothing is done
	 * @param data for which first reference is removed
	 */
	public void onLastRefDelete(AbstractRegEntityData data) {
	}

	public AbstractRegEntityIndex createEntryIdIndex(AbstractRegEntityData data) {
		AbstractRegEntityIndex entryIdIndex = createNewIndexEntity();
		entryIdIndex.setData(data);
		entryIdIndex.setKluc(AbstractRegEntityIndex.IndexedFields.ZAZNAM_ID);
		entryIdIndex.setHodnota(data.getId().toString());
		entryIdIndex.setHodnotaZjednodusena(data.getId().toString());
		entryIdIndex.setUcinnostOd(data.getUcinnostOd());
		entryIdIndex.setUcinnostDo(data.getUcinnostDo());
		entryIdIndex.setUcinnostOd(data.getUcinnostOd());
		entryIdIndex.setSekvencia(0);
		entryIdIndex.setAktualny(true);
		entryIdIndex.setKontext("");
		return entryIdIndex;
	}

	/**
	 * Toto vráti true ak je register indexovany podla daneho kluca. 
	 * Register je indexovaný aj podľa {@link #INDEXED_FIELD_KEY_ENTRY_ID} ktorý nie je v zozname indexovaných polí!
	 * @param keyName názov kľúča
	 * @return true ak je register indexovany podla daneho kluca. 
	 */
	public boolean isIndexedBy(String keyName) {
		if(keyName.equals(AbstractRegPlugin.INDEXED_FIELD_KEY_ENTRY_ID)) {
			return true;//podla entryId je indexovany vzdy
		}
		return this.indexedFields.stream().anyMatch(f -> keyName.equals(f.getKeyName()));
	}

	public RegisterId getInternalId() {
		return this.internalId;
	}

	public RegisterId getPublicId() {
		return this.publicId;
	}

	public void validateData(Document doc){
	}
}