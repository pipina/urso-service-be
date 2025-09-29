package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * RegisterListRequestFilter
 */

@JsonTypeName("registerListRequestFilter")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class RegisterListRequestFilter {

  @JsonProperty("registerId")
  private String registerId;

  @JsonProperty("verziaId")
  private Integer verziaId;

  @JsonProperty("nazov")
  private String nazov;

  @JsonProperty("povoleny")
  private Boolean povoleny;

  public RegisterListRequestFilter registerId(String registerId) {
    this.registerId = registerId;
    return this;
  }

  /**
   * Get registerId
   * @return registerId
  */
  @Size(min = 1, max = 256) 
  @Schema(name = "registerId", required = false)
  public String getRegisterId() {
    return registerId;
  }

  public void setRegisterId(String registerId) {
    this.registerId = registerId;
  }

  public RegisterListRequestFilter verziaId(Integer verziaId) {
    this.verziaId = verziaId;
    return this;
  }

  /**
   * Get verziaId
   * @return verziaId
  */
  
  @Schema(name = "verziaId", required = false)
  public Integer getVerziaId() {
    return verziaId;
  }

  public void setVerziaId(Integer verziaId) {
    this.verziaId = verziaId;
  }

  public RegisterListRequestFilter nazov(String nazov) {
    this.nazov = nazov;
    return this;
  }

  /**
   * Get nazov
   * @return nazov
  */
  
  @Schema(name = "nazov", required = false)
  public String getNazov() {
    return nazov;
  }

  public void setNazov(String nazov) {
    this.nazov = nazov;
  }

  public RegisterListRequestFilter povoleny(Boolean povoleny) {
    this.povoleny = povoleny;
    return this;
  }

  /**
   * Get povoleny
   * @return povoleny
  */
  
  @Schema(name = "povoleny", required = false)
  public Boolean getPovoleny() {
    return povoleny;
  }

  public void setPovoleny(Boolean povoleny) {
    this.povoleny = povoleny;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterListRequestFilter registerListRequestFilter = (RegisterListRequestFilter) o;
    return Objects.equals(this.registerId, registerListRequestFilter.registerId) &&
        Objects.equals(this.verziaId, registerListRequestFilter.verziaId) &&
        Objects.equals(this.nazov, registerListRequestFilter.nazov) &&
        Objects.equals(this.povoleny, registerListRequestFilter.povoleny);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaId, nazov, povoleny);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterListRequestFilter {\n");
    sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
    sb.append("    verziaId: ").append(toIndentedString(verziaId)).append("\n");
    sb.append("    nazov: ").append(toIndentedString(nazov)).append("\n");
    sb.append("    povoleny: ").append(toIndentedString(povoleny)).append("\n");
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

