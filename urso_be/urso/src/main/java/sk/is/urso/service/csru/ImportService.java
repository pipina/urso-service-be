package sk.is.urso.service.csru;

import org.alfa.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sk.is.urso.service.CiselnikService;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ImportService {
//    public static final String NEBOL_VYPLNENY_POVINNY_UDAJ_Z_XSD_SCHEMY = "Nebol vyplnený povinný údaj z XSD schémy.";
//    public static final String NEPLATNA_OPERACIA_IMPORTU = "Neplatná operácia importu.";
//    public static final String ZADANY_CISELNIK_NIE_JE_AKTUALNE_PLATNY = "Zadaný číselník nie je aktuálne platný.";
//    public static final String NEEXISTUJE_PLATNY_CISELNIK_PRE_HODNOTU_S_ITEM_CODE = "Neexistuje platný číselník pre hodnotu s itemCode: ";
//    public static final String DUPLIKOVANY_ITEM_CODE = "Duplikovaný itemCode: ";
//    public static final String HODNOTA_CISELNIKA_SA_UZ_NACHADZA_V_DATABAZE_ITEM_CODE = "Hodnota číselníka sa už nachádza v databáze, itemCode: ";
//    public static final String CHYBA_ITEM_CODE_V_OBJEKTE_RECORD = "Chýba itemCode v objekte Record.";
//    public static final String DATUM_KONCA_UCINNOSTI_JE_NASTAVENY_PRED_DATUM_ZACIATKU_UCINNOSTI = "Dátum konca účinnosti je nastavený pred dátum začiatku účinnosti.";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ITEM_NAME_LOC_TYPE = "Duplicitný kód jazyka pre jeden objekt ItemNameLocType.";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ITEM_SHORTENED_NAME_LOC_TYPE = "Duplicitný kód jazyka pre jeden objekt ItemShortenedNameLocType.";
//    public static final String DEFAULT = "default";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ITEM_ABBREVIATED_NAME_LOC_TYPE = "Duplicitný kód jazyka pre jeden objekt ItemAbbreviatedNameLocType.";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ADDITIONAL_CONTENT_LOC_TYPE = "Duplicitný kód jazyka pre jeden objekt AdditionalContentLocType.";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_NOTE_LOC_TYPE = "Duplicitný kód jazyka pre jeden objekt NoteLocType.";
//    public static final String OPERACIA_NIE_JE_POVOLENA = "Operácia nie je povolená.";
//    public static final String NIE_JE_POVOLENA_INICIALNY_IMPORT_UZ_PREBEHOL = " nie je povolená. Iniciálny import už prebehol!";
//    public static final String OPERACIA_PRE_CISELNIK = "Operácia pre číselník ";
//    public static final String CHYBAJUCI_UDAJ_CODELIST_CODE = "Chýbajúci údaj: CodelistCode. ";
//    public static final String CHYBAJUCI_UDAJ_CODELIST_NAME = "Chýbajúci údaj: CodelistName. ";
//    public static final String CHYBAJU_UDAJE_O_MANAZEROVI_CISELNIKA = "Chýbajú údaje o manažérovi číselníka.";
//    public static final String DATUM_KONCA_UCINNOSTI_JE_NASTAVENY_PRED_DATUMOM_ZACIATKU_UCINNOSTI = "Dátum konca účinnosti je nastavený pred dátumom začiatku účinnosti.";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_NAME = "Duplicitný kód jazyka pre jeden objekt CodelistCodeName.";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_MANAGER = "Duplicitný kód jazyka pre jeden objekt CodelistCodeManager.";
//    public static final String DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_NOTE = "Duplicitný kód jazyka pre jeden objekt CodelistCodeNote.";
//    public static final String CHYBA_ITEM_CODE = "Chýba itemCode.";
//    public static final String NEEXISTUJE_CISELNIK_KTORY_JE_ALEBO_BY_MAL_BYT_PLATNY = "Neexistuje číselník, ktorý je alebo by mal byť platný.";
//    public static final String DATUM_ZACIATKU_PLATNOSTI_JE_VYPLNENY = "Dátum začiatku platnosti je vyplnený.";
//    public static final String DATUM_ZACIATKU_UCINNOSTI_JE_PO_DATUME_KONCA_UCINNOSTI = "Dátum začiatku účinnosti je po dátume konca účinnosti.";
//    public static final String DATUM_PLATNOSTI_JE_PO_DATUME_ZACIATKU_UCINNOSTI = "Dátum platnosti je po dátume začiatku účinnosti";
//    public static final String CHYBA_MENO_CISELNIKA = "Chýba meno číselníka";
//    public static final String GET_LANG = "getLang";
//    public static final String CHYBA_ITEM_CODE_UDAJ = "Chýba ItemCode údaj.";
//    public static final String NOVY_DATUM_KONCA_UCINNOSTI_PRE_HODNOTU_S_ITEM_CODE = "Nový dátum konca účinnosti pre hodnotu s itemCode='";
//    public static final String JE_PO_DATUME_UCINNOSTI_CISELNIKA = "' je po dátume účinnosti číselníka.";
//    public static final String NOVY_DATUM_ZACIATKU_UCINNOSTI_PRE_HODNOTU_S_ITEM_CODE = "Nový dátum začiatku účinnosti pre hodnotu s itemCode='";
//    public static final String JE_PRED_DATUMOM_UCINNOSTI_CISELNIKA = "' je pred dátumom účinnosti číselníka.";
//    public static final String DATUM_KONCA_UCINNOSTI_PRE_HODNOTU_S_ITEM_CODE = "Dátum konca účinnosti pre hodnotu s itemCode='";
//    public static final String JE_PRED_DATUMOM_ZACIATKU_UCINNOSTI = "' je pred dátumom začiatku účinnosti.";
//    public static final String CHYBAJUCE_MENO_HODNOTY_CISELNIKA_PRE_ITEM_CODE = "Chýbajúce meno hodnoty číselníka pre itemCode: ";
//    public static final String NEPLATNY_LOKALIZACNY_UDAJ_DLHSI_AKO_2_ZNAKY_PRE_CODELIST_CODE_ITEM_CODE = "Neplatný lokalizačný údaj (dlhší ako 2 znaky), pre CodelistCode/ItemCode: ";
//    public static final String CHYBA_PRI_SPRACOVANI_LOKALIZACNYCH_UDAJOV = "Chyba pri spracovaní lokalizačných údajov.";
//    @Autowired
//    private CiselnikService ciselnikService;
//
//    @Autowired
//    private EnumerationRepositoryService enumerationRepositoryService;
//
//    @Autowired
//    private EnumerationInitialLoadDeniedService enumerationInitialLoadDeniedService;
//
//    @Autowired
//    private EnumerationSnapshotService enumerationSnapshotService;
//
//    @Autowired
//    private EnumerationMultivaluesService enumerationMultivaluesService;
//
//    @Autowired
//    private EnumerationMultivaluesSnapshotService enumerationMultivaluesSnapshotService;
//
//    @Autowired
//    private EnumerationListService enumerationListService;
//
//    @Autowired
//    private EnumerationEventService enumerationEventService;
//
//    @Autowired
//    private EnumerationValueEventService enumerationValueEventService;
//
//    @Autowired
//    private EnumerationValueService enumerationValuesService;
//
//    @Autowired
//    private EnumerationValueSnapshotService enumerationValueSnapshotService;
//
//    @Autowired
//    private EnumerationValueListService enumerationValueListService;
//
//    @Autowired
//    private EnumerationValueMultivaluesService enumerationValuesMultivaluesService;
//
//    @Autowired
//    private EnumerationValueMultivaluesSnapshotService enumerationValueMultivaluesSnapshotService;
//
//    @Autowired
//    private AuthorizationService authorizationService;
//
//    @Autowired
//    private NotificationService notificationService;
//
//    @Autowired
//    UserInfoService userInfoService;
//
//    @Autowired
//    AutoExportConfig autoExportConfig;
//
//    @Value("${import-initial-load-allowed}")
//    private boolean initialLoadAllowed;
//
//    @Value("${multiple-initial-import-allowed}")
//    private boolean multipleInitialImportAllowed;
//    private static final String ENUMERATION_TOPIC = "NOTIFICATION_ENUMERATION";
//    private static final String ENUMERATION_VALUE_TOPIC = "NOTIFICATION_ENUMERATION_VALUE";
//    private static final String NOTIFICATION_TOPIC = "CURRENT_ENUMERATION_EXPORT";
//
//    private static final String ENUMERATION = "enumeration";
//    private static final String ENUMERATION_VALUE = "enumerationValue";
//
//    /**
//     * Spustenie príslešnej operácie importu
//     *
//     * @param operationEnum typ importovacej operácie, ktorá sa ma vykonať
//     * @param event, ku ktorému sa operácia má viazať
//     * @param importedData  dáta, ktoré sa budú importovať
//     * @throws Exception Vyvolaná v prípade neplatnej operácie alebo zlyhania
//     *                   importovania
//     */
//    public void processOperation(OperationEnum operationEnum, Event event, ImportExportType importedData, UserInfo userInfo) throws Exception {
//        removeSpacesFromRecords(importedData.getRecords());
//
//        checkRecordsItemCodeAndParentCode(importedData.getRecords());
//
//        List<Enumeration> enumerations;
//        String codelistCode = importedData.getType().getImport().getCodelistCode();
//        authorizationService.checkIsAuthorized(codelistCode, AuthorizationDomainEnum.ENUMERATION, AuthorizationOperationEnum.ENUMERATION_IMPORT, ciselnikService.getEnumerationCodelistName(codelistCode, "sk"), userInfo);
//
//        enumerations = enumerationRepositoryService.findAllByCodelistCode(codelistCode);
//        if (enumerations.isEmpty())
//            throw new CommonException(HttpStatus.BAD_REQUEST, "Neexistuje číselník s codelistCode = '" +codelistCode+ "'.", null);
//
//        AtomicInteger enumerationValueSnapshotIndex= new AtomicInteger(0);
//        AtomicInteger enumerationValueMultivaluesSnapshotIndex = new AtomicInteger(0);
//
//        if (operationEnum.equals(OperationEnum.ADDNEW_DATA)) {
//            event.setDescription("import_type=ADDNEW_DATA");
//            addNewData(importedData, enumerations, event, enumerationValueSnapshotIndex, enumerationValueMultivaluesSnapshotIndex, userInfo);
//        } else if (operationEnum.equals(OperationEnum.INITIAL_LOAD)) {
//            event.setDescription("import_type=INITIAL_LOAD");
//            initialLoad(importedData, enumerations, event, enumerationValueSnapshotIndex, enumerationValueMultivaluesSnapshotIndex, userInfo);
//        } else if (operationEnum.equals(OperationEnum.RELOAD_DATA)) {
//            event.setDescription("import_type=RELOAD_DATA");
//            reloadData(importedData, enumerations, event, enumerationValueSnapshotIndex, enumerationValueMultivaluesSnapshotIndex, userInfo);
//        } else {
//            event.setEventSuccesfull(false);
//            event.setDescription("Neplatná operácia");
//            throw new CommonException(HttpStatus.BAD_REQUEST, "Neplatná operácia, operácia " + operationEnum + " neexistuje.", null);
//        }
//    }
//
//    public void removeSpacesFromRecords(RecordsType records) {
//        for (CodeListDataRecType codeListDataRecType : records.getRecord()) {
//            codeListDataRecType.getItemName().stream().forEach(e -> e.setDefaultItemName(e.getDefaultItemName() != null ? e.getDefaultItemName().strip() : "DefaultItemName"));
//            codeListDataRecType.getItemName().stream().forEach(e -> e.getLocalizedItemName().stream().forEach(l -> l.setItemName(l.getItemName() != null ? l.getItemName().strip() : "LocalizedItemName")));
//
//            codeListDataRecType.getItemShortenedName().stream().forEach(e -> e.setDefaultItemShortenedName(e.getDefaultItemShortenedName() != null ? e.getDefaultItemShortenedName().strip() : "DefaultItemShortenedName"));
//            codeListDataRecType.getItemShortenedName().stream().forEach(e -> e.getLocalizedItemShortenedName().stream().forEach(l -> l.setItemShortenedName(l.getItemShortenedName() != null ? l.getItemShortenedName().strip() : "LocalizedItemShortenedName")));
//
//            codeListDataRecType.getItemAbbreviatedName().stream().forEach(e -> e.setDefaultItemAbbreviatedName(e.getDefaultItemAbbreviatedName() != null ? e.getDefaultItemAbbreviatedName().strip() : "DefaultItemAbbreviatedName"));
//            codeListDataRecType.getItemAbbreviatedName().stream().forEach(e -> e.getLocalizedItemAbbreviatedName().stream().forEach(l -> l.setItemAbbreviatedName(l.getItemAbbreviatedName() != null ? l.getItemAbbreviatedName().strip() : "LocalizedItemAbbreviatedName")));
//        }
//    }
//
//    public void checkRecordsItemCodeAndParentCode(RecordsType records) {
//        records.getRecord().stream().forEach(c -> {
//            if (c.getItemName() != null && c.getParentItemCode() != null && c.getItemCode().equals(c.getParentItemCode()))
//                throw new CommonException(HttpStatus.BAD_REQUEST, "ItemCode a parentItemCode sa nemôžu rovnať: '" +  c.getItemCode() + "'.", null);
//        });
//    }
//
//    /**
//     * Vygenerovanie objektu reprezentujúceho JSON odpovede, ktorá predstavuje
//     * zákaldné údaje číselníka, ku ktorému sa importované dáta viažu
//     *
//     * @param importType objekt reprezentujúci importované dáta
//     * @param importId   ID súboru, v ktorom sa dáta nachádzajú
//     * @return objekt reprezentujúci JSON odpoveď, ktorá obsahuje údaje číslníka, ku
//     *         ktorému sa dáta viažu
//     */
//    public ImportHeader generateImportHeader(ImportDescriptionType importType, String importId) {
//        ImportHeader importHeader = new ImportHeader();
//
//        try {
//            importHeader.setCodelistDataDate(DateUtils.toLocalDate(importType.getCodelistDataDate()));
//            importHeader.setImportCreationTime(DateUtils.toLocalDateTime(importType.getImportCreationTime()));
//            importHeader.setDataAutor(importType.getDataAuthor());
//        } catch (@SuppressWarnings("unused") NullPointerException ex) {
//            throw new CommonException(HttpStatus.BAD_REQUEST, NEBOL_VYPLNENY_POVINNY_UDAJ_Z_XSD_SCHEMY, null);
//        }
//
//        try {
//            importHeader.setOperation(ImportHeader.OperationEnum.fromValue(importType.getOperation().name()));
//        } catch (@SuppressWarnings("unused") Exception ex) {
//            throw new CommonException(HttpStatus.BAD_REQUEST, NEPLATNA_OPERACIA_IMPORTU, null);
//        }
//
//        importHeader.setImportId(importId);
//        importHeader.setNotes(importType.getNotes());
//        importHeader.setEnumeration(new EnumerationDetail());
//        importHeader.getEnumeration().setCodelistCode(importType.getCodelistCode());
//        importHeader.getEnumeration().setValid(true);
//
//        if (importType.getCodeList() != null) {
//            importHeader.getEnumeration().setReferenceIdentifier(importType.getCodeList().getReferenceIdentifier());
//
//            if (importType.getCodeList().getEffectiveFrom() != null)
//                importHeader.getEnumeration().setEffectiveFrom(DateUtils.toLocalDate(importType.getCodeList().getEffectiveFrom()));
//            if (importType.getCodeList().getEffectiveTo() != null)
//                importHeader.getEnumeration().setEffectiveTo(DateUtils.toLocalDate(importType.getCodeList().getEffectiveTo()));
//            if (importType.getCodeList().getValidFrom() != null)
//                importHeader.getEnumeration().setValidFrom(DateUtils.toLocalDate(importType.getCodeList().getValidFrom()));
//
//            HashMap<String, EnumerationDetailLocalizedData> multivaluesDataMap = new HashMap<>();
//            if (importType.getCodeList().getCodelistManager() != null) {
//                for (LocalizedCodelistManagerType localizedCodelistManagerType : importType.getCodeList().getCodelistManager()) {
//                    String key = SK_LOCALE;
//                    if (!multivaluesDataMap.containsKey(key)) {
//                        multivaluesDataMap.put(key, new EnumerationDetailLocalizedData());
//                        multivaluesDataMap.get(key).setLocale(key);
//                    }
//                    IndexedString255 indexString = new IndexedString255();
//                    multivaluesDataMap.get(key).addCodelistManagerItem(indexString);
//                    indexString.setIndex(multivaluesDataMap.get(key).getCodelistManager().size());
//                    indexString.setValue(localizedCodelistManagerType.getDefaultCodelistManager());
//                    for (CodelistManagerLocType codelistLocManagerType : localizedCodelistManagerType.getLocalizedCodelistManager()) {
//                        key = checkLocalizationString(codelistLocManagerType.getLang().getLanguageId(), importType.getCodelistCode());
//                        if (!multivaluesDataMap.containsKey(key)) {
//                            multivaluesDataMap.put(key, new EnumerationDetailLocalizedData());
//                            multivaluesDataMap.get(key).setLocale(key);
//                        }
//                        indexString = new IndexedString255();
//                        multivaluesDataMap.get(key).addCodelistManagerItem(indexString);
//                        indexString.setIndex(multivaluesDataMap.get(key).getCodelistManager().size());
//                        indexString.setValue(codelistLocManagerType.getCodelistManager());
//                    }
//                }
//            }
//
//            if (importType.getCodeList().getNote() != null) {
//                for (LocalizedNoteType localizedNoteType : importType.getCodeList().getNote()) {
//                    String key = SK_LOCALE;
//                    if (!multivaluesDataMap.containsKey(key)) {
//                        multivaluesDataMap.put(key, new EnumerationDetailLocalizedData());
//                        multivaluesDataMap.get(key).setLocale(key);
//                    }
//                    IndexedString4095 indexString = new IndexedString4095();
//                    multivaluesDataMap.get(key).addNoteItem(indexString);
//                    indexString.setIndex(multivaluesDataMap.get(key).getNote().size());
//                    indexString.setValue(localizedNoteType.getDefaultNote());
//                    for (NoteLocType noteLocType : localizedNoteType.getLocalizedNote()) {
//                        key = checkLocalizationString(noteLocType.getLang().getLanguageId(), importType.getCodelistCode());
//                        if (!multivaluesDataMap.containsKey(key)) {
//                            multivaluesDataMap.put(key, new EnumerationDetailLocalizedData());
//                            multivaluesDataMap.get(key).setLocale(key);
//                        }
//                        indexString = new IndexedString4095();
//                        multivaluesDataMap.get(key).addNoteItem(indexString);
//                        indexString.setIndex(multivaluesDataMap.get(key).getNote().size());
//                        indexString.setValue(noteLocType.getNote());
//                    }
//                }
//            }
//
//            if (importType.getCodeList().getCodelistName() != null) {
//                String key = SK_LOCALE;
//                LocalizedCodelistNameType codelistNameType = importType.getCodeList().getCodelistName();
//                if (!multivaluesDataMap.containsKey(key)) {
//                    multivaluesDataMap.put(key, new EnumerationDetailLocalizedData());
//                    multivaluesDataMap.get(key).setLocale(key);
//                }
//                multivaluesDataMap.get(key).setCodelistName(codelistNameType.getDefaultCodelistName());
//                for (CodelistNameLocType codelistNameLocType : codelistNameType.getLocalizedCodelistName()) {
//                    key = checkLocalizationString(codelistNameLocType.getLang().getLanguageId(), importType.getCodelistCode());
//                    if (!multivaluesDataMap.containsKey(key)) {
//                        multivaluesDataMap.put(key, new EnumerationDetailLocalizedData());
//                        multivaluesDataMap.get(key).setLocale(key);
//                    }
//                    multivaluesDataMap.get(key).setCodelistName(codelistNameLocType.getCodelistName());
//                }
//            }
//
//            for (String sourceCodelist : importType.getCodeList().getSourceCodelist()) {
//                SourceCodelist so = new SourceCodelist();
//                so.setValue(sourceCodelist);
//                importHeader.getEnumeration().addSourceCodelistItem(so);
//            }
//
//            for (EnumerationDetailLocalizedData enumerationDetailLocalizedData : multivaluesDataMap.values()) {
//                importHeader.getEnumeration().addEnumerationLocalizedItem(enumerationDetailLocalizedData);
//            }
//        }
//
//        return importHeader;
//    }
//
//    /**
//     * Pridanie nových hodnôt číslníka {@link EnumerationValue}
//     *
//     * @param importedData objekto reprezentujúci importované dáta
//     * @param enumerations, číslníky, ku ktorému sa dáta viažu
//     * @param event, ku ktorému sa operácia importu viaže
//     * @throws Exception Vyvolána v prípade chybných alebo nevalidných dát
//     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    private void addNewData(ImportExportType importedData, List<Enumeration> enumerations, Event event, AtomicInteger enumerationValueSnapshotIndex, AtomicInteger enumerationValueMultivaluesSnapshotIndex, UserInfo userInfo) throws Exception {
//        RecordsType records = importedData.getRecords();
//        HashSet<String> itemCodes = new HashSet<>();
//        enumerations = enumerations.stream().filter(element ->element.getEffectiveTo() == null || element.getEffectiveTo().after(new Date())).collect(Collectors.toList());
//
//        List<NotificationT> notifications = new ArrayList<>();
//
//        if (enumerations.isEmpty())
//            throw new CommonException(HttpStatus.BAD_REQUEST, ZADANY_CISELNIK_NIE_JE_AKTUALNE_PLATNY, null);
//        enumerationEventService.createEnumerationEvent(enumerations.get(0), event);
//
//        for (CodeListDataRecType codeListRecord : records.getRecord()) {
//            String codelistCode = importedData.getType().getImport().getCodelistCode();
//            Date effectiveFrom = codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime();
//
//            List<Enumeration> effectiveEnumerationsForRecord ;
//            if (codeListRecord.getEffectiveTo() == null) {
//                effectiveEnumerationsForRecord = enumerations.stream()
//                        .filter(element -> (element.getEffectiveTo() == null || !element.getEffectiveTo().before(effectiveFrom)))
//                        .collect(Collectors.toList());
//            } else {
//                effectiveEnumerationsForRecord = enumerations.stream()
//                        .filter(element -> !element.getEffectiveFrom().after(codeListRecord.getEffectiveTo().toGregorianCalendar().getTime()) && (element.getEffectiveTo() == null || !element.getEffectiveTo().before(effectiveFrom)))
//                        .collect(Collectors.toList());
//            }
//
//            if (effectiveEnumerationsForRecord.isEmpty())
//                throw new CommonException(HttpStatus.BAD_REQUEST, NEEXISTUJE_PLATNY_CISELNIK_PRE_HODNOTU_S_ITEM_CODE + codeListRecord.getItemCode()
//                        + "medzi dátumami hodnoty " + effectiveFrom + " a " + effectiveFrom, null);
//
//            enumerationEventService.createEnumerationEvent(effectiveEnumerationsForRecord.get(0), event);
//            if (itemCodes.contains(codeListRecord.getItemCode()))
//                throw new CommonException(HttpStatus.BAD_REQUEST, DUPLIKOVANY_ITEM_CODE + codeListRecord.getItemCode(), null);
//            itemCodes.add(codeListRecord.getItemCode());
//
//            if (enumerationValuesService.existByItemCodeAndCodelistCode(codeListRecord.getItemCode(), codelistCode))
//                throw new CommonException(HttpStatus.BAD_REQUEST, HODNOTA_CISELNIKA_SA_UZ_NACHADZA_V_DATABAZE_ITEM_CODE + codeListRecord.getItemCode(), null);
//
//            EnumerationValue newEnumerationValue = createEnumerationValueFromRecord(OperationEnum.ADDNEW_DATA, codeListRecord, codelistCode, event, enumerationValueSnapshotIndex, enumerationValueMultivaluesSnapshotIndex);
//            notifications.add(notificationService.createNotification(notificationService.createEnumerationValueEventData(codelistCode, newEnumerationValue.getItemCode(), newEnumerationValue.getId(), null, notificationService.createUrl(ENUMERATION_VALUE, codelistCode, newEnumerationValue.getItemCode(), newEnumerationValue.getId().toString())), event.getId(), CategoryT.CREATE, DomainT.ENUMERATION_VALUE, userInfo.getLogin(), null));
//        }
//        notificationService.sendNotifications(ENUMERATION_VALUE_TOPIC, notifications);
//    }
//
//    /**
//     * Vytvorenie EnumerationValue z Recordu prijatého z XML
//     *
//     * @param codeListRecord , spracovávaný údaj do triedy EnumerationValue
//     * @param codelistCode , kód číselníka, ku ktorému hodnota patrí
//     * @param event , ku ktorému sa operácia importu viaže
//     * @param enumerationValueSnapshotIndex snapshot index udalosti
//     * @param enumerationValueMultivaluesSnapshotIndex multivalue snapshot index udalosti
//     */
//    EnumerationValue createEnumerationValueFromRecord(OperationEnum operationType, CodeListDataRecType codeListRecord, String codelistCode, Event event, AtomicInteger enumerationValueSnapshotIndex, AtomicInteger enumerationValueMultivaluesSnapshotIndex) {
//        EnumerationValue enumerationValue = new EnumerationValue();
//        enumerationValue.setCodelistCode(codelistCode);
//
//        if(codeListRecord.getItemCode() == null)
//            throw new CommonException(HttpStatus.BAD_REQUEST, CHYBA_ITEM_CODE_V_OBJEKTE_RECORD, null) ;
//
//        enumerationValue.setItemCode(codeListRecord.getItemCode());
//        enumerationValue.setParentItemCode(codeListRecord.getParentItemCode());
//        enumerationValue.setHierarchicalItemCode(codeListRecord.getHierarchicalItemCode());
//        enumerationValue.setOriginalItemCode(codeListRecord.getOriginalItemCode());
//        enumerationValue.setUnitOfMeasure(codeListRecord.getUnitOfMeasure());
//        enumerationValue.setItemLogicalOrder(codeListRecord.getItemLogicalOrder());
//        enumerationValue.setReferenceIdentifier(codeListRecord.getReferenceIdentifier());
//        enumerationValue.setCreateDate(new Date());
//        if (codeListRecord.isLegislativeValidity() == null)
//            enumerationValue.setIsValid(true);
//        else
//            enumerationValue.setIsValid(codeListRecord.isLegislativeValidity());
//
//        if (codeListRecord.getValidFrom() == null) {
//            enumerationValue.setValidFrom(new Date());
//        } else {
//            enumerationValue.setValidFrom(codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime());
//        }
//
//        if (operationType.equals(OperationEnum.INITIAL_LOAD)) {
//            if (codeListRecord.getEffectiveFrom() != null) {
//                enumerationValue.setEffectiveFrom(codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime());
//            }	else {
//                enumerationValue.setEffectiveFrom(DateUtils.toDate(DateUtils.toLocalDate(new Date()).plusDays(1)));
//            }
//        } else {
//            if (codeListRecord.getEffectiveFrom() != null) {
//                enumerationValue.setEffectiveFrom(codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime());
//                if (!enumerationValue.getEffectiveFrom().after(new Date()))
//                    enumerationValue.setEffectiveFrom(DateUtils.toDate(DateUtils.toLocalDate(new Date()).plusDays(1)));
//            }	else {
//                enumerationValue.setEffectiveFrom(DateUtils.toDate(DateUtils.toLocalDate(new Date()).plusDays(1)));
//            }
//        }
//
//        if (codeListRecord.getEffectiveTo() != null) {
//            enumerationValue.setEffectiveTo(codeListRecord.getEffectiveTo().toGregorianCalendar().getTime());
//            if (enumerationValue.getEffectiveTo().before(enumerationValue.getEffectiveFrom()))
//                throw new CommonException(HttpStatus.BAD_REQUEST, DATUM_KONCA_UCINNOSTI_JE_NASTAVENY_PRED_DATUM_ZACIATKU_UCINNOSTI, null);
//        }
//
//        EnumerationValuesList enumerationValueList = new EnumerationValuesList();
//        enumerationValueList.setEnumerationValueListId(new EnumerationValueListId(codeListRecord.getItemCode(), enumerationValue.getCodelistCode()));
//        enumerationValueListService.save(enumerationValueList);
//        EnumerationValue enumerationValueDB = enumerationValuesService.save(enumerationValue);
//        enumerationValueEventService.createNewEnumerationValueEvent(enumerationValueDB, event);
//        enumerationValueSnapshotService.createNewEnumerationValueSnapshot(enumerationValueDB, event, enumerationValueSnapshotIndex.get());
//        enumerationValueSnapshotIndex.addAndGet(1);
//
//        HashMap<String, List<String>> itemNames = new HashMap<>();
//        HashMap<String, List<String>> itemShortenedNames = new HashMap<>();
//        HashMap<String, List<String>> itemAbbreviatedNames = new HashMap<>();
//        HashMap<String, List<String>> additionalContents = new HashMap<>();
//        HashMap<String, List<String>> notes = new HashMap<>();
//
//        fillHashMaps(codeListRecord,enumerationValue,itemNames,itemShortenedNames,itemAbbreviatedNames,additionalContents,notes);
//
//        fillEnumerationValuesMultivalues(event,enumerationValueMultivaluesSnapshotIndex,enumerationValue,itemNames,itemShortenedNames,itemAbbreviatedNames,additionalContents,notes);
//
//        return enumerationValueDB;
//    }
//
//    private void fillHashMaps(CodeListDataRecType codeListRecord, EnumerationValue enumerationValue, HashMap<String, List<String>> itemNames, HashMap<String, List<String>> itemShortenedNames, HashMap<String, List<String>> itemAbbreviatedNames, HashMap<String, List<String>> additionalContents, HashMap<String, List<String>> notes) {
//
//        for (LocalizedItemNameType localizedItemNameType : codeListRecord.getItemName()) {
//            HashSet<String> localIdNameType = new HashSet<>();
//            if (localizedItemNameType.getDefaultItemName() != null) {
//                itemNames.computeIfAbsent(SK_LOCALE, k -> new ArrayList<>());
////				if (!itemNames.containsKey(SK_LOCALE)) {
////					itemNames.put(SK_LOCALE, new ArrayList<>());
////				}
//                itemNames.get(SK_LOCALE).add(localizedItemNameType.getDefaultItemName());
//                localIdNameType.add(DEFAULT);
//            }
//            for (ItemNameLocType itemNameLocType : localizedItemNameType.getLocalizedItemName()) {
//                String localid = checkLocalizationString(itemNameLocType.getLang().getLanguageId(), enumerationValue.getItemCode());
//                if (localIdNameType.contains(localid))
//                    throw new CommonException(HttpStatus.BAD_REQUEST, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ITEM_NAME_LOC_TYPE, null);
//                if (localid.equals(SK_LOCALE)) {
//                    localIdNameType.add(SK_LOCALE);
//                    itemNames.get(SK_LOCALE).set(itemNames.get(SK_LOCALE).size()-1, itemNameLocType.getItemName());
//                    continue;
//                }
//                if (!itemNames.containsKey(localid)) {
//                    itemNames.put(localid, new ArrayList<>());
//                    localIdNameType.add(localid);
//                }
//                itemNames.get(localid).add(itemNameLocType.getItemName());
//            }
//        }
//
//        for (LocalizedItemShortenedNameType localizedItemShortenedNameType : codeListRecord.getItemShortenedName()) {
//            HashSet<String> localIdShortenedName = new HashSet<>();
//            if (localizedItemShortenedNameType.getDefaultItemShortenedName() != null) {
//                itemShortenedNames.computeIfAbsent(SK_LOCALE, k -> new ArrayList<>());
//                itemShortenedNames.get(SK_LOCALE).add(localizedItemShortenedNameType.getDefaultItemShortenedName());
//                localIdShortenedName.add(DEFAULT);
//            }
//            for (ItemShortenedNameLocType itemShortenedNameLocType : localizedItemShortenedNameType.getLocalizedItemShortenedName()) {
//                String localid = checkLocalizationString(itemShortenedNameLocType.getLang().getLanguageId(), enumerationValue.getItemCode());
//                if (localIdShortenedName.contains(localid))
//                    throw new CommonException(HttpStatus.BAD_REQUEST, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ITEM_SHORTENED_NAME_LOC_TYPE, null);
//                if (localid.equals(SK_LOCALE)) {
//                    localIdShortenedName.add(SK_LOCALE);
//                    itemShortenedNames.get(SK_LOCALE).set(itemShortenedNames.get(SK_LOCALE).size()-1, itemShortenedNameLocType.getItemShortenedName());
//                    continue;
//                }
//                if (!itemShortenedNames.containsKey(localid)) {
//                    itemShortenedNames.put(localid, new ArrayList<>());
//                    localIdShortenedName.add(localid);
//                }
//                itemShortenedNames.get(localid).add(itemShortenedNameLocType.getItemShortenedName());
//            }
//        }
//        for (LocalizedItemAbbreviatedNameType localizedItemAbbreviatedNameType : codeListRecord.getItemAbbreviatedName()) {
//            HashSet<String> localIdAbbreviatedName = new HashSet<>();
//            if (localizedItemAbbreviatedNameType.getDefaultItemAbbreviatedName() != null && localizedItemAbbreviatedNameType.getDefaultItemAbbreviatedName().length() <= 20) {
//                itemAbbreviatedNames.computeIfAbsent(SK_LOCALE, k -> new ArrayList<>());
//                itemAbbreviatedNames.get(SK_LOCALE).add(localizedItemAbbreviatedNameType.getDefaultItemAbbreviatedName());
//                localIdAbbreviatedName.add(DEFAULT);
//            }
//            for (ItemAbbreviatedNameLocType itemAbbreviatedNameLocType : localizedItemAbbreviatedNameType.getLocalizedItemAbbreviatedName()) {
//                String localid = checkLocalizationString(itemAbbreviatedNameLocType.getLang().getLanguageId(), enumerationValue.getItemCode());
//                if (localIdAbbreviatedName.contains(localid))
//                    throw new CommonException(HttpStatus.BAD_REQUEST, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ITEM_ABBREVIATED_NAME_LOC_TYPE, null);
//                if (localid.equals(SK_LOCALE)) {
//                    localIdAbbreviatedName.add(SK_LOCALE);
//                    itemAbbreviatedNames.get(SK_LOCALE).set(itemAbbreviatedNames.get(SK_LOCALE).size()-1, itemAbbreviatedNameLocType.getItemAbbreviatedName());
//                    continue;
//                }
//                if (!itemAbbreviatedNames.containsKey(localid)) {
//                    itemAbbreviatedNames.put(localid, new ArrayList<>());
//                    localIdAbbreviatedName.add(localid);
//                }
//                itemAbbreviatedNames.get(localid).add(itemAbbreviatedNameLocType.getItemAbbreviatedName());
//            }
//        }
//
//        for (LocalizedAdditionalContentType localizedAdditionalContentType : codeListRecord.getAdditionalContent()) {
//            HashSet<String> localIdAdditionalContent = new HashSet<>();
//            if (localizedAdditionalContentType.getDefaultAdditionalContent() != null) {
//                additionalContents.computeIfAbsent(SK_LOCALE, k -> new ArrayList<>());
//                additionalContents.get(SK_LOCALE).add(localizedAdditionalContentType.getDefaultAdditionalContent());
//                localIdAdditionalContent.add(DEFAULT);
//            }
//            for (AdditionalContentLocType additionalContentLocType : localizedAdditionalContentType.getLocalizedAdditionalContent()) {
//                String localid = checkLocalizationString(additionalContentLocType.getLang().getLanguageId(), enumerationValue.getItemCode());
//                if (localIdAdditionalContent.contains(localid))
//                    throw new CommonException(HttpStatus.BAD_REQUEST, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_ADDITIONAL_CONTENT_LOC_TYPE, null);
//                if (localid.equals(SK_LOCALE)) {
//                    localIdAdditionalContent.add(SK_LOCALE);
//                    additionalContents.get(SK_LOCALE).set(additionalContents.get(SK_LOCALE).size()-1, additionalContentLocType.getAdditionalContent());
//                    continue;
//                }
//                if (!additionalContents.containsKey(localid)) {
//                    additionalContents.put(localid, new ArrayList<>());
//                    localIdAdditionalContent.add(localid);
//                }
//                additionalContents.get(localid).add(additionalContentLocType.getAdditionalContent());
//            }
//        }
//
//        HashSet<String> localIdNote = new HashSet<>();
//        if (codeListRecord.getNote() != null) {
//            if (codeListRecord.getNote().getDefaultNote() != null) {
//                notes.put(SK_LOCALE, new ArrayList<>());
//                notes.get(SK_LOCALE).add(codeListRecord.getNote().getDefaultNote());
//                localIdNote.add(DEFAULT);
//            }
//
//            for (NoteLocType localizedNoteLocType : codeListRecord.getNote().getLocalizedNote()) {
//                String localid = checkLocalizationString(localizedNoteLocType.getLang().getLanguageId(), enumerationValue.getItemCode());
//                if (localIdNote.contains(localid))
//                    throw new CommonException(HttpStatus.BAD_REQUEST, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_NOTE_LOC_TYPE, null);
//                if (localid.equals(SK_LOCALE)) {
//                    localIdNote.add(SK_LOCALE);
//                    notes.get(SK_LOCALE).set(notes.get(SK_LOCALE).size()-1, localizedNoteLocType.getNote());
//                    continue;
//                }
//                if (!notes.containsKey(localid)) {
//                    notes.put(localid, new ArrayList<>());
//                    localIdNote.add(localid);
//                }
//                notes.get(localid).add(localizedNoteLocType.getNote());
//            }
//        }
//    }
//
//    /**
//     * Nahradenie záznamov o číselníku novými dátami prijatými v súbore
//     *
//     * @param importedData objekto reprezentujúci dáta, ktoré sa budú importovať
//     * @param enumerations  číselníky, ku ktorému sa dáta viažu
//     * @param event        ,ku ktorému sa operácia viaže
//     * @throws Exception vznikne, ak sú dáta nevalidné alebo chybné
//     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    private void initialLoad(ImportExportType importedData, List<Enumeration> enumerations, Event event, AtomicInteger enumerationValueSnapshotIndex, AtomicInteger enumerationValueMultivaluesSnapshotIndex, UserInfo userInfo) throws Exception {
//        if (!initialLoadAllowed) {
//            throw new CommonException(HttpStatus.FORBIDDEN, OPERACIA_NIE_JE_POVOLENA, null);
//        }
//
//        if (!multipleInitialImportAllowed && !enumerationInitialLoadDeniedService.checkAllowance(importedData.getType().getImport().getCodelistCode())) {
//            throw new CommonException(HttpStatus.FORBIDDEN, OPERACIA_PRE_CISELNIK + importedData.getType().getImport().getCodelistCode() + NIE_JE_POVOLENA_INICIALNY_IMPORT_UZ_PREBEHOL, null);
//        }
//
//        List<NotificationT> notifications = new ArrayList<>();
//		/*List<EnumerationValue> enumerationValues = enumerationValuesService.findAllByCodelistCode(enumerations.get(0).getCodelistCode(), false);
//		for (EnumerationValue enumerationValue : enumerationValues) {
//			enumerationValuesService.deleteEnumerationValueData(enumerationValue);
//		}*/
//
//        enumerationValueEventService.deleteEnumerationValueData(enumerations.get(0).getCodelistCode());
//        enumerationValueSnapshotService.deleteEnumerationValueData(enumerations.get(0).getCodelistCode());
//        enumerationValuesMultivaluesService.deleteEnumerationValueData(enumerations.get(0).getCodelistCode());
//        enumerationValueMultivaluesSnapshotService.deleteEnumerationValueData(enumerations.get(0).getCodelistCode());
//        enumerationValuesService.deleteEnumerationValueData(enumerations.get(0).getCodelistCode());
//        enumerationValueListService.deleteEnumerationValueListData(enumerations.get(0).getCodelistCode());
//
//        for (Enumeration enumeration : enumerations)
//        {
//            enumerationEventService.deleteEnumerationData(enumeration);
//            enumerationMultivaluesService.deleteEnumerationData(enumeration);
//            enumerationMultivaluesSnapshotService.deleteEnumerationData(enumeration);
//            enumerationSnapshotService.deleteEnumerationData(enumeration);
//            enumerationRepositoryService.deleteEnumerationData(enumeration);
//        }
//
//        Enumeration initialEnumeration = new Enumeration();
//        CodeListType codeListType = importedData.getType().getImport().getCodeList();
//
//        if (codeListType.getCodelistCode() == null || codeListType.getCodelistCode().equals(""))
//            throw new CommonException(HttpStatus.BAD_REQUEST, CHYBAJUCI_UDAJ_CODELIST_CODE, null);
//        if (codeListType.getCodelistName() == null || codeListType.getCodelistName().getDefaultCodelistName() == null || codeListType.getCodelistName().getDefaultCodelistName().equals(""))
//            throw new CommonException(HttpStatus.BAD_REQUEST, CHYBAJUCI_UDAJ_CODELIST_NAME, null);
//        if (codeListType.getCodelistManager().isEmpty())
//            throw new CommonException(HttpStatus.BAD_REQUEST, CHYBAJU_UDAJE_O_MANAZEROVI_CISELNIKA, null);
//
//        initialEnumeration.setCodelistCode(codeListType.getCodelistCode());
//        initialEnumeration.setReferenceIdentifier(codeListType.getReferenceIdentifier());
//        initialEnumeration.setIsValid(true);
//        initialEnumeration.setCreateDate(new Date());
//
//        if (codeListType.getValidFrom() != null)
//            initialEnumeration.setValidFrom(DateUtils.toDate(codeListType.getValidFrom()));
//            //throw new CommonException(HttpStatus.BAD_REQUEST, "Bol zadaný dátum platnosti.", null);
//        else
//            initialEnumeration.setValidFrom(new Date());
//
//        if (codeListType.getEffectiveFrom() != null) {
//            initialEnumeration.setEffectiveFrom(codeListType.getEffectiveFrom().toGregorianCalendar().getTime());
//        } else {
//            initialEnumeration.setEffectiveFrom(DateUtils.toDate(LocalDate.now().plusDays(1)));
//        }
//
//        if (codeListType.getEffectiveTo() != null)
//            initialEnumeration.setEffectiveTo(codeListType.getEffectiveTo().toGregorianCalendar().getTime());
//
//        if (initialEnumeration.getEffectiveTo() != null && initialEnumeration.getEffectiveTo().before(initialEnumeration.getEffectiveFrom())) {
//            throw new CommonException(HttpStatus.BAD_REQUEST, DATUM_KONCA_UCINNOSTI_JE_NASTAVENY_PRED_DATUMOM_ZACIATKU_UCINNOSTI, null);
//        }
//
//        EnumerationList enumerationList = new EnumerationList();
//        enumerationList.setCodelistCode(initialEnumeration.getCodelistCode());
//        enumerationListService.save(enumerationList);
//        Enumeration enumerationDB = enumerationRepositoryService.save(initialEnumeration);
//        enumerationEventService.createEnumerationEventNew(enumerationDB, event);
//
//        List<EnumerationMultivalues> enumerationMultivaluesList = new ArrayList<>();
//
//        Map<String, List<String>> codelistNameList = new HashMap<>();
//        Map<String, List<String>> codelistNoteList = new HashMap<>();
//        Map<String, List<String>> codelistManagerList = new HashMap<>();
//        Map<String, List<String>> codelistSourceList = new HashMap<>();
//
//        HashSet<String> localIdCodelistName = new HashSet<>();
//        if (codeListType.getCodelistName() != null) {
//            if (codeListType.getCodelistName().getDefaultCodelistName() != null) {
//                codelistNameList.put(SK_LOCALE, new ArrayList<>());
//                codelistNameList.get(SK_LOCALE).add(codeListType.getCodelistName().getDefaultCodelistName());
//                localIdCodelistName.add(DEFAULT);
//            }
//
//            for (CodelistNameLocType nameLocType : codeListType.getCodelistName().getLocalizedCodelistName()) {
//                String localId = checkLocalizationString(nameLocType.getLang().getLanguageId(), initialEnumeration.getCodelistCode());
//                if (localIdCodelistName.contains(localId))
//                    throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_NAME, null);
//                if (localId.equals(SK_LOCALE)) {
//                    localIdCodelistName.add(SK_LOCALE);
//                    codelistNameList.get(SK_LOCALE).set(codelistNameList.get(SK_LOCALE).size()-1, nameLocType.getCodelistName());
//                    continue;
//                }
//                if (!codelistNameList.containsKey(localId)) {
//                    codelistNameList.put(localId, new ArrayList<>());
//                    localIdCodelistName.add(localId);
//                }
//                codelistNameList.get(localId).add(nameLocType.getCodelistName());
//            }
//        }
//
//        for (LocalizedCodelistManagerType localizedCodelistCodeManagerType : codeListType.getCodelistManager()) {
//            Set<String> localIdManager = new HashSet<>();
//            if (!codelistManagerList.containsKey(SK_LOCALE)) {
//                codelistManagerList.put(SK_LOCALE, new ArrayList<>());
//                localIdManager.add(DEFAULT);
//            }
//            codelistManagerList.get(SK_LOCALE).add(localizedCodelistCodeManagerType.getDefaultCodelistManager());
//
//            for (CodelistManagerLocType codelistManagerLocType : localizedCodelistCodeManagerType.getLocalizedCodelistManager()) {
//                String localId = checkLocalizationString(codelistManagerLocType.getLang().getLanguageId(), initialEnumeration.getCodelistCode());
//                if (localIdManager.contains(localId))
//                    throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_MANAGER, null);
//                if (localId.equals(SK_LOCALE)) {
//                    localIdManager.add(SK_LOCALE);
//                    codelistManagerList.get(SK_LOCALE).set(codelistManagerList.get(SK_LOCALE).size()-1, codelistManagerLocType.getCodelistManager());
//                    continue;
//                }
//                if (!codelistManagerList.containsKey(localId)) {
//                    codelistManagerList.put(localId, new ArrayList<>());
//                    localIdManager.add(localId);
//                }
//                codelistManagerList.get(localId).add(codelistManagerLocType.getCodelistManager());
//            }
//        }
//
//        for (LocalizedNoteType localizedNoteType : codeListType.getNote()) {
//            Set<String> localIdNote = new HashSet<>();
//            if (localizedNoteType.getDefaultNote() != null) {
//                if (!codelistNoteList.containsKey(SK_LOCALE)) {
//                    codelistNoteList.put(SK_LOCALE, new ArrayList<>());
//                    localIdNote.add(DEFAULT);
//                }
//                codelistNoteList.get(SK_LOCALE).add(localizedNoteType.getDefaultNote());
//            }
//
//            for (NoteLocType noteLocType : localizedNoteType.getLocalizedNote()) {
//                String localId = checkLocalizationString(noteLocType.getLang().getLanguageId(), initialEnumeration.getCodelistCode());
//                if (localIdNote.contains(localId))
//                    throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_NOTE, null);
//                if (localId.equals(SK_LOCALE)) {
//                    localIdNote.add(SK_LOCALE);
//                    codelistNoteList.get(SK_LOCALE).set(codelistNoteList.get(SK_LOCALE).size()-1 ,noteLocType.getNote());
//                    continue;
//                }
//                if (!codelistNoteList.containsKey(localId)) {
//                    codelistNoteList.put(localId, new ArrayList<>());
//                    localIdNote.add(localId);
//                }
//                codelistNoteList.get(localId).add(noteLocType.getNote());
//            }
//        }
//
//        codelistSourceList.put(SK_LOCALE, new ArrayList<>());
//        for (String codelistSource : codeListType.getSourceCodelist()) {
//            codelistSourceList.get(SK_LOCALE).add(codelistSource);
//        }
//        Set<String> processedLocalizedId = new HashSet<>();
//        List<String> allKeys = new ArrayList<>();
//        allKeys.addAll(codelistNameList.keySet());
//        allKeys.addAll(codelistManagerList.keySet());
//        allKeys.addAll(codelistNoteList.keySet());
//
//        for (String key : allKeys) {
//            if (processedLocalizedId.contains(key))
//                continue;
//            processedLocalizedId.add(key);
//
//            int nameSize = codelistNameList.get(key) == null ? 0 : codelistNameList.get(key).size();
//            int managerSize = codelistManagerList.get(key) == null ? 0 : codelistManagerList.get(key).size();
//            int noteSize = codelistNoteList.get(key) == null ? 0 : codelistNoteList.get(key).size();
//            int sourceSize = codelistSourceList.get(key) == null ? 0 : codelistSourceList.get(key).size();
//            int[] sizes = { nameSize, managerSize, noteSize, sourceSize };
//            int max = Arrays.stream(sizes).max().getAsInt();
//
//            for (int i = 0; i < max; i++) {
//                EnumerationMultivaluesId enumerationMultiValuesId = new EnumerationMultivaluesId();
//                EnumerationMultivalues enumerationMultivalues = new EnumerationMultivalues();
//                enumerationMultivaluesList.add(enumerationMultivalues);
//                enumerationMultiValuesId.setMultivalueIndex(i);
//                enumerationMultiValuesId.setLocaleId(key);
//                enumerationMultivalues.setEnumeration(enumerationDB);
//                enumerationMultivalues.setEnumerationMultivaluesId(enumerationMultiValuesId);
//
//                if (codelistNameList.containsKey(key) && codelistNameList.get(key).size() > i) {
//                    enumerationMultivalues.setCodelistName(codelistNameList.get(key).get(i));
//                }
//                if (codelistNoteList.containsKey(key) && codelistNoteList.get(key).size() > i) {
//                    enumerationMultivalues.setNote(codelistNoteList.get(key).get(i));
//                }
//                if (codelistManagerList.containsKey(key) && codelistManagerList.get(key).size() > i) {
//                    enumerationMultivalues.setCodelistManager(codelistManagerList.get(key).get(i));
//                }
//                if (codelistSourceList.containsKey(key) && codelistSourceList.get(key).size() > i) {
//                    enumerationMultivalues.setSourceCodelist(codelistSourceList.get(key).get(i));
//                }
//            }
//        }
//        enumerationSnapshotService.createNewEnumerationSnapshot(initialEnumeration, event, 0);
//        initialEnumeration.getEnumerationMultivalues().addAll(enumerationMultivaluesService.saveAll(enumerationMultivaluesList));
//        enumerationMultivaluesSnapshotService.createNewEnumerationMultivaluesSnapshot(initialEnumeration, event, 0);
//
//        Map<String, List<EnumerationValue>> itemCodes = new HashMap<>();
//
//        List<CodeListDataRecType> records = new ArrayList<CodeListDataRecType>(importedData.getRecords().getRecord());
//
//        for (CodeListDataRecType codeListRecord : getSortedRecords(records)) {
//            String itemCode = codeListRecord.getItemCode();
//
//            if (itemCode == null)
//                throw new CommonException(HttpStatus.BAD_REQUEST, CHYBA_ITEM_CODE, null);
//
//            EnumerationValue newEnumerationValue = createEnumerationValueFromRecord(OperationEnum.INITIAL_LOAD, codeListRecord, initialEnumeration.getCodelistCode(), event, enumerationValueSnapshotIndex, enumerationValueMultivaluesSnapshotIndex);
//
//            if (itemCodes.containsKey(itemCode)) {
//                if (enumerationValuesService.checkIfEnumerationValueOverlapsEnumerationValuesInList(itemCodes.get(itemCode), newEnumerationValue))
//                    throw new CommonException(HttpStatus.BAD_REQUEST, DUPLIKOVANY_ITEM_CODE + codeListRecord.getItemCode(), null);
//
//                List<EnumerationValue> listDataRecTypes = itemCodes.get(itemCode);
//                listDataRecTypes.add(newEnumerationValue);
//                itemCodes.put(itemCode, listDataRecTypes);
//            } else {
//                List<EnumerationValue> listDataRecTypes = new ArrayList<>();
//                listDataRecTypes.add(newEnumerationValue);
//                itemCodes.put(itemCode, listDataRecTypes);
//            }
//            notifications.add(notificationService.createNotification(notificationService.createEnumerationValueEventData(enumerationDB.getCodelistCode(), newEnumerationValue.getItemCode(), newEnumerationValue.getId(), null, notificationService.createUrl(ENUMERATION_VALUE, enumerationDB.getCodelistCode(), newEnumerationValue.getItemCode(), newEnumerationValue.getId().toString())), event.getId(), CategoryT.CREATE, DomainT.ENUMERATION_VALUE, userInfo.getLogin(), null));
//        }
//        enumerationInitialLoadDeniedService.add(initialEnumeration.getCodelistCode());
//
//        notificationService.sendNotifications(ENUMERATION_VALUE_TOPIC, notifications);
//        NotificationT notification = notificationService.createNotification(notificationService.createEnumerationEventData(enumerationDB.getCodelistCode(), enumerationDB.getId(), null, notificationService.createUrl(ENUMERATION, enumerationDB.getCodelistCode(), enumerationDB.getId().toString())), event.getId(), CategoryT.CREATE, DomainT.ENUMERATION, userInfo.getLogin(), null);
//        notificationService.sendNotifications(ENUMERATION_TOPIC, Arrays.asList(notification));
//
//        autoExportConfig.sendNotification(codeListType.getCodelistCode(), null, null, false);
//    }
//
//    private Map <String, CodeListDataRecType> recordListToMap(List<CodeListDataRecType> records) {
//        Map <String, CodeListDataRecType> map = new HashMap<>();
//        for (CodeListDataRecType codeListRecord : records) {
//            map.put(codeListRecord.getItemCode(), codeListRecord);
//        }
//        return map;
//    }
//
//    private List<CodeListDataRecType> getPredecessors(Map <String, CodeListDataRecType> map, CodeListDataRecType codeListRecord) {
//        List<CodeListDataRecType> result = new ArrayList<>();
//        result.add(codeListRecord);
//
//        String parentItemCode = codeListRecord.getParentItemCode();
//        while (map.containsKey(parentItemCode)) {
//            CodeListDataRecType parent = map.get(parentItemCode);
//            result.add(0, parent);
//            parentItemCode = parent.getParentItemCode();
//        }
//
//        for (CodeListDataRecType r : result) {
//            map.remove(r.getItemCode());
//        }
//        return result;
//    }
//
//    private List<CodeListDataRecType> getSortedRecords(List<CodeListDataRecType> records) {
//        List<CodeListDataRecType> result = new ArrayList<>();
//        HashSet <String> addedRecords = new HashSet<>();
//        int i;
//        boolean addedItem=true;
//
//        while(records.size()>0 && addedItem==true)
//        {
//            addedItem = false;
//            for(i=0;i<records.size();i++)
//            {
//                if (records.get(i).getParentItemCode()==null || addedRecords.contains(records.get(i).getParentItemCode()))
//                {
//                    result.add(records.get(i));
//                    addedRecords.add(records.get(i).getItemCode());
//                    records.remove(i);
//                    addedItem = true;
//                    i--;
//                }
//            }
//        }
//
//        return result;
//
//		/*Map <String, CodeListDataRecType> recordMap = recordListToMap(records);
//
//
//		while (recordMap.size() > 0) {
//			Map.Entry<String, CodeListDataRecType> entry = recordMap.entrySet().iterator().next();
//			result.addAll(getPredecessors(recordMap, entry.getValue()));
//		}
//		*/
//    }
//
//    /**
//     * Aktuliazácia číselníka a jeho hodnôt
//     *
//     * @param importedData objekto reprezentujúci importované dáta
//     * @param enumerations  číslníky, ku ktorému sa dáta viažu
//     * @param event        ku ktorému sa operácia importu viaže
//     * @throws Exception vznikne, ak sú dáta nevalidné alebo chybné (v rozpore s inými dátami - napr. dátumy)
//     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    private void reloadData(ImportExportType importedData, List<Enumeration> enumerations, Event event, AtomicInteger enumerationValueSnapshotIndex, AtomicInteger enumerationValueMultivaluesSnapshotIndex, UserInfo userInfo) throws Exception {
//
//        Enumeration enumeration;
//        Long oldEnumerationId;
//
//        List<Enumeration> validEnumerations = enumerations.stream().filter(Enumeration::getIsValid).collect(Collectors.toList());
//        if (validEnumerations.isEmpty()) {
//            throw new CommonException(HttpStatus.BAD_REQUEST, NEEXISTUJE_CISELNIK_KTORY_JE_ALEBO_BY_MAL_BYT_PLATNY, null);
//        }else {
//            validEnumerations.sort(Comparator.comparing(Enumeration::getEffectiveFrom));
//            enumeration = validEnumerations.get(0);
//            oldEnumerationId = enumeration.getId();
//            validEnumerations.remove(enumeration);
//        }
//        enumerationEventService.createEnumerationEvent(enumeration, event);
//        List<NotificationT> notifications = new ArrayList<>();
//
//        for (Enumeration validEnumeration : validEnumerations) {
//            if (validEnumeration.getEffectiveFrom().after(new Date()))
//                validEnumeration.setIsValid(false);
//            else
//                validEnumeration.setEffectiveTo(new Date());
//        }
//
//        CodeListType codeListType = importedData.getType().getImport().getCodeList();
//        boolean enumerationUpdated;
//
//        enumerationUpdated = checkChangeSimpleEnumerationAttributes(enumeration, codeListType);
//        if (!enumerationUpdated)
//            enumerationUpdated = checkChangeEnumerationMultivaluesAttributes(enumeration, codeListType);
//
//        if (enumerationUpdated) {
//            enumerationSnapshotService.createOldEnumerationSnapshot(enumeration, event, 0);
//            enumerationMultivaluesSnapshotService.createOldEnumerationMultivaluesSnapshot(enumeration, event, 0);
//
//            Enumeration newEnumeration = new Enumeration();
//
//            newEnumeration.setCodelistCode(codeListType.getCodelistCode());
//            newEnumeration.setReferenceIdentifier(codeListType.getReferenceIdentifier());
//            newEnumeration.setIsValid(true);
//            newEnumeration.setCreateDate(new Date());
//
//            if (codeListType.getEffectiveFrom() == null || !codeListType.getEffectiveFrom().toGregorianCalendar().getTime().after(new Date()))
//                newEnumeration.setEffectiveFrom(DateUtils.toDate(LocalDate.now().plusDays(1)));
//            else
//                newEnumeration.setEffectiveFrom(codeListType.getEffectiveFrom().toGregorianCalendar().getTime());
//
//            if (codeListType.getValidFrom() != null)
//                //throw new CommonException(HttpStatus.BAD_REQUEST, DATUM_ZACIATKU_PLATNOSTI_JE_VYPLNENY, null);
//                newEnumeration.setValidFrom(codeListType.getValidFrom().toGregorianCalendar().getTime());
//            else
//                newEnumeration.setValidFrom(new Date());
//
//            if (codeListType.getEffectiveTo() != null)
//                newEnumeration.setEffectiveTo(codeListType.getEffectiveTo().toGregorianCalendar().getTime());
//            else
//                newEnumeration.setEffectiveTo(null);
//
//            if (newEnumeration.getEffectiveTo() != null &&  newEnumeration.getEffectiveFrom().after(newEnumeration.getEffectiveTo()))
//                throw new CommonException(HttpStatus.BAD_REQUEST, DATUM_ZACIATKU_UCINNOSTI_JE_PO_DATUME_KONCA_UCINNOSTI, null);
//
//            if (newEnumeration.getValidFrom().after(newEnumeration.getEffectiveFrom()))
//                throw new CommonException(HttpStatus.BAD_REQUEST, DATUM_PLATNOSTI_JE_PO_DATUME_ZACIATKU_UCINNOSTI, null);
//
//            if (enumeration.getEffectiveFrom().after(new Date()))
//                enumeration.setIsValid(false);
//
//            if (enumeration.getEffectiveTo() == null) {
//                enumeration.setEffectiveTo(DateUtils.toDate(DateUtils.toLocalDate(newEnumeration.getEffectiveFrom()).minusDays(1)));
//            } else if (!enumeration.getEffectiveTo().before(newEnumeration.getEffectiveFrom())) {
//                enumeration.setEffectiveTo(DateUtils.toDate(DateUtils.toLocalDate(newEnumeration.getEffectiveFrom()).minusDays(1)));
//            }
//            enumeration.setCreateDate(new Date());
//
//            enumerationRepositoryService.save(enumeration);
//            enumerationSnapshotService.createNewEnumerationSnapshot(enumeration, event, 0);
//            enumerationMultivaluesSnapshotService.createNewEnumerationMultivaluesSnapshot(enumeration, event, 0);
//            enumerationRepositoryService.save(newEnumeration);
//            enumerationEventService.createEnumerationEventNew(newEnumeration, event);
//            enumeration = newEnumeration;
//
//            //MULTIVALUES pre ENUM
//            List<EnumerationMultivalues> enumerationMultivaluesList = new ArrayList<>();
//
//            HashMap<String, List<String>> codelistNameList = new HashMap<>();
//            HashMap<String, List<String>> codelistNoteList = new HashMap<>();
//            HashMap<String, List<String>> codelistManagerList = new HashMap<>();
//            HashMap<String, List<String>> codelistSourceList = new HashMap<>();
//
//            HashSet<String> localIdCodelistName = new HashSet<>();
//            if (codeListType.getCodelistName() != null) {
//                if (codeListType.getCodelistName().getDefaultCodelistName() != null) {
//                    codelistNameList.put(SK_LOCALE, new ArrayList<>());
//                    codelistNameList.get(SK_LOCALE).add(codeListType.getCodelistName().getDefaultCodelistName());
//                    localIdCodelistName.add(DEFAULT);
//                }
//
//                for (CodelistNameLocType nameLocType : codeListType.getCodelistName().getLocalizedCodelistName()) {
//                    String localId = checkLocalizationString(nameLocType.getLang().getLanguageId(), newEnumeration.getCodelistCode());
//                    if (localIdCodelistName.contains(localId))
//                        throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_NAME, null);
//                    if (localId.equals(SK_LOCALE)) {
//                        localIdCodelistName.add(SK_LOCALE);
//                        codelistNameList.get(SK_LOCALE).set(codelistNameList.get(SK_LOCALE).size()-1, nameLocType.getCodelistName());
//                        continue;
//                    }
//                    if (!codelistNameList.containsKey(localId)) {
//                        codelistNameList.put(localId, new ArrayList<>());
//                        localIdCodelistName.add(localId);
//                    }
//                    codelistNameList.get(localId).add(nameLocType.getCodelistName());
//                }
//            }
//
//            for (LocalizedCodelistManagerType localizedCodelistCodeManagerType : codeListType.getCodelistManager()) {
//                HashSet<String> localIdManager = new HashSet<>();
//                if (!codelistManagerList.containsKey(SK_LOCALE)) {
//                    codelistManagerList.put(SK_LOCALE, new ArrayList<>());
//                    localIdManager.add(DEFAULT);
//                }
//                codelistManagerList.get(SK_LOCALE).add(localizedCodelistCodeManagerType.getDefaultCodelistManager());
//
//                for (CodelistManagerLocType codelistManagerLocType : localizedCodelistCodeManagerType.getLocalizedCodelistManager()) {
//                    String localId = checkLocalizationString(codelistManagerLocType.getLang().getLanguageId(), newEnumeration.getCodelistCode());
//                    if (localIdManager.contains(localId))
//                        throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_MANAGER, null);
//                    if (localId.equals(SK_LOCALE)) {
//                        localIdManager.add(SK_LOCALE);
//                        codelistManagerList.get(SK_LOCALE).set(codelistManagerList.get(SK_LOCALE).size()-1, codelistManagerLocType.getCodelistManager());
//                        continue;
//                    }
//                    if (!codelistManagerList.containsKey(localId)) {
//                        codelistManagerList.put(localId, new ArrayList<>());
//                        localIdManager.add(localId);
//                    }
//                    codelistManagerList.get(localId).add(codelistManagerLocType.getCodelistManager());
//                }
//            }
//
//            for (LocalizedNoteType localizedNoteType : codeListType.getNote()) {
//                HashSet<String> localIdNote = new HashSet<>();
//                if (localizedNoteType.getDefaultNote() != null) {
//                    if (!codelistNoteList.containsKey(SK_LOCALE)) {
//                        codelistNoteList.put(SK_LOCALE, new ArrayList<>());
//                        localIdNote.add(DEFAULT);
//                    }
//                    codelistNoteList.get(SK_LOCALE).add(localizedNoteType.getDefaultNote());
//                }
//
//                for (NoteLocType noteLocType : localizedNoteType.getLocalizedNote()) {
//                    String localId = checkLocalizationString(noteLocType.getLang().getLanguageId(), newEnumeration.getCodelistCode());
//                    if (localIdNote.contains(localId))
//                        throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, DUPLICITNY_KOD_JAZYKA_PRE_JEDEN_OBJEKT_CODELIST_CODE_NOTE, null);
//                    if (localId.equals(SK_LOCALE)) {
//                        localIdNote.add(SK_LOCALE);
//                        codelistNoteList.get(SK_LOCALE).set(codelistNoteList.get(SK_LOCALE).size()-1, noteLocType.getNote());
//                        continue;
//                    }
//                    if (!codelistNoteList.containsKey(localId)) {
//                        codelistNoteList.put(localId, new ArrayList<>());
//                        localIdNote.add(localId);
//                    }
//                    codelistNoteList.get(localId).add(noteLocType.getNote());
//                }
//            }
//
//            codelistSourceList.put(SK_LOCALE, new ArrayList<>());
//            for (String codelistSource : codeListType.getSourceCodelist()) {
//                codelistSourceList.get(SK_LOCALE).add(codelistSource);
//            }
//
//            if (!codelistNameList.containsKey(SK_LOCALE))
//                throw new CommonException(HttpStatus.BAD_REQUEST, CHYBA_MENO_CISELNIKA, null);
//
//            HashSet<String> processedLocalizedId = new HashSet<>();
//            List<String> allKeys = new ArrayList<>();
//            allKeys.addAll(codelistNameList.keySet());
//            allKeys.addAll(codelistManagerList.keySet());
//            allKeys.addAll(codelistNoteList.keySet());
//
//            for (String key : allKeys) {
//                if (processedLocalizedId.contains(key))
//                    continue;
//                processedLocalizedId.add(key);
//
//                int nameSize = codelistNameList.get(key) == null ? 0 : codelistNameList.get(key).size();
//                int managerSize = codelistManagerList.get(key) == null ? 0 : codelistManagerList.get(key).size();
//                int noteSize = codelistNoteList.get(key) == null ? 0 : codelistNoteList.get(key).size();
//                int sourceSize = codelistSourceList.get(key) == null ? 0 : codelistSourceList.get(key).size();
//                int[] sizes = { nameSize, managerSize, noteSize, sourceSize };
//                int max = Arrays.stream(sizes).max().getAsInt();
//
//                for (int i = 0; i < max; i++) {
//                    EnumerationMultivaluesId enumerationMultiValuesId = new EnumerationMultivaluesId();
//                    EnumerationMultivalues enumerationMultivalues = new EnumerationMultivalues();
//                    enumerationMultivaluesList.add(enumerationMultivalues);
//                    enumerationMultiValuesId.setMultivalueIndex(i);
//                    enumerationMultiValuesId.setLocaleId(key);
//                    enumerationMultivalues.setEnumeration(newEnumeration);
//                    enumerationMultivalues.setEnumerationMultivaluesId(enumerationMultiValuesId);
//
//                    if (codelistNameList.containsKey(key) && codelistNameList.get(key).size() > i) {
//                        enumerationMultivalues.setCodelistName(codelistNameList.get(key).get(i));
//                    }
//                    if (codelistNoteList.containsKey(key) && codelistNoteList.get(key).size() > i) {
//                        enumerationMultivalues.setNote(codelistNoteList.get(key).get(i));
//                    }
//                    if (codelistManagerList.containsKey(key) && codelistManagerList.get(key).size() > i) {
//                        enumerationMultivalues.setCodelistManager(codelistManagerList.get(key).get(i));
//                    }
//                    if (codelistSourceList.containsKey(key) && codelistSourceList.get(key).size() > i) {
//                        enumerationMultivalues.setSourceCodelist(codelistSourceList.get(key).get(i));
//                    }
//                }
//            }
//            enumerationSnapshotService.createNewEnumerationSnapshot(newEnumeration, event, 1);
//            newEnumeration.getEnumerationMultivalues().addAll(enumerationMultivaluesService.saveAll(enumerationMultivaluesList));
//            enumerationMultivaluesSnapshotService.createNewEnumerationMultivaluesSnapshot(newEnumeration, event, 0);
//        }
//
//        List<EnumerationValue> enumerationValues = enumerationValuesService.findAllByCodelistCode(enumeration.getCodelistCode(), false).stream().filter(element -> element.getIsValid() && (element.getEffectiveTo() == null || !element.getEffectiveTo().before(new Date()))).collect(Collectors.toList());
//        HashMap<String, EnumerationValue> enumerationValueMap = new HashMap<>();
//        for (EnumerationValue enumerationValue : enumerationValues) {
//            enumerationValueMap.put(enumerationValue.getItemCode(), enumerationValue);
//        }
//
//        boolean enumerationValueUpdated = false;
//        for (CodeListDataRecType codeListRecord : importedData.getRecords().getRecord()) {
//            if (checkChangeEnumerationValueAttributes(enumerationValueMap.get(codeListRecord.getItemCode()), codeListRecord) || checkChangeEnumerationValueMultivaluesAttributes(enumerationValueMap.get(codeListRecord.getItemCode()), codeListRecord)) {
//                NotificationT notification = updateChangeEnumerationValueAttributes(enumeration, enumerationValueMap.get(codeListRecord.getItemCode()), codeListRecord, event, enumerationValueSnapshotIndex, enumerationValueMultivaluesSnapshotIndex, userInfo);
//                notifications.add(notification);
//                enumerationValueUpdated = true;
//            }
//            enumerationValueMap.remove(codeListRecord.getItemCode());
//        }
//
//        for (Map.Entry<String, EnumerationValue> entry: enumerationValueMap.entrySet()) {
//            EnumerationValue enumerationValue = entry.getValue();
//            enumerationValueSnapshotService.createOldEnumerationValueSnapshot(entry.getValue(), event, enumerationValueSnapshotIndex.get());
//            enumerationValuesService.disableEnumerationValue(enumerationValue);
//            enumerationValueSnapshotService.createNewEnumerationValueSnapshot(entry.getValue(), event, enumerationValueSnapshotIndex.get());
//            enumerationValueSnapshotIndex.addAndGet(1);
//            enumerationValueUpdated = true;
//            enumerationValueEventService.createEnumerationValueEvent(entry.getValue(), event);
//            notifications.add(notificationService.createNotification(notificationService.createEnumerationValueEventData(enumerationValue.getCodelistCode(), enumerationValue.getItemCode(), enumerationValue.getId(), enumerationValue.getId(), notificationService.createUrl(ENUMERATION_VALUE, enumerationValue.getCodelistCode(), enumerationValue.getItemCode(), enumerationValue.getId().toString())), event.getId(), CategoryT.UPDATE, DomainT.ENUMERATION_VALUE, userInfo.getLogin(), null));
//        }
//
//        if (enumerationUpdated || enumerationValueUpdated)
//            enumerationEventService.createEnumerationEventNew(enumeration, event);
//
//        if (enumerationUpdated) {
//            NotificationT notification = notificationService.createNotification(notificationService.createEnumerationEventData(enumeration.getCodelistCode(), enumeration.getId(), oldEnumerationId, notificationService.createUrl(ENUMERATION, enumeration.getCodelistCode(), enumeration.getId().toString())), event.getId(), CategoryT.UPDATE, DomainT.ENUMERATION, userInfo.getLogin(), null);
//            notificationService.sendNotifications(ENUMERATION_TOPIC, Arrays.asList(notification));
//        }
//        notificationService.sendNotifications(ENUMERATION_VALUE_TOPIC, notifications);
//    }
//
//    /**
//     * Porovnanie jednoduchých atribútov Enumeration s XML dátami
//     *
//     * @param enumeration z databázy, s ktorým sa dáta z XML porovnávajú
//     * @param codeListType dáta z XML
//     * @return true, ak našlo rozdiel medzi dátami v Enumeration a XML
//     */
//    @SuppressWarnings("static-method")
//    private boolean checkChangeSimpleEnumerationAttributes(Enumeration enumeration, CodeListType codeListType) {
//        if (codeListType.getReferenceIdentifier() != null && !codeListType.getReferenceIdentifier().equals(enumeration.getReferenceIdentifier()))
//            return true;
//
//        if (codeListType.getEffectiveFrom() != null && !codeListType.getEffectiveFrom().toGregorianCalendar().getTime().equals(enumeration.getEffectiveFrom())) {
//            return true;
//        }
//
//        if (codeListType.getEffectiveTo() != null && !codeListType.getEffectiveTo().toGregorianCalendar().getTime().equals(enumeration.getEffectiveTo())) {
//            return true;
//        }
//
//        if (codeListType.getValidFrom() != null) {
//            return !codeListType.getValidFrom().toGregorianCalendar().getTime().equals(enumeration.getValidFrom());
//        }
//
//        return false;
//    }
//
//    /**
//     * Porovnanie Multivalues atribútov Enumeration s XML dátami
//     *
//     * @param enumeration z databázy, s ktorým sa dáta z XML porovnávajú
//     * @param codeListType dáta z XML
//     * @return true, ak našlo rozdiel medzi dátami v Enumeration a XML
//     * @throws SecurityException
//     * @throws NoSuchMethodException
//     */
//    private boolean checkChangeEnumerationMultivaluesAttributes(Enumeration enumeration, CodeListType codeListType) throws NoSuchMethodException, SecurityException {
//        List<EnumerationMultivalues> defaultSourceCodes = enumeration.getEnumerationMultivalues().stream().filter(element -> element.getSourceCodelist() != null && !element.getSourceCodelist().isEmpty()).sorted(Comparator.comparingInt((EnumerationMultivalues em) -> em.getEnumerationMultivaluesId().getMultivalueIndex())).collect(Collectors.toList());
//
//        Iterator<EnumerationMultivalues> defaultSourceCodeIterator = defaultSourceCodes.iterator();
//        Iterator<String> sourceCodesIterator = codeListType.getSourceCodelist().iterator();
//
//        while (defaultSourceCodeIterator.hasNext() && sourceCodesIterator.hasNext()) {
//            EnumerationMultivalues defaultEnumMultivalue = defaultSourceCodeIterator.next();
//            String importedSourceCode = sourceCodesIterator.next();
//            if (!importedSourceCode.equals(defaultEnumMultivalue.getSourceCodelist())) {
//                return true;
//            }
//        }
//
//        if (defaultSourceCodeIterator.hasNext() ^ sourceCodesIterator.hasNext())
//            return true;
//
//        List<EnumerationMultivalues> enumerationMultivaluesManagers = enumeration.getEnumerationMultivalues().stream().filter(element -> element.getCodelistManager() != null && !element.getCodelistManager().isEmpty()).collect(Collectors.toList());
//        HashMap<String, List<EnumerationMultivalues>> managersByLocalId = new HashMap<>();
//        HashMap<String, Integer> indexCounter = new HashMap<>();
//
//        for (LocalizedCodelistManagerType codelistManager : codeListType.getCodelistManager()) {
//            List<CodelistManagerLocType> localizedCopy = new ArrayList<>(codelistManager.getLocalizedCodelistManager());
//            String defaultValue = codelistManager.getDefaultCodelistManager();
//            int index = checkSKLocalization(codelistManager.getLocalizedCodelistManager(),  CodelistManagerLocType.class.getMethod(GET_LANG), codeListType.getCodelistCode());
//            if (index >= 0) {
//                defaultValue = codelistManager.getLocalizedCodelistManager().get(index).getCodelistManager();
//                localizedCopy.remove(index);
//            }
//
//            if (!managersByLocalId.containsKey(SK_LOCALE)) {
//                managersByLocalId.put(SK_LOCALE, enumerationMultivaluesManagers.stream().filter(element -> element.getEnumerationMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//                managersByLocalId.get(SK_LOCALE).sort(Comparator.comparingInt((EnumerationMultivalues em) -> em.getEnumerationMultivaluesId().getMultivalueIndex()));
//                indexCounter.put(SK_LOCALE, 0);
//            }
//            if (indexCounter.get(SK_LOCALE) < managersByLocalId.get(SK_LOCALE).size()) {
//                if (!managersByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getCodelistManager().equals(defaultValue)) {
//                    return true;
//                }
//                indexCounter.put(SK_LOCALE, indexCounter.get(SK_LOCALE) + 1);
//            } else {
//                return true;
//            }
//
//            for (CodelistManagerLocType codelistManagerLocType : localizedCopy) {
//                String key = checkLocalizationString(codelistManagerLocType.getLang().getLanguageId(), codeListType.getCodelistCode());
//                if (!managersByLocalId.containsKey(key)) {
//                    managersByLocalId.put(key, enumerationMultivaluesManagers.stream().filter(element -> element.getEnumerationMultivaluesId().getLocaleId().equals(key)).collect(Collectors.toList()));
//                    managersByLocalId.get(key).sort(Comparator.comparingInt((EnumerationMultivalues em) -> em.getEnumerationMultivaluesId().getMultivalueIndex()));
//                    indexCounter.put(key, 0);
//                }
//                if (indexCounter.get(key) < managersByLocalId.get(key).size()) {
//                    if (!managersByLocalId.get(key).get(indexCounter.get(key)).getCodelistManager().equals(codelistManagerLocType.getCodelistManager())) {
//                        return true;
//                    }
//                    indexCounter.put(key, indexCounter.get(key) + 1);
//                }
//                else {
//                    return true;
//                }
//            }
//        }
//
//        List<EnumerationMultivalues> enumerationMultivaluesNotes = enumeration.getEnumerationMultivalues().stream().filter(element -> element.getNote() != null && !element.getNote().isEmpty()).collect(Collectors.toList());
//        HashMap<String, List<EnumerationMultivalues>> noteByLocalId = new HashMap<>();
//        indexCounter = new HashMap<>();
//        for (LocalizedNoteType localizedNoteType : codeListType.getNote()) {
//            List<NoteLocType> localizedCopy = new ArrayList<>(localizedNoteType.getLocalizedNote());
//            String defaultValue = localizedNoteType.getDefaultNote();
//            int index = checkSKLocalization(localizedNoteType.getLocalizedNote(),  NoteLocType.class.getMethod(GET_LANG), codeListType.getCodelistCode());
//            if (index >= 0) {
//                defaultValue = localizedNoteType.getLocalizedNote().get(index).getNote();
//                localizedCopy.remove(index);
//            }
//
//            if (!noteByLocalId.containsKey(SK_LOCALE)) {
//                noteByLocalId.put(SK_LOCALE, enumerationMultivaluesNotes.stream().filter(element -> element.getEnumerationMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//                noteByLocalId.get(SK_LOCALE).sort(Comparator.comparingInt((EnumerationMultivalues em) -> em.getEnumerationMultivaluesId().getMultivalueIndex()));
//                indexCounter.put(SK_LOCALE, 0);
//            }
//
//            if (indexCounter.get(SK_LOCALE) < noteByLocalId.get(SK_LOCALE).size()) {
//                if (!noteByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getNote().equals(defaultValue)) {
//                    return true;
//                }
//                indexCounter.put(SK_LOCALE, indexCounter.get(SK_LOCALE) + 1);
//            } else {
//                return true;
//            }
//
//            for (NoteLocType noteLocType : localizedCopy) {
//                String key =  checkLocalizationString(noteLocType.getLang().getLanguageId(), codeListType.getCodelistCode());
//                if (!noteByLocalId.containsKey(key)) {
//                    noteByLocalId.put(key, enumerationMultivaluesNotes.stream().filter(element -> element.getNote() != null && !element.getNote().isEmpty()).collect(Collectors.toList()));
//                    noteByLocalId.get(key).sort(Comparator.comparingInt((EnumerationMultivalues em) -> em.getEnumerationMultivaluesId().getMultivalueIndex()));
//                    indexCounter.put(key, 0);
//                }
//
//                if (indexCounter.get(key) < noteByLocalId.get(key).size()) {
//                    if (!noteByLocalId.get(key).get(indexCounter.get(key)).getNote().equals(noteLocType.getNote())) {
//                        return true;
//                    }
//                    indexCounter.put(key, indexCounter.get(key) + 1);
//                } else {
//                    return true;
//                }
//            }
//
//        }
//
//        List<EnumerationMultivalues> enumerationMultivaluesCodelistNames = enumeration.getEnumerationMultivalues().stream().filter(element -> element.getCodelistName() != null && !element.getCodelistName().isEmpty()).sorted(Comparator.comparingInt((EnumerationMultivalues em) -> em.getEnumerationMultivaluesId().getMultivalueIndex())).collect(Collectors.toList());
//        HashMap<String, List<EnumerationMultivalues>> codelistNamesByLocalId = new HashMap<>();
//        indexCounter = new HashMap<>();
//
//
//        if (codeListType.getCodelistName() != null) {
//            List<CodelistNameLocType> localizedCopy = new ArrayList<>( codeListType.getCodelistName().getLocalizedCodelistName());
//            String defaultValue = codeListType.getCodelistName().getDefaultCodelistName();
//            int index = checkSKLocalization(codeListType.getCodelistName().getLocalizedCodelistName(),  CodelistNameLocType.class.getMethod(GET_LANG), codeListType.getCodelistCode());
//            if (index >= 0) {
//                defaultValue =codeListType.getCodelistName().getLocalizedCodelistName().get(index).getCodelistName();
//                localizedCopy.remove(index);
//            }
//
//            codelistNamesByLocalId.put(SK_LOCALE, enumerationMultivaluesCodelistNames.stream().filter(element -> element.getEnumerationMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//            indexCounter.put(SK_LOCALE, 0);
//
//            if (indexCounter.get(SK_LOCALE) < codelistNamesByLocalId.get(SK_LOCALE).size()) {
//                if (!codelistNamesByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getCodelistName().equals(defaultValue)) {
//                    return true;
//                }
//                indexCounter.put(SK_LOCALE, 1);
//            } else {
//                return true;
//            }
//
//            for (CodelistNameLocType codelistNameLocType : localizedCopy) {
//                String key =  checkLocalizationString(codelistNameLocType.getLang().getLanguageId(), codeListType.getCodelistCode());
//                if (!codelistNamesByLocalId.containsKey(key)) {
//                    codelistNamesByLocalId.put(key, enumerationMultivaluesCodelistNames.stream().filter(element -> element.getEnumerationMultivaluesId().getLocaleId().equals(key)).collect(Collectors.toList()));
//                    indexCounter.put(key, 0);
//                }
//
//                if (indexCounter.get(key) < codelistNamesByLocalId.get(key).size()) {
//                    if (!codelistNamesByLocalId.get(key).get(indexCounter.get(key)).getCodelistName().equals(codelistNameLocType.getCodelistName())) {
//                        return true;
//                    }
//                    indexCounter.put(key, indexCounter.get(key) + 1);
//                } else {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Porovnanie jednoduchých atribútov EnumerationValue s XML dátami
//     *
//     * @param enumerationValue z databázy, s ktorým sa dáta z XML porovnávajú
//     * @param codeListRecord dáta z XML
//     * @return true, ak našlo rozdiel medzi dátami v EnumerationValue a XML
//     * @throws SecurityException
//     * @throws NoSuchMethodException
//     */
//    private boolean checkChangeEnumerationValueAttributes(EnumerationValue enumerationValue, CodeListDataRecType codeListRecord) throws NoSuchMethodException, SecurityException {
//        if (enumerationValue == null)
//            return true;
//
//        if (codeListRecord.getValidFrom() != null && !codeListRecord.getValidFrom().toGregorianCalendar().getTime().equals(enumerationValue.getValidFrom()))
//            return true;
//
//        if (codeListRecord.getEffectiveFrom() != null) {
//            if(codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime().after(new Date()) && !codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime().equals(enumerationValue.getEffectiveFrom()))
//                return true;
//        }
//
//        if (codeListRecord.getEffectiveTo() != null && !codeListRecord.getEffectiveTo().toGregorianCalendar().getTime().equals(enumerationValue.getEffectiveTo()))
//            return true;
//
//        if (codeListRecord.getHierarchicalItemCode() != null && !codeListRecord.getHierarchicalItemCode().equals(enumerationValue.getHierarchicalItemCode()))
//            return true;
//
//        if (codeListRecord.getItemLogicalOrder() != null && !codeListRecord.getItemLogicalOrder().equals(enumerationValue.getItemLogicalOrder()))
//            return true;
//
//        if (codeListRecord.getOriginalItemCode() != null && !codeListRecord.getOriginalItemCode().equals(enumerationValue.getOriginalItemCode()))
//            return true;
//
//        if (codeListRecord.getParentItemCode() != null && !codeListRecord.getParentItemCode().equals(enumerationValue.getParentItemCode()))
//            return true;
//
//        if (codeListRecord.getUnitOfMeasure() != null && !codeListRecord.getUnitOfMeasure().equals(enumerationValue.getUnitOfMeasure()))
//            return true;
//
//        return checkChangeEnumerationValueMultivaluesAttributes(enumerationValue, codeListRecord);
//    }
//
//    /**
//     * Porovnanie Multivalues atribútov EnumerationValue s XML dátami
//     *
//     * @param enumerationValue z databázy, s ktorým sa dáta z XML porovnávajú
//     * @param codeListRecord dáta z XML
//     * @return true, ak našlo rozdiel medzi dátami v EnumerationValue a XML
//     * @throws SecurityException
//     * @throws NoSuchMethodException
//     */
//    private boolean checkChangeEnumerationValueMultivaluesAttributes(EnumerationValue enumerationValue, CodeListDataRecType codeListRecord) throws NoSuchMethodException, SecurityException {
//        List<EnumerationValuesMultivalues> itemNames = enumerationValue.getEnumerationValuesMultivalues().stream().filter(element -> element.getItemName() != null).collect(Collectors.toList());
//        HashMap<String, List<EnumerationValuesMultivalues>> itemNamesByLocalId = new HashMap<>();
//        HashMap<String, Integer> indexCounter = new HashMap<>();
//        for (LocalizedItemNameType localizedItemNameType : codeListRecord.getItemName()) {
//            List<ItemNameLocType> localizedCopy = new ArrayList<>(localizedItemNameType.getLocalizedItemName());
//            String defaultValue = localizedItemNameType.getDefaultItemName();
//            int index = checkSKLocalization(localizedCopy,  ItemNameLocType.class.getMethod(GET_LANG), enumerationValue.getItemCode());
//            if (index >= 0) {
//                defaultValue =localizedCopy.get(index).getItemName();
//                localizedCopy.remove(index);
//            }
//
//            if (!itemNamesByLocalId.containsKey(SK_LOCALE)) {
//                itemNamesByLocalId.put(SK_LOCALE,itemNames.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//                itemNamesByLocalId.get(SK_LOCALE).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                indexCounter.put(SK_LOCALE,0);
//            }
//            if (indexCounter.get(SK_LOCALE) < itemNamesByLocalId.get(SK_LOCALE).size()) {
//                if(!itemNamesByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getItemName().equals(defaultValue)) {
//                    return true;
//                }
//                indexCounter.put(SK_LOCALE, indexCounter.get(SK_LOCALE)+1);
//            }else {
//                return true;
//            }
//
//            for (ItemNameLocType itemNameLocType : localizedCopy) {
//                String key = itemNameLocType.getLang().getLanguageId();
//                if (!itemNamesByLocalId.containsKey(key)) {
//                    itemNamesByLocalId.put(key, itemNames.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(key)).collect(Collectors.toList()));
//                    itemNamesByLocalId.get(key).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                    indexCounter.put(key, 0);
//                }
//                if (indexCounter.get(key) < itemNamesByLocalId.get(key).size()) {
//                    if (!itemNamesByLocalId.get(key).get(indexCounter.get(key)).getItemName().equals(itemNameLocType.getItemName())) {
//                        return true;
//                    }
//                    indexCounter.put(key, indexCounter.get(key) + 1);
//                } else {
//                    return true;
//                }
//            }
//        }
//
//        List<EnumerationValuesMultivalues> itemShortenedNames = enumerationValue.getEnumerationValuesMultivalues().stream().filter(element -> element.getItemShortenedName() != null).collect(Collectors.toList());
//        HashMap<String, List<EnumerationValuesMultivalues>> itemShortenedNamesByLocalId = new HashMap<>();
//        indexCounter = new HashMap<>();
//        for (LocalizedItemShortenedNameType localizedItemShortenedNameType : codeListRecord.getItemShortenedName()) {
//            List<ItemShortenedNameLocType> localizedCopy = new ArrayList<>(localizedItemShortenedNameType.getLocalizedItemShortenedName());
//            String defaultValue = localizedItemShortenedNameType.getDefaultItemShortenedName();
//            int index = checkSKLocalization(localizedCopy,  ItemShortenedNameLocType.class.getMethod(GET_LANG), enumerationValue.getItemCode());
//            if (index >= 0) {
//                defaultValue =localizedCopy.get(index).getItemShortenedName();
//                localizedCopy.remove(index);
//            }
//
//            if (!itemShortenedNamesByLocalId.containsKey(SK_LOCALE)) {
//                itemShortenedNamesByLocalId.put(SK_LOCALE, itemShortenedNames.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//                itemShortenedNamesByLocalId.get(SK_LOCALE).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                indexCounter.put(SK_LOCALE, 0);
//            }
//            if (indexCounter.get(SK_LOCALE) < itemShortenedNamesByLocalId.get(SK_LOCALE).size()) {
//                if (!itemShortenedNamesByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getItemShortenedName().equals(defaultValue)) {
//                    return true;
//                }
//            }else {
//                return true;
//            }
//
//            for (ItemShortenedNameLocType itemShortenedNameLocType : localizedCopy) {
//                String key = itemShortenedNameLocType.getLang().getLanguageId();
//                if (!itemShortenedNamesByLocalId.containsKey(key)) {
//                    itemShortenedNamesByLocalId.put(key, itemShortenedNames.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(key)).collect(Collectors.toList()));
//                    itemShortenedNamesByLocalId.get(key).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                    indexCounter.put(key, 0);
//                }
//                if (indexCounter.get(key) < itemShortenedNamesByLocalId.get(key).size()) {
//                    if (!itemShortenedNamesByLocalId.get(key).get(indexCounter.get(key)).getItemShortenedName().equals(itemShortenedNameLocType.getItemShortenedName())) {
//                        return true;
//                    }
//                }else {
//                    return true;
//                }
//            }
//        }
//
//        List<EnumerationValuesMultivalues> itemAbbreviatedNames = enumerationValue.getEnumerationValuesMultivalues().stream().filter(element -> element.getItemAbbreviatedName() != null).collect(Collectors.toList());
//        HashMap<String, List<EnumerationValuesMultivalues>> itemAbbreviatedNameByLocalId = new HashMap<>();
//        indexCounter = new HashMap<>();
//        for (LocalizedItemAbbreviatedNameType localizedItemAbbreviatedNameType : codeListRecord.getItemAbbreviatedName()) {
//            List<ItemAbbreviatedNameLocType> localizedCopy = new ArrayList<>(localizedItemAbbreviatedNameType.getLocalizedItemAbbreviatedName());
//            String defaultValue = localizedItemAbbreviatedNameType.getDefaultItemAbbreviatedName();
//            int index = checkSKLocalization(localizedCopy,  ItemAbbreviatedNameLocType.class.getMethod(GET_LANG), enumerationValue.getItemCode());
//            if (index >= 0) {
//                defaultValue =localizedCopy.get(index).getItemAbbreviatedName();
//                localizedCopy.remove(index);
//            }
//            if (!itemAbbreviatedNameByLocalId.containsKey(SK_LOCALE)) {
//                itemAbbreviatedNameByLocalId.put(SK_LOCALE, itemAbbreviatedNames.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//                itemAbbreviatedNameByLocalId.get(SK_LOCALE).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                indexCounter.put(SK_LOCALE, 0);
//            }
//            if(indexCounter.get(SK_LOCALE) < itemAbbreviatedNameByLocalId.get(SK_LOCALE).size()) {
//                if (!itemAbbreviatedNameByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getItemAbbreviatedName().equals(defaultValue)) {
//                    return true;
//                }
//                indexCounter.put(SK_LOCALE, indexCounter.get(SK_LOCALE) + 1);
//            }else {
//                return true;
//            }
//
//            for (ItemAbbreviatedNameLocType itemAbbreviatedNameLocType : localizedCopy) {
//                String key = itemAbbreviatedNameLocType.getLang().getLanguageId();
//                if (!itemAbbreviatedNameByLocalId.containsKey(key)) {
//                    itemAbbreviatedNameByLocalId.put(key, itemAbbreviatedNames.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(key)).collect(Collectors.toList()));
//                    itemAbbreviatedNameByLocalId.get(key).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                    indexCounter.put(key, 0);
//                }
//                if (indexCounter.get(key) < itemAbbreviatedNameByLocalId.get(key).size()) {
//                    if (!itemAbbreviatedNameByLocalId.get(key).get(indexCounter.get(key)).getItemAbbreviatedName().equals(itemAbbreviatedNameLocType.getItemAbbreviatedName())) {
//                        return true;
//                    }
//                    indexCounter.put(key, indexCounter.get(key) + 1);
//                }else {
//                    return true;
//                }
//            }
//        }
//
//        List<EnumerationValuesMultivalues> additionalContents = enumerationValue.getEnumerationValuesMultivalues().stream().filter(element -> element.getAdditionalContent() != null).collect(Collectors.toList());
//        HashMap<String, List<EnumerationValuesMultivalues>> additionalContentByLocalId = new HashMap<>();
//        indexCounter = new HashMap<>();
//        for (LocalizedAdditionalContentType localizedAdditionalContentType : codeListRecord.getAdditionalContent()) {
//            List<AdditionalContentLocType> localizedCopy = new ArrayList<>(localizedAdditionalContentType.getLocalizedAdditionalContent());
//            String defaultValue = localizedAdditionalContentType.getDefaultAdditionalContent();
//            int index = checkSKLocalization(localizedCopy,  AdditionalContentLocType.class.getMethod(GET_LANG), enumerationValue.getItemCode());
//            if (index >= 0) {
//                defaultValue =localizedCopy.get(index).getAdditionalContent();
//                localizedCopy.remove(index);
//            }
//            if (!additionalContentByLocalId.containsKey(SK_LOCALE)) {
//                additionalContentByLocalId.put(SK_LOCALE, additionalContents.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//                additionalContentByLocalId.get(SK_LOCALE).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                indexCounter.put(SK_LOCALE, 0);
//            }
//            if (indexCounter.get(SK_LOCALE) < additionalContentByLocalId.get(SK_LOCALE).size()) {
//                if (!additionalContentByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getAdditionalContent().equals(defaultValue)) {
//                    return true;
//                }
//                indexCounter.put(SK_LOCALE, indexCounter.get(SK_LOCALE) + 1);
//            }else {
//                return true;
//            }
//
//            for (AdditionalContentLocType additionalContentLocType : localizedCopy) {
//                String key = additionalContentLocType.getLang().getLanguageId();
//                if (!additionalContentByLocalId.containsKey(key)) {
//                    additionalContentByLocalId.put(key, additionalContents.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(key)).collect(Collectors.toList()));
//                    additionalContentByLocalId.get(key).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                    indexCounter.put(key, 0);
//                }
//                if (indexCounter.get(key) < additionalContentByLocalId.get(key).size()) {
//                    if (!additionalContentByLocalId.get(key).get(indexCounter.get(key)).getAdditionalContent().equals(additionalContentLocType.getAdditionalContent())) {
//                        return true;
//                    }
//                    indexCounter.put(key, indexCounter.get(key) + 1);
//                }else {
//                    return true;
//                }
//            }
//        }
//
//        List<EnumerationValuesMultivalues> notes = enumerationValue.getEnumerationValuesMultivalues().stream().filter(element -> element.getNote() != null).collect(Collectors.toList());
//        HashMap<String, List<EnumerationValuesMultivalues>> notesByLocalId = new HashMap<>();
//        List<NoteLocType> localizedCopy = new ArrayList<>(codeListRecord.getNote().getLocalizedNote());
//        String defaultValue = codeListRecord.getNote().getDefaultNote();
//        int index = checkSKLocalization(localizedCopy,  NoteLocType.class.getMethod(GET_LANG), enumerationValue.getItemCode());
//        if (index >= 0) {
//            defaultValue =localizedCopy.get(index).getNote();
//            localizedCopy.remove(index);
//        }
//
//        indexCounter = new HashMap<>();
//        notesByLocalId.put(SK_LOCALE, notes.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(SK_LOCALE)).collect(Collectors.toList()));
//        notesByLocalId.get(SK_LOCALE).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//        indexCounter.put(SK_LOCALE, 0);
//        if (indexCounter.get(SK_LOCALE) < notesByLocalId.get(SK_LOCALE).size()) {
//            if (!notesByLocalId.get(SK_LOCALE).get(indexCounter.get(SK_LOCALE)).getNote().equals(defaultValue)) {
//                return true;
//            }
//            indexCounter.put(SK_LOCALE, indexCounter.get(SK_LOCALE) + 1);
//        } else {
//            return true;
//        }
//
//        for (NoteLocType noteLocType : localizedCopy) {
//            String key = noteLocType.getLang().getLanguageId();
//            if (!notesByLocalId.containsKey(key)) {
//                notesByLocalId.put(key, notes.stream().filter(element -> element.getEnumerationValuesMultivaluesId().getLocaleId().equals(key)).collect(Collectors.toList()));
//                notesByLocalId.get(key).sort(Comparator.comparingInt((EnumerationValuesMultivalues em) -> em.getEnumerationValuesMultivaluesId().getMultivalueIndex()));
//                indexCounter.put(key, 0);
//            }
//            if (indexCounter.get(key) < notesByLocalId.get(key).size()) {
//                if (!notesByLocalId.get(key).get(indexCounter.get(key)).getNote().equals(codeListRecord.getNote().getDefaultNote())) {
//                    return true;
//                }
//                indexCounter.put(key, indexCounter.get(key) + 1);
//            } else {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * Update change enumeration value attributes.
//     *
//     * @param enumeration , ku ktorému sa hodnota EnumerationValue viaže
//     * @param enumerationValue hodnota, ktorá sa má aktualizovať
//     * @param codeListRecord dáta z XML, podľa ktorých sa hodnota EnumerationValue bude aktualizovať
//     * @param event , ku ktorému sa operácia viaže
//     * @return
//     * @throws Exception
//     */
//    private NotificationT updateChangeEnumerationValueAttributes(Enumeration enumeration, EnumerationValue enumerationValue, CodeListDataRecType codeListRecord, Event event, AtomicInteger enumerationValueSnapshotIndex, AtomicInteger enumerationValueMultivaluesSnapshotIndex, UserInfo userInfo) throws Exception {
//
//        Long oldEnumerationValueId = null;
//
//        if (enumerationValue != null) {
//            enumerationValueSnapshotService.createOldEnumerationValueSnapshot(enumerationValue, event, enumerationValueSnapshotIndex.get());
//            enumerationValueMultivaluesSnapshotService.createOldEnumerationValueMultivaluesSnapshot(enumerationValue, event, enumerationValueMultivaluesSnapshotIndex.get());
//            enumerationValueMultivaluesSnapshotIndex.addAndGet(1);
//            enumerationValueEventService.createEnumerationValueEvent(enumerationValue, event);
//
//            if (!enumerationValue.getEffectiveFrom().after(new Date()))
//                enumerationValue.setEffectiveTo(new Date());
//            if (enumerationValue.getEffectiveTo()!= null && !enumerationValue.getEffectiveTo().equals(new Date()))
//                enumerationValue.setIsValid(false);
//            enumerationValue.setCreateDate(new Date());
//            enumerationValuesService.save(enumerationValue);
//            enumerationValueSnapshotService.createNewEnumerationValueSnapshot(enumerationValue, event, enumerationValueSnapshotIndex.get());
//            enumerationValueSnapshotIndex.addAndGet(1);
//            oldEnumerationValueId = enumerationValue.getId();
//        }
//
//        if (codeListRecord.getItemCode() == null)
//            throw new CommonException(HttpStatus.BAD_REQUEST, CHYBA_ITEM_CODE_UDAJ, null);
//
//        enumerationValue = new EnumerationValue();
//        enumerationValue.setItemCode(codeListRecord.getItemCode());
//        enumerationValue.setCodelistCode(enumeration.getCodelistCode());
//        enumerationValue.setIsValid(true);
//        enumerationValue.setCreateDate(new Date());
//
//        if (codeListRecord.getEffectiveTo() != null) {
//            Date effectiveTo = codeListRecord.getEffectiveTo().toGregorianCalendar().getTime();
//            if (enumeration.getEffectiveTo() != null && effectiveTo.after(enumeration.getEffectiveTo()))
//                throw new CommonException(HttpStatus.BAD_REQUEST, NOVY_DATUM_KONCA_UCINNOSTI_PRE_HODNOTU_S_ITEM_CODE + codeListRecord.getItemCode() + JE_PO_DATUME_UCINNOSTI_CISELNIKA, null);
//            enumerationValue.setEffectiveTo(effectiveTo);
//        } else {
//            if (enumeration.getEffectiveTo() != null)
//                throw new CommonException(HttpStatus.BAD_REQUEST, NOVY_DATUM_KONCA_UCINNOSTI_PRE_HODNOTU_S_ITEM_CODE + codeListRecord.getItemCode() + JE_PO_DATUME_UCINNOSTI_CISELNIKA, null);
//        }
//
//        if (codeListRecord.getEffectiveFrom() == null || !codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime().after(new Date()))
//            enumerationValue.setEffectiveFrom(DateUtils.toDate(LocalDate.now().plusDays(1)));
//        else
//            enumerationValue.setEffectiveFrom(codeListRecord.getEffectiveFrom().toGregorianCalendar().getTime());
//
//        if (codeListRecord.getValidFrom() == null)
//            enumerationValue.setValidFrom(new Date());
//        else
//            enumerationValue.setValidFrom(codeListRecord.getValidFrom().toGregorianCalendar().getTime());
//
//        if (enumerationValue.getEffectiveTo() != null && enumerationValue.getEffectiveTo().before(enumerationValue.getEffectiveFrom())) {
//            throw new CommonException(HttpStatus.BAD_REQUEST, DATUM_KONCA_UCINNOSTI_PRE_HODNOTU_S_ITEM_CODE + codeListRecord.getItemCode() + JE_PRED_DATUMOM_ZACIATKU_UCINNOSTI, null);
//        }
//
//        enumerationValue.setHierarchicalItemCode(codeListRecord.getHierarchicalItemCode());
//        enumerationValue.setItemLogicalOrder(codeListRecord.getItemLogicalOrder());
//        enumerationValue.setOriginalItemCode(codeListRecord.getOriginalItemCode());
//        enumerationValue.setParentItemCode(codeListRecord.getParentItemCode());
//        enumerationValue.setUnitOfMeasure(codeListRecord.getUnitOfMeasure());
//        enumerationValue.setReferenceIdentifier(codeListRecord.getReferenceIdentifier());
//
//        EnumerationValuesList enumerationValueList = new EnumerationValuesList();
//        enumerationValueList.setEnumerationValueListId(new EnumerationValueListId(enumerationValue.getItemCode(),enumerationValue.getCodelistCode()));
//
//        enumerationValueListService.save(enumerationValueList);
//        enumerationValue = enumerationValuesService.save(enumerationValue);
//        enumerationValueSnapshotService.createNewEnumerationValueSnapshot(enumerationValue, event, enumerationValueSnapshotIndex.get());
//        enumerationValueEventService.createNewEnumerationValueEvent(enumerationValue, event);
//        enumerationValueSnapshotIndex.addAndGet(1);
//
//
//
//        HashMap<String, List<String>> itemNames = new HashMap<>();
//        HashMap<String, List<String>> itemShortenedNames = new HashMap<>();
//        HashMap<String, List<String>> itemAbbreviatedNames = new HashMap<>();
//        HashMap<String, List<String>> additionalContents = new HashMap<>();
//        HashMap<String, List<String>> notes = new HashMap<>();
//
//        fillHashMaps(codeListRecord,enumerationValue,itemNames,itemShortenedNames,itemAbbreviatedNames,additionalContents,notes);
//
//        if (!itemNames.containsKey(SK_LOCALE))
//            throw new CommonException(HttpStatus.BAD_REQUEST, CHYBAJUCE_MENO_HODNOTY_CISELNIKA_PRE_ITEM_CODE + enumerationValue.getItemCode(), null);
//
//        fillEnumerationValuesMultivalues(event,enumerationValueMultivaluesSnapshotIndex,enumerationValue,itemNames,itemShortenedNames,itemAbbreviatedNames,additionalContents,notes);
//
//        if (oldEnumerationValueId == null) {
//            return notificationService.createNotification(notificationService.createEnumerationValueEventData(enumerationValue.getCodelistCode(), enumerationValue.getItemCode(), enumerationValue.getId(), oldEnumerationValueId, notificationService.createUrl(ENUMERATION_VALUE, new String[0])), event.getId(), CategoryT.UPDATE, DomainT.ENUMERATION_VALUE, userInfo.getLogin(), null);
//        }
//
//        return notificationService.createNotification(notificationService.createEnumerationValueEventData(enumerationValue.getCodelistCode(), enumerationValue.getItemCode(), enumerationValue.getId(), null, notificationService.createUrl(ENUMERATION_VALUE, new String[0])), event.getId(), CategoryT.CREATE, DomainT.ENUMERATION_VALUE, userInfo.getLogin(), null);
//    }
//
//    private void fillEnumerationValuesMultivalues(Event event, AtomicInteger enumerationValueMultivaluesSnapshotIndex, EnumerationValue enumerationValue, HashMap<String, List<String>> itemNames, HashMap<String, List<String>> itemShortenedNames, HashMap<String, List<String>> itemAbbreviatedNames, HashMap<String, List<String>> additionalContents, HashMap<String, List<String>> notes) {
//        List<EnumerationValuesMultivalues> enumerationValuesMultivaluesList = new ArrayList<>();
//        List<String> allLocalizedKeys = new ArrayList<>();
//        allLocalizedKeys.addAll(itemNames.keySet());
//        allLocalizedKeys.addAll(itemShortenedNames.keySet());
//        allLocalizedKeys.addAll(itemAbbreviatedNames.keySet());
//        allLocalizedKeys.addAll(additionalContents.keySet());
//        allLocalizedKeys.addAll(notes.keySet());
//        HashSet<String> processedKeys = new HashSet<>();
//
//        for (String key : allLocalizedKeys) {
//            if (processedKeys.contains(key))
//                continue;
//            processedKeys.add(key);
//
//            int itemNamesSize = itemNames.get(key) == null ? 0 : itemNames.get(key).size();
//            int itemShortenedNameSize = itemShortenedNames.get(key) == null ? 0 : itemShortenedNames.get(key).size();
//            int itemAbbreviatedNamesSize = itemAbbreviatedNames.get(key) == null ? 0 : itemAbbreviatedNames.get(key).size();
//            int additionalContentsSize = additionalContents.get(key) == null ? 0 : additionalContents.get(key).size();
//            int notesSize = notes.get(key) == null ? 0 : notes.get(key).size();
//
//            int[] sizes = { itemNamesSize, itemShortenedNameSize, itemAbbreviatedNamesSize, additionalContentsSize, notesSize };
//            int max = Arrays.stream(sizes).max().getAsInt();
//
//            for (int i = 0; i < max; i++) {
//                EnumerationValuesMultivalues enumerationValuesMultivalues = new EnumerationValuesMultivalues();
//                EnumerationValuesMultivaluesId enumerationValuesMutivaluesId = new EnumerationValuesMultivaluesId();
//                enumerationValuesMultivalues.setEnumerationValue(enumerationValue);
//                enumerationValuesMultivalues.setEnumerationValuesMultivaluesId(enumerationValuesMutivaluesId);
//                enumerationValuesMutivaluesId.setLocaleId(key);
//                enumerationValuesMutivaluesId.setMultivalueIndex(i);
//                enumerationValuesMultivaluesList.add(enumerationValuesMultivalues);
//
//                if (itemNames.containsKey(key) && itemNames.get(key).size() > i) {
//                    enumerationValuesMultivalues.setItemName(itemNames.get(key).get(i));
//                }
//
//                if (itemShortenedNames.containsKey(key) && itemShortenedNames.get(key).size() > i) {
//                    enumerationValuesMultivalues.setItemShortenedName(itemShortenedNames.get(key).get(i));
//                }
//
//                if (itemAbbreviatedNames.containsKey(key) && itemAbbreviatedNames.get(key).size() > i) {
//                    enumerationValuesMultivalues.setItemAbbreviatedName(itemAbbreviatedNames.get(key).get(i));
//                }
//
//                if (additionalContents.containsKey(key) && additionalContents.get(key).size() > i) {
//                    enumerationValuesMultivalues.setAdditionalContent(additionalContents.get(key).get(i));
//                }
//
//                if (notes.containsKey(key) && notes.get(key).size() > i) {
//                    enumerationValuesMultivalues.setNote(notes.get(key).get(i));
//                }
//            }
//        }
//
//        enumerationValuesMultivaluesList = enumerationValuesMultivaluesService.saveAll(enumerationValuesMultivaluesList);
//        for (EnumerationValuesMultivalues enumerationValuesMultivalues : enumerationValuesMultivaluesList) {
//            enumerationValueMultivaluesSnapshotService.createEnumerationValueMultivalueSnapshot(enumerationValuesMultivalues, event, enumerationValueMultivaluesSnapshotIndex.get());
//            enumerationValueMultivaluesSnapshotIndex.addAndGet(1);
//        }
//    }
//
//    @SuppressWarnings("static-method")
//    private String checkLocalizationString(String localId, String code) {
//        if (localId.length() != 2)
//            throw new CommonException(HttpStatus.BAD_REQUEST, NEPLATNY_LOKALIZACNY_UDAJ_DLHSI_AKO_2_ZNAKY_PRE_CODELIST_CODE_ITEM_CODE + code, null);
//        else
//            return localId.toLowerCase();
//    }
//
//    private int checkSKLocalization(List<?> recordsMultivalues, Method method, String code) {
//        int index = 0;
//        for (Object object : recordsMultivalues) {
//            try {
//
//                Class<?> c = Class.forName(object.getClass().getName());
//                Method m = c.getDeclaredMethod(method.getName());
//                String localId = checkLocalizationString(((LanguageType) m.invoke(object)).getLanguageId(), code);
//                if (localId.equals(SK_LOCALE))
//                    return index;
//
//                index += 1;
//
//            }catch (Exception ex) {
//                throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, CHYBA_PRI_SPRACOVANI_LOKALIZACNYCH_UDAJOV, ex);
//            }
//        }
//        return -1;
//    }
}
