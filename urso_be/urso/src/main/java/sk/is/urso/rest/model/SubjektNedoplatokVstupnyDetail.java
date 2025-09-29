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
 * SubjektNedoplatokVstupnyDetail
 */

@JsonTypeName("subjektNedoplatokVstupnyDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-11-23T13:54:30.535017600+01:00[Europe/Berlin]")
public class SubjektNedoplatokVstupnyDetail {

    @JsonProperty("rodneCislo")
    private String rodneCislo;

    @JsonProperty("meno")
    private String meno;

    @JsonProperty("priezvisko")
    private String priezvisko;

    @JsonProperty("datumNarodenia")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate datumNarodenia;

    @JsonProperty("ico")
    private String ico;

    @JsonProperty("nazovSpolocnosti")
    private String nazovSpolocnosti;

    @JsonProperty("dic")
    private String dic;

    public SubjektNedoplatokVstupnyDetail rodneCislo(String rodneCislo) {
        this.rodneCislo = rodneCislo;
        return this;
    }

    /**
     * Get rodneCislo
     *
     * @return rodneCislo
     */

    @Schema(name = "rodneCislo", required = false)
    public String getRodneCislo() {
        return rodneCislo;
    }

    public void setRodneCislo(String rodneCislo) {
        this.rodneCislo = rodneCislo;
    }

    public SubjektNedoplatokVstupnyDetail meno(String meno) {
        this.meno = meno;
        return this;
    }

    /**
     * Get meno
     *
     * @return meno
     */

    @Schema(name = "meno", required = false)
    public String getMeno() {
        return meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public SubjektNedoplatokVstupnyDetail priezvisko(String priezvisko) {
        this.priezvisko = priezvisko;
        return this;
    }

    /**
     * Get priezvisko
     *
     * @return priezvisko
     */

    @Schema(name = "priezvisko", required = false)
    public String getPriezvisko() {
        return priezvisko;
    }

    public void setPriezvisko(String priezvisko) {
        this.priezvisko = priezvisko;
    }

    public SubjektNedoplatokVstupnyDetail datumNarodenia(LocalDate datumNarodenia) {
        this.datumNarodenia = datumNarodenia;
        return this;
    }

    /**
     * Get datumNarodenia
     *
     * @return datumNarodenia
     */
    @Valid
    @Schema(name = "datumNarodenia", required = false)
    public LocalDate getDatumNarodenia() {
        return datumNarodenia;
    }

    public void setDatumNarodenia(LocalDate datumNarodenia) {
        this.datumNarodenia = datumNarodenia;
    }

    public SubjektNedoplatokVstupnyDetail ico(String ico) {
        this.ico = ico;
        return this;
    }

    /**
     * Get ico
     *
     * @return ico
     */

    @Schema(name = "ico", required = false)
    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public SubjektNedoplatokVstupnyDetail nazovSpolocnosti(String nazovSpolocnosti) {
        this.nazovSpolocnosti = nazovSpolocnosti;
        return this;
    }

    /**
     * Get nazovSpolocnosti
     *
     * @return nazovSpolocnosti
     */

    @Schema(name = "nazovSpolocnosti", required = false)
    public String getNazovSpolocnosti() {
        return nazovSpolocnosti;
    }

    public void setNazovSpolocnosti(String nazovSpolocnosti) {
        this.nazovSpolocnosti = nazovSpolocnosti;
    }

    public SubjektNedoplatokVstupnyDetail dic(String dic) {
        this.dic = dic;
        return this;
    }

    /**
     * Get dic
     *
     * @return dic
     */

    @Schema(name = "dic", required = false)
    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubjektNedoplatokVstupnyDetail subjektNedoplatokVstupnyDetail = (SubjektNedoplatokVstupnyDetail) o;
        return Objects.equals(this.rodneCislo, subjektNedoplatokVstupnyDetail.rodneCislo) &&
                Objects.equals(this.meno, subjektNedoplatokVstupnyDetail.meno) &&
                Objects.equals(this.priezvisko, subjektNedoplatokVstupnyDetail.priezvisko) &&
                Objects.equals(this.datumNarodenia, subjektNedoplatokVstupnyDetail.datumNarodenia) &&
                Objects.equals(this.ico, subjektNedoplatokVstupnyDetail.ico) &&
                Objects.equals(this.nazovSpolocnosti, subjektNedoplatokVstupnyDetail.nazovSpolocnosti) &&
                Objects.equals(this.dic, subjektNedoplatokVstupnyDetail.dic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rodneCislo, meno, priezvisko, datumNarodenia, ico, nazovSpolocnosti, dic);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SubjektNedoplatokVstupnyDetail {\n");
        sb.append("    rodneCislo: ").append(toIndentedString(rodneCislo)).append("\n");
        sb.append("    meno: ").append(toIndentedString(meno)).append("\n");
        sb.append("    priezvisko: ").append(toIndentedString(priezvisko)).append("\n");
        sb.append("    datumNarodenia: ").append(toIndentedString(datumNarodenia)).append("\n");
        sb.append("    ico: ").append(toIndentedString(ico)).append("\n");
        sb.append("    nazovSpolocnosti: ").append(toIndentedString(nazovSpolocnosti)).append("\n");
        sb.append("    dic: ").append(toIndentedString(dic)).append("\n");
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

