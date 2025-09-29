package sk.is.urso.service;

import org.alfa.converter.AbstractEntityConverter;
import org.alfa.converter.NotUsed;
import org.alfa.model.common.IId;
import org.alfa.repository.EntityRepository;
import org.alfa.service.AbstractModelService;

public abstract class BaseModelService<I, O, L, SO, LR, RF, E extends IId<ID>, ID> extends AbstractModelService<I, O, L, SO, LR, RF, NotUsed, NotUsed, NotUsed, E, ID> {

    public BaseModelService(EntityRepository<E, ID> entityRepository,
                            AbstractEntityConverter<I, O, L, SO, NotUsed, NotUsed, E> entityConverter) {
        super(entityRepository, entityConverter);
    }

}
