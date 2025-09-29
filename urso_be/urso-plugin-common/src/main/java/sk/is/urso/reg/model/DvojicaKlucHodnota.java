package sk.is.urso.reg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * DvojicaKlucHodnota
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2022-11-30T09:30:52.258432300+01:00[Europe/Bratislava]")
public class DvojicaKlucHodnota   {
    @JsonProperty("kluc")
    private String kluc;

    @JsonProperty("hodnota")
    private String hodnota;

    public DvojicaKlucHodnota kluc(String kluc) {
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

    public DvojicaKlucHodnota hodnota(String hodnota) {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DvojicaKlucHodnota dvojicaKlucHodnota = (DvojicaKlucHodnota) o;
        return Objects.equals(this.kluc, dvojicaKlucHodnota.kluc) &&
                Objects.equals(this.hodnota, dvojicaKlucHodnota.hodnota);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kluc, hodnota);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DvojicaKlucHodnota {\n");

        sb.append("    kluc: ").append(toIndentedString(kluc)).append("\n");
        sb.append("    hodnota: ").append(toIndentedString(hodnota)).append("\n");
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

