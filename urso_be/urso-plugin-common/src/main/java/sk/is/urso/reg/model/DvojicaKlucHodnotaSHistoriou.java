package sk.is.urso.reg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * DvojicaKlucHodnotaSHistoriou
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-30T09:30:52.258432300+01:00[Europe/Bratislava]")
public class DvojicaKlucHodnotaSHistoriou   {
    @JsonProperty("kluc")
    private String kluc;

    @JsonProperty("hodnota")
    private String hodnota;

    @JsonProperty("ucinnostOd")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate ucinnostOd;

    @JsonProperty("ucinnostDo")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate ucinnostDo;

    @JsonProperty("sekvencia")
    private Integer sekvencia;

    @JsonProperty("platna")
    private Boolean platna;

    @JsonProperty("aktualna")
    private Boolean aktualna;

    @JsonProperty("nazovZobrazenia")
    private String nazovZobrazenia;

    @JsonProperty("kontext")
    private String kontext;

    public DvojicaKlucHodnotaSHistoriou kluc(String kluc) {
        this.kluc = kluc;
        return this;
    }

    /**
     * Get kluc
     * @return kluc
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull


    public String getKluc() {
        return kluc;
    }

    public void setKluc(String kluc) {
        this.kluc = kluc;
    }

    public DvojicaKlucHodnotaSHistoriou hodnota(String hodnota) {
        this.hodnota = hodnota;
        return this;
    }

    /**
     * Get hodnota
     * @return hodnota
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull


    public String getHodnota() {
        return hodnota;
    }

    public void setHodnota(String hodnota) {
        this.hodnota = hodnota;
    }

    public DvojicaKlucHodnotaSHistoriou ucinnostOd(LocalDate ucinnostOd) {
        this.ucinnostOd = ucinnostOd;
        return this;
    }

    /**
     * Get ucinnostOd
     * @return ucinnostOd
     */
    @ApiModelProperty(value = "")

    @Valid

    public LocalDate getUcinnostOd() {
        return ucinnostOd;
    }

    public void setUcinnostOd(LocalDate ucinnostOd) {
        this.ucinnostOd = ucinnostOd;
    }

    public DvojicaKlucHodnotaSHistoriou ucinnostDo(LocalDate ucinnostDo) {
        this.ucinnostDo = ucinnostDo;
        return this;
    }

    /**
     * Get ucinnostDo
     * @return ucinnostDo
     */
    @ApiModelProperty(value = "")

    @Valid

    public LocalDate getUcinnostDo() {
        return ucinnostDo;
    }

    public void setUcinnostDo(LocalDate ucinnostDo) {
        this.ucinnostDo = ucinnostDo;
    }

    public DvojicaKlucHodnotaSHistoriou sekvencia(Integer sekvencia) {
        this.sekvencia = sekvencia;
        return this;
    }

    /**
     * Get sekvencia
     * @return sekvencia
     */
    @ApiModelProperty(value = "")


    public Integer getSekvencia() {
        return sekvencia;
    }

    public void setSekvencia(Integer sekvencia) {
        this.sekvencia = sekvencia;
    }

    public DvojicaKlucHodnotaSHistoriou platna(Boolean platna) {
        this.platna = platna;
        return this;
    }

    /**
     * Get platna
     * @return platna
     */
    @ApiModelProperty(value = "")


    public Boolean getPlatna() {
        return platna;
    }

    public void setPlatna(Boolean platna) {
        this.platna = platna;
    }

    public DvojicaKlucHodnotaSHistoriou aktualna(Boolean aktualna) {
        this.aktualna = aktualna;
        return this;
    }

    /**
     * Get aktualna
     * @return aktualna
     */
    @ApiModelProperty(value = "")


    public Boolean getAktualna() {
        return aktualna;
    }

    public void setAktualna(Boolean aktualna) {
        this.aktualna = aktualna;
    }

    public DvojicaKlucHodnotaSHistoriou nazovZobrazenia(String nazovZobrazenia) {
        this.nazovZobrazenia = nazovZobrazenia;
        return this;
    }

    /**
     * Get nazovZobrazenia
     * @return nazovZobrazenia
     */
    @ApiModelProperty(value = "")


    public String getNazovZobrazenia() {
        return nazovZobrazenia;
    }

    public void setNazovZobrazenia(String nazovZobrazenia) {
        this.nazovZobrazenia = nazovZobrazenia;
    }

    public DvojicaKlucHodnotaSHistoriou kontext(String kontext) {
        this.kontext = kontext;
        return this;
    }

    /**
     * Get kontext
     * @return kontext
     */
    @ApiModelProperty(value = "")


    public String getKontext() {
        return kontext;
    }

    public void setKontext(String kontext) {
        this.kontext = kontext;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DvojicaKlucHodnotaSHistoriou dvojicaKlucHodnotaSHistoriou = (DvojicaKlucHodnotaSHistoriou) o;
        return Objects.equals(this.kluc, dvojicaKlucHodnotaSHistoriou.kluc) &&
                Objects.equals(this.hodnota, dvojicaKlucHodnotaSHistoriou.hodnota) &&
                Objects.equals(this.ucinnostOd, dvojicaKlucHodnotaSHistoriou.ucinnostOd) &&
                Objects.equals(this.ucinnostDo, dvojicaKlucHodnotaSHistoriou.ucinnostDo) &&
                Objects.equals(this.sekvencia, dvojicaKlucHodnotaSHistoriou.sekvencia) &&
                Objects.equals(this.platna, dvojicaKlucHodnotaSHistoriou.platna) &&
                Objects.equals(this.aktualna, dvojicaKlucHodnotaSHistoriou.aktualna) &&
                Objects.equals(this.nazovZobrazenia, dvojicaKlucHodnotaSHistoriou.nazovZobrazenia) &&
                Objects.equals(this.kontext, dvojicaKlucHodnotaSHistoriou.kontext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kluc, hodnota, ucinnostOd, ucinnostDo, sekvencia, platna, aktualna, nazovZobrazenia, kontext);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DvojicaKlucHodnotaSHistoriou {\n");

        sb.append("    kluc: ").append(toIndentedString(kluc)).append("\n");
        sb.append("    hodnota: ").append(toIndentedString(hodnota)).append("\n");
        sb.append("    ucinnostOd: ").append(toIndentedString(ucinnostOd)).append("\n");
        sb.append("    ucinnostDo: ").append(toIndentedString(ucinnostDo)).append("\n");
        sb.append("    sekvencia: ").append(toIndentedString(sekvencia)).append("\n");
        sb.append("    platna: ").append(toIndentedString(platna)).append("\n");
        sb.append("    aktualna: ").append(toIndentedString(aktualna)).append("\n");
        sb.append("    nazovZobrazenia: ").append(toIndentedString(nazovZobrazenia)).append("\n");
        sb.append("    kontext: ").append(toIndentedString(kontext)).append("\n");
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

