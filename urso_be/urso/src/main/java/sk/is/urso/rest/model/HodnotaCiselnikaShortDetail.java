package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * HodnotaCiselnikaShortDetail
 */

@JsonTypeName("hodnotaCiselnikaShortDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class HodnotaCiselnikaShortDetail {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("kodPolozky")
  private String kodPolozky;

  @JsonProperty("nazovPolozky")
  private String nazovPolozky;

  public HodnotaCiselnikaShortDetail id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull 
  @Schema(name = "id", required = true)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public HodnotaCiselnikaShortDetail kodPolozky(String kodPolozky) {
    this.kodPolozky = kodPolozky;
    return this;
  }

  /**
   * Get kodPolozky
   * @return kodPolozky
  */
  @NotNull @Size(min = 1, max = 255) 
  @Schema(name = "kodPolozky", required = true)
  public String getKodPolozky() {
    return kodPolozky;
  }

  public void setKodPolozky(String kodPolozky) {
    this.kodPolozky = kodPolozky;
  }

  public HodnotaCiselnikaShortDetail nazovPolozky(String nazovPolozky) {
    this.nazovPolozky = nazovPolozky;
    return this;
  }

  /**
   * Get nazovPolozky
   * @return nazovPolozky
  */
  @NotNull @Size(max = 256) 
  @Schema(name = "nazovPolozky", required = true)
  public String getNazovPolozky() {
    return nazovPolozky;
  }

  public void setNazovPolozky(String nazovPolozky) {
    this.nazovPolozky = nazovPolozky;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HodnotaCiselnikaShortDetail hodnotaCiselnikaShortDetail = (HodnotaCiselnikaShortDetail) o;
    return Objects.equals(this.id, hodnotaCiselnikaShortDetail.id) &&
        Objects.equals(this.kodPolozky, hodnotaCiselnikaShortDetail.kodPolozky) &&
        Objects.equals(this.nazovPolozky, hodnotaCiselnikaShortDetail.nazovPolozky);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, kodPolozky, nazovPolozky);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HodnotaCiselnikaShortDetail {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    kodPolozky: ").append(toIndentedString(kodPolozky)).append("\n");
    sb.append("    nazovPolozky: ").append(toIndentedString(nazovPolozky)).append("\n");
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

