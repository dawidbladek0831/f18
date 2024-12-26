package pl.app.common.shared.exception;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ApiError implements Serializable {
    private String path;
    private String message;
    private Integer statusCode;
    private LocalDateTime dateTime;

    public ApiError() {
    }

    public ApiError(String path, String message, Integer statusCode, LocalDateTime dateTime) {
        this.path = path;
        this.message = message;
        this.statusCode = statusCode;
        this.dateTime = dateTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
