package sk.is.urso.converter;

import org.alfa.converter.NotUsed;
import org.modelmapper.Converter;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;
import sk.is.urso.model.ciselniky.Ciselnik;
import sk.is.urso.rest.model.CiselnikInputDetail;
import sk.is.urso.rest.model.CiselnikList;
import sk.is.urso.rest.model.CiselnikOutputDetail;
import sk.is.urso.rest.model.CiselnikSimpleOutput;

@Component
public class CiselnikConverter extends BaseEntityConverter<
        CiselnikInputDetail,
        CiselnikOutputDetail,
        CiselnikList,
        CiselnikSimpleOutput,
        Ciselnik> {

    @Override
    protected void configureEntityToEntity(TypeMap<Ciselnik, Ciselnik> entityToEntityTypeMap) {
        entityToEntityTypeMap.addMappings(mapper -> mapper.using(updateVerzia).map(Ciselnik::getVerzia, Ciselnik::setVerzia));
    }

    @Override
    protected void configureInputToEntity(TypeMap<CiselnikInputDetail, Ciselnik> inputToEntityTypeMap) {
        inputToEntityTypeMap.addMappings(mapper -> mapper.skip(Ciselnik::setVerzia));
    }

    @Override
    protected void configureEntityToOutput(TypeMap<Ciselnik, CiselnikOutputDetail> entityToOutputTypeMap) {

    }

    @Override
    protected void configureEntityToShortDetail(TypeMap<Ciselnik, NotUsed> entityToShortDetailTypeMap) {

    }

    @Override
    protected void configureEntityToSimpleOutput(TypeMap<Ciselnik, CiselnikSimpleOutput> entityToSimpleOutputTypeMap) {

    }

    private Converter<Integer, Integer> updateVerzia = mappingContext -> {
        Integer dVersion = mappingContext.getDestination();
        return dVersion + 1;
    };
}
