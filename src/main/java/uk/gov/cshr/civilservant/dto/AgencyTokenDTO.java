package uk.gov.cshr.civilservant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.Set;

@NoArgsConstructor
@Data
public class AgencyTokenDTO {

    @NotEmpty(message = "token cannot be empty")
    @NotBlank(message = "token cannot be blank")
    @NotNull(message = "token cannot be null")
    private String token;

    @Min(value = 1, message = "capacity should be greater than 0")
    @Max(value = 1500, message = "capacity should not be greater than 1500")
    private int capacity;

    @PositiveOrZero
    private int capacityUsed;

    @NotEmpty
    private Set<AgencyDomainDTO> agencyDomains;
}
