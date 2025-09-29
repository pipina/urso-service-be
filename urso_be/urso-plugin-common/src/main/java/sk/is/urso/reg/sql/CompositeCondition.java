package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CompositeCondition implements ConditionIHasParameters {

	public final ConditionIHasParameters[] conditions;
	
	public CompositeCondition(ConditionIHasParameters c1, ConditionIHasParameters c2) {
		this.conditions = new ConditionIHasParameters[] {c1, c2};
	}
	
	public CompositeCondition(List<? extends ConditionIHasParameters> conditions) {
		this.conditions = conditions.toArray(new ConditionIHasParameters[conditions.size()]);
	}
	
	@Override
	public Object[] getParameters() {
		return getParameters(this.conditions);
	}

}
