package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * StreetFindRequestDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class StreetFindRequestDTO {

  @JsonProperty("offset")
  private Integer offset;

  @JsonProperty("limit")
  private Integer limit;

  @JsonProperty("count")
  private Boolean count;

  @JsonProperty("sortBy")
  private String sortBy;

  @JsonProperty("sortDesc")
  private Boolean sortDesc;

  @JsonProperty("name")
  private String name;

  @JsonProperty("municipalityId")
  private Long municipalityId;

  public StreetFindRequestDTO offset(Integer offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Get offset
   * minimum: 0
   * maximum: 2147483647
   * @return offset
  */
  @Min(0) @Max(2147483647) 
  @Schema(name = "offset", required = false)
  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public StreetFindRequestDTO limit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Get limit
   * minimum: 1
   * maximum: 100
   * @return limit
  */
  @NotNull @Min(1) @Max(100) 
  @Schema(name = "limit", required = true)
  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public StreetFindRequestDTO count(Boolean count) {
    this.count = count;
    return this;
  }

  /**
   * Get count
   * @return count
  */
  
  @Schema(name = "count", required = false)
  public Boolean getCount() {
    return count;
  }

  public void setCount(Boolean count) {
    this.count = count;
  }

  public StreetFindRequestDTO sortBy(String sortBy) {
    this.sortBy = sortBy;
    return this;
  }

  /**
   * Get sortBy
   * @return sortBy
  */
  
  @Schema(name = "sortBy", required = false)
  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  public StreetFindRequestDTO sortDesc(Boolean sortDesc) {
    this.sortDesc = sortDesc;
    return this;
  }

  /**
   * Get sortDesc
   * @return sortDesc
  */
  
  @Schema(name = "sortDesc", required = false)
  public Boolean getSortDesc() {
    return sortDesc;
  }

  public void setSortDesc(Boolean sortDesc) {
    this.sortDesc = sortDesc;
  }

  public StreetFindRequestDTO name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  
  @Schema(name = "name", required = false)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public StreetFindRequestDTO municipalityId(Long municipalityId) {
    this.municipalityId = municipalityId;
    return this;
  }

  /**
   * Get municipalityId
   * minimum: 1
   * @return municipalityId
  */
  @Min(1L) 
  @Schema(name = "municipalityId", required = false)
  public Long getMunicipalityId() {
    return municipalityId;
  }

  public void setMunicipalityId(Long municipalityId) {
    this.municipalityId = municipalityId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StreetFindRequestDTO streetFindRequestDTO = (StreetFindRequestDTO) o;
    return Objects.equals(this.offset, streetFindRequestDTO.offset) &&
        Objects.equals(this.limit, streetFindRequestDTO.limit) &&
        Objects.equals(this.count, streetFindRequestDTO.count) &&
        Objects.equals(this.sortBy, streetFindRequestDTO.sortBy) &&
        Objects.equals(this.sortDesc, streetFindRequestDTO.sortDesc) &&
        Objects.equals(this.name, streetFindRequestDTO.name) &&
        Objects.equals(this.municipalityId, streetFindRequestDTO.municipalityId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(offset, limit, count, sortBy, sortDesc, name, municipalityId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class StreetFindRequestDTO {\n");
    sb.append("    offset: ").append(toIndentedString(offset)).append("\n");
    sb.append("    limit: ").append(toIndentedString(limit)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    sortBy: ").append(toIndentedString(sortBy)).append("\n");
    sb.append("    sortDesc: ").append(toIndentedString(sortDesc)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    municipalityId: ").append(toIndentedString(municipalityId)).append("\n");
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

