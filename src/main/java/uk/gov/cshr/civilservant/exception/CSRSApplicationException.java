package uk.gov.cshr.civilservant.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSRSApplicationException extends Exception {

    public CSRSApplicationException(String msg, Throwable e) {
        super(msg, e);
    }

}
