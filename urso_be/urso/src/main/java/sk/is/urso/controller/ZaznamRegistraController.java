package sk.is.urso.controller;

import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.alfa.model.UserInfo;
import org.alfa.service.ListRequestService;
import org.alfa.service.UserInfoService;
import org.alfa.utils.TrimmingUtils;
import org.alfa.utils.XmlUtils;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.w3c.dom.Document;
import sk.is.urso.reg.AbstractRegEntityData;
import sk.is.urso.reg.AbstractRegEntityDataReference;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.RegisterEntryReferenceKey;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.reg.model.ZaznamRegistraList;
import sk.is.urso.reg.model.ZaznamRegistraListRequest;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.reg.model.ZaznamRegistraReferencia;
import sk.is.urso.component.AdditionalPluginOpsImpl;
import sk.is.urso.config.Registers;
import sk.is.urso.model.Udalost;
import sk.is.urso.rest.api.ZaznamRegistraApi;
import sk.is.urso.rest.model.UdalostDomenaEnum;
import sk.is.urso.rest.model.UdalostKategoriaEnum;
import sk.is.urso.rest.model.ZaznamRegistraXPathData;
import sk.is.urso.rest.model.ZaznamRegistraXPathZmena;
import sk.is.urso.service.RegisterService;
import sk.is.urso.service.UdalostService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The Class zaznamRegistraController.
 */
@RestController
public class ZaznamRegistraController implements ZaznamRegistraApi, IException {

    public static final String CHYBA_PRI_CITANI_REGISTRA = "Neočakávaná chyba pri čítaní registra";
    public static final String CHYBA_PRI_VKLADANI_REGISTRA = "Neočakávaná chyba pri vkladaní registra";
    public static final String NEBOL_NAJDENY_VYHOVUJUCI_ZAZNAM = "Nebol nájdený vyhovujúci záznam";
    public static final String REGISTER = "', register '";
    public static final String MODUL = "', modul '";
    public static final String CHYBA_PRI_AKTUALIZOVANI_REGISTRA = "Chyba pri aktualizovaní registra";
    public static final String CHYBA_PRI_VYHLADAVANI_REGISTRA = "Chyba pri vyhľadávaní registra";

    //private static final Logger log = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UdalostService udalostService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private ListRequestService listRequestService;

    @Autowired
    private Registers registers;

    @Autowired
    private AdditionalPluginOpsImpl additionalOps;

    @PersistenceContext
    private EntityManager entityManager;

    private final ModelMapper modelMapper = new ModelMapper();

