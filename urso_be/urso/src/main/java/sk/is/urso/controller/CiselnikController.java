package sk.is.urso.controller;

import org.alfa.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.is.urso.model.ciselniky.Ciselnik;
import sk.is.urso.rest.api.CiselnikApi;
import sk.is.urso.rest.model.CiselnikInputDetail;
import sk.is.urso.rest.model.CiselnikList;
import sk.is.urso.rest.model.CiselnikListRequest;
import sk.is.urso.rest.model.CiselnikOutputDetail;
import sk.is.urso.rest.model.CiselnikRequestFilter;
import sk.is.urso.rest.model.CiselnikSimpleOutput;
import sk.is.urso.service.CiselnikService;

@RestController
public class CiselnikController extends BaseController<
        CiselnikInputDetail,
        CiselnikOutputDetail,
        CiselnikList,
        CiselnikSimpleOutput,
        CiselnikListRequest,
        CiselnikRequestFilter,
        Ciselnik,
        Long> implements CiselnikApi {

    private static final String ERROR_ENUMERATION_CREATE = "Chyba pri pokuse vložiť číselník.";
    private static final String ERROR_ENUMERATION_FILTER = "Chyba pri načítaní filtrovaného zoznamu číselníkov.";
    private static final String ERROR_ENUMERATION_UPDATE = "Chyba pri aktualizovaní číselníka.";
    private static final String ERROR_ENUMERATION_GET = "Chyba pri načítaní číselníka.";
    private static final String ERROR_ENUMERATION_DELETE = "Chyba pri vymazávaní číselníka.";
    private static final String ERROR_NOT_IMPLEMENTED = "Operácia nie je implementovaná.";

    @Autowired
    public CiselnikController(CiselnikService entityService) {
        super(entityService);
    }

    @Override
    protected String getErrorEntityCreate() {
        return ERROR_ENUMERATION_CREATE;
    }

    @Override
    protected String getErrorEntityFilter() {
        return ERROR_ENUMERATION_FILTER;
    }

    @Override
    protected String getErrorEntityGet() {
        return ERROR_ENUMERATION_GET;
    }

    @Override
    protected String getErrorEntityDelete() {
        return ERROR_ENUMERATION_DELETE;
    }

    @Override
    protected String getErrorEntityUpdate() {
        return ERROR_ENUMERATION_UPDATE;
    }
    
    @Override
    protected String getErrorEntityAll() {
        throw new CommonException(HttpStatus.NOT_IMPLEMENTED, ERROR_NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<CiselnikList> filterCiselnik(CiselnikListRequest ciselnikListRequest) {
        return super.filter(ciselnikListRequest);
    }

    @Override
    public ResponseEntity<CiselnikOutputDetail> getCilsenik(Long id) {
        return super.get(id);
    }

    @Override
    public ResponseEntity<CiselnikOutputDetail> createCiselnik(CiselnikInputDetail ciselnikInputDetail) {
        return super.create(ciselnikInputDetail);
    }

    @Override
    public ResponseEntity<CiselnikOutputDetail> updateCilsenik(Long id, CiselnikInputDetail ciselnikInputDetail) {
        return super.update(id, ciselnikInputDetail);
    }

    @Override
    public ResponseEntity<Void> deleteCilsenik(Long id) {
        return super.delete(id);
    }
}
