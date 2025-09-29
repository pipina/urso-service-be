package sk.is.urso.service;

import org.alfa.converter.NotUsed;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sk.is.urso.model.csru.ra.RaAddressSearch;
import sk.is.urso.repository.csru.ra.RaSearchRepository;
import sk.is.urso.converter.SearchConverter;
import sk.is.urso.rest.model.SearchList;
import sk.is.urso.rest.model.SearchListRequest;
import sk.is.urso.rest.model.SearchRequestFilter;
import sk.is.urso.rest.model.SearchSimpleOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SearchService extends BaseModelService<
        NotUsed,
        NotUsed,
        SearchList,
        SearchSimpleOutput,
        SearchListRequest,
        SearchRequestFilter,
        RaAddressSearch,
        Long> {

    @Value("${address.search.limit}")
    private int limit;

    private static final String ERROR_SHORT_LIST = "Chyba pri načítaní zoznamu.";
    private static final String ERROR_SEARCH_ADDRESS_FILTER = "Chyba pri načítaní filtrovaného zoznamu adries.";
    private static final String ERROR_INVALID_SEARCH_ADDRESS_ID = "Adresa s ID '%s' neexistuje.";

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public SearchService(RaSearchRepository entityRepository, SearchConverter entityConverter) {
        super(entityRepository, entityConverter);
    }

    @Override
    protected String getErrorShortList() {
        return ERROR_SHORT_LIST;
    }

    @Override
    protected void checkCreate(NotUsed input) {

    }

    @Override
    protected void checkUpdate(Long aLong, NotUsed input) {

    }

    @Override
    protected void checkDelete(Long aLong) {

    }

    @Override
    protected void checkGet(Long aLong) {

    }

    @Override
    protected void postConvert(NotUsed input, RaAddressSearch entity) {

    }

    @Override
    protected void checkPostGet(RaAddressSearch entity) {

    }

    @Override
    protected String getIdName() {
        return RaAddressSearch.Fields.id;
    }

    @Override
    protected Optional<String> getDeletedName() {
        return Optional.empty();
    }

    @Override
    protected String[] getSupportedSortValues() {
        return new String[]{
                RaAddressSearch.Fields.id
        };
    }

    @Override
    protected String getErrorInvalidEntityId(Long id) {
        return String.format(ERROR_INVALID_SEARCH_ADDRESS_ID, id);
    }

    @Override
    protected String getErrorFilter() {
        return ERROR_SEARCH_ADDRESS_FILTER;
    }

    @Override
    protected void checkFilter(SearchListRequest listRequest) {

    }
}
