package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

/**
 * HodnotaCiselnikaSimpleOutput
 */

@JsonTypeName("hodnotaCiselnikaSimpleOutput")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class HodnotaCiselnikaSimpleOutput {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("kodPolozky")
  private String kodPolozky;

  @JsonProperty("nazovPolozky")
  private String nazovPolozky;

  @JsonProperty("kodCiselnika")
  private String kodCiselnika;

  @JsonProperty("dodatocnyObsah")
  private String dodatocnyObsah;

  @JsonProperty("poradie")
  private Long poradie;

  @JsonProperty("platnostOd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostOd;

  @JsonProperty("platnostDo")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostDo;

  public HodnotaCiselnikaSimpleOutput id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", required = false)
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public HodnotaCiselnikaSimpleOutput kodPolozky(String kodPolozky) {
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

  public HodnotaCiselnikaSimpleOutput nazovPolozky(String nazovPolozky) {
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

  public HodnotaCiselnikaSimpleOutput kodCiselnika(String kodCiselnika) {
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

  public HodnotaCiselnikaSimpleOutput dodatocnyObsah(String dodatocnyObsah) {
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

  public HodnotaCiselnikaSimpleOutput poradie(Long poradie) {
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

  public HodnotaCiselnikaSimpleOutput platnostOd(LocalDate platnostOd) {
    this.platnostOd = platnostOd;
    return this;
  }

  /**
   * Get platnostOd
   * @return platnostOd
  */
  @Valid 
  @Schema(name = "platnostOd", required = false)
  public LocalDate getPlatnostOd() {
    return platnostOd;
  }

  public void setPlatnostOd(LocalDate platnostOd) {
    this.platnostOd = platnostOd;
  }

  public HodnotaCiselnikaSimpleOutput platnostDo(LocalDate platnostDo) {
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
    HodnotaCiselnikaSimpleOutput hodnotaCiselnikaSimpleOutput = (HodnotaCiselnikaSimpleOutput) o;
    return Objects.equals(this.id, hodnotaCiselnikaSimpleOutput.id) &&
        Objects.equals(this.kodPolozky, hodnotaCiselnikaSimpleOutput.kodPolozky) &&
        Objects.equals(this.nazovPolozky, hodnotaCiselnikaSimpleOutput.nazovPolozky) &&
        Objects.equals(this.kodCiselnika, hodnotaCiselnikaSimpleOutput.kodCiselnika) &&
        Objects.equals(this.dodatocnyObsah, hodnotaCiselnikaSimpleOutput.dodatocnyObsah) &&
        Objects.equals(this.poradie, hodnotaCiselnikaSimpleOutput.poradie) &&
        Objects.equals(this.platnostOd, hodnotaCiselnikaSimpleOutput.platnostOd) &&
        Objects.equals(this.platnostDo, hodnotaCiselnikaSimpleOutput.platnostDo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, kodPolozky, nazovPolozky, kodCiselnika, dodatocnyObsah, poradie, platnostOd, platnostDo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HodnotaCiselnikaSimpleOutput {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    kodPolozky: ").append(toIndentedString(kodPolozky)).append("\n");
    sb.append("    nazovPolozky: ").append(toIndentedString(nazovPolozky)).append("\n");
    sb.append("    kodCiselnika: ").append(toIndentedString(kodCiselnika)).append("\n");
    sb.append("    dodatocnyObsah: ").append(toIndentedString(dodatocnyObsah)).append("\n");
    sb.append("    poradie: ").append(toIndentedString(poradie)).append("\n");
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

