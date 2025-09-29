package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruVysledokSpracovaniaEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T12:19:12.206284600+01:00[Europe/Bratislava]")
public enum CsruVysledokSpracovaniaEnum {
  
  ZASLANE_UDAJE_OD_ZP("ZASLANE_UDAJE_OD_ZP"),
  
  NIE_JE_EVIDOVANY("NIE_JE_EVIDOVANY"),
  
  NIE_SU_EVIDOVANE_ZIADNE_UDAJE("NIE_SU_EVIDOVANE_ZIADNE_UDAJE"),
  
  NESULAD_SUBJEKTU_V_OBALKE_A_OBSAHU("NESULAD_SUBJEKTU_V_OBALKE_A_OBSAHU"),
  
  NESULAD_RC_ICO_IFO("NESULAD_RC_ICO_IFO"),
  
  NEEVIDOVANIY_PARTNER_PRE_POSKYTNUTIE_UDAJOV("NEEVIDOVANIY_PARTNER_PRE_POSKYTNUTIE_UDAJOV"),
  
  NEZNAMY("NEZNAMY");

  private String value;

  CsruVysledokSpracovaniaEnum(String value) {
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
  public static CsruVysledokSpracovaniaEnum fromValue(String value) {
    for (CsruVysledokSpracovaniaEnum b : CsruVysledokSpracovaniaEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

