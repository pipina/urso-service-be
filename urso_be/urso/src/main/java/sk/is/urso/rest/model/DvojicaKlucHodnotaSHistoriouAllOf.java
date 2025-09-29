package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import java.util.Objects;

/**
 * DvojicaKlucHodnotaSHistoriouAllOf
 */

@JsonTypeName("dvojicaKlucHodnotaSHistoriou_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class DvojicaKlucHodnotaSHistoriouAllOf {

  @JsonProperty("nazovZobrazenia")
  private String nazovZobrazenia;

  @JsonProperty("kontext")
  private String kontext;

  public DvojicaKlucHodnotaSHistoriouAllOf nazovZobrazenia(String nazovZobrazenia) {
    this.nazovZobrazenia = nazovZobrazenia;
    return this;
  }

  /**
   * Get nazovZobrazenia
   * @return nazovZobrazenia
  */
  
  @Schema(name = "nazovZobrazenia", required = false)
  public String getNazovZobrazenia() {
    return nazovZobrazenia;
  }

  public void setNazovZobrazenia(String nazovZobrazenia) {
    this.nazovZobrazenia = nazovZobrazenia;
  }

  public DvojicaKlucHodnotaSHistoriouAllOf kontext(String kontext) {
    this.kontext = kontext;
    return this;
  }

  /**
   * Get kontext
   * @return kontext
  */
  
  @Schema(name = "kontext", required = false)
  public String getKontext() {
    return kontext;
  }

  public void setKontext(String kontext) {
    this.kontext = kontext;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DvojicaKlucHodnotaSHistoriouAllOf dvojicaKlucHodnotaSHistoriouAllOf = (DvojicaKlucHodnotaSHistoriouAllOf) o;
    return Objects.equals(this.nazovZobrazenia, dvojicaKlucHodnotaSHistoriouAllOf.nazovZobrazenia) &&
        Objects.equals(this.kontext, dvojicaKlucHodnotaSHistoriouAllOf.kontext);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nazovZobrazenia, kontext);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DvojicaKlucHodnotaSHistoriouAllOf {\n");
    sb.append("    nazovZobrazenia: ").append(toIndentedString(nazovZobrazenia)).append("\n");
    sb.append("    kontext: ").append(toIndentedString(kontext)).append("\n");
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

