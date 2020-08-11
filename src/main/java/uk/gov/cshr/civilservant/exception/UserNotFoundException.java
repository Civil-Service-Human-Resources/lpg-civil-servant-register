package uk.gov.cshr.civilservant.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String userId) {
    super(String.format("User not found: %s", userId));
  }
}
