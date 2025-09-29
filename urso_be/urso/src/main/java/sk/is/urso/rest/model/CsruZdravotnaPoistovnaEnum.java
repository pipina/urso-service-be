package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruZdravotnaPoistovnaEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-27T11:49:41.895354300+01:00[Europe/Berlin]")
public enum CsruZdravotnaPoistovnaEnum {
  
  DOVERA("DOVERA"),
  
  UNION("UNION"),

  VSEOBECNA_ZDRAVOTNA("VSEOBECNA_ZDRAVOTNA"),
  
  NEZNAMA("NEZNAMA");

  private String value;

  CsruZdravotnaPoistovnaEnum(String value) {
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
  public static CsruZdravotnaPoistovnaEnum fromValue(String value) {
    for (CsruZdravotnaPoistovnaEnum b : CsruZdravotnaPoistovnaEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

