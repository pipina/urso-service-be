package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * RegisterListRequest
 */

@JsonTypeName("registerListRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class RegisterListRequest {

  @JsonProperty("page")
  private Integer page;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("sort")
  private String sort;

  @JsonProperty("order")
  private OrderEnum order;

  @JsonProperty("filter")
  private RegisterListRequestFilter filter;

  public RegisterListRequest page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
  */
  @NotNull 
  @Schema(name = "page", required = true)
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public RegisterListRequest limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Get limit
   * @return limit
  */
  @NotNull 
  @Schema(name = "limit", required = true)
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public RegisterListRequest sort(String sort) {
    this.sort = sort;
    return this;
  }

  /**
   * Get sort
   * @return sort
  */
  
  @Schema(name = "sort", required = false)
  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public RegisterListRequest order(OrderEnum order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   * @return order
  */
  @Valid 
  @Schema(name = "order", required = false)
  public OrderEnum getOrder() {
    return order;
  }

  public void setOrder(OrderEnum order) {
    this.order = order;
  }

  public RegisterListRequest filter(RegisterListRequestFilter filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Get filter
   * @return filter
  */
  @NotNull @Valid 
  @Schema(name = "filter", required = true)
  public RegisterListRequestFilter getFilter() {
    return filter;
  }

  public void setFilter(RegisterListRequestFilter filter) {
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
    RegisterListRequest registerListRequest = (RegisterListRequest) o;
    return Objects.equals(this.page, registerListRequest.page) &&
        Objects.equals(this.limit, registerListRequest.limit) &&
        Objects.equals(this.sort, registerListRequest.sort) &&
        Objects.equals(this.order, registerListRequest.order) &&
        Objects.equals(this.filter, registerListRequest.filter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, limit, sort, order, filter);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterListRequest {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    sort: ").append(toIndentedString(sort)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
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

