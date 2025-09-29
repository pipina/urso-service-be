package sk.is.urso.converter;

import org.alfa.converter.NotUsed;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;
import sk.is.urso.model.csru.ra.RaAddressSearch;
import sk.is.urso.rest.model.SearchList;
import sk.is.urso.rest.model.SearchSimpleOutput;

@Component
public class SearchConverter extends BaseEntityConverter<
        NotUsed,
        NotUsed,
        SearchList,
        SearchSimpleOutput,
        RaAddressSearch> {

    @Override
    protected void configureEntityToEntity(TypeMap<RaAddressSearch, RaAddressSearch> entityToEntityTypeMap) {

    }

    @Override
    protected void configureInputToEntity(TypeMap<NotUsed, RaAddressSearch> inputToEntityTypeMap) {

    }

    @Override
    protected void configureEntityToOutput(TypeMap<RaAddressSearch, NotUsed> entityToOutputTypeMap) {

    }

    @Override
    protected void configureEntityToShortDetail(TypeMap<RaAddressSearch, NotUsed> entityToShortDetailTypeMap) {

    }

    @Override
    protected void configureEntityToSimpleOutput(TypeMap<RaAddressSearch, SearchSimpleOutput> entityToSimpleOutputTypeMap) {
        entityToSimpleOutputTypeMap.addMappings(mapper -> mapper.map(RaAddressSearch::getFulltextSearch, SearchSimpleOutput::setDisplayValue));
    }
}
