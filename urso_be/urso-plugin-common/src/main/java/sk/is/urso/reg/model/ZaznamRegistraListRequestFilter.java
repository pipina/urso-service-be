package sk.is.urso.reg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ZaznamRegistraListRequestFilter
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-30T09:30:52.258432300+01:00[Europe/Bratislava]")
public class ZaznamRegistraListRequestFilter   {
  @JsonProperty("polia")
  @Valid
  private List<DvojicaKlucHodnotaVolitelna> polia = new ArrayList<>();

  @JsonProperty("platny")
  private Boolean platny = true;

  @JsonProperty("datumUcinnosti")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
  private LocalDate datumUcinnosti;

  @JsonProperty("referencujuciModul")
  private String referencujuciModul;

  public ZaznamRegistraListRequestFilter polia(List<DvojicaKlucHodnotaVolitelna> polia) {
    this.polia = polia;
    return this;
  }

  public ZaznamRegistraListRequestFilter addPoliaItem(DvojicaKlucHodnotaVolitelna poliaItem) {
    this.polia.add(poliaItem);
    return this;
  }

  /**
   * Get polia
   * @return polia
   */
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public List<DvojicaKlucHodnotaVolitelna> getPolia() {
    return polia;
  }

  public void setPolia(List<DvojicaKlucHodnotaVolitelna> polia) {
    this.polia = polia;
  }

  public ZaznamRegistraListRequestFilter platny(Boolean platny) {
    this.platny = platny;
    return this;
  }

  /**
   * Get platny
   * @return platny
   */
  @ApiModelProperty(value = "")


  public Boolean getPlatny() {
    return platny;
  }

  public void setPlatny(Boolean platny) {
    this.platny = platny;
  }

  public ZaznamRegistraListRequestFilter datumUcinnosti(LocalDate datumUcinnosti) {
    this.datumUcinnosti = datumUcinnosti;
    return this;
  }

  /**
   * Get datumUcinnosti
   * @return datumUcinnosti
   */
  @ApiModelProperty(value = "")

  @Valid

  public LocalDate getDatumUcinnosti() {
    return datumUcinnosti;
  }

  public void setDatumUcinnosti(LocalDate datumUcinnosti) {
    this.datumUcinnosti = datumUcinnosti;
  }

  public ZaznamRegistraListRequestFilter referencujuciModul(String referencujuciModul) {
    this.referencujuciModul = referencujuciModul;
    return this;
  }

  /**
   * Get referencujuciModul
   * @return referencujuciModul
   */
  @ApiModelProperty(value = "")

  @Size(min=2,max=8)
  public String getReferencujuciModul() {
    return referencujuciModul;
  }

  public void setReferencujuciModul(String referencujuciModul) {
    this.referencujuciModul = referencujuciModul;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraListRequestFilter zaznamRegistraListRequestFilter = (ZaznamRegistraListRequestFilter) o;
    return Objects.equals(this.polia, zaznamRegistraListRequestFilter.polia) &&
            Objects.equals(this.platny, zaznamRegistraListRequestFilter.platny) &&
            Objects.equals(this.datumUcinnosti, zaznamRegistraListRequestFilter.datumUcinnosti) &&
            Objects.equals(this.referencujuciModul, zaznamRegistraListRequestFilter.referencujuciModul);
  }

  @Override
  public int hashCode() {
    return Objects.hash(polia, platny, datumUcinnosti, referencujuciModul);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraListRequestFilter {\n");

    sb.append("    polia: ").append(toIndentedString(polia)).append("\n");
    sb.append("    platny: ").append(toIndentedString(platny)).append("\n");
    sb.append("    datumUcinnosti: ").append(toIndentedString(datumUcinnosti)).append("\n");
    sb.append("    referencujuciModul: ").append(toIndentedString(referencujuciModul)).append("\n");
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