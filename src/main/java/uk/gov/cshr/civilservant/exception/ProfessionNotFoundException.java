package uk.gov.cshr.civilservant.exception;

public class ProfessionNotFoundException extends RuntimeException{
    public ProfessionNotFoundException (String message) {
        super(String.format("%s", message));
    }
}
