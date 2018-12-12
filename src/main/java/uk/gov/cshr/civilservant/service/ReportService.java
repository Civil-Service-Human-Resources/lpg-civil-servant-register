package uk.gov.cshr.civilservant.service;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.exception.UserNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final CivilServantRepository civilServantRepository;
    private final CivilServantResourceFactory civilServantResourceFactory;

    public ReportService(CivilServantRepository civilServantRepository, CivilServantResourceFactory civilServantResourceFactory) {
        this.civilServantRepository = civilServantRepository;
        this.civilServantResourceFactory = civilServantResourceFactory;
    }

    @Transactional(readOnly = true)
    public Map<String, Resource<CivilServantResource>> getCivilServantMapByUserOrganisation(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return civilServantRepository.findAllByOrganisationalUnit(user.getOrganisationalUnit()).stream()
                .collect(Collectors.toMap(civilServant -> civilServant.getIdentity().getUid(), civilServantResourceFactory::create));
    }

    @Transactional(readOnly = true)
    public Map<String, Resource<CivilServantResource>> getCivilServantMapByUserProfession(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return civilServantRepository.findAllByProfession(user.getProfession()).stream()
                .collect(Collectors.toMap(civilServant -> civilServant.getIdentity().getUid(), civilServantResourceFactory::create));
    }

    @Transactional(readOnly = true)
    public Map<String, Resource<CivilServantResource>> getCivilServantMap() {
        return civilServantRepository.findAll().stream()
                .collect(Collectors.toMap(civilServant -> civilServant.getIdentity().getUid(), civilServantResourceFactory::create));
    }
}
