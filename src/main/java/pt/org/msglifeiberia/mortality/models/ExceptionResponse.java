package pt.org.msglifeiberia.mortality.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Exception model", name = "Exception")
public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String details;
    private String httpCodeMessage;

    public ExceptionResponse(Date timestamp, String message, String details,String httpCodeMessage) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.httpCodeMessage=httpCodeMessage;
    }

    public String getHttpCodeMessage() {
        return httpCodeMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}