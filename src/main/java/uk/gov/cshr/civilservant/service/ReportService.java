package uk.gov.cshr.civilservant.service;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.exception.UserNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.IdentityRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final CivilServantRepository civilServantRepository;
    private final CivilServantResourceFactory civilServantResourceFactory;

    public ReportService(CivilServantRepository civilServantRepository, CivilServantResourceFactory civilServantResourceFactory) {
        this.civilServantRepository = civilServantRepository;
        this.civilServantResourceFactory = civilServantResourceFactory;
    }

    public List<Resource<CivilServantResource>> listCivilServantsByUserOrganisation(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return civilServantRepository.findAllByOrganisationalUnit(user.getOrganisationalUnit()).stream()
                .map(civilServantResourceFactory::create)
                .collect(Collectors.toList());
    }
}
