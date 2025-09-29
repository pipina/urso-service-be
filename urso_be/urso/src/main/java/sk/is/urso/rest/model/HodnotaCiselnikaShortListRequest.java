package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * HodnotaCiselnikaShortListRequest
 */

@JsonTypeName("hodnotaCiselnikaShortListRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class HodnotaCiselnikaShortListRequest {

  @JsonProperty("nazovPolozky")
  private String nazovPolozky;

  @JsonProperty("dodatocnyObsah")
  private String dodatocnyObsah;

  public HodnotaCiselnikaShortListRequest nazovPolozky(String nazovPolozky) {
    this.nazovPolozky = nazovPolozky;
    return this;
  }

  /**
   * Get nazovPolozky
   * @return nazovPolozky
  */
  @Size(max = 256) 
  @Schema(name = "nazovPolozky", required = false)
  public String getNazovPolozky() {
    return nazovPolozky;
  }

  public void setNazovPolozky(String nazovPolozky) {
    this.nazovPolozky = nazovPolozky;
  }

  public HodnotaCiselnikaShortListRequest dodatocnyObsah(String dodatocnyObsah) {
    this.dodatocnyObsah = dodatocnyObsah;
    return this;
  }

  /**
   * Get dodatocnyObsah
   * @return dodatocnyObsah
  */
  @Size(max = 256) 
  @Schema(name = "dodatocnyObsah", required = false)
  public String getDodatocnyObsah() {
    return dodatocnyObsah;
  }

  public void setDodatocnyObsah(String dodatocnyObsah) {
    this.dodatocnyObsah = dodatocnyObsah;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HodnotaCiselnikaShortListRequest hodnotaCiselnikaShortListRequest = (HodnotaCiselnikaShortListRequest) o;
    return Objects.equals(this.nazovPolozky, hodnotaCiselnikaShortListRequest.nazovPolozky) &&
        Objects.equals(this.dodatocnyObsah, hodnotaCiselnikaShortListRequest.dodatocnyObsah);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nazovPolozky, dodatocnyObsah);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HodnotaCiselnikaShortListRequest {\n");
    sb.append("    nazovPolozky: ").append(toIndentedString(nazovPolozky)).append("\n");
    sb.append("    dodatocnyObsah: ").append(toIndentedString(dodatocnyObsah)).append("\n");
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

