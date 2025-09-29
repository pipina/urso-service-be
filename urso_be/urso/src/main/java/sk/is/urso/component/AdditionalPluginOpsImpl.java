package sk.is.urso.component;

import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.alfa.model.UserInfo;
import org.alfa.service.UserInfoService;
import org.alfa.utils.DateUtils;
import org.alfa.utils.SearchUtils;
import org.alfa.utils.XmlUtils;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityDataReference;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.AdditionalPluginOps;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.reg.model.ZaznamRegistraReferencia;
import sk.is.urso.config.RegisterConfig;
import sk.is.urso.controller.ZaznamRegistraController;
import sk.is.urso.model.Udalost;
import sk.is.urso.plugin.entity.SubjectReg1DataEntity;
import sk.is.urso.rest.model.UdalostDomenaEnum;
import sk.is.urso.rest.model.UdalostKategoriaEnum;
import sk.is.urso.service.RaInternalService;
import sk.is.urso.service.RegisterService;
import sk.is.urso.service.UdalostService;
import sk.is.urso.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.xml.validation.Schema;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class AdditionalPluginOpsImpl implements AdditionalPluginOps, Iterable<AbstractRegPlugin>, IException {

    @Autowired
    private UdalostService udalostService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private RaInternalService raInternalService;

    @Autowired
    private UserInfoService userInfoService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${registers-file-path}")
    private String registersFilePath;

    @Override
    public AbstractRegPlugin getPlugin(String registerId, Integer verziaRegistraId) throws CommonException {
        return getPlugin(new RegisterId(registerId, verziaRegistraId));
    }

    @Override
    public List<AbstractRegPlugin> getRegisterPlugins() {
        return new ArrayList<>(RegisterConfig.registerPlugins.values());
    }

    private AbstractRegPlugin getPlugin(RegisterId reg) {
        AbstractRegPlugin plugin = RegisterConfig.registerPlugins.get(reg);
        if (plugin == null) {
            throw new CommonException(HttpStatus.NOT_FOUND, "Register s id = " + reg.getRegisterId() + ", verzia = " + reg.getVerziaRegistraId() + " neexistuje", null);
        }
        return plugin;
    }

    @Override
    public Iterator<AbstractRegPlugin> iterator() {
        return RegisterConfig.registerPlugins.values().iterator();
    }

    @Override
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPost(ZaznamRegistraInputDetail zaznamRegistraInputDetail, UserInfo userInfo) {

        Udalost udalost = null;
        RegisterId register;
        AbstractRegEntityData data;

        try {

            register = new RegisterId(zaznamRegistraInputDetail.getRegisterId(), zaznamRegistraInputDetail.getVerziaRegistraId());
            final AbstractRegPlugin plugin = getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }

            Date today = new Date();
            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.CREATE, userInfo.getLogin());

            Schema schema = XmlUtils.loadXsdSchema(new File(registersFilePath + File.separator + plugin.getPrimaryXsdFile().getPath()));
            //validacia vstupnych dat - uz vstupne data musia byt dobre validovane inak chyby ktore vraciame budu matuce
            registerService.validateData(zaznamRegistraInputDetail.getData(), plugin, schema);


            Document dataDocument;
            try {
                dataDocument = XmlUtils.parse(zaznamRegistraInputDetail.getData());
            } catch (Exception ex) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "Chyba pri spracovaní XML dát", ex);
            }
            registerService.checkReferenceRegisterValues(plugin, dataDocument);
            registerService.checkEnumerationFieldsValues(plugin, dataDocument);
            plugin.validateData(dataDocument);

            plugin.prepareXmlForUpdate(null, dataDocument);
            data = plugin.createNewDataEntityForInsert(dataDocument);
            dataDocument = plugin.prepareXmlForInsert(dataDocument, data);

            if (!plugin.isUniqueByIdInIndexTable(dataDocument)) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "ID nie je unikátne");
            }

            //registerService.checkReferenceRegisterValues(plugin, dataDocument); robi sa skor a dvakrat to netreba?
            zaznamRegistraInputDetail.setData(XmlUtils.xmlToString(dataDocument));
            data.setXml(zaznamRegistraInputDetail.getData());

            registerService.validateData(zaznamRegistraInputDetail.getData(), plugin, schema);

            if (plugin.isExternal() && !userInfo.isAdministrator()) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "Pridanie záznamu do externého registra nie je možné", null);
            }

            if (zaznamRegistraInputDetail.getPlatnostOd() == null) {
                zaznamRegistraInputDetail.setPlatnostOd(LocalDate.now());
            }

            registerService.updateNewDataEntity(data, zaznamRegistraInputDetail, today);
            // plugin.saveDataEntity(data);
            //var entryIdIndex = plugin.createEntryIdIndex(data);
            //plugin.saveIndexEntity(entryIdIndex);


            registerService.createNewDataHistoryEntity(plugin.createNewDataHistoryEntity(), data, udalost);
            //plugin.saveDataHistoryEntity(regEntryDataHistory);

            registerService.createRegisterIndexes(plugin, data, dataDocument);
            plugin.saveDataEntity(data);

            ZaznamRegistraOutputDetail zaznamRegistraOutputDetail = registerService.prepareZaznamRegistraOutputDetail(data, plugin);

            if (register.getRegisterId().equals("RA_INTERNAL")) {
                raInternalService.increaseCount();
            }

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(zaznamRegistraOutputDetail, HttpStatus.OK);

        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException(ZaznamRegistraController.CHYBA_PRI_VKLADANI_REGISTRA, ex);
        }
    }

