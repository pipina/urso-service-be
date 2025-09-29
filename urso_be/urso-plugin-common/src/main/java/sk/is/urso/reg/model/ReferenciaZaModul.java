package sk.is.urso.reg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ReferenciaZaModul
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-30T09:30:52.258432300+01:00[Europe/Bratislava]")
public class ReferenciaZaModul   {
    @JsonProperty("modul")
    private String modul;

    @JsonProperty("pocetReferencii")
    private Integer pocetReferencii;

    public ReferenciaZaModul modul(String modul) {
        this.modul = modul;
        return this;
    }

    /**
     * Get modul
     * @return modul
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull

    @Size(min=2,max=8)
    public String getModul() {
        return modul;
    }

    public void setModul(String modul) {
        this.modul = modul;
    }

    public ReferenciaZaModul pocetReferencii(Integer pocetReferencii) {
        this.pocetReferencii = pocetReferencii;
        return this;
    }

    /**
     * Get pocetReferencii
     * @return pocetReferencii
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull


    public Integer getPocetReferencii() {
        return pocetReferencii;
    }

    public void setPocetReferencii(Integer pocetReferencii) {
        this.pocetReferencii = pocetReferencii;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReferenciaZaModul referenciaZaModul = (ReferenciaZaModul) o;
        return Objects.equals(this.modul, referenciaZaModul.modul) &&
                Objects.equals(this.pocetReferencii, referenciaZaModul.pocetReferencii);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modul, pocetReferencii);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReferenciaZaModul {\n");

        sb.append("    modul: ").append(toIndentedString(modul)).append("\n");
        sb.append("    pocetReferencii: ").append(toIndentedString(pocetReferencii)).append("\n");
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
