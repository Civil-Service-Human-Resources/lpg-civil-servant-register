package uk.gov.cshr.civilservant.exception;

public class NoOrganisationsFoundException extends RuntimeException {
    public NoOrganisationsFoundException(String domain) {
        super(String.format("Not organisations found for domain: %s", domain));
    }
}
