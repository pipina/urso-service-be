package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ZaznamRegistraXPathZmena
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraXPathZmena {

  @JsonProperty("staryPlatny")
  private Boolean staryPlatny = true;

  @JsonProperty("staryAktualny")
  private Boolean staryAktualny = false;

  @JsonProperty("zmeny")
  @Valid
  private List<ZaznamRegistraXPathDataUpdate> zmeny = new ArrayList<>();

  public ZaznamRegistraXPathZmena staryPlatny(Boolean staryPlatny) {
    this.staryPlatny = staryPlatny;
    return this;
  }

  /**
   * Ak true (default hodnota) tak stara historicka hodnota je oznacena za effectiveTo=dnes Ak false tak stara historicka hodnota je oznacena valid=false
   * @return staryPlatny
  */
  
  @Schema(name = "staryPlatny", description = "Ak true (default hodnota) tak stara historicka hodnota je oznacena za effectiveTo=dnes Ak false tak stara historicka hodnota je oznacena valid=false", required = false)
  public Boolean getStaryPlatny() {
    return staryPlatny;
  }

  public void setStaryPlatny(Boolean staryPlatny) {
    this.staryPlatny = staryPlatny;
  }

  public ZaznamRegistraXPathZmena staryAktualny(Boolean staryAktualny) {
    this.staryAktualny = staryAktualny;
    return this;
  }

  /**
   * Ak false (default hodnota) tak stara historicka hodnota je oznacena current=false Ak true tak stara historicka hodnota je oznacena za current=true
   * @return staryAktualny
  */
  
  @Schema(name = "staryAktualny", description = "Ak false (default hodnota) tak stara historicka hodnota je oznacena current=false Ak true tak stara historicka hodnota je oznacena za current=true", required = false)
  public Boolean getStaryAktualny() {
    return staryAktualny;
  }

  public void setStaryAktualny(Boolean staryAktualny) {
    this.staryAktualny = staryAktualny;
  }

  public ZaznamRegistraXPathZmena zmeny(List<ZaznamRegistraXPathDataUpdate> zmeny) {
    this.zmeny = zmeny;
    return this;
  }

  public ZaznamRegistraXPathZmena addZmenyItem(ZaznamRegistraXPathDataUpdate zmenyItem) {
    this.zmeny.add(zmenyItem);
    return this;
  }

  /**
   * Get zmeny
   * @return zmeny
  */
  @NotNull @Valid 
  @Schema(name = "zmeny", required = true)
  public List<ZaznamRegistraXPathDataUpdate> getZmeny() {
    return zmeny;
  }

  public void setZmeny(List<ZaznamRegistraXPathDataUpdate> zmeny) {
    this.zmeny = zmeny;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraXPathZmena zaznamRegistraXPathZmena = (ZaznamRegistraXPathZmena) o;
    return Objects.equals(this.staryPlatny, zaznamRegistraXPathZmena.staryPlatny) &&
        Objects.equals(this.staryAktualny, zaznamRegistraXPathZmena.staryAktualny) &&
        Objects.equals(this.zmeny, zaznamRegistraXPathZmena.zmeny);
  }

  @Override
  public int hashCode() {
    return Objects.hash(staryPlatny, staryAktualny, zmeny);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraXPathZmena {\n");
    sb.append("    staryPlatny: ").append(toIndentedString(staryPlatny)).append("\n");
    sb.append("    staryAktualny: ").append(toIndentedString(staryAktualny)).append("\n");
    sb.append("    zmeny: ").append(toIndentedString(zmeny)).append("\n");
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

