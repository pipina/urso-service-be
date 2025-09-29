package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RfoExternalIdsObject
 */

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-03-07T12:13:45.400042500+01:00[Europe/Berlin]")
public class RfoExternalIdsObject {
    @JsonProperty("rfoExternalIds")
    @Valid

    private List<String> rfoExternalIds = null;

    public RfoExternalIdsObject rfoExternalIds(List<String> rfoExternalIds) {
        this.rfoExternalIds = rfoExternalIds;

        return this;
    }

    public RfoExternalIdsObject addRfoExternalIdsItem(String rfoExternalIdsItem) {
        if (this.rfoExternalIds == null) {
            this.rfoExternalIds = new ArrayList<>();
        }
        this.rfoExternalIds.add(rfoExternalIdsItem);

        return this;
    }

    /**
     * Get rfoExternalIds
     *
     * @return rfoExternalIds
     */

    @ApiModelProperty(value = "")

    public List<String> getRfoExternalIds() {
        return rfoExternalIds;
    }

    public void setRfoExternalIds(List<String> rfoExternalIds) {
        this.rfoExternalIds = rfoExternalIds;
    }

    @Override

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RfoExternalIdsObject rfoExternalIdsObject = (RfoExternalIdsObject) o;

        return Objects.equals(this.rfoExternalIds, rfoExternalIdsObject.rfoExternalIds);
    }

    @Override

    public int hashCode() {
        return Objects.hash(rfoExternalIds);
    }

    @Override

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RfoExternalIdsObject {\n");

        sb.append(" rfoExternalIds: ").append(toIndentedString(rfoExternalIds)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * <p>
     * (except the first line).
     */

    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n ");
    }
}