    /**
     * Register entry formio register id register version id entry id get.
     *
     * @param registerId        the register id
     * @param verziaRegistraId the register version id
     * @param  zaznamId           the entry id
     * @return the response entity
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraFormioRegisterIdVerziaRegistraIdZaznamIdGet(String registerId, Integer verziaRegistraId, Long zaznamId) {
        try {
            RegisterId register = new RegisterId(registerId, verziaRegistraId);
            final AbstractRegPlugin plugin = registers.getPlugin(register);

            ResponseEntity<ZaznamRegistraOutputDetail> responseEntity =   zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(registerId, verziaRegistraId, zaznamId);
              ZaznamRegistraOutputDetail zaznamRegistraFormioOutputDetail = modelMapper.map(responseEntity.getBody(),   ZaznamRegistraOutputDetail.class);

            zaznamRegistraFormioOutputDetail.setData(registerService.createFormioJsonFromXml(zaznamRegistraFormioOutputDetail.getData(), plugin));
            return new ResponseEntity<>(zaznamRegistraFormioOutputDetail, HttpStatus.OK);

        } catch (Exception ex) {
            throw toException(CHYBA_PRI_CITANI_REGISTRA, ex);
        }
    }

    /**
     * Register entry formio post.
     *
     * @param zaznamRegistraInputDetail the register entry update detail
     * @return the response entity
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraFormioPut(ZaznamRegistraInputDetail zaznamRegistraInputDetail) {
        try {

            var register = new RegisterId(  zaznamRegistraInputDetail.getRegisterId(), zaznamRegistraInputDetail.getVerziaRegistraId());
            final AbstractRegPlugin plugin = registers.getPlugin(register);

            zaznamRegistraInputDetail.setData(registerService.createXmlFromXsdAndJson(zaznamRegistraInputDetail.getData(), plugin));
            ResponseEntity<ZaznamRegistraOutputDetail> outputResponse = zaznamRegistraPost(zaznamRegistraInputDetail);
            if (outputResponse.getBody() != null) {
                outputResponse.getBody().setData(registerService.createFormioJsonFromXml(outputResponse.getBody().getData(), plugin));
            }
            return outputResponse;

        } catch (Exception ex) {
            throw toException(CHYBA_PRI_VKLADANI_REGISTRA, ex);
        }
    }

    /**
     * Register entry formio put.
     *
     * @param zaznamRegistraInputDetail the register entry insert detail
     * @return the response entity
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraFormioPost(ZaznamRegistraInputDetail zaznamRegistraInputDetail) {

        try {

            var register = new RegisterId(zaznamRegistraInputDetail.getRegisterId(), zaznamRegistraInputDetail.getVerziaRegistraId());
            final AbstractRegPlugin plugin = registers.getPlugin(register);

            zaznamRegistraInputDetail.setData(registerService.createXmlFromXsdAndJson(zaznamRegistraInputDetail.getData(), plugin));
            ResponseEntity<ZaznamRegistraOutputDetail> outputResponse = zaznamRegistraPut(zaznamRegistraInputDetail);
            if (outputResponse.getBody() != null) {
                outputResponse.getBody().setData(registerService.createFormioJsonFromXml(outputResponse.getBody().getData(), plugin));
            }
            return outputResponse;

        } catch (Exception ex) {
            throw toException(CHYBA_PRI_VKLADANI_REGISTRA, ex);
        }
    }

    /**
     * Register entry register id register version id entry id get.
     *
     * @param registerId        the register id
     * @param verziaRegistraId the register version id
     * @param  zaznamId           the entry id
     * @return the response entity
     */

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(String registerId, Integer verziaRegistraId, Long zaznamId) {
        return zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(registerId, verziaRegistraId,  zaznamId, userInfoService.getUserInfo());
    }


    @Transactional(readOnly = true)
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(String registerId, Integer verziaRegistraId, Long zaznamId, UserInfo userInfo) {
        return additionalOps.zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(registerId, verziaRegistraId,  zaznamId, userInfo);
    }

    /**
     * Služba vytvorí nový záznam registra na základe prijatých údajov.
     *
     * @param zaznamRegistraInputDetail záznam registra s prijatými údajmi
     * @return {@link   ZaznamRegistraOutputDetail} detail vytvoreného záznamu registra
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPost(ZaznamRegistraInputDetail zaznamRegistraInputDetail) {
        return zaznamRegistraPost(zaznamRegistraInputDetail, userInfoService.getUserInfo());
    }

    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPost(ZaznamRegistraInputDetail zaznamRegistraInputDetail, UserInfo userInfo) {
        return additionalOps.zaznamRegistraPost(zaznamRegistraInputDetail, userInfo);
    }

    /**
     * Služba aktualizuje záznam registra na základe prijatých aktuálnych údajov.
     *
     * @param zaznamRegistraInputDetail záznam registra s aktuálnymi údajmi
     * @return {@link   ZaznamRegistraOutputDetail} aktualizovaný detail záznamu registra
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPut(ZaznamRegistraInputDetail zaznamRegistraInputDetail) {
        return zaznamRegistraPut(zaznamRegistraInputDetail, userInfoService.getUserInfo());
    }

    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPut(ZaznamRegistraInputDetail zaznamRegistraInputDetail, UserInfo userInfo) {
        return additionalOps.zaznamRegistraPost(zaznamRegistraInputDetail, userInfo);
    }

    /**
     * Register entry register id register version id entry id delete.
     *
     * @param registerId        the register id
     * @param verziaRegistraId the register version id
     * @param  zaznamId           the entry id
     * @return the response entity
     */

