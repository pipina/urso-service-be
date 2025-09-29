package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruNedoplatokEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-27T11:49:41.895354300+01:00[Europe/Berlin]")
public enum CsruNedoplatokEnum {
  
  MA_NEDOPLATOK("MA_NEDOPLATOK"),
  
  NEMA_NEDOPLATOK("NEMA_NEDOPLATOK"),
  
  MA_NEDOPLATOK_NIE_JE_MOZNE_VYCISLIT("MA_NEDOPLATOK_NIE_JE_MOZNE_VYCISLIT"),
  
  MA_NEDOPLATOK_NESPLNENIE_POVINNOSTI("MA_NEDOPLATOK_NESPLNENIE_POVINNOSTI"),
  
  NIE_JE_V_EVIDENCII("NIE_JE_V_EVIDENCII"),
  
  NEKOMPLETNE_DATA_TECHNICKA_CHYBA("NEKOMPLETNE_DATA_TECHNICKA_CHYBA"),
  
  NEIDENTIFIKOVANA_OSOBA("NEIDENTIFIKOVANA_OSOBA"),
  
  CHYBA("CHYBA"),
  
  NEZNAMY("NEZNAMY");

  private String value;

  CsruNedoplatokEnum(String value) {
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
  public static CsruNedoplatokEnum fromValue(String value) {
    for (CsruNedoplatokEnum b : CsruNedoplatokEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

