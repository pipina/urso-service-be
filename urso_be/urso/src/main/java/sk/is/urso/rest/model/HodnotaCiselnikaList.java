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
 * HodnotaCiselnikaList
 */

@JsonTypeName("hodnotaCiselnikaList")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class HodnotaCiselnikaList {

  @JsonProperty("total")
  private Long total;

  @JsonProperty("result")
  @Valid
  private List<HodnotaCiselnikaSimpleOutput> result = new ArrayList<>();

  public HodnotaCiselnikaList total(Long total) {
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

  public HodnotaCiselnikaList result(List<HodnotaCiselnikaSimpleOutput> result) {
    this.result = result;
    return this;
  }

  public HodnotaCiselnikaList addResultItem(HodnotaCiselnikaSimpleOutput resultItem) {
    this.result.add(resultItem);
    return this;
  }

  /**
   * Get result
   * @return result
  */
  @NotNull @Valid 
  @Schema(name = "result", required = true)
  public List<HodnotaCiselnikaSimpleOutput> getResult() {
    return result;
  }

  public void setResult(List<HodnotaCiselnikaSimpleOutput> result) {
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
    HodnotaCiselnikaList hodnotaCiselnikaList = (HodnotaCiselnikaList) o;
    return Objects.equals(this.total, hodnotaCiselnikaList.total) &&
        Objects.equals(this.result, hodnotaCiselnikaList.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HodnotaCiselnikaList {\n");
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

