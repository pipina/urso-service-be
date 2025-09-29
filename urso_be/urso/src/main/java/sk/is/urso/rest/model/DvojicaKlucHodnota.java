package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * DvojicaKlucHodnota
 */

@JsonTypeName("dvojicaKlucHodnota")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class DvojicaKlucHodnota {

  @JsonProperty("kluc")
  private String kluc;

  @JsonProperty("hodnota")
  private String hodnota;

  public DvojicaKlucHodnota kluc(String kluc) {
    this.kluc = kluc;
    return this;
  }

  /**
   * Get kluc
   * @return kluc
  */
  @NotNull 
  @Schema(name = "kluc", required = true)
  public String getKluc() {
    return kluc;
  }

  public void setKluc(String kluc) {
    this.kluc = kluc;
  }

  public DvojicaKlucHodnota hodnota(String hodnota) {
    this.hodnota = hodnota;
    return this;
  }

  /**
   * Get hodnota
   * @return hodnota
  */
  @NotNull 
  @Schema(name = "hodnota", required = true)
  public String getHodnota() {
    return hodnota;
  }

  public void setHodnota(String hodnota) {
    this.hodnota = hodnota;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DvojicaKlucHodnota dvojicaKlucHodnota = (DvojicaKlucHodnota) o;
    return Objects.equals(this.kluc, dvojicaKlucHodnota.kluc) &&
        Objects.equals(this.hodnota, dvojicaKlucHodnota.hodnota);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kluc, hodnota);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DvojicaKlucHodnota {\n");
    sb.append("    kluc: ").append(toIndentedString(kluc)).append("\n");
    sb.append("    hodnota: ").append(toIndentedString(hodnota)).append("\n");
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

