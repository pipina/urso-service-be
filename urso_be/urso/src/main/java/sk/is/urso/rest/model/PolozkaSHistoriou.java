package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Objects;

/**
 * PolozkaSHistoriou
 */

@JsonTypeName("polozkaSHistoriou")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class PolozkaSHistoriou {

  @JsonProperty("ucinnostOd")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate ucinnostOd;

  @JsonProperty("ucinnostDo")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate ucinnostDo;

  @JsonProperty("sekvencia")
  private Integer sekvencia;

  @JsonProperty("platna")
  private Boolean platna;

  @JsonProperty("aktualna")
  private Boolean aktualna;

  public PolozkaSHistoriou ucinnostOd(LocalDate ucinnostOd) {
    this.ucinnostOd = ucinnostOd;
    return this;
  }

  /**
   * Get ucinnostOd
   * @return ucinnostOd
  */
  @Valid 
  @Schema(name = "ucinnostOd", required = false)
  public LocalDate getUcinnostOd() {
    return ucinnostOd;
  }

  public void setUcinnostOd(LocalDate ucinnostOd) {
    this.ucinnostOd = ucinnostOd;
  }

  public PolozkaSHistoriou ucinnostDo(LocalDate ucinnostDo) {
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

  public PolozkaSHistoriou sekvencia(Integer sekvencia) {
    this.sekvencia = sekvencia;
    return this;
  }

  /**
   * Get sekvencia
   * @return sekvencia
  */
  
  @Schema(name = "sekvencia", required = false)
  public Integer getSekvencia() {
    return sekvencia;
  }

  public void setSekvencia(Integer sekvencia) {
    this.sekvencia = sekvencia;
  }

  public PolozkaSHistoriou platna(Boolean platna) {
    this.platna = platna;
    return this;
  }

  /**
   * Get platna
   * @return platna
  */
  
  @Schema(name = "platna", required = false)
  public Boolean getPlatna() {
    return platna;
  }

  public void setPlatna(Boolean platna) {
    this.platna = platna;
  }

  public PolozkaSHistoriou aktualna(Boolean aktualna) {
    this.aktualna = aktualna;
    return this;
  }

  /**
   * Get aktualna
   * @return aktualna
  */
  
  @Schema(name = "aktualna", required = false)
  public Boolean getAktualna() {
    return aktualna;
  }

  public void setAktualna(Boolean aktualna) {
    this.aktualna = aktualna;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PolozkaSHistoriou polozkaSHistoriou = (PolozkaSHistoriou) o;
    return Objects.equals(this.ucinnostOd, polozkaSHistoriou.ucinnostOd) &&
        Objects.equals(this.ucinnostDo, polozkaSHistoriou.ucinnostDo) &&
        Objects.equals(this.sekvencia, polozkaSHistoriou.sekvencia) &&
        Objects.equals(this.platna, polozkaSHistoriou.platna) &&
        Objects.equals(this.aktualna, polozkaSHistoriou.aktualna);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ucinnostOd, ucinnostDo, sekvencia, platna, aktualna);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PolozkaSHistoriou {\n");
    sb.append("    ucinnostOd: ").append(toIndentedString(ucinnostOd)).append("\n");
    sb.append("    ucinnostDo: ").append(toIndentedString(ucinnostDo)).append("\n");
    sb.append("    sekvencia: ").append(toIndentedString(sekvencia)).append("\n");
    sb.append("    platna: ").append(toIndentedString(platna)).append("\n");
    sb.append("    aktualna: ").append(toIndentedString(aktualna)).append("\n");
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

