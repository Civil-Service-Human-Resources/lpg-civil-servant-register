package uk.gov.cshr.civilservant.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cshr.civilservant.domain.CivilServant;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Builder
@EqualsAndHashCode
@Getter
public class CivilServantDto {
    private String id;
    private String uid;
    private String name;
    private String email;
    private String organisation;
    private String profession;
    private List<String> otherAreasOfWork;
    private String grade;
}
