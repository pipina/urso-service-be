package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * SearchRequestFilter
 */

@JsonTypeName("searchRequestFilter")
public class SearchRequestFilter {

    @JsonProperty("fulltextSearch")
    private String fulltextSearch;

    @JsonProperty("okresName")
    private String okresName;

    @JsonProperty("obecName")
    private String obecName;

    @JsonProperty("ulicaName")
    private String ulicaName;

    @JsonProperty("regCisloNumber")
    private String regCisloNumber;

    @JsonProperty("cisloBudovyNumber")
    private String cisloBudovyNumber;

    @JsonProperty("postalCode")
    private String postalCode;

    public SearchRequestFilter fulltextSearch(String fulltextSearch) {
        this.fulltextSearch = fulltextSearch;
        return this;
    }

    /**
     * Get fulltextSearch
     * @return fulltextSearch
     */

    @Schema(name = "fulltextSearch", required = false)
    public String getFulltextSearch() {
        return fulltextSearch;
    }

    public void setFulltextSearch(String fulltextSearch) {
        this.fulltextSearch = fulltextSearch;
    }

    public SearchRequestFilter okresName(String okresName) {
        this.okresName = okresName;
        return this;
    }

    /**
     * Get okresName
     * @return okresName
     */
    @Schema(name = "okresName", required = false)
    public String getOkresName() {
        return okresName;
    }

    public void setOkresName(String okresName) {
        this.okresName = okresName;
    }

    public SearchRequestFilter obecName(String obecName) {
        this.obecName = obecName;
        return this;
    }

    /**
     * Get obecName
     * @return obecName
     */
    @Schema(name = "obecName", required = false)
    public String getObecName() {
        return obecName;
    }

    public void setObecName(String obecName) {
        this.obecName = obecName;
    }

    public SearchRequestFilter ulicaName(String ulicaName) {
        this.ulicaName = ulicaName;
        return this;
    }

    /**
     * Get ulicaName
     * @return ulicaName
     */
    @Schema(name = "ulicaName", required = false)
    public String getUlicaName() {
        return ulicaName;
    }

    public void setUlicaName(String ulicaName) {
        this.ulicaName = ulicaName;
    }

    public SearchRequestFilter regCisloNumber(String regCisloNumber) {
        this.regCisloNumber = regCisloNumber;
        return this;
    }

    /**
     * Get regCisloNumber
     * @return regCisloNumber
     */
    @Schema(name = "regCisloNumber", required = false)
    public String getRegCisloNumber() {
        return regCisloNumber;
    }

    public void setRegCisloNumber(String regCisloNumber) {
        this.regCisloNumber = regCisloNumber;
    }

    public SearchRequestFilter cisloBudovyNumber(String cisloBudovyNumber) {
        this.cisloBudovyNumber = cisloBudovyNumber;
        return this;
    }

    /**
     * Get cisloBudovyNumber
     * @return cisloBudovyNumber
     */
    @Schema(name = "cisloBudovyNumber", required = false)
    public String getCisloBudovyNumber() {
        return cisloBudovyNumber;
    }

    public void setCisloBudovyNumber(String cisloBudovyNumber) {
        this.cisloBudovyNumber = cisloBudovyNumber;
    }

    public SearchRequestFilter postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    /**
     * Get postalCode
     * @return postalCode
     */
    @Schema(name = "postalCode", required = false)
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchRequestFilter that = (SearchRequestFilter) o;
        return Objects.equals(fulltextSearch, that.fulltextSearch) && Objects.equals(okresName, that.okresName) && Objects.equals(obecName, that.obecName) && Objects.equals(ulicaName, that.ulicaName) && Objects.equals(regCisloNumber, that.regCisloNumber) && Objects.equals(cisloBudovyNumber, that.cisloBudovyNumber) && Objects.equals(postalCode, that.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fulltextSearch, okresName, obecName, ulicaName, regCisloNumber, cisloBudovyNumber, postalCode);
    }

    @Override
    public String toString() {
        return "SearchRequestFilter{" +
                "fulltextSearch='" + fulltextSearch + '\'' +
                ", okresName='" + okresName + '\'' +
                ", obecName='" + obecName + '\'' +
                ", ulicaName='" + ulicaName + '\'' +
                ", regCisloNumber='" + regCisloNumber + '\'' +
                ", cisloBudovyNumber='" + cisloBudovyNumber + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}

