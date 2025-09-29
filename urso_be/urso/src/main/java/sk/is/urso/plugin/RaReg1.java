package sk.is.urso.plugin;

import lombok.Getter;
import lombok.NonNull;
import org.alfa.exception.IException;
import org.alfa.model.ListRequestModel;
import org.alfa.service.UserInfoService;
import org.alfa.utils.DateUtils;
import org.alfa.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.model.*;
import sk.is.urso.plugin.entity.*;
import sk.is.urso.plugin.repository.*;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class RaReg1 extends AbstractRegPlugin implements IException {
	public static final RegisterId registerId = new RegisterId("RA", 1);
    @Autowired
    RaReg1DataRepository dataRepo;
    @Autowired
    RaReg1IndexRepository indexRepo;
    @Autowired
    RaReg1DataReferenceRepository dataReferenceRepo;
    @Autowired
    RaReg1DataHistoryRepository dataHistoryRepo;
    @Autowired
    RaReg1NaturalIdRepository naturalIdRepository;
    @Autowired
    RaInternalReg1 raInternalReg1;
    @Autowired
    UserInfoService userInfoService;

    public RaReg1(RegisterPlugin info, RegisterPluginConfig plugin) {
        super(info, plugin, RaReg1DataEntity.class, RaReg1IndexEntity.class, RaReg1DataReferenceEntity.class, RaReg1DataHistoryEntity.class, RaReg1NaturalIdEntity.class);
    }

    @Override
    public RaReg1DataRepository getDataRepository() {
        return this.dataRepo;
    }

    @Override
    public RaReg1IndexRepository getIndexRepository() {
        return this.indexRepo;
    }

    @Override
    public RaReg1DataReferenceRepository getDataReferenceRepository() {
        return this.dataReferenceRepo;
    }

    @Override
    public RaReg1DataHistoryRepository getDataHistoryRepository() {
        return this.dataHistoryRepo;
    }

    @Override
    public RaReg1NaturalIdRepository getNaturalIdRepository() {
        return this.naturalIdRepository;
    }

    @Override
    public ZaznamRegistraList findRegisterEntries(@NonNull ZaznamRegistraListRequestFilter filter, @NonNull ListRequestModel listRequest) {
        ZaznamRegistraList zaznamRegistraList = raInternalReg1.findRegisterEntries(filter, listRequest);

        return joinLevels(zaznamRegistraList.getResult());
    }

    private ZaznamRegistraList joinLevels(@NotNull @Valid List<ZaznamRegistra> response) {
        ZaznamRegistraList zaznamRegistraList = new ZaznamRegistraList();
        zaznamRegistraList.setResult(new ArrayList<>());

        HashMap<Long, List<DvojicaKlucHodnotaSHistoriou>> hashMap = new HashMap<>();
        List<Long> highestLevels = new ArrayList<>();
        for (ZaznamRegistra zaznamRegistra : response) {
            hashMap.put(zaznamRegistra.getZaznamId(), zaznamRegistra.getPolia());
            for (DvojicaKlucHodnotaSHistoriou keyValue : zaznamRegistra.getPolia()) {
                if (keyValue.getKluc().equals(RaInternalReg1.RA_INTERNAL_LEVELS.get(0))) {
                    highestLevels.add(zaznamRegistra.getZaznamId());
                    break;
                }
            }
        }

        for (Long entryId : highestLevels) {
            ZaznamRegistra zaznamRegistra = new ZaznamRegistra();

            zaznamRegistra.setZaznamId(entryId);
            zaznamRegistra.setPolia(fillIndexList(entryId, hashMap));

            zaznamRegistraList.getResult().add(zaznamRegistra);
        }
        zaznamRegistraList.setTotal((long) highestLevels.size());
        return zaznamRegistraList;
    }

    private List<DvojicaKlucHodnotaSHistoriou> fillIndexList(Long entryId, HashMap<Long, List<DvojicaKlucHodnotaSHistoriou>> hashMap) {
        List<DvojicaKlucHodnotaSHistoriou> registerEntryFieldsList = new ArrayList<>();

        Long nextLevel = entryId;
        for (int i = 0; i < RaInternalReg1.RA_INTERNAL_LEVELS.size(); i++) {
            for (DvojicaKlucHodnotaSHistoriou abstractRegEntityIndex : hashMap.get(nextLevel)) {
                if (abstractRegEntityIndex.getKluc().equals(RaInternalReg1.STREET_NAME_ID) || abstractRegEntityIndex.getKluc().equals(RaInternalReg1.CITY_ID)) {
                    registerEntryFieldsList.add(hashMap.get(Long.parseLong(abstractRegEntityIndex.getHodnota())).get(0));
                }

                if (RaInternalReg1.RA_INTERNAL_LEVELS.get(i).equals(abstractRegEntityIndex.getKluc())) {
                    registerEntryFieldsList.add(abstractRegEntityIndex);
                } else {
                    if (RaInternalReg1.RA_LEVELS.get(i + 1).concat("Id").equals(abstractRegEntityIndex.getKluc())) {
                        nextLevel = Long.parseLong(abstractRegEntityIndex.getHodnota());
                    }
                }
            }
        }
        return registerEntryFieldsList;
    }


    @Override
    public <U> U findEntryByEntryId(Long entryId, Class<U> type) {
        ZaznamRegistraList data = raInternalReg1.getComposedAddress(entryId);
        try {
            String xml = generateXmlFromIndexes(data.getResult(), entryId);

            RaReg1DataEntity raReg1DataEntity = new RaReg1DataEntity();
            raReg1DataEntity.setXml(xml);
            raReg1DataEntity.setId(entryId);
            raReg1DataEntity.setPlatnostOd(DateUtils.toDate(data.getResult().get(0).getPlatnostOd()));
            raReg1DataEntity.setUcinnostOd(DateUtils.toDate(data.getResult().get(0).getUcinnostOd()));
            raReg1DataEntity.setUcinnostDo(DateUtils.toDate(data.getResult().get(0).getUcinnostDo()));
            raReg1DataEntity.setModul(null);
            raReg1DataEntity.setPouzivatel(null);
            raReg1DataEntity.setPovodneId(null);

            return (U) raReg1DataEntity;
        } catch (Exception e) {
            throw toException("Chyba pri generovaní XML.", e);
        }
    }

    private String generateXmlFromIndexes(@NotNull @Valid List<ZaznamRegistra> data, Long entryId) throws ParserConfigurationException {
        ZaznamRegistraList zaznamRegistraList = new ZaznamRegistraList();
        zaznamRegistraList.setResult(new ArrayList<>());

        HashMap<Long, List<DvojicaKlucHodnotaSHistoriou>> hashMap = new HashMap<>();
        for (ZaznamRegistra zaznamRegistra : data) {
            hashMap.put(zaznamRegistra.getZaznamId(), zaznamRegistra.getPolia());
        }

        return generateXml(fillIndexList(entryId,hashMap));
    }

    private String generateXml(List<DvojicaKlucHodnotaSHistoriou> registerEntriesList) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Address");

        for (int i = registerEntriesList.size() - 1; i >= 0; i--) {
            Element newElement = doc.createElement(registerEntriesList.get(i).getKluc());
            newElement.appendChild(doc.createTextNode(registerEntriesList.get(i).getHodnota()));
            rootElement.appendChild(newElement);
        }
        doc.appendChild(rootElement);

        return XmlUtils.xmlToString(doc);
    }

    @Override
    public Long getNextSequence() {
        throw new AssertionError("Implemented through " + raInternalReg1.getFullInternalRegisterId() + " so inserting data here is not allowed!");
    }
}
