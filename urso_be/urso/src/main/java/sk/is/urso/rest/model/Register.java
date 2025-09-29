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
 * Register
 */

@JsonTypeName("register")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class Register {

  @JsonProperty("registerId")
  private String registerId;

  @JsonProperty("verziaId")
  private Integer verziaId;

  @JsonProperty("nazov")
  private String nazov;

  @JsonProperty("popis")
  private String popis;

  @JsonProperty("platnostOd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostOd;

  @JsonProperty("platnostDo")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostDo;

  @JsonProperty("externy")
  private Boolean externy;

  @JsonProperty("overeny")
  private Boolean overeny;

  @JsonProperty("povoleny")
  private Boolean povoleny;

  @JsonProperty("identifikovany")
  private Boolean identifikovany;

  @JsonProperty("gdprRelevantny")
  private Boolean gdprRelevantny;

  public Register registerId(String registerId) {
    this.registerId = registerId;
    return this;
  }

  /**
   * Get registerId
   * @return registerId
  */
  @NotNull @Size(min = 1, max = 256) 
  @Schema(name = "registerId", required = true)
  public String getRegisterId() {
    return registerId;
  }

  public void setRegisterId(String registerId) {
    this.registerId = registerId;
  }

  public Register verziaId(Integer verziaId) {
    this.verziaId = verziaId;
    return this;
  }

  /**
   * Get verziaId
   * @return verziaId
  */
  @NotNull 
  @Schema(name = "verziaId", required = true)
  public Integer getVerziaId() {
    return verziaId;
  }

  public void setVerziaId(Integer verziaId) {
    this.verziaId = verziaId;
  }

  public Register nazov(String nazov) {
    this.nazov = nazov;
    return this;
  }

  /**
   * Get nazov
   * @return nazov
  */
  @NotNull 
  @Schema(name = "nazov", required = true)
  public String getNazov() {
    return nazov;
  }

  public void setNazov(String nazov) {
    this.nazov = nazov;
  }

  public Register popis(String popis) {
    this.popis = popis;
    return this;
  }

  /**
   * Get popis
   * @return popis
  */
  
  @Schema(name = "popis", required = false)
  public String getPopis() {
    return popis;
  }

  public void setPopis(String popis) {
    this.popis = popis;
  }

  public Register platnostOd(LocalDate platnostOd) {
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

  public Register platnostDo(LocalDate platnostDo) {
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

  public Register externy(Boolean externy) {
    this.externy = externy;
    return this;
  }

  /**
   * Get externy
   * @return externy
  */
  @NotNull 
  @Schema(name = "externy", required = true)
  public Boolean getExterny() {
    return externy;
  }

  public void setExterny(Boolean externy) {
    this.externy = externy;
  }

  public Register overeny(Boolean overeny) {
    this.overeny = overeny;
    return this;
  }

  /**
   * Get overeny
   * @return overeny
  */
  @NotNull 
  @Schema(name = "overeny", required = true)
  public Boolean getOvereny() {
    return overeny;
  }

  public void setOvereny(Boolean overeny) {
    this.overeny = overeny;
  }

  public Register povoleny(Boolean povoleny) {
    this.povoleny = povoleny;
    return this;
  }

  /**
   * Get povoleny
   * @return povoleny
  */
  @NotNull 
  @Schema(name = "povoleny", required = true)
  public Boolean getPovoleny() {
    return povoleny;
  }

  public void setPovoleny(Boolean povoleny) {
    this.povoleny = povoleny;
  }

  public Register identifikovany(Boolean identifikovany) {
    this.identifikovany = identifikovany;
    return this;
  }

  /**
   * Get identifikovany
   * @return identifikovany
  */
  @NotNull 
  @Schema(name = "identifikovany", required = true)
  public Boolean getIdentifikovany() {
    return identifikovany;
  }

  public void setIdentifikovany(Boolean identifikovany) {
    this.identifikovany = identifikovany;
  }

  public Register gdprRelevantny(Boolean gdprRelevantny) {
    this.gdprRelevantny = gdprRelevantny;
    return this;
  }

  /**
   * Get gdprRelevantny
   * @return gdprRelevantny
  */
  @NotNull 
  @Schema(name = "gdprRelevantny", required = true)
  public Boolean getGdprRelevantny() {
    return gdprRelevantny;
  }

  public void setGdprRelevantny(Boolean gdprRelevantny) {
    this.gdprRelevantny = gdprRelevantny;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Register register = (Register) o;
    return Objects.equals(this.registerId, register.registerId) &&
        Objects.equals(this.verziaId, register.verziaId) &&
        Objects.equals(this.nazov, register.nazov) &&
        Objects.equals(this.popis, register.popis) &&
        Objects.equals(this.platnostOd, register.platnostOd) &&
        Objects.equals(this.platnostDo, register.platnostDo) &&
        Objects.equals(this.externy, register.externy) &&
        Objects.equals(this.overeny, register.overeny) &&
        Objects.equals(this.povoleny, register.povoleny) &&
        Objects.equals(this.identifikovany, register.identifikovany) &&
        Objects.equals(this.gdprRelevantny, register.gdprRelevantny);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaId, nazov, popis, platnostOd, platnostDo, externy, overeny, povoleny, identifikovany, gdprRelevantny);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Register {\n");
    sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
    sb.append("    verziaId: ").append(toIndentedString(verziaId)).append("\n");
    sb.append("    nazov: ").append(toIndentedString(nazov)).append("\n");
    sb.append("    popis: ").append(toIndentedString(popis)).append("\n");
    sb.append("    platnostOd: ").append(toIndentedString(platnostOd)).append("\n");
    sb.append("    platnostDo: ").append(toIndentedString(platnostDo)).append("\n");
    sb.append("    externy: ").append(toIndentedString(externy)).append("\n");
    sb.append("    overeny: ").append(toIndentedString(overeny)).append("\n");
    sb.append("    povoleny: ").append(toIndentedString(povoleny)).append("\n");
    sb.append("    identifikovany: ").append(toIndentedString(identifikovany)).append("\n");
    sb.append("    gdprRelevantny: ").append(toIndentedString(gdprRelevantny)).append("\n");
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

