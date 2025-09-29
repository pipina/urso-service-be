package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubSelect implements QueryIHasParameters, IsAliasIHasParameters {

	public final Sql sql;
	public final Alias alias;
	
	@Override
	public Alias asAlias() {
		return alias;
	}
	
	@Override
	public String toString() {
		return " ( " + sql.toString() + " ) as " + alias.alias;
	}

	@Override
	public Object[] getParameters() {
		return sql.getParameters();
	}

	@Override
	public String getSql() {
		return toString();
	}
}
