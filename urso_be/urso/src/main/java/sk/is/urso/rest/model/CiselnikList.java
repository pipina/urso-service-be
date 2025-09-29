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
 * CiselnikList
 */

@JsonTypeName("ciselnikList")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class CiselnikList {

  @JsonProperty("total")
  private Long total;

  @JsonProperty("result")
  @Valid
  private List<CiselnikSimpleOutput> result = new ArrayList<>();

  public CiselnikList total(Long total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
  */
  @NotNull 
  @Schema(name = "total", required = true)
  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public CiselnikList result(List<CiselnikSimpleOutput> result) {
    this.result = result;
    return this;
  }

  public CiselnikList addResultItem(CiselnikSimpleOutput resultItem) {
    this.result.add(resultItem);
    return this;
  }

  /**
   * Get result
   * @return result
  */
  @NotNull @Valid 
  @Schema(name = "result", required = true)
  public List<CiselnikSimpleOutput> getResult() {
    return result;
  }

  public void setResult(List<CiselnikSimpleOutput> result) {
    this.result = result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CiselnikList ciselnikList = (CiselnikList) o;
    return Objects.equals(this.total, ciselnikList.total) &&
        Objects.equals(this.result, ciselnikList.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CiselnikList {\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

