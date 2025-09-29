package sk.is.urso.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class InitializationException extends RuntimeException {

    public InitializationException(String message, Exception ex) {
        super(message, ex);
    }
}
