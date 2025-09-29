package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ConfirmChangesIdObject
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-03-07T12:13:45.400042500+01:00[Europe/Berlin]")
public class ConfirmChangesIdObject {
    @JsonProperty("confirmChangesIds")
    @Valid
    private List<String> confirmChangesIds = null;
    public ConfirmChangesIdObject confirmChangesIds(List<String> confirmChangesIds) {
        this.confirmChangesIds = confirmChangesIds;
        return this;

    }
    public ConfirmChangesIdObject addConfirmChangesIdsItem(String confirmChangesIdsItem) {
        if (this.confirmChangesIds == null) {
            this.confirmChangesIds = new ArrayList<>();
        }
        this.confirmChangesIds.add(confirmChangesIdsItem);
        return this;

    }
    /**
     * Get confirmChangesIds
     * @return confirmChangesIds
     */
    @ApiModelProperty(value = "")
    public List<String> getConfirmChangesIds() {
        return confirmChangesIds;

    }
    public void setConfirmChangesIds(List<String> confirmChangesIds) {
        this.confirmChangesIds = confirmChangesIds;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;

        }
        if (o == null || getClass() != o.getClass()) {
            return false;

        }

        ConfirmChangesIdObject confirmChangesIdObject = (ConfirmChangesIdObject) o;
        return Objects.equals(this.confirmChangesIds, confirmChangesIdObject.confirmChangesIds);

    }
    @Override
    public int hashCode() {
        return Objects.hash(confirmChangesIds);

    }
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("class ConfirmChangesIdObject {\n");

        sb.append(" confirmChangesIds: ").append(toIndentedString(confirmChangesIds)).append("\n");

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
        return o.toString().replace("\n", "\n ");
    }
}

