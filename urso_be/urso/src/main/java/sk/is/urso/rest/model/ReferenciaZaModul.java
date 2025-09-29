package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ReferenciaZaModul
 */

@JsonTypeName("referenciaZaModul")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ReferenciaZaModul {

  @JsonProperty("modul")
  private String modul;

  @JsonProperty("pocetReferencii")
  private Integer pocetReferencii;

  public ReferenciaZaModul modul(String modul) {
    this.modul = modul;
    return this;
  }

  /**
   * Get modul
   * @return modul
  */
  @NotNull @Size(min = 2, max = 8) 
  @Schema(name = "modul", required = true)
  public String getModul() {
    return modul;
  }

  public void setModul(String modul) {
    this.modul = modul;
  }

  public ReferenciaZaModul pocetReferencii(Integer pocetReferencii) {
    this.pocetReferencii = pocetReferencii;
    return this;
  }

  /**
   * Get pocetReferencii
   * @return pocetReferencii
  */
  @NotNull 
  @Schema(name = "pocetReferencii", required = true)
  public Integer getPocetReferencii() {
    return pocetReferencii;
  }

  public void setPocetReferencii(Integer pocetReferencii) {
    this.pocetReferencii = pocetReferencii;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferenciaZaModul referenciaZaModul = (ReferenciaZaModul) o;
    return Objects.equals(this.modul, referenciaZaModul.modul) &&
        Objects.equals(this.pocetReferencii, referenciaZaModul.pocetReferencii);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modul, pocetReferencii);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReferenciaZaModul {\n");
    sb.append("    modul: ").append(toIndentedString(modul)).append("\n");
    sb.append("    pocetReferencii: ").append(toIndentedString(pocetReferencii)).append("\n");
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

