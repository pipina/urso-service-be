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
 * Pomocny objekt pre definovanie spolocnych poli zaznamu registra ktore sa pouzivaju pri inserte ako aj pri aktualizacii a citani zaznamy registra.
 */

@Schema(name = "zaznamRegistraZakladneData", description = "Pomocny objekt pre definovanie spolocnych poli zaznamu registra ktore sa pouzivaju pri inserte ako aj pri aktualizacii a citani zaznamy registra.")
@JsonTypeName("zaznamRegistraZakladneData")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class ZaznamRegistraZakladneData {

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

  public ZaznamRegistraZakladneData registerId(String registerId) {
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

  public ZaznamRegistraZakladneData verziaRegistraId(Integer verziaRegistraId) {
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

  public ZaznamRegistraZakladneData platnostOd(LocalDate platnostOd) {
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

  public ZaznamRegistraZakladneData ucinnostOd(LocalDate ucinnostOd) {
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

  public ZaznamRegistraZakladneData ucinnostDo(LocalDate ucinnostDo) {
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

  public ZaznamRegistraZakladneData platnost(Boolean platnost) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ZaznamRegistraZakladneData zaznamRegistraZakladneData = (ZaznamRegistraZakladneData) o;
    return Objects.equals(this.registerId, zaznamRegistraZakladneData.registerId) &&
        Objects.equals(this.verziaRegistraId, zaznamRegistraZakladneData.verziaRegistraId) &&
        Objects.equals(this.platnostOd, zaznamRegistraZakladneData.platnostOd) &&
        Objects.equals(this.ucinnostOd, zaznamRegistraZakladneData.ucinnostOd) &&
        Objects.equals(this.ucinnostDo, zaznamRegistraZakladneData.ucinnostDo) &&
        Objects.equals(this.platnost, zaznamRegistraZakladneData.platnost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(registerId, verziaRegistraId, platnostOd, ucinnostOd, ucinnostDo, platnost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ZaznamRegistraZakladneData {\n");
    sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
    sb.append("    verziaRegistraId: ").append(toIndentedString(verziaRegistraId)).append("\n");
    sb.append("    platnostOd: ").append(toIndentedString(platnostOd)).append("\n");
    sb.append("    ucinnostOd: ").append(toIndentedString(ucinnostOd)).append("\n");
    sb.append("    ucinnostDo: ").append(toIndentedString(ucinnostDo)).append("\n");
    sb.append("    platnost: ").append(toIndentedString(platnost)).append("\n");
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