//    @Transactional
//    @Override
//    public ResponseEntity<ZaznamRegistraOutputDetail> registerEntryPost(ZaznamRegistraInputDetail registerEntryUpdateDetail, UserInfo userInfo, Boolean actualUpdate) {
//
//        Udalost event = null;
//        RegisterId register = null;
//        AbstractRegEntityData data = null;
//
//        try {
//
//            event = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.UPDATE, userInfo.getLogin());
//
//            if (registerEntryUpdateDetail.getRegisterId() == null || registerEntryUpdateDetail.getRegisterId().isEmpty())
//                throw new CommonException(HttpStatus.BAD_REQUEST, "Chýba ID registra", "registerId", null);
//
//            if (registerEntryUpdateDetail.getVerziaRegistraId() == null)
//                throw new CommonException(HttpStatus.BAD_REQUEST, "Chýba verzia registra", "registerVersionId", null);
//
//            if (registerEntryUpdateDetail.getZaznamId() == null)
//                throw new CommonException(HttpStatus.BAD_REQUEST, "Nebolo zadané entryId pre register", "entryId", null);
//
//            register = new RegisterId(registerEntryUpdateDetail.getRegisterId(), registerEntryUpdateDetail.getVerziaRegistraId());
//            final String subject = register.toString();
//            final AbstractRegPlugin plugin = getPlugin(register);
//            if (plugin.getInfo().getPublicRegisterId() != null) {
//                register.setRegisterId(plugin.getInfo().getRegisterId());
//            }
////            authorizationService.checkIsAuthorized(subject, AuthorizationDomainEnum.REGISTER, AuthorizationOperationEnum.REGISTER_EDIT, plugin.getInfo().getName(), userInfo);
//
//            if (plugin.isExternal() && !userInfo.isAdministrator()) {
//                throw new CommonException(HttpStatus.BAD_REQUEST, "Aktualizácia záznamu externého registra nie je možné", null);
//            }
//
//            Session session = entityManager.unwrap(Session.class);
//            data = session.get(plugin.getDataEntityClass(), registerEntryUpdateDetail.getZaznamId(), LockMode.PESSIMISTIC_WRITE);
//            if (data == null) {
//                throw new CommonException(HttpStatus.BAD_REQUEST, "Data s príslušným ID neexistujú", null);
//            }
//
//
//            if (registerEntryUpdateDetail.getPlatny() != null)
//                data.setNeplatny(!registerEntryUpdateDetail.getPlatny());
//
//            if (registerEntryUpdateDetail.getUcinnostOd() != null)
//                data.setUcinnostOd(DateUtils.toDate(registerEntryUpdateDetail.getUcinnostOd()));
//
//            if (registerEntryUpdateDetail.getUcinnostDo() != null)
//                data.setUcinnostDo(DateUtils.toDate(registerEntryUpdateDetail.getUcinnostDo()));
//
//            if (registerEntryUpdateDetail.getPlatnostOd() != null)
//                data.setPlatnostOd(DateUtils.toDate(registerEntryUpdateDetail.getPlatnostOd()));
//
//            if (registerEntryUpdateDetail.getModul() != null)
//                data.setModul(registerEntryUpdateDetail.getModul());
//
//            if (registerEntryUpdateDetail.getPouzivatel() != null)
//                data.setPouzivatel(registerEntryUpdateDetail.getPouzivatel());
//            else
//                data.setPouzivatel(userInfo.getLogin());
//
//            if (registerEntryUpdateDetail.getUcinnostOd() == null)
//                registerEntryUpdateDetail.setUcinnostOd(data.getUcinnostOd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//
//            //plugin.findAllIndexEntityByEntryId(registerEntryUpdateDetail.getEntryId()).forEach(element -> plugin.getIndexRepository().deleteById(element.getId()));
//
//            Document dataDocument = XmlUtils.parse(data.getXml());
//
//            Schema schema = XmlUtils.loadXsdSchema(new File(registersFilePath + File.separator + plugin.getPrimaryXsdFile().getPath()));
//            //validacia vstupnych dat - uz vstupne data musia byt dobre validovane inak chyby ktore vraciame budu matuce
//            registerService.validateData(registerEntryUpdateDetail.getData(), plugin, schema);
//
//            Document newDataDocument = XmlUtils.parse(registerEntryUpdateDetail.getData());
//
//            String xmlData;
//            if (actualUpdate) {
//                plugin.actualUpdate(newDataDocument, dataDocument);
//
//                registerService.checkReferenceRegisterValues(plugin, dataDocument);
//                plugin.getIndexRepository().deleteByEntryId(registerEntryUpdateDetail.getEntryId());
//                registerService.checkHistoricalFieldsValues(plugin, dataDocument);
//                registerService.updateEnumerationFieldsValues(plugin, dataDocument);
//                plugin.validateData(dataDocument);
//
//                xmlData = XmlUtils.xmlToString(dataDocument);
//
//                registerService.validateData(xmlData, plugin, schema);
//
//                registerService.checkAuthorization(data, plugin, userInfo);
//                registerService.createRegisterIndexes(plugin, data, dataDocument);
//            } else {
//                registerService.checkReferenceRegisterValues(plugin, newDataDocument);
//                plugin.getIndexRepository().deleteByEntryId(registerEntryUpdateDetail.getEntryId());
//                registerService.checkHistoricalFieldsValues(plugin, newDataDocument);
//                plugin.validateData(newDataDocument);
//
//                Document docWithEnumerationFields = plugin.prepareXmlForUpdate(dataDocument, newDataDocument);
//                //aktualizacia ciselnikovych hodnot - je nutne ju vykonat az po prepareXmlForUpdate, kde sa kontroluje ci sa nezmenili historicke hodnoty
//                registerService.updateEnumerationFieldsValues(plugin, docWithEnumerationFields);
//                docWithEnumerationFields = plugin.updateXml(docWithEnumerationFields);
//                xmlData = XmlUtils.xmlToString(docWithEnumerationFields);
//
//                registerService.validateData(xmlData, plugin, schema);
//
//                registerService.checkAuthorization(data, plugin, userInfo);
//                registerService.createRegisterIndexes(plugin, data, docWithEnumerationFields);
//            }
//
//            data.setXml(xmlData);
//
//            AbstractRegEntityDataHistory regEntryDataHistory = registerService.createNewDataHistoryEntity(plugin.createNewDataHistoryEntity(), data, event);
//            plugin.updateDataEntity(data);
//
//            registerValueEventService.createRegisterValueEvent(data, register, event);
//            RegisterEntryOutputDetail registerEntryOutputDetail = registerService.prepareRegisterEntryOutputDetail(data, plugin);
//
//            if (plugin.getPluginConfig().getGdprParams().isIsGdprRelevant()) {
//                AbstractRegEntityData abstractRegEntityData = plugin.createNewDataEntity();
//                abstractRegEntityData.setId(Utils.objectToLong(data.getId()));
//                if (plugin.getDataReferenceRepository().existsByEntryIdAndReferenceCountGreaterThan(abstractRegEntityData, 0)) {
//                    sendNotifications(plugin, regEntryDataHistory.getEntryId().getId(), null, null, null, null, notificationService.createUrl(RegisterEntryController.REGISTER_ENTRY), data.getXml(), event.getId(), CategoryT.UPDATE, DomainT.REGISTER_VALUE, userInfo.getLogin(), registerEntryUpdateDetail.getUser(), registerEntryUpdateDetail.getModule(), DateUtils.toLocalDate(data.getValidFrom()), DateUtils.toLocalDate(data.getEffectiveFrom()), DateUtils.toLocalDate(data.getEffectiveTo()), data.getNaturalId(), registerEntryUpdateDetail.getCorrelationId());
//                }
//            } else {
//                sendNotifications(plugin, regEntryDataHistory.getEntryId().getId(), null, null, null, null, notificationService.createUrl(RegisterEntryController.REGISTER_ENTRY), data.getXml(), event.getId(), CategoryT.UPDATE, DomainT.REGISTER_VALUE, userInfo.getLogin(), registerEntryUpdateDetail.getUser(), registerEntryUpdateDetail.getModule(), DateUtils.toLocalDate(data.getValidFrom()), DateUtils.toLocalDate(data.getEffectiveFrom()), DateUtils.toLocalDate(data.getEffectiveTo()), data.getNaturalId(), registerEntryUpdateDetail.getCorrelationId());
//            }
//
//            if (register.getRegisterId().equals("RA_INTERNAL")) {
//                raInternalService.increaseCount();
//            }
//
//            eventService.updateEvent(event, null);
//            return new ResponseEntity<>(registerEntryOutputDetail, HttpStatus.OK);
//        } catch (Exception ex) {
//            try {
//                registerValueEventService.createRegisterValueEvent(data, register, event);
//                eventService.updateEvent(event, ex.getMessage());
//            } catch (Exception suppresedException) {
//                ex.addSuppressed(suppresedException);
//            }
//            throw toException(RegisterEntryController.CHYBA_PRI_AKTUALIZOVANI_REGISTRA, ex);
//        }
//    }

    @Transactional
    @Override
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPut(ZaznamRegistraInputDetail zaznamRegistraInputDetail, UserInfo userInfo) {

        Udalost udalost = null;
        RegisterId register;
        AbstractRegEntityData data;

        try {

            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.UPDATE, userInfo.getLogin());

            if (zaznamRegistraInputDetail.getRegisterId() == null || zaznamRegistraInputDetail.getRegisterId().isEmpty())
                throw new CommonException(HttpStatus.BAD_REQUEST, "Chýba ID registra", "registerId", null);

            if (zaznamRegistraInputDetail.getVerziaRegistraId() == null)
                throw new CommonException(HttpStatus.BAD_REQUEST, "Chýba verzia registra", "registerVersionId", null);

            if (zaznamRegistraInputDetail.getZaznamId() == null)
                throw new CommonException(HttpStatus.BAD_REQUEST, "Nebolo zadané entryId pre register", "entryId", null);

            register = new RegisterId(zaznamRegistraInputDetail.getRegisterId(), zaznamRegistraInputDetail.getVerziaRegistraId());
            final AbstractRegPlugin plugin = getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }

            if (plugin.isExternal() && !userInfo.isAdministrator()) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "Aktualizácia záznamu externého registra nie je možné", null);
            }

            Session session = entityManager.unwrap(Session.class);
            data = session.get(plugin.getDataEntityClass(), zaznamRegistraInputDetail.getZaznamId(), LockMode.PESSIMISTIC_WRITE);
            if (data == null) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "Data s príslušným ID neexistujú", null);
            }

            if (zaznamRegistraInputDetail.getPlatny() != null)
                data.setNeplatny(!zaznamRegistraInputDetail.getPlatny());

            if (zaznamRegistraInputDetail.getUcinnostOd() != null)
                data.setUcinnostOd(DateUtils.toDate(zaznamRegistraInputDetail.getUcinnostOd()));

            if (zaznamRegistraInputDetail.getUcinnostDo() != null)
                data.setUcinnostDo(DateUtils.toDate(zaznamRegistraInputDetail.getUcinnostDo()));

            if (zaznamRegistraInputDetail.getPlatnostOd() != null)
                data.setPlatnostOd(DateUtils.toDate(zaznamRegistraInputDetail.getPlatnostOd()));

            if (zaznamRegistraInputDetail.getModul() != null)
                data.setModul(zaznamRegistraInputDetail.getModul());

            if (zaznamRegistraInputDetail.getPouzivatel() != null)
                data.setPouzivatel(zaznamRegistraInputDetail.getPouzivatel());
            else
                data.setPouzivatel(userInfo.getLogin());

            if (zaznamRegistraInputDetail.getUcinnostOd() == null)
                zaznamRegistraInputDetail.setUcinnostOd(data.getUcinnostOd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            //plugin.findAllIndexEntityByEntryId(registerEntryUpdateDetail.getZaznamId()).forEach(element -> plugin.getIndexRepository().deleteById(element.getId()));
            plugin.getIndexRepository().deleteByZaznamId(zaznamRegistraInputDetail.getZaznamId());

            Document dataDocument = XmlUtils.parse(data.getXml());

            Schema schema = XmlUtils.loadXsdSchema(new File(registersFilePath + File.separator + plugin.getPrimaryXsdFile().getPath()));
            //validacia vstupnych dat - uz vstupne data musia byt dobre validovane inak chyby ktore vraciame budu matuce
            registerService.validateData(zaznamRegistraInputDetail.getData(), plugin, schema);

            Document newDataDocument = XmlUtils.parse(zaznamRegistraInputDetail.getData());
            registerService.checkReferenceRegisterValues(plugin, newDataDocument);
            registerService.checkEnumerationFieldsValues(plugin, newDataDocument);
            plugin.validateData(newDataDocument);

            Document docWithEnumerationFields = plugin.prepareXmlForUpdate(dataDocument, newDataDocument);
            //docWithEnumerationFields = registerService.checkEnumerationFieldsValues(plugin, docWithEnumerationFields);
            String xmlData = XmlUtils.xmlToString(docWithEnumerationFields);

            registerService.validateData(xmlData, plugin, schema);
            registerService.createRegisterIndexes(plugin, data, docWithEnumerationFields);

            data.setXml(xmlData);

            registerService.createNewDataHistoryEntity(plugin.createNewDataHistoryEntity(), data, udalost);
            plugin.updateDataEntity(data);

            ZaznamRegistraOutputDetail zaznamRegistraOutputDetail = registerService.prepareZaznamRegistraOutputDetail(data, plugin);

            if (register.getRegisterId().equals("RA_INTERNAL")) {
                raInternalService.increaseCount();
            }

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(zaznamRegistraOutputDetail, HttpStatus.OK);

        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException(ZaznamRegistraController.CHYBA_PRI_AKTUALIZOVANI_REGISTRA, ex);
        }
    }

    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(String registerId, Integer verziaRegistraId, Long zaznamId, String modulId) {
        Udalost udalost = null;
        RegisterId register = null;
        AbstractRegEntityData data = null;

        try {

            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.REFERENCE_INCREMENT);
            register = new RegisterId(registerId, verziaRegistraId);
            final String subject = register.toString();
            final AbstractRegPlugin plugin = getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }
