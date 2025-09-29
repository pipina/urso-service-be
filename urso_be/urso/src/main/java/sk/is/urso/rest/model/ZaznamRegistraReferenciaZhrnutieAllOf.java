package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ZaznamRegistraReferenciaZhrnutieAllOf
 */

@JsonTypeName("zaznamRegistraReferenciaZhrnutie_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraReferenciaZhrnutieAllOf {

  @JsonProperty("referencie")
  @Valid
  private List<ReferenciaZaModul> referencie = new ArrayList<>();

  public ZaznamRegistraReferenciaZhrnutieAllOf referencie(List<ReferenciaZaModul> referencie) {
    this.referencie = referencie;
    return this;
  }

  public ZaznamRegistraReferenciaZhrnutieAllOf addReferencieItem(ReferenciaZaModul referencieItem) {
    this.referencie.add(referencieItem);
    return this;
  }

  /**
   * Get referencie
   * @return referencie
  */
  @NotNull @Valid 
  @Schema(name = "referencie", required = true)
  public List<ReferenciaZaModul> getReferencie() {
    return referencie;
  }

  public void setReferencie(List<ReferenciaZaModul> referencie) {
    this.referencie = referencie;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraReferenciaZhrnutieAllOf zaznamRegistraReferenciaZhrnutieAllOf = (ZaznamRegistraReferenciaZhrnutieAllOf) o;
    return Objects.equals(this.referencie, zaznamRegistraReferenciaZhrnutieAllOf.referencie);
  }

  @Override
  public int hashCode() {
    return Objects.hash(referencie);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraReferenciaZhrnutieAllOf {\n");
    sb.append("    referencie: ").append(toIndentedString(referencie)).append("\n");
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

