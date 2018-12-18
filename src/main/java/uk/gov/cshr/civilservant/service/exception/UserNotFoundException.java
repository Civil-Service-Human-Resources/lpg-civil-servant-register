package uk.gov.cshr.civilservant.service.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Throwable e) {
        super(e);
    }
}
