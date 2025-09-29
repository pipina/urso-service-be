package sk.is.urso.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.formio.schema.ObjectFactory;
import sk.is.urso.rest.model.FormioSchemaTyp;


import javax.xml.xpath.XPath;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class XsdMetadata {

	@NonNull
	private final RegisterPluginConfig pluginConfig;
	@NonNull
	private final XPath xPath;
	@NonNull
	private final Document typesXsdDocument;
	@NonNull
	private final Map<String, Document> importXsd;
	
	String name;
	String type;
	String namespace;
	String elementType;
	Document xmlDocument;
	NodeList childElements;
	ObjectFactory objectFactory;
	FormioSchemaTyp formioSchemaTyp;
	Set<String> simpleObjects = new HashSet<>();
	Set<String> complexObjects = new HashSet<>();
	Set<String> foreignObjects = new HashSet<>();
	Set<String> arrayElements = new HashSet<>();
	Set<String> duplicateElements = new HashSet<>();
	Set<String> codelistItemElements = new HashSet<>();
	Set<String> codelistItemOptionalElements = new HashSet<>();
	Map<String, String> simpleElements = new HashMap<>();
	Map<String, String> complexElements = new HashMap<>();
	Map<String, String> foreignElements = new HashMap<>();
	Map<String, String> foreignElementsNs = new HashMap<>();
	
	public XsdMetadata(Document typesXsdDocument, Set<String> duplicateElements, Map<String, Document> importXsd, XPath xPath, Document xmlDocument, ObjectFactory objectFactory, RegisterPluginConfig pluginConfig, FormioSchemaTyp formioSchemaTyp) {
		this(pluginConfig, xPath, typesXsdDocument, importXsd);
		this.xmlDocument = xmlDocument;
		this.objectFactory = objectFactory;
		this.formioSchemaTyp = formioSchemaTyp;
		this.duplicateElements = duplicateElements;
	}
}
