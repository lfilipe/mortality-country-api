package pt.org.msglifeiberia.mortality.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class InvalidException extends RuntimeException {

    public InvalidException(String message) {
        super(message);
    }
}
