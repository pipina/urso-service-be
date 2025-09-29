package sk.is.urso.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.alfa.exception.CommonException;
import org.alfa.utils.DateUtils;
import org.alfa.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sk.is.urso.model.fo.TDCDDCIO;
import sk.is.urso.model.fo.TMOSO;
import sk.is.urso.model.fo.TOEXOEIO;
import sk.is.urso.model.fo.TPOBPHRO;
import sk.is.urso.model.fo.TPRIO;
import sk.is.urso.model.fo.TREGO;
import sk.is.urso.model.fo.TRPRO;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.subject.v1.ExternalRegisterReferenceT;
import sk.is.urso.subject.v1.FormattedNameT;
import sk.is.urso.subject.v1.IdentificatorT;
import sk.is.urso.subject.v1.ObjectFactory;
import sk.is.urso.subject.v1.PhysicalPersonT;
import sk.is.urso.subject.v1.PostalAddressT;
import sk.is.urso.subject.v1.REGCodelistItemOptionalT;
import sk.is.urso.subject.v1.REGCodelistItemT;
import sk.is.urso.subject.v1.REGItemWithHistoryT;
import sk.is.urso.subject.v1.SimplifiedAddressT;
import sk.is.urso.subject.v1.StreetT;
import sk.is.urso.subject.v1.SubjectT;
import sk.is.urso.subject.v1.SubjectTypeT;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class InitializeRfo extends AbstractInitialize {

    private static final String RFO_1 = "rfo_1";
    private static final String ENUMERATION_COUNTRY_SLOVAKIA = "703";
    public static final String IDENTIFIER_TYPE_BIRTH_NUMBER = "9";
    public static final String FO_PREFIX = "FO";
    public static final String ZERO = "0";
    public static final String RFO = "RFO";
    public static final String SUBJECT = "SUBJECT";

    private int initializedFilesNum = 0;
    private int initializedRecordsNumPerFile = 0;
    private long foId;
    private AbstractRegPlugin pluginSubject;
    private AbstractRegPlugin pluginRfo;
    private static LocalDate NOW;
    private XPathFactory xPathfactory;
    private XPath xpath;

    public InitializeRfo(Initialize initialize) {
        super(initialize);
    }

    public void initialize(List<FileHeader> fileHeaderList, ZipFile zip) throws IOException {
        Initialize.log.info("Initializing register RFO in CSV mode.");
        prepareCsvFiles(RFO_1);
        prepareSubjectCsvFiles();
        foId = getSubjectFoIdSequence();
        try {
            pluginSubject = getRegisterPlugin(SUBJECT);
            pluginRfo = getRegisterPlugin(RFO);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException
                 | IllegalAccessException e) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Problem pri načítani pluginov rfo a subjektov.", e);
        }
        NOW = LocalDate.now();
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();

        for (FileHeader fileHeader : fileHeaderList) {
            if (fileHeader != null && !fileHeader.isDirectory()) {
                try {
                    initialize.allCount++;
                    try (ZipInputStream is = zip.getInputStream(fileHeader)) {
                        Document documentParsed = XmlUtils.parse(is.readAllBytes());

                        NodeList oexList = documentParsed.getElementsByTagName("OEX");
                        if (oexList.getLength() > 0) {
                            Node next = oexList.item(0);
                            while (next != null) {
                                Document document = readDocument((Element) next);
                                next = next.getNextSibling().getNextSibling();

                                String id = getSimplifiedValue(fileHeader, document);

                                Long entryId = initialize.rfoIndexRepository.findEntryId(id);
                                if (entryId == null) {
                                    checkInitializedRecordsNum();
                                    entryId = createValues(document);
                                    createSubject(document, entryId);
                                    initializedRecordsNumPerFile++;
                                    initialize.successCount++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    initialize.error("Error RFO.FileName = " + fileHeader.getFileName().substring(fileHeader.getFileName().indexOf("/") + 1) + "! "
                            + "Error stackTrace: " + Arrays.stream(e.getStackTrace()).findFirst() + ". Exception: ", e);
                    initialize.failCount++;
                    if (initialize.endAfterError) {
                        break;
                    }
                }
            }
        }
        closeCsvFiles();
        closeSubjectCsvFiles();
        setFoId();
        Initialize.log.info("Initialized " + initializedFilesNum + " records from file " + initialize.zipFilePath.substring(initialize.zipFilePath.indexOf("/") + 1) + ".");
    }

    private void createSubject(Document document, long entryId) {
        TOEXOEIO toexoeio = XmlUtils.parse(document, TOEXOEIO.class);
        SubjectT subjectT = reflectRfoDataToSubject(toexoeio, entryId);
        createSubjectValues(subjectT, document);
    }

    private void createSubjectValues(SubjectT subjectT, Document document) {
        try {
            String subjectId = generateSubjectId();
            subjectT.setSubjectID(subjectId);
            subjectT.setType(SubjectTypeT.FO);

            JAXBElement<SubjectT> subject = new ObjectFactory().createSubject(subjectT);
            String xml = XmlUtils.xmlToString(subject).replaceAll("\n", "").replaceAll("\t", "");

            long id = initialize.subjectDataRepository.getNextSequence();
            String effectiveFrom = getSimplifiedValue(document);
            document = XmlUtils.parse(xml);

            subjectDataWriter.write(getSubjectData(xml, id, effectiveFrom, NOW, subjectId) + "\n");
            subjectDataHistoryWriter.write(getSubjectDataHistory(xml, id, effectiveFrom, NOW) + "\n");
            subjectNaturalIdWriter.write(getSubjectNaturalId(id) + "\n");
            subjectIndexWriter.write(createIndexes(document, id, pluginSubject, effectiveFrom));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Document readDocument(Element oexElement) throws IOException, SAXException, ParserConfigurationException {
        Document newDocument = XmlUtils.newDocument();
        newDocument.appendChild(newDocument.adoptNode(oexElement.cloneNode(true)));

        Element rootNode = newDocument.getDocumentElement();
        rootNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootNode.setAttribute("xsi:schemaLocation", "http://www.dominanz.sk/UVZ/Reg/FO ../../uvz_reg_common/configuration/RFO_1/RFO_1_DATA.xsd");
        newDocument.renameNode(rootNode, "http://www.dominanz.sk/UVZ/Reg/FO", "fo:FO");
        XmlUtils.addNamespaceRecursive(newDocument.getFirstChild(), "http://www.dominanz.sk/UVZ/Reg/FO", "fo:");

        BufferedReader br = new BufferedReader(new StringReader(XmlUtils.xmlToString(newDocument)));
        return XmlUtils.parse(br.lines().collect(Collectors.joining("\n")).replaceAll("\n", "").replaceAll("\t", ""));
    }

    private String getSimplifiedValue(FileHeader fileHeader, Document document) {
        try {
            return ((Node) xpath.compile(ignoreNameSpace("fo:FO/fo:ID")).evaluate(document, XPathConstants.NODE)).getFirstChild().getNodeValue().toLowerCase(Locale.ROOT);
        } catch (XPathExpressionException e) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Xml " + fileHeader.getFileName().substring(fileHeader.getFileName().indexOf("/") + 1) + " neobsahuje typ zdroja registra.");
        }
    }

    private String getSimplifiedValue(Document document) throws XPathExpressionException {
        return ((Node) xpath.compile(ignoreNameSpace("fo:FO/fo:DN")).evaluate(document, XPathConstants.NODE)).getFirstChild().getNodeValue();
    }

    private String ignoreNameSpace(String xPath) {
        List<String> elementList = Arrays.asList(xPath.split("/"));

        StringBuilder stringBuilder = new StringBuilder();

        elementList.forEach(element -> {
            if (!element.equals("")) {
                element = element.replaceAll("[a-zA-Z]*.:", "");
                stringBuilder.append("/*[local-name() = '").append(element).append("']");
            }
        });
        return stringBuilder.toString();
    }

    private void checkInitializedRecordsNum() {
        if (initializedRecordsNumPerFile == Integer.parseInt(initialize.csvRecordsNum)) {
            try {
                initializedRecordsNumPerFile = 0;
                closeCsvFiles();
                closeSubjectCsvFiles();
                csvOrderNum++;
                prepareCsvFiles(RFO_1);
                prepareSubjectCsvFiles();
            } catch (IOException e) {
                throw new CommonException(HttpStatus.BAD_GATEWAY, "Initialization ended because of an error.", e);
            }
        }
    }

    private long createValues(Document document) {
        try {
            String xml = XmlUtils.xmlToString(document).replace('\n', ' ');

            long id = initialize.rfoDataRepository.getNextSequence();
            String effectiveFrom = getSimplifiedValue(document);

            dataWriter.write(getData(xml, id, effectiveFrom, NOW) + "\n");
            dataHistoryWriter.write(getDataHistory(xml, id, effectiveFrom, NOW) + "\n");
            naturalIdWriter.write(getNaturalId(id) + "\n");
            indexWriter.write(createIndexes(document, id, pluginRfo, effectiveFrom));

            return id;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String createIndexes(Document dataDocument, long id, AbstractRegPlugin plugin, String effectiveFrom) throws ParseException {

        try {
            plugin.prepareXmlForUpdate(null, dataDocument);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        AbstractRegEntityData data = plugin.createNewDataEntityForInsert(dataDocument);
        data.setId(id);
        data.setUcinnostOd(DateUtils.toDate(effectiveFrom));

        try {
            createRegisterIndexes(plugin, data, dataDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder indexRows = new StringBuilder();
        for (var ind : data.getEntityIndexes()) {
            indexRows.append("'").append(ind.getKontext()).append("';").append(ind.getAktualny()).append(";").append(effectiveFrom != null ? "'" + effectiveFrom + "'" : null).append(";").append(data.getUcinnostDo() != null ? "'" + DateUtils.toLocalDate(data.getUcinnostDo()) + "'" : null).append(";'").append(ind.getKluc()).append("';").append(ind.getSekvencia()).append(";'").append(ind.getHodnota().replace("'", "`")).append("';'").append(ind.getHodnotaZjednodusena().replace("'", "`")).append("';").append(id);
            indexRows.append("\n");
        }
        return indexRows.toString();
    }

    private String getData(String xml, long id, String effectiveFrom, LocalDate now) {
        return "false;'" + effectiveFrom + "';" + null + ";'" + new Timestamp(System.currentTimeMillis()) + "';'REG';'" + initialize.userSystemLogin + "';'" + now + "';'" + xml.replace("'", "`") + "';" + id;
    }

    private String getDataHistory(String xml, long id, String effectiveFrom, LocalDate now) {
        return "false;'" + effectiveFrom + "';" + null + ";'REG, DEV';'" + new Timestamp(System.currentTimeMillis()) + "';'" + initialize.userSystemLogin + "';'" + now + "';'" + xml.replace("'", "`") + "';" + id;
    }

    private String getNaturalId(long id) {
        return id + ";" + id;
    }

    /**
     * Format: id, xml, valid_from, effective_from, effective_to, disabled, last_reference_timestamp, subjectid, foid,  \"user\",  module.
     *
     * @param xml
     * @param id
     * @param effectiveFrom
     * @param now
     * @return
     */
    private String getSubjectData(String xml, long id, String effectiveFrom, LocalDate now, String subjectId) {
        return id + ";'" + xml.replace("'", "`") + "';'" + now + "';'" + effectiveFrom + "';" + null + ";false;'" + new Timestamp(System.currentTimeMillis()) + "';'" + subjectId + "';'" + foId + "';'" + initialize.userSystemLogin + "';'REG'";
    }

    /**
     * Format: entry_id, event_id, xml, valid_from, effective_from, effective_to, disabled, timestamp,  \"user\",  module.
     *
     * @param xml
     * @param entry_id
     * @param effectiveFrom
     * @param now
     * @return
     */
    private String getSubjectDataHistory(String xml, long entry_id, String effectiveFrom, LocalDate now) {
        return entry_id + "';'" + xml.replace("'", "`") + "';'" + now + "';'" + effectiveFrom + "';" + null + ";false;'" + new Timestamp(System.currentTimeMillis()) + "';'" + initialize.userSystemLogin + "';'REG'";
    }

    /**
     * Format: entry_id, natural_id.
     *
     * @param id
     * @return
     */
    private String getSubjectNaturalId(long id) {
        return id + ";" + id;
    }

    private String generateSubjectId() {
        String stringId = String.valueOf(++foId);
        return FO_PREFIX + ZERO.repeat(Math.max(0, 10 - stringId.length())) + stringId;
    }

    private void setFoId() {
        initialize.subjectDataRepository.setSubjectFoIdSequence(foId);
    }

    private long getSubjectFoIdSequence() {
        return initialize.subjectDataRepository.getSubjectFoIdSequence().longValue();
    }

    private SubjectT reflectRfoDataToSubject(TOEXOEIO toexoeio, long entryId) {

        sk.is.urso.subject.v1.ObjectFactory objectFactory = new sk.is.urso.subject.v1.ObjectFactory();
        XMLGregorianCalendar today = DateUtils.toXmlDate(NOW);

        SubjectT subject = objectFactory.createSubjectT();

        subject.setType(SubjectTypeT.FO);
        if (toexoeio.getPobList() != null) {
            List<SimplifiedAddressT> postalAddresses = subject.getAddress().stream().filter(address -> address.getPostalAddress() != null).collect(Collectors.toList());
            short maxSequence = (short) postalAddresses.stream().mapToInt(address -> address.getPostalAddress().getSequence()).max().orElse(0);

            List<SimplifiedAddressT> newAddresses = new ArrayList<>();
            if (toexoeio.getPobList().getPob() != null) {
                for (int i = 0; i < toexoeio.getPobList().getPob().size(); i++) {
                    TPOBPHRO pob = toexoeio.getPobList().getPob().get(i);
                    List<SimplifiedAddressT> addresses = subject.getAddress().stream().filter(address -> address.getAddressUsage().getItemCode().equalsIgnoreCase(String.valueOf(pob.getTp()))).collect(Collectors.toList());
                    for (SimplifiedAddressT address : addresses) {
                        if (address.getPostalAddress() != null) {
                            invalidateItemWithHistory(address.getPostalAddress());
                        }
                    }
                    if (i != 0 || !postalAddresses.isEmpty()) {
                        ++maxSequence;
                    }
                    newAddresses.add(createAddress(pob, maxSequence));
                }
            }
            subject.getAddress().addAll(newAddresses);
        }

        subject.getPhysicalPerson().forEach(fo -> {
            fo.setEffectiveTo(today);
            fo.setCurrent(false);
        });
        short maxSequence = (short) subject.getPhysicalPerson().stream().mapToInt(PhysicalPersonT::getSequence).max().orElse(0);

        PhysicalPersonT physicalPerson = createPhysicalPerson(toexoeio, !subject.getPhysicalPerson().isEmpty() ? ++maxSequence : 0);

        if (physicalPerson != null) {
            subject.getPhysicalPerson().add(physicalPerson);
            subject.getFormattedName().add(createFormattedName(physicalPerson, maxSequence));
        }
        if (toexoeio.getDcdList() != null && toexoeio.getDcdList().getDcd() != null) {
            maxSequence = (short) subject.getIdentificator().stream().mapToInt(IdentificatorT::getSequence).max().orElse(0);
            for (TDCDDCIO dcd : toexoeio.getDcdList().getDcd()) {
                List<IdentificatorT> identifiers = subject.getIdentificator().stream().filter(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(String.valueOf(dcd.getDd()))).collect(Collectors.toList());
                for (IdentificatorT identifier : identifiers) {
                    identifier.setEffectiveTo(today);
                    identifier.setCurrent(false);
                }
                subject.getIdentificator().add(createIdentifier(dcd, null, null, !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
            }
        }

        if (subject.getIdentificator().stream().noneMatch(identifier -> identifier.getIDType().getItemCode().equalsIgnoreCase(IDENTIFIER_TYPE_BIRTH_NUMBER))) {
            subject.getIdentificator().add(createIdentifier(null, IDENTIFIER_TYPE_BIRTH_NUMBER, toexoeio.getRc(), !subject.getIdentificator().isEmpty() ? ++maxSequence : 0));
        }

        ExternalRegisterReferenceT rfoReference = new ExternalRegisterReferenceT();
        rfoReference.setEntryId(String.valueOf(entryId));
        rfoReference.setExternalId(toexoeio.getId());
        subject.setRfoReference(rfoReference);

        return subject;
    }

    private IdentificatorT createIdentifier(TDCDDCIO dcd, String type, String value, short sequence) {
        IdentificatorT identifier = new IdentificatorT();
        setAttributes(identifier, sequence);

        REGCodelistItemT idType = new REGCodelistItemT();
        idType.setItemCode(dcd != null ? String.valueOf(dcd.getDd()) : type);
        identifier.setIDType(idType);
        identifier.setIDValue(dcd != null ? String.valueOf(dcd.getCd()) : value);
        return identifier;
    }

    private void invalidateItemWithHistory(REGItemWithHistoryT itemWithHistory) {
        if (DateUtils.toDate(itemWithHistory.getEffectiveFrom()).after(new Date())) {
            itemWithHistory.setValid(false);
        } else {
            itemWithHistory.setEffectiveTo(DateUtils.toXmlDate(DateUtils.toLocalDate(new Date()).minusDays(1)));
        }
    }

    private FormattedNameT createFormattedName(PhysicalPersonT physicalPerson, short sequence) {
        FormattedNameT formattedName = new FormattedNameT();
        setAttributes(formattedName, sequence);
        formattedName.setName(prepareName(physicalPerson.getName(), physicalPerson.getSurname(), physicalPerson.getTitleBefore(), physicalPerson.getTitleAfter()));
        return formattedName;
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

    private PhysicalPersonT createPhysicalPerson(TOEXOEIO toexoeio, short sequence) {
        PhysicalPersonT physicalPerson = new PhysicalPersonT();
        setAttributes(physicalPerson, sequence);
        boolean empty = true;

        if (toexoeio.getPi() != null) {
            REGCodelistItemT gender = new REGCodelistItemT();
            gender.setItemCode(String.valueOf(toexoeio.getPi().intValue()));
            physicalPerson.setGender(gender);
            empty = false;
        }

        if (toexoeio.getSn() != null && toexoeio.getSn() == 211) {
            REGCodelistItemT nationality = new REGCodelistItemT();
            nationality.setItemCode(ENUMERATION_COUNTRY_SLOVAKIA);
            physicalPerson.setNationality(nationality);
            empty = false;
        }

        if (toexoeio.getMosList() != null && toexoeio.getMosList().getMos() != null) {
            toexoeio.getMosList().getMos().sort(Comparator.comparing(TMOSO::getPo));
            physicalPerson.setName(!toexoeio.getMosList().getMos().isEmpty() ? toexoeio.getMosList().getMos().get(0).getMe() : null);
            empty = false;
        }
        if (toexoeio.getPriList() != null && toexoeio.getPriList().getPri() != null) {
            toexoeio.getPriList().getPri().sort(Comparator.comparing(TPRIO::getPo));
            physicalPerson.setSurname(!toexoeio.getPriList().getPri().isEmpty() ? toexoeio.getPriList().getPri().get(0).getPr() : null);
            empty = false;
        }
        if (toexoeio.getRprList() != null && toexoeio.getRprList().getRpr() != null) {
            toexoeio.getRprList().getRpr().sort(Comparator.comparing(TRPRO::getPo));
            physicalPerson.setBirthname(!toexoeio.getRprList().getRpr().isEmpty() ? toexoeio.getRprList().getRpr().get(0).getRp() : null);
            empty = false;
        }
        physicalPerson.setBirthDate(toexoeio.getDn());
        if (empty) {
            return null;
        }
        return physicalPerson;
    }

    private void setAttributes(REGItemWithHistoryT itemWithHistory, short sequence) {
        itemWithHistory.setSequence(sequence);
        itemWithHistory.setCurrent(true);
        itemWithHistory.setValid(true);
        itemWithHistory.setEffectiveFrom(DateUtils.nowXmlDate());
    }

    private SimplifiedAddressT createAddress(TPOBPHRO pob, short sequence) {
        SimplifiedAddressT address = new SimplifiedAddressT();

        REGCodelistItemT addressUsage = new REGCodelistItemT();
        addressUsage.setItemCode(String.valueOf(pob.getTp()));
        address.setAddressUsage(addressUsage);

        PostalAddressT postalAddress = null;

        REGCodelistItemOptionalT municipality = null;
        REGCodelistItemOptionalT region = new REGCodelistItemOptionalT();
        REGCodelistItemT state = null;
        StreetT street = null;

        if (Boolean.parseBoolean(pob.getMp())) {
            if (pob.getOo() != null && !pob.getOo().isEmpty()) {
                municipality = new REGCodelistItemOptionalT();
                municipality.setItemValue(pob.getOo());
            }

            street = new StreetT();
            street.setStreetName(pob.getUm() != null && !pob.getUm().isEmpty() ? pob.getUm() : null);
            street.setStreetNumber(pob.getOs() != null && !pob.getOs().isEmpty() ? pob.getOs() : null);
            street.setRegistrationNumber(pob.getSi() != null && !pob.getSi().isEmpty() ? pob.getSi() : null);
            street.setBuildingPart(pob.getCu() != null && !pob.getCu().isEmpty() ? pob.getCu() : null);

            if (pob.getSt() != null) {
                state = new REGCodelistItemT();
                state.setItemCode(String.valueOf(pob.getSt()));
                state.setItemValue(pob.getNs() != null && !pob.getNs().isEmpty() ? pob.getNs() : null);
            }
            if (pob.getRegList() != null) {
                postalAddress = new PostalAddressT();
                region.setItemValue(createForeignAddressRegion(pob.getRegList().getReg()));
                postalAddress.setRegion(region);
            }
        } else {
            if (pob.getNo() != null && !pob.getNo().isEmpty()) {
                municipality = new REGCodelistItemOptionalT();
                municipality.setItemValue(pob.getNo());
            }
            street = new StreetT();
            street.setRegistrationNumber(pob.getSc() != null ? String.valueOf(pob.getSc()) : null);
            street.setBuilding(pob.getDi() != null ? String.valueOf(pob.getDi().intValue()) : null);
            street.setStreetName(pob.getNu() != null && !pob.getNu().isEmpty() ? pob.getNu() : null);
            street.setStreetNumber(pob.getOl() != null && !pob.getOl().isEmpty() ? pob.getOl() : null);
            street.setBuildingPart(pob.getCb() != null && !pob.getCb().isEmpty() ? pob.getCb() : null);

            if (pob.getSi() != null && !pob.getSi().isEmpty()) {
                state = new REGCodelistItemT();
                state.setItemCode(pob.getSi());
                state.setItemValue(pob.getNs() != null && !pob.getNs().isEmpty() ? pob.getNs() : null);
            }
        }
        if (municipality != null) {
            if (postalAddress == null) {
                postalAddress = new PostalAddressT();
            }
            postalAddress.setMunicipality(municipality);
        }
        if (state != null) {
            if (postalAddress == null) {
                postalAddress = new PostalAddressT();
            }
            postalAddress.setState(state);
        }
        if (postalAddress == null) {
            postalAddress = new PostalAddressT();
        }
        postalAddress.setStreet(street);
        postalAddress.setAddressInSyncWithREG(true);
        setAttributes(postalAddress, sequence);
        address.setPostalAddress(postalAddress);
        return address;
    }

    private String createForeignAddressRegion(List<TREGO> regList) {
        regList.sort(Comparator.comparing(TREGO::getPo));
        String region = null;
        for (TREGO reg : regList) {
            if (region == null) {
                region = reg.getRe();
            } else {
                String tmpItemCode = region.concat(" ").concat(reg.getRe());
                region = tmpItemCode.length() + 3 <= 255 ? tmpItemCode : region.concat("...");
            }
        }
        return region;
    }
}
