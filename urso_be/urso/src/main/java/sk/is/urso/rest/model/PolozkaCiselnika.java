package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * PolozkaCiselnika
 */

@JsonTypeName("polozkaCiselnika")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class PolozkaCiselnika {

  @JsonProperty("kodPolozky")
  private String kodPolozky;

  @JsonProperty("hodnotaPolozky")
  private String hodnotaPolozky;

  @JsonProperty("kodCiselnika")
  private String kodCiselnika;

  public PolozkaCiselnika kodPolozky(String kodPolozky) {
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

  public PolozkaCiselnika hodnotaPolozky(String hodnotaPolozky) {
    this.hodnotaPolozky = hodnotaPolozky;
    return this;
  }

  /**
   * Get hodnotaPolozky
   * @return hodnotaPolozky
  */
  @Size(max = 256) 
  @Schema(name = "hodnotaPolozky", required = false)
  public String getHodnotaPolozky() {
    return hodnotaPolozky;
  }

  public void setHodnotaPolozky(String hodnotaPolozky) {
    this.hodnotaPolozky = hodnotaPolozky;
  }

  public PolozkaCiselnika kodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
    return this;
  }

  /**
   * Get kodCiselnika
   * @return kodCiselnika
  */
  @Pattern(regexp = "[a-zA-Z0-9]+") @Size(min = 1, max = 100) 
  @Schema(name = "kodCiselnika", required = false)
  public String getKodCiselnika() {
    return kodCiselnika;
  }

  public void setKodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PolozkaCiselnika polozkaCiselnika = (PolozkaCiselnika) o;
    return Objects.equals(this.kodPolozky, polozkaCiselnika.kodPolozky) &&
        Objects.equals(this.hodnotaPolozky, polozkaCiselnika.hodnotaPolozky) &&
        Objects.equals(this.kodCiselnika, polozkaCiselnika.kodCiselnika);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kodPolozky, hodnotaPolozky, kodCiselnika);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PolozkaCiselnika {\n");
    sb.append("    kodPolozky: ").append(toIndentedString(kodPolozky)).append("\n");
    sb.append("    hodnotaPolozky: ").append(toIndentedString(hodnotaPolozky)).append("\n");
    sb.append("    kodCiselnika: ").append(toIndentedString(kodCiselnika)).append("\n");
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

