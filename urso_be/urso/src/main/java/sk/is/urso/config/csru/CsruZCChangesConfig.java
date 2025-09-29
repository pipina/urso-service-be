package sk.is.urso.config.csru;

import lombok.extern.slf4j.Slf4j;
import org.alfa.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sk.is.urso.csru.zc.codelist.CodelistCType;
import sk.is.urso.csru.zc.codelist.CodelistItemCType;
import sk.is.urso.csru.zc.codelist.CodelistNameLocHisSCType;
import sk.is.urso.model.ciselniky.Ciselnik;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;
import sk.is.urso.model.csru.api.async.GetChangedReferenceDataServiceAsync.GetChangedReferenceDataResponseCType;
import sk.is.urso.model.csru.api.async.GetReferenceDataByIdentifiersServiceAsync.GetReferenceDataByIdentifiersResponseCType;
import sk.is.urso.model.csru.api.async.common.FileCType;
import sk.is.urso.model.csru.api.async.common.GetStatusResponseCType;
import sk.is.urso.repository.ciselniky.CiselnikRepository;
import sk.is.urso.repository.ciselniky.HodnotaCiselnikaRepository;
import sk.is.urso.service.csru.CsruZcService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
public class CsruZCChangesConfig {

    @Autowired
    private CsruZcService zcService;

    @Autowired
    private HodnotaCiselnikaRepository hodnotaCiselnikaRepository;

    @Autowired
    private CiselnikRepository ciselnikRepository;

    @Value("${integration.csru.basicEnumerations}")
    private List<String> basicEnumerations;

    @Value("${integration.csru.basicEnumerations.maxWaitTime}")
    private Integer maxWaitTime;

    private static final String LOG_PREFIX = "[ZC CHANGES]: ";

//    @Bean
    public void runAtStartCsruZCChangesConfig() throws Exception {
        Thread thread = new Thread(() -> {
            processZc();
        });
        thread.start();
    }

    public void processZc() {
        log.info(LOG_PREFIX + "[START]");

        List<String> neinicializovaneCiselniky = new ArrayList<>();
        List<Ciselnik> inicializovaneciselniky = new ArrayList<>();

        for (String kodCiselnika : basicEnumerations) {
            Ciselnik ciselnik = ciselnikRepository.findByKodCiselnikaAndDeletedIsFalse(kodCiselnika).orElse(null);
            if (ciselnik != null) {
                inicializovaneciselniky.add(ciselnik);
            } else {
                neinicializovaneCiselniky.add(kodCiselnika);
            }

//            try {
//                inicializovaneciselniky.add(ciselnik);
//            } catch (HttpClientErrorException e) {
//                if (e.getStatusCode().value() == 404) neinicializovaneCiselniky.add(kodCiselnika);
//                else throw e;
//            }
        }
        if (!inicializovaneciselniky.isEmpty())
            zcProcessChanges(inicializovaneciselniky);

        if (!neinicializovaneCiselniky.isEmpty())
            zcProcessInitial(neinicializovaneCiselniky, true);

        log.info(LOG_PREFIX + "[END]");
    }