//            authorizationService.checkIsAuthorized(subject, AuthorizationDomainEnum.REGISTER, AuthorizationOperationEnum.REGISTER_EDIT, plugin.getInfo().getName(), userInfo); TODO

            //Session session = entityManager.unwrap(Session.class);
            //data = session.get(plugin.getDataEntityClass(), entryId, LockMode.PESSIMISTIC_WRITE);
            data = plugin.findEntryByEntryId(zaznamId, AbstractRegEntityData.class);

            if (data == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje záznam '" + zaznamId + "' pre register '" + plugin.getInfo().getName() + "' (" + subject + ")", null);
            }
            if (data instanceof SubjectReg1DataEntity) {
                data.setPovodneId(SearchUtils.sanitizeValue(((SubjectReg1DataEntity) data).getSubjektId()));
            }
            entityManager.lock(data, LockModeType.PESSIMISTIC_WRITE);

            data.setDatumCasPoslednejReferencie(null);
            plugin.updateDataEntity(data);

            AbstractRegEntityDataReference dataReference = plugin.incrementReference(zaznamId, modulId, subject, data);
            if (dataReference.getPocetReferencii() == 1) {
//                plugin.onFirstRefInsert(data, objectWithCorrelationId != null ? objectWithCorrelationId.getCorrelationId() : null, userInfo); TODO
                plugin.onFirstRefInsert(data);
            }

