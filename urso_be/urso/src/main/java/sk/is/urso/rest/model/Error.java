package sk.is.urso.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Error
 */

@JsonTypeName("error")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-05-29T11:27:41.522351500+02:00[Europe/Bratislava]")
public class Error {

  @JsonProperty("message")
  private String message;

  @JsonProperty("detail")
  private String detail;

  @JsonProperty("field")
  private String field;

  public Error message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Tu je chybová hláška vhodná aj pre používateľa. Hláška by mala byť v slovenskom jazyku
   * @return message
  */
  @NotNull 
  @Schema(name = "message", description = "Tu je chybová hláška vhodná aj pre používateľa. Hláška by mala byť v slovenskom jazyku", required = true)
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Error detail(String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * Tu môže byť zadaná detailná hláška v angličtine, stack trace a podobne!
   * @return detail
  */
  
  @Schema(name = "detail", description = "Tu môže byť zadaná detailná hláška v angličtine, stack trace a podobne!", required = false)
  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public Error field(String field) {
    this.field = field;
    return this;
  }

  /**
   * Pole ktoré je nevalidné, v prípade že chyba je vo validácii vstupu.  V takom prípade popis obsahuje lokalizovanú validačnú hlášku.
   * @return field
  */
  
  @Schema(name = "field", description = "Pole ktoré je nevalidné, v prípade že chyba je vo validácii vstupu.  V takom prípade popis obsahuje lokalizovanú validačnú hlášku.", required = false)
  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Error error = (Error) o;
    return Objects.equals(this.message, error.message) &&
        Objects.equals(this.detail, error.detail) &&
        Objects.equals(this.field, error.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, detail, field);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    field: ").append(toIndentedString(field)).append("\n");
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

