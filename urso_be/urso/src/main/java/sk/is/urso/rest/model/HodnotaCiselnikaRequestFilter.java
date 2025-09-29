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
 * HodnotaCiselnikaRequestFilter
 */

@JsonTypeName("hodnotaCiselnikaRequestFilter")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class HodnotaCiselnikaRequestFilter {

  @JsonProperty("kodPolozky")
  private String kodPolozky;

  @JsonProperty("nazovPolozky")
  private String nazovPolozky;

  @JsonProperty("kodCiselnika")
  private String kodCiselnika;

  @JsonProperty("kodCiselnikaEQUAL")
  private String kodCiselnikaEQUAL;

  @JsonProperty("ciselnik$id")
  @org.alfa.specification.Mapping(value="ciselnik.id", filterType=org.alfa.specification.FilterType.EQUAL)
  private Long ciselnik$id;

  @JsonProperty("dodatocnyObsah")
  private String dodatocnyObsah;

  @JsonProperty("poradie")
  private Long poradie;

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

  public HodnotaCiselnikaRequestFilter kodPolozky(String kodPolozky) {
    this.kodPolozky = kodPolozky;
    return this;
  }

  /**
   * Get kodPolozky
   * @return kodPolozky
  */
  @Size(min = 1, max = 255) 
  @Schema(name = "kodPolozky", required = false)
  public String getKodPolozky() {
    return kodPolozky;
  }

  public void setKodPolozky(String kodPolozky) {
    this.kodPolozky = kodPolozky;
  }

  public HodnotaCiselnikaRequestFilter nazovPolozky(String nazovPolozky) {
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

  public HodnotaCiselnikaRequestFilter kodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
    return this;
  }

  /**
   * Get kodCiselnika
   * @return kodCiselnika
  */
  @Size(max = 256) 
  @Schema(name = "kodCiselnika", required = false)
  public String getKodCiselnika() {
    return kodCiselnika;
  }

  public void setKodCiselnika(String kodCiselnika) {
    this.kodCiselnika = kodCiselnika;
  }

  public HodnotaCiselnikaRequestFilter kodCiselnikaEQUAL(String kodCiselnikaEQUAL) {
    this.kodCiselnikaEQUAL = kodCiselnikaEQUAL;
    return this;
  }

  /**
   * Get kodCiselnikaEQUAL
   * @return kodCiselnikaEQUAL
   */
  @Pattern(regexp = "[a-zA-Z0-9]+") @Size(min = 1, max = 100)
  @Schema(name = "kodCiselnikaEQUAL", accessMode = Schema.AccessMode.READ_ONLY, required = false)
  public String getKodCiselnikaEQUAL() {
    return kodCiselnikaEQUAL;
  }

  public void setKodCiselnikaEQUAL(String kodCiselnikaEQUAL) {
    this.kodCiselnikaEQUAL = kodCiselnikaEQUAL;
  }

  public HodnotaCiselnikaRequestFilter ciselnik$id(Long ciselnik$id) {
    this.ciselnik$id = ciselnik$id;
    return this;
  }

  /**
   * Get ciselnik$id
   * @return ciselnik$id
  */
  
  @Schema(name = "ciselnik$id", required = false)
  public Long getCiselnik$id() {
    return ciselnik$id;
  }

  public void setCiselnik$id(Long ciselnik$id) {
    this.ciselnik$id = ciselnik$id;
  }

  public HodnotaCiselnikaRequestFilter dodatocnyObsah(String dodatocnyObsah) {
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

  public HodnotaCiselnikaRequestFilter poradie(Long poradie) {
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

  public HodnotaCiselnikaRequestFilter platnostOdFROM(LocalDate platnostOdFROM) {
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

  public HodnotaCiselnikaRequestFilter platnostOdTO(LocalDate platnostOdTO) {
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

  public HodnotaCiselnikaRequestFilter platnostDoFROM(LocalDate platnostDoFROM) {
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

  public HodnotaCiselnikaRequestFilter platnostDoTO(LocalDate platnostDoTO) {
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
    HodnotaCiselnikaRequestFilter hodnotaCiselnikaRequestFilter = (HodnotaCiselnikaRequestFilter) o;
    return Objects.equals(this.kodPolozky, hodnotaCiselnikaRequestFilter.kodPolozky) &&
        Objects.equals(this.nazovPolozky, hodnotaCiselnikaRequestFilter.nazovPolozky) &&
        Objects.equals(this.kodCiselnika, hodnotaCiselnikaRequestFilter.kodCiselnika) &&
        Objects.equals(this.ciselnik$id, hodnotaCiselnikaRequestFilter.ciselnik$id) &&
        Objects.equals(this.dodatocnyObsah, hodnotaCiselnikaRequestFilter.dodatocnyObsah) &&
        Objects.equals(this.poradie, hodnotaCiselnikaRequestFilter.poradie) &&
        Objects.equals(this.platnostOdFROM, hodnotaCiselnikaRequestFilter.platnostOdFROM) &&
        Objects.equals(this.platnostOdTO, hodnotaCiselnikaRequestFilter.platnostOdTO) &&
        Objects.equals(this.platnostDoFROM, hodnotaCiselnikaRequestFilter.platnostDoFROM) &&
        Objects.equals(this.platnostDoTO, hodnotaCiselnikaRequestFilter.platnostDoTO);
  }

  @Override
  public int hashCode() {
    return Objects.hash(kodPolozky, nazovPolozky, kodCiselnika, ciselnik$id, dodatocnyObsah, poradie, platnostOdFROM, platnostOdTO, platnostDoFROM, platnostDoTO);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HodnotaCiselnikaRequestFilter {\n");
    sb.append("    kodPolozky: ").append(toIndentedString(kodPolozky)).append("\n");
    sb.append("    nazovPolozky: ").append(toIndentedString(nazovPolozky)).append("\n");
    sb.append("    kodCiselnika: ").append(toIndentedString(kodCiselnika)).append("\n");
    sb.append("    ciselnik$id: ").append(toIndentedString(ciselnik$id)).append("\n");
    sb.append("    dodatocnyObsah: ").append(toIndentedString(dodatocnyObsah)).append("\n");
    sb.append("    poradie: ").append(toIndentedString(poradie)).append("\n");
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

