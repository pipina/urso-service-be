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
 * Nedoplatok
 */

@JsonTypeName("nedoplatok")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T12:19:12.206284600+01:00[Europe/Bratislava]")
public class Nedoplatok {

    @JsonProperty("chybovyKod")
    private CsruNedoplatokChybovyKodEnum chybovyKod;

    @JsonProperty("chybovaSprava")
    private String chybovaSprava;

    @JsonProperty("zdravotnaPoistovna")
    private CsruZdravotnaPoistovnaEnum zdravotnaPoistovna;

    @JsonProperty("vysledokSpracovania")
    private CsruVysledokSpracovaniaEnum vysledokSpracovania;

    @JsonProperty("nedoplatok")
    private CsruNedoplatokEnum nedoplatok;

    @JsonProperty("vyskaNedoplatku")
    private String vyskaNedoplatku;

    @JsonProperty("mena")
    private String mena;

    @JsonProperty("datumNedoplatku")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate datumNedoplatku;

    @JsonProperty("druhDaneAleboPohladavky")
    private CsruDruhDaneAleboPohladavkyEnum druhDaneAleboPohladavky;

    @JsonProperty("popisOsbStavu")
    private String popisOsbStavu;

    public Nedoplatok chybovyKod(CsruNedoplatokChybovyKodEnum chybovyKod) {
        this.chybovyKod = chybovyKod;
        return this;
    }

    /**
     * Get chybovyKod
     *
     * @return chybovyKod
     */
    @Valid
    @Schema(name = "chybovyKod", required = false)
    public CsruNedoplatokChybovyKodEnum getChybovyKod() {
        return chybovyKod;
    }

    public void setChybovyKod(CsruNedoplatokChybovyKodEnum chybovyKod) {
        this.chybovyKod = chybovyKod;
    }

    public Nedoplatok chybovaSprava(String chybovaSprava) {
        this.chybovaSprava = chybovaSprava;
        return this;
    }

    /**
     * Get chybovaSprava
     *
     * @return chybovaSprava
     */

    @Schema(name = "chybovaSprava", required = false)
    public String getChybovaSprava() {
        return chybovaSprava;
    }

    public void setChybovaSprava(String chybovaSprava) {
        this.chybovaSprava = chybovaSprava;
    }

    public Nedoplatok zdravotnaPoistovna(CsruZdravotnaPoistovnaEnum zdravotnaPoistovna) {
        this.zdravotnaPoistovna = zdravotnaPoistovna;
        return this;
    }

    /**
     * Get zdravotnaPoistovna
     *
     * @return zdravotnaPoistovna
     */
    @Valid
    @Schema(name = "zdravotnaPoistovna", required = false)
    public CsruZdravotnaPoistovnaEnum getZdravotnaPoistovna() {
        return zdravotnaPoistovna;
    }

    public void setZdravotnaPoistovna(CsruZdravotnaPoistovnaEnum zdravotnaPoistovna) {
        this.zdravotnaPoistovna = zdravotnaPoistovna;
    }

    public Nedoplatok vysledokSpracovania(CsruVysledokSpracovaniaEnum vysledokSpracovania) {
        this.vysledokSpracovania = vysledokSpracovania;
        return this;
    }

    /**
     * Get vysledokSpracovania
     *
     * @return vysledokSpracovania
     */
    @Valid
    @Schema(name = "vysledokSpracovania", required = false)
    public CsruVysledokSpracovaniaEnum getVysledokSpracovania() {
        return vysledokSpracovania;
    }

    public void setVysledokSpracovania(CsruVysledokSpracovaniaEnum vysledokSpracovania) {
        this.vysledokSpracovania = vysledokSpracovania;
    }

    public Nedoplatok nedoplatok(CsruNedoplatokEnum nedoplatok) {
        this.nedoplatok = nedoplatok;
        return this;
    }

    /**
     * Get nedoplatok
     *
     * @return nedoplatok
     */
    @Valid
    @Schema(name = "nedoplatok", required = false)
    public CsruNedoplatokEnum getNedoplatok() {
        return nedoplatok;
    }

    public void setNedoplatok(CsruNedoplatokEnum nedoplatok) {
        this.nedoplatok = nedoplatok;
    }

    public Nedoplatok vyskaNedoplatku(String vyskaNedoplatku) {
        this.vyskaNedoplatku = vyskaNedoplatku;
        return this;
    }

    /**
     * Get vyskaNedoplatku
     *
     * @return vyskaNedoplatku
     */

    @Schema(name = "vyskaNedoplatku", required = false)
    public String getVyskaNedoplatku() {
        return vyskaNedoplatku;
    }

