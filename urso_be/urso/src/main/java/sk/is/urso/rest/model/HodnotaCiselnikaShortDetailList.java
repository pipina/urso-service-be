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
 * Zoznam hodnôt číselníka do select box
 */

@Schema(name = "hodnotaCiselnikaShortDetailList", description = "Zoznam hodnôt číselníka do select box")
@JsonTypeName("hodnotaCiselnikaShortDetailList")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class HodnotaCiselnikaShortDetailList {

  @JsonProperty("result")
  @Valid
  private List<HodnotaCiselnikaShortDetail> result = new ArrayList<>();

  public HodnotaCiselnikaShortDetailList result(List<HodnotaCiselnikaShortDetail> result) {
    this.result = result;
    return this;
  }

  public HodnotaCiselnikaShortDetailList addResultItem(HodnotaCiselnikaShortDetail resultItem) {
    this.result.add(resultItem);
    return this;
  }

  /**
   * Get result
   * @return result
  */
  @NotNull @Valid 
  @Schema(name = "result", required = true)
  public List<HodnotaCiselnikaShortDetail> getResult() {
    return result;
  }

  public void setResult(List<HodnotaCiselnikaShortDetail> result) {
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
    HodnotaCiselnikaShortDetailList hodnotaCiselnikaShortDetailList = (HodnotaCiselnikaShortDetailList) o;
    return Objects.equals(this.result, hodnotaCiselnikaShortDetailList.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HodnotaCiselnikaShortDetailList {\n");
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

