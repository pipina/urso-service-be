package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * SearchSimpleOutput
 */

@JsonTypeName("searchSimpleOutput")
public class SearchSimpleOutput {

    @JsonProperty("displayValue")
    private String displayValue;

    public SearchSimpleOutput displayValue(String displayValue) {
        this.displayValue = displayValue;
        return this;
    }

    /**
     * Get displayValue
     * @return displayValue
     */

    @Schema(name = "displayValue", required = false)
    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}

