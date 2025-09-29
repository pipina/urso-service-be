package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ZaznamRegistraXPathData
 */

@JsonTypeName("zaznamRegistraXPathData")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraXPathData {

  @JsonProperty("xpath")
  private String xpath;

  @JsonProperty("hodnota")
  private String hodnota;

  public ZaznamRegistraXPathData xpath(String xpath) {
    this.xpath = xpath;
    return this;
  }

  /**
   * Get xpath
   * @return xpath
  */
  @NotNull @Size(max = 1024) 
  @Schema(name = "xpath", required = true)
  public String getXpath() {
    return xpath;
  }

  public void setXpath(String xpath) {
    this.xpath = xpath;
  }

  public ZaznamRegistraXPathData hodnota(String hodnota) {
    this.hodnota = hodnota;
    return this;
  }

  /**
   * Get hodnota
   * @return hodnota
  */
  @NotNull @Size(max = 4096) 
  @Schema(name = "hodnota", required = true)
  public String getHodnota() {
    return hodnota;
  }

  public void setHodnota(String hodnota) {
    this.hodnota = hodnota;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraXPathData zaznamRegistraXPathData = (ZaznamRegistraXPathData) o;
    return Objects.equals(this.xpath, zaznamRegistraXPathData.xpath) &&
        Objects.equals(this.hodnota, zaznamRegistraXPathData.hodnota);
  }

  @Override
  public int hashCode() {
    return Objects.hash(xpath, hodnota);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraXPathData {\n");
    sb.append("    xpath: ").append(toIndentedString(xpath)).append("\n");
    sb.append("    hodnota: ").append(toIndentedString(hodnota)).append("\n");
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

