package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * ZaznamRegistraListRequest
 */

@JsonTypeName("zaznamRegistraListRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraListRequest {

  @JsonProperty("page")
  private Integer page;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("sort")
  private String sort;

  @JsonProperty("order")
  private OrderEnum order;

  @JsonProperty("filter")
  private ZaznamRegistraListRequestFilter filter;

  public ZaznamRegistraListRequest page(Integer page) {
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

  public ZaznamRegistraListRequest limit(Integer limit) {
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

  public ZaznamRegistraListRequest sort(String sort) {
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

  public ZaznamRegistraListRequest order(OrderEnum order) {
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

  public ZaznamRegistraListRequest filter(ZaznamRegistraListRequestFilter filter) {
    this.filter = filter;
    return this;
  }

  /**
   * Get filter
   * @return filter
  */
  @NotNull @Valid 
  @Schema(name = "filter", required = true)
  public ZaznamRegistraListRequestFilter getFilter() {
    return filter;
  }

  public void setFilter(ZaznamRegistraListRequestFilter filter) {
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
    ZaznamRegistraListRequest zaznamRegistraListRequest = (ZaznamRegistraListRequest) o;
    return Objects.equals(this.page, zaznamRegistraListRequest.page) &&
        Objects.equals(this.limit, zaznamRegistraListRequest.limit) &&
        Objects.equals(this.sort, zaznamRegistraListRequest.sort) &&
        Objects.equals(this.order, zaznamRegistraListRequest.order) &&
        Objects.equals(this.filter, zaznamRegistraListRequest.filter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, limit, sort, order, filter);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraListRequest {\n");
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

