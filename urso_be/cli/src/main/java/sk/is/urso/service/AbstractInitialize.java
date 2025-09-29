package sk.is.urso.service;

import lombok.RequiredArgsConstructor;
import org.alfa.exception.CommonException;
import org.alfa.utils.DateUtils;
import org.alfa.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.is.urso.common.regconfig.plugin.v1.RegisterEntryField;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;
import sk.is.urso.common.regconfig.v1.RegistersConfig;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityIndex;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.model.DvojicaKlucHodnotaSHistoriou;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.alfa.utils.SearchUtils.sanitizeValue;

@Component
@RequiredArgsConstructor
public abstract class AbstractInitialize {

    protected final Initialize initialize;

    protected static final String CSV = ".csv";
    protected static final String DATA = "_data_";
    protected static final String INDEX = "_index_";
    protected static final String DATA_REFERENCE = "_data_reference_";
    protected static final String DATA_HISTORY = "_data_history_";
    protected static final String NATURAL_ID = "_natural_id_";
    private static final String SEQUENCE = "sequence";
    private static final String CURRENT = "current";
    private static final String EFFECTIVE_FROM = "effectiveFrom";
    private static final String EFFECTIVE_TO = "effectiveTo";
    private static final String SUBJECT = "SUBJECT";
    private static final String RPO = "RPO";
    private static final String RFO = "RFO";
    private static final String SUBJECT_1 = "subject_1";
    public static final String FILE_REGISTERS_CONFIGURATION = "registersConfig_v1.xml";
    public static final String XML_EXTENSION = ".xml";

    File dataFile;
    File dataHistoryFile;
    File dataReferenceFile;
    File indexFile;
    File naturalIdFile;

    FileWriter dataWriter;
    FileWriter dataHistoryWriter;
    FileWriter dataReferenceWriter;
    FileWriter naturalIdWriter;
    FileWriter indexWriter;

    File subjectDataFile;
    File subjectDataHistoryFile;
    File subjectDataReferenceFile;
    File subjectIndexFile;
    File subjectNaturalIdFile;

    FileWriter subjectDataWriter;
    FileWriter subjectDataHistoryWriter;
    FileWriter subjectDataReferenceWriter;
    FileWriter subjectNaturalIdWriter;
    FileWriter subjectIndexWriter;

    int csvOrderNum = 1;

    public void prepareCsvFiles(String register) throws IOException {
        dataFile = new File(initialize.csvFilePath + register + DATA + csvOrderNum + CSV);
        dataHistoryFile = new File(initialize.csvFilePath + register + DATA_HISTORY + csvOrderNum + CSV);
        dataReferenceFile = new File(initialize.csvFilePath + register + DATA_REFERENCE + csvOrderNum + CSV);
        indexFile = new File(initialize.csvFilePath + register + INDEX + csvOrderNum + CSV);
        naturalIdFile = new File(initialize.csvFilePath + register + NATURAL_ID + csvOrderNum + CSV);

        dataWriter = new FileWriter(dataFile, true);
        dataHistoryWriter = new FileWriter(dataHistoryFile, true);
        dataReferenceWriter = new FileWriter(dataReferenceFile, true);
        indexWriter = new FileWriter(indexFile, true);
        naturalIdWriter = new FileWriter(naturalIdFile, true);

        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        if (!dataHistoryFile.exists()) {
            dataHistoryFile.createNewFile();
        }
        if (!dataReferenceFile.exists()) {
            dataReferenceFile.createNewFile();
        }
        if (!indexFile.exists()) {
            indexFile.createNewFile();
        }
        if (!naturalIdFile.exists()) {
            naturalIdFile.createNewFile();
        }

        dataWriter.write("neplatny, ucinnost_od, ucinnost_do, datum_cas_poslednej_referencie, modul, pouzivatel, platnost_od, xml, id\n");
        dataHistoryWriter.write("neplatny, ucinnost_od, ucinnost_do, modul, datum_cas_vytvorenia, pouzivatel, platnost_od, xml, zaznam_id, udalost_id\n");
        naturalIdWriter.write("zaznam_id, povodne_id\n");
        indexWriter.write("kontext, aktualny, ucinnost_od, ucinnost_do, kluc, sekvencia, hodnota, hodnota_zjednodusena, zaznam_id\n");
    }

    public void closeCsvFiles() throws IOException {
        dataWriter.close();
        dataHistoryWriter.close();
        dataReferenceWriter.close();
        indexWriter.close();
        naturalIdWriter.close();
    }

