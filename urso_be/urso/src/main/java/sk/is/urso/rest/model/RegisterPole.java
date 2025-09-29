package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * RegisterPole
 */

@JsonTypeName("registerPole")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class RegisterPole {

  @JsonProperty("nazov")
  private String nazov;

  @JsonProperty("popis")
  private String popis;

  @JsonProperty("xPath")
  private String xPath;

  @JsonProperty("typHodnoty")
  private TypHodnotyEnum typHodnoty;

  @JsonProperty("funkcia")
  private Boolean funkcia;

  @JsonProperty("index")
  private Boolean index;

  @JsonProperty("vystup")
  private Boolean vystup;

  @JsonProperty("nazovZobrazenia")
  private String nazovZobrazenia;

  public RegisterPole nazov(String nazov) {
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

  public RegisterPole popis(String popis) {
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

  public RegisterPole xPath(String xPath) {
    this.xPath = xPath;
    return this;
  }

  /**
   * Get xPath
   * @return xPath
  */
  @NotNull 
  @Schema(name = "xPath", required = true)
  public String getxPath() {
    return xPath;
  }

  public void setxPath(String xPath) {
    this.xPath = xPath;
  }

  public RegisterPole typHodnoty(TypHodnotyEnum typHodnoty) {
    this.typHodnoty = typHodnoty;
    return this;
  }

  /**
   * Get typHodnoty
   * @return typHodnoty
  */
  @NotNull @Valid 
  @Schema(name = "typHodnoty", required = true)
  public TypHodnotyEnum getTypHodnoty() {
    return typHodnoty;
  }

  public void setTypHodnoty(TypHodnotyEnum typHodnoty) {
    this.typHodnoty = typHodnoty;
  }

  public RegisterPole funkcia(Boolean funkcia) {
    this.funkcia = funkcia;
    return this;
  }

  /**
   * Get funkcia
   * @return funkcia
  */
  @NotNull 
  @Schema(name = "funkcia", required = true)
  public Boolean getFunkcia() {
    return funkcia;
  }

  public void setFunkcia(Boolean funkcia) {
    this.funkcia = funkcia;
  }

  public RegisterPole index(Boolean index) {
    this.index = index;
    return this;
  }

  /**
   * Get index
   * @return index
  */
  @NotNull 
  @Schema(name = "index", required = true)
  public Boolean getIndex() {
    return index;
  }

  public void setIndex(Boolean index) {
    this.index = index;
  }

  public RegisterPole vystup(Boolean vystup) {
    this.vystup = vystup;
    return this;
  }

  /**
   * Get vystup
   * @return vystup
  */
  @NotNull 
  @Schema(name = "vystup", required = true)
  public Boolean getVystup() {
    return vystup;
  }

  public void setVystup(Boolean vystup) {
    this.vystup = vystup;
  }

  public RegisterPole nazovZobrazenia(String nazovZobrazenia) {
    this.nazovZobrazenia = nazovZobrazenia;
    return this;
  }

  /**
   * Get nazovZobrazenia
   * @return nazovZobrazenia
  */
  @NotNull 
  @Schema(name = "nazovZobrazenia", required = true)
  public String getNazovZobrazenia() {
    return nazovZobrazenia;
  }

  public void setNazovZobrazenia(String nazovZobrazenia) {
    this.nazovZobrazenia = nazovZobrazenia;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterPole registerPole = (RegisterPole) o;
    return Objects.equals(this.nazov, registerPole.nazov) &&
        Objects.equals(this.popis, registerPole.popis) &&
        Objects.equals(this.xPath, registerPole.xPath) &&
        Objects.equals(this.typHodnoty, registerPole.typHodnoty) &&
        Objects.equals(this.funkcia, registerPole.funkcia) &&
        Objects.equals(this.index, registerPole.index) &&
        Objects.equals(this.vystup, registerPole.vystup) &&
        Objects.equals(this.nazovZobrazenia, registerPole.nazovZobrazenia);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nazov, popis, xPath, typHodnoty, funkcia, index, vystup, nazovZobrazenia);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterPole {\n");
    sb.append("    nazov: ").append(toIndentedString(nazov)).append("\n");
    sb.append("    popis: ").append(toIndentedString(popis)).append("\n");
    sb.append("    xPath: ").append(toIndentedString(xPath)).append("\n");
    sb.append("    typHodnoty: ").append(toIndentedString(typHodnoty)).append("\n");
    sb.append("    funkcia: ").append(toIndentedString(funkcia)).append("\n");
    sb.append("    index: ").append(toIndentedString(index)).append("\n");
    sb.append("    vystup: ").append(toIndentedString(vystup)).append("\n");
    sb.append("    nazovZobrazenia: ").append(toIndentedString(nazovZobrazenia)).append("\n");
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

