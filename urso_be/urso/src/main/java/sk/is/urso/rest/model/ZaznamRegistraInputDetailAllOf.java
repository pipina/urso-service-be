package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ZaznamRegistraInputDetailAllOf
 */

@JsonTypeName("zaznamRegistraInputDetail_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraInputDetailAllOf {

  @JsonProperty("data")
  private String data;

  @JsonProperty("modul")
  private String modul;

  @JsonProperty("pouzivatel")
  private String pouzivatel;

  public ZaznamRegistraInputDetailAllOf data(String data) {
    this.data = data;
    return this;
  }

  /**
   * Get data
   * @return data
  */
  @NotNull 
  @Schema(name = "data", required = true)
  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public ZaznamRegistraInputDetailAllOf modul(String modul) {
    this.modul = modul;
    return this;
  }

  /**
   * Get modul
   * @return modul
  */
  @Size(min = 2, max = 8) 
  @Schema(name = "modul", required = false)
  public String getModul() {
    return modul;
  }

  public void setModul(String modul) {
    this.modul = modul;
  }

  public ZaznamRegistraInputDetailAllOf pouzivatel(String pouzivatel) {
    this.pouzivatel = pouzivatel;
    return this;
  }

  /**
   * Get pouzivatel
   * @return pouzivatel
  */
  @Size(max = 256) 
  @Schema(name = "pouzivatel", required = false)
  public String getPouzivatel() {
    return pouzivatel;
  }

  public void setPouzivatel(String pouzivatel) {
    this.pouzivatel = pouzivatel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraInputDetailAllOf zaznamRegistraInputDetailAllOf = (ZaznamRegistraInputDetailAllOf) o;
    return Objects.equals(this.data, zaznamRegistraInputDetailAllOf.data) &&
        Objects.equals(this.modul, zaznamRegistraInputDetailAllOf.modul) &&
        Objects.equals(this.pouzivatel, zaznamRegistraInputDetailAllOf.pouzivatel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, modul, pouzivatel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraInputDetailAllOf {\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    modul: ").append(toIndentedString(modul)).append("\n");
    sb.append("    pouzivatel: ").append(toIndentedString(pouzivatel)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

