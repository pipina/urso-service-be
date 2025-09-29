package sk.is.urso.reg.sql;

public interface IsAliasIHasParameters extends IHasParameters {

	/**
	 * Return representation of current class as {@link Alias}
	 * This can create new {@link Alias} instance
	 * @return
	 */
	public Alias asAlias();
}
