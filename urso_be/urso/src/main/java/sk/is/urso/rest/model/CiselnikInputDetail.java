package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * CiselnikInputDetail
 */

@JsonTypeName("ciselnikInputDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class CiselnikInputDetail {

  @JsonProperty("kodCiselnika")
  private String kodCiselnika;

  @JsonProperty("nazovCiselnika")
  private String nazovCiselnika;

  @JsonProperty("externyKod")
  private String externyKod;

  @JsonProperty("platnostOd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostOd;

  @JsonProperty("platnostDo")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostDo;

  public CiselnikInputDetail kodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
    return this;
  }

  /**
   * Get kodCiselnika
   * @return kodCiselnika
  */
  @NotNull @Pattern(regexp = "[a-zA-Z0-9]+") @Size(min = 1, max = 100) 
  @Schema(name = "kodCiselnika", required = true)
  public String getKodCiselnika() {
    return kodCiselnika;
  }

  public void setKodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
  }

  public CiselnikInputDetail nazovCiselnika(String nazovCiselnika) {
    this.nazovCiselnika = nazovCiselnika;
    return this;
  }

  /**
   * Get nazovCiselnika
   * @return nazovCiselnika
  */
  @NotNull @Size(max = 256) 
  @Schema(name = "nazovCiselnika", required = true)
  public String getNazovCiselnika() {
    return nazovCiselnika;
  }

  public void setNazovCiselnika(String nazovCiselnika) {
    this.nazovCiselnika = nazovCiselnika;
  }

  public CiselnikInputDetail externyKod(String externyKod) {
    this.externyKod = externyKod;
    return this;
  }

  /**
   * Get externyKod
   * @return externyKod
  */
  @Pattern(regexp = "[a-zA-Z0-9]+") @Size(min = 1, max = 100) 
  @Schema(name = "externyKod", required = false)
  public String getExternyKod() {
    return externyKod;
  }

  public void setExternyKod(String externyKod) {
    this.externyKod = externyKod;
  }

  public CiselnikInputDetail platnostOd(LocalDate platnostOd) {
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

  public CiselnikInputDetail platnostDo(LocalDate platnostDo) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CiselnikInputDetail ciselnikInputDetail = (CiselnikInputDetail) o;
    return Objects.equals(this.kodCiselnika, ciselnikInputDetail.kodCiselnika) &&
        Objects.equals(this.nazovCiselnika, ciselnikInputDetail.nazovCiselnika) &&
        Objects.equals(this.externyKod, ciselnikInputDetail.externyKod) &&
        Objects.equals(this.platnostOd, ciselnikInputDetail.platnostOd) &&
        Objects.equals(this.platnostDo, ciselnikInputDetail.platnostDo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kodCiselnika, nazovCiselnika, externyKod, platnostOd, platnostDo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CiselnikInputDetail {\n");
    sb.append("    kodCiselnika: ").append(toIndentedString(kodCiselnika)).append("\n");
    sb.append("    nazovCiselnika: ").append(toIndentedString(nazovCiselnika)).append("\n");
    sb.append("    externyKod: ").append(toIndentedString(externyKod)).append("\n");
    sb.append("    platnostOd: ").append(toIndentedString(platnostOd)).append("\n");
    sb.append("    platnostDo: ").append(toIndentedString(platnostDo)).append("\n");
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

