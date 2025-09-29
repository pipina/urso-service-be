package sk.is.urso.reg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * ZaznamRegistraReferencia
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-30T09:30:52.258432300+01:00[Europe/Bratislava]")
public class ZaznamRegistraReferencia   {
    @JsonProperty("registerId")
    private String registerId;

    @JsonProperty("verziaRegistraId")
    private Integer verziaRegistraId;

    @JsonProperty("zaznamId")
    private Long zaznamId;

    @JsonProperty("modul")
    private String modul;

    @JsonProperty("pocetReferencii")
    private Integer pocetReferencii;

    public ZaznamRegistraReferencia registerId(String registerId) {
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

    public ZaznamRegistraReferencia verziaRegistraId(Integer verziaRegistraId) {
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

    public ZaznamRegistraReferencia zaznamId(Long zaznamId) {
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

    public ZaznamRegistraReferencia modul(String modul) {
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

    public ZaznamRegistraReferencia pocetReferencii(Integer pocetReferencii) {
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
        ZaznamRegistraReferencia zaznamRegistraReferencia = (ZaznamRegistraReferencia) o;
        return Objects.equals(this.registerId, zaznamRegistraReferencia.registerId) &&
                Objects.equals(this.verziaRegistraId, zaznamRegistraReferencia.verziaRegistraId) &&
                Objects.equals(this.zaznamId, zaznamRegistraReferencia.zaznamId) &&
                Objects.equals(this.modul, zaznamRegistraReferencia.modul) &&
                Objects.equals(this.pocetReferencii, zaznamRegistraReferencia.pocetReferencii);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registerId, verziaRegistraId, zaznamId, modul, pocetReferencii);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ZaznamRegistraReferencia {\n");

        sb.append("    registerId: ").append(toIndentedString(registerId)).append("\n");
        sb.append("    verziaRegistraId: ").append(toIndentedString(verziaRegistraId)).append("\n");
        sb.append("    zaznamId: ").append(toIndentedString(zaznamId)).append("\n");
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
