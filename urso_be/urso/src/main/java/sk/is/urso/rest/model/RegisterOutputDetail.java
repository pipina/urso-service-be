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
 * RegisterOutputDetail
 */

@JsonTypeName("registerOutputDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class RegisterOutputDetail {

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

  @JsonProperty("polia")
  @Valid
  private List<RegisterPole> polia = null;

  @JsonProperty("gdpr")
  private RegisterDetailGdpr gdpr;

  public RegisterOutputDetail registerId(String registerId) {
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

  public RegisterOutputDetail verziaId(Integer verziaId) {
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

  public RegisterOutputDetail nazov(String nazov) {
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

  public RegisterOutputDetail popis(String popis) {
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

  public RegisterOutputDetail platnostOd(LocalDate platnostOd) {
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

  public RegisterOutputDetail platnostDo(LocalDate platnostDo) {
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

  public RegisterOutputDetail externy(Boolean externy) {
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

  public RegisterOutputDetail overeny(Boolean overeny) {
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

  public RegisterOutputDetail povoleny(Boolean povoleny) {
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

  public RegisterOutputDetail identifikovany(Boolean identifikovany) {
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

  public RegisterOutputDetail gdprRelevantny(Boolean gdprRelevantny) {
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

  public RegisterOutputDetail polia(List<RegisterPole> polia) {
    this.polia = polia;
    return this;
  }

  public RegisterOutputDetail addPoliaItem(RegisterPole poliaItem) {
    if (this.polia == null) {
      this.polia = new ArrayList<>();
    }
    this.polia.add(poliaItem);
    return this;
  }

  /**
   * Get polia
   * @return polia
  */
  @Valid 
  @Schema(name = "polia", required = false)
  public List<RegisterPole> getPolia() {
    return polia;
  }

  public void setPolia(List<RegisterPole> polia) {
    this.polia = polia;
  }

  public RegisterOutputDetail gdpr(RegisterDetailGdpr gdpr) {
    this.gdpr = gdpr;
    return this;
  }

  /**
   * Get gdpr
   * @return gdpr
  */
  @Valid 
  @Schema(name = "gdpr", required = false)
  public RegisterDetailGdpr getGdpr() {
    return gdpr;
  }

  public void setGdpr(RegisterDetailGdpr gdpr) {
    this.gdpr = gdpr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterOutputDetail registerOutputDetail = (RegisterOutputDetail) o;
    return Objects.equals(this.registerId, registerOutputDetail.registerId) &&
        Objects.equals(this.verziaId, registerOutputDetail.verziaId) &&
        Objects.equals(this.nazov, registerOutputDetail.nazov) &&
        Objects.equals(this.popis, registerOutputDetail.popis) &&
        Objects.equals(this.platnostOd, registerOutputDetail.platnostOd) &&
        Objects.equals(this.platnostDo, registerOutputDetail.platnostDo) &&
        Objects.equals(this.externy, registerOutputDetail.externy) &&
        Objects.equals(this.overeny, registerOutputDetail.overeny) &&
        Objects.equals(this.povoleny, registerOutputDetail.povoleny) &&
        Objects.equals(this.identifikovany, registerOutputDetail.identifikovany) &&
        Objects.equals(this.gdprRelevantny, registerOutputDetail.gdprRelevantny) &&
        Objects.equals(this.polia, registerOutputDetail.polia) &&
        Objects.equals(this.gdpr, registerOutputDetail.gdpr);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaId, nazov, popis, platnostOd, platnostDo, externy, overeny, povoleny, identifikovany, gdprRelevantny, polia, gdpr);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterOutputDetail {\n");
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
    sb.append("    polia: ").append(toIndentedString(polia)).append("\n");
    sb.append("    gdpr: ").append(toIndentedString(gdpr)).append("\n");
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

