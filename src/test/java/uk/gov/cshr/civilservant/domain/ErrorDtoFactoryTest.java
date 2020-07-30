package uk.gov.cshr.civilservant.domain;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpStatus;

public class ErrorDtoFactoryTest {
  private final ErrorDtoFactory factory = new ErrorDtoFactory();

  @Test
  public void shouldReturnValidationErrors() {
    String error1 = "error-1";
    String error2 = "error-2";

    ErrorDto errorDto = factory.create(HttpStatus.BAD_REQUEST, Arrays.asList(error1, error2));

    assertEquals(400, errorDto.getStatus());
    assertEquals("Bad Request", errorDto.getMessage());
    assertEquals(Arrays.asList(error1, error2), errorDto.getErrors());
  }
}
