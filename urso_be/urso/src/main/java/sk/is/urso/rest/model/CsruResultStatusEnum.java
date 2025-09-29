package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruResultStatusEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-13T11:04:24.037635800+01:00[Europe/Berlin]")
public enum CsruResultStatusEnum {
  
  OK("OK"),
  
  ERROR("ERROR");

  private String value;

  CsruResultStatusEnum(String value) {
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
  public static CsruResultStatusEnum fromValue(String value) {
    for (CsruResultStatusEnum b : CsruResultStatusEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

