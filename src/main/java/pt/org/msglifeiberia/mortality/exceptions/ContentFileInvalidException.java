package pt.org.msglifeiberia.mortality.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ContentFileInvalidException extends RuntimeException {

    private HttpStatus httpStatus;
    private final List<String> errorMessages;

    public ContentFileInvalidException(HttpStatus httpStatus, List<String> errorMessages) {
        this.httpStatus = httpStatus;
        this.errorMessages = errorMessages;

    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