    public void zcProcessInitial(List<String> enumerationList, boolean isNewEnumeration) {
        try {
            GetReferenceDataByIdentifiersResponseCType referenceDataByIdentifiers = zcService.getReferenceDataByIdentifiers(UUID.randomUUID().toString(), enumerationList);
            log.info(LOG_PREFIX + "[RESPONSE][REQUEST_ID]" + referenceDataByIdentifiers.getRequestId());
            log.info(LOG_PREFIX + "[RESPONSE][ERROR_MESSAGE]" + referenceDataByIdentifiers.getErrorMessage());
            log.info(LOG_PREFIX + "[RESPONSE][RESULT_CODE]" + referenceDataByIdentifiers.getResultCode());

            GetStatusResponseCType statusResponse = waitForResponse(referenceDataByIdentifiers.getRequestId());

            if (statusResponse != null) {
                for (FileCType file : statusResponse.getFileList().getFile()) {
                    CodelistCType codelist = XmlUtils.parse(zcService.getFile(file), CodelistCType.class);

                    if (isNewEnumeration) {
                        upsertEnumeration(codelist);
                        upsertEnumerationValues(codelist);
                    } else {
                        upsertEnumerationValues(codelist);
                        upsertEnumeration(codelist);
                    }
                }
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    public void zcProcessChanges(List<Ciselnik> inicializovaneCiselniky) {
        try {
            int positionIndex;
            List<String> inicializovaneKodyCiselnikov = new ArrayList<>();
            LinkedHashSet<String> ciselnikyNaSpracovanie = new LinkedHashSet<>();

//array LocalDateTime - z dovodu moznosti nastavenia hodnoty v enclosed scope
            LocalDateTime[] dateFrom = {LocalDate.parse("1990-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()};
            inicializovaneCiselniky.forEach(ciselnik -> {
//                if (ciselnik.getModifiedOn().isAfter(dateFrom[0]))
//                    dateFrom[0] = ciselnik.getModifiedOn();
                inicializovaneKodyCiselnikov.add(ciselnik.getKodCiselnika());
            });

            GetChangedReferenceDataResponseCType changedReferenceData = zcService.getChangedReferenceData(UUID.randomUUID().toString(), dateFrom[0].toLocalDate());
            GetStatusResponseCType statusResponse = waitForResponse(changedReferenceData.getRequestId());

            if (statusResponse != null) {
                for (FileCType file : statusResponse.getFileList().getFile()) {
                    Document doc = XmlUtils.parse(zcService.getFile(file));
                    NodeList changesInDay = doc.getElementsByTagName("ChangesInDay");

                    for (int i = 0; i < changesInDay.getLength(); i++) {
                        Element changeInDay = (Element) changesInDay.item(i);
                        Node dateOfChangeNode = changeInDay.getElementsByTagName("DateOfChange").item(0);

                        if (dateOfChangeNode != null) {
//                            Date dateOfChange = new SimpleDateFormat("yyyy-MM-dd").parse(dateOfChangeNode.getFirstChild().getNodeValue());
                            NodeList changes = changeInDay.getElementsByTagName("Changes");

                            for (int j = 0; j < changes.getLength(); j++) {
                                Element change = (Element) changes.item(j);
                                String codelistCode = change.getElementsByTagName("Entity_ID").item(0).getFirstChild().getNodeValue();
                                positionIndex = inicializovaneKodyCiselnikov.indexOf(codelistCode);
//
//                                if (positionIndex != -1 && inicializovaneCiselniky.get(positionIndex).getModifiedOn().isBefore(DateUtils.toLocalDateTime(dateOfChange))) {
//                                    ciselnikyNaSpracovanie.add(codelistCode);
//                                }
                                if (positionIndex != -1) {
                                    ciselnikyNaSpracovanie.add(codelistCode);
                                }
                            }
                        }
                    }
                }
                if (!ciselnikyNaSpracovanie.isEmpty()) {
                    ArrayList<String> codelistCodesToProcess = new ArrayList<>(ciselnikyNaSpracovanie);
                    zcProcessInitial(codelistCodesToProcess, false);
                }
            }
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    public GetStatusResponseCType waitForResponse(Long requestId) throws InterruptedException {
        GetStatusResponseCType statusResponse = new GetStatusResponseCType();

        int totalWaitTime = 0;

        boolean statusReceived = false;
        String transactionId = UUID.randomUUID().toString();

        while (!statusReceived) {

            if (totalWaitTime > maxWaitTime) {
                String error = "Chyba pri zaevidovaní zmien referenčných údajov. Dáta neboli pripravené v očakávanom čase.";

                log.error(LOG_PREFIX + ": " + error);

                break;
            }
            statusResponse = zcService.getStatus(transactionId, requestId);

            if (statusResponse.getResultCode() != 0) {
                String error = "Chyba pri zaevidovaní zmien referenčných údajov. Návratový kód WS: " + statusResponse.getResultCode() + ", Návratový oznam WS: " + statusResponse.getErrorMessage();

                log.error(LOG_PREFIX + ": " + error);

                break;
            } else {
                if (statusResponse.getStatus() == 0) {
                    Thread.sleep(1000);
                    totalWaitTime += 1000;
                } else if (statusResponse.getStatus() == 1 || statusResponse.getStatus() == 4) {
                    statusReceived = true;
                } else if (statusResponse.getStatus() == 2) {
                    String error = "Chyba pri zaevidovaní zmien referenčných údajov. Návratový kód WS: " + statusResponse.getStatus() + ", Návratový oznam WS: " + statusResponse.getRequestProcessingErrorMessage();

                    log.error(LOG_PREFIX + ": " + error);

                    break;
                }
            }
        }

        if (statusReceived) {
            return statusResponse;
        }
        return null;
    }

    private void upsertEnumeration(CodelistCType codelist) {
        Ciselnik ciselnik = ciselnikRepository.findByKodCiselnikaAndDeletedIsFalse(codelist.getCodelistCode()).orElse(new Ciselnik());
//        if (ciselnik != null) {
//            ciselnik.setKodCiselnika(codelist.getCodelistCode());
//            ciselnik.setPlatnostOd(LocalDate.now());
//            codelist.getCodelistName().forEach(name -> {
//                if (name.getLanguage().equalsIgnoreCase("sk")) {
//                    ciselnik.setNazovCiselnika(name.getValue());
//                }
//            });
//            ciselnikRepository.save(ciselnik);
//        } else {
//            // TODO exception
//        }
        ciselnik.setKodCiselnika(codelist.getCodelistCode());
        ciselnik.setPlatnostOd(LocalDate.now());
        codelist.getCodelistName().forEach(name -> {
            if (name.getLanguage().equalsIgnoreCase("sk")) {
                ciselnik.setNazovCiselnika(name.getValue());
            }
        });
        ciselnikRepository.save(ciselnik);
    }

    private void upsertEnumerationValues(CodelistCType codelist) {
        for (CodelistItemCType codelistItemCType : codelist.getCodelistItem()) {
            HodnotaCiselnika hodnotaCiselnika = hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnikaAndDeletedIsFalse(
                    codelistItemCType.getItemCode(), codelist.getCodelistCode()).orElse(new HodnotaCiselnika());
            hodnotaCiselnika.setKodCiselnika(codelist.getCodelistCode());
            hodnotaCiselnika.setCiselnik(ciselnikRepository.findByKodCiselnika(codelist.getCodelistCode()));
            hodnotaCiselnika.setKodPolozky(codelistItemCType.getItemCode());
            hodnotaCiselnika.setPlatnostOd(LocalDate.now());
            codelistItemCType.getItemName().forEach(name -> {
                if (name.getLanguage().equalsIgnoreCase("sk")) {
                    hodnotaCiselnika.setNazovPolozky(name.getValue());
                }
            });
            if (codelistItemCType.getAdditionalContent() != null)
                codelistItemCType.getAdditionalContent().forEach(addContent -> {
                    if (addContent.getValue().length() == 2) hodnotaCiselnika.setDodatocnyObsah(addContent.getValue());
                });
            hodnotaCiselnikaRepository.save(hodnotaCiselnika);
        }
    }
}
