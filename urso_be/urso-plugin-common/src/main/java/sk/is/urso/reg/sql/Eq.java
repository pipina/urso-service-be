package sk.is.urso.reg.sql;

public class Eq extends Condition {

	public Eq(Alias alias, String name, Object value) {
		super(new Field(alias, name), new Value(value));
	}
	public Eq(Alias alias, String name, String cast, Object value) {
		super(new Field(alias, name, cast), new Value(value));
	}
	public Eq(Alias source, String sourceField, Alias target, String targetField) {
		super(new Field(source, sourceField), new Field(target, targetField));
	}

	@Override
	public String toString() {
		return left + " = " + right;
	}

}
