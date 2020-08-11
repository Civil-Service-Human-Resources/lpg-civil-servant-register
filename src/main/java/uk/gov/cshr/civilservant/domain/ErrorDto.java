package uk.gov.cshr.civilservant.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ErrorDto {
  private final Instant timestamp = Instant.now();
  private List<String> errors = new ArrayList<>();
  private int status;
  private String message;
}
