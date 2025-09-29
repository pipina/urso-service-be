package sk.is.urso.reg.sql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Value implements ValueIHasParameters {

	@NonNull
	public final Object value;//NOSONAR
	
	@Override
	public String toString() {
		return " ? ";
	}

	@Override
	public Object[] getParameters() {
		return new Object[] {this.value};
	}
}
