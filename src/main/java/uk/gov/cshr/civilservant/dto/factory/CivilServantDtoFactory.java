package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;

import java.util.stream.Collectors;

@Component
public class CivilServantDtoFactory extends DtoFactory<CivilServantDto, CivilServant>  {

    public CivilServantDtoFactory() {
    }

    /**
     * Create transform given Entity to corresponding Dto object. Note: CivilServantDto.email is always of null value
     * @param civilServant the CivilServant entity
     * @return CivilServantDto the CivilServant Dto object
     */
    public CivilServantDto create(CivilServant civilServant) {
        CivilServantDto.CivilServantDtoBuilder builder = CivilServantDto.builder();

        builder.id(civilServant.getIdentity().getUid());
        builder.name(civilServant.getFullName());

        if (civilServant.getOrganisationalUnit().isPresent()) {
            builder.organisation(civilServant.getOrganisationalUnit().get().getName());
        }

        if (civilServant.getProfession().isPresent()) {
           builder.profession(civilServant.getProfession().get().getName());
        }

        builder.otherAreasOfWork(civilServant.getOtherAreasOfWork().stream()
                .map(Profession::getName)
                .collect(Collectors.toList()));

        if (civilServant.getGrade().isPresent()) {
            builder.grade(civilServant.getGrade().get().getName());
        }

        return builder.build();
    }
}
