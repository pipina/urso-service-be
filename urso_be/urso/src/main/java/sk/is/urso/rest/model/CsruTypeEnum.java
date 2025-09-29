package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Gets or Sets csruTypeEnum
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-13T11:04:24.037635800+01:00[Europe/Berlin]")
public enum CsruTypeEnum {
  
  RA("RA"),
  
  RFO("RFO"),
  
  RPO("RPO"),
  
  ZC("ZC"),
  
  SU_FIX("SU_FIX"),
  
  SFA_FIX("SFA_FIX"),
  
  SU_DUPL("SU_DUPL"),
  
  SU_DUPL_TZS("SU_DUPL_TZS"),
  
  RPO_REF_FIX("RPO_REF_FIX"),
  
  SUBJECT_REFRESH("SUBJECT_REFRESH"),
  
  SUBJECT_RPO_REFRESH("SUBJECT_RPO_REFRESH"),
  
  SUBJECT_RFO_REFRESH("SUBJECT_RFO_REFRESH");

  private String value;

  CsruTypeEnum(String value) {
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
  public static CsruTypeEnum fromValue(String value) {
    for (CsruTypeEnum b : CsruTypeEnum.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

