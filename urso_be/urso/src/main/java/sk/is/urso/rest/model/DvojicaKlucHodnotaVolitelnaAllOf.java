package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import java.util.Objects;

/**
 * DvojicaKlucHodnotaVolitelnaAllOf
 */

@JsonTypeName("dvojicaKlucHodnotaVolitelna_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class DvojicaKlucHodnotaVolitelnaAllOf {

  @JsonProperty("volitelna")
  private Boolean volitelna = false;

  public DvojicaKlucHodnotaVolitelnaAllOf volitelna(Boolean volitelna) {
    this.volitelna = volitelna;
    return this;
  }

  /**
   * Ak je true tak tento vyhľadávací argument nie je povinný.
   * @return volitelna
  */
  
  @Schema(name = "volitelna", description = "Ak je true tak tento vyhľadávací argument nie je povinný.", required = false)
  public Boolean getVolitelna() {
    return volitelna;
  }

  public void setVolitelna(Boolean volitelna) {
    this.volitelna = volitelna;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DvojicaKlucHodnotaVolitelnaAllOf dvojicaKlucHodnotaVolitelnaAllOf = (DvojicaKlucHodnotaVolitelnaAllOf) o;
    return Objects.equals(this.volitelna, dvojicaKlucHodnotaVolitelnaAllOf.volitelna);
  }

  @Override
  public int hashCode() {
    return Objects.hash(volitelna);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DvojicaKlucHodnotaVolitelnaAllOf {\n");
    sb.append("    volitelna: ").append(toIndentedString(volitelna)).append("\n");
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

