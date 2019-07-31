package uk.gov.cshr.civilservant.exception;

public class TokenDoesNotExistException extends RuntimeException {
    public TokenDoesNotExistException(String organisationId) {
        super(String.format("Agency token does not exist for organisation with ID: %s", organisationId));
    }
}
