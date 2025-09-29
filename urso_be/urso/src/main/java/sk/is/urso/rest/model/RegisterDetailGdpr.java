package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import java.util.Objects;

/**
 * RegisterDetailGdpr
 */

@JsonTypeName("registerDetailGdpr")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class RegisterDetailGdpr {

  @JsonProperty("odstraneniePriPoslednejReferencii")
  private Boolean odstraneniePriPoslednejReferencii;

  @JsonProperty("ttl")
  private Integer ttl;

  /**
   * Gets or Sets ttlJednotka
   */
  public enum TtlJednotkaEnum {
    YEARS("YEARS"),
    
    MONTHS("MONTHS"),
    
    WEEKS("WEEKS"),
    
    DAYS("DAYS");

    private String value;

    TtlJednotkaEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TtlJednotkaEnum fromValue(String value) {
      for (TtlJednotkaEnum b : TtlJednotkaEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("ttlJednotka")
  private TtlJednotkaEnum ttlJednotka;

  public RegisterDetailGdpr odstraneniePriPoslednejReferencii(Boolean odstraneniePriPoslednejReferencii) {
    this.odstraneniePriPoslednejReferencii = odstraneniePriPoslednejReferencii;
    return this;
  }

  /**
   * Get odstraneniePriPoslednejReferencii
   * @return odstraneniePriPoslednejReferencii
  */
  
  @Schema(name = "odstraneniePriPoslednejReferencii", required = false)
  public Boolean getOdstraneniePriPoslednejReferencii() {
    return odstraneniePriPoslednejReferencii;
  }

  public void setOdstraneniePriPoslednejReferencii(Boolean odstraneniePriPoslednejReferencii) {
    this.odstraneniePriPoslednejReferencii = odstraneniePriPoslednejReferencii;
  }

  public RegisterDetailGdpr ttl(Integer ttl) {
    this.ttl = ttl;
    return this;
  }

  /**
   * Get ttl
   * @return ttl
  */
  
  @Schema(name = "ttl", required = false)
  public Integer getTtl() {
    return ttl;
  }

  public void setTtl(Integer ttl) {
    this.ttl = ttl;
  }

  public RegisterDetailGdpr ttlJednotka(TtlJednotkaEnum ttlJednotka) {
    this.ttlJednotka = ttlJednotka;
    return this;
  }

  /**
   * Get ttlJednotka
   * @return ttlJednotka
  */
  
  @Schema(name = "ttlJednotka", required = false)
  public TtlJednotkaEnum getTtlJednotka() {
    return ttlJednotka;
  }

  public void setTtlJednotka(TtlJednotkaEnum ttlJednotka) {
    this.ttlJednotka = ttlJednotka;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegisterDetailGdpr registerDetailGdpr = (RegisterDetailGdpr) o;
    return Objects.equals(this.odstraneniePriPoslednejReferencii, registerDetailGdpr.odstraneniePriPoslednejReferencii) &&
        Objects.equals(this.ttl, registerDetailGdpr.ttl) &&
        Objects.equals(this.ttlJednotka, registerDetailGdpr.ttlJednotka);
  }

  @Override
  public int hashCode() {
    return Objects.hash(odstraneniePriPoslednejReferencii, ttl, ttlJednotka);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegisterDetailGdpr {\n");
    sb.append("    odstraneniePriPoslednejReferencii: ").append(toIndentedString(odstraneniePriPoslednejReferencii)).append("\n");
    sb.append("    ttl: ").append(toIndentedString(ttl)).append("\n");
    sb.append("    ttlJednotka: ").append(toIndentedString(ttlJednotka)).append("\n");
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

