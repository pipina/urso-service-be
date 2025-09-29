package sk.is.urso.reg.sql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Field implements ValueIHasParameters {
	public final Alias alias;
	@NonNull
	public final String field;//NOSONAR

	public String cast;

	public Field(Alias alias, String name, String cast) {
		this.alias = alias;
		this.field = name;
		this.cast = cast;
	}

	@Override
	public String toString() {
		if(alias == null) {
			return field;
		}
		if (cast != null) {
			return "CAST( " + alias.alias + "." + field + " AS " + cast + ")";
		}
		return " " + alias.alias + "." + field;
	}

	@Override
	public Object[] getParameters() {
		return NOPARAMS;
	}
}
