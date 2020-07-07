package uk.gov.cshr.civilservant.exception;

public class QuizServiceException extends RuntimeException {
  public QuizServiceException(String message) {
    super(String.format("%s", message));
  }
}