//            registerValueEventService.createRegisterValueEvent(data, register, udalost); TODO
            ZaznamRegistraReferencia registerEntryReference = new ZaznamRegistraReferencia().registerId(registerId).verziaRegistraId(verziaRegistraId).zaznamId(zaznamId).modul(modulId).pocetReferencii(dataReference.getPocetReferencii());

//            if (plugin.getPluginConfig().getGdprParams().isIsGdprRelevant() && dataReference.getPocetReferencii() == 1) {
//                sendNotifications(plugin, entryId, null, null, null, null, notificationService.createUrl(RegisterEntryController.REGISTER_ENTRY), data.getXml(), udalost.getId(), CategoryT.CREATE, DomainT.REGISTER_VALUE, userInfo.getLogin(), DateUtils.toLocalDate(data.getValidFrom()), DateUtils.toLocalDate(data.getEffectiveFrom()), DateUtils.toLocalDate(data.getEffectiveTo()), data.getNaturalId(), objectWithCorrelationId != null ? objectWithCorrelationId.getCorrelationId() : null); TODO
//            }
            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(registerEntryReference, HttpStatus.OK);

        } catch (Exception ex) {
            try {
//                registerValueEventService.createRegisterValueEvent(data, register, udalost); TODO
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException("Chyba pri inkrementovaní počtu referencií pre daný modul", ex);
        }
    }

    //    @Override
