package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets udalostKategoriaEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public enum UdalostKategoriaEnum {
  
  CREATE("create"),
  
  READ("read"),
  
  UPDATE("update"),
  
  DELETE("delete"),
  
  IMPORT("import"),
  
  EXPORT("export"),
  
  IDENTIFICATION("identification"),
  
  REFERENCE_INCREMENT("reference_increment"),
  
  REFERENCE_DECREMENT("reference_decrement");

  private String value;

  UdalostKategoriaEnum(String value) {
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
  public static UdalostKategoriaEnum fromValue(String value) {
    for (UdalostKategoriaEnum b : UdalostKategoriaEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

