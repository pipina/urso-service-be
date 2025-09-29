package sk.is.urso.reg.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IHasParameters {
	public static final Object[] NOPARAMS = new Object[] {};
	
	Object[] getParameters();
	
	public default Object[] getParameters(IHasParameters... hasParams) {
		List<Object> params = new ArrayList<>();
		for(IHasParameters condition : hasParams) {
			if (condition != null) {
				Collections.addAll(params, condition.getParameters());
			}
		}
		return params.toArray(new Object[params.size()]);
	}
	
	public default Object[] getParameters(Iterable<? extends IHasParameters> hasParams) {
		List<Object> params = new ArrayList<>();
		for(IHasParameters condition : hasParams) {
			Collections.addAll(params, condition.getParameters());
		}
		return params.toArray(new Object[params.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public default Object[] getParameters(Iterable<? extends IHasParameters> ... hasParamsIterables) {
		List<Object> params = new ArrayList<>();
		for(Iterable<? extends IHasParameters> hasParams : hasParamsIterables) {
			for(IHasParameters condition : hasParams) {
				Collections.addAll(params, condition.getParameters());
			}
		}
		return params.toArray(new Object[params.size()]);
	}
}