//    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(String registerId, Integer verziaRegistraId, Long  zaznamId, String moduleId) {
////    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(String registerId, Integer verziaRegistraId, Long  zaznamId, String moduleId, sk.uvzsr.is.reg.model.ObjectWithCorrelationId objectWithCorrelationId, UserInfo userInfo) { TODO
//
//        Udalost udalost = null;
//        RegisterId register = null;
//        AbstractRegEntityData data = null;
//
//        try {
//
//            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.REFERENCE_INCREMENT);
//            register = new RegisterId(registerId, verziaRegistraId);
//            final String subject = register.toString();
//            final AbstractRegPlugin plugin = getPlugin(register);
//            if (plugin.getInfo().getPublicRegisterId() != null) {
//                register.setRegisterId(plugin.getInfo().getRegisterId());
//            }
////            authorizationService.checkIsAuthorized(subject, AuthorizationDomainEnum.REGISTER, AuthorizationOperationEnum.REGISTER_EDIT, plugin.getInfo().getName(), userInfo); TODO
//
//            //Session session = entityManager.unwrap(Session.class);
//            //data = session.get(plugin.getDataEntityClass(), entryId, LockMode.PESSIMISTIC_WRITE);
//            data = plugin.findEntryByEntryId(zaznamId, AbstractRegEntityData.class);
//
//            if (data == null) {
//                throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje záznam '" + zaznamId + "' pre register '" + plugin.getInfo().getName() + "' (" + subject + ")", null);
//            }
//            if (data instanceof SubjectReg1DataEntity) {
//                data.setPovodneId(SearchUtils.sanitizeValue(((SubjectReg1DataEntity) data).getSubjektId()));
//            }
//            entityManager.lock(data, LockModeType.PESSIMISTIC_WRITE);
//
//            data.setDatumCasPoslednejReferencie(null);
//            plugin.updateDataEntity(data);
//
//            AbstractRegEntityDataReference dataReference = plugin.incrementReference(zaznamId, moduleId, subject, data);
//            if (dataReference.getPocetReferencii() == 1) {
////                plugin.onFirstRefInsert(data, objectWithCorrelationId != null ? objectWithCorrelationId.getCorrelationId() : null, userInfo); TODO
//                plugin.onFirstRefInsert(data);
//            }
//
////            registerValueEventService.createRegisterValueEvent(data, register, udalost); TODO
//            ZaznamRegistraReferencia registerEntryReference = new ZaznamRegistraReferencia().registerId(registerId).verziaRegistraId(verziaRegistraId).zaznamId(zaznamId).modul(moduleId).pocetReferencii(dataReference.getPocetReferencii());
//
////            if (plugin.getPluginConfig().getGdprParams().isIsGdprRelevant() && dataReference.getPocetReferencii() == 1) {
////                sendNotifications(plugin, entryId, null, null, null, null, notificationService.createUrl(RegisterEntryController.REGISTER_ENTRY), data.getXml(), udalost.getId(), CategoryT.CREATE, DomainT.REGISTER_VALUE, userInfo.getLogin(), DateUtils.toLocalDate(data.getValidFrom()), DateUtils.toLocalDate(data.getEffectiveFrom()), DateUtils.toLocalDate(data.getEffectiveTo()), data.getNaturalId(), objectWithCorrelationId != null ? objectWithCorrelationId.getCorrelationId() : null); TODO
////            }
//            udalostService.updateEvent(udalost, null);
//            return new ResponseEntity<>(registerEntryReference, HttpStatus.OK);
//
//        } catch (Exception ex) {
//            try {
////                registerValueEventService.createRegisterValueEvent(data, register, udalost); TODO
//                udalostService.updateEvent(udalost, ex.getMessage());
//            } catch (Exception suppresedException) {
//                ex.addSuppressed(suppresedException);
//            }
//            throw toException("Chyba pri inkrementovaní počtu referencií pre daný modul", ex);
//        }
//    }

    @Override
    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPost(String registerId, Integer verziaRegistraId, Long zaznamId, String moduleId) {

        Udalost udalost = null;
        RegisterId register;
        AbstractRegEntityData data;

        try {

            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.REFERENCE_INCREMENT);
            register = new RegisterId(registerId, verziaRegistraId);
            final String subject = register.toString();
            final AbstractRegPlugin plugin = getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }

            Session session = entityManager.unwrap(Session.class);
            data = session.get(plugin.getDataEntityClass(), zaznamId, LockMode.PESSIMISTIC_WRITE);

            if (data == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje záznam '" + zaznamId + "' pre register '" + plugin.getInfo().getName() + "' (" + subject + ")", null);
            }
            if (data instanceof SubjectReg1DataEntity) {
                data.setPovodneId(SearchUtils.sanitizeValue(((SubjectReg1DataEntity) data).getSubjektId()));
            }

            data.setDatumCasPoslednejReferencie(null);
            plugin.updateDataEntity(data);

            AbstractRegEntityDataReference dataReference = plugin.incrementReference(zaznamId, moduleId, subject, data);
            if (dataReference.getPocetReferencii() == 1) {
                plugin.onFirstRefInsert(data);
            }

            ZaznamRegistraReferencia zaznamRegistraReferencia = new ZaznamRegistraReferencia().registerId(registerId).verziaRegistraId(verziaRegistraId).zaznamId(zaznamId).modul(moduleId).pocetReferencii(dataReference.getPocetReferencii());

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(zaznamRegistraReferencia, HttpStatus.OK);

        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException("Chyba pri inkrementovaní počtu referencií pre daný modul", ex);
        }
    }

    @Override
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(String registerId, Integer verziaRegistraId, Long zaznamId, UserInfo userInfo) {

        Udalost udalost = null;
        RegisterId register;
        AbstractRegEntityData data;

        try {

            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.READ, userInfo.getLogin());
            register = new RegisterId(registerId, verziaRegistraId);
            final AbstractRegPlugin plugin = getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }
            //data = plugin.getDataRepository().findById(entryId);

            data = plugin.findEntryByEntryId(zaznamId, AbstractRegEntityData.class);
            if (data == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, ZaznamRegistraController.NEBOL_NAJDENY_VYHOVUJUCI_ZAZNAM, null);
            }

            ZaznamRegistraOutputDetail zaznamRegistraOutputDetail = registerService.prepareZaznamRegistraOutputDetail(data, plugin);

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(zaznamRegistraOutputDetail, HttpStatus.OK);

        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException(ZaznamRegistraController.CHYBA_PRI_VYHLADAVANI_REGISTRA, ex);
        }
    }

