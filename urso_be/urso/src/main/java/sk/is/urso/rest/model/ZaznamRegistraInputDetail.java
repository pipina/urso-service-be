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
 * ZaznamRegistraInputDetail
 */

@JsonTypeName("zaznamRegistraInputDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraInputDetail {

  @JsonProperty("registerId")
  private String registerId;

  @JsonProperty("verziaRegistraId")
  private Integer verziaRegistraId;

  @JsonProperty("platnostOd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate platnostOd;

  @JsonProperty("ucinnostOd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate ucinnostOd;

  @JsonProperty("ucinnostDo")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate ucinnostDo;

  @JsonProperty("platnost")
  private Boolean platnost;

  @JsonProperty("data")
  private String data;

  @JsonProperty("modul")
  private String modul;

  @JsonProperty("pouzivatel")
  private String pouzivatel;

  @JsonProperty("zaznamId")
  private Long zaznamId;

  public ZaznamRegistraInputDetail registerId(String registerId) {
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

  public ZaznamRegistraInputDetail verziaRegistraId(Integer verziaRegistraId) {
    this.verziaRegistraId = verziaRegistraId;
    return this;
  }

  /**
   * Get verziaRegistraId
   * @return verziaRegistraId
  */
  @NotNull 
  @Schema(name = "verziaRegistraId", required = true)
  public Integer getVerziaRegistraId() {
    return verziaRegistraId;
  }

  public void setVerziaRegistraId(Integer verziaRegistraId) {
    this.verziaRegistraId = verziaRegistraId;
  }

  public ZaznamRegistraInputDetail platnostOd(LocalDate platnostOd) {
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

  public ZaznamRegistraInputDetail ucinnostOd(LocalDate ucinnostOd) {
    this.ucinnostOd = ucinnostOd;
    return this;
  }

  /**
   * Pri update ak nie je vyplnené sa nemení. Pri insert ak nie je vyplnené tak sa nastaví na dnešok.
   * @return ucinnostOd
  */
  @Valid 
  @Schema(name = "ucinnostOd", description = "Pri update ak nie je vyplnené sa nemení. Pri insert ak nie je vyplnené tak sa nastaví na dnešok.", required = false)
  public LocalDate getUcinnostOd() {
    return ucinnostOd;
  }

  public void setUcinnostOd(LocalDate ucinnostOd) {
    this.ucinnostOd = ucinnostOd;
  }

  public ZaznamRegistraInputDetail ucinnostDo(LocalDate ucinnostDo) {
    this.ucinnostDo = ucinnostDo;
    return this;
  }

  /**
   * Get ucinnostDo
   * @return ucinnostDo
  */
  @Valid 
  @Schema(name = "ucinnostDo", required = false)
  public LocalDate getUcinnostDo() {
    return ucinnostDo;
  }

  public void setUcinnostDo(LocalDate ucinnostDo) {
    this.ucinnostDo = ucinnostDo;
  }

  public ZaznamRegistraInputDetail platnost(Boolean platnost) {
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

  public ZaznamRegistraInputDetail data(String data) {
    this.data = data;
    return this;
  }

  /**
   * Get data
   * @return data
  */
  @NotNull 
  @Schema(name = "data", required = true)
  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public ZaznamRegistraInputDetail modul(String modul) {
    this.modul = modul;
    return this;
  }

  /**
   * Get modul
   * @return modul
  */
  @Size(min = 2, max = 8) 
  @Schema(name = "modul", required = false)
  public String getModul() {
    return modul;
  }

  public void setModul(String modul) {
    this.modul = modul;
  }

  public ZaznamRegistraInputDetail pouzivatel(String pouzivatel) {
    this.pouzivatel = pouzivatel;
    return this;
  }

  /**
   * Get pouzivatel
   * @return pouzivatel
  */
  @Size(max = 256) 
  @Schema(name = "pouzivatel", required = false)
  public String getPouzivatel() {
    return pouzivatel;
  }

  public void setPouzivatel(String pouzivatel) {
    this.pouzivatel = pouzivatel;
  }

  public ZaznamRegistraInputDetail zaznamId(Long zaznamId) {
    this.zaznamId = zaznamId;
    return this;
  }

  /**
   * Get zaznamId
   * @return zaznamId
  */
  @NotNull 
  @Schema(name = "zaznamId", required = true)
  public Long getZaznamId() {
    return zaznamId;
  }

  public void setZaznamId(Long zaznamId) {
    this.zaznamId = zaznamId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraInputDetail zaznamRegistraInputDetail = (ZaznamRegistraInputDetail) o;
    return Objects.equals(this.registerId, zaznamRegistraInputDetail.registerId) &&
        Objects.equals(this.verziaRegistraId, zaznamRegistraInputDetail.verziaRegistraId) &&
        Objects.equals(this.platnostOd, zaznamRegistraInputDetail.platnostOd) &&
        Objects.equals(this.ucinnostOd, zaznamRegistraInputDetail.ucinnostOd) &&
        Objects.equals(this.ucinnostDo, zaznamRegistraInputDetail.ucinnostDo) &&
        Objects.equals(this.platnost, zaznamRegistraInputDetail.platnost) &&
        Objects.equals(this.data, zaznamRegistraInputDetail.data) &&
        Objects.equals(this.modul, zaznamRegistraInputDetail.modul) &&
        Objects.equals(this.pouzivatel, zaznamRegistraInputDetail.pouzivatel) &&
        Objects.equals(this.zaznamId, zaznamRegistraInputDetail.zaznamId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaRegistraId, platnostOd, ucinnostOd, ucinnostDo, platnost, data, modul, pouzivatel, zaznamId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraInputDetail {\n");
    sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
    sb.append("    verziaRegistraId: ").append(toIndentedString(verziaRegistraId)).append("\n");
    sb.append("    platnostOd: ").append(toIndentedString(platnostOd)).append("\n");
    sb.append("    ucinnostOd: ").append(toIndentedString(ucinnostOd)).append("\n");
    sb.append("    ucinnostDo: ").append(toIndentedString(ucinnostDo)).append("\n");
    sb.append("    platnost: ").append(toIndentedString(platnost)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    modul: ").append(toIndentedString(modul)).append("\n");
    sb.append("    pouzivatel: ").append(toIndentedString(pouzivatel)).append("\n");
    sb.append("    zaznamId: ").append(toIndentedString(zaznamId)).append("\n");
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

