package sk.is.urso.controller;

import org.alfa.controller.AbstractController;
import org.alfa.converter.NotUsed;
import org.alfa.model.common.IId;
import org.alfa.service.AbstractModelService;

public abstract class BaseController<I, O, L, SO, LR, RF, E extends IId<ID>, ID> extends AbstractController<I, O, L, SO, LR, RF, NotUsed, NotUsed, NotUsed, E, ID> {

    public BaseController(AbstractModelService<I, O, L, SO, LR, RF, NotUsed, NotUsed, NotUsed, E, ID> modelService) {
        super(modelService);
    }

}