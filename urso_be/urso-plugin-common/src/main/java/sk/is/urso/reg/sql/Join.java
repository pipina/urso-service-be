package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Join implements IHasParameters {

	private final List<Eq> on = new ArrayList<>();
	public final Alias source;
	public final Alias target;
	public final String type;

	public Join onFields(String sourceField, String targetField) {
		var eq = new Eq(source, sourceField, target, targetField);
		this.on.add(eq);
		return this;
	}
	
	public Join onSourceValue(String field, String value) {
		var eq = new Eq(source, field, value);
		this.on.add(eq);
		return this;
	}
	
	public Join onTargetValue(String field, String value) {
		var eq = new Eq(target, field, value);
		this.on.add(eq);
		return this;
	}
	
	@Override
	public String toString() {
		var sb = new StringBuilder();
		sb.append(" " + type + " ");
		sb.append(" join ");
		sb.append(target.toString());
		var first = true;
		for(Eq eq : this.on) {
			if(first) {
				sb.append(" on ");
			}
			else {
				sb.append(" and ");
			}
			first = false;
			sb.append(eq.toString());
		}
		sb.append(" ");
		return sb.toString();
	}

	@Override
	public Object[] getParameters() {
		return getParameters(this.on);
	}

}
