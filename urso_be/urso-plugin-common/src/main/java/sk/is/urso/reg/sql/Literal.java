package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Literal implements ValueIHasParameters {

	public final String value;
	
	@Override
	public String toString() {
		return " " + value + " ";
	}
	
	@Override
	public Object[] getParameters() {
		return NOPARAMS;
	}

}
