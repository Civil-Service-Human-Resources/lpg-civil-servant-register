package uk.gov.cshr.civilservant.exception;

public class TokenAlreadyExistsException extends RuntimeException {
    public TokenAlreadyExistsException(String organisationId) {
        super(String.format("Agency token already exists for organisation with ID: %s", organisationId));
    }
}
