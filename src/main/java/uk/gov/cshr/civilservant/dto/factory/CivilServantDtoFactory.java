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

    public CivilServantDto create(CivilServant civilServant) {
        CivilServantDto civilServantDto = new CivilServantDto();
        civilServantDto.setId(civilServant.getIdentity().getUid());
        civilServantDto.setName(civilServant.getFullName());

        if (civilServant.getOrganisationalUnit().isPresent()) {
            civilServantDto.setOrganisation(civilServant.getOrganisationalUnit().get().getName());
        }

        if (civilServant.getProfession().isPresent()) {
            civilServantDto.setProfession(civilServant.getProfession().get().getName());
        }

        civilServantDto.setOtherAreasOfWork(civilServant.getOtherAreasOfWork().stream()
                .map(Profession::getName)
                .collect(Collectors.toList()));

        if (civilServant.getGrade().isPresent()) {
            civilServantDto.setGrade(civilServant.getGrade().get().getName());
        }

        if(civilServant.getForceOrgReset() == null) {
            civilServantDto.setForceOrgFlag(false);
        } else {
            civilServantDto.setForceOrgFlag(civilServant.getForceOrgReset());
        }

        return civilServantDto;
    }
}
