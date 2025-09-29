package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * SubjektVystupnyDetail
 */

@JsonTypeName("subjektVystupnyDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-13T14:59:21.099708+01:00[Europe/Berlin]")
public class SubjektVystupnyDetail {

  @JsonProperty("navratovyKodOperacie")
  private CsruNavratovyKodOperacieEnum navratovyKodOperacie;

  @JsonProperty("chybovaHlaskaOperacie")
  private String chybovaHlaskaOperacie;

  @JsonProperty("navratovyKodStavu")
  private CsruNavratovyKodOperacieEnum navratovyKodStavu;

  @JsonProperty("chybovaHlaskaStavu")
  private String chybovaHlaskaStavu;

  @JsonProperty("stavZiadosti")
  private CsruStavZiadostiEnum stavZiadosti;

  @JsonProperty("nedoplatky")
  @Valid
  private List<Nedoplatok> nedoplatky = null;

  @JsonProperty("maNedoplatok")
  private Boolean maNedoplatok;

  public SubjektVystupnyDetail navratovyKodOperacie(CsruNavratovyKodOperacieEnum navratovyKodOperacie) {
    this.navratovyKodOperacie = navratovyKodOperacie;
    return this;
  }

  /**
   * Get navratovyKodOperacie
   * @return navratovyKodOperacie
  */
  @Valid 
  @Schema(name = "navratovyKodOperacie", required = false)
  public CsruNavratovyKodOperacieEnum getNavratovyKodOperacie() {
    return navratovyKodOperacie;
  }

  public void setNavratovyKodOperacie(CsruNavratovyKodOperacieEnum navratovyKodOperacie) {
    this.navratovyKodOperacie = navratovyKodOperacie;
  }

  public SubjektVystupnyDetail chybovaHlaskaOperacie(String chybovaHlaskaOperacie) {
    this.chybovaHlaskaOperacie = chybovaHlaskaOperacie;
    return this;
  }

  /**
   * Get chybovaHlaskaOperacie
   * @return chybovaHlaskaOperacie
  */
  
  @Schema(name = "chybovaHlaskaOperacie", required = false)
  public String getChybovaHlaskaOperacie() {
    return chybovaHlaskaOperacie;
  }

  public void setChybovaHlaskaOperacie(String chybovaHlaskaOperacie) {
    this.chybovaHlaskaOperacie = chybovaHlaskaOperacie;
  }

  public SubjektVystupnyDetail navratovyKodStavu(CsruNavratovyKodOperacieEnum navratovyKodStavu) {
    this.navratovyKodStavu = navratovyKodStavu;
    return this;
  }

  /**
   * Get navratovyKodStavu
   * @return navratovyKodStavu
  */
  @Valid 
  @Schema(name = "navratovyKodStavu", required = false)
  public CsruNavratovyKodOperacieEnum getNavratovyKodStavu() {
    return navratovyKodStavu;
  }

  public void setNavratovyKodStavu(CsruNavratovyKodOperacieEnum navratovyKodStavu) {
    this.navratovyKodStavu = navratovyKodStavu;
  }

  public SubjektVystupnyDetail chybovaHlaskaStavu(String chybovaHlaskaStavu) {
    this.chybovaHlaskaStavu = chybovaHlaskaStavu;
    return this;
  }

  /**
   * Get chybovaHlaskaStavu
   * @return chybovaHlaskaStavu
  */
  
  @Schema(name = "chybovaHlaskaStavu", required = false)
  public String getChybovaHlaskaStavu() {
    return chybovaHlaskaStavu;
  }

  public void setChybovaHlaskaStavu(String chybovaHlaskaStavu) {
    this.chybovaHlaskaStavu = chybovaHlaskaStavu;
  }

  public SubjektVystupnyDetail stavZiadosti(CsruStavZiadostiEnum stavZiadosti) {
    this.stavZiadosti = stavZiadosti;
    return this;
  }

  /**
   * Get stavZiadosti
   * @return stavZiadosti
  */
  @Valid 
  @Schema(name = "stavZiadosti", required = false)
  public CsruStavZiadostiEnum getStavZiadosti() {
    return stavZiadosti;
  }

  public void setStavZiadosti(CsruStavZiadostiEnum stavZiadosti) {
    this.stavZiadosti = stavZiadosti;
  }

  public SubjektVystupnyDetail nedoplatky(List<Nedoplatok> nedoplatky) {
    this.nedoplatky = nedoplatky;
    return this;
  }

  public SubjektVystupnyDetail addNedoplatkyItem(Nedoplatok nedoplatkyItem) {
    if (this.nedoplatky == null) {
      this.nedoplatky = new ArrayList<>();
    }
    this.nedoplatky.add(nedoplatkyItem);
    return this;
  }

  /**
   * Get nedoplatky
   * @return nedoplatky
  */
  @Valid 
  @Schema(name = "nedoplatky", required = false)
  public List<Nedoplatok> getNedoplatky() {
    return nedoplatky;
  }

  public void setNedoplatky(List<Nedoplatok> nedoplatky) {
    this.nedoplatky = nedoplatky;
  }

  public SubjektVystupnyDetail maNedoplatok(Boolean maNedoplatok) {
    this.maNedoplatok = maNedoplatok;
    return this;
  }

  /**
   * Get maNedoplatok
   * @return maNedoplatok
  */
  
  @Schema(name = "maNedoplatok", required = false)
  public Boolean getMaNedoplatok() {
    return maNedoplatok;
  }

  public void setMaNedoplatok(Boolean maNedoplatok) {
    this.maNedoplatok = maNedoplatok;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubjektVystupnyDetail subjektVystupnyDetail = (SubjektVystupnyDetail) o;
    return Objects.equals(this.navratovyKodOperacie, subjektVystupnyDetail.navratovyKodOperacie) &&
        Objects.equals(this.chybovaHlaskaOperacie, subjektVystupnyDetail.chybovaHlaskaOperacie) &&
        Objects.equals(this.navratovyKodStavu, subjektVystupnyDetail.navratovyKodStavu) &&
        Objects.equals(this.chybovaHlaskaStavu, subjektVystupnyDetail.chybovaHlaskaStavu) &&
        Objects.equals(this.stavZiadosti, subjektVystupnyDetail.stavZiadosti) &&
        Objects.equals(this.nedoplatky, subjektVystupnyDetail.nedoplatky) &&
        Objects.equals(this.maNedoplatok, subjektVystupnyDetail.maNedoplatok);
  }

  @Override
  public int hashCode() {
    return Objects.hash(navratovyKodOperacie, chybovaHlaskaOperacie, navratovyKodStavu, chybovaHlaskaStavu, stavZiadosti, nedoplatky, maNedoplatok);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubjektVystupnyDetail {\n");
    sb.append("    navratovyKodOperacie: ").append(toIndentedString(navratovyKodOperacie)).append("\n");
    sb.append("    chybovaHlaskaOperacie: ").append(toIndentedString(chybovaHlaskaOperacie)).append("\n");
    sb.append("    navratovyKodStavu: ").append(toIndentedString(navratovyKodStavu)).append("\n");
    sb.append("    chybovaHlaskaStavu: ").append(toIndentedString(chybovaHlaskaStavu)).append("\n");
    sb.append("    stavZiadosti: ").append(toIndentedString(stavZiadosti)).append("\n");
    sb.append("    nedoplatky: ").append(toIndentedString(nedoplatky)).append("\n");
    sb.append("    maNedoplatok: ").append(toIndentedString(maNedoplatok)).append("\n");
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

