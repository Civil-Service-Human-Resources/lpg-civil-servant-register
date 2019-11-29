package uk.gov.cshr.civilservant.service.exception;

public class GeneralServiceException extends RuntimeException {
    public GeneralServiceException(String message) {
        super(message);
    }

    public GeneralServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
