package sk.is.urso.reg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ZaznamRegistra
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-30T09:30:52.258432300+01:00[Europe/Bratislava]")
public class ZaznamRegistra   {
    @JsonProperty("registerId")
    private String registerId;

    @JsonProperty("verziaRegistraId")
    private Integer verziaRegistraId;

    @JsonProperty("zaznamId")
    private Long zaznamId;

    @JsonProperty("platnostOd")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate platnostOd;

    @JsonProperty("ucinnostOd")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate ucinnostOd;

    @JsonProperty("ucinnostDo")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate ucinnostDo;

    @JsonProperty("platny")
    private Boolean platny;

    @JsonProperty("polia")
    @Valid
    private List<DvojicaKlucHodnotaSHistoriou> polia = new ArrayList<>();

    public ZaznamRegistra registerId(String registerId) {
        this.registerId = registerId;
        return this;
    }

    /**
     * Get registerId
     * @return registerId
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull

    @Size(min=1,max=256)
    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public ZaznamRegistra verziaRegistraId(Integer verziaRegistraId) {
        this.verziaRegistraId = verziaRegistraId;
        return this;
    }

    /**
     * Get verziaRegistraId
     * @return verziaRegistraId
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull


    public Integer getVerziaRegistraId() {
        return verziaRegistraId;
    }

    public void setVerziaRegistraId(Integer verziaRegistraId) {
        this.verziaRegistraId = verziaRegistraId;
    }

    public ZaznamRegistra zaznamId(Long zaznamId) {
        this.zaznamId = zaznamId;
        return this;
    }

    /**
     * Get zaznamId
     * @return zaznamId
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull


    public Long getZaznamId() {
        return zaznamId;
    }

    public void setZaznamId(Long zaznamId) {
        this.zaznamId = zaznamId;
    }

    public ZaznamRegistra platnostOd(LocalDate platnostOd) {
        this.platnostOd = platnostOd;
        return this;
    }

    /**
     * Get platnostOd
     * @return platnostOd
     */
    @ApiModelProperty(value = "")

    @Valid

    public LocalDate getPlatnostOd() {
        return platnostOd;
    }

    public void setPlatnostOd(LocalDate platnostOd) {
        this.platnostOd = platnostOd;
    }

    public ZaznamRegistra ucinnostOd(LocalDate ucinnostOd) {
        this.ucinnostOd = ucinnostOd;
        return this;
    }

    /**
     * Pri update ak nie je vyplnené sa nemení. Pri insert ak nie je vyplnené tak sa nastaví na dnešok.
     * @return ucinnostOd
     */
    @ApiModelProperty(value = "Pri update ak nie je vyplnené sa nemení. Pri insert ak nie je vyplnené tak sa nastaví na dnešok.")

    @Valid

    public LocalDate getUcinnostOd() {
        return ucinnostOd;
    }

    public void setUcinnostOd(LocalDate ucinnostOd) {
        this.ucinnostOd = ucinnostOd;
    }

    public ZaznamRegistra ucinnostDo(LocalDate ucinnostDo) {
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

    public ZaznamRegistra platny(Boolean platny) {
        this.platny = platny;
        return this;
    }

    /**
     * Get platny
     * @return platny
     */
    @ApiModelProperty(value = "")


    public Boolean getPlatny() {
        return platny;
    }

    public void setPlatny(Boolean platny) {
        this.platny = platny;
    }

    public ZaznamRegistra polia(List<DvojicaKlucHodnotaSHistoriou> polia) {
        this.polia = polia;
        return this;
    }

    public ZaznamRegistra addPoliaItem(DvojicaKlucHodnotaSHistoriou poliaItem) {
        this.polia.add(poliaItem);
        return this;
    }

    /**
     * Get polia
     * @return polia
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull

    @Valid

    public List<DvojicaKlucHodnotaSHistoriou> getPolia() {
        return polia;
    }

    public void setPolia(List<DvojicaKlucHodnotaSHistoriou> polia) {
        this.polia = polia;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZaznamRegistra zaznamRegistra = (ZaznamRegistra) o;
        return Objects.equals(this.registerId, zaznamRegistra.registerId) &&
                Objects.equals(this.verziaRegistraId, zaznamRegistra.verziaRegistraId) &&
                Objects.equals(this.zaznamId, zaznamRegistra.zaznamId) &&
                Objects.equals(this.platnostOd, zaznamRegistra.platnostOd) &&
                Objects.equals(this.ucinnostOd, zaznamRegistra.ucinnostOd) &&
                Objects.equals(this.ucinnostDo, zaznamRegistra.ucinnostDo) &&
                Objects.equals(this.platny, zaznamRegistra.platny) &&
                Objects.equals(this.polia, zaznamRegistra.polia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registerId, verziaRegistraId, zaznamId, platnostOd, ucinnostOd, ucinnostDo, platny, polia);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ZaznamRegistra {\n");

        sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
        sb.append("    verziaRegistraId: ").append(toIndentedString(verziaRegistraId)).append("\n");
        sb.append("    zaznamId: ").append(toIndentedString(zaznamId)).append("\n");
        sb.append("    platnostOd: ").append(toIndentedString(platnostOd)).append("\n");
        sb.append("    ucinnostOd: ").append(toIndentedString(ucinnostOd)).append("\n");
        sb.append("    ucinnostDo: ").append(toIndentedString(ucinnostDo)).append("\n");
        sb.append("    platny: ").append(toIndentedString(platny)).append("\n");
        sb.append("    polia: ").append(toIndentedString(polia)).append("\n");
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

