package uk.gov.cshr.civilservant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cshr.civilservant.validation.MaxCapacityAgencyToken;
import uk.gov.cshr.civilservant.validation.MinCapacityAgencyToken;

import javax.validation.constraints.*;
import java.util.Set;

@NoArgsConstructor
@Data
public class AgencyTokenDTO {

    @NotEmpty(message = "token cannot be empty")
    @NotBlank(message = "token cannot be blank")
    @NotNull(message = "token cannot be null")
    private String token;

    @MinCapacityAgencyToken
    @MaxCapacityAgencyToken
    private int capacity;

    @PositiveOrZero
    private int capacityUsed;

    @NotEmpty
    private Set<AgencyDomainDTO> agencyDomains;
}
