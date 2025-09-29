package sk.is.urso.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.alfa.exception.CommonException;
import org.alfa.utils.DateUtils;
import org.alfa.utils.Utils;
import org.alfa.utils.XmlUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sk.is.urso.model.HodnotaCiselnika;
import sk.is.urso.plugin.SubjectReg1;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.subject.v1.CorporateBodyT;
import sk.is.urso.subject.v1.ElectronicAddressT;
import sk.is.urso.subject.v1.EmailT;
import sk.is.urso.subject.v1.ExternalRegisterReferenceT;
import sk.is.urso.subject.v1.FormattedAddressT;
import sk.is.urso.subject.v1.FormattedNameT;
import sk.is.urso.subject.v1.IdentificatorT;
import sk.is.urso.subject.v1.ObjectFactory;
import sk.is.urso.subject.v1.PhoneT;
import sk.is.urso.subject.v1.PoboxT;
import sk.is.urso.subject.v1.PostalAddressT;
import sk.is.urso.subject.v1.REGCodelistItemOptionalT;
import sk.is.urso.subject.v1.REGCodelistItemT;
import sk.is.urso.subject.v1.REGItemWithHistoryT;
import sk.is.urso.subject.v1.SimplifiedAddressT;
import sk.is.urso.subject.v1.StreetT;
import sk.is.urso.subject.v1.SubjectT;
import sk.is.urso.subject.v1.SubjectTypeT;

