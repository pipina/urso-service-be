package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ZaznamRegistraReferencia
 */

@JsonTypeName("zaznamRegistraReferencia")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraReferencia {

  @JsonProperty("registerId")
  private String registerId;

  @JsonProperty("verziaRegistraId")
  private Integer verziaRegistraId;

  @JsonProperty("zaznamId")
  private Long zaznamId;

  @JsonProperty("modul")
  private String modul;

  @JsonProperty("pocetReferencii")
  private Integer pocetReferencii;

  public ZaznamRegistraReferencia registerId(String registerId) {
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

  public ZaznamRegistraReferencia verziaRegistraId(Integer verziaRegistraId) {
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

  public ZaznamRegistraReferencia zaznamId(Long zaznamId) {
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

  public ZaznamRegistraReferencia modul(String modul) {
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

  public ZaznamRegistraReferencia pocetReferencii(Integer pocetReferencii) {
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
    ZaznamRegistraReferencia zaznamRegistraReferencia = (ZaznamRegistraReferencia) o;
    return Objects.equals(this.registerId, zaznamRegistraReferencia.registerId) &&
        Objects.equals(this.verziaRegistraId, zaznamRegistraReferencia.verziaRegistraId) &&
        Objects.equals(this.zaznamId, zaznamRegistraReferencia.zaznamId) &&
        Objects.equals(this.modul, zaznamRegistraReferencia.modul) &&
        Objects.equals(this.pocetReferencii, zaznamRegistraReferencia.pocetReferencii);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaRegistraId, zaznamId, modul, pocetReferencii);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraReferencia {\n");
    sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
    sb.append("    verziaRegistraId: ").append(toIndentedString(verziaRegistraId)).append("\n");
    sb.append("    zaznamId: ").append(toIndentedString(zaznamId)).append("\n");
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

