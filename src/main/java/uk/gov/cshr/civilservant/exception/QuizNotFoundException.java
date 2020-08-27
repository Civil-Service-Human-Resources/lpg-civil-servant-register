package uk.gov.cshr.civilservant.exception;

public class QuizNotFoundException extends Exception {
  public QuizNotFoundException(String message) {
    super(String.format("%s", message));
  }
}