import javax.xml.bind.JAXBElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class InitializeRpo extends AbstractInitialize {

    private static final String RPO_1 = "rpo_1";
    private static final String SUBJECT = "SUBJECT";
    private static final String RPO = "RPO";
    private long poId;
    private static final String PO_PREFIX = "PO";
    private static final String ZERO = "0";
    private static final String EFFECTIVE_FROM = "EffectiveFrom";
    private static final String EFFECTIVE_TO = "EffectiveTo";
    private static final String CURRENT = "Current";
    private static final String PO_PHYSICAL_ADDRESS_XPATH = "po:PO/po:PhysicalAddress";
    private static final String PO_PHYSICAL_ADDRESS_FORMAT_XPATH = "po:PO[1]/po:PhysicalAddress[%d]";
    private static final String PO_TELEPHONE_ADDRESS_NUMBER_FORMATTED_NUMBER_XPATH = "po:PO/po:TelephoneAddress/po:Number/po:FormattedNumber";
    private static final String PO_INTERNET_ADDRESS_ADDRESS_XPATH = "po:PO/po:InternetAddress/po:Address";
    private static final String PO_CORPORATE_BODY_FULL_NAME_XPATH = "po:PO/po:CorporateBodyFullName";
    private static final String PO_LEGAL_FORM_XPATH = "po:PO/po:LegalForm";
    private static final String PO_SK_NACE_CODELIST_CODELIST_ITEM_ITEM_CODE_XPATH = "po:PO/po:SkNaceMain/cs:Codelist/cs:CodelistItem/cs:ItemCode";
    private static final String PO_CORPORATE_BODY_ALTERNATIVE_NAME_XPATH = "po:PO/po:CorporateBodyAlternativeName";
    private static final String PO_ORGANIZATION_UNIT_ORGANIZATION_UNIT_NAME_XPATH = "po:PO/po:OrganizationUnit/po:OrganizationUnitName";
    private static final String PO_LEGAL_FORM_FORMAT_XPATH = "po:PO[1]/po:LegalForm[%d]";
    private static final String PO_ID_CURRENT_XPATH = "po:PO/po:ID[@Current='true']";
    private static final String PO_ID_XPATH = "po:PO/po:ID";
    private static final String PO_TAX_IDENTIFICATION_NUMBER_XPATH = "po:PO/po:TaxIdentificationNumber";
    private static final String PO_VAT_IDENTIFICATION_NUMBER_XPATH = "po:PO/po:VatIdentificationNumber";
    private static final String PO_ID_CURRENT_IDENTIFIER_VALUE_XPATH = "po:PO/po:ID[@Current='true']/cs:IdentifierValue";
    private static final String PO_ID_IDENTIFIER_VALUE_XPATH = "po:PO/po:ID/cs:IdentifierValue";
    private static final String CODELIST_CODELIST_ITEM_ITEM_CODE_XPATH = "/cs:Codelist/cs:CodelistItem/cs:ItemCode";
    private static final String LEGAL_FORM_CODELIST_CODE = "CUREG000011";
    private static final String BUILDING_INDEX_XPATH = "/po:BuildingIndex";
    private static final String PROPERTY_REGISTRATION_NUMBER_XPATH = "/po:PropertyRegistrationNumber";
    private static final String BUILDING_NUMBER_XPATH = "/po:BuildingNumber";
    private static final String STREET_NAME_XPATH = "/po:StreetName";
    private static final String DELIVERY_ADDRESS_POST_OFFICE_BOX_XPATH = "/po:DeliveryAddress/po:PostOfficeBox";
    private static final String DELIVERY_ADDRESS_POSTAL_CODE_XPATH = "/po:DeliveryAddress/po:PostalCode";
    private static final String CODELIST_CODELIST_ITEM_ITEM_CODE_FORMAT_XPATH = "/po:%s/cs:Codelist/cs:CodelistItem/cs:ItemCode";
    private static final String NON_CODELIST_DATA_FORMAT_XPATH = "/po:%s/cs:NonCodelistData";
    private static final String ADDRESS_TYPE_ADDRESS_CLASS_XPATH = "/po:AddressType/po:AddressClass";
    private static final String ADDRESS_TYPE_ADDRESS_CLASS_CODELIST_CODELIST_ITEM_ITEM_CODE_XPATH = "/po:AddressType/po:AddressClass/cs:Codelist/cs:CodelistItem/cs:ItemCode";
    private static final String ADDRESS_TYPE_ADDRESS_CLASS_NON_CODELIST_DATA_XPATH = "/po:AddressType/po:AddressClass/cs:NonCodelistData";
    private static final String ADDRESS_TYPE_ADDRESS_CLASS_CODELIST_CODELIST_ITEM_ITEM_NAME_XPATH = "/po:AddressType/po:AddressClass/cs:Codelist/cs:CodelistItem/cs:ItemName";
    private static final String ADDRESS_USAGE_CODELIST_CODE = "CUREG000016";
    private static final String MUNICIPALITY = "Municipality";
    private static final String MUNICIPALITY_CODELIST_CODE = "CUREG000005";
    private static final String REGION = "Region";
    private static final String REGION_CODELIST_CODE = "CUREG000006";
    private static final String COUNTRY = "Country";
    private static final String COUNTRY_CODELIST_CODE = "CUREG000008";
    public static final String CORRESPONDENCE_ADDRESS = "11";
    public static final String ELECTRONIC_ADDRESS = "12";
    public static final String TELEPHONE_ADDRESS = "13";
    private static final int ENUMERATION_VALUE_LIMIT = 1000;

    private AbstractRegPlugin pluginSubject;
    private static LocalDate NOW;
    private int initializedRecordsNumPerFile = 0;

    public InitializeRpo(Initialize initialize) {
        super(initialize);
    }

    public void initialize(List<FileHeader> fileHeaderList, ZipFile zip) throws IOException {
        Initialize.log.info("Initializing register RPO in CSV mode.");
        prepareCsvFiles(RPO_1);
        prepareSubjectCsvFiles();
        poId = getSubjectPoIdSequence();
        try {
            pluginSubject = getRegisterPlugin(SUBJECT);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException
                 | IllegalAccessException e) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Problem pri načítani pluginov rfo a subjektov.", e);
        }
        NOW = LocalDate.now();

        for (FileHeader fileHeader : fileHeaderList) {
            if (fileHeader != null && !fileHeader.isDirectory()) {
                try {
                    initialize.allCount++;
                    Document document = readDocument(zip, fileHeader);

                    String sourceRegisterId = getSimplifiedValue(fileHeader, document, "po:PO/po:SourceRegisterId");
                    String sourceRegisterType = getSimplifiedValue(fileHeader, document, "po:PO/po:Source/po:SourceRegister/cs:Codelist/cs:CodelistItem/cs:ItemCode");

                    Long entryId = initialize.rpoIndexRepository.findEntryId(sourceRegisterId, sourceRegisterType);
                    if (entryId == null) {
                        checkInitializedRecordsNum();
                        entryId = createValues(document);
                        createSubject(document, entryId, sourceRegisterId);
                        initializedRecordsNumPerFile++;
                        initialize.successCount++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    initialize.error("Error RPO.FileName = " + fileHeader.getFileName().substring(fileHeader.getFileName().indexOf("/") + 1) + "! "
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
        setPoId();

        int initializedFilesNum = 0;
        Initialize.log.info("Initialized " + initializedFilesNum + " records from file " + initialize.zipFilePath.substring(initialize.zipFilePath.indexOf("/") + 1) + ".");
    }

    private void setPoId() {
        initialize.subjectDataRepository.setSubjectPoIdSequence(poId);
    }

    private void createSubject(Document document, Long entryId, String sourceRegisterId) throws XPathExpressionException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        SubjectT subject = reflectData(document, entryId, sourceRegisterId);
        createSubjectValues(subject, document);
    }

    private void createSubjectValues(SubjectT subjectT, Document document) {
        try {
            LocalDate now = LocalDate.now();

            String subjectId = generateSubjectId();
            subjectT.setSubjectID(subjectId);
            subjectT.setType(SubjectTypeT.PO);

            JAXBElement<SubjectT> subject = new ObjectFactory().createSubject(subjectT);
            String xml = XmlUtils.xmlToString(subject).replaceAll("\n", "").replaceAll("\t", "");

            long id = initialize.subjectDataRepository.getNextSequence();
            String effectiveFrom = null;
            Node idNode = getSimplifiedValue(document);
            if (idNode != null && idNode.hasAttributes()) {
                effectiveFrom = idNode.getAttributes().getNamedItem("EffectiveFrom") != null ? idNode.getAttributes().getNamedItem("EffectiveFrom").getNodeValue() : now.toString();
            }
            document = XmlUtils.parse(xml);

            subjectDataWriter.write(getSubjectData(xml, id, effectiveFrom, NOW, subjectId) + "\n");
            subjectDataHistoryWriter.write(getDataHistory(xml, id, effectiveFrom, NOW) + "\n");
            subjectNaturalIdWriter.write(getNaturalId(id) + "\n");
            subjectIndexWriter.write(createIndexes(document, id, pluginSubject, effectiveFrom));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private String getSubjectData(String xml, long id, String effectiveFrom, LocalDate now, String subjectId) {
        return id + ";'" + xml.replace("'", "`") + "';'" + now + "';'" + effectiveFrom + "';" + null + ";false;'" + new Timestamp(System.currentTimeMillis()) + "';'" + subjectId + "';'" + poId + "';'" + initialize.userSystemLogin + "';'REG'";
    }

    private String generateSubjectId() {
        String stringId = String.valueOf(++poId);
        return PO_PREFIX + ZERO.repeat(Math.max(0, 10 - stringId.length())) + stringId;
    }

    public SubjectT reflectData(Document rpoDataXml, Long entryId, String sourceRegisterId) throws XPathExpressionException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        AbstractRegPlugin plugin = getRegisterPlugin(RPO);
        SubjectT subject = new SubjectT();

        NodeList rpoPhysicalAddresses = (NodeList) plugin.getXPath().compile(PO_PHYSICAL_ADDRESS_XPATH).evaluate(rpoDataXml, XPathConstants.NODESET);
        if (rpoPhysicalAddresses.getLength() > 0) {

            for (int i = 0; i < rpoPhysicalAddresses.getLength(); i++) {

                Element physicalAddress = (Element) rpoPhysicalAddresses.item(i);
                if (physicalAddress.getAttribute(EFFECTIVE_TO).isEmpty() || DateUtils.toXmlDate(physicalAddress.getAttribute(EFFECTIVE_FROM)).compare(DateUtils.toXmlDate(physicalAddress.getAttribute(EFFECTIVE_TO))) <= 0) {
                    SimplifiedAddressT rpoPhysicalAddress = createPhysicalAddress(String.format(PO_PHYSICAL_ADDRESS_FORMAT_XPATH, (i + 1)), physicalAddress, (short) i, rpoDataXml, plugin.getXPath());
                    subject.getAddress().add(rpoPhysicalAddress);
                }
            }
        }

        NodeList telephoneAddresses = (NodeList) plugin.getXPath().compile(PO_TELEPHONE_ADDRESS_NUMBER_FORMATTED_NUMBER_XPATH).evaluate(rpoDataXml, XPathConstants.NODESET);
        if (telephoneAddresses.getLength() > 0) {

            SimplifiedAddressT rpoPhoneAddress = new SimplifiedAddressT();
            REGCodelistItemT addressUsage = new REGCodelistItemT();
            addressUsage.setItemCode(TELEPHONE_ADDRESS);
            rpoPhoneAddress.setAddressUsage(addressUsage);

            for (int i = 0; i < telephoneAddresses.getLength(); i++) {
                Element telephoneAddress = (Element) telephoneAddresses.item(i);
                if (telephoneAddress.getAttribute(EFFECTIVE_TO).isEmpty() || DateUtils.toXmlDate(telephoneAddress.getAttribute(EFFECTIVE_FROM)).compare(DateUtils.toXmlDate(telephoneAddress.getAttribute(EFFECTIVE_TO))) <= 0) {
                    rpoPhoneAddress.getPhones().add(createPhone(telephoneAddress, (short) i));
                }
            }
            subject.getAddress().add(rpoPhoneAddress);
        }

        NodeList internetAddresses = (NodeList) plugin.getXPath().compile(PO_INTERNET_ADDRESS_ADDRESS_XPATH).evaluate(rpoDataXml, XPathConstants.NODESET);
        if (internetAddresses.getLength() > 0) {

            SimplifiedAddressT rpoElectronicAddress = new SimplifiedAddressT();
            REGCodelistItemT addressUsage = new REGCodelistItemT();
            addressUsage.setItemCode(ELECTRONIC_ADDRESS);
            rpoElectronicAddress.setAddressUsage(addressUsage);
            rpoElectronicAddress.setElectronicAddress(new ElectronicAddressT());

            for (int i = 0; i < internetAddresses.getLength(); i++) {
                Element internetAddress = (Element) internetAddresses.item(i);
                if (internetAddress.getAttribute(EFFECTIVE_TO).isEmpty() || DateUtils.toXmlDate(internetAddress.getAttribute(EFFECTIVE_FROM)).compare(DateUtils.toXmlDate(internetAddress.getAttribute(EFFECTIVE_TO))) <= 0) {
                    rpoElectronicAddress.getElectronicAddress().getEmail().add(createEmail(internetAddress, (short) i));
                }
            }
            subject.getAddress().add(rpoElectronicAddress);
        }

        NodeList corporateBodyFullNames = (NodeList) plugin.getXPath().compile(PO_CORPORATE_BODY_FULL_NAME_XPATH).evaluate(rpoDataXml, XPathConstants.NODESET);
        if (corporateBodyFullNames.getLength() > 0) {

            for (int i = 0; i < corporateBodyFullNames.getLength(); i++) {

                Element corporateBodyFullName = (Element) corporateBodyFullNames.item(i);
                if (corporateBodyFullName.getAttribute(EFFECTIVE_TO).isEmpty() || DateUtils.toXmlDate(corporateBodyFullName.getAttribute(EFFECTIVE_FROM)).compare(DateUtils.toXmlDate(corporateBodyFullName.getAttribute(EFFECTIVE_TO))) <= 0) {
                    FormattedNameT newFormattedName = createFormattedName(corporateBodyFullName, (short) i);
                    subject.getFormattedName().add(newFormattedName);
                }
            }
        }

        NodeList legalForms = (NodeList) plugin.getXPath().compile(PO_LEGAL_FORM_XPATH).evaluate(rpoDataXml, XPathConstants.NODESET);
        String skNace = (String) plugin.getXPath().compile(PO_SK_NACE_CODELIST_CODELIST_ITEM_ITEM_CODE_XPATH).evaluate(rpoDataXml, XPathConstants.STRING);

        for (int i = 0; i < legalForms.getLength(); i++) {

            Element legalForm = (Element) legalForms.item(i);
            if (legalForm.getAttribute(EFFECTIVE_TO).isEmpty() || DateUtils.toXmlDate(legalForm.getAttribute(EFFECTIVE_FROM)).compare(DateUtils.toXmlDate(legalForm.getAttribute(EFFECTIVE_TO))) <= 0) {
                String effectiveFrom = legalForm.getAttribute(EFFECTIVE_FROM);
                String effectiveTo = legalForm.getAttribute(EFFECTIVE_TO);
                String datesXPath = !effectiveTo.isEmpty() ? "[@EffectiveFrom='" + effectiveFrom + "' and @EffectiveTo='" + effectiveTo + "']" : "[@EffectiveFrom='" + effectiveFrom + "']";
                String corporateBodyAlternativeName = (String) plugin.getXPath().compile(PO_CORPORATE_BODY_ALTERNATIVE_NAME_XPATH + datesXPath).evaluate(rpoDataXml, XPathConstants.STRING);
                NodeList organizationUnitNames = (NodeList) plugin.getXPath().compile(PO_ORGANIZATION_UNIT_ORGANIZATION_UNIT_NAME_XPATH + datesXPath).evaluate(rpoDataXml, XPathConstants.NODESET);
                CorporateBodyT rpoCorporateBody = createCorporateBody(String.format(PO_LEGAL_FORM_FORMAT_XPATH, (i + 1)), legalForm, corporateBodyAlternativeName, organizationUnitNames, skNace, (short) i, rpoDataXml, plugin.getXPath());

                subject.getCorporateBody().add(rpoCorporateBody);
            }
        }

        short sequence = 0;
        Element rpoId = (Element) plugin.getXPath().compile(PO_ID_CURRENT_XPATH).evaluate(rpoDataXml, XPathConstants.NODE);
        if (rpoId == null) {
            rpoId = (Element) plugin.getXPath().compile(PO_ID_XPATH).evaluate(rpoDataXml, XPathConstants.NODE);
        }

        String taxIdentificationNumber = (String) plugin.getXPath().compile(PO_TAX_IDENTIFICATION_NUMBER_XPATH).evaluate(rpoDataXml, XPathConstants.STRING);
        if (!taxIdentificationNumber.isEmpty()) {
            IdentificatorT rpoIdentifier = createIdentifier(taxIdentificationNumber, rpoId, SubjectReg1.IDENTIFIER_TYPE_DIC, sequence++);
            subject.getIdentificator().add(rpoIdentifier);
        }

        String vatIdentificationNumber = (String) plugin.getXPath().compile(PO_VAT_IDENTIFICATION_NUMBER_XPATH).evaluate(rpoDataXml, XPathConstants.STRING);
        if (!vatIdentificationNumber.isEmpty()) {
            IdentificatorT rpoIdentifier = createIdentifier(vatIdentificationNumber, rpoId, SubjectReg1.IDENTIFIER_TYPE_IC_DPH, sequence++);
            subject.getIdentificator().add(rpoIdentifier);
        }

        String currentIdentifierValue = (String) plugin.getXPath().compile(PO_ID_CURRENT_IDENTIFIER_VALUE_XPATH).evaluate(rpoDataXml, XPathConstants.STRING);
        if (!currentIdentifierValue.isEmpty()) {
            IdentificatorT rpoIdentifier = createIdentifier(currentIdentifierValue, rpoId, SubjectReg1.IDENTIFIER_TYPE_ICO, sequence);
            subject.getIdentificator().add(rpoIdentifier);
        } else {
            String identifierValue = (String) plugin.getXPath().compile(PO_ID_IDENTIFIER_VALUE_XPATH).evaluate(rpoDataXml, XPathConstants.STRING);
            if (!identifierValue.isEmpty()) {
                IdentificatorT rpoIdentifier = createIdentifier(identifierValue, rpoId, SubjectReg1.IDENTIFIER_TYPE_ICO, sequence);
                subject.getIdentificator().add(rpoIdentifier);
            }
        }

        ExternalRegisterReferenceT rpoReference = new ExternalRegisterReferenceT();
        rpoReference.setEntryId(String.valueOf(entryId));
        rpoReference.setExternalId(sourceRegisterId);
        subject.setRpoReference(rpoReference);

        return subject;
    }

    private FormattedAddressT createFormattedAddress(PostalAddressT postalAddress) {
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

        FormattedAddressT formattedAddress = initialize.modelMapperConfig.getModelMapper().map(postalAddress, FormattedAddressT.class);
        formattedAddress.setAddress(address.toString());
        return formattedAddress;
    }

    private IdentificatorT createIdentifier(String value, Element rpoId, String itemCode, short sequence) {
        IdentificatorT identifier = new IdentificatorT();
        setAttributes(identifier, rpoId, sequence);

        REGCodelistItemT idType = new REGCodelistItemT();
        idType.setItemCode(itemCode);

        identifier.setIDType(idType);
        identifier.setIDValue(value);
        return identifier;
    }

    private CorporateBodyT createCorporateBody(String legalFormXPath, Element legalForm, String corporateBodyAlternativeName, NodeList organizationUnitNames, String skNaceItemCode, short sequence, Document rpoXml, XPath xpath) throws XPathExpressionException {
        CorporateBodyT corporateBody = new CorporateBodyT();
        setAttributes(corporateBody, legalForm, sequence);

        if (!corporateBodyAlternativeName.isEmpty()) {
            corporateBody.setAlternativeName(corporateBodyAlternativeName);
        }

        for (int i = 0; i < organizationUnitNames.getLength(); i++) {
            corporateBody.getOrgUnitName().add(organizationUnitNames.item(i).getFirstChild().getNodeValue());
        }

        if (!skNaceItemCode.isEmpty()) {
            String itemCode = skNaceItemCode.substring(0, 2) + "." + skNaceItemCode.substring(2, 4) + "." + skNaceItemCode.substring(4);
            REGCodelistItemT nace = new REGCodelistItemT();
            nace.setItemCode(itemCode);
            corporateBody.setNACE(nace);
        }

        corporateBody.setLegalForm(createCodelistItem(legalFormXPath + CODELIST_CODELIST_ITEM_ITEM_CODE_XPATH, LEGAL_FORM_CODELIST_CODE, rpoXml, xpath));
        return corporateBody;
    }

    private REGCodelistItemT createCodelistItem(String itemCodeXpath, String codelistCode, Document rpoXml, XPath xpath) throws XPathExpressionException {
        String itemCode = (String) xpath.compile(itemCodeXpath).evaluate(rpoXml, XPathConstants.STRING);
        if (!itemCode.isEmpty()) {
            REGCodelistItemT codelistItem = new REGCodelistItemT();
            codelistItem.setItemCode(itemCode);
            codelistItem.setCodeListCode(codelistCode);
            return codelistItem;
        }
        return null;
    }

    private REGCodelistItemOptionalT createCodelistItemOptional(String itemCodeXpath, String nonCodelistXPath, String codelistCode, Document rpoXml, XPath xpath) throws XPathExpressionException {
        REGCodelistItemOptionalT codelistItemOptional = new REGCodelistItemOptionalT();

        String itemCode = (String) xpath.compile(itemCodeXpath).evaluate(rpoXml, XPathConstants.STRING);
        if (itemCode.isEmpty()) {
            String nonCodelist = (String) xpath.compile(nonCodelistXPath).evaluate(rpoXml, XPathConstants.STRING);
            if (nonCodelist.isEmpty()) {
                return null;
            }
            codelistItemOptional.setItemValue(nonCodelist);
        } else {
            codelistItemOptional.setItemCode(itemCode);
            codelistItemOptional.setCodeListCode(codelistCode);
        }
        return codelistItemOptional;
    }

    private FormattedNameT createFormattedName(Element formattedName, short sequence) {
        FormattedNameT formattedNameT = new FormattedNameT();
        setAttributes(formattedNameT, formattedName, sequence);
        formattedNameT.setName(formattedName.getFirstChild().getNodeValue());
        return formattedNameT;
    }

    private EmailT createEmail(Node email, short sequence) {
        EmailT emailT = new EmailT();
        setAttributes(emailT, null, sequence);
        emailT.setEmail(email.getFirstChild().getNodeValue());
        return emailT;
    }

    private PhoneT createPhone(Node phone, short sequence) {
        PhoneT phoneT = new PhoneT();
        setAttributes(phoneT, null, sequence);
        phoneT.setPhone(phone.getFirstChild().getNodeValue().replaceAll("\\s", ""));
        return phoneT;
    }

    private void setAttributes(REGItemWithHistoryT subjectElement, Element rpoElement, short sequence) {
        if (rpoElement != null) {
            subjectElement.setCurrent(Boolean.parseBoolean(rpoElement.getAttribute(CURRENT)));
            subjectElement.setEffectiveFrom(DateUtils.toXmlDate(rpoElement.getAttribute(EFFECTIVE_FROM)));
            String effectiveTo = rpoElement.getAttribute(EFFECTIVE_TO);
            if (!effectiveTo.isEmpty()) {
                subjectElement.setEffectiveTo(DateUtils.toXmlDate(effectiveTo));
            }
        } else {
            subjectElement.setEffectiveFrom(DateUtils.nowXmlDate());
            subjectElement.setCurrent(true);
        }
        subjectElement.setSequence(sequence);
        subjectElement.setValid(true);
    }

    private SimplifiedAddressT createPhysicalAddress(String context, Element rpoPhysicalAddress, short sequence, Document rpoXml, XPath xpath) throws XPathExpressionException {
        SimplifiedAddressT address = new SimplifiedAddressT();

        REGCodelistItemT addressUsage = new REGCodelistItemT();
        Node addressType = (Node) xpath.compile(context + ADDRESS_TYPE_ADDRESS_CLASS_XPATH).evaluate(rpoXml, XPathConstants.NODE);
        if (addressType == null) {
            addressUsage.setItemCode(CORRESPONDENCE_ADDRESS);
        } else {
            String itemCode = (String) xpath.compile(context + ADDRESS_TYPE_ADDRESS_CLASS_CODELIST_CODELIST_ITEM_ITEM_CODE_XPATH).evaluate(rpoXml, XPathConstants.STRING);
            if (itemCode.isEmpty()) {
                String nonCodelistData = (String) xpath.compile(context + ADDRESS_TYPE_ADDRESS_CLASS_NON_CODELIST_DATA_XPATH).evaluate(rpoXml, XPathConstants.STRING);
                if (nonCodelistData.isEmpty()) {
                    addressUsage.setItemCode(CORRESPONDENCE_ADDRESS);
                } else {
                    addressUsage.setItemValue(nonCodelistData);
                }
            } else {
                String itemName = (String) xpath.compile(context + ADDRESS_TYPE_ADDRESS_CLASS_CODELIST_CODELIST_ITEM_ITEM_NAME_XPATH).evaluate(rpoXml, XPathConstants.STRING);
                List<HodnotaCiselnika> enumerationValues = initialize.hodnotaCiselnikaRepository.findAllByCodelistCodeAndItemName(ADDRESS_USAGE_CODELIST_CODE, itemName, ENUMERATION_VALUE_LIMIT);
                if (!enumerationValues.isEmpty()) {
                    addressUsage.setItemCode(enumerationValues.get(0).getKodPolozky());
                } else {
                    addressUsage.setItemCode(CORRESPONDENCE_ADDRESS);
                }
            }
        }
        address.setAddressUsage(addressUsage);

        PostalAddressT postalAddress = new PostalAddressT();
        setAttributes(postalAddress, rpoPhysicalAddress, sequence);
        postalAddress.setAddressInSyncWithREG(true);

        String itemCode = context + CODELIST_CODELIST_ITEM_ITEM_CODE_FORMAT_XPATH;
        String nonCodelist = context + NON_CODELIST_DATA_FORMAT_XPATH;

        postalAddress.setMunicipality(createCodelistItemOptional(String.format(itemCode, MUNICIPALITY), String.format(nonCodelist, MUNICIPALITY), MUNICIPALITY_CODELIST_CODE, rpoXml, xpath));
        postalAddress.setRegion(createCodelistItemOptional(String.format(itemCode, REGION), String.format(nonCodelist, REGION), REGION_CODELIST_CODE, rpoXml, xpath));
        postalAddress.setState(createCodelistItem(String.format(itemCode, COUNTRY), COUNTRY_CODELIST_CODE, rpoXml, xpath));

        String postalCode = (String) xpath.compile(context + DELIVERY_ADDRESS_POSTAL_CODE_XPATH).evaluate(rpoXml, XPathConstants.STRING);
        postalAddress.setZIP(formatPostalCodeToZip(postalCode));

        String postOfficeBox = (String) xpath.compile(context + DELIVERY_ADDRESS_POST_OFFICE_BOX_XPATH).evaluate(rpoXml, XPathConstants.STRING);
        if (!postOfficeBox.isEmpty()) {
            PoboxT pobox = new PoboxT();
            pobox.setPoBox(postOfficeBox);
            postalAddress.setPobox(pobox);
        }

        StreetT street = new StreetT();
        boolean notEmpty = false;
        String streetName = (String) xpath.compile(context + STREET_NAME_XPATH).evaluate(rpoXml, XPathConstants.STRING);
        if (!streetName.isEmpty()) {
            street.setStreetName(streetName);
            notEmpty = true;
        }
        String streetNumber = (String) xpath.compile(context + BUILDING_NUMBER_XPATH).evaluate(rpoXml, XPathConstants.STRING);
        if (!streetNumber.isEmpty()) {
            street.setStreetNumber(streetNumber);
            notEmpty = true;
        }
        String registrationNumber = (String) xpath.compile(context + PROPERTY_REGISTRATION_NUMBER_XPATH).evaluate(rpoXml, XPathConstants.STRING);
        if (!registrationNumber.isEmpty()) {
            street.setRegistrationNumber(registrationNumber);
            notEmpty = true;
        }
        String building = (String) xpath.compile(context + BUILDING_INDEX_XPATH).evaluate(rpoXml, XPathConstants.STRING);
        if (!building.isEmpty()) {
            street.setBuilding(building);
            notEmpty = true;
        }
        if (notEmpty) {
            postalAddress.setStreet(street);
        }

        address.setPostalAddress(postalAddress);
        address.setFormattedAddress(createFormattedAddress(postalAddress));
        return address;
    }

    private String formatPostalCodeToZip(String postalCode) {
        if (postalCode == null || postalCode.isBlank()) {
            return null;
        }
        postalCode = postalCode.replaceAll("\\s+", "");
        if (postalCode.length() < 3) {
            return postalCode;
        }
        postalCode = postalCode.substring(0, 3) + " " + postalCode.substring(3);
        return postalCode;
    }

    private long getSubjectPoIdSequence() {
        return initialize.subjectDataRepository.getSubjectPoIdSequence().longValue();
    }

    private Document readDocument(ZipFile zip, FileHeader fileHeader) throws IOException, SAXException, ParserConfigurationException {
        try (ZipInputStream is = zip.getInputStream(fileHeader)) {
            var document = XmlUtils.parse(is.readAllBytes());

            var rootNode = document.getElementsByTagName("ns2:CorporateBody").item(0);
            document.renameNode(rootNode, "http://www.dominanz.sk/UVZ/Reg/PO", "ns2:PO");

            var newDocument = XmlUtils.newDocument();
            var rootElement = (Element) newDocument.adoptNode(rootNode.cloneNode(true));
            rootElement.setAttribute("xmlns", "http://rpo.statistics.sk/RPO/Datatypes/rpo_core_schema-v2.4");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            newDocument.appendChild(rootElement);
            renameNamespaceRecursive(rootElement, "http://www.dominanz.sk/UVZ/Reg/PO");
            return newDocument;
        }
    }

    private String getSimplifiedValue(FileHeader fileHeader, Document document, String xpathString) {
        try {
            var xPathfactory = XPathFactory.newInstance();
            var xpath = xPathfactory.newXPath();
            return ((Node) xpath.compile(ignoreNameSpace(xpathString)).evaluate(document, XPathConstants.NODE)).getFirstChild().getNodeValue().toLowerCase(Locale.ROOT);
        } catch (XPathExpressionException e) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Xml " + fileHeader.getFileName().substring(fileHeader.getFileName().indexOf("/") + 1) + " neobsahuje typ zdroja registra.");
        }
    }

    private Node getSimplifiedValue(Document document) throws XPathExpressionException {
        var xPathfactory = XPathFactory.newInstance();
        var xpath = xPathfactory.newXPath();
        return ((Node) xpath.compile(ignoreNameSpace("po:PO/po:ID")).evaluate(document, XPathConstants.NODE));
    }

    public void renameNamespaceRecursive(Node node, String namespace) {
        var document = node.getOwnerDocument();
        if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().startsWith("ns2:")) {
            document.renameNode(node, namespace, node.getNodeName());
        }
        NodeList list = node.getChildNodes();
        for (var i = 0; i < list.getLength(); ++i) {
            renameNamespaceRecursive(list.item(i), namespace);
        }
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

    private long createValues(Document document) {
        try {
            String xml = XmlUtils.xmlToString(document).replace('\n', ' ');
            LocalDate now = LocalDate.now();

            AbstractRegPlugin plugin = getRegisterPlugin(RPO);
            long id = initialize.rpoDataRepository.getNextSequence();

            String effectiveFrom = null;
            Node idNode = getSimplifiedValue(document);
            if (idNode != null && idNode.hasAttributes()) {
                effectiveFrom = idNode.getAttributes().getNamedItem("EffectiveFrom") != null ? idNode.getAttributes().getNamedItem("EffectiveFrom").getNodeValue() : now.toString();
            }
            dataWriter.write(getData(xml, id, effectiveFrom, now) + "\n");
            dataHistoryWriter.write(getDataHistory(xml, id, effectiveFrom, now) + "\n");
            naturalIdWriter.write(getNaturalId(id) + "\n");
            indexWriter.write(createIndexes(document, id, plugin, effectiveFrom));

            return id;
        } catch (Exception ex) {
            ex.printStackTrace();
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

        //(context, current, effective_from, effective_to, key, sequence, value, value_simplified, entry_id)
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
        return "false;'" + effectiveFrom + "';" + null + ";'REG, DEV';'" + new Timestamp(System.currentTimeMillis()) + "';'" + initialize.userSystemLogin + "';'" + now + "';'" + xml.replace("'", "`") + "';" + id + ";";
    }

    private String getNaturalId(long id) {
        return id + ";" + id;
    }

    private void checkInitializedRecordsNum() {
        if (initializedRecordsNumPerFile == Integer.parseInt(initialize.csvRecordsNum)) {
            try {
                initializedRecordsNumPerFile = 0;
                closeCsvFiles();
                csvOrderNum++;
                prepareCsvFiles(RPO_1);
            } catch (IOException e) {
                throw new CommonException(HttpStatus.BAD_GATEWAY, "Initialization ended because of an error.", e);
            }
        }
    }
}
