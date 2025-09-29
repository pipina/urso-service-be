package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ConditionMultipleValues implements ConditionIHasParameters {
	public final Alias alias;
	public final String name; 
	public final Object[] values;
	
	public String paramPlaceholder() {
		return " ? "+ Arrays.stream(values, 1, values.length).map(v -> " ,? ").collect(Collectors.joining());
	}
	
	@Override
	public Object[] getParameters() {
		return this.values;
	}
}
