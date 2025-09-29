package sk.is.urso.reg.sql;

import java.util.List;

public class In extends ConditionMultipleValues {

	public In(Alias alias, String name, String ... values) {
		super(alias, name, values);
	}
	
	public In(Alias alias, String name, List<String> values) {
		super(alias, name, values.toArray(new String[values.size()]));
	}
	
	@Override
	public String toString() {
		return " " + alias.field(name) + " in (" + paramPlaceholder() + ") ";
	}

}