//    @Transactional
//    public ResponseEntity<ZaznamRegistraReferencia> registerEntryReferenceRegisterIdRegisterVersionIdEntryIdModuleIdPut(String registerId, Integer registerVersionId, Long entryId, String moduleId, UserInfo userInfo) {
//
//        Udalost event = null;
//        RegisterId register = null;
//        AbstractRegEntityData data = null;
//
//        try {
//
//            event = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.REFERENCE_INCREMENT, userInfo.getLogin());
//            register = new RegisterId(registerId, registerVersionId);
//            final String subject = register.toString();
//            final AbstractRegPlugin plugin = getPlugin(register);
//            if (plugin.getInfo().getPublicRegisterId() != null) {
//                register.setRegisterId(plugin.getInfo().getRegisterId());
//            }
//            authorizationService.checkIsAuthorized(subject, AuthorizationDomainEnum.REGISTER, AuthorizationOperationEnum.REGISTER_EDIT, plugin.getInfo().getName(), userInfo);
//
//            //Session session = entityManager.unwrap(Session.class);
//            //data = session.get(plugin.getDataEntityClass(), entryId, LockMode.PESSIMISTIC_WRITE);
//            data = plugin.findEntryByEntryId(entryId, AbstractRegEntityData.class);
//
//            if (data == null) {
//                throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje záznam '" + entryId + "' pre register '" + plugin.getInfo().getName() + "' (" + subject + ")", null);
//            }
//            if (data instanceof SubjectReg1DataEntity) {
//                data.setPovodneId(SearchUtils.sanitizeValue(((SubjectReg1DataEntity) data).getSubjektId()));
//            }
//            entityManager.lock(data, LockModeType.PESSIMISTIC_WRITE);
//
//            data.setDatumCasPoslednejReferencie(null);
//            plugin.updateDataEntity(data);
//
//            AbstractRegEntityDataReference dataReference = plugin.incrementReference(entryId, moduleId, subject, data);
//            if (dataReference.getPocetReferencii() == 1) {
//                plugin.onFirstRefInsert(data, userInfo);
//            }
//
//            ZaznamRegistraReferencia registerEntryReference = new ZaznamRegistraReferencia().registerId(registerId).verziaRegistraId(registerVersionId).zaznamId(entryId).modul(moduleId).pocetReferencii(dataReference.getPocetReferencii());
//
//            udalostService.updateEvent(event, null);
//            return new ResponseEntity<>(registerEntryReference, HttpStatus.OK);
//
//        } catch (Exception ex) {
//            try {
//                udalostService.updateEvent(event, ex.getMessage());
//            } catch (Exception suppresedException) {
//                ex.addSuppressed(suppresedException);
//            }
//            throw toException("Chyba pri inkrementovaní počtu referencií pre daný modul", ex);
//        }
//    }
}
