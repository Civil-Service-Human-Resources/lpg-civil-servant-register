package uk.gov.cshr.civilservant.exception;

public class ProfessionNotFoundException extends Exception {
  public ProfessionNotFoundException(String message) {
    super(String.format("%s", message));
  }
}
