package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
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

@JsonTypeName("zaznamRegistraListRequestFilter")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraListRequestFilter {

  @JsonProperty("polia")
  @Valid
  private List<DvojicaKlucHodnotaVolitelna> polia = new ArrayList<>();

  @JsonProperty("platnost")
  private Boolean platnost = true;

  @JsonProperty("datumUcinnosti")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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
  @NotNull @Valid 
  @Schema(name = "polia", required = true)
  public List<DvojicaKlucHodnotaVolitelna> getPolia() {
    return polia;
  }

  public void setPolia(List<DvojicaKlucHodnotaVolitelna> polia) {
    this.polia = polia;
  }

  public ZaznamRegistraListRequestFilter platnost(Boolean platnost) {
    this.platnost = platnost;
    return this;
  }

  /**
   * Get platnost
   * @return platnost
  */
  
  @Schema(name = "platnost", required = false)
  public Boolean getPlatnost() {
    return platnost;
  }

  public void setPlatnost(Boolean platnost) {
    this.platnost = platnost;
  }

  public ZaznamRegistraListRequestFilter datumUcinnosti(LocalDate datumUcinnosti) {
    this.datumUcinnosti = datumUcinnosti;
    return this;
  }

  /**
   * Get datumUcinnosti
   * @return datumUcinnosti
  */
  @Valid 
  @Schema(name = "datumUcinnosti", required = false)
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
  @Size(min = 2, max = 8) 
  @Schema(name = "referencujuciModul", required = false)
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
        Objects.equals(this.platnost, zaznamRegistraListRequestFilter.platnost) &&
        Objects.equals(this.datumUcinnosti, zaznamRegistraListRequestFilter.datumUcinnosti) &&
        Objects.equals(this.referencujuciModul, zaznamRegistraListRequestFilter.referencujuciModul);
  }

  @Override
  public int hashCode() {
    return Objects.hash(polia, platnost, datumUcinnosti, referencujuciModul);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraListRequestFilter {\n");
    sb.append("    polia: ").append(toIndentedString(polia)).append("\n");
    sb.append("    platnost: ").append(toIndentedString(platnost)).append("\n");
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

