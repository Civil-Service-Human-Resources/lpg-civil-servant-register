package uk.gov.cshr.civilservant.exception;

public class QuizServiceException extends Exception {
  public QuizServiceException(String message) {
    super(String.format("%s", message));
  }
}
