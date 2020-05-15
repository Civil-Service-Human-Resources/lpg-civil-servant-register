package uk.gov.cshr.civilservant.exception;

public class TokenDoesNotExistException extends RuntimeException {

    public TokenDoesNotExistException() {
        super();
    }

    public TokenDoesNotExistException(String uid) {
        super(String.format("Agency token does not exist for user with UID: %s", uid));
    }
}
