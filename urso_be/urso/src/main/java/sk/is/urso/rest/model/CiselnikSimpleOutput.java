package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * CiselnikSimpleOutput
 */

@JsonTypeName("ciselnikSimpleOutput")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class CiselnikSimpleOutput {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("kodCiselnika")
  private String kodCiselnika;

  @JsonProperty("nazovCiselnika")
  private String nazovCiselnika;

  @JsonProperty("externyKod")
  private String externyKod;

  public CiselnikSimpleOutput id(Long id) {
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

  public CiselnikSimpleOutput kodCiselnika(String kodCiselnika) {
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

  public CiselnikSimpleOutput nazovCiselnika(String nazovCiselnika) {
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

  public CiselnikSimpleOutput externyKod(String externyKod) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CiselnikSimpleOutput ciselnikSimpleOutput = (CiselnikSimpleOutput) o;
    return Objects.equals(this.id, ciselnikSimpleOutput.id) &&
        Objects.equals(this.kodCiselnika, ciselnikSimpleOutput.kodCiselnika) &&
        Objects.equals(this.nazovCiselnika, ciselnikSimpleOutput.nazovCiselnika) &&
        Objects.equals(this.externyKod, ciselnikSimpleOutput.externyKod);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, kodCiselnika, nazovCiselnika, externyKod);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CiselnikSimpleOutput {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    kodCiselnika: ").append(toIndentedString(kodCiselnika)).append("\n");
    sb.append("    nazovCiselnika: ").append(toIndentedString(nazovCiselnika)).append("\n");
    sb.append("    externyKod: ").append(toIndentedString(externyKod)).append("\n");
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

