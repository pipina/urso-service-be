package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

/**
 * Reprezentuje tabuľku prípadne jej alias. 
 * Toto je final trieda. 
 * Iné objekty ktoré majú tiež názov nemôžu dediť od {@link Alias} pretože to prekrýva možnosť použitia {@link #toString()}
 *
 */
@RequiredArgsConstructor
public final class Alias implements IsAliasIHasParameters {
	public final Class<?> entity;
	public final String table;
	public final String alias;
	
	public Alias(Alias alias) {
		this.entity = alias.entity;
		this.table = alias.table;
		this.alias = alias.alias;
	}
	
	@Override
	public Alias asAlias() {
		return this;
	}
	
	@Override
	public String toString() {
		return " " + table + " as " + alias + " ";
	}

	public String field(String sourceId) {
		return this.alias + "." + sourceId;
	}

	@Override
	public Object[] getParameters() {
		return NOPARAMS;
	}
}
