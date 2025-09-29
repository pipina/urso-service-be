package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("searchList")
public class SearchList {

    @JsonProperty("total")
    private Long total;

    @JsonProperty("result")
    @Valid
    private List<SearchSimpleOutput> result = new ArrayList<>();

    public SearchList total(Long total) {
        this.total = total;
        return this;
    }

    @NotNull
    @Schema(name = "total", required = true)
    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public SearchList result(List<SearchSimpleOutput> result) {
        this.result = result;
        return this;
    }

    public SearchList addResultItem(SearchSimpleOutput resultItem) {
        this.result.add(resultItem);
        return this;
    }

    @NotNull
    @Valid
    @Schema(name = "result", required = true)
    public List<SearchSimpleOutput> getResult() {
        return result;
    }

    public void setResult(List<SearchSimpleOutput> result) {
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
        SearchList that = (SearchList) o;
        return Objects.equals(total, that.total) &&
                Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, result);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SearchList {\n");
        sb.append("    total: ").append(toIndentedString(total)).append("\n");
        sb.append("    result: ").append(toIndentedString(result)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
