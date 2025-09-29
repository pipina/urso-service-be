package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * CiselnikListRequestAllOf
 */

@JsonTypeName("ciselnikListRequest_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class CiselnikListRequestAllOf {

  @JsonProperty("filter")
  private CiselnikRequestFilter filter;

  public CiselnikListRequestAllOf filter(CiselnikRequestFilter filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Get filter
   * @return filter
  */
  @NotNull @Valid 
  @Schema(name = "filter", required = true)
  public CiselnikRequestFilter getFilter() {
    return filter;
  }

  public void setFilter(CiselnikRequestFilter filter) {
    this.filter = filter;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CiselnikListRequestAllOf ciselnikListRequestAllOf = (CiselnikListRequestAllOf) o;
    return Objects.equals(this.filter, ciselnikListRequestAllOf.filter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filter);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CiselnikListRequestAllOf {\n");
    sb.append("    filter: ").append(toIndentedString(filter)).append("\n");
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

