package uk.gov.cshr.civilservant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cshr.civilservant.validation.ValidCapacity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Set;

@NoArgsConstructor
@Data
public class AgencyTokenDTO {

    @NotEmpty(message = "token cannot be empty")
    @NotBlank(message = "token cannot be blank")
    @NotNull(message = "token cannot be null")
    private String token;

    @ValidCapacity
    private int capacity;

    @PositiveOrZero
    private int capacityUsed;

    @NotEmpty
    private Set<AgencyDomainDTO> agencyDomains;
}