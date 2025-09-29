package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * CiselnikRequestFilter
 */

@JsonTypeName("ciselnikRequestFilter")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class CiselnikRequestFilter {

  @JsonProperty("kodCiselnika")
  private String kodCiselnika;

  @JsonProperty("nazovCiselnika")
  private String nazovCiselnika;

  @JsonProperty("externyKod")
  private String externyKod;

  @JsonProperty("platnostOdFROM")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostOdFROM;

  @JsonProperty("platnostOdTO")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostOdTO;

  @JsonProperty("platnostDoFROM")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostDoFROM;

  @JsonProperty("platnostDoTO")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostDoTO;

  public CiselnikRequestFilter kodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
    return this;
  }

  /**
   * Get kodCiselnika
   * @return kodCiselnika
  */
  @Pattern(regexp = "[a-zA-Z0-9]+") @Size(min = 1, max = 100) 
  @Schema(name = "kodCiselnika", required = false)
  public String getKodCiselnika() {
    return kodCiselnika;
  }

  public void setKodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
  }

  public CiselnikRequestFilter nazovCiselnika(String nazovCiselnika) {
    this.nazovCiselnika = nazovCiselnika;
    return this;
  }

  /**
   * Get nazovCiselnika
   * @return nazovCiselnika
  */
  @Size(max = 256) 
  @Schema(name = "nazovCiselnika", required = false)
  public String getNazovCiselnika() {
    return nazovCiselnika;
  }

  public void setNazovCiselnika(String nazovCiselnika) {
    this.nazovCiselnika = nazovCiselnika;
  }

  public CiselnikRequestFilter externyKod(String externyKod) {
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

  public CiselnikRequestFilter platnostOdFROM(LocalDate platnostOdFROM) {
    this.platnostOdFROM = platnostOdFROM;
    return this;
  }

  /**
   * Get platnostOdFROM
   * @return platnostOdFROM
  */
  @Valid 
  @Schema(name = "platnostOdFROM", required = false)
  public LocalDate getPlatnostOdFROM() {
    return platnostOdFROM;
  }

  public void setPlatnostOdFROM(LocalDate platnostOdFROM) {
    this.platnostOdFROM = platnostOdFROM;
  }

  public CiselnikRequestFilter platnostOdTO(LocalDate platnostOdTO) {
    this.platnostOdTO = platnostOdTO;
    return this;
  }

  /**
   * Get platnostOdTO
   * @return platnostOdTO
  */
  @Valid 
  @Schema(name = "platnostOdTO", required = false)
  public LocalDate getPlatnostOdTO() {
    return platnostOdTO;
  }

  public void setPlatnostOdTO(LocalDate platnostOdTO) {
    this.platnostOdTO = platnostOdTO;
  }

  public CiselnikRequestFilter platnostDoFROM(LocalDate platnostDoFROM) {
    this.platnostDoFROM = platnostDoFROM;
    return this;
  }

  /**
   * Get platnostDoFROM
   * @return platnostDoFROM
  */
  @Valid 
  @Schema(name = "platnostDoFROM", required = false)
  public LocalDate getPlatnostDoFROM() {
    return platnostDoFROM;
  }

  public void setPlatnostDoFROM(LocalDate platnostDoFROM) {
    this.platnostDoFROM = platnostDoFROM;
  }

  public CiselnikRequestFilter platnostDoTO(LocalDate platnostDoTO) {
    this.platnostDoTO = platnostDoTO;
    return this;
  }

  /**
   * Get platnostDoTO
   * @return platnostDoTO
  */
  @Valid 
  @Schema(name = "platnostDoTO", required = false)
  public LocalDate getPlatnostDoTO() {
    return platnostDoTO;
  }

  public void setPlatnostDoTO(LocalDate platnostDoTO) {
    this.platnostDoTO = platnostDoTO;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CiselnikRequestFilter ciselnikRequestFilter = (CiselnikRequestFilter) o;
    return Objects.equals(this.kodCiselnika, ciselnikRequestFilter.kodCiselnika) &&
        Objects.equals(this.nazovCiselnika, ciselnikRequestFilter.nazovCiselnika) &&
        Objects.equals(this.externyKod, ciselnikRequestFilter.externyKod) &&
        Objects.equals(this.platnostOdFROM, ciselnikRequestFilter.platnostOdFROM) &&
        Objects.equals(this.platnostOdTO, ciselnikRequestFilter.platnostOdTO) &&
        Objects.equals(this.platnostDoFROM, ciselnikRequestFilter.platnostDoFROM) &&
        Objects.equals(this.platnostDoTO, ciselnikRequestFilter.platnostDoTO);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kodCiselnika, nazovCiselnika, externyKod, platnostOdFROM, platnostOdTO, platnostDoFROM, platnostDoTO);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CiselnikRequestFilter {\n");
    sb.append("    kodCiselnika: ").append(toIndentedString(kodCiselnika)).append("\n");
    sb.append("    nazovCiselnika: ").append(toIndentedString(nazovCiselnika)).append("\n");
    sb.append("    externyKod: ").append(toIndentedString(externyKod)).append("\n");
    sb.append("    platnostOdFROM: ").append(toIndentedString(platnostOdFROM)).append("\n");
    sb.append("    platnostOdTO: ").append(toIndentedString(platnostOdTO)).append("\n");
    sb.append("    platnostDoFROM: ").append(toIndentedString(platnostDoFROM)).append("\n");
    sb.append("    platnostDoTO: ").append(toIndentedString(platnostDoTO)).append("\n");
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

