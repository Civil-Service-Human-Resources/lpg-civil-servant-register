package uk.gov.cshr.civilservant.domain;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorDtoFactory {
  public ErrorDto create(HttpStatus httpStatus, List<String> errors) {
    sort(errors);
    ErrorDto errorDto = new ErrorDto();
    errorDto.setStatus(httpStatus.value());
    errorDto.setMessage(httpStatus.getReasonPhrase());
    errorDto.setErrors(new ArrayList<>(errors));
    return errorDto;
  }
}
