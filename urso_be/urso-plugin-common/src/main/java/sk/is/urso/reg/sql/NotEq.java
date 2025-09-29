package sk.is.urso.reg.sql;

public class NotEq extends Condition {

	public NotEq(Alias alias, String name, Object value) {
		super(new Field(alias, name), new Value(value));
	}
	
	@Override
	public String toString() {
		return left + " <> " + right;
	}

}
