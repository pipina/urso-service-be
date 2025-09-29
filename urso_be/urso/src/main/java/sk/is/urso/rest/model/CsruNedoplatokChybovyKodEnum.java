package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruNedoplatokChybovyKodEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-13T11:04:24.037635800+01:00[Europe/Berlin]")
public enum CsruNedoplatokChybovyKodEnum {
  
  OK("OK"),
  
  SUBJEKT_MA_DUPLICITU_V_EVIDENCII("SUBJEKT_MA_DUPLICITU_V_EVIDENCII"),
  
  NEZNAMY("NEZNAMY");

  private String value;

  CsruNedoplatokChybovyKodEnum(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static CsruNedoplatokChybovyKodEnum fromValue(String value) {
    for (CsruNedoplatokChybovyKodEnum b : CsruNedoplatokChybovyKodEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

