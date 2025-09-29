package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets instituciaEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-10-26T09:59:21.066787900+02:00[Europe/Berlin]")
public enum InstituciaEnum {
  
  FS("FS"),
  
  ZP("ZP"),
  
  SP("SP");

  private String value;

  InstituciaEnum(String value) {
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
  public static InstituciaEnum fromValue(String value) {
    for (InstituciaEnum b : InstituciaEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

