package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Not implements ConditionIHasParameters {
	public final ConditionIHasParameters condition;
	
	@Override
	public String toString() {
		return " not ( " + condition.toString() + " ) ";
	}
	@Override
	public Object[] getParameters() {
		return condition.getParameters();
	}
}
