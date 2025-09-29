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
 * ZaznamRegistraAllOf
 */

@JsonTypeName("zaznamRegistra_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraAllOf {

  @JsonProperty("polia")
  @Valid
  private List<DvojicaKlucHodnotaSHistoriou> polia = new ArrayList<>();

  public ZaznamRegistraAllOf polia(List<DvojicaKlucHodnotaSHistoriou> polia) {
    this.polia = polia;
    return this;
  }

  public ZaznamRegistraAllOf addPoliaItem(DvojicaKlucHodnotaSHistoriou poliaItem) {
    this.polia.add(poliaItem);
    return this;
  }

  /**
   * Get polia
   * @return polia
  */
  @NotNull @Valid 
  @Schema(name = "polia", required = true)
  public List<DvojicaKlucHodnotaSHistoriou> getPolia() {
    return polia;
  }

  public void setPolia(List<DvojicaKlucHodnotaSHistoriou> polia) {
    this.polia = polia;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraAllOf zaznamRegistraAllOf = (ZaznamRegistraAllOf) o;
    return Objects.equals(this.polia, zaznamRegistraAllOf.polia);
  }

  @Override
  public int hashCode() {
    return Objects.hash(polia);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraAllOf {\n");
    sb.append("    polia: ").append(toIndentedString(polia)).append("\n");
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

