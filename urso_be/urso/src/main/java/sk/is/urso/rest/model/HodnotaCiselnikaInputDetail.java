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
import java.util.Objects;

/**
 * HodnotaCiselnikaInputDetail
 */

@JsonTypeName("hodnotaCiselnikaInputDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class HodnotaCiselnikaInputDetail {

  @JsonProperty("kodPolozky")
  private String kodPolozky;

  @JsonProperty("nazovPolozky")
  private String nazovPolozky;

  @JsonProperty("ciselnikId")
  private Long ciselnikId;

  @JsonProperty("nadradenaHodnotaCiselnikaId")
  private Long nadradenaHodnotaCiselnikaId;

  @JsonProperty("platnostOd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostOd;

  @JsonProperty("platnostDo")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostDo;

  @JsonProperty("poradie")
  private Long poradie;

  @JsonProperty("dodatocnyObsah")
  private String dodatocnyObsah;

  public HodnotaCiselnikaInputDetail kodPolozky(String kodPolozky) {
    this.kodPolozky = kodPolozky;
    return this;
  }

  /**
   * Get kodPolozky
   * @return kodPolozky
  */
  @NotNull @Size(min = 1, max = 255) 
  @Schema(name = "kodPolozky", required = true)
  public String getKodPolozky() {
    return kodPolozky;
  }

  public void setKodPolozky(String kodPolozky) {
    this.kodPolozky = kodPolozky;
  }

  public HodnotaCiselnikaInputDetail nazovPolozky(String nazovPolozky) {
    this.nazovPolozky = nazovPolozky;
    return this;
  }

  /**
   * Get nazovPolozky
   * @return nazovPolozky
  */
  @NotNull @Size(max = 256) 
  @Schema(name = "nazovPolozky", required = true)
  public String getNazovPolozky() {
    return nazovPolozky;
  }

  public void setNazovPolozky(String nazovPolozky) {
    this.nazovPolozky = nazovPolozky;
  }

  public HodnotaCiselnikaInputDetail ciselnikId(Long ciselnikId) {
    this.ciselnikId = ciselnikId;
    return this;
  }

  /**
   * Get ciselnikId
   * @return ciselnikId
  */
  @NotNull 
  @Schema(name = "ciselnikId", required = true)
  public Long getCiselnikId() {
    return ciselnikId;
  }

  public void setCiselnikId(Long ciselnikId) {
    this.ciselnikId = ciselnikId;
  }

  public HodnotaCiselnikaInputDetail nadradenaHodnotaCiselnikaId(Long nadradenaHodnotaCiselnikaId) {
    this.nadradenaHodnotaCiselnikaId = nadradenaHodnotaCiselnikaId;
    return this;
  }

  /**
   * Get nadradenaHodnotaCiselnikaId
   * @return nadradenaHodnotaCiselnikaId
  */
  
  @Schema(name = "nadradenaHodnotaCiselnikaId", required = false)
  public Long getNadradenaHodnotaCiselnikaId() {
    return nadradenaHodnotaCiselnikaId;
  }

  public void setNadradenaHodnotaCiselnikaId(Long nadradenaHodnotaCiselnikaId) {
    this.nadradenaHodnotaCiselnikaId = nadradenaHodnotaCiselnikaId;
  }

  public HodnotaCiselnikaInputDetail platnostOd(LocalDate platnostOd) {
    this.platnostOd = platnostOd;
    return this;
  }

  /**
   * Get platnostOd
   * @return platnostOd
  */
  @NotNull @Valid 
  @Schema(name = "platnostOd", required = true)
  public LocalDate getPlatnostOd() {
    return platnostOd;
  }

  public void setPlatnostOd(LocalDate platnostOd) {
    this.platnostOd = platnostOd;
  }

  public HodnotaCiselnikaInputDetail platnostDo(LocalDate platnostDo) {
    this.platnostDo = platnostDo;
    return this;
  }

  /**
   * Get platnostDo
   * @return platnostDo
  */
  @Valid 
  @Schema(name = "platnostDo", required = false)
  public LocalDate getPlatnostDo() {
    return platnostDo;
  }

  public void setPlatnostDo(LocalDate platnostDo) {
    this.platnostDo = platnostDo;
  }

  public HodnotaCiselnikaInputDetail poradie(Long poradie) {
    this.poradie = poradie;
    return this;
  }

  /**
   * Get poradie
   * @return poradie
  */
  
  @Schema(name = "poradie", required = false)
  public Long getPoradie() {
    return poradie;
  }

  public void setPoradie(Long poradie) {
    this.poradie = poradie;
  }

  public HodnotaCiselnikaInputDetail dodatocnyObsah(String dodatocnyObsah) {
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
    HodnotaCiselnikaInputDetail hodnotaCiselnikaInputDetail = (HodnotaCiselnikaInputDetail) o;
    return Objects.equals(this.kodPolozky, hodnotaCiselnikaInputDetail.kodPolozky) &&
        Objects.equals(this.nazovPolozky, hodnotaCiselnikaInputDetail.nazovPolozky) &&
        Objects.equals(this.ciselnikId, hodnotaCiselnikaInputDetail.ciselnikId) &&
        Objects.equals(this.nadradenaHodnotaCiselnikaId, hodnotaCiselnikaInputDetail.nadradenaHodnotaCiselnikaId) &&
        Objects.equals(this.platnostOd, hodnotaCiselnikaInputDetail.platnostOd) &&
        Objects.equals(this.platnostDo, hodnotaCiselnikaInputDetail.platnostDo) &&
        Objects.equals(this.poradie, hodnotaCiselnikaInputDetail.poradie) &&
        Objects.equals(this.dodatocnyObsah, hodnotaCiselnikaInputDetail.dodatocnyObsah);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kodPolozky, nazovPolozky, ciselnikId, nadradenaHodnotaCiselnikaId, platnostOd, platnostDo, poradie, dodatocnyObsah);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HodnotaCiselnikaInputDetail {\n");
    sb.append("    kodPolozky: ").append(toIndentedString(kodPolozky)).append("\n");
    sb.append("    nazovPolozky: ").append(toIndentedString(nazovPolozky)).append("\n");
    sb.append("    ciselnikId: ").append(toIndentedString(ciselnikId)).append("\n");
    sb.append("    nadradenaHodnotaCiselnikaId: ").append(toIndentedString(nadradenaHodnotaCiselnikaId)).append("\n");
    sb.append("    platnostOd: ").append(toIndentedString(platnostOd)).append("\n");
    sb.append("    platnostDo: ").append(toIndentedString(platnostDo)).append("\n");
    sb.append("    poradie: ").append(toIndentedString(poradie)).append("\n");
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

