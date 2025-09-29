package sk.is.urso.reg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RegisterEntriesList
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-04-30T09:08:07.363724+02:00[Europe/Prague]")

public class ZaznamRegistraList {
    @JsonProperty("total")
    private Long total;

    @JsonProperty("result")
    @Valid
    private List<ZaznamRegistra> result = new ArrayList<>();

    public ZaznamRegistraList total(Long total) {
        this.total = total;
        return this;
    }

    /**
     * Get total
     * @return total
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public ZaznamRegistraList result(List<ZaznamRegistra> result) {
        this.result = result;
        return this;
    }

    public ZaznamRegistraList addResultItem(ZaznamRegistra resultItem) {
        this.result.add(resultItem);
        return this;
    }

    /**
     * Get result
     * @return result
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull

    @Valid

    public List<ZaznamRegistra> getResult() {
        return result;
    }

    public void setResult(List<ZaznamRegistra> result) {
        this.result = result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZaznamRegistraList zaznamRegistraList = (ZaznamRegistraList) o;
        return Objects.equals(this.total, zaznamRegistraList.total) &&
                Objects.equals(this.result, zaznamRegistraList.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RegisterEntriesList {\n");

        sb.append("    total: ").append(toIndentedString(total)).append("\n");
        sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

