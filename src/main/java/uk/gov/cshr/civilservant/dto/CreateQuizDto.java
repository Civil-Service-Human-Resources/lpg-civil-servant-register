package uk.gov.cshr.civilservant.dto;

import javax.validation.constraints.NotNull;

import lombok.*;
import uk.gov.cshr.civilservant.domain.Profession;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateQuizDto {
  @NotNull Profession profession;
}