    public void prepareSubjectCsvFiles() throws IOException {
        subjectDataFile = new File(initialize.csvFilePath + "/subject/" + SUBJECT_1 + DATA + csvOrderNum + CSV);
        subjectDataHistoryFile = new File(initialize.csvFilePath + "/subject/" + SUBJECT_1 + DATA_HISTORY + csvOrderNum + CSV);
        subjectDataReferenceFile = new File(initialize.csvFilePath + "/subject/" + SUBJECT_1 + DATA_REFERENCE + csvOrderNum + CSV);
        subjectIndexFile = new File(initialize.csvFilePath + "/subject/" + SUBJECT_1 + INDEX + csvOrderNum + CSV);
        subjectNaturalIdFile = new File(initialize.csvFilePath + "/subject/" + SUBJECT_1 + NATURAL_ID + csvOrderNum + CSV);

        subjectDataWriter = new FileWriter(subjectDataFile, true);
        subjectDataHistoryWriter = new FileWriter(subjectDataHistoryFile, true);
        subjectDataReferenceWriter = new FileWriter(subjectDataReferenceFile, true);
        subjectIndexWriter = new FileWriter(subjectIndexFile, true);
        subjectNaturalIdWriter = new FileWriter(subjectNaturalIdFile, true);

        if (!subjectDataFile.exists()) {
            subjectDataFile.createNewFile();
        }
        if (!subjectDataHistoryFile.exists()) {
            subjectDataHistoryFile.createNewFile();
        }
        if (!subjectDataReferenceFile.exists()) {
            subjectDataReferenceFile.createNewFile();
        }
        if (!subjectIndexFile.exists()) {
            subjectIndexFile.createNewFile();
        }
        if (!subjectNaturalIdFile.exists()) {
            subjectNaturalIdFile.createNewFile();
        }

        subjectDataWriter.write("id, xml, platnost_od, ucinnost_od, ucinnost_do, neplatny, datum_cas_poslednej_referencie, subjekt_id, fo_id,  pouzivatel,  modul\n");
        subjectDataReferenceWriter.write("zaznam_id, modul, pocet_referencii, subjekt_id\n");
        subjectDataHistoryWriter.write("zaznam_id, udalost_id, xml, platnost_od, ucinnost_od, ucinnost_do, neplatny, datum_cas_vytvorenia,  pouzivatel,  modul");
        subjectNaturalIdWriter.write("zaznam_id, povodne_id\n");
        subjectIndexWriter.write("kontext, aktualny, ucinnost_od, ucinnost_do, kluc, sekvencia, hodnota, hodnota_zjednodusena, zaznam_id\n");
    }

    public void closeSubjectCsvFiles() throws IOException {
        subjectDataWriter.close();
        subjectDataReferenceWriter.close();
        subjectDataHistoryWriter.close();
        subjectNaturalIdWriter.close();
        subjectIndexWriter.close();
    }

    public AbstractRegPlugin getRegisterPlugin(String register) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        final var registersFile = new File(initialize.rainternalRegistersConfigPath, FILE_REGISTERS_CONFIGURATION);
        var registersConfig = XmlUtils.parse(registersFile, RegistersConfig.class);
        RegisterPlugin registerPlugin = null;

        for (RegisterPlugin plug : registersConfig.getRegisterPlugin()) {
            if (plug.getRegisterId().equals(register)) {
                registerPlugin = plug;
                break;
            }
        }
        if (registerPlugin == null) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "Plugin pre " + register + " sa nenasiel");
        }
        String registerPluginName;
        switch (register) {
            case RFO:
                registerPluginName = "sk.is.urso.plugin.RfoReg1";
                break;
            case RPO:
                registerPluginName = "sk.is.urso.plugin.RpoReg1";
                break;
            case SUBJECT:
                registerPluginName = "sk.is.urso.plugin.SubjectReg1";
                break;
            default:
                throw new CommonException(HttpStatus.BAD_REQUEST, "Plugin pre " + register + " sa nenasiel");
        }

        Constructor<?> constructor = Class.forName(registerPluginName).getConstructor(RegisterPlugin.class, RegisterPluginConfig.class);
        File registerPluginFile = new File(initialize.rainternalRegisterPluginFilePath, registerPlugin.getRegisterId() + "_" + registerPlugin.getVersion() + XML_EXTENSION);
        var registerPluginConfig = XmlUtils.parse(registerPluginFile, RegisterPluginConfig.class);
        return (AbstractRegPlugin) constructor.newInstance(registerPlugin, registerPluginConfig);
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
                    data.getEntityIndexes().add(entityIndex);
                }
            }
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
                } else {
                    context = Objects.equals(context, "") ? node.getNodeName() + "[0]" : node.getNodeName() + "[0]/" + context;
                    arrayNodes.put(node.getNodeName(), 0);
                }
            } else {
                context = Objects.equals(context, "") ? node.getNodeName() + "[0]" : node.getNodeName() + "[0]/" + context;
            }
            return createContextPath(parentNode, context, arrayNodes);
        }
        return context;
    }

    private void checkNodeAttributes(Node node, DvojicaKlucHodnotaSHistoriou pairWithHistory) {

        if (node.getAttributes() == null) {
            return;
        }

        Node sequence = node.getAttributes().getNamedItem(SEQUENCE);
        if (sequence != null) {
            pairWithHistory.sekvencia(Integer.valueOf(sequence.getNodeValue()));
        }
        Node current = node.getAttributes().getNamedItem(CURRENT);
        if (current != null) {
            pairWithHistory.aktualna(Boolean.valueOf(current.getNodeValue()));
        }
        Node effectiveFrom = node.getAttributes().getNamedItem(EFFECTIVE_FROM);
        if (effectiveFrom != null) {
            pairWithHistory.ucinnostOd(LocalDate.parse(effectiveFrom.getNodeValue()));
        }
        Node effectiveTo = node.getAttributes().getNamedItem(EFFECTIVE_TO);
        if (effectiveTo != null) {
            pairWithHistory.ucinnostDo(LocalDate.parse(effectiveTo.getNodeValue()));
        }

        if (sequence == null && current == null && node.getParentNode() != null) {
            checkNodeAttributes(node.getParentNode(), pairWithHistory);
        }
    }
}
