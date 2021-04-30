package qa.exceptions.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ErrorMessage {
    @JsonProperty("status")
    private final int statusCode;
    private final Date timestamp;
    private final String message;
    private final String description;

    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }

    protected ErrorMessage() {
        this.statusCode = 0;
        this.timestamp = null;
        this.message = null;
        this.description = null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
