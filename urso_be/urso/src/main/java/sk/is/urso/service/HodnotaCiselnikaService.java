package sk.is.urso.service;

import org.alfa.exception.CommonException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.is.urso.converter.HodnotaCiselnikaConverter;
import sk.is.urso.model.ciselniky.Ciselnik;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;
import sk.is.urso.repository.ciselniky.CiselnikRepository;
import sk.is.urso.repository.ciselniky.HodnotaCiselnikaRepository;
import sk.is.urso.rest.model.HodnotaCiselnikaInputDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaList;
import sk.is.urso.rest.model.HodnotaCiselnikaListRequest;
import sk.is.urso.rest.model.HodnotaCiselnikaOutputDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaRequestFilter;
import sk.is.urso.rest.model.HodnotaCiselnikaShortDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaSimpleOutput;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static sk.is.urso.controller.HodnotaCiselnikaController.DODATOCNY_OBSAH;
import static sk.is.urso.controller.HodnotaCiselnikaController.NAZOV_POLOZKY;

@Service
public class HodnotaCiselnikaService extends BaseModelService<
        HodnotaCiselnikaInputDetail,
        HodnotaCiselnikaOutputDetail,
        HodnotaCiselnikaList,
        HodnotaCiselnikaSimpleOutput,
        HodnotaCiselnikaListRequest,
        HodnotaCiselnikaRequestFilter,
        HodnotaCiselnika,
        Long> {

    private static final String ERROR_INVALID_ENUMERATION_VALUE_ID = "Hodnota číselníka s ID '%s' neexistuje.";
    private static final String ERROR_ENUMERATION_VALUE_FILTER = "Chyba pri načítaní filtrovaného zoznamu hodnôt číselníka.";
    private static final String ERROR_SHORT_LIST = "Chyba pri načítaní zoznamu.";
    private static final String ERROR_EXISTS_ENUMERATION_CODE = "Číselník s kódom '%s' neexistuje.";
    private static final String ERROR_DIFFERENT_ENUMERATION_CODE = "Hodnoty nie sú z rovnakého číselníka: '%s', '%s'.";
    private static final String ERROR_SAME_ITEM_CODES = "Kód položky nadradenej hodnoty sa nemôže rovnať: '%s'.";
    private static final String ERROR_EXISTS_VALUE_FOR_ENUMERATION = "Pre číselník s kódom '%s' už existuje hodnota s kódom '%s'.";
    private static final String DATUM_ZACIATKU_PLATNOSTI_JE_PO_DATUME_KONCA_PLATNOSTI = "Dátum začiatku platnosti '%s' je po dátume konca platnosti '%s'.";
    private static final String PLATNOST_HODNOTY_CISELNIKA_SA_NEPREKRYVA_S_PLATNOSTOU_ZIADNEJ_VERZIE_CISELNIKA = "Platnosť hodnoty číselníka sa neprekrýva s platnosťou číselníka.";

    private final CiselnikRepository ciselnikRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Value("${hodnota.ciselnika.search.limit:3000}")
    private Integer searchLimit;

    @Autowired
    private HodnotaCiselnikaRepository hodnotaCiselnikaRepository;

    @Autowired
    public HodnotaCiselnikaService(HodnotaCiselnikaRepository entityRepository, HodnotaCiselnikaConverter entityConverter, CiselnikRepository ciselnikRepository) {
        super(entityRepository, entityConverter);
        this.ciselnikRepository = ciselnikRepository;
    }

    @Override
    protected String getIdName() {
        return HodnotaCiselnika.Fields.id;
    }

    @Override
    protected Optional<String> getDeletedName() {
        return Optional.of(HodnotaCiselnika.Fields.deleted);
    }

    @Override
    protected String[] getSupportedSortValues() {
        return new String[]{
                HodnotaCiselnika.Fields.id,
                HodnotaCiselnika.Fields.kodPolozky,
                HodnotaCiselnika.Fields.nazovPolozky,
                HodnotaCiselnika.Fields.kodCiselnika,
                HodnotaCiselnika.Fields.platnostOd,
                HodnotaCiselnika.Fields.platnostDo
        };
    }

    @Override
    protected String getErrorInvalidEntityId(Long id) {
        return String.format(ERROR_INVALID_ENUMERATION_VALUE_ID, id);
    }

    @Override
    protected String getErrorFilter() {
        return ERROR_ENUMERATION_VALUE_FILTER;
    }

    @Override
    protected void checkFilter(HodnotaCiselnikaListRequest listRequest) {
    }

    @Override
    protected String getErrorShortList() {
        return ERROR_SHORT_LIST;
    }

    @Override
    protected void checkCreate(HodnotaCiselnikaInputDetail input) {
        Ciselnik ciselnik = ciselnikRepository.findEntityById(input.getCiselnikId());

        if (((HodnotaCiselnikaRepository) entityRepository).existsByKodPolozkyAndKodCiselnikaAndDeletedIsFalse(input.getKodPolozky(), ciselnik.getKodCiselnika())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ERROR_EXISTS_VALUE_FOR_ENUMERATION, ciselnik.getKodCiselnika(), input.getKodPolozky()), null);
        }
        checkDates(input.getPlatnostOd(), input.getPlatnostDo());
        checkIfHodnotaCiselnikaOverlapEnumeration(input.getPlatnostOd(), input.getPlatnostDo(), ciselnik);

        if (input.getNadradenaHodnotaCiselnikaId() != null) {
            checkNadradenaHodnotaCiselnika(input.getNadradenaHodnotaCiselnikaId(), ciselnik.getKodCiselnika(), input.getKodPolozky());
        }
    }

    @Override
    protected void checkUpdate(Long id, HodnotaCiselnikaInputDetail input) {

        Ciselnik ciselnik = ciselnikRepository.findEntityById(input.getCiselnikId());
        HodnotaCiselnika hodnotaCiselnika = this.entityRepository.findEntityById(id);

        if (!input.getKodPolozky().equalsIgnoreCase(hodnotaCiselnika.getKodPolozky()) && ((HodnotaCiselnikaRepository) entityRepository).existsByKodPolozkyAndKodCiselnikaAndDeletedIsFalse(input.getKodPolozky(), ciselnik.getKodCiselnika())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ERROR_EXISTS_VALUE_FOR_ENUMERATION, ciselnik.getKodCiselnika(), input.getKodPolozky()), null);
        }
        checkDates(input.getPlatnostOd(), input.getPlatnostDo());
        checkIfHodnotaCiselnikaOverlapEnumeration(input.getPlatnostOd(), input.getPlatnostDo(), ciselnik);

        if (input.getNadradenaHodnotaCiselnikaId() != null) {
            checkNadradenaHodnotaCiselnika(input.getNadradenaHodnotaCiselnikaId(), ciselnik.getKodCiselnika(), input.getKodPolozky());
        }
    }

    @Override
    protected void checkDelete(Long aLong) {

    }

    @Override
    protected void checkGet(Long aLong) {

    }

    @Override
    protected void postConvert(HodnotaCiselnikaInputDetail input, HodnotaCiselnika entity) {
        Ciselnik ciselnik = ciselnikRepository.findEntityById(input.getCiselnikId());
        entity.setCiselnik(ciselnik);
        entity.setKodCiselnika(ciselnik.getKodCiselnika());
        if (input.getNadradenaHodnotaCiselnikaId() != null) {
            entity.setNadradenaHodnotaCiselnika(this.entityRepository.findEntityById(input.getNadradenaHodnotaCiselnikaId()));
        }
    }

    @Override
    protected void checkPostGet(HodnotaCiselnika entity) {

    }

    public void checkNadradenaHodnotaCiselnika(Long id, String kodCiselnika, String kodPolozky) {
        HodnotaCiselnika parentHodnotaCiselnika = entityRepository.findEntityById(id);
        if (!kodCiselnika.equalsIgnoreCase(parentHodnotaCiselnika.getKodCiselnika())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ERROR_DIFFERENT_ENUMERATION_CODE, kodCiselnika, parentHodnotaCiselnika.getKodCiselnika()), null);
        }
        if (kodPolozky.equalsIgnoreCase(parentHodnotaCiselnika.getKodPolozky())) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ERROR_SAME_ITEM_CODES, kodPolozky), null);
        }
    }

    private void checkDates(LocalDate validFrom, LocalDate validTo) {
        if (validTo != null && validTo.isBefore(validFrom)) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(DATUM_ZACIATKU_PLATNOSTI_JE_PO_DATUME_KONCA_PLATNOSTI, validFrom, validTo), null);
        }
    }

    private void checkIfHodnotaCiselnikaOverlapEnumeration(LocalDate enValFrom, LocalDate enValTo, Ciselnik ciselnik) {

        LocalDate enFrom = ciselnik.getPlatnostOd();
        LocalDate enTo = ciselnik.getPlatnostDo();

        if (enTo == null && enValTo == null) {
            return;
        }
        if (enTo == null && !enValTo.isBefore(enFrom)) {
            return;
        }
        if (enValTo == null && !enTo.isBefore(enValFrom)) {
            return;
        }
        if (enTo != null && enValTo != null && !enValTo.isBefore(enFrom) && !enTo.isBefore(enValFrom)) {
            return;
        }
        throw new CommonException(HttpStatus.BAD_REQUEST, PLATNOST_HODNOTY_CISELNIKA_SA_NEPREKRYVA_S_PLATNOSTOU_ZIADNEJ_VERZIE_CISELNIKA, null);
    }

    public List<HodnotaCiselnikaShortDetail> findByParameter(String kodCiselnika, String search, String requestFilterParam) {

        if (!ciselnikRepository.existsByKodCiselnikaAndDeletedIsFalse(kodCiselnika)) {
            throw new CommonException(HttpStatus.BAD_REQUEST, String.format(ERROR_EXISTS_ENUMERATION_CODE, kodCiselnika), null);
        }

        HodnotaCiselnikaListRequest listRequest = new HodnotaCiselnikaListRequest();
        listRequest.setLimit(searchLimit);
        listRequest.setPage(0);
        listRequest.setSort(NAZOV_POLOZKY);
        HodnotaCiselnikaRequestFilter requestFilter = new HodnotaCiselnikaRequestFilter();
        listRequest.setFilter(requestFilter);
        requestFilter.setKodCiselnikaEQUAL(kodCiselnika);

        if (requestFilterParam.equalsIgnoreCase(NAZOV_POLOZKY)) {
            requestFilter.setNazovPolozky(search);
        } else if (requestFilterParam.equalsIgnoreCase(DODATOCNY_OBSAH)) {
            requestFilter.setDodatocnyObsah(search);
        }

        HodnotaCiselnikaList hodnotaCiselnikaList = filter(listRequest);
        return hodnotaCiselnikaList.getResult().stream().map(simpleOutput -> modelMapper.map(simpleOutput, HodnotaCiselnikaShortDetail.class)).toList();
    }

    @Transactional
    public HodnotaCiselnika findByNazovPolozky(String kodPolozky, String nazovPolozky) {
        return hodnotaCiselnikaRepository.findByKodPolozkyAndNazovPolozky(kodPolozky, nazovPolozky).orElse(null);
    }

    @Transactional
    public HodnotaCiselnika findByKodPolozkyAndKodCiselnika(String kodPolozky, String kodCiselnika) {
        return hodnotaCiselnikaRepository.findByKodPolozkyAndKodCiselnika(kodPolozky, kodCiselnika).orElse(null);
    }
}