    @Override
    @Transactional
    public ResponseEntity<Void> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdDelete(String registerId, Integer verziaRegistraId, Long zaznamId) {

        Udalost udalost = null;
        RegisterId register;
        AbstractRegEntityData data;

        try {

            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.DELETE);
            register = new RegisterId(registerId, verziaRegistraId);
            final AbstractRegPlugin plugin = registers.getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }

            if (plugin.existsDataReferenceEntity(zaznamId)) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "K záznamu existuje referencia a nie je možné ho zmazať", null);
            }

            data = plugin.getDataRepository().findById(zaznamId, AbstractRegEntityData.class);
            if (data == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, NEBOL_NAJDENY_VYHOVUJUCI_ZAZNAM, null);
            }

            plugin.deleteNaturalIdEntityByDataEntryId(zaznamId);
            plugin.deleteDataEntityById(data.getId());

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException("Chyba pri mazaní registra", ex);
        }
    }

    /**
     * Register entry reference register id register version id entry id module id put.
     *
     * @param registerId        the register id
     * @param verziaRegistraId the register version id
     * @param  zaznamId           the entry id
     * @param modulId          the module id
     * @return the response entity
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPost(String registerId, Integer verziaRegistraId, Long zaznamId, String modulId) {
        return additionalOps.zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPost(registerId, verziaRegistraId,  zaznamId, modulId);
    }

    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(String registerId, Integer registerVersionId, Long entryId, String moduleId) {
        return additionalOps.zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(registerId, registerVersionId, entryId, moduleId);
    }

    /**
     * Register entry reference register id register version id entry id module id get.
     *
     * @param registerId        the register id
     * @param verziaRegistraId the register version id
     * @param  zaznamId           the entry id
     * @param modulId          the module id
     * @return the response entity
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdGet(String registerId, Integer verziaRegistraId, Long zaznamId, String modulId) {

        try {

            final RegisterId register = new RegisterId(registerId, verziaRegistraId);
            final String subject = register.toString();
            final AbstractRegPlugin plugin = registers.getPlugin(register);

            AbstractRegEntityDataReference dataReference = plugin.getDataReferenceRepository().findById(new RegisterEntryReferenceKey(zaznamId, modulId)).orElse(null);
            if (dataReference == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje referencia pre záznam '" +  zaznamId + MODUL + modulId + REGISTER + plugin.getInfo().getName() + "' (" + subject + ")", null);
            }

            return new ResponseEntity<>(new ZaznamRegistraReferencia().registerId(registerId).verziaRegistraId(verziaRegistraId).zaznamId(zaznamId).modul(modulId).pocetReferencii(dataReference.getPocetReferencii()), HttpStatus.OK);

        } catch (Exception ex) {
            throw toException("Chyba pri vyhľadávaní počtu referencií pre daný modul", ex);
        }
    }

    /**
     * Register entry reference register id register version id entry id module id delete.
     *
     * @param registerId        the register id
     * @param verziaRegistraId the register version id
     * @param  zaznamId           the entry id
     * @param modulId          the module id
     * @return the response entity
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdDelete(String registerId, Integer verziaRegistraId, Long zaznamId, String modulId) {

        Udalost udalost = null;
        RegisterId register;
        AbstractRegEntityData data;

        try {
            ZaznamRegistraReferencia zaznamRegistraReference;

            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.REFERENCE_DECREMENT);
            register = new RegisterId(registerId, verziaRegistraId);
            final String subject = register.toString();
            final AbstractRegPlugin plugin = registers.getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }

            Session session = entityManager.unwrap(Session.class);
            data = session.get(plugin.getDataEntityClass(),  zaznamId, LockMode.PESSIMISTIC_WRITE);
            if (data == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje záznam '" +  zaznamId + "' pre register '" + plugin.getInfo().getName() + "' (" + subject + ")", null);
            }

            AbstractRegEntityDataReference dataReference = plugin.getDataReferenceRepository().findById(new RegisterEntryReferenceKey(zaznamId, modulId)).orElse(null);
            if (dataReference == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, "Neexistuje referencia pre záznam '" +  zaznamId + MODUL + modulId + REGISTER + plugin.getInfo().getName() + "' (" + subject + ")", null);
            }
            if (dataReference.getPocetReferencii() <= 0) {
                throw new CommonException(HttpStatus.BAD_REQUEST, "Počet referencií pre pre záznam '" +  zaznamId + MODUL + modulId + REGISTER + plugin.getInfo().getName() + "' (" + subject + ") nie je možné dekrementovať", null);
            }

            if (dataReference.getPocetReferencii() - 1 == 0) {
                data.getEntityDataReferences().remove(dataReference);
                plugin.deleteDataReferenceEntity(dataReference);

                plugin.onLastRefDelete(data);

                if (data.getEntityDataReferences().isEmpty() && plugin.getPluginConfig().getGdprParams().isImmediateDeleteOnLastReference() != null && plugin.getPluginConfig().getGdprParams().isImmediateDeleteOnLastReference()) {
                    zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdDelete(registerId, verziaRegistraId,  zaznamId);
                }
                if (data.getEntityDataReferences().isEmpty()) {
                    data.setDatumCasPoslednejReferencie(LocalDateTime.now());
                    session.save(data);
                }

                zaznamRegistraReference = new ZaznamRegistraReferencia().registerId(registerId).verziaRegistraId(verziaRegistraId).zaznamId(zaznamId).modul(modulId).pocetReferencii(0);

                udalostService.updateEvent(udalost, null);
                return new ResponseEntity<>(zaznamRegistraReference, HttpStatus.OK);
            }

            dataReference.setPocetReferencii(dataReference.getPocetReferencii() - 1);
            plugin.saveDataReferenceEntity(dataReference);

            session.close();

            zaznamRegistraReference = new ZaznamRegistraReferencia().registerId(registerId).verziaRegistraId(verziaRegistraId).zaznamId(zaznamId).modul(modulId).pocetReferencii(dataReference.getPocetReferencii());

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(zaznamRegistraReference, HttpStatus.OK);

        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException("Chyba pri dekrementovaní počtu referencií pre daný modul", ex);
        }
    }

    /**
     * Register entries register id register version id post.
     *
     * @param registerId the register id
     * @param verziaRegistraId the register version id
     * @param zaznamRegistraListRequest the register entries list request
     * @return the response entity
     */
    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraList> zaznamRegistraRegisterIdVerziaRegistraIdFilterPost(String registerId, Integer verziaRegistraId, ZaznamRegistraListRequest zaznamRegistraListRequest) {
        //toto nie je implementovane cez listRequestService.standard lebo je to zlozitejsie
        Udalost udalost = null;
        RegisterId register = new RegisterId(registerId, verziaRegistraId);

        try {
            TrimmingUtils.trimStringFields(zaznamRegistraListRequest);
            udalost = udalostService.createEvent(UdalostDomenaEnum.REGISTER, UdalostKategoriaEnum.READ);

            final AbstractRegPlugin plugin = registers.getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {// zvonka posielaju public id ale my chceme interne
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }

            ZaznamRegistraList zaznamRegistraList = plugin.findRegisterEntries(zaznamRegistraListRequest.getFilter(), listRequestService.toRequestList(zaznamRegistraListRequest));

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(zaznamRegistraList, HttpStatus.OK);
        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException("Chyba pri vyhľadávaní registrov", ex);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdActualGet(String registerId, Integer verziaRegistraId, Long zaznamId) {
        Udalost udalost = null;
        RegisterId register;
        AbstractRegEntityData data;

        try {

            udalost = udalostService.createEvent(UdalostDomenaEnum.HODNOTA_REGISTRA, UdalostKategoriaEnum.READ);
            register = new RegisterId(registerId, verziaRegistraId);
            final AbstractRegPlugin plugin = registers.getPlugin(register);
            if (plugin.getInfo().getPublicRegisterId() != null) {
                register.setRegisterId(plugin.getInfo().getRegisterId());
            }

            data = plugin.findEntryByEntryId(zaznamId, AbstractRegEntityData.class);
            if (data == null) {
                throw new CommonException(HttpStatus.NOT_FOUND, NEBOL_NAJDENY_VYHOVUJUCI_ZAZNAM, null);
            }

            ZaznamRegistraOutputDetail zaznamRegistraOutputDetail = registerService.prepareZaznamRegistraOutputDetail(data, plugin);
            zaznamRegistraOutputDetail.setData(registerService.getActualData(plugin, data));

            udalostService.updateEvent(udalost, null);
            return new ResponseEntity<>(zaznamRegistraOutputDetail, HttpStatus.OK);
        } catch (Exception ex) {
            try {
                udalostService.updateEvent(udalost, ex.getMessage());
            } catch (Exception suppresedException) {
                ex.addSuppressed(suppresedException);
            }
            throw toException(CHYBA_PRI_VYHLADAVANI_REGISTRA, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<ZaznamRegistraXPathData>> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdXpathGet(String registerId, Integer verziaRegistraId, Long zaznamId, List<String> xpath) {
        RegisterId register;
        try {
            register = new RegisterId(registerId, verziaRegistraId);
            final AbstractRegPlugin plugin = registers.getPlugin(register);

              ZaznamRegistraOutputDetail   ZaznamRegistraOutputDetail =   zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(registerId, verziaRegistraId,  zaznamId).getBody();
            List<ZaznamRegistraXPathData> zaznamRegistraXPathDataList;
            if (  ZaznamRegistraOutputDetail != null) {
                zaznamRegistraXPathDataList = registerService.getXPathData(  ZaznamRegistraOutputDetail.getData(), xpath, plugin);
            } else {
                throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Neexistuje vyhľadávaný záznam xpathData.");
            }

            return new ResponseEntity<>(zaznamRegistraXPathDataList, HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba pri načítaní dát cez xpath výrazy!", ex);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdXpathPut(String registerId, Integer verziaRegistraId, Long zaznamId, ZaznamRegistraXPathZmena zaznamRegistraXPathZmena) {
        try {
            RegisterId register = new RegisterId(registerId, verziaRegistraId);
            final AbstractRegPlugin plugin = registers.getPlugin(register);

            ResponseEntity<ZaznamRegistraOutputDetail> responseEntity = zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(registerId, verziaRegistraId,  zaznamId);

            Document xmlData = XmlUtils.parse(Objects.requireNonNull(responseEntity.getBody()).getData());

            String updatedXml = registerService.changeXml(zaznamRegistraXPathZmena, plugin, xmlData);
            ZaznamRegistraInputDetail zaznamRegistraInputDetail = modelMapper.map(responseEntity.getBody(), ZaznamRegistraInputDetail.class);
            zaznamRegistraInputDetail.setData(updatedXml);
            ResponseEntity<ZaznamRegistraOutputDetail> outputResponse = zaznamRegistraPost(zaznamRegistraInputDetail);
            return new ResponseEntity<>(outputResponse.getBody(), HttpStatus.OK);
        } catch (Exception ex) {
            throw toException(CHYBA_PRI_AKTUALIZOVANI_REGISTRA, ex);
        }
    }

//    @Transactional
//    public ResponseEntity<ZaznamRegistraOutputDetail> registerEntryActualPost(ZaznamRegistraInputDetail registerEntryUpdateDetail, UserInfo userInfo) {
//        return additionalOps.zaznamRegistraPost(registerEntryUpdateDetail, userInfo, true);
//    }

    /**
     * Gets the request.
     *
     * @return the request
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }
}