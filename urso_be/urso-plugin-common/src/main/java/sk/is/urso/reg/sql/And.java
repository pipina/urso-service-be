package sk.is.urso.reg.sql;

import java.util.List;

public class And extends CompositeCondition {

	public And(ConditionIHasParameters c1, ConditionIHasParameters c2) {
		super(c1, c2);
	}

	public And(ConditionIHasParameters[] conditions) {
		super(conditions);
	}

	public And(List<ConditionIHasParameters> conditions) {
		super(conditions);
	}

	@Override
	public String toString() {
		if(this.conditions.length == 1) {//sprehladnenie generovaneho kodu
			return conditions[0].toString();
		}
		var first = true;
		var sb = new StringBuilder();
		sb.append(" ( ");
		for(ConditionIHasParameters condition : conditions) {
			if(!first) {
				sb.append(" and ");
			}
			first = false;
			sb.append(condition.toString());
		}
		sb.append(" ) ");
		return sb.toString();
	}
}
