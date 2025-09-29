package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ZaznamRegistraXPathDataUpdate
 */

@JsonTypeName("zaznamRegistraXPathDataUpdate")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraXPathDataUpdate {

  @JsonProperty("xpath")
  private String xpath;

  @JsonProperty("value")
  private String value;

  @JsonProperty("action")
  private XpathDataUpdateType action;

  public ZaznamRegistraXPathDataUpdate xpath(String xpath) {
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

  public ZaznamRegistraXPathDataUpdate value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  @NotNull @Size(max = 4096) 
  @Schema(name = "value", required = true)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ZaznamRegistraXPathDataUpdate action(XpathDataUpdateType action) {
    this.action = action;
    return this;
  }

  /**
   * Get action
   * @return action
  */
  @Valid 
  @Schema(name = "action", required = false)
  public XpathDataUpdateType getAction() {
    return action;
  }

  public void setAction(XpathDataUpdateType action) {
    this.action = action;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraXPathDataUpdate zaznamRegistraXPathDataUpdate = (ZaznamRegistraXPathDataUpdate) o;
    return Objects.equals(this.xpath, zaznamRegistraXPathDataUpdate.xpath) &&
        Objects.equals(this.value, zaznamRegistraXPathDataUpdate.value) &&
        Objects.equals(this.action, zaznamRegistraXPathDataUpdate.action);
  }

  @Override
  public int hashCode() {
    return Objects.hash(xpath, value, action);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraXPathDataUpdate {\n");
    sb.append("    xpath: ").append(toIndentedString(xpath)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
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

