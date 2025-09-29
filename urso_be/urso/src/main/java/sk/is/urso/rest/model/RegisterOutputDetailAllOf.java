package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RegisterOutputDetailAllOf
 */

@JsonTypeName("registerOutputDetail_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class RegisterOutputDetailAllOf {

  @JsonProperty("polia")
  @Valid
  private List<RegisterPole> polia = null;

  @JsonProperty("gdpr")
  private RegisterDetailGdpr gdpr;

  public RegisterOutputDetailAllOf polia(List<RegisterPole> polia) {
    this.polia = polia;
    return this;
  }

  public RegisterOutputDetailAllOf addPoliaItem(RegisterPole poliaItem) {
    if (this.polia == null) {
      this.polia = new ArrayList<>();
    }
    this.polia.add(poliaItem);
    return this;
  }

  /**
   * Get polia
   * @return polia
  */
  @Valid 
  @Schema(name = "polia", required = false)
  public List<RegisterPole> getPolia() {
    return polia;
  }

  public void setPolia(List<RegisterPole> polia) {
    this.polia = polia;
  }

  public RegisterOutputDetailAllOf gdpr(RegisterDetailGdpr gdpr) {
    this.gdpr = gdpr;
    return this;
  }

  /**
   * Get gdpr
   * @return gdpr
  */
  @Valid 
  @Schema(name = "gdpr", required = false)
  public RegisterDetailGdpr getGdpr() {
    return gdpr;
  }

  public void setGdpr(RegisterDetailGdpr gdpr) {
    this.gdpr = gdpr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterOutputDetailAllOf registerOutputDetailAllOf = (RegisterOutputDetailAllOf) o;
    return Objects.equals(this.polia, registerOutputDetailAllOf.polia) &&
        Objects.equals(this.gdpr, registerOutputDetailAllOf.gdpr);
  }

  @Override
  public int hashCode() {
    return Objects.hash(polia, gdpr);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterOutputDetailAllOf {\n");
    sb.append("    polia: ").append(toIndentedString(polia)).append("\n");
    sb.append("    gdpr: ").append(toIndentedString(gdpr)).append("\n");
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

