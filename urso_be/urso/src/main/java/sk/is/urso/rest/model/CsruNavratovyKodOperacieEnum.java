package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruNavratovyKodOperacieEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-13T11:04:24.037635800+01:00[Europe/Berlin]")
public enum CsruNavratovyKodOperacieEnum {
  
  OK("OK"),
  
  CHYBA_OVERENIA_OPRAVNENI("CHYBA_OVERENIA_OPRAVNENI"),
  
  CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV("CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV"),
  
  INTERNA_CHYBA("INTERNA_CHYBA"),
  
  NEPLATNE_ID_POZIADAVKY("NEPLATNE_ID_POZIADAVKY"),
  
  NEZNAMY("NEZNAMY");

  private String value;

  CsruNavratovyKodOperacieEnum(String value) {
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
  public static CsruNavratovyKodOperacieEnum fromValue(String value) {
    for (CsruNavratovyKodOperacieEnum b : CsruNavratovyKodOperacieEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

