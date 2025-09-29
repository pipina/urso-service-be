package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ZaznamRegistraId
 */

@JsonTypeName("zaznamRegistraId")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraId {

  @JsonProperty("registerId")
  private String registerId;

  @JsonProperty("verziaRegistraId")
  private Integer verziaRegistraId;

  @JsonProperty("zaznamId")
  private Long zaznamId;

  public ZaznamRegistraId registerId(String registerId) {
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

  public ZaznamRegistraId verziaRegistraId(Integer verziaRegistraId) {
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

  public ZaznamRegistraId zaznamId(Long zaznamId) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraId zaznamRegistraId = (ZaznamRegistraId) o;
    return Objects.equals(this.registerId, zaznamRegistraId.registerId) &&
        Objects.equals(this.verziaRegistraId, zaznamRegistraId.verziaRegistraId) &&
        Objects.equals(this.zaznamId, zaznamRegistraId.zaznamId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaRegistraId, zaznamId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraId {\n");
    sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
    sb.append("    verziaRegistraId: ").append(toIndentedString(verziaRegistraId)).append("\n");
    sb.append("    zaznamId: ").append(toIndentedString(zaznamId)).append("\n");
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

