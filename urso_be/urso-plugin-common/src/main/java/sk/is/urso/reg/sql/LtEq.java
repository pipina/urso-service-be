package sk.is.urso.reg.sql;

public class LtEq extends Condition {

	public LtEq(Alias alias, String name, Object value) {
		super(new Field(alias, name), new Value(value));
	}
	
	@Override
	public String toString() {
		return left + " <= " + right;
	}

}
