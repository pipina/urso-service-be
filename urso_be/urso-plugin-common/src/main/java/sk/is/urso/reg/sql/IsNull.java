package sk.is.urso.reg.sql;

public class IsNull extends Condition {

	public IsNull(Alias alias, String name) {
		super(new Field(alias, name), null);
	}
	
	@Override
	public String toString() {
		return left + " is null ";
	}

}
