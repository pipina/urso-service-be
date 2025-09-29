package sk.is.urso.reg.sql;

import lombok.RequiredArgsConstructor;

/**
 * Join ktory este nema ziadne on. Je to vytvorene aby sme pri joine nemohli zabudnut na podmienku
 *
 */
@RequiredArgsConstructor
public class JoinIncomplete {
	private final Join join;

	public Join onFields(String sourceField, String targetField) {
		return join.onFields(sourceField, targetField);
	}
}
