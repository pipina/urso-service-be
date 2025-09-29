package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruDruhDaneAleboPohladavkyEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-13T11:04:24.037635800+01:00[Europe/Berlin]")
public enum CsruDruhDaneAleboPohladavkyEnum {
  
  NDS("NDS"),
  
  SPD("SPD"),
  
  COL("COL"),
  
  NEZNAMY("NEZNAMY");

  private String value;

  CsruDruhDaneAleboPohladavkyEnum(String value) {
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
  public static CsruDruhDaneAleboPohladavkyEnum fromValue(String value) {
    for (CsruDruhDaneAleboPohladavkyEnum b : CsruDruhDaneAleboPohladavkyEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

