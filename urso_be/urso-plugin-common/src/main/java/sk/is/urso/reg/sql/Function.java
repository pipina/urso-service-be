package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Function implements ValueIHasParameters {

	public final String functionName;
	public final ValueIHasParameters functionParam;
	
	@Override
	public String toString() {
		return functionName + "( " + functionParam.toString() + " ) ";
	}
	@Override
	public Object[] getParameters() {
		return functionParam.getParameters();
	}

}
