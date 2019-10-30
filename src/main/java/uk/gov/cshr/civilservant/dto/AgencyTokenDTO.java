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

    @Size(min = 1, max = 1500, message
            = "Capacity must be between 1 and 1500")
    private int capacity;

    @PositiveOrZero
    private int capacityUsed;

    @NotEmpty
    private Set<AgencyDomainDTO> agencyDomains;
}
