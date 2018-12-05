package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.dto.ProfessionDto;
import uk.gov.cshr.civilservant.dto.factory.ProfessionDtoFactory;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;

@Service
public class ProfessionService extends SelfReferencingEntityService<Profession, ProfessionDto> {
    public ProfessionService(ProfessionRepository professionRepository, RepositoryEntityService<Profession> repositoryEntityService, ProfessionDtoFactory dtoFactory) {
        super(professionRepository, repositoryEntityService, dtoFactory);
    }
}
