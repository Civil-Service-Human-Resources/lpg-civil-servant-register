package uk.gov.cshr.civilservant.exception;

public class InvalidCapacityUsedException extends RuntimeException {
    public InvalidCapacityUsedException(String token) {
        super(String.format("Capacity Used cannot be less than zero for agency token with ID: %s", token));
    }
}
