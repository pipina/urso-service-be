package sk.is.urso.controller;

import org.alfa.exception.IException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import sk.is.urso.config.csru.CsruRfoChangesConfig;
import sk.is.urso.config.csru.CsruRfoCodelistConfig;
import sk.is.urso.config.csru.RfoIdentificationConfig;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.rest.api.RfoApi;
import sk.is.urso.rest.model.ConfirmChangesIdObject;
import sk.is.urso.rest.model.HodnotaCiselnikaShortDetail;
import sk.is.urso.rest.model.RfoExternalIdsObject;
import sk.is.urso.rest.model.ZaznamRegistra;
import sk.is.urso.service.csru.RfoService;

import java.util.List;
import java.util.Optional;

@RestController
public class TestovanieRfoController implements RfoApi, IException {

    @Autowired
    private RfoService rfoService;

    @Autowired
    private RfoIdentificationConfig rfoIdentificationConfig;

    @Autowired
    private CsruRfoChangesConfig csruRfoChangesConfig;

    @Autowired
    private CsruRfoCodelistConfig csruRfoCodelistConfig;

    @Override
    public ResponseEntity<ZaznamRegistra> rfoByRodneCisloPost(String rodneCislo) {
        try {
            return new ResponseEntity<>(rfoService.csruSearch(rodneCislo), HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba pri vyhľadávaní v registri RFO", ex);
        }
    }

    @Override
    public ResponseEntity<List<ZaznamRegistraOutputDetail>> rfoDataByIdPost(RfoExternalIdsObject rfoExternalIdsObject) {
        try {
            var response = rfoIdentificationConfig.rfoDataById(rfoService.getSubjects(rfoExternalIdsObject.getRfoExternalIds()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba 'rfoDataByIdGet'.", ex);
        }
    }

    @Override
    public ResponseEntity<Void> poiMarkingPost(RfoExternalIdsObject rfoExternalIdsObject) {
        try {
            rfoIdentificationConfig.createPoiMarkingRequestSynchr(rfoService.getSubjects(rfoExternalIdsObject.getRfoExternalIds()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba 'poiMarkingGet'.", ex);
        }
    }

    @Override
    public ResponseEntity<Void> poiUnmarkingPost(RfoExternalIdsObject rfoExternalIdsObject) {
        try {
            rfoIdentificationConfig.createPoiUnmarkingRequestSynchr(rfoService.getSubjects(rfoExternalIdsObject.getRfoExternalIds()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba 'poiUnmarkingPost'.", ex);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ConfirmChangesIdObject> rfoChangesGet() {
        try {
            ConfirmChangesIdObject confirmChangesIdObject = new ConfirmChangesIdObject();
            List<String> confirmationIds = csruRfoChangesConfig.sendChangedFoRequestSynchr(false);
            confirmChangesIdObject.setConfirmChangesIds(confirmationIds);
            return new ResponseEntity<>(confirmChangesIdObject, HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba 'rfoChangesPost'.", ex);
        }
    }

    @Override
    public ResponseEntity<Void> confirmChangeChangeIdPost(Long changeId) {
        try {
            csruRfoChangesConfig.sendConfirmationSynchr(changeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba pri 'confirmChangeChangeIdPost'.", ex);
        }
    }

    @Override
    public ResponseEntity<List<HodnotaCiselnikaShortDetail>> titleCodelistGet() {
        try {
            return new ResponseEntity<>(csruRfoCodelistConfig.sendRfoCodelistRequestTitles(), HttpStatus.OK);
        } catch (Exception ex) {
            throw toException("Chyba pri 'titleCodelistGet'.", ex);
        }
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return RfoApi.super.getRequest();
    }
}
