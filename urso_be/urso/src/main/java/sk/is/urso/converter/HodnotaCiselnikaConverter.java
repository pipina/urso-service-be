package sk.is.urso.converter;

import org.alfa.converter.NotUsed;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;
import sk.is.urso.model.ciselniky.HodnotaCiselnika;
import sk.is.urso.rest.model.HodnotaCiselnikaInputDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaList;
import sk.is.urso.rest.model.HodnotaCiselnikaOutputDetail;
import sk.is.urso.rest.model.HodnotaCiselnikaSimpleOutput;

@Component
public class HodnotaCiselnikaConverter extends BaseEntityConverter<
        HodnotaCiselnikaInputDetail,
        HodnotaCiselnikaOutputDetail,
        HodnotaCiselnikaList,
        HodnotaCiselnikaSimpleOutput,
        HodnotaCiselnika> {

    @Override
    protected void configureEntityToEntity(TypeMap<HodnotaCiselnika, HodnotaCiselnika> entityToEntityTypeMap) {
        entityToEntityTypeMap.addMappings(mapper -> mapper.using(MappingContext::getSource).map(HodnotaCiselnika::getNadradenaHodnotaCiselnika, HodnotaCiselnika::setNadradenaHodnotaCiselnika));
    }

    @Override
    protected void configureInputToEntity(TypeMap<HodnotaCiselnikaInputDetail, HodnotaCiselnika> inputToEntityTypeMap) {
        inputToEntityTypeMap.addMappings(mapper -> mapper.skip(HodnotaCiselnika::setNadradenaHodnotaCiselnika));
        inputToEntityTypeMap.addMappings(mapper -> mapper.skip(HodnotaCiselnika::setCiselnik));
        inputToEntityTypeMap.addMappings(mapper -> mapper.skip(HodnotaCiselnika::setKodCiselnika));
    }

    @Override
    protected void configureEntityToOutput(TypeMap<HodnotaCiselnika, HodnotaCiselnikaOutputDetail> entityToOutputTypeMap) {
        entityToOutputTypeMap.addMappings(mapper -> mapper.map(src -> src.getNadradenaHodnotaCiselnika().getKodPolozky(), HodnotaCiselnikaOutputDetail::setNadradenaHodnotaCiselnikaKodPolozky));
    }

    @Override
    protected void configureEntityToShortDetail(TypeMap<HodnotaCiselnika, NotUsed> entityToShortDetailTypeMap) {

    }

    @Override
    protected void configureEntityToSimpleOutput(TypeMap<HodnotaCiselnika, HodnotaCiselnikaSimpleOutput> entityToSimpleOutputTypeMap) {

    }
}
