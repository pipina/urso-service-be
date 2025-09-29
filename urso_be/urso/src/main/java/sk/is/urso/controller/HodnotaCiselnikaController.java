package sk.is.urso.controller;

import org.alfa.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;
import sk.is.urso.rest.api.HodnotaCiselnikaApi;
import sk.is.urso.rest.model.HodnotaCiselnikaInputDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaList;
import sk.is.urso.rest.model.HodnotaCiselnikaListRequest;
import sk.is.urso.rest.model.HodnotaCiselnikaOutputDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaRequestFilter;
import sk.is.urso.rest.model.HodnotaCiselnikaShortDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaSimpleOutput;
import sk.is.urso.service.HodnotaCiselnikaService;

import java.util.List;

@RestController
public class HodnotaCiselnikaController extends BaseController<
        HodnotaCiselnikaInputDetail,
        HodnotaCiselnikaOutputDetail,
        HodnotaCiselnikaList,
        HodnotaCiselnikaSimpleOutput,
        HodnotaCiselnikaListRequest,
        HodnotaCiselnikaRequestFilter,
        HodnotaCiselnika,
        Long> implements HodnotaCiselnikaApi {

    private static final String ERROR_ENUMERATION_VALUE_CREATE = "Chyba pri pokuse vložiť hodnotu číselníka.";
    private static final String ERROR_ENUMERATION_VALUE_FILTER = "Chyba pri načítaní filtrovaného zoznamu hodnôt číselníka.";
    private static final String ERROR_ENUMERATION_VALUE_UPDATE = "Chyba pri aktualizovaní hodnoty číselníka.";
    private static final String ERROR_ENUMERATION_VALUE_GET = "Chyba pri načítaní hodnoty číselníka.";
    private static final String ERROR_ENUMERATION_VALUE_DELETE = "Chyba pri vymazávaní hodnoty číselníka.";
    private static final String ERROR_NOT_IMPLEMENTED = "Operácia nie je implementovaná.";
    public static final String NAZOV_POLOZKY = "nazovPolozky";
    public static final String DODATOCNY_OBSAH = "dodatocnyObsah";
    private static final String ERROR_ENUMERATION_VALUE_FILTER_ADD_CONTENT = "Chyba pri načítaní filtrovaného zoznamu hodnôt číselníka podľa dodatočného obsahu";
    private static final String ERROR_ENUMERATION_VALUE_FILTER_ITEM_NAME = "Chyba pri načítaní filtrovaného zoznamu hodnôt číselníka podľa názvu položky.";

    @Autowired
    public HodnotaCiselnikaController(HodnotaCiselnikaService entityService) {
        super(entityService);
    }

    @Override
    protected String getErrorEntityCreate() {
        return ERROR_ENUMERATION_VALUE_CREATE;
    }

    @Override
    protected String getErrorEntityFilter() {
        return ERROR_ENUMERATION_VALUE_FILTER;
    }

    @Override
    protected String getErrorEntityGet() {
        return ERROR_ENUMERATION_VALUE_GET;
    }

    @Override
    protected String getErrorEntityDelete() {
        return ERROR_ENUMERATION_VALUE_DELETE;
    }

    @Override
    protected String getErrorEntityUpdate() {
        return ERROR_ENUMERATION_VALUE_UPDATE;
    }

    @Override
    protected String getErrorEntityAll() {
        throw new CommonException(HttpStatus.NOT_IMPLEMENTED, ERROR_NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<HodnotaCiselnikaList> filterHodnotaCiselnika(HodnotaCiselnikaListRequest hodnotaCiselnikaListRequest) {
        return super.filter(hodnotaCiselnikaListRequest);
    }

    @Override
    public ResponseEntity<HodnotaCiselnikaOutputDetail> getHodnotaCiselnika(Long id) {
        return super.get(id);
    }

    @Override
    public ResponseEntity<HodnotaCiselnikaOutputDetail> createHodnotaCiselnika(HodnotaCiselnikaInputDetail hodnotaCiselnikaInputDetail) {
        return super.create(hodnotaCiselnikaInputDetail);
    }

    @Override
    public ResponseEntity<HodnotaCiselnikaOutputDetail> updateHodnotaCiselnika(Long id, HodnotaCiselnikaInputDetail hodnotaCiselnikaInputDetail) {
        return super.update(id, hodnotaCiselnikaInputDetail);
    }

    @Override
    public ResponseEntity<Void> deleteHodnotaCiselnika(Long id) {
        return super.delete(id);
    }

    @Override
    public ResponseEntity<List<HodnotaCiselnikaShortDetail>> additionalInfoFilterHodnotaCiselnika(String kodCiselnika, String search) {
        try {
            return new ResponseEntity<>(((HodnotaCiselnikaService) this.modelService).findByParameter(kodCiselnika, search, DODATOCNY_OBSAH), HttpStatus.OK);
        } catch (Exception e) {
            throw toException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_ENUMERATION_VALUE_FILTER_ADD_CONTENT, e);
        }
    }

    @Override
    public ResponseEntity<List<HodnotaCiselnikaShortDetail>> simpleFilterHodnotaCiselnika(String kodCiselnika, String search) {
        try {
            return new ResponseEntity<>(((HodnotaCiselnikaService) this.modelService).findByParameter(kodCiselnika, search, NAZOV_POLOZKY), HttpStatus.OK);
        } catch (Exception e) {
            throw toException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_ENUMERATION_VALUE_FILTER_ITEM_NAME, e);
        }
    }
}
