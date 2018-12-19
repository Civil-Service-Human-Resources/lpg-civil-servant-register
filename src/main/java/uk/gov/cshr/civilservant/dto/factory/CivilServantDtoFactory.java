package uk.gov.cshr.civilservant.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.stream.Collectors;

@Component
public class CivilServantDtoFactory {

    private final IdentityService identityService;

    public CivilServantDtoFactory(IdentityService identityService) {
        this.identityService = identityService;
    }

    public CivilServantDto create(CivilServant civilServant) {
        CivilServantDto civilServantDto = new CivilServantDto();
        civilServantDto.setId(civilServant.getIdentity().getUid());
        civilServantDto.setEmail(identityService.getEmailAddress(civilServant));
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

        return civilServantDto;
    }
}
