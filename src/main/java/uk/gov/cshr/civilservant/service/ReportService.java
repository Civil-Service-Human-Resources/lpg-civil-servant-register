package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.dto.factory.CivilServantDtoFactory;
import uk.gov.cshr.civilservant.exception.UserNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final CivilServantRepository civilServantRepository;
    private final CivilServantDtoFactory civilServantDtoFactory;

    public ReportService(CivilServantRepository civilServantRepository, CivilServantDtoFactory civilServantDtoFactory) {
        this.civilServantRepository = civilServantRepository;
        this.civilServantDtoFactory = civilServantDtoFactory;
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantDto> getCivilServantMapByUserOrganisation(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getOrganisationalUnit().isPresent()) {
            return civilServantRepository.findAllByOrganisationalUnit(user.getOrganisationalUnit().get()).stream()
                    .collect(Collectors.toMap(civilServant -> civilServant.getIdentity().getUid(), civilServantDtoFactory::create));
        }
        return Collections.emptyMap();
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantDto> getCivilServantMapByUserProfession(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getProfession().isPresent()) {
            return civilServantRepository.findAllByProfession(user.getProfession().get()).stream()
                    .collect(Collectors.toMap(civilServant -> civilServant.getIdentity().getUid(), civilServantDtoFactory::create));
        }

        return Collections.emptyMap();
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantDto> getCivilServantMap() {
        return civilServantRepository.findAll().stream()
                .collect(Collectors.toMap(civilServant -> civilServant.getIdentity().getUid(), civilServantDtoFactory::create));
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantDto> getCivilServantMapNormalised() {
        return civilServantRepository.findAllNormalised().stream()
                .collect(Collectors.toMap(CivilServantDto::getUid, civilServantDto -> civilServantDto));
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantDto> getCivilServantMapByUserOrganisationNormalised(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getOrganisationalUnit().isPresent()) {
            return civilServantRepository.findAllByOrganisationNormalised(user.getOrganisationalUnit().get()).stream()
                    .collect(Collectors.toMap(CivilServantDto::getUid, civilServant -> civilServant));
        }
        return Collections.emptyMap();
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantDto> getCivilServantMapByUserProfessionNormalised(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getProfession().isPresent()) {
            return civilServantRepository.findAllByProfessionNormalised(user.getProfession().get()).stream()
                    .collect(Collectors.toMap(CivilServantDto::getUid, civilServant -> civilServant));
        }

        return Collections.emptyMap();
    }

}
