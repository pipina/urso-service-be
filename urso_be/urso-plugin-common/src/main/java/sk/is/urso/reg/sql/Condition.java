package sk.is.urso.reg.sql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Condition implements ConditionIHasParameters {
	@NonNull
	public final ValueIHasParameters left;
	public final ValueIHasParameters right;
	
	@Override
	public Object[] getParameters() {
		return getParameters(left, right);
	}
}
