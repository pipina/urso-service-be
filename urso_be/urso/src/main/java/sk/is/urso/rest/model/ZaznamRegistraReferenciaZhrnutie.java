package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ZaznamRegistraReferenciaZhrnutie
 */

@JsonTypeName("zaznamRegistraReferenciaZhrnutie")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraReferenciaZhrnutie {

  @JsonProperty("registerId")
  private String registerId;

  @JsonProperty("verziaRegistraId")
  private Integer verziaRegistraId;

  @JsonProperty("zaznamId")
  private Long zaznamId;

  @JsonProperty("referencie")
  @Valid
  private List<ReferenciaZaModul> referencie = new ArrayList<>();

  public ZaznamRegistraReferenciaZhrnutie registerId(String registerId) {
    this.registerId = registerId;
    return this;
  }

  /**
   * Get registerId
   * @return registerId
  */
  @NotNull @Size(min = 1, max = 256) 
  @Schema(name = "registerId", required = true)
  public String getRegisterId() {
    return registerId;
  }

  public void setRegisterId(String registerId) {
    this.registerId = registerId;
  }

  public ZaznamRegistraReferenciaZhrnutie verziaRegistraId(Integer verziaRegistraId) {
    this.verziaRegistraId = verziaRegistraId;
    return this;
  }

  /**
   * Get verziaRegistraId
   * @return verziaRegistraId
  */
  @NotNull 
  @Schema(name = "verziaRegistraId", required = true)
  public Integer getVerziaRegistraId() {
    return verziaRegistraId;
  }

  public void setVerziaRegistraId(Integer verziaRegistraId) {
    this.verziaRegistraId = verziaRegistraId;
  }

  public ZaznamRegistraReferenciaZhrnutie zaznamId(Long zaznamId) {
    this.zaznamId = zaznamId;
    return this;
  }

  /**
   * Get zaznamId
   * @return zaznamId
  */
  @NotNull 
  @Schema(name = "zaznamId", required = true)
  public Long getZaznamId() {
    return zaznamId;
  }

  public void setZaznamId(Long zaznamId) {
    this.zaznamId = zaznamId;
  }

  public ZaznamRegistraReferenciaZhrnutie referencie(List<ReferenciaZaModul> referencie) {
    this.referencie = referencie;
    return this;
  }

  public ZaznamRegistraReferenciaZhrnutie addReferencieItem(ReferenciaZaModul referencieItem) {
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
    ZaznamRegistraReferenciaZhrnutie zaznamRegistraReferenciaZhrnutie = (ZaznamRegistraReferenciaZhrnutie) o;
    return Objects.equals(this.registerId, zaznamRegistraReferenciaZhrnutie.registerId) &&
        Objects.equals(this.verziaRegistraId, zaznamRegistraReferenciaZhrnutie.verziaRegistraId) &&
        Objects.equals(this.zaznamId, zaznamRegistraReferenciaZhrnutie.zaznamId) &&
        Objects.equals(this.referencie, zaznamRegistraReferenciaZhrnutie.referencie);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaRegistraId, zaznamId, referencie);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraReferenciaZhrnutie {\n");
    sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
    sb.append("    verziaRegistraId: ").append(toIndentedString(verziaRegistraId)).append("\n");
    sb.append("    zaznamId: ").append(toIndentedString(zaznamId)).append("\n");
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

