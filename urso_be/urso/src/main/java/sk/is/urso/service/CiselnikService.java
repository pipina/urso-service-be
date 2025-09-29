package sk.is.urso.service;

import org.alfa.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sk.is.urso.model.ciselniky.Ciselnik;
import sk.is.urso.repository.ciselniky.CiselnikRepository;
import sk.is.urso.converter.CiselnikConverter;
import sk.is.urso.rest.model.CiselnikInputDetail;
import sk.is.urso.rest.model.CiselnikList;
import sk.is.urso.rest.model.CiselnikListRequest;
import sk.is.urso.rest.model.CiselnikOutputDetail;
import sk.is.urso.rest.model.CiselnikRequestFilter;
import sk.is.urso.rest.model.CiselnikSimpleOutput;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CiselnikService extends BaseModelService<
        CiselnikInputDetail,
        CiselnikOutputDetail,
        CiselnikList,
        CiselnikSimpleOutput,
        CiselnikListRequest,
        CiselnikRequestFilter,
        Ciselnik,
        Long> {

    private static final String ERROR_INVALID_ENUMERATION_ID = "Číselník s ID '%s' neexistuje.";
    private static final String ERROR_EXISTS_ENUMERATION_CODE = "Číselník s kódom '%s' existuje.";
    private static final String ERROR_ENUMERATION_FILTER = "Chyba pri načítaní filtrovaného zoznamu číselníkov.";
    private static final String ERROR_SHORT_LIST = "Chyba pri načítaní zoznamu.";
    private static final String DATUM_ZACIATKU_PLATNOSTI_JE_PO_DATUME_KONCA_PLATNOSTI = "Dátum začiatku platnosti je po dátume konca platnosti!";

    @Autowired
    public CiselnikService(CiselnikRepository entityRepository, CiselnikConverter entityConverter) {
        super(entityRepository, entityConverter);
    }

    @Override
    protected String getIdName() {
        return Ciselnik.Fields.id;
    }

    @Override
    protected Optional<String> getDeletedName() {
        return Optional.of(Ciselnik.Fields.deleted);
    }

    @Override
    protected String[] getSupportedSortValues() {
        return new String[]{
                Ciselnik.Fields.id,
                Ciselnik.Fields.kodCiselnika,
                Ciselnik.Fields.nazovCiselnika,
                Ciselnik.Fields.externyKod
        };
    }

    @Override
    protected String getErrorInvalidEntityId(Long id) {
        return String.format(ERROR_INVALID_ENUMERATION_ID, id);
    }

    @Override
    protected String getErrorFilter() {
        return ERROR_ENUMERATION_FILTER;
    }

    @Override
    protected String getErrorShortList() {
        return ERROR_SHORT_LIST;
    }

    @Override
    protected void checkCreate(CiselnikInputDetail input) {
        if (((CiselnikRepository) entityRepository).existsByKodCiselnikaAndDeletedIsFalse(input.getKodCiselnika())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ERROR_EXISTS_ENUMERATION_CODE, input.getKodCiselnika()), null);
        }
        checkDates(input.getPlatnostOd(), input.getPlatnostDo());
    }

    @Override
    protected void checkUpdate(Long id, CiselnikInputDetail input) {
        Ciselnik ciselnik = entityRepository.findEntityById(id);
        checkDates(input.getPlatnostOd(), input.getPlatnostDo());
        if (!ciselnik.getKodCiselnika().equalsIgnoreCase(input.getKodCiselnika()) && ((CiselnikRepository) entityRepository).existsByKodCiselnikaAndDeletedIsFalse(input.getKodCiselnika())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ERROR_EXISTS_ENUMERATION_CODE, input.getKodCiselnika()), null);
        }
    }

    @Override
    protected void checkDelete(Long aLong) {

    }

    @Override
    protected void checkGet(Long aLong) {

    }

    @Override
    protected void postConvert(CiselnikInputDetail input, Ciselnik entity) {

    }

    @Override
    protected void checkPostGet(Ciselnik entity) {

    }

    @Override
    protected void checkFilter(CiselnikListRequest listRequest) {

    }

    private void checkDates(LocalDate validFrom, LocalDate validTo) {
        if (validTo != null && validTo.isBefore(validFrom)) {
            throw new CommonException(HttpStatus.BAD_REQUEST, DATUM_ZACIATKU_PLATNOSTI_JE_PO_DATUME_KONCA_PLATNOSTI, null);
        }
    }
}
