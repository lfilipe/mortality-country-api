package pt.org.msglifeiberia.mortality.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@Schema(description = "Exception erro lis", name = "Exception")
public class ExceptionErrorsResponse {
    private Date timestamp;
    private String httpCodeMessage;
    private List<String> errors;

    public ExceptionErrorsResponse(Date timestamp, String httpCodeMessage, List<String> errors) {
        super();
        this.timestamp = timestamp;
        this.httpCodeMessage=httpCodeMessage;
        this.errors = errors;
    }

    public String getHttpCodeMessage() {
        return httpCodeMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }
}