    public void setVyskaNedoplatku(String vyskaNedoplatku) {
        this.vyskaNedoplatku = vyskaNedoplatku;
    }

    public Nedoplatok mena(String mena) {
        this.mena = mena;
        return this;
    }

    /**
     * Get mena
     *
     * @return mena
     */

    @Schema(name = "mena", required = false)
    public String getMena() {
        return mena;
    }

    public void setMena(String mena) {
        this.mena = mena;
    }

    public Nedoplatok datumNedoplatku(LocalDate datumNedoplatku) {
        this.datumNedoplatku = datumNedoplatku;
        return this;
    }

    /**
     * Get datumNedoplatku
     *
     * @return datumNedoplatku
     */
    @Valid
    @Schema(name = "datumNedoplatku", required = false)
    public LocalDate getDatumNedoplatku() {
        return datumNedoplatku;
    }

    public void setDatumNedoplatku(LocalDate datumNedoplatku) {
        this.datumNedoplatku = datumNedoplatku;
    }

    public Nedoplatok druhDaneAleboPohladavky(CsruDruhDaneAleboPohladavkyEnum druhDaneAleboPohladavky) {
        this.druhDaneAleboPohladavky = druhDaneAleboPohladavky;
        return this;
    }

    /**
     * Get druhDaneAleboPohladavky
     *
     * @return druhDaneAleboPohladavky
     */
    @Valid
    @Schema(name = "druhDaneAleboPohladavky", required = false)
    public CsruDruhDaneAleboPohladavkyEnum getDruhDaneAleboPohladavky() {
        return druhDaneAleboPohladavky;
    }

    public void setDruhDaneAleboPohladavky(CsruDruhDaneAleboPohladavkyEnum druhDaneAleboPohladavky) {
        this.druhDaneAleboPohladavky = druhDaneAleboPohladavky;
    }

    public Nedoplatok popisOsbStavu(String popisOsbStavu) {
        this.popisOsbStavu = popisOsbStavu;
        return this;
    }

    /**
     * Get popisOsbStavu
     *
     * @return popisOsbStavu
     */

    @Schema(name = "popisOsbStavu", required = false)
    public String getPopisOsbStavu() {
        return popisOsbStavu;
    }

    public void setPopisOsbStavu(String popisOsbStavu) {
        this.popisOsbStavu = popisOsbStavu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Nedoplatok nedoplatok = (Nedoplatok) o;
        return Objects.equals(this.chybovyKod, nedoplatok.chybovyKod) &&
                Objects.equals(this.chybovaSprava, nedoplatok.chybovaSprava) &&
                Objects.equals(this.zdravotnaPoistovna, nedoplatok.zdravotnaPoistovna) &&
                Objects.equals(this.vysledokSpracovania, nedoplatok.vysledokSpracovania) &&
                Objects.equals(this.nedoplatok, nedoplatok.nedoplatok) &&
                Objects.equals(this.vyskaNedoplatku, nedoplatok.vyskaNedoplatku) &&
                Objects.equals(this.mena, nedoplatok.mena) &&
                Objects.equals(this.datumNedoplatku, nedoplatok.datumNedoplatku) &&
                Objects.equals(this.druhDaneAleboPohladavky, nedoplatok.druhDaneAleboPohladavky) &&
                Objects.equals(this.popisOsbStavu, nedoplatok.popisOsbStavu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chybovyKod, chybovaSprava, zdravotnaPoistovna, vysledokSpracovania, nedoplatok, vyskaNedoplatku, mena, datumNedoplatku, druhDaneAleboPohladavky, popisOsbStavu);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Nedoplatok {\n");
        sb.append("    chybovyKod: ").append(toIndentedString(chybovyKod)).append("\n");
        sb.append("    chybovaSprava: ").append(toIndentedString(chybovaSprava)).append("\n");
        sb.append("    zdravotnaPoistovna: ").append(toIndentedString(zdravotnaPoistovna)).append("\n");
        sb.append("    vysledokSpracovania: ").append(toIndentedString(vysledokSpracovania)).append("\n");
        sb.append("    nedoplatok: ").append(toIndentedString(nedoplatok)).append("\n");
        sb.append("    vyskaNedoplatku: ").append(toIndentedString(vyskaNedoplatku)).append("\n");
        sb.append("    mena: ").append(toIndentedString(mena)).append("\n");
        sb.append("    datumNedoplatku: ").append(toIndentedString(datumNedoplatku)).append("\n");
        sb.append("    druhDaneAleboPohladavky: ").append(toIndentedString(druhDaneAleboPohladavky)).append("\n");
        sb.append("    popisOsbStavu: ").append(toIndentedString(popisOsbStavu)).append("\n");
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

