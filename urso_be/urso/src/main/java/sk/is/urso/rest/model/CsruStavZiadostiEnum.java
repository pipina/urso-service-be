package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruStavZiadostiEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-13T11:04:24.037635800+01:00[Europe/Berlin]")
public enum CsruStavZiadostiEnum {
  
  PREBIEHA_SPRACOVANIE("PREBIEHA_SPRACOVANIE"),
  
  SPRACOVANIE_USPESNE_UKONCENE("SPRACOVANIE_USPESNE_UKONCENE"),
  
  SPRACOVANIE_UKONCENE_S_CHYBOU("SPRACOVANIE_UKONCENE_S_CHYBOU"),
  
  NEZNAMA_POZIADAVKA("NEZNAMA_POZIADAVKA"),
  
  SPRACOVANIE_UKONCENE_S_UPOZORNENIM("SPRACOVANIE_UKONCENE_S_UPOZORNENIM"),
  
  NEZNAMY("NEZNAMY");

  private String value;

  CsruStavZiadostiEnum(String value) {
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
  public static CsruStavZiadostiEnum fromValue(String value) {
    for (CsruStavZiadostiEnum b : CsruStavZiadostiEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

