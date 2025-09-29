package sk.is.urso.reg.sql;

public interface ConditionIHasParameters extends IHasParameters {

	default ConditionIHasParameters not() {
		return new Not(this);
	}
}
