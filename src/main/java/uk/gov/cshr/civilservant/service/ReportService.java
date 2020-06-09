package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.dto.CivilServantReportDto;
import uk.gov.cshr.civilservant.dto.factory.CivilServantDtoFactory;
import uk.gov.cshr.civilservant.exception.UserNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalReportingPermissionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final CivilServantRepository civilServantRepository;
    private final CivilServantDtoFactory civilServantDtoFactory;
    private final OrganisationalReportingPermissionRepository organisationalReportingPermissionRepository;

    public ReportService(CivilServantRepository civilServantRepository, CivilServantDtoFactory civilServantDtoFactory,
                         OrganisationalReportingPermissionRepository organisationalReportingPermissionRepository) {
        this.civilServantRepository = civilServantRepository;
        this.civilServantDtoFactory = civilServantDtoFactory;
        this.organisationalReportingPermissionRepository = organisationalReportingPermissionRepository;
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
    public Map<String, CivilServantReportDto> getCivilServantMapNormalised() {
        return civilServantRepository.findAllNormalised().stream()
                .collect(Collectors.toMap(CivilServantReportDto::getUid, civilServantDto -> civilServantDto));
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantReportDto> getCivilServantMapNormalisedWithCodes() {
        return civilServantRepository.findAllNormalisedWithCodes().stream()
                .collect(Collectors.toMap(CivilServantReportDto::getUid, civilServantDto -> civilServantDto));
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantReportDto> getCivilServantMapByUserOrganisationNormalised(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Long> reportingOrganisationIdList = organisationalReportingPermissionRepository
                .findAllOrganisationIdByCivilServantId(user.getId());
        if(reportingOrganisationIdList != null && !reportingOrganisationIdList.isEmpty()) {
            List<CivilServantReportDto> dtos = civilServantRepository
                    .findAllByReportingOrganisationId(reportingOrganisationIdList);
            return dtos.stream()
                    .collect(Collectors
                            .toMap(CivilServantReportDto::getUid, civilServant -> civilServant));
        }
        return Collections.emptyMap();
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantReportDto> getCivilServantMapByUserProfessionNormalised(String userId) {
        CivilServant user = civilServantRepository.findByIdentity(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getProfession().isPresent()) {
            return civilServantRepository.findAllByProfessionNormalised(user.getProfession().get()).stream()
                    .collect(Collectors.toMap(CivilServantReportDto::getUid, civilServant -> civilServant));
        }

        return Collections.emptyMap();
    }

    @Transactional(readOnly = true)
    public Map<String, CivilServantReportDto> getCivilServantMapByOrganisationCodeNormalised(String organisationCode) {
        List<CivilServantReportDto> allByOrganisationCodeNormalised = civilServantRepository.findAllByOrganisationCodeNormalised(organisationCode);
        return allByOrganisationCodeNormalised.stream()
                .collect(Collectors.toMap(CivilServantReportDto::getUid, civilServant -> civilServant));
    }
}
