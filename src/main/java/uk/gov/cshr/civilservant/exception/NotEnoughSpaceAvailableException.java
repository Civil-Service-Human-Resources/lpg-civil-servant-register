package uk.gov.cshr.civilservant.exception;

public class NotEnoughSpaceAvailableException extends RuntimeException {
    public NotEnoughSpaceAvailableException(String token) {
        super(String.format("Not enough space left for agency token with ID: %s", token));
    }
}
