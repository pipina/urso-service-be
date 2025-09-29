package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets typHodnotyEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public enum TypHodnotyEnum {
  
  STRING("STRING"),
  
  DATE("DATE"),
  
  TIME("TIME"),
  
  DATETIME("DATETIME"),
  
  NUMBER("NUMBER");

  private String value;

  TypHodnotyEnum(String value) {
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
  public static TypHodnotyEnum fromValue(String value) {
    for (TypHodnotyEnum b : TypHodnotyEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